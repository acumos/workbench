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

package org.acumos.workbench.projectservice.controller;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.projectservice.service.ProjectServiceImpl;
import org.acumos.workbench.projectservice.service.ProjectSharingServiceImpl;
import org.acumos.workbench.projectservice.service.ProjectSharingValidationSercviceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/")
public class ProjectSharingController {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	@Autowired
	ProjectSharingValidationSercviceImpl projectSharingValidationSercviceImpl;
	@Autowired
	ProjectSharingServiceImpl projectSharingServiceImpl;
	@Autowired
	ProjectServiceImpl projectServiceImpl;

	/** The method to share Project to user.
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * @param projectId
	 *            the projectId
	 * @param collaborators
	 *            the collaborators object(User)
	 * @return the project object
	 */
	@ApiOperation(value = "Share Project with user")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/collaborators", method = RequestMethod.POST)
	public ResponseEntity<Project> shareProject(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "The Project Id", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "Collaborators Details", required = true) @RequestBody Users collaborators) {
		logger.debug("shareProjects() Begin");
		
		// Check if authenticated User exists
		projectSharingValidationSercviceImpl.isUserExists(authenticatedUserId);
		
		// Check if User is owner of the project
		projectServiceImpl.isOwnerOfProject(authenticatedUserId, projectId);
		
		// Check if input Users in input collaborator list exists (i.e., status Active)
		projectSharingValidationSercviceImpl.isActiveUser(collaborators);
		
		//Check if Project for input ProjectId exists (i.e., status Active)
		projectServiceImpl.projectExists(projectId);
		
		//Check if user in the input collaborators list is already a collaborator  for a Project
		projectSharingValidationSercviceImpl.isCollaboratorExists(collaborators, projectId);
		
		Project result = projectSharingServiceImpl.shareProject(authenticatedUserId, projectId, collaborators);
		
		logger.debug("shareProjects() End");
		return new ResponseEntity<Project>(result, HttpStatus.OK);

    }

	/**
	 * To remove User from Collaborator list
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * @param projectId
	 *            the projectId
	 * @param collaborators
	 *            the collaborators
	 * @return the project object
	 * 
	 */
	@ApiOperation(value = "Remove User from Collaborator list")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/collaborators", method = RequestMethod.DELETE)
	public ResponseEntity<Project> removeCollaborator(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "The Project Id", required = true)@PathVariable("projectId") String projectId,
			@ApiParam(value = "Collaborators Details", required = true) @RequestBody Users collaborators) {
		logger.debug("shareProjects() Begin");
		
		// Check if authenticated User exists
		projectSharingValidationSercviceImpl.isUserExists(authenticatedUserId);
		
		// Check if User is owner of the project
		projectServiceImpl.isOwnerOfProject(authenticatedUserId, projectId);
		
		// Check if input Users in input collaborator list exists (i.e., status Active)
		projectSharingValidationSercviceImpl.isActiveUser(collaborators);
		
		//Check if Project for input ProjectId exists (i.e., status Active)
		projectServiceImpl.projectExists(projectId);
		
		//check if Users input by user is exists in couchdb (i.e they are collaborators or not) 
		projectSharingValidationSercviceImpl.isCollaboratorRemovable(collaborators, projectId);
		
		Project result=projectSharingServiceImpl.removeCollaborator(authenticatedUserId, projectId, collaborators);
		
		logger.debug("shareProjects() End");
		
		return new ResponseEntity<Project>(result, HttpStatus.OK);

    }

	/**
	 * To get the list of shared projects for logged in user
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 *
	 * @return the List of project object
	 * 
	 */
	@ApiOperation(value = "Get the list of shared Project for the logged in User")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/shared", method = RequestMethod.GET)
	public ResponseEntity<List<Project>> getSharedProjects(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId) {
		logger.debug("getSharedProjects() Begin");
		
		// Check if authenticated User exists
		projectSharingValidationSercviceImpl.isUserExists(authenticatedUserId);
		
		List<Project> result = projectSharingServiceImpl.getSharedProjects(authenticatedUserId);
		
		logger.debug("getSharedProjects() End");
		
		return new ResponseEntity<List<Project>>(result, HttpStatus.OK);

	}
	
	
	
}
