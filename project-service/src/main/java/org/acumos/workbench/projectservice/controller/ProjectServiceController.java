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

import org.acumos.workbench.projectservice.service.InputValidationService;
import org.acumos.workbench.projectservice.service.ProjectService;
import org.acumos.workbench.projectservice.service.ProjectValidationService;
import org.acumos.workbench.projectservice.vo.Project;
import org.acumos.workbench.projectservice.vo.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequestMapping(value = "/mlWorkbench/v1/")
public class ProjectServiceController {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationService;
	
	@Autowired
	@Qualifier("ProjectValidationServiceImpl")
	private ProjectValidationService projectValidationService;
	
	@Autowired
	@Qualifier("ProjectServiceImpl")
	private ProjectService projectService;
	
	/**
	 * The method to create new Project for a user.
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param project
	 * 		the new project details : name, version and description is only allowed. 
	 * 
	 * @return ResponseEntity<Project>
	 * 		returns the Project object with additional details. 
	 * 		
	 */
	@ApiOperation(value = "To create new  Project in ML Workbench")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/", method = RequestMethod.POST)
    public ResponseEntity<Project> createProject(@ApiParam(value = "The Authenticated User Id",allowableValues = "")@PathVariable("authenticatedUserId") String authenticatedUserId,@ApiParam(value = "Project") @RequestBody Project project) {
		logger.debug("createProject() Begin");
		//Validation 
		projectValidationService.validateInput(authenticatedUserId, project);
		
		// 5. Check if Project Name and Version already present for the userId in Workspace. (Call to CDS)
		projectService.projectExists(authenticatedUserId, project);
		
		// 6. If UserId not present then insert an entry in Workbench User Table.  (Call to CDS)
		
		// 7. Service call to create new project (Call to CDS)
		Project result = projectService.createProject(authenticatedUserId, project);
		
		logger.debug("createProject() End");
        return new ResponseEntity<Project>(result, HttpStatus.CREATED);
        
    }
	
	
	/**
	 * To update existing project
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param projectId
	 * 		the project id to be updated
	 * @param project
	 * 		the project details to be updated : name, version and description is only allowed.
	 * @return ResponseEntity<Project>
	 * 		returns Project object with addition and status details. 
	 */
	@ApiOperation(value = "To update existing project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@ApiParam(value = "The Authenticated User Id",allowableValues="")@PathVariable("authenticatedUserId") String authenticatedUserId,@ApiParam(value = "ProjectId",allowableValues = "") @PathVariable("projectId") String projectId,@ApiParam(value ="Project") @RequestBody Project project) {
		logger.debug("updateProject() Begin");
		Project result = null;
		//Validation 
		projectValidationService.validateInput(authenticatedUserId, project);
		inputValidationService.isValuePresent("ProjectId", projectId);
		
		// 5. Check if authenticated user is the owner of the Project. (Call to CDS)
		projectService.isOwnerOfProject(authenticatedUserId, projectId);
		
		// 6. Check if Project is Archived, it should not be. (Call to CDS)
		projectService.isProjectArchived(projectId);
		
		result = projectService.updateProject(authenticatedUserId, project);
		logger.debug("updateProject() End");
      	return new ResponseEntity<Project>(result, HttpStatus.OK);
    }
	
	
	/**
	 * To get the project details 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param projectId
	 * 		the project id to be updated
	 * @return ResponseEntity<Project> 
	 * 		returns Project object with all the required details. 
	 * 
	 */
	@ApiOperation(value = "To get the existing project details for a user")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProject(@ApiParam(value = "The Authenticated User Id",allowableValues = "")@PathVariable("authenticatedUserId") String authenticatedUserId,@ApiParam(value ="ProjectId", allowableValues = "") @PathVariable("projectId") String projectId) {
		logger.debug("getProject() Begin");
		// 1. Validate the input
		
		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
		
		// 3. Project Id should be present
		inputValidationService.isValuePresent("ProjectId", projectId);
		
		// 4. Check if authenticated user is the owner of the Project. (Call to CDS)
		projectService.isOwnerOfProject(authenticatedUserId, projectId);
		
		// 5. Service call to get existing project (Call to CDS)
		Project result = projectService.getProject(authenticatedUserId, projectId);
		logger.debug("getProject() End");
        return new ResponseEntity<Project>(result, HttpStatus.OK);
    }
	
	
	/**
	 * To return the list of Project accessible to the user. 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @return ResponseEntity<List<Project>>
	 * 		returns list of Project objects.
	 * 
	 */
	@ApiOperation(value = "To get list of project for a user")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/", method = RequestMethod.GET)
    public ResponseEntity<?> getProjects(@ApiParam(value = "The Authenticated User Id",allowableValues = "")@PathVariable("authenticatedUserId") String authenticatedUserId) {
		logger.debug("getProjects() Begin");
		// 1. Validate the input

		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
			
		
		// 3. Service call to get existing project (active and archive both). (Call to CDS)
		List<Project> result = projectService.getProjects(authenticatedUserId);
		logger.debug("getProjects() End");
        return new ResponseEntity<List<Project>>(result, HttpStatus.OK);
    }
	
	
	/**
	 * To delete the Project 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id.
	 * @param projectId
	 * 		the project Id of project to be deleted. 
	 * 
	 * @return ResponseEntity<ServiceState> 
	 * 		returns ServiceStat indicating project is deleted successfully. 
	 */
	@ApiOperation(value = "To delete existing project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProject(@ApiParam(value = "The Authenticated User Id",allowableValues = "")@PathVariable("authenticatedUserId") String authenticatedUserId,@ApiParam(value ="ProjectId", allowableValues = "") @PathVariable("projectId") String projectId) {
		logger.debug("deleteProject() Begin");
		ServiceState result = null;
		
		// 1. Validate the input

		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
		
		// 3. Check if the user is the owner of the project or has the permission to archive the project.(call to CDS).  
		projectService.isOwnerOfProject(authenticatedUserId, projectId);
		
		// 4. Delete Project 
		result = projectService.deleteProject(projectId);
		logger.debug("deleteProject() End");
        return new ResponseEntity<ServiceState>(result, HttpStatus.OK);
    }
	
	
	/**
	 * To archive the project. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated user Id.
	 * @param projectId
	 * 		the project Id of project to be archived. 
	 * @return ResponseEntity<Project> 
	 * 		returns the Project Object with additional details indication project is been archived. 
	 * 
	 */
	//@ApiOperation(value = "To archive existing project")
	//@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/", method = RequestMethod.PUT)
    public ResponseEntity<Project> archiveProject(@ApiParam(value = "The Authenticated User Id",allowableValues = "")@PathVariable("authenticatedUserId") String authenticatedUserId,@ApiParam(value ="ProjectId", allowableValues = "") @PathVariable("projectId") String projectId) {
    	logger.debug("archiveProject() Begin");
		// 1. Validate the input

		// 2. Check authenticatedUserId should be present
    	inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
    	
		// 3. Check if the user is the owner of the project or has the permission to archive the project.(call to CDS).  
    	projectService.isOwnerOfProject(authenticatedUserId, projectId);
    	
		// 4. Mark the project as archived (call to CDS).
		Project result = projectService.archiveProject(authenticatedUserId, projectId);
		logger.debug("archiveProject() End");
        return new ResponseEntity<Project>(result, HttpStatus.OK);
        
    }
	
}
