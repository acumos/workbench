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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.notebookservice.config.HandlerInterceptorConfiguration;
import org.acumos.workbench.notebookservice.controller.NotebookCommons;
import org.acumos.workbench.notebookservice.util.ConfigurationProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

//@RunWith(SpringRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(NotebookServiceUtil.class)
public class NotebookServiceImplTest { // extends NotebookCommons {

	private static final String authenticatedUserId = "19a554b1-4b00-4135-a122-2b6061480185";

	/* 
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private NotebookServiceImpl notebookServiceImpl;

	@Mock
	private CommonDataServiceRestClientImpl cmnDataService;

	@Mock
	private NotebookServiceUtil notebookServiceUtil;
	
	@Mock
	private ConfigurationProperties confProps;

	@Mock
	private HandlerInterceptorConfiguration handlerInterceptorConfiguration;

	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	//It works for TestNG
	//@Test(expectedExceptions = {NotebookNotFoundException.class,TargetServiceInvocationException.class}) 
	@Test(expected = Exception.class)
	public void notebookExistsTest() {
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		MLPNotebook mlpNotebook = buildMLPNotebook();
		when(cmnDataService.getNotebook(mlpNotebook.getNotebookId())).thenReturn(mlpNotebook);
		notebookServiceImpl.notebookExists(mlpNotebook.getNotebookId());
		notebookServiceImpl.notebookExists(null);
		
	}
	
	// Need to Mock the RestPageResponse<MLPUser> then result will come
	//@Test
	public void notebookExistsForProjectTest(){
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		MLPUser mlpUser = getUserDetails();
		Notebook notebook = buildNotebook();
		List<MLPNotebook> mlpNotebookList = new ArrayList<MLPNotebook>();
		MLPNotebook mlpNotebook = buildMLPNotebook();
		mlpNotebookList.add(mlpNotebook);
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("name",notebook.getNoteBookId().getName());
		queryParams.put("version",notebook.getNoteBookId().getVersionId().getLabel());
		queryParams.put("userId",mlpUser.getUserId());
		queryParams.put("notebookTypeCode", notebook.getNotebookType());
		PageRequest pageReq = new PageRequest(0, 10);
		RestPageRequest restPageReq = new RestPageRequest(0,10);
		RestPageResponse<MLPNotebook> pageResp = new RestPageResponse<>(mlpNotebookList, pageReq, 1);
		when(cmnDataService.searchNotebooks(queryParams, false, restPageReq)).thenReturn(pageResp);
		notebookServiceImpl.notebookExists(authenticatedUserId, "123", notebook);
		
	}
	
	//@Test(expected = Exception.class)
	public void createNotebookTest(){
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		// Need to Mock the RestPageResponse<MLPUser> in getUserDetails() 
		MLPUser mlpUser = getUserDetails();
		Notebook notebook = buildNotebook();
		MLPNotebook mlpNotebook = buildMLPNotebook();
		// Static method Mocking
		PowerMockito.mockStatic(NotebookServiceUtil.class);
		PowerMockito.when(NotebookServiceUtil.getMLPNotebook(authenticatedUserId, notebook)).thenReturn(mlpNotebook);
		
		when(cmnDataService.createNotebook(mlpNotebook)).thenReturn(mlpNotebook);
		when(ServiceStatus.COMPLETED.getServiceStatusCode()).thenReturn("COMPLETED");
		PowerMockito.when(NotebookServiceUtil.getNotebookVO(mlpNotebook, mlpUser)).thenReturn(notebook);
		Notebook notebookResult = notebookServiceImpl.createNotebook(authenticatedUserId, null, notebook);
		assertNotNull(notebookResult);
		
		
		doNothing().when(cmnDataService).addProjectNotebook("123", mlpNotebook.getNotebookId());
		Notebook notebookResult1 = notebookServiceImpl.createNotebook(authenticatedUserId, "123", notebook);
		assertNotNull(notebookResult1);
		
		when(cmnDataService.createNotebook(null)).thenReturn(mlpNotebook);
		Notebook notebookResult2 = notebookServiceImpl.createNotebook(authenticatedUserId, null, notebook);
		assertNotNull(notebookResult2);
	}
	
	//@Test(expected = Exception.class)
	public void isOwnerOfNotebookTest(){
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		Notebook notebook = buildNotebook();
		// Need to Mock the RestPageResponse<MLPUser> in getUserDetails() 
		MLPUser mlpUser = getUserDetails();
		MLPNotebook mlpNotebook = buildMLPNotebook();
		Boolean result = true;
		
		when(cmnDataService.getNotebook(notebook.getNoteBookId().getUuid())).thenReturn(mlpNotebook);
		result = notebookServiceImpl.isOwnerOfNotebook(authenticatedUserId, notebook.getNoteBookId().getUuid());
		Assert.assertTrue(result);
	}
	
	@Test(expected = ArchivedException.class)
	public void isNotebookArchivedTest(){
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		Boolean result = false;
		Notebook notebook = buildNotebook();
		ArtifactState artiState = new ArtifactState();
		artiState.setStatus(ArtifactStatus.ARCHIVED);
		notebook.setArtifactStatus(artiState);
		MLPNotebook mlpNotebook = buildMLPNotebook();
		
		when(cmnDataService.getNotebook(notebook.getNoteBookId().getUuid())).thenReturn(mlpNotebook);
		result = notebookServiceImpl.isNotebookArchived(notebook.getNoteBookId().getUuid());
		Assert.assertFalse(result);
		mlpNotebook.setActive(false);
		when(cmnDataService.getNotebook(notebook.getNoteBookId().getUuid())).thenReturn(mlpNotebook);
		Boolean result1 = true;
		result1 = notebookServiceImpl.isNotebookArchived(notebook.getNoteBookId().getUuid());
		Assert.assertTrue(result1);
		
	}
	// TODO : Need to work on
	//@Test
	public void updateNotebookTest(){
		
	}
	
	//@Test(expected = NotebookNotFoundException.class)
	public void getNotebookTest(){
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		MLPUser mlpUser = getUserDetails();
		Notebook notebook = buildNotebook();
		MLPNotebook mlpNotebook = buildMLPNotebook();
		when(cmnDataService.getNotebook(notebook.getNoteBookId().getUuid())).thenReturn(mlpNotebook);
		PowerMockito.when(NotebookServiceUtil.getNotebookVO(mlpNotebook, mlpUser)).thenReturn(notebook);
		Notebook notebookResult = notebookServiceImpl.getNotebook(authenticatedUserId, notebook.getNoteBookId().getUuid());
		assertNotNull(notebookResult);
		when(cmnDataService.getNotebook(notebook.getNoteBookId().getUuid())).thenReturn(null);
		Notebook notebookResult1 = notebookServiceImpl.getNotebook(authenticatedUserId, notebook.getNoteBookId().getUuid());
		assertNotNull(notebookResult1);
		
	}
	
	//@Test(expected = Exception.class)
	public void getNotebooksTest() {
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		MLPUser mlpUser = getUserDetails();
		List<MLPNotebook> mlpNotebookList = new ArrayList<MLPNotebook>();
		MLPNotebook mlpNotebook = buildMLPNotebook();
		mlpNotebookList.add(mlpNotebook);
		List<Notebook> notebookList = new ArrayList<Notebook>();
		Notebook notebook = buildNotebook();
		notebookList.add(notebook);
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("userId",mlpUser.getUserId());
		PageRequest pageReq = new PageRequest(0, 10);
		RestPageRequest restPageReq = new RestPageRequest(0,10);
		when(confProps.getResultsetSize()).thenReturn(10);
		RestPageResponse<MLPNotebook> pageResp = new RestPageResponse<MLPNotebook>(mlpNotebookList, pageReq, 1);
		when(cmnDataService.searchNotebooks(queryParams, false, restPageReq)).thenReturn(pageResp);
		PowerMockito.when(NotebookServiceUtil.getNotebookVOs(mlpNotebookList, mlpUser)).thenReturn(notebookList);
		
		List<Notebook> result = notebookServiceImpl.getNotebooks(authenticatedUserId, null);
		assertNotNull(result);
		
		when(cmnDataService.getProjectNotebooks("123")).thenReturn(mlpNotebookList);
		PowerMockito.when(NotebookServiceUtil.getNotebookVOs(mlpNotebookList, mlpUser)).thenReturn(notebookList);
		List<Notebook> result1 = notebookServiceImpl.getNotebooks(authenticatedUserId, "123");
		assertNotNull(result1);
		
	}
	
	//@Test
	public void deleteNotebookTest(){
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		ServiceState state = new ServiceState();
		state.setStatus(ServiceStatus.COMPLETED);
		List<MLPProject> mlpProjects = new ArrayList<MLPProject>();
		MLPProject mlpProject = buildMLPProject();
		mlpProjects.add(mlpProject);
		when(cmnDataService.getNotebookProjects("123")).thenReturn(mlpProjects);
		doNothing().when(cmnDataService).dropProjectNotebook(mlpProject.getProjectId(),"123");
		doNothing().when(cmnDataService).deleteNotebook("123");
		state = notebookServiceImpl.deleteNotebook(authenticatedUserId,"123");
		assertNotNull(state);
		
	}
	
	@Test(expected = Exception.class)
	public void getUserDetailsTest(){
		MLPUser mlpUser = getUserDetails();
		assertNotNull(mlpUser);
	}
	
	// TODO : Need to work on
	//@Test
	public void archiveNotebookTest(){
		
	}
	
	// TODO : Need to work on
	// @Test
	public void launchNotebookTest() {

	}
	
	
	// TODO : Need to work on
	// @Test
	public void isNotebookProjectAssociatedTest() {

	}
	

	private MLPNotebook buildMLPNotebook() {
		MLPNotebook mlpNotebook = new MLPNotebook();
		mlpNotebook.setActive(true);
		mlpNotebook.setCreated(Instant.now());
		mlpNotebook.setDescription("NotebookDescription");
		mlpNotebook.setKernelTypeCode("X86");
		mlpNotebook.setModified(Instant.now());
		mlpNotebook.setName("NewMLPNotebook");
		mlpNotebook.setNotebookId("123");
		mlpNotebook.setNotebookTypeCode("JUPYTER");
		mlpNotebook.setRepositoryUrl("http://acumos-nexus01.eastus.cloudapp.azure.com:8081/repositoryURL");
		mlpNotebook.setServiceStatusCode("CO");
		mlpNotebook.setServiceUrl("http://acumos-nexus01.eastus.cloudapp.azure.com:8081/serviceURL");
		mlpNotebook.setUserId(authenticatedUserId);
		mlpNotebook.setVersion("1.0.0");
		return mlpNotebook;
	}
	
	
	

	@SuppressWarnings("deprecation")
	private MLPUser getUserDetails() {
		List<MLPUser> mlpUserList =  new ArrayList<MLPUser>();
		MLPUser mlpUser = new MLPUser();
		mlpUser.setActive(true);
		mlpUser.setCreated(Instant.now());
		mlpUser.setEmail("email@att.com");
		mlpUser.setFirstName("Mukesh");
		mlpUser.setLastName("Manthan");
		mlpUser.setLastLogin(Instant.now());
		mlpUser.setLoginName("techmdev");
		mlpUser.setLoginPassExpire(Instant.now());
		mlpUser.setMiddleName("Ratan");
		mlpUser.setModified(Instant.now());
		mlpUser.setOrgName("TechMahindra");
		mlpUser.setUserId("19a554b1-4b00-4135-a122-2b6061480185");
		mlpUser.setVerifyExpiration(Instant.now());
		mlpUserList.add(mlpUser);
		
		when(confProps.getResultsetSize()).thenReturn(10);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", "techmdev");
		PageRequest pageRequest = new PageRequest(0, 10);
		RestPageRequest restPageRequets = new RestPageRequest(0,10);
		RestPageResponse<MLPUser> pageResponse = new RestPageResponse<>(mlpUserList, pageRequest, 1);
		when(cmnDataService.searchUsers(queryParameters, false, restPageRequets)).thenReturn(pageResponse);
		when(notebookServiceImpl.getUserDetails("19a554b1-4b00-4135-a122-2b6061480185")).thenReturn(mlpUser);
		
		return mlpUser;
	}
	
	private MLPProject buildMLPProject() {
		MLPProject mlpProject = new MLPProject();
		mlpProject.setActive(true);
		mlpProject.setCreated(Instant.now());
		mlpProject.setDescription("MLPProject Description");
		mlpProject.setModified(Instant.now());
		mlpProject.setName("ProjectService");
		mlpProject.setProjectId("123");
		mlpProject.setRepositoryUrl("https://nexus.acumos.org/content/repositories");
		mlpProject.setServiceStatusCode("ACTIVE");
		mlpProject.setUserId(authenticatedUserId);
		mlpProject.setVersion("1.0.0");
		return mlpProject;
	}
	
	*/
}
