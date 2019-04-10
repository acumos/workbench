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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.notebookservice.service.InputValidationService;
import org.acumos.workbench.notebookservice.service.NotebookService;
import org.acumos.workbench.notebookservice.service.NotebookValidationService;
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
			@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value="Project Id under which Notebook need to be created")@PathVariable("projectId") String projectId,
			@ApiParam(value="Notebook Details")@RequestBody Notebook notebook) {
        
		Notebook result = createNotebook(authenticatedUserId, projectId, notebook);
		
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook details")@RequestBody Notebook notebook) {
		
		Notebook result = createNotebook(authenticatedUserId, null, notebook);
		
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
			@ApiParam(value="Acumos User login Id")@PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId,
			@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		Notebook result = new Notebook();
		
		// Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
				
		// project Id should be present 
		inputValidationService.isValuePresent("Project Id", projectId);

		// notebookId should be present 
		inputValidationService.isValuePresent("Notebook Id", notebookId);
		
		//4. notebook should exists
		notebookService.notebookExists(notebookId);
		
		//5. notebook should not be archived
		notebookService.isNotebookArchived(notebookId);
		
		//6. check if user is authorized to launch the notebook 
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		//check if notebook is associated to a project
		notebookService.isNotebookProjectAssociated(projectId, notebookId);
		
		//7. Call JupyterHub Server to start an instance of the Notebook Server for the user
		result = notebookService.launchNotebook(authenticatedUserId, projectId, notebookId);
				
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
	public ResponseEntity<?> launchNotebook(
			@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
			@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		Notebook result = new Notebook();
		
		// Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
				 
		
		// notebookId should be present 
		inputValidationService.isValuePresent("Notebook Id", notebookId);
		
		//4. notebook should exists
		notebookService.notebookExists(notebookId);
		
		//5. notebook should not be archived
		notebookService.isNotebookArchived(notebookId);
		
		//6. check if user is authorized to launch the notebook 
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
				
		//7. Call JupyterHub Server to start an instance of the Notebook Server for the user
		result = notebookService.launchNotebook(authenticatedUserId, null, notebookId);
		
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId) {
		
		// 1. Validate the input

		// Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
		
		// project Id should be present 
		inputValidationService.isValuePresent("Project Id", projectId);
				
		//Validate project 
		notebookValidationService.validateProject(authenticatedUserId, projectId);
		
		// Service call to get existing project (active and archive both). (Call to CDS)
		List<Notebook> result = notebookService.getNotebooks(authenticatedUserId, projectId);
		
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
    public ResponseEntity<?> getAllNotebooks(@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId) {
		
		// 1. Validate the input

		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
			
		//3. Validate is user authorized to request this operation 
		
		//4. Service call to get existing Notebooks (active and archive both). (Call to CDS)
		List<Notebook> result = notebookService.getNotebooks(authenticatedUserId, null);
		
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
    public ResponseEntity<?> updatePrjectNotebook(
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId,
    		@ApiParam(value="Notebook Details")@RequestBody Notebook notebook) {
		Notebook result = null;
		
		
		inputValidationService.isValuePresent("Project Id", projectId);
		//Validation 
		notebookValidationService.validateNotebook(authenticatedUserId, notebook);
		
		//Check if notebook for given id exists 
		notebookService.notebookExists(notebookId);
		//Check if authenticated user is the owner of the Notebook. (Call to CDS)
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		notebookValidationService.validateProject(authenticatedUserId, projectId);
		
		result = notebookService.updateNotebook(authenticatedUserId, projectId, notebookId, notebook);
			
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId, 
    		@ApiParam(value="Notebook Details")@RequestBody Notebook notebook) {
		Notebook result = null;
		
		//Validation 
		notebookValidationService.validateNotebook(authenticatedUserId, notebook);
		//check if notebook with given Id exists
		notebookService.notebookExists(notebookId);
		//Check if authenticated user is the owner of the Notebook. (Call to CDS)
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		result = notebookService.updateNotebook(authenticatedUserId, null, notebookId, notebook);
		
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		
		// 1. Validate the input
		
		// 2. Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
		
		// 3. Notebook Id should be present
		inputValidationService.isValuePresent("Notebook Id", notebookId);
		
		// 4. Check if authenticated user is the owner of the Project. (Call to CDS)
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		// 5. Service call to get existing project (Call to CDS)
		Notebook result = notebookService.getNotebook(authenticatedUserId, notebookId);
		
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId) {
		
		ServiceState result = null;
		
		//Check authenticatedUserId should be present
		inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
		
		// Notebook Exists 
		notebookService.notebookExists(notebookId);
		
		//Check if the user is the owner of the Notebook or has the permission to archive the Notebook.(call to CDS).  
		notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
		
		//Delete Notebook 
		result = notebookService.deleteNotebook(notebookId);
		
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Project Id ")@PathVariable("projectId") String projectId,
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId,
    		@ApiParam(value="Action Type", allowableValues = "A, UA")@PathVariable("actionType") String actionType) {
		Notebook result = null;
		result = archiveNotebook(authenticatedUserId, projectId, notebookId, actionType);
		
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
    		@ApiParam(value="Acumos User login Id ")@PathVariable("authenticatedUserId") String authenticatedUserId, 
    		@ApiParam(value="Notebook Id ")@PathVariable("notebookId") String notebookId,
    		@ApiParam(value="Action Type ", allowableValues = "A, UA")@PathVariable("actionType") String actionType) {
		Notebook result = null;
		result = archiveNotebook(authenticatedUserId, null, notebookId, actionType);
		
        return new ResponseEntity<Notebook>(result, HttpStatus.OK);
        
    }
	
	private Notebook createNotebook(String authenticatedUserId, String projectId, Notebook notebook) { 
	 	//Validation 
		notebookValidationService.validateNotebook(authenticatedUserId, notebook);
		
		if(null != projectId ){
			notebookValidationService.validateProject(authenticatedUserId, projectId);
		}
		
		// 5. Check if Notebook Name and Notebook Type [and Version optional] already present for the userId in Workspace. (Call to CDS)
		notebookService.notebookExists(authenticatedUserId, projectId, notebook);
		
		// 6. If UserId not present then insert an entry in Workbench User Table.  (Call to CDS)
		
		// 7. Service call to create new Notebook (Call to CDS)
		Notebook result = notebookService.createNotebook(authenticatedUserId, projectId, notebook);
		
		return result;
	}
   
	
	private Notebook archiveNotebook(String authenticatedUserId,
			String projectId, String notebookId, String actionType) {
		Notebook result;
		// 1. Validate the input

		//Check authenticatedUserId should be present
    	inputValidationService.isValuePresent("AuthenticatedUserId", authenticatedUserId);
    	
    	//Check projectId is present 
    	if(null != projectId) {
    		inputValidationService.isValuePresent("Project Id", projectId);
    	}
    	//Check notebookId is present 
    	inputValidationService.isValuePresent("Notebook Id", notebookId);
    	
		// Check if the user is the owner of the notebook or has the permission to archive/un archive the project.(call to CDS).  
    	notebookService.isOwnerOfNotebook(authenticatedUserId, notebookId);
    	
    	//TODO : Check if the Notebook is referenced by other Users or in Other Projects.  Need to discuss how to get this done. 
    	
    	// 4. Mark the project as archived/un archived (call to CDS).
		result = notebookService.archiveNotebook(authenticatedUserId, projectId, notebookId, actionType );
		return result;
	}
}
