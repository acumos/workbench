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

import org.acumos.workbench.notebookservice.vo.Notebook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("NotebookValidationServiceImpl")
public class NotebookValidationServiceImpl implements NotebookValidationService {
	
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	InputValidationService inputValidationServiceImpl;
	
	public void validateInput(String authenticatedUserId, Notebook notebook ) { 
		
		//1. Validation service to validate the input
		inputValidationServiceImpl.validateNotebookInputJson(notebook);
		
		//2. Check authenticatedUserId should be present
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		
		//3. Notebook name should be present
		inputValidationServiceImpl.isValuePresent("Notebook Name", notebook.getNoteBookId().getName());
		
		//4. Notebook Type should be present
		inputValidationServiceImpl.isValuePresent("Notebook Type ", notebook.getNotebookType());
		
		//5. Validate Notebook Name & Version (for allowed special character)
		inputValidationServiceImpl.validateNotebookName(notebook.getNoteBookId().getName());
		if(null != notebook.getNoteBookId().getVersionId().getLabel()) {
			inputValidationServiceImpl.validateVersion(notebook.getNoteBookId().getVersionId().getLabel());
		}
		
		//6. Validate Notebook Type 
		inputValidationServiceImpl.validateNotebookType(notebook.getNotebookType());
		
	}
	
	
	
}
