/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.workbench.pipelineservice.service;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.pipelineservice.exception.ArchivedException;
import org.acumos.workbench.pipelineservice.exception.NotProjectOwnerException;
import org.acumos.workbench.pipelineservice.exception.ProjectNotFoundException;
import org.acumos.workbench.pipelineservice.exception.ValueNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("PipeLineValidationServiceImpl")
public class PipeLineValidationServiceImpl implements PipeLineValidationService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationServiceImpl;
	
	@Autowired
	@Qualifier("ProjectServiceRestClientImpl")
	private ProjectServiceRestClientImpl psClient;
	

	@Override
	public void validateInputData(String authenticatedUserId, Pipeline pipeLine) {
		logger.debug("validateInputData() Begin");
		// 1. Validation service to validate the input: only ProjectId, PipeLine
		// Name, version, and description should be the input values rest should be empty.
		inputValidationServiceImpl.validatePipeLineInputJsonStructure(pipeLine);

		// 2. Check authenticatedUserId should be present
		inputValidationServiceImpl.isValueExists("Acumos User Id", authenticatedUserId);

		// 3. PipeLine name should be present
		inputValidationServiceImpl.isValueExists("PipeLine Name", pipeLine.getPipelineId().getName());

		// 4. Validate PipeLine Name & Version (for allowed special character)
		inputValidationServiceImpl.validatePipeLineName(pipeLine.getPipelineId().getName());

		if (null != pipeLine.getPipelineId().getVersionId().getLabel()) {
			inputValidationServiceImpl.validateVersion(pipeLine.getPipelineId().getVersionId().getLabel());
		}
		logger.debug("validateInputData() End");

	}
	
	// TODO : Move method to workbench-common library module.
	public void validateProject(String authenticatedUserId, String projectId)
			throws ValueNotFoundException, ProjectNotFoundException, NotProjectOwnerException, ArchivedException {
		logger.debug("validateProject() Begin");
		ResponseEntity<Project> response = psClient.getProject(authenticatedUserId, projectId);
		if (null != response) {
			Project project = response.getBody();
			if (null != project) {
				if (!ServiceStatus.ERROR.equals(project.getServiceStatus().getStatus())) {
					if (ArtifactStatus.ARCHIVED.equals(project.getArtifactStatus().getStatus())) {
						logger.error("Specified Project : " + projectId + " is archived");
						throw new ArchivedException("Specified Project : " + projectId + " is archived");
					}
				} else {
					if (HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) { // Bad request
						logger.warn(project.getServiceStatus().getStatusMessage() + " Value Not Found");
						throw new ValueNotFoundException(project.getServiceStatus().getStatusMessage());
					} else if (HttpStatus.FORBIDDEN.equals(response.getStatusCode())) { // Not owner
						logger.error("User is not owner of the Project or has permission to access Project");
						throw new NotProjectOwnerException();
					}
				}
			} else {
				logger.error("Requested Project Not found");
				throw new ProjectNotFoundException();
			}
		} else {
			logger.error("Requested Project Not found");
			throw new ProjectNotFoundException();
		}
		logger.debug("validateProject() End");
	}
	

}
