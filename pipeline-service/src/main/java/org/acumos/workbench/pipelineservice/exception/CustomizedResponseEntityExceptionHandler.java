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

package org.acumos.workbench.pipelineservice.exception;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.BadRequestException;
import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.exception.ForbiddenException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.pipelineservice.service.PipeLineCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private PipeLineCacheService pipelineCacheService;
	
	/**
	 * The method to handle BadRequestException and return the appropriate response to UI. 
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Pipeline> 
	 * 		returns Pipeline with ServiceStatus indicating error 
	 */
	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
		removeRequestFromCache(request);
		Pipeline pipeline = getPipeline(ex);
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.BAD_REQUEST);
	}

	
	/**
	 * The method to handle ForbiddenException and return the appropriate response to UI.
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Pipeline> 
	 * 		returns Pipeline with ServiceStatus indicating error 
	 */
	@ExceptionHandler(ForbiddenException.class)
	public final ResponseEntity<?> handleBadRequestException(ForbiddenException ex, WebRequest request) {
		removeRequestFromCache(request);
		Pipeline pipeline = getPipeline(ex);
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.FORBIDDEN);
	}
	
	/**
	 * The method to handle ArchivedException and return the appropriate response to UI.
	 * 
	 * @param ex
	 * 		the exception thrown by the service methods. 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Pipeline> 
	 * 		returns Pipeline with ServiceStatus indicating error 
	 */
	@ExceptionHandler(ArchivedException.class)
	public final ResponseEntity<?> handleArchivedException(ForbiddenException ex, WebRequest request) {
		removeRequestFromCache(request);
		Pipeline pipeline = getPipeline(ex);
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.LOCKED);
	}

	/**
	 * To handle EntityNotFoundException and return appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Pipeline> 
	 * 		returns Pipeline with ServiceStatus indicating error
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<?> handleArtifactNotFoundException(EntityNotFoundException ex, WebRequest request) {
		removeRequestFromCache(request);
		Pipeline pipeline = getPipeline(ex);
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * To handle RestClientResponseException and return appropriate response to UI. 
	 * @param ex
	 * 		the exception thrown by the service method
	 * @param request
	 * 		the WebRequest
	 * @return ResponseEntitiy<Pipeline>
	 * 		returns Pipeline with ServiceStatus indicating error
	 */
	@ExceptionHandler(RestClientResponseException.class)
	public final ResponseEntity<?> handleRestClientResponseException(RestClientResponseException ex, WebRequest request) { 
		logger.debug("handleRestClientResponseException() Begin",ex.getResponseBodyAsString());
		removeRequestFromCache(request);
		Pipeline pipeline = new Pipeline();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage("Error invoking CDS Micro Service");
		pipeline.setServiceStatus(serviceState);
		logger.debug("handleRestClientResponseException() End");
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.METHOD_FAILURE);
	}
	
	/**
	 * The method to handle DuplicateRequestException and return the appropriate response to UI. 
	 * 
	 * @param ex
	 * 		the exception thrown 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Pipeline> 
	 * 		returns Pipeline with ServiceStatus indicating error 
	 */
	@ExceptionHandler(DuplicateRequestException.class)
	public final ResponseEntity<?> handleDuplicateRequestException(DuplicateRequestException ex, WebRequest request) {
		removeRequestFromCache(request);
		Pipeline pipeline = getPipeline(ex);
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * The method to handle NiFiInstanceCreationException and return the appropriate response to UI. 
	 * 
	 * @param ex
	 * 		the exception thrown 
	 * @param request
	 * 		the Web request. 
	 * @return ResponseEntitiy<Pipeline> 
	 * 		returns Pipeline with ServiceStatus indicating error 
	 */
	@ExceptionHandler(NiFiInstanceCreationException.class)
	public final ResponseEntity<?> handleNiFiInstanceCreationException(NiFiInstanceCreationException ex, WebRequest request) {
		removeRequestFromCache(request);
		Pipeline pipeline = getPipeline(ex);
		return new ResponseEntity<Pipeline>(pipeline, HttpStatus.METHOD_FAILURE);
	}
	
	
	private Pipeline getPipeline(RuntimeException ex) {
		logger.debug("getPipeline() Begin");
		Pipeline pipeline = new Pipeline();
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ERROR);
		serviceState.setStatusMessage(ex.getMessage());
		pipeline.setServiceStatus(serviceState);
		logger.debug("getPipeline() End");
		return pipeline;
	}

	private void removeRequestFromCache(WebRequest request) {
		String requestId = request.getHeader(LoggingConstants.Headers.REQUEST_ID);
		if(null == requestId ) {
			requestId = MDC.get(LoggingConstants.MDCs.REQUEST_ID);
		}
		pipelineCacheService.removeRequest(requestId);
	}
	
}
