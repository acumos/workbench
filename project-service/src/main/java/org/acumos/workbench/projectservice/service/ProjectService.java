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

import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.NotOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.projectservice.exception.DuplicateProjectException;

public interface ProjectService {

	
	/**
	 * This method check if Project exists for input projectId. 
	 * 
	 * @param projectId
	 * 		projectId of project
	 * @throws ProjectNotFoundException
	 * 		throws ProjectNotFoundException if project not found. 
	 */
	public void projectExists(String projectId) throws ProjectNotFoundException;
	
	/**
	 * This method check if Project already exists with same name and version in DB. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * 
	 * @param project
	 * 		the Project object
	 * @throws DuplicateProjectException
	 * 		throws DuplicateProjectException if project already exists. 
	 */
	public void projectExists(String authenticatedUserId,Project project) throws DuplicateProjectException;
	
	/**
	 * The method to create Project in DB using CDS client. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated user Id. 
	 * @param project
	 * 		the Poject object 
	 * @return Project
	 * 		returns Project object with additional details. 
	 */
	public Project createProject(String authenticatedUserId, Project project);
	
	/**
	 * To check whether input userId is owner of the Project.
	 * 
	 * @param userId
	 * 		the user Id
	 * @param Id
	 * 		the project Id
	 * 
	 * @return boolean 
	 * 		returns true or false, indicating whether user with input userId is owner of the Project.
	 * 
	 * @throws NotOwnerException
	 * 		throws NotOwnerException if user is not owner of the ProjectId
	 */
	public boolean isOwnerOfProject(String authenticatedUserId, String projectId) throws NotOwnerException;
	
	/**
	 * 
	 * To check whether Project is archived. 
	 * 
	 * @param projectId
	 * 		The project Id to be verified. 
	 * @return boolean
	 * 		return true or false, indicating whether project with input project Id is archived or not.
	 * @throws ArchivedException
	 * 		throws ArchivedException is the project is archived. 
	 */
	public boolean isProjectArchived(String projectId) throws ArchivedException;
	
	/**
	 * To update the existing project. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * 
	 * @param projectId
	 * 		projectId to be updated
	 * 
	 * @param project
	 * 		Project details to be updated
	 * 
	 * @return Project
	 * 		the Project object with additional details. 
	 */
	public Project updateProject(String authenticatedUserId, String projectId, Project project) throws DuplicateProjectException;
	
	/**
	 * To get the Project details for input projectId. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * 
	 * @param projectId
	 * 		the project Id.
	 * @return Project 
	 * 		returns Project object with all required details. 
	 */
	public Project getProject(String authenticatedUserId, String projectId) throws ProjectNotFoundException;
	
	/**
	 * To get list of project for a input userId. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * 
	 * @return List<Project> 
	 * 		the list of Project objects for a input userId. 
	 */
	public List<Project> getProjects(String authenticatedUserId);
	
	/**
	 * To delete the Project for input project Id. 
	 * @param projectId
	 * 		the project Id. 
	 * @return ServiceState 
	 * 		returns ServiceState indicating Project is deleted successfully. 
	 */
	public ServiceState deleteProject(String projectId);
	
	/**
	 * To archive the Project for input project Id.
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the project Id to be archived. 
	 * @param actionType
	 * 		to Archive project actionType = A
	 * 		to un Archive project actionType = UA
	 * @return Project
	 * 		the Project object with ServiceState indication project is been archived. 
	 */
	public Project archiveProject(String authenticatedUserId, String projectId, String actionType);
	
	/**
	 * To get the user details based on authenticationUserId which is the Acumos User LoginId. 
	 * @param authenticatedUserId
	 * 		the user login id.
	 * @return MLPUser
	 * 		returns the MLPUser Instance. 
	 * 
	 * @throws UserNotFoundException
	 * 		in case if user is not found throws UserNotFoundException.
	 */
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException;
	
}
