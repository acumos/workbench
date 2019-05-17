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

package org.acumos.workbench.pipelineservice.service;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acumos.workbench.common.exception.IncorrectValueException;
import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.pipelineservice.util.ValidationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("InputValidationServiceImpl")
public class InputValidationServiceImpl implements InputValidationService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Override
	public void validatePipeLineInputJsonStructure(Pipeline pipeLine) throws InvalidInputJSONException {
		logger.debug("validatePipeLineInputJsonStructure() Begin");
		boolean result = false;
		if (null != pipeLine) {
			if (null != pipeLine.getPipelineId()) {
				Identifier pipeLineIdentifier = pipeLine.getPipelineId();
				if (null != pipeLineIdentifier.getName()) {
					result = true;
				}
			}
		}
		if (!result) {
			logger.error("Incorrectly formatted input – Invalid JSON");
			throw new InvalidInputJSONException();
		}
		logger.debug("validatePipeLineInputJsonStructure() End");
	}

	@Override
	public void isValueExists(String fieldName, String value) throws ValueNotFoundException {
		logger.debug("isValueExists() Begin");
		boolean result = false;
		String msg = "Mandatory field: " + fieldName + " is missing";
		if (null != value && !value.trim().equals("")) {
			result = true;
		}
		if (!result) {
			logger.error("Mandatory field : " + fieldName + " is missing");
			throw new ValueNotFoundException(msg);
		}
		logger.debug("isValueExists() End");
	}

	@Override
	public void validatePipeLineName(String value) throws IncorrectValueException {
		logger.debug("validatePipeLineName() Begin");
		boolean result = false;
		String msg = "Pipeline Name Syntax Invalid";
		result = validateInputValue(ValidationRule.NAME, value);
		if (!result) {
			logger.error("Pipeline Name Syntax Invalid");
			throw new IncorrectValueException(msg);
		}
		logger.debug("validatePipeLineName() End");
	}


	private boolean validateInputValue(ValidationRule rule, String value) {
		logger.debug("validateInputValue() Begin");
		Pattern pattern = rule.getPattern();
		Matcher matcher = pattern.matcher(value);
		logger.debug("validateInputValue() End");
		return matcher.matches();
	}

}
