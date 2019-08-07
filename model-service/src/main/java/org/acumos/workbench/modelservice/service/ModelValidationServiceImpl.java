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

package org.acumos.workbench.modelservice.service;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.vo.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ModelValidationServiceImpl")
public class ModelValidationServiceImpl implements ModelValidationService{
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private InputValidationService inputValidationService;

	@Override
	public void validateInputData(String authenticatedUserId, Model model) {
		logger.debug("validateInputData() begins");
		// The Model Service must check the Acumos User Id is exists or not
		inputValidationService.isValuePresent("Acumos User Id",authenticatedUserId);
		// Check all the mandatory fields exists or not in Json structure
		inputValidationService.validateModelInputJson(model);
		logger.debug("validateInputData() begins");
		
	}

}
