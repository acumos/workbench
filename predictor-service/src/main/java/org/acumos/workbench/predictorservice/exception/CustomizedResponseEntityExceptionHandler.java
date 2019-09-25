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

package org.acumos.workbench.predictorservice.exception;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Predictor;
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
	 * To handle EntityNotFoundException and returns appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Predictor> 
	 * 		returns Predictor with ServiceStatus indicating error
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		Predictor predictor = getModelWithErrorStatus(ex);
		return new ResponseEntity<Predictor>(predictor, HttpStatus.NOT_FOUND);
	}
	/**
	 * To handle PredictorException and returns appropriate response to UI
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Predictor> 
	 * 		returns Predictor with ServiceStatus indicating error
	 */
	@ExceptionHandler(PredictorException.class)
	public final ResponseEntity<?> handlePredictorException(PredictorException ex, WebRequest request) {
		Predictor predictor = getModelWithErrorStatus(ex);
		return new ResponseEntity<Predictor>(predictor, HttpStatus.FORBIDDEN);
	}
	
	/**
	 * To handle InputValidationException and returns appropriate response to UI
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Predictor> 
	 * 		returns Predictor with ServiceStatus indicating error
	 */
	@ExceptionHandler(InputValidationException.class)
	public final ResponseEntity<?> handleInputValidationException(InputValidationException ex, WebRequest request) {
		Predictor predictor = getModelWithErrorStatus(ex);
		return new ResponseEntity<Predictor>(predictor, HttpStatus.NOT_ACCEPTABLE);
	}
	
	
	/**
	 * To handle CouchDBException and returns appropriate response to UI
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Predictor> 
	 * 		returns Predictor with ServiceStatus indicating error
	 */
	@ExceptionHandler(CouchDBException.class)
	public final ResponseEntity<?> handleCouchDBException(CouchDBException ex, WebRequest request) {
		Predictor predictor = getModelWithErrorStatus(ex);
		return new ResponseEntity<Predictor>(predictor, HttpStatus.NOT_ACCEPTABLE);
	}
	
	/**
	 * To handle AssociationException and returns appropriate response to UI
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Predictor> 
	 * 		returns Predictor with ServiceStatus indicating error
	 */
	@ExceptionHandler(AssociationException.class)
	public final ResponseEntity<?> handleAssociationException(AssociationException ex, WebRequest request) {
		Predictor predictor = getModelWithErrorStatus(ex);
		return new ResponseEntity<Predictor>(predictor, HttpStatus.NOT_FOUND);
	}
	
	private Predictor getModelWithErrorStatus(RuntimeException ex) {
		logger.debug("getModelWithErrorStatus() Begin");
		Predictor predictor = new Predictor();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		predictor.setServiceStatus(serviceState);
		logger.debug("getModelWithErrorStatus() End");
		return predictor;
	}
}