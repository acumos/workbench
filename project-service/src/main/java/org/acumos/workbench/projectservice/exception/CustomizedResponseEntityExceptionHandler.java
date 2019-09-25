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

package org.acumos.workbench.projectservice.exception;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.BadRequestException;
import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.exception.ForbiddenException;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * The method to handle BadRequestException and return the appropriate
	 * response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service methods.
	 * @param request
	 *            the Web request.
	 * @return ResponseEntitiy<Project> returns Project with ServiceStatus indicating error
	 */

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The method to handle ForbiddenException and return the appropriate
	 * response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service methods.
	 * @param request
	 *            the Web request.
	 * @return ResponseEntitiy<Project> returns Project with ServiceStatus indicating error
	 */

	@ExceptionHandler(ForbiddenException.class)
	public final ResponseEntity<?> handleForbiddenException(ForbiddenException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.FORBIDDEN);
	}
	
	/**
	 * The method to handle ArchivedException and return the appropriate
	 * response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service methods.
	 * @param request
	 *            the Web request.
	 * @return ResponseEntitiy<Project> returns Project with ServiceStatus indicating error
	 */

	@ExceptionHandler(ArchivedException.class)
	public final ResponseEntity<?> handleArchivedException(ArchivedException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.LOCKED);
	}

	/**
	 * To handle EntityNotFoundException and resturn appropriate response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service method
	 * @param request
	 *            the WebRequest
	 * @return ResponseEntitiy<Project> returns Project with ServiceStatus indicating error
	 */

	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<?> handleArtifactNotFoundException(EntityNotFoundException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * To handle DuplicateCollaboratorException and return appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Project> 
	 * 		returns Project with ServiceStatus indicating error
	 */
	@ExceptionHandler(DuplicateCollaboratorException.class)
	public final ResponseEntity<?> handleDuplicateCollaboratorException(DuplicateCollaboratorException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.NOT_FOUND);
	}
	/**
	 * To handle ProjectNotActiveException and return appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Project> 
	 * 		returns Project with ServiceStatus indicating error
	 */
	@ExceptionHandler(ProjectNotActiveException.class)
	public final ResponseEntity<?> handleProjectNotActiveException(ProjectNotActiveException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.NOT_FOUND);
	}
	/**
	 * To handle CouchDBException and return appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Project> 
	 * 		returns Project with ServiceStatus indicating error
	 */
	@ExceptionHandler(CouchDBException.class)
	public final ResponseEntity<?> handleCouchDBException(CouchDBException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * To handle CollaboratorNotExistsException and return appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Project> 
	 * 		returns Project with ServiceStatus indicating error
	 */
	@ExceptionHandler(CollaboratorNotExistsException.class)
	public final ResponseEntity<?> handleCollaboratorNotExistsException(CollaboratorNotExistsException ex, WebRequest request) {
		Project project = getProject(ex);
		return new ResponseEntity<Project>(project, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RestClientResponseException.class)
	public final ResponseEntity<?> handleRestClientResponseException(RestClientResponseException ex, WebRequest request) { 
		//TODO : Include logger to log the CDS error details.
		System.out.println(ex.getResponseBodyAsString());
		Project project = new Project();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage("Error invoking CDS Micro Service");
		project.setServiceStatus(serviceState);
		return new ResponseEntity<Project>(project, HttpStatus.METHOD_FAILURE);
	}
	
	private Project getProject(RuntimeException ex) {
		Project project = new Project();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		project.setServiceStatus(serviceState);
		return project;
	}
	 
}
