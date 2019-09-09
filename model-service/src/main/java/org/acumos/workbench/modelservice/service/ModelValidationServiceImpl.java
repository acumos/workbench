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

package org.acumos.workbench.modelservice.service;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("ModelValidationServiceImpl")
public class ModelValidationServiceImpl implements ModelValidationService{
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private InputValidationService inputValidationService;
	
	@Autowired
	private ProjectServiceRestClientImpl psClient;

	@Override
	public void validateInputData(Model model) {
		logger.debug("validateInputData() begins");
		// Check all the mandatory fields exists or not in Json structure
		inputValidationService.validateModelInputJson(model);
		logger.debug("validateInputData() begins");
		
	}

	@Override
	public void validateProject(String authenticatedUserId, String projectId, String authToken) {
		logger.debug("checkProjectDetails() Begin");
		ResponseEntity<Project> response = psClient.getProject(authenticatedUserId, projectId, authToken);
		if(null != response) {
			Project project = response.getBody();
			if(null != project) { 
				if(!ServiceStatus.ERROR.equals(project.getServiceStatus().getStatus())) {
					if(ArtifactStatus.ARCHIVED.equals(project.getArtifactStatus().getStatus())){
						logger.error("Specified Project : " + projectId + " is archived");
						throw new ArchivedException("Update Not Allowed, Specified Project : " + projectId + " is archived");
					}
				} else {
					if(HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) { //Bad request 
						logger.error("Error Message : " + project.getServiceStatus().getStatusMessage());
						throw new ValueNotFoundException(project.getServiceStatus().getStatusMessage());
					} else if (HttpStatus.FORBIDDEN.equals(response.getStatusCode())) { //Not owner
						logger.error("User is not owner of the Project or authorized to access Project");
						throw new NotProjectOwnerException();
					}
				}
			} else {
				logger.error("Project doesn't exists");
				throw new ProjectNotFoundException();
			}
		} else { 
			logger.error("Project doesn't exists");
			throw new ProjectNotFoundException();
		}
		logger.debug("checkProjectDetails() End");
	}

	@Override
	public void checkMandatoryFields(Model model) {
		logger.debug("checkMandatoryFields() Start");
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;

		String version = model.getModelId().getVersionId().getLabel();

		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				associationId = kv.getValue();
				if ((null == associationId) || (null == version)) {
					logger.error("AssociationId or Version is not valid or not given by the user");
					throw new InvalidInputJSONException();
				}
			}

		}
		logger.debug("checkMandatoryFields() End");
	}

}
