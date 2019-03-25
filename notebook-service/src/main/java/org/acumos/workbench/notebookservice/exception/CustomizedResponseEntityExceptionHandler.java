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

package org.acumos.workbench.notebookservice.exception;

import org.acumos.workbench.notebookservice.util.ServiceStatus;
import org.acumos.workbench.notebookservice.vo.Notebook;
import org.acumos.workbench.notebookservice.vo.Project;
import org.acumos.workbench.notebookservice.vo.ServiceState;
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
	 * The method to handle BadRequestException and return the appropriate response to UI. 
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Notebook> 
	 * 		returns Notebook with ServiceStatus indicating error 
	 */
	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
		Notebook notebook = getNotebok(ex);
		return new ResponseEntity<Notebook>(notebook, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The method to handle ForbiddenException and return the appropriate response to UI.
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Notebook> 
	 * 		returns Notebook with ServiceStatus indicating error 
	 */
	@ExceptionHandler(ForbiddenException.class)
	public final ResponseEntity<?> handleBadRequestException(ForbiddenException ex, WebRequest request) {
		Notebook notebook = getNotebok(ex);
		return new ResponseEntity<Notebook>(notebook, HttpStatus.FORBIDDEN);
	}
	
	/**
	 * The method to handle ArchivedException and return the appropriate response to UI.
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Notebook> 
	 * 		returns Notebook with ServiceStatus indicating error 
	 */
	@ExceptionHandler(ArchivedException.class)
	public final ResponseEntity<?> handleArchivedException(ForbiddenException ex, WebRequest request) {
		Notebook notebook = getNotebok(ex);
		return new ResponseEntity<Notebook>(notebook, HttpStatus.LOCKED);
	}

	/**
	 * To handle EntityNotFoundException and returns appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Notebook> 
	 * 		returns Notebook with ServiceStatus indicating error
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<?> handleArtifactNotFoundException(EntityNotFoundException ex, WebRequest request) {
		Notebook notebook = getNotebok(ex);
		return new ResponseEntity<Notebook>(notebook, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * To handle RestClientResponseException from CDS
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Notebook> 
	 * 		returns Notebook with ServiceStatus indicating error
	 */
	@ExceptionHandler(RestClientResponseException.class)
	public final ResponseEntity<?> handleRestClientResponseException(RestClientResponseException ex, WebRequest request) { 
		//TODO : Include logger to log the CDS error details.
		Notebook notebook = new Notebook();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage("Error invoking CDS Micro Service");
		notebook.setServiceStatus(serviceState);
		return new ResponseEntity<Notebook>(notebook, HttpStatus.METHOD_FAILURE);
	}
	
	private Notebook getNotebok(RuntimeException ex) {
		Notebook notebook = new Notebook();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		notebook.setServiceStatus(serviceState);
		return notebook;
	}
	 
}
