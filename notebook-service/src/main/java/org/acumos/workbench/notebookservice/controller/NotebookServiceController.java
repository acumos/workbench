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

package org.acumos.workbench.notebookservice.controller;

import static org.acumos.workbench.common.security.SecurityConstants.*;
import static org.acumos.workbench.notebookservice.util.NotebookServiceConstants.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.notebookservice.service.InputValidationService;
import org.acumos.workbench.notebookservice.service.NotebookService;
import org.acumos.workbench.notebookservice.service.NotebookValidationService;
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

@RestController
@RequestMapping(value = "/")
public class NotebookServiceController {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationService;
	
	@Autowired
	@Qualifier("NotebookValidationServiceImpl")
	private NotebookValidationService notebookValidationService;
	
	@Autowired
	@Qualifier("NotebookServiceImpl")
	private NotebookService notebookService;
	
	/**
	 * Creates new Notebook under a Project for a user.
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param projectId
	 * 		the new project Id
	 * 
	 * @param notebook
	 * 		the Notebook object instance with : name, type and description. Optional version. 
	 * 
	 * @return ResponseEntity<Notebook>
	 * 		returns the Notebook object with additional details. 
	 * 		
	 */
	@ApiOperation(value = "Creates new  Notebook under a Project in ML Workbench", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/notebooks", method = RequestMethod.POST)
	public ResponseEntity<?> createNotebookUnderProject(
			HttpServletRequest request,
			@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value="Project Id under which Notebook need to be created")@PathVariable("projectId") String projectId,
			@ApiParam(value="Notebook Details")@RequestBody Notebook notebook) {
		logger.debug("createNotebookUnderProject() Begin");
		String authToken = getAuthJWTToken(request);
		inputValidationService.isValuePresent(FIELD_PROJECT_ID, projectId);
		Notebook result = createNotebook(authenticatedUserId, projectId, notebook, authToken);
		logger.debug("createNotebookUnderProject() End");
        return new ResponseEntity<Notebook>(result, HttpStatus.CREATED);
        
    }
	
