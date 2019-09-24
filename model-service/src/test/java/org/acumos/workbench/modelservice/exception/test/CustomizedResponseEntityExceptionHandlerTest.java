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

package org.acumos.workbench.modelservice.exception.test;

import static org.junit.Assert.assertNotNull;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.BadRequestException;
import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.exception.ForbiddenException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.modelservice.exceptionhandling.AssociationException;
import org.acumos.workbench.modelservice.exceptionhandling.CouchDBException;
import org.acumos.workbench.modelservice.exceptionhandling.CustomizedResponseEntityExceptionHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RunWith(SpringRunner.class)
public class CustomizedResponseEntityExceptionHandlerTest {
	
	@InjectMocks
	private CustomizedResponseEntityExceptionHandler customizedResponseEntityExceptionHandler;
	
	private MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/");
	
	private MockHttpServletResponse servletResponse = new MockHttpServletResponse();
	
	private WebRequest request = new ServletWebRequest(this.servletRequest, this.servletResponse);
	
	@Test
	public void handleBadRequestExceptionTest(){
		BadRequestException ex = new BadRequestException("BadRequest");
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleBadRequestException(ex, request);
		assertNotNull(response);
	}
	
	@Test
	public void handleForbiddenExceptionTest(){
		ForbiddenException ex = new ForbiddenException("Forbidden Request");
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleForbiddenException(ex, request);
		assertNotNull(response);
	}
	
	@Test
	public void handleArchivedExceptionTest(){
		ArchivedException ex = new ArchivedException("Archived Exception");
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleArchivedException(ex, request);
		assertNotNull(response);
	}
	
	@Test
	public void handleArtifactNotFoundExceptionTest(){
		EntityNotFoundException ex = new EntityNotFoundException("EntityNotFoundException");
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleArtifactNotFoundException(ex, request);
		assertNotNull(response);
	}
	
	@Test
	public void handleAssociationExistsExceptionTest(){
		AssociationException ex = new AssociationException("AssociationException");
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleAssociationExistsException(ex, request);
		assertNotNull(response);
	}
	
	@Test
	public void handleCouchDBExceptionTest(){
		CouchDBException ex = new CouchDBException();
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleCouchDBException(ex, request);
		assertNotNull(response);
		
	}
	
	@Test
	public void handleCouchDBExceptionTest1(){
		CouchDBException ex = new CouchDBException("CouchDbException");
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleCouchDBException(ex, request);
		assertNotNull(response);
		
	}
	
	@Test
	public void handleRestClientExceptionTest(){
		TargetServiceInvocationException ex = new TargetServiceInvocationException();
		ResponseEntity<?> response = customizedResponseEntityExceptionHandler.handleRestClientException(ex, request);
	}

}
