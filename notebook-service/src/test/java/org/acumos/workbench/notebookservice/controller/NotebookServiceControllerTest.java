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

package org.acumos.workbench.notebookservice.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.notebookservice.service.InputValidationService;
import org.acumos.workbench.notebookservice.service.NotebookService;
import org.acumos.workbench.notebookservice.service.NotebookValidationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.ResponseEntity;

public class NotebookServiceControllerTest extends NotebookCommons {
	
	private static final String authenticatedUserId = "123"; 
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private NotebookServiceController notebookServiceController;
	
	@Mock
	private InputValidationService inputValidationService;
	
	@Mock
	private NotebookValidationService notebookValidationService;
	
	@Mock
	private NotebookService notebookService;
	
	@Mock
    HttpServletRequest request;
	
	private NotebookValidationService notebookValidationServiceImpl;
	
	private NotebookService notebookServiceImpl;
	
	private InputValidationService inputValidationServiceImpl;
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
		 notebookValidationServiceImpl = mock(NotebookValidationService.class);
		 notebookServiceImpl = mock(NotebookService.class);
		 inputValidationServiceImpl = mock(InputValidationService.class);
	}
	
	@Test
	public void createNotebookUnderProjectTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(notebookValidationServiceImpl).validateNotebook(authenticatedUserId, notebook);
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, "123", "123");
		doNothing().when(notebookServiceImpl).notebookExists(authenticatedUserId, "123", notebook);
		when(notebookService.createNotebook(authenticatedUserId, "123", notebook)).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.createNotebookUnderProject(request, authenticatedUserId, "123", notebook);
		assertNotNull(notebookResult);
	}
	
	@Test
	public void createIndependentNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(notebookValidationServiceImpl).validateNotebook(authenticatedUserId, notebook);
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, null, null);
		doNothing().when(notebookServiceImpl).notebookExists(authenticatedUserId, null, notebook);
		when(notebookService.createNotebook(authenticatedUserId, null, notebook)).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.createNotebookUnderProject(request, authenticatedUserId, null, notebook);
		assertNotNull(notebookResult);
	}
	
	@Test
	public void getNotebooksTest(){
		Notebook notebook = buildNotebook();
		List<Notebook> notebookList = new ArrayList<Notebook>();
		notebookList.add(notebook);
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Id", "123");
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, "123",null);
		when(notebookService.getNotebooks(authenticatedUserId, "123")).thenReturn(notebookList);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.getNotebooks(request, authenticatedUserId, "123");
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void getAllNotebooksTest(){
		Notebook notebook = buildNotebook();
		List<Notebook> notebookList = new ArrayList<Notebook>();
		notebookList.add(notebook);
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		when(notebookService.getNotebooks(authenticatedUserId, null)).thenReturn(notebookList);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.getNotebooks(request, authenticatedUserId, null);
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void updateProjectNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Id", "123");
		doNothing().when(notebookValidationServiceImpl).validateNotebook(authenticatedUserId, notebook);
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, "123","123");
		when(notebookService.updateNotebook(authenticatedUserId, "123", "123", notebook)).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.updateProjectNotebook(request, authenticatedUserId, "123", "123", notebook);
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void updateNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(notebookValidationServiceImpl).validateNotebook(authenticatedUserId, notebook);
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		when(notebookService.updateNotebook(authenticatedUserId, null, "123", notebook)).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.updateNotebook(request, authenticatedUserId, "123", notebook);
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void getNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Id", "123");
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		when(notebookService.getNotebook(authenticatedUserId, "123")).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.getNotebook(request, authenticatedUserId, "123");
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void deleteNotebookTest(){
		ServiceState state = new ServiceState();
		state.setStatus(ServiceStatus.COMPLETED);
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		when(notebookService.deleteNotebook(authenticatedUserId,"123")).thenReturn(state);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.deleteNotebook(request, authenticatedUserId, "123");
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void archiveProjectNotebookTest(){
		Notebook notebook = buildNotebook();
		ArtifactState state = new ArtifactState();
		state.setStatus(ArtifactStatus.ARCHIVED);
		notebook.setArtifactStatus(state);
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Id", "123");
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, "123","123");
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Id", "123");
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		when(notebookService.archiveNotebook(authenticatedUserId, "123", "123", "A")).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.archiveProjectNotebook(request, authenticatedUserId,"123", "123", "A");
		assertNotNull(notebookResult);
		
	}
	
	@Test
	public void archiveNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Id", "123");
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, "123", null);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Id", "123");
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		when(notebookService.archiveNotebook(authenticatedUserId, null, "123", "UA")).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.archiveNotebook(request, authenticatedUserId,"123", "UA");
		assertNotNull(notebookResult);
	}
	
	@Test
	public void launchProjectNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Id", "123");
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Id", "123");
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isNotebookArchived("123")).thenReturn(false);
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		doNothing().when(notebookServiceImpl).isNotebookProjectAssociated("123", "123");
		when(notebookService.launchNotebook(authenticatedUserId, "123", "123")).thenReturn(notebook);
		doNothing().when(notebookValidationServiceImpl).validateProject(authenticatedUserId, "123", "123");
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.launchProjectNotebook(request, authenticatedUserId, "123", "123");
		assertNotNull(notebookResult);
	}
	
	@Test
	public void launchIndependentNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Id", "123");
		doNothing().when(notebookServiceImpl).notebookExists("123");
		when(notebookServiceImpl.isNotebookArchived("123")).thenReturn(false);
		when(notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, "123")).thenReturn(true);
		when(notebookService.launchNotebook(authenticatedUserId, null, "123")).thenReturn(notebook);
		when(request.getHeader("Authorization")).thenReturn("123");
		ResponseEntity<?> notebookResult = notebookServiceController.launchIndependentNotebook(request, authenticatedUserId, "123");
		assertNotNull(notebookResult);
		
	}
	
}
