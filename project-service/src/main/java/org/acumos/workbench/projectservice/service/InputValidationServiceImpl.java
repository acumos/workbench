/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acumos.workbench.projectservice.exception.IncorrectValueException;
import org.acumos.workbench.projectservice.exception.InvalidInputJSONException;
import org.acumos.workbench.projectservice.exception.ValueNotFoundException;
import org.acumos.workbench.projectservice.util.ValidationRule;
import org.acumos.workbench.projectservice.vo.Identifier;
import org.acumos.workbench.projectservice.vo.Project;
import org.acumos.workbench.projectservice.vo.Version;
import org.springframework.stereotype.Service;

@Service("InputValidationServiceImpl")
public class InputValidationServiceImpl implements InputValidationService {

	
	
	public void validateProjectInputJson(Project project) throws InvalidInputJSONException { 
		boolean result = false;
		
		if(null != project) { 
			if(null != project.getProjectId() && null != project.getDescription()) { 
				Identifier projectIdentifier = project.getProjectId();
				if(null != projectIdentifier.getName() && null != projectIdentifier.getVersionId()) { 
					Version version = projectIdentifier.getVersionId();
					if(null != version.getLabel()){
						result = true;
					}
				}
			}
		} 
		
		if(!result){
			throw new InvalidInputJSONException();
		}
	}
	
	public void isValuePresent(String fieldName, String value) throws ValueNotFoundException { 
		boolean result = false;
		String msg = fieldName + " is missing"; 
		
		if(null != value && !value.trim().equals("")){
			result = true;
		}
		
		if(!result) {
			throw new ValueNotFoundException(msg);
		}
	}
	
	public void validateProjectName(String value) throws IncorrectValueException { 
		boolean result = false; 
		
		String msg = "Project Name Syntax Invalid";
		
		result = validateInputValue(ValidationRule.NAME, value);
		
		if(!result) { 
			throw new IncorrectValueException(msg);
		}
		
	}
	
	public void validateVersion(String value) throws IncorrectValueException { 
		boolean result = false; 
		
		String msg = "Project Version Syntax Invalid";
		
		result = validateInputValue(ValidationRule.VERSION, value);
		
		if(!result) { 
			throw new IncorrectValueException(msg);
		}
		
	}
	
	private boolean validateInputValue(ValidationRule rule, String value) {
		Pattern pattern = Pattern.compile(rule.getPattern());
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

}
