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
 * 
 * ===============LICENSE_END=========================================================
 */

package org.acumos.workbench.projectservice.service;

import static org.mockito.Mockito.doNothing;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.projectservice.controller.UnitTestCommons;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProjectValidationServiceImplTest extends UnitTestCommons {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String authenticatedUserId = "123"; 
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private ProjectValidationServiceImpl projectValidationServiceImpl;
	
	@Mock
	InputValidationServiceImpl inputValidationServiceImpl;
	
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void validateInputTest(){
		Project project = buildProject();
		doNothing().when(inputValidationServiceImpl).validateProjectInputJson(project);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Acumos User Id",authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Name", project.getProjectId().getName());
		doNothing().when(inputValidationServiceImpl).isValuePresent("Project Version", project.getProjectId().getVersionId().getLabel());
		doNothing().when(inputValidationServiceImpl).validateProjectName(project.getProjectId().getName());
		doNothing().when(inputValidationServiceImpl).validateVersion(project.getProjectId().getVersionId().getLabel());
		projectValidationServiceImpl.validateInput(authenticatedUserId, project);
	}
	
}