	/**
	 * Creates new independent Notebook for a user.
	 * @param authenticatedUserId
	 * 		the authenticated user id.
	 * @param notebook
	 * 		the Notebook object instance with : name, type and description. Optional version. 
	 *  
	 * @return ResponseEntity<Notebook>
	 * 		returns the Notebook object with additional details. 
	 */
	@ApiOperation(value = "Creates new  independent Notebook for a User in ML Workbench", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks", method = RequestMethod.POST)
    public ResponseEntity<?> createIndependentNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook details")@RequestBody Notebook notebook) {
		logger.debug("createIndependentNotebook() Begin");
		String authToken = getAuthJWTToken(request);
		Notebook result = createNotebook(authenticatedUserId, null, notebook, authToken);
		logger.debug("createIndependentNotebook() End");
        return new ResponseEntity<Notebook>(result, HttpStatus.CREATED);
		
	}
    
	
	/**
	 * Launch the Notebook associated to a Project. 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the Project Id to which Notebook is associated to.  
	 * @param notebookId
	 * 		the Notebook Id 
	 * @return
	 * 		returns Notebook instance
	 */
	@ApiOperation(value = "Launch the new instance of Notebook associated to a Project based on type and return the Notebook instance will URL.", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/notebooks/{notebookId}/launch", method = RequestMethod.GET)
	public ResponseEntity<?> launchProjectNotebook(
			HttpServletRequest request,
			@ApiParam(value="Acumos User login Id")@PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId,
			@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		logger.debug("launchProjectNotebook() Begin");
		String authToken = getAuthJWTToken(request);
		// project Id should be present 
		inputValidationService.isValuePresent(FIELD_PROJECT_ID, projectId);
		Notebook result = launchNotebook(authenticatedUserId, projectId, notebookId, authToken);
		logger.debug("launchProjectNotebook() End");
		return new ResponseEntity<Notebook>(result, HttpStatus.OK);
		
	}

	
	/**
	 * Launch the Notebook  
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param notebookId
	 * 		the Notebook Id 
	 * @return
	 * 		returns Notebook instance
	 */
	@ApiOperation(value = "Launch the new instance of Notebook based on type and return the Notebook instance with URL.", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks/{notebookId}/launch", method = RequestMethod.GET)
	public ResponseEntity<?> launchIndependentNotebook(
			HttpServletRequest request,
			@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
			@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		logger.debug("launchNotebook() Begin");
		String authToken = getAuthJWTToken(request);
		Notebook result = launchNotebook(authenticatedUserId, null, notebookId, authToken);
		logger.debug("launchNotebook() End");
		return new ResponseEntity<Notebook>(result, HttpStatus.OK);
		
	}
	
	/**
	 * Returns list of Notebooks accessible to the user for a Project. 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param projectId 
	 * 		the Project Id
	 * @return ResponseEntity<List<Notebook>>
	 * 		returns list of Notebook objects.
	 * 
	 */
	@ApiOperation(value = "Gets list of Notebook associated to a project", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/notebooks/", method = RequestMethod.GET)
    public ResponseEntity<?> getNotebooks(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId) {
		logger.debug("getNotebooks() Begin");
		String authToken = getAuthJWTToken(request);
		// 1. Validate the input

		// Check authenticatedUserId should be present
		inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		
		// project Id should be present 
		inputValidationService.isValuePresent(FIELD_PROJECT_ID, projectId);
				
		//Validate project 
		notebookValidationService.validateProject(authenticatedUserId, projectId, authToken);
		
		// Service call to get existing project (active and archive both). (Call to CDS)
		List<Notebook> result = notebookService.getNotebooks(authenticatedUserId, projectId);
		logger.debug("getNotebooks() End");
        return new ResponseEntity<List<Notebook>>(result, HttpStatus.OK);
    }
	
	
	/**
	 * Returns all Notebooks accessible to the user 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @return ResponseEntity<List<Notebook>>
	 * 		returns list of Notebook objects.
	 * 
	 */
	@ApiOperation(value = "Gets list of all Notebook for a user", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks/", method = RequestMethod.GET)
    public ResponseEntity<?> getAllNotebooks(HttpServletRequest request, @ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId) {
		logger.debug("getAllNotebooks() Begin");
		// 1. Validate the input

		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
			
		//3. Validate is user authorized to request this operation 
		
		//4. Service call to get existing Notebooks (active and archive both). (Call to CDS)
		List<Notebook> result = notebookService.getNotebooks(authenticatedUserId, null);
		logger.debug("getAllNotebooks() End");
        return new ResponseEntity<List<Notebook>>(result, HttpStatus.OK);
    }
	
	
	
	/**
	 * To update existing Notebook associated to a Project
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param projectId
	 * 		the project Id with which notebook is associated. 
	 * @param notebookId
	 * 		the notebook Id to be updated
	 * @param notebook
	 * 		the Notebook details to be updated : name, type, version and description 
	 * @return ResponseEntity<Notebook>
	 * 		returns Notebook object with addition and status details. 
	 */
	@ApiOperation(value = "Updates existing Notebook associated to a Project", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/notebooks/{notebookId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProjectNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId,
    		@ApiParam(value="Notebook Details")@RequestBody Notebook notebook) {
		logger.debug("updatePrjectNotebook() Begin");
		String authToken = getAuthJWTToken(request);
		inputValidationService.isValuePresent(FIELD_PROJECT_ID, projectId);
		 Notebook result = updateNotebook(authenticatedUserId, projectId, notebookId, notebook, authToken);
		
		logger.debug("updatePrjectNotebook() End");
      	return new ResponseEntity<Notebook>(result, HttpStatus.OK);
    }

	
	/**
	 * To update existing Notebook 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param notebookId
	 * 		the notebook Id to be updated
	 * @param notebook
	 * 		the Notebook details to be updated : name, type, version and description 
	 * @return ResponseEntity<Notebook>
	 * 		returns Notebook object with addition and status details. 
	 */
	@ApiOperation(value = "Updates existing Notebook", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks/{notebookId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId, 
    		@ApiParam(value="Notebook Details")@RequestBody Notebook notebook) {
		logger.debug("updateNotebook() Begin");
		Notebook result = updateNotebook(authenticatedUserId, null, notebookId, notebook, null);
		logger.debug("updateNotebook() End");
      	return new ResponseEntity<Notebook>(result, HttpStatus.OK);
    }
	
	/**
	 * To get the notebook details 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param notebookId
	 * 		the Notebook id 
	 * @return ResponseEntity<Notebook> 
	 * 		returns Notebook object with all the required details. 
	 * 
	 */
	@ApiOperation(value = "Gets the existing Notebook details for a user", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks/{notebookId}", method = RequestMethod.GET)
    public ResponseEntity<?> getNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		logger.debug("getNotebook() Begin");
		// 1. Validate the input
		
		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		
		// 3. Notebook Id should be present
		inputValidationService.isValuePresent(NOTEBOOK_ID, notebookId);
		
		// 4. Check if authenticated user is the owner of the Project. (Call to CDS)
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		// 5. Service call to get existing project (Call to CDS)
		Notebook result = notebookService.getNotebook(authenticatedUserId, notebookId);
		logger.debug("getNotebook() End");
        return new ResponseEntity<Notebook>(result, HttpStatus.OK);
    }
	
	
	
	/**
	 * To delete the Notebook 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id.
	 * @param notebookId
	 * 		the Notebook Id of Notebook to be deleted. 
	 * 
	 * @return ResponseEntity<ServiceState> 
	 * 		returns ServiceStat indicating Notebook is deleted successfully. 
	 */
	@ApiOperation(value = "Delete Notebook", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks/{notebookId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		logger.debug("deleteNotebook() Begin");
		ServiceState result = null;
		
		//Check authenticatedUserId should be present
		inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		
		// Notebook Exists 
		notebookService.notebookExists(notebookId);
		
		//Check if the user is the owner of the Notebook or has the permission to archive the Notebook.(call to CDS).  
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		//Delete Notebook 
		result = notebookService.deleteNotebook(authenticatedUserId, notebookId);
		logger.debug("deleteNotebook() End");
        return new ResponseEntity<ServiceState>(result, HttpStatus.OK);
    }
	
	/**
	 * Archive/Un Archive Notebook associated to a Project
	 * @param authenticatedUserId
	 * 		Authenticated UserId i.e., login Id.
	 * @param projectId
	 * 		Associated project Id
	 * @param notebookId
	 * 		Notebook Id to be archived
	 * @param actionType
	 * 		To archive Notebook : actionType = A (default)
	 * 		To Un archive Notebook : actionType = UA 
	 * @return
	 * 		returns the Notebook Object with additional details indication project is been archived and status = 200.
	 */
	@ApiOperation(value = "Archive/Un Archive Notebook associated to a Project", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/notebooks/{notebookId}/{actionType}", method = RequestMethod.PUT)
    public ResponseEntity<Notebook> archiveProjectNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId,
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId,
    		@ApiParam(value="Action Type", allowableValues = "A, UA")@PathVariable("actionType") String actionType) {
		logger.debug("archiveProjectNotebook() Begin");
		String authToken = getAuthJWTToken(request);
		inputValidationService.isValuePresent(FIELD_PROJECT_ID, projectId);
		Notebook result = archiveNotebook(authenticatedUserId, projectId, notebookId, actionType, authToken);
		logger.debug("archiveProjectNotebook() End");
        return new ResponseEntity<Notebook>(result, HttpStatus.OK);
        
    }
	
	/**
	 * Archive/Un Archive Notebook 
	 * @param authenticatedUserId
	 * 		Authenticated UserId i.e., login Id.
	 * @param notebookId
	 * 		Notebook Id to be archived
	 * @param actionType
	 * 		To archive Notebook : actionType = A (default)
	 * 		To Un archive Notebook : actionType = UA 
	 * @return
	 * 		returns the Notebook Object with additional details indication project is been archived and status = 200.
	 */
	@ApiOperation(value = "Archive/Un Archive Notebook ", response = Notebook.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/notebooks/{notebookId}/{actionType}", method = RequestMethod.PUT)
    public ResponseEntity<Notebook> archiveNotebook(
    		HttpServletRequest request,
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId,
    		@ApiParam(value="Action Type ", allowableValues = "A, UA")@PathVariable("actionType") String actionType) {
		logger.debug("archiveNotebook() Begin");
		Notebook result = null;
		result = archiveNotebook(authenticatedUserId, null, notebookId, actionType, null);
		logger.debug("archiveNotebook() End");
        return new ResponseEntity<Notebook>(result, HttpStatus.OK);
        
    }
	
	private Notebook createNotebook(String authenticatedUserId, String projectId, Notebook notebook, String authToken) {
		logger.debug("createNotebook() Begin");
		
		//Check authenticatedUserId should be present
    	inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
    	
	 	//Validation 
		notebookValidationService.validateNotebook(authenticatedUserId, notebook);
		
		if(null != projectId ){
			notebookValidationService.validateProject(authenticatedUserId, projectId, authToken);
		}
		
		// 5. Check if Notebook Name and Notebook Type [and Version optional] already present for the userId in Workspace. (Call to CDS)
		notebookService.notebookExists(authenticatedUserId, projectId, notebook);
		
		// 6. If UserId not present then insert an entry in Workbench User Table.  (Call to CDS)
		
		// 7. Service call to create new Notebook (Call to CDS)
		Notebook result = notebookService.createNotebook(authenticatedUserId, projectId, notebook);
		logger.debug("createNotebook() End");
		return result;
	}
   
	
	private Notebook archiveNotebook(String authenticatedUserId,
			String projectId, String notebookId, String actionType, String authToken) {
		logger.debug("archiveNotebook() Begin");
		Notebook result;
		// 1. Validate the input

		//Check authenticatedUserId should be present
    	inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
    	
    	//Check projectId is present 
    	if(null != projectId) {
    		notebookValidationService.validateProject(authenticatedUserId, projectId, authToken);
    	}
    	//Check notebookId is present 
    	inputValidationService.isValuePresent(NOTEBOOK_ID, notebookId);
    	
    	notebookService.notebookExists(notebookId);
    	
		// Check if the user is the owner of the notebook or has the permission to archive/un archive the project.(call to CDS).  
    	notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
    	
    	//TODO : Check if the Notebook is referenced by other Users or in Other Projects.  Need to discuss how to get this done. 
    	
    	// 4. Mark the project as archived/un archived (call to CDS).
		result = notebookService.archiveNotebook(authenticatedUserId, projectId, notebookId, actionType);
		logger.debug("archiveNotebook() End");
		return result;
	}
	

	private Notebook updateNotebook(String authenticatedUserId, String projectId,
			String notebookId, Notebook notebook, String authToken) {
		Notebook result = null;
		
		//Check authenticatedUserId should be present
    	inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
    	
		if(null != projectId) {
			notebookValidationService.validateProject(authenticatedUserId, projectId, authToken);
		}
		//Validation 
		notebookValidationService.validateNotebook(authenticatedUserId, notebook);
		
		//Check if notebook for given id exists 
		notebookService.notebookExists(notebookId);
		//Check if authenticated user is the owner of the Notebook. (Call to CDS)
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		result = notebookService.updateNotebook(authenticatedUserId, projectId, notebookId, notebook);
		return result;
	}
	
	private Notebook launchNotebook(String authenticatedUserId, String projectId, String notebookId, String authToken) {
		Notebook result = null;
		// Check authenticatedUserId should be present
		inputValidationService.isValuePresent(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
				
		
		// notebookId should be present 
		inputValidationService.isValuePresent(NOTEBOOK_ID, notebookId);
		
		//4. notebook should exists
		notebookService.notebookExists(notebookId);
		
		//5. notebook should not be archived
		notebookService.isNotebookArchived(notebookId);
		
		//6. check if user is authorized to launch the notebook 
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		if (null != projectId) {
			//check if notebook is associated to a project
			notebookService.isNotebookProjectAssociated(projectId, notebookId);
			notebookValidationService.validateProject(authenticatedUserId, projectId, authToken);
		}
		
		//7. Call JupyterHub Server to start an instance of the Notebook Server for the user
		result = notebookService.launchNotebook(authenticatedUserId, projectId, notebookId);
		return result;
	}
	
	private String getAuthJWTToken(HttpServletRequest request) { 
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authToken = null;
		authToken = httpRequest.getHeader(AUTHORIZATION_HEADER_KEY);
		
		if (authToken == null) {
			authToken = httpRequest.getHeader(JWT_TOKEN_HEADER_KEY);
		}
		if (authToken == null) {
			authToken = request.getParameter(JWT_TOKEN_HEADER_KEY);
		}
		return authToken;
	}
}
