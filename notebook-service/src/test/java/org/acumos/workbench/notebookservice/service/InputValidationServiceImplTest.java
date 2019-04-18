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

import static org.mockito.Mockito.when;

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.notebookservice.controller.NotebookCommons;
import org.acumos.workbench.notebookservice.exception.IncorrectValueException;
import org.acumos.workbench.notebookservice.exception.InvalidInputJSONException;
import org.acumos.workbench.notebookservice.exception.ValueNotFoundException;
import org.acumos.workbench.notebookservice.util.NotebookServiceProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InputValidationServiceImplTest extends NotebookCommons{
	
	private static final String authenticatedUserId = "123";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private InputValidationServiceImpl inputValidationServiceImpl;
	@Mock
	private NotebookServiceProperties props;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateNotebookInputTest()throws InvalidInputJSONException {
		Notebook notebook = buildNotebook();
		inputValidationServiceImpl.validateNotebookInput(notebook);
		notebook.setNotebookType(null);
		inputValidationServiceImpl.validateNotebookInput(notebook);
		
	}
	
	@Test(expected = IncorrectValueException.class)
	public void validateNotebookNameTest() throws IncorrectValueException{
		when(props.getInvalidNotebookName()).thenReturn("Notebook Name Syntax Invalid");
		inputValidationServiceImpl.validateNotebookName("NotebookServiceName");
		inputValidationServiceImpl.validateNotebookName("!@#$%NotebookName");
	}
	
	@Test(expected = ValueNotFoundException.class)
	public void isValuePresentTest()throws ValueNotFoundException{
		when(props.getMissingFieldValue()).thenReturn("Mandatory field: {0} is missing");
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		inputValidationServiceImpl.isValuePresent("Acumos User Id","");
	}
	
	@Test(expected = IncorrectValueException.class)
	public void validateVersionTest() throws IncorrectValueException{
		when(props.getInvalidNotebookVersion()).thenReturn("Notebook Version Syntax Invalid");
		inputValidationServiceImpl.validateVersion("1.0.0");
		inputValidationServiceImpl.validateVersion("!@#1.0.0");
	}
	
	@Test(expected = IncorrectValueException.class)
	public void validateNotebookTypeTest() throws IncorrectValueException{
		when(props.getInvalidNotebookType()).thenReturn("Invalid notebook type provided");
		inputValidationServiceImpl.validateNotebookType("JUPYTER");
		inputValidationServiceImpl.validateNotebookType("ZP");
		
	}
	
	
}
