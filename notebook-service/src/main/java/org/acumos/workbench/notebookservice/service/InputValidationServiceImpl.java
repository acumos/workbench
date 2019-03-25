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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.notebookservice.exception.IncorrectValueException;
import org.acumos.workbench.notebookservice.exception.InvalidInputJSONException;
import org.acumos.workbench.notebookservice.exception.ValueNotFoundException;
import org.acumos.workbench.notebookservice.util.ValidationRule;
import org.springframework.stereotype.Service;

@Service("InputValidationServiceImpl")
public class InputValidationServiceImpl implements InputValidationService {

	private static final String NOTEBOOK_TYPE = "JUPYTER,ZEPPELIN";
	
	@Override
	public void validateNotebookInputJson(Notebook notebook) throws InvalidInputJSONException { 
		boolean result = false;
		if (null != notebook) {
			if (null != notebook.getNoteBookId() && null != notebook.getNotebookType()) {
				Identifier notebookIdentifier = notebook.getNoteBookId();
				if (null != notebookIdentifier.getName()) {
					result = true;
				}
			}
		}
		if (!result) {
			throw new InvalidInputJSONException();
		}

	}

	@Override
	public void validateNotebookName(String value) throws IncorrectValueException {
		boolean result = false; 
		
		String msg = "Notebook Name Syntax Invalid";
		
		result = validateInputValue(ValidationRule.NAME, value);
		
		if(!result) { 
			throw new IncorrectValueException(msg);
		}
		
		
	}

	@Override
	public void isValuePresent(String fieldName, String value) throws ValueNotFoundException { 
		boolean result = false;
		String msg = "Mandatory field: " + fieldName + " is missing"; 
		
		if(null != value && !value.trim().equals("")){
			result = true;
		}
		
		if(!result) {
			throw new ValueNotFoundException(msg);
		}
	}
	
	@Override
	public void validateVersion(String value) throws IncorrectValueException { 
		boolean result = false; 
		
		String msg = "Notebook Version Syntax Invalid";
		
		result = validateInputValue(ValidationRule.VERSION, value);
		
		if(!result) { 
			throw new IncorrectValueException(msg);
		}
		
	}
	
	@Override
	public void validateNotebookType(String notebookType) throws IncorrectValueException { 
		String msg = "Invalid notebook type provided";
		if(!NOTEBOOK_TYPE.contains(notebookType.toUpperCase())) { 
			throw new IncorrectValueException(msg);
		}
	}
	
	private boolean validateInputValue(ValidationRule rule, String value) {
		Pattern pattern = rule.getPattern();
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

		
}
