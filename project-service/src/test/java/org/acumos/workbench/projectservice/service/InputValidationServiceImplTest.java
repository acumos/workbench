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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.acumos.workbench.projectservice.exception.IncorrectValueException;
import org.acumos.workbench.projectservice.exception.InvalidInputJSONException;
import org.acumos.workbench.projectservice.exception.ValueNotFoundException;
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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InputValidationServiceImplTest {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	String authenticatedUserId = "123";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	InputValidationServiceImpl inputValidationServiceImpl;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = InvalidInputJSONException.class)
	public void validateInputTest() throws InvalidInputJSONException {
		Project project = getProject();
		inputValidationServiceImpl.validateProjectInputJson(project);
		project.getProjectId().getVersionId().setLabel(null);
		inputValidationServiceImpl.validateProjectInputJson(project);

	}
	@Test(expected = ValueNotFoundException.class)
	public void isValuePresentTest() throws ValueNotFoundException{
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		authenticatedUserId = null;
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
	}
	
	@Test(expected = IncorrectValueException.class)
	public void validateProjectNameTest() throws IncorrectValueException{
		inputValidationServiceImpl.validateProjectName("ProjectService");
		inputValidationServiceImpl.validateProjectName("#$%ProjectService");
	}
	
	@Test(expected = IncorrectValueException.class)
	public void validateVersion() throws IncorrectValueException{
		inputValidationServiceImpl.validateVersion("1.0.0");
		inputValidationServiceImpl.validateVersion("@#$1.0.0");
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
