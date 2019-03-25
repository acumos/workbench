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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acumos.workbench.projectservice.exception.IncorrectValueException;
import org.acumos.workbench.projectservice.exception.InvalidInputJSONException;
import org.acumos.workbench.projectservice.exception.ValueNotFoundException;
import org.acumos.workbench.projectservice.util.ValidationRule;
import org.acumos.workbench.projectservice.vo.Identifier;
import org.acumos.workbench.projectservice.vo.Project;
import org.acumos.workbench.projectservice.vo.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("InputValidationServiceImpl")
public class InputValidationServiceImpl implements InputValidationService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public void validateProjectInputJson(Project project) throws InvalidInputJSONException { 
		logger.debug("validateProjectInputJson() Begin");
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
			logger.error("InvalidInputJSONException occured in validateProjectInputJson()");
			throw new InvalidInputJSONException();
		}
		logger.debug("validateProjectInputJson() End");
	}
	
	public void isValuePresent(String fieldName, String value) throws ValueNotFoundException { 
		logger.debug("isValuePresent() Begin");
		boolean result = false;
		String msg = fieldName + " is missing"; 
		
		if(null != value && !value.trim().equals("")){
			result = true;
		}
		
		if(!result) {
			logger.error("ValueNotFoundException occured in isValuePresent()" + msg);
			throw new ValueNotFoundException(msg);
		}
		logger.debug("isValuePresent() End");
	}
	
	public void validateProjectName(String value) throws IncorrectValueException { 
		logger.debug("validateProjectName() Begin");
		boolean result = false; 
		
		String msg = "Project Name Syntax Invalid";
		
		result = validateInputValue(ValidationRule.NAME, value);
		
		if(!result) { 
			logger.error("IncorrectValueException occured in validateProjectName() : " + msg);
			throw new IncorrectValueException(msg);
		}
		logger.debug("validateProjectName() End");
	}
	
	public void validateVersion(String value) throws IncorrectValueException { 
		logger.debug("validateVersion() Begin");
		boolean result = false; 
		
		String msg = "Project Version Syntax Invalid";
		
		result = validateInputValue(ValidationRule.VERSION, value);
		
		if(!result) { 
			logger.error("IncorrectValueException occured in validateVersion() : " + msg);
			throw new IncorrectValueException(msg);
		}
		logger.debug("validateVersion() End");
	}
	
	private boolean validateInputValue(ValidationRule rule, String value) {
		logger.debug("validateInputValue() Begin");
		Pattern pattern = rule.getPattern();
		Matcher matcher = pattern.matcher(value);
		logger.debug("validateInputValue() End");
		return matcher.matches();
		
	}

}
