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

import org.acumos.workbench.common.vo.Users;

public interface ProjectSharingValidationService {
	
	/**
	 * To check if User Exists in CDS
	 * 
	 * @param authenticatedUserId 
	 * 			the authenticatedUserId
	 * 
	 * @throws UserNotFoundException
	 */
	public void isUserExists(String authenticatedUserId);

	/**
	 * To check if Users list Exists in CDS
	 * 
	 * @param collaborators
	 * 				 the collaborators
	 * @throws UserNotFoundException
	 */
	public void isActiveUser(Users collaborators);
	

	/**
	 * To check if user in the input collaborators list is already a collaborator for a Project
	 * 
	 * @param collaborators
	 *            the collaborators
	 * @param projectId
	 *            the projectId
	 * 
	 * @throws DuplicateCollaboratorException
	 */
	public void isCollaboratorExists(Users collaborators,String projectId);

	
	/**
	 * To check is the user is exists in the collaborator list
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * 
	 * @param projectId
	 *            the project id
	 * 
	 * @throws UserNotFoundException
	 */
	public void isCollaboratorRemovable(Users collaborators,String projectId);
	
}
