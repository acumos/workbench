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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ProjectValidationServiceImpl")
public class ProjectValidationServiceImpl implements ProjectValidationService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationServiceImpl;
	
	@Override
	public void validateInput(String authenticatedUserId, Project project ) { 
		logger.debug("validateInput() Begin");
		//1. Validation service to validate the input: only Name, version, and description should be the input values rest should be empty.
		inputValidationServiceImpl.validateProjectInputJson(project);
		
		//2. Check authenticatedUserId should be present
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		
		//3. Project name should be present
		inputValidationServiceImpl.isValuePresent("Project Name", project.getProjectId().getName());
		inputValidationServiceImpl.isValuePresent("Project Version", project.getProjectId().getVersionId().getLabel());
		
		// 4. Validate Project Name & Version (for allowed special character)
		inputValidationServiceImpl.validateProjectName(project.getProjectId().getName());
		inputValidationServiceImpl.validateVersion(project.getProjectId().getVersionId().getLabel());
		logger.debug("validateInput() End");
	}
	
	
	
}
