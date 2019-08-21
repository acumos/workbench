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

package org.acumos.workbench.modelservice.exceptionhandling;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.BadRequestException;
import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.exception.ForbiddenException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/**
	 * The method to handle BadRequestException and return the appropriate response to UI. 
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Model> 
	 * 		returns Model with ServiceStatus indicating error 
	 */
	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
		Model model = getModelWithErrorStatus(ex);
		return new ResponseEntity<Model>(model, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The method to handle ForbiddenException and return the appropriate response to UI.
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Model> 
	 * 		returns Model with ServiceStatus indicating error 
	 */
	@ExceptionHandler(ForbiddenException.class)
	public final ResponseEntity<?> handleForbiddenException(ForbiddenException ex, WebRequest request) {
		Model model = getModelWithErrorStatus(ex);
		return new ResponseEntity<Model>(model, HttpStatus.FORBIDDEN);
	}
	
	/**
	 * The method to handle ArchivedException and return the appropriate response to UI.
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Model> 
	 * 		returns Model with ServiceStatus indicating error 
	 */
	@ExceptionHandler(ArchivedException.class)
	public final ResponseEntity<?> handleArchivedException(ArchivedException ex, WebRequest request) {
		Model model = getModelWithErrorStatus(ex);
		return new ResponseEntity<Model>(model, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * To handle EntityNotFoundException and returns appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Model> 
	 * 		returns Model with ServiceStatus indicating error
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<?> handleArtifactNotFoundException(EntityNotFoundException ex, WebRequest request) {
		Model model = getModelWithErrorStatus(ex);
		return new ResponseEntity<Model>(model, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * To handle AssociationExistsException and returns appropriate response to UI. 
	 * @param ex
	 * 			the exception thrown by the service method
	 * @param request
	 * 			the WebRequest
	 * @return ResponseEntitiy<Model> 
	 * 			returns Model with ServiceStatus indicating error
	 */
	@ExceptionHandler(AssociationExistsException.class)
	public final ResponseEntity<?> handleAssociationExistsException(AssociationExistsException ex, WebRequest request) {
		Model model = getModelWithErrorStatus(ex);
		return new ResponseEntity<Model>(model, HttpStatus.NOT_FOUND);
	}
	
	
	/**
	 * To handle TargetServiceInvocationException from CDS
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Model> 
	 * 		returns Model with ServiceStatus indicating error
	 */
	@ExceptionHandler(TargetServiceInvocationException.class)
	public final ResponseEntity<?> handleRestClientException(TargetServiceInvocationException ex, WebRequest request) { 
		logger.debug("handleRestClientException() Begin");
		Model model = new Model();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		model.setServiceStatus(serviceState);
		logger.debug("handleRestClientException() End");
		return new ResponseEntity<Model>(model, HttpStatus.SERVICE_UNAVAILABLE);
	}

	private Model getModelWithErrorStatus(RuntimeException ex) {
		logger.debug("getModelWithErrorStatus() Begin");
		Model model = new Model();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		model.setServiceStatus(serviceState);
		logger.debug("getModelWithErrorStatus() End");
		return model;
	}
}