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

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.notebookservice.exception.ArchivedException;
import org.acumos.workbench.notebookservice.exception.NotProjectOwnerException;
import org.acumos.workbench.notebookservice.exception.ProjectNotFoundException;
import org.acumos.workbench.notebookservice.exception.ValueNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("NotebookValidationServiceImpl")
public class NotebookValidationServiceImpl implements NotebookValidationService {
	
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationServiceImpl;
	
	@Autowired
	@Qualifier("ProjectServiceRestClientImpl")
	private ProjectServiceRestClientImpl psClient;
	
	public void validateNotebook(String authenticatedUserId, Notebook notebook ) { 
		
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
		
	}
	
	//TODO : Move method to workbench-common library module.
	public void validateProject(String authenticatedUserId, String projectId)
			throws ValueNotFoundException, ProjectNotFoundException,
			NotProjectOwnerException, ArchivedException { 
		ResponseEntity<Project> response = psClient.getProject(authenticatedUserId, projectId);
		if(null != response) { 
			Project project = response.getBody();
			if(null != project) { 
				if(!ServiceStatus.ERROR.equals(project.getServiceStatus().getStatus())) {
					if(ArtifactStatus.ARCHIVED.equals(project.getArtifactStatus().getStatus())){
						throw new ArchivedException("Specified Project : " + projectId + " is archived");
					}
				} else {
					if(HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) { //Bad request 
						throw new ValueNotFoundException(project.getServiceStatus().getStatusMessage());
					} else if (HttpStatus.FORBIDDEN.equals(response.getStatusCode())) { //Not owner
						throw new NotProjectOwnerException();
					}
				}
			} else {
				throw new ProjectNotFoundException();
			}
		} else { 
			throw new ProjectNotFoundException();
		}
	}
	
}
