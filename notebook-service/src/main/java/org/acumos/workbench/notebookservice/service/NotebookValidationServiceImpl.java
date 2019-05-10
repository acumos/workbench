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

package org.acumos.workbench.notebookservice.service;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("NotebookValidationServiceImpl")
public class NotebookValidationServiceImpl implements NotebookValidationService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationServiceImpl;
	
	@Autowired
	private ProjectServiceRestClientImpl psClient;
	
	public void validateNotebook(String authenticatedUserId, Notebook notebook ) { 
		logger.debug("validateNotebook() Begin");
		
		//1. Validation service to validate the input
		inputValidationServiceImpl.validateNotebookInput(notebook);
		
		//2. Check authenticatedUserId should be present
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		
		//3. Notebook name should be present
		inputValidationServiceImpl.isValuePresent("Notebook Name", notebook.getNoteBookId().getName());
		
		//4. Notebook Type should be present
		inputValidationServiceImpl.isValuePresent("Notebook Type ", notebook.getNotebookType());
		
		//5. Validate Notebook Name & Version (for allowed special character)
		inputValidationServiceImpl.validateNotebookName(notebook.getNoteBookId().getName());
		if(null != notebook.getNoteBookId().getVersionId().getLabel()) {
			inputValidationServiceImpl.validateVersion(notebook.getNoteBookId().getVersionId().getLabel());
		}
		
		//6. Validate Notebook Type 
		inputValidationServiceImpl.validateNotebookType(notebook.getNotebookType());
		logger.debug("validateNotebook() End");
		
	}
	
	//TODO : Move method to workbench-common library module.
	public void validateProject(String authenticatedUserId, String projectId, String authToken)
			throws ValueNotFoundException, ProjectNotFoundException,
			NotProjectOwnerException, ArchivedException { 
		logger.debug("validateProject() Begin");
		ResponseEntity<Project> response = psClient.getProject(authenticatedUserId, projectId, authToken);
		if(null != response) { 
			Project project = response.getBody();
			if(null != project) { 
				if(!ServiceStatus.ERROR.equals(project.getServiceStatus().getStatus())) {
					if(ArtifactStatus.ARCHIVED.equals(project.getArtifactStatus().getStatus())){
						logger.error("Specified Project : " + projectId + " is archived");
						throw new ArchivedException("Specified Project : " + projectId + " is archived");
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
				logger.error("Project Specified Not found");
				throw new ProjectNotFoundException();
			}
		} else { 
			logger.error("Project Specified Not found");
			throw new ProjectNotFoundException();
		}
		logger.debug("validateProject() End");
	}
	
}
