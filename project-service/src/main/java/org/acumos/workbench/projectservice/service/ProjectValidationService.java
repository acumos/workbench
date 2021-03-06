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

import org.acumos.workbench.common.vo.Project;

public interface ProjectValidationService {

	/**
	 * To validate Input for : 
	 * 		1. Only Name, version, and description should be the input values
	 * 		2. AuthenticatedUserId value should be present.
	 * 		3. Project name and version value should be present.
	 * 		4. Input value validation for Project Name & Version (for allowed special character).
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param project
	 * 		the Project object 
	 */
	public void validateInput(String authenticatedUserId, Project project );
	
	
}
