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

import java.util.List;

import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Users;

public interface ProjectSharingService {
	/**
	 * This method insert the entry in couch db for Project sharing
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * @param projectId
	 *            the projectId
	 * @param collaborators
	 *            the collaborators
	 * @return the project object
	 */
	public Project ShareProject(String authenticatedUserId, String projectId, Users collaborators);

	/**
	 * This method remove the collaborator entry in couch db for Project sharing
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * @param projectId
	 *            the projectId
	 * @param collaborators
	 *            the collaborators
	 * @return the project object
	 */
	public Project removeCollaborator(String authenticatedUserId, String projectId, Users collaborators);

	/**
	 * This method gives the list of shared projects for the user
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * @return the project object
	 */
	public List<Project> getSharedProjects(String authenticatedUserId);

}
