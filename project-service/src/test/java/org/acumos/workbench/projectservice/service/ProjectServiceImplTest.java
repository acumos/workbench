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


package org.acumos.workbench.projectservice.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.projectservice.config.HandlerInterceptorConfiguration;
import org.acumos.workbench.projectservice.controller.UnitTestCommons;
import org.acumos.workbench.projectservice.exception.ArchivedException;
import org.acumos.workbench.projectservice.exception.DuplicateProjectException;
import org.acumos.workbench.projectservice.exception.ProjectNotFoundException;
import org.acumos.workbench.projectservice.util.ProjectServiceUtil;
import org.acumos.workbench.projectservice.util.WBProjectProperties;
import org.acumos.workbench.projectservice.vo.Project;
import org.junit.Before;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@RunWith(SpringRunner.class)
@PrepareForTest(ProjectServiceUtil.class)
public class ProjectServiceImplTest extends UnitTestCommons{
	
private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String authenticatedUserId = "123";
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private ProjectServiceImpl projectServiceImpl;
	
	@Mock
    private CommonDataServiceRestClientImpl cmnDataService;
	
	@Mock
	private WBProjectProperties props;
	
	@Mock
	private ProjectServiceUtil projectServiceUtil;
	
	@Mock
	private HandlerInterceptorConfiguration handlerInterceptorConfiguration;
	
	
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
	}
	//@Test(expected = DuplicateProjectException.class)
	//@Test
	//@SuppressWarnings("deprecation")
	@SuppressWarnings("deprecation")
	public void projectExistsTest() throws DuplicateProjectException{
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		Project project = buildProject();
		
		MLPUser mlpUser = getUserDetails();
		
		List<MLPProject> mlpProjectList = new ArrayList<MLPProject>();
		MLPProject mlpProject = buildMLPProject();
		mlpProjectList.add(mlpProject);
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("name",project.getProjectId().getName());
		queryParams.put("version",project.getProjectId().getVersionId().getLabel());
		queryParams.put("userId",mlpUser.getUserId());
		Pageable pageReq = new PageRequest(0, 10);
		RestPageRequest restPageReq = new RestPageRequest(0,10);
		RestPageResponse<MLPProject> pageResp = new RestPageResponse<>(mlpProjectList, pageReq, 1);
		when(cmnDataService.searchProjects(queryParams, false, restPageReq)).thenReturn(pageResp);
		projectServiceImpl.projectExists(authenticatedUserId, project);
		
	}
	
	
	//@Test
	public void createProjectTest(){
		MLPUser mlpUser = getUserDetails();
		Project project = buildProject();
		
		MLPProject mlpProject = buildMLPProject();
		PowerMockito.mockStatic(ProjectServiceUtil.class);
		PowerMockito.when(ProjectServiceUtil.getMLPProject(mlpUser.getUserId(), project)).thenReturn(mlpProject);
		//when(projectServiceUtil.getMLPProject(mlpUser.getUserId(), project)).thenReturn(mlpProject);
		when(cmnDataService.createProject(mlpProject)).thenReturn(mlpProject);
		PowerMockito.when(ProjectServiceUtil.getProjectVO(mlpProject,mlpUser)).thenReturn(project);
		//when(projectServiceUtil.getProjectVO(mlpProject,mlpUser)).thenReturn(project);
		projectServiceImpl.createProject(authenticatedUserId, project);
	}
	
	//@SuppressWarnings("deprecation")
	//@Test
	@SuppressWarnings("deprecation")
	public void isOwnerOfProjectTest(){
		//Project project = getProject();
		MLPUser mlpUser = getUserDetails();
		
		List<MLPProject> mlpProjectList = new ArrayList<MLPProject>();
		MLPProject mlpProject = buildMLPProject();
		mlpProjectList.add(mlpProject);
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("userId",mlpUser.getUserId());
		queryParams.put("projectId","123");
		Pageable pageReq = new PageRequest(0, 10);
		RestPageRequest restPageReq = new RestPageRequest(0,10);
		RestPageResponse<MLPProject> pageResp = new RestPageResponse<>(mlpProjectList, pageReq, 1);
		when(cmnDataService.searchProjects(queryParams, false, restPageReq)).thenReturn(pageResp);
		
		projectServiceImpl.isOwnerOfProject(authenticatedUserId, "123");
		
	}
	
	@Test(expected = ArchivedException.class)
	public void isProjectArchived() throws ArchivedException{
		MLPProject mlpProject = buildMLPProject();
		when(cmnDataService.getProject("123")).thenReturn(mlpProject);
		projectServiceImpl.isProjectArchived("123");
		mlpProject.setActive(false);
		mlpProject.setServiceStatusCode("INACTIVE");
		when(cmnDataService.getProject("123")).thenReturn(mlpProject);
		projectServiceImpl.isProjectArchived("123");
		
	}
	
	//@Test(expected = DuplicateProjectException.class)
	//@Test
	//@SuppressWarnings("deprecation")
	@SuppressWarnings("deprecation")
	public void updateProjectTest()throws DuplicateProjectException{
		Project project = buildProject();
		MLPUser mlpUser = getUserDetails();
		List<MLPProject> mlpProjectList = new ArrayList<MLPProject>();
		MLPProject mlpProject = buildMLPProject();
		mlpProject.setActive(false);
		mlpProjectList.add(mlpProject);
		when(cmnDataService.getProject(project.getProjectId().getUuid())).thenReturn(mlpProject);
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("name","ProjectService1");
		queryParams.put("version","2.0.0");
		queryParams.put("userId",authenticatedUserId);
		Pageable pageReq = new PageRequest(0, 10);
		RestPageRequest restPageReq = new RestPageRequest(0,10);
		// To Check the Project is duplicated or not, if yes then mlpProjectList is not null 
		//and it will throw the DuplicateProjectException else it will proceed for next.
		RestPageResponse<MLPProject> pageResp = new RestPageResponse<>(mlpProjectList, pageReq, 1);
		when(cmnDataService.searchProjects(queryParams, false, restPageReq)).thenReturn(pageResp);
		mlpProjectList.remove(0);
		mlpProjectList.add(0, null);
		RestPageResponse<MLPProject> pageResp1 = new RestPageResponse<>(mlpProjectList, pageReq, 1);
		when(cmnDataService.searchProjects(queryParams, false, restPageReq)).thenReturn(pageResp1);
		PowerMockito.mockStatic(ProjectServiceUtil.class);
		PowerMockito.when(ProjectServiceUtil.updateMLPProject(mlpProject, project)).thenReturn(mlpProject);
		//when(projectServiceUtil.updateMLPProject(mlpProject, project)).thenReturn(mlpProject);
		doNothing().when(cmnDataService).updateProject(mlpProject);
		PowerMockito.when(ProjectServiceUtil.getProjectVO(mlpProject,mlpUser)).thenReturn(project);
		//when(projectServiceUtil.getProjectVO(mlpProject, mlpUser)).thenReturn(project);
		projectServiceImpl.updateProject(authenticatedUserId, project);
		
	}
	
	//@Test(expected = ProjectNotFoundException.class)
	//@Test
	public void getProjectTest() throws ProjectNotFoundException{
		Project project = buildProject();
		MLPUser mlpUser = getUserDetails();
		MLPProject mlpProject = buildMLPProject();
		mlpProject.setActive(false);
		when(cmnDataService.getProject("123")).thenReturn(mlpProject);
		PowerMockito.mockStatic(ProjectServiceUtil.class);
		PowerMockito.when(ProjectServiceUtil.getProjectVO(mlpProject,mlpUser)).thenReturn(project);
		//when(projectServiceUtil.getProjectVO(mlpProject, mlpUser)).thenReturn(project);
		when(cmnDataService.getProject("123")).thenReturn(null);
		projectServiceImpl.getProject(authenticatedUserId, "123");
	}
	
	//@SuppressWarnings("deprecation")
	//@Test
	@SuppressWarnings("deprecation")
	public void getProjectsTest(){
		//MLPUser mlpUser = getUserDetails();
		List<MLPProject> mlpProjectList = new ArrayList<MLPProject>();
		MLPProject mlpProject = buildMLPProject();
		mlpProject.setActive(false);
		mlpProjectList.add(mlpProject);
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("userId","123");
		Pageable pageReq = new PageRequest(0, 10);
		RestPageRequest restPageReq = new RestPageRequest(0,10);
		RestPageResponse<MLPProject> pageResp = new RestPageResponse<>(mlpProjectList, pageReq, 1);
		when(cmnDataService.searchProjects(queryParams, false, restPageReq)).thenReturn(pageResp);
		projectServiceImpl.getProjects(authenticatedUserId);
	}
	
	@Test
	public void deleteProject(){
		List<MLPPipeline> mlpPipelines = new ArrayList<>();
		MLPPipeline pipeLine = new MLPPipeline();
		pipeLine.setActive(true);
		pipeLine.setCreated(Instant.now());
		pipeLine.setDescription("PipeLine Created");
		pipeLine.setModified(Instant.now());
		pipeLine.setName("PipeLine");
		pipeLine.setPipelineId("123");
		pipeLine.setRepositoryUrl("https://nexus.acumos.org/content/repositories");
		pipeLine.setServiceStatusCode("ACTIVE");
		pipeLine.setServiceUrl("https://nexus.acumos.org/content/staging");
		pipeLine.setUserId(authenticatedUserId);
		pipeLine.setVersion("1.0.0");
		mlpPipelines.add(pipeLine);
		when(cmnDataService.getProjectPipelines("123")).thenReturn(mlpPipelines);
		
		List<MLPNotebook> mlpNotebooks = new ArrayList<MLPNotebook>();
		MLPNotebook notebook = new MLPNotebook();
		notebook.setActive(true);
		notebook.setCreated(Instant.now());
		notebook.setDescription("Notebook Created");
		notebook.setKernelTypeCode("Kubectl");
		notebook.setModified(Instant.now());
		notebook.setName("Notebook");
		notebook.setNotebookId("123");
		notebook.setNotebookTypeCode("Zappelin");
		notebook.setRepositoryUrl("https://nexus.acumos.org/content/repositories");
		notebook.setServiceStatusCode("ACTIVE");
		notebook.setServiceUrl("https://nexus.acumos.org/content/staging");
		notebook.setUserId(authenticatedUserId);
		notebook.setVersion("1.0.0");
		mlpNotebooks.add(notebook);
		when(cmnDataService.getProjectNotebooks("123")).thenReturn(mlpNotebooks);
		doNothing().when(cmnDataService).dropProjectPipeline("123",pipeLine.getPipelineId());
		doNothing().when(cmnDataService).dropProjectPipeline("123",notebook.getNotebookId());
		doNothing().when(cmnDataService).deleteProject("123");
		//doNothing().when(result).setStatus(ServiceStatus.COMPLETED);
		//doNothing().when(result).setStatusMessage("Project Deleted successfully.");
		projectServiceImpl.deleteProject("123");
	}
	
	//@Test
	public void archiveProject(){
		Project project = buildProject();
		MLPUser mlpUser = getUserDetails();
		MLPProject mlpProject = buildMLPProject();
		mlpProject.setActive(false);
		when(cmnDataService.getProject("123")).thenReturn(mlpProject);
		doNothing().when(cmnDataService).updateProject(mlpProject);
		PowerMockito.mockStatic(ProjectServiceUtil.class);
		PowerMockito.when(ProjectServiceUtil.getProjectVO(mlpProject,mlpUser)).thenReturn(project);
		when(projectServiceImpl.archiveProject(authenticatedUserId, "123", "A")).thenReturn(project);
		//when(projectServiceUtil.getProjectVO(mlpProject, mlpUser)).thenReturn(project);
		projectServiceImpl.archiveProject(authenticatedUserId, "123","A");
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
		mlpUser.setUserId("123");
		mlpUser.setVerifyExpiration(Instant.now());
		mlpUserList.add(mlpUser);
		
		when(props.getResultsetSize()).thenReturn(10);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", authenticatedUserId);
		Pageable pageRequest = new PageRequest(0, 10);
		RestPageRequest restPageRequets = new RestPageRequest(0,10);
		RestPageResponse<MLPUser> pageResponse = new RestPageResponse<>(mlpUserList, pageRequest, 1);
		when(projectServiceImpl.getUserDetails(authenticatedUserId)).thenReturn(mlpUser);
		when(cmnDataService.searchUsers(queryParameters, false, restPageRequets)).thenReturn(pageResponse);
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

	

}
