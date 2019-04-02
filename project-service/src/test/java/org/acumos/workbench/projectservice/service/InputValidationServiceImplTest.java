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

import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.projectservice.controller.UnitTestCommons;
import org.acumos.workbench.projectservice.exception.IncorrectValueException;
import org.acumos.workbench.projectservice.exception.InvalidInputJSONException;
import org.acumos.workbench.projectservice.exception.ValueNotFoundException;
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
public class InputValidationServiceImplTest extends UnitTestCommons{
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String authenticatedUserId = "123";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private InputValidationServiceImpl inputValidationServiceImpl;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = InvalidInputJSONException.class)
	public void validateInputTest() throws InvalidInputJSONException {
		Project project = buildProject();
		inputValidationServiceImpl.validateProjectInputJson(project);
		project.getProjectId().getVersionId().setLabel(null);
		inputValidationServiceImpl.validateProjectInputJson(project);

	}
	@Test(expected = ValueNotFoundException.class)
	public void isValuePresentTest() throws ValueNotFoundException{
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		inputValidationServiceImpl.isValuePresent("Acumos User Id","");
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

}
