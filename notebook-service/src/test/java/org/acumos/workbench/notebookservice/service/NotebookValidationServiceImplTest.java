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

import static org.mockito.Mockito.doNothing;

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.notebookservice.controller.NotebookCommons;
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

//@RunWith(SpringRunner.class)
public class NotebookValidationServiceImplTest { // extends NotebookCommons {

	private static final String authenticatedUserId = "123";

	/*
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private NotebookValidationServiceImpl notebookValidationServiceImpl;
	
	@Mock
	InputValidationServiceImpl inputValidationServiceImpl;
	
	@Mock
	private NotebookServiceProperties props;
	
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
	}

	@Test
	public void validateNotebookTest(){
		Notebook notebook = buildNotebook();
		doNothing().when(inputValidationServiceImpl).validateNotebookInput(notebook);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Acumos User Id",authenticatedUserId);
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Name", notebook.getNoteBookId().getName());
		doNothing().when(inputValidationServiceImpl).isValuePresent("Notebook Type ", notebook.getNotebookType());
		doNothing().when(inputValidationServiceImpl).validateNotebookName(notebook.getNoteBookId().getName());
		doNothing().when(inputValidationServiceImpl).validateVersion(notebook.getNoteBookId().getVersionId().getLabel());
		doNothing().when(inputValidationServiceImpl).validateNotebookType(notebook.getNotebookType());
		
		notebookValidationServiceImpl.validateNotebook(authenticatedUserId, notebook);
	}
	
	*/
}
