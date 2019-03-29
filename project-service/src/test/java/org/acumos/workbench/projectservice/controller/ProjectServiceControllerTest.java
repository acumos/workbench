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

package org.acumos.workbench.projectservice.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.acumos.workbench.projectservice.service.InputValidationService;
import org.acumos.workbench.projectservice.service.ProjectService;
import org.acumos.workbench.projectservice.service.ProjectValidationService;
import org.acumos.workbench.projectservice.util.ArtifactStatus;
import org.acumos.workbench.projectservice.util.IdentifierType;
import org.acumos.workbench.projectservice.vo.ArtifactState;
import org.acumos.workbench.projectservice.vo.Identifier;
import org.acumos.workbench.projectservice.vo.Models;
import org.acumos.workbench.projectservice.vo.Notebooks;
import org.acumos.workbench.projectservice.vo.Organization;
import org.acumos.workbench.projectservice.vo.Pipelines;
import org.acumos.workbench.projectservice.vo.Project;
import org.acumos.workbench.projectservice.vo.Projects;
import org.acumos.workbench.projectservice.vo.Role;
import org.acumos.workbench.projectservice.vo.User;
import org.acumos.workbench.projectservice.vo.Version;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public class ProjectServiceControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	String authenticatedUserId = "123"; 
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	ProjectServiceController projectServiceController;
	
	@Mock
	InputValidationService inputValidationService;
	
	@Mock
	ProjectValidationService projectValidationService;
	
	@Mock
	ProjectService projectService;
	
	private ProjectValidationService projectValidationServiceImpl;
	
	private ProjectService projectServiceImpl;
	
	private InputValidationService inputValidationServiceImpl;
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
		 projectValidationServiceImpl = mock(ProjectValidationService.class);
		 projectServiceImpl = mock(ProjectService.class);
		 inputValidationServiceImpl = mock(InputValidationService.class);
	}
	@Test
	public void createProjectTest(){
		Project project = getProject();
		doNothing().when(projectValidationServiceImpl).validateInput(authenticatedUserId, project);
		doNothing().when(projectServiceImpl).projectExists(authenticatedUserId, project);
		when(projectService.createProject(authenticatedUserId, project)).thenReturn(project);
		ResponseEntity<Project> pro = projectServiceController.createProject(authenticatedUserId, project);
		assertNotNull(pro);
	}
	
	@Test
	public void updateProjectTest(){
		Project project = getProject();
		doNothing().when(projectValidationServiceImpl).validateInput(authenticatedUserId, project);
		doNothing().when(inputValidationServiceImpl).isValuePresent("ProjectId", "123");
		when(projectService.isOwnerOfProject(authenticatedUserId, "123")).thenReturn(true);
		when(projectService.isProjectArchived("123")).thenReturn(true);
		when(projectService.updateProject(authenticatedUserId, project)).thenReturn(project);
		ResponseEntity<?> pro = projectServiceController.updateProject(authenticatedUserId,"123", project);
		assertNotNull(pro);
	}
	
	@Test
	public void getProjectTest(){
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("ProjectId", "123");
		
		//doNothing().doThrow(new RuntimeException()).when(inputValidationService).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		//doNothing().doThrow(new RuntimeException()).when(inputValidationService).isValuePresent("ProjectId", "123");
		when(projectService.isOwnerOfProject(authenticatedUserId, "123")).thenReturn(true);
		ResponseEntity<?> pro = projectServiceController.getProject(authenticatedUserId, "123");
		assertNotNull(pro);
	}
	
	@Test
	public void getProjectsTest(){
		
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		//doNothing().doThrow(new RuntimeException()).when(inputValidationService).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		ResponseEntity<?> pro = projectServiceController.getProjects(authenticatedUserId);
		assertNotNull(pro);
	}

	@Test
	public void deleteProjectTest(){
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		//doNothing().doThrow(new RuntimeException()).when(inputValidationService).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		when(projectService.isOwnerOfProject(authenticatedUserId, "123")).thenReturn(true);
		ResponseEntity<?> proj = projectServiceController.deleteProject(authenticatedUserId, "123");
		assertNotNull(proj);
	}
	
	@Test
	public void archiveProjectTest(){
		Project project = getProject();
		doNothing().when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		//doNothing().doThrow(new RuntimeException()).when(inputValidationServiceImpl).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		when(projectService.isOwnerOfProject(authenticatedUserId, "123")).thenReturn(true);
		when(projectService.archiveProject(authenticatedUserId, "123", "A")).thenReturn(project);
		ResponseEntity<Project> proj = projectServiceController.archiveProject(authenticatedUserId, "123","A");
		assertNotNull(proj);
	}
	
	
	private Project getProject() {
		Project project = new Project();
		ArtifactState artifactState = new ArtifactState();
		artifactState.setStatus(ArtifactStatus.ACTIVE);
		project.setArtifactStatus(artifactState);
		project.setDescription("Project Description");
		Models models = new Models();
		project.setModels(models);
		Notebooks notebooks = new Notebooks();
		project.setNotebooks(notebooks);
		User owner = new User();
		owner.setAuthenticatedUserId("123");
		owner.setModels(models);
		owner.setNotebooks(notebooks);
		Organization organization = new Organization();
		owner.setOrganization(organization);
		Pipelines pipelines = new Pipelines();
		owner.setPipelines(pipelines);
		Role roles = new Role();
		owner.setRoles(roles);
		Identifier userId = new Identifier();
		userId.setUuid("123");
		userId.setName("TechMDev");
		userId.setServiceUrl("https://acumos.org");
		userId.setIdentifierType(IdentifierType.PIPELINE);
		owner.setUserId(userId);
		List<Project> projectList = new ArrayList<Project>();
		projectList.add(project);
		Projects projects = new Projects();
		projects.setProjects(projectList);
		owner.setProjects(projects);
		project.setOwner(owner);
		Identifier identifier = new Identifier();
		identifier.setIdentifierType(IdentifierType.PIPELINE);
		identifier.setName("IdentifierName");
		identifier.setServiceUrl("https://acumos.org");
		identifier.setRepositoryUrl("https://acumos.org");
		identifier.setUuid("123");
		Version version = new Version();
		version.setComment("comment");
		version.setLabel("Label");
		version.setTimeStamp("");
		version.setUser("123");
		identifier.setVersionId(version);
		project.setProjectId(identifier);
		return project;
	}

}
