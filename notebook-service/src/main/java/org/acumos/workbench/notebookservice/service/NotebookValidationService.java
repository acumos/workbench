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

public interface NotebookValidationService {

	
	/**
	 * Validates : 
	 * 	1. Input 
	 *  2. authenticatedUserId should be present
	 *  3. Notebook name should be present
	 *  4. Notebook Type should be present
	 *  5. Notebook Name & Version for allowed special character
	 *  
	 * @param authenticatedUserId
	 * 		the authenticatedUserId (login id)
	 * @param notebook
	 * 		Notebook Instance to be validated
	 */
	public void validateInput(String authenticatedUserId, Notebook notebook );
	
	
}
