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

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.notebookservice.exception.ArchivedException;
import org.acumos.workbench.notebookservice.exception.NotProjectOwnerException;
import org.acumos.workbench.notebookservice.exception.ProjectNotFoundException;
import org.acumos.workbench.notebookservice.exception.ValueNotFoundException;

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
	void validateNotebook(String authenticatedUserId, Notebook notebook );
	
	//TODO : Move method to workbench-common library module.
	/**
	 * Validates Project for : 
	 * 		1. Project exists
	 * 		2. User is owner of the Project
	 * 		3. Whether the Project is archived.
	 *  
	 * @param authenticatedUserId
	 * 		Acumos user login Id
	 * @param projectId
	 * 		Project Id
	 * @throws ValueNotFoundException
	 * 		Both input parameter is mandatory, if in case value is null of any of the parameter 
	 * 		method throws ValueNotFoundException
	 * @throws ProjectNotFoundException
	 * 		Throws ProjectNotFoundException if no project exists in DB for input projectId value.
	 * @throws NotProjectOwnerException
	 * 		Throws NotProjectOwnerException input userId is not owner of the Project
	 * @throws ArchivedException
	 * 		Throws ArchivedException if Project is archived.
	 */
	void validateProject(String authenticatedUserId,String projectId) throws ValueNotFoundException, ProjectNotFoundException, NotProjectOwnerException, ArchivedException;
	
	
}
