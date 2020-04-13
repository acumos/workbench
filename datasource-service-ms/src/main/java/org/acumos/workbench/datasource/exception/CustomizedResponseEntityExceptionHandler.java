/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.datasource.exception;

import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ServiceState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{
	
	/**
	 * The method to handle ValueNotFoundException and return the appropriate
	 * response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service methods.
	 * @param request
	 *            the Web request.
	 * @return ResponseEntitiy<ServiceState> returns ServiceStatus indicating error
	 */

	@ExceptionHandler(ValueNotFoundException.class)
	public final ResponseEntity<?> handleValueNotFoundException(ValueNotFoundException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The method to handle DataSourceException and return the appropriate
	 * response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service methods.
	 * @param request
	 *            the Web request.
	 * @return ResponseEntitiy<ServiceState> returns ServiceStatus indicating error
	 */
	@ExceptionHandler(DataSourceException.class)
	public final ResponseEntity<?> handleDataSourceException(DataSourceException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.BAD_REQUEST);
	}
	
	
	/**
	 * The method to handle DataSourceNotFoundException and return the appropriate
	 * response to UI.
	 * 
	 * @param ex
	 *            the exception thrown by the service methods.
	 * @param request
	 *            the Web request.
	 * @return ResponseEntitiy<ServiceState> returns ServiceStatus indicating error
	 */

	@ExceptionHandler(DataSourceNotFoundException.class)
	public final ResponseEntity<?> handleDataSourceNotFoundException(DataSourceNotFoundException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * To handle CouchDBException and returns appropriate response to UI
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<ServiceState> 
	 * 		returns ResponseEntity<ServiceState> indicating error
	 */
	@ExceptionHandler(CouchDBException.class)
	public final ResponseEntity<?> handleCouchDBException(CouchDBException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * To handle the Association Exception
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return
	 * 		returns ResponseEntity<ServiceState> indicating error
	 */
	@ExceptionHandler(AssociationException.class)
	public final ResponseEntity<?> handleAssociationException(AssociationException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * To handle the ServiceConnectivityException
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return
	 * 		returns ResponseEntity<ServiceState> indicating error
	 */
	@ExceptionHandler(ServiceConnectivityException.class)
	public final ResponseEntity<?> handleServiceConnectivityException(ServiceConnectivityException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(CollaboratorExistsException.class)
	public final ResponseEntity<?> handleCollaboratorExistsException(CollaboratorExistsException ex, WebRequest request) {
		ServiceState serviceState = getDataSetErrorDetails(ex);
		return new ResponseEntity<ServiceState>(serviceState, HttpStatus.BAD_REQUEST);
	}
	
	private ServiceState getDataSetErrorDetails(RuntimeException ex) {
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		return serviceState;
	}

}
