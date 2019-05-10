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

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.Pipeline;

public interface PipeLineValidationService {

	/**
	 * To Validate the input for :
	 * 				1. Only ProjectId(If PipeLine is created under Project then only it will be there), PipeLineName, version, and description should be the input values
	 * 				2. AuthenticatedUserId value should be present.
	 * 				3. PipeLine Name value should be present.
	 * 				4. PipeLine Version is optional.
	 * 				5. Input value validation for PipeLine Name & Version (for allowed special character).
	 * @param authenticatedUserId
	 * @param pipeLine
	 */
	public void validateInputData(String authenticatedUserId, Pipeline pipeLine);
	
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
		 * @param authToken
		 * 		JWT Auth Token
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
		void validateProject(String authenticatedUserId,String projectId, String authToken) throws ValueNotFoundException, ProjectNotFoundException, NotProjectOwnerException, ArchivedException;

}
