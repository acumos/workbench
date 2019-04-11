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

package org.acumos.workbench.pipelineservice.controller;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.pipelineservice.service.InputValidationService;
import org.acumos.workbench.pipelineservice.service.PipeLineService;
import org.acumos.workbench.pipelineservice.service.PipeLineValidationService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/")
public class PipeLineServiceController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationService;

	@Autowired
	@Qualifier("PipeLineValidationServiceImpl")
	private PipeLineValidationService pipeLineValidationService;

	@Autowired
	@Qualifier("PipeLineServiceImpl")
	private PipeLineService pipeLineService;
	
	/**
	 * Creates new independent Pipeline for a user.
	 * @param authenticatedUserId
	 * 		the authenticated user id.
	 * @param pipeline
	 * 		the Pipeline object instance with : name, type and description. Optional version. 
	 *  
	 * @return ResponseEntity<Pipeline>
	 * 		returns the Pipeline object with additional details. 
	 */
	
	@ApiOperation(value = "Create new Pipeline outside the Project in ML Workbench(Independent Pipeline)")
	@RequestMapping(value = "users/{authenticatedUserId}/pipelines", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createIndependentPipeline(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {

		logger.debug("createIndependentPipeline() Begin");

		Pipeline result = createPipeline(authenticatedUserId, null, pipeLine);

		logger.debug("createIndependentPipeline() End");

		return new ResponseEntity<Pipeline>(result, HttpStatus.CREATED);
	}
	
	/**
	 * The method to create new PipeLine for a user.
	 * @param authenticatedUserId
	 *            the authenticated user id.
	 * @param pipeLine
	 *            the new pipeLine details : name, version and description is
	 *            only allowed.
	 * @return ResponseEntity<Pipeline> returns the Pipeline object with
	 *         additional details.
	 */

	@ApiOperation(value = "Create new pipeLine under the Project in ML Workbench")
	@RequestMapping(value = "users/{authenticatedUserId}/projects/{projectId}/pipelines", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createPipelineUnderProject(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {

		logger.debug("createPipelineUnderProject() Begin");

		Pipeline result = createPipeline(authenticatedUserId, projectId, pipeLine);

		logger.debug("createPipelineUnderProject() End");

		return new ResponseEntity<Pipeline>(result, HttpStatus.CREATED);
	}

	
	
	/**
	 * Launch the Pipeline associated to a Project. 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the Project Id to which Pipeline is associated to.  
	 * @param pipelineId
	 * 		the Pipeline Id 
	 * @return
	 * 		returns Pipeline instance
	 */
	
	@ApiOperation(value = "Launch the new instance of Pipeline associated to a Project based on type and return the Pipeline instance with URL.", response = Pipeline.class)
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/pipelines/{pipelineId}/launch", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> launchPipelineUnderProject(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId) {

		logger.debug("launchPipelineUnderProject() Begin");
		Pipeline result = new Pipeline();
		// 1. Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);
		
		// 2. projectId should be present
		inputValidationService.isValueExists("Project Id", projectId);
		
		// 3. pipelineId should be present
		inputValidationService.isValueExists("Pipeline Id", pipelineId);
		
		// 4. Check if authenticated user is the owner of the Pipeline and PipeLineExists or not. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		
		// 5. pipeline should not be archived
		pipeLineService.isPipelineArchived(pipelineId);
		
		// 6. check if pipeline is associated to a project.
		pipeLineService.isPipelineAssociatedUnderProject(projectId,pipelineId);
		
		// 7. Call Nifi Server to start an instance of the Pipeline Server for the user
		//result = pipeLineService.launchPipeline(authenticatedUserId, projectId, pipelineId);
		logger.debug("launchPipelineUnderProject() End");

		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	/**
	 * Launch the Pipeline. 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param pipelineId
	 * 		the pipelineId
	 * @return
	 * 		returns Pipeline instance
	 */
	@ApiOperation(value = "Launch the new instance of Pipeline based on type and return the Pipeline instance with URL.")
	@RequestMapping(value = "/users/{authenticatedUserId}/pipelines/{pipelineId}/launch", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> launchPipeline(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId) {

		logger.debug("launchPipeline() Begin");
		Pipeline result = new Pipeline();
		// 1. Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);
		
		// 2. pipelineId should be present
		inputValidationService.isValueExists("Pipeline Id", pipelineId);
		
		// 3. Check if authenticated user is the owner of the Pipeline and PipeLineExists or not. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		
		// 4. pipeline should not be archived
		pipeLineService.isPipelineArchived(pipelineId);
		
		// 5. Call Nifi Server to start an instance of the Pipeline Server for the user
		//result = pipeLineService.launchPipeline(authenticatedUserId, null, pipelineId);
		
		logger.debug("launchPipeline() End");
		
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Gets list of Pipelines associated to a project")
	@RequestMapping(value = "users/{authenticatedUserId}/projects/{projectId}/pipelines/", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPipelines(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId", required = true) @PathVariable("projectId") String projectId) {

		logger.debug("getPipelines() Begin");
		// 1. validate the Input data

		// Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);

		// project Id should be present
		inputValidationService.isValueExists("Project Id", projectId);
		// validate the project
		pipeLineValidationService.validateProject(authenticatedUserId, projectId);

		// Service call to get existing project (active and archive both). (Call to CDS)
		List<Pipeline> result = pipeLineService.getPipelines(authenticatedUserId, projectId);
		logger.debug("getPipelines() End");
		return new ResponseEntity<List<Pipeline>>(result, HttpStatus.OK);

	}
	
	/**
	 * Returns all Notebooks accessible to the user 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @return ResponseEntity<List<Notebook>>
	 * 		returns list of Notebook objects.
	 * 
	 */
	
	@ApiOperation(value = "Gets the list of all Independent Pipelines for a User")
	@RequestMapping(value = "users/{authenticatedUserId}/pipelines/", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getAllPipelines(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId) {

		logger.debug("getAllPipelines() Begin");
		// 1. Validate the input

		// 2. Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);
		// 3. Validate is user authorized to request this operation

		// 4. Service call to get existing Notebooks (active and archive both).(Call to CDS)
		List<Pipeline> result = pipeLineService.getPipelines(authenticatedUserId, null);
		logger.debug("getAllPipelines() End");
		return new ResponseEntity<List<Pipeline>>(result, HttpStatus.OK);

	}
	
	/**
	 * To update existing Pipeline associated to a Project
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param projectId
	 * 		the project Id with which pipeline is associated. 
	 * @param pipelineId
	 * 		the pipelineId to be updated
	 * @param pipeLine
	 * 		the Pipeline details to be updated : name, type, version and description 
	 * @return ResponseEntity<Pipeline>
	 * 		returns Pipeline object with addition and status details. 
	 */
	@ApiOperation(value = "Update the existing Pipeline associated to a Project")
	@RequestMapping(value = "users/{authenticatedUserId}/projects/{projectId}/pipelines/{pipelineId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updatePipelineUnderProject(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {
		logger.debug("updatePipelineUnderProject() Begin");
		Pipeline result = null;

		inputValidationService.isValueExists("projectId", projectId);
		// Validation
		pipeLineValidationService.validateInputData(authenticatedUserId, pipeLine);

		// Check if authenticated user is the owner of the Pipeline. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		
		// validate project
		pipeLineValidationService.validateProject(authenticatedUserId, projectId);

		result = pipeLineService.updatePipeline(authenticatedUserId, projectId, pipelineId, pipeLine);
		logger.debug("updatePipelineUnderProject() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	/**
	 * To update existing Pipeline 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param pipelineId
	 * 		the pipeline Id to be updated
	 * @param pipeLine
	 * 		the Pipeline details to be updated : name, type, version and description 
	 * @return ResponseEntity<Pipeline>
	 * 		returns Pipeline object with addition and status details. 
	 */
	@ApiOperation(value = "Update the existing Independent Pipeline ")
	@RequestMapping(value = "users/{authenticatedUserId}/pipelines/{pipelineId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updatePipeline(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {
		logger.debug("updatePipeline() Begin");

		Pipeline result = null;
		// Validation
		pipeLineValidationService.validateInputData(authenticatedUserId, pipeLine);
		// Check if authenticated user is the owner of the Notebook. (Call toCDS)

		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);

		result = pipeLineService.updatePipeline(authenticatedUserId, null, pipelineId, pipeLine);

		logger.debug("updatePipeline() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	/**
	 * To get the pipeline details 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated user id. 
	 * @param pipelineId
	 * 		the Pipeline id 
	 * @return ResponseEntity<Pipeline> 
	 * 		returns Pipeline object with all the required details. 
	 * 
	 */
	
	@ApiOperation(value = "Gets the existing Pipeline details for a User for the specific pipelineId")
	@RequestMapping(value = "/users/{authenticatedUserId}/pipelines/{pipelineId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPipeline(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId) {
		logger.debug("getPipeline() Begin");

		// 1. Validate the input
		// 2. Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);
		// 3. Pipeline Id should be present
		inputValidationService.isValueExists("Pipeline Id", pipelineId);
		// 4. Check if authenticated user is the owner of the Pipeline. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		// 5. Service call to get existing project (Call to CDS)
		Pipeline result = pipeLineService.getPipeline(authenticatedUserId, pipelineId);

		logger.debug("getPipeline() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);
	}
	
	/**
	 * To delete the Pipeline 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id.
	 * @param pipelineId
	 * 		the Pipeline Id of Pipeline to be deleted. 
	 * 
	 * @return ResponseEntity<ServiceState> 
	 * 		returns ServiceStat indicating Notebook is deleted successfully. 
	 */
	@ApiOperation(value = "Delete Pipeline")
	@RequestMapping(value = "/users/{authenticatedUserId}/pipeliens/{pipelineId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePipeLine(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId) {
		logger.debug("deletePipeLine() Begin");
		ServiceState result = null;
		// 1. Validate the input
		// 2. Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);
		// 3. Check if the user is the owner of the Pipeline or has the permission to archive the Pipeline.(call to CDS).
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		// 4. Delete Notebook
		result = pipeLineService.deletePipeline(pipelineId);
		logger.debug("deletePipeLine() End");
		return new ResponseEntity<ServiceState>(result, HttpStatus.OK);

	}
	
	/**
	 * Archive/Un Archive Pipeline 
	 * @param authenticatedUserId
	 * 		Authenticated UserId i.e., login Id.
	 * 
	 * @param projectId
	 * 		 the project Id 
	 * @param pipelineId
	 * 		Pipeline Id to be archived
	 * 
	 * @return
	 * 		returns the Pipeline Object with additional details indication project is been archived and status = 200.
	 */
	@ApiOperation(value = "Archive/UnArchive Pipeline Under Project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/pipelines/{pipelineId}/{actionType}", method = RequestMethod.PUT)
	public ResponseEntity<?> archivePipelineUnderProject(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value= "projectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "actionType", allowableValues = "A,UA", required = true) @PathVariable("actionType") String actionType) {
		logger.debug("archivePipelineUnderProject() Begin");

		Pipeline result = null;
		// Archive the pipeline
		result = archivePipeline(authenticatedUserId, null, pipelineId, actionType);

		logger.debug("archivePipelineUnderProject() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Archive/UnArchive Independent Pipeline")
	@RequestMapping(value = "/users/{authenticatedUserId}/pipelines/{pipelineId}/{actionType}", method = RequestMethod.PUT)
	public ResponseEntity<?> archivePipeline(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "actionType", allowableValues = "A,UA", required = true) @PathVariable("actionType") String actionType) {
		logger.debug("archivePipelineUnderProject() Begin");

		Pipeline result = null;
		// Archive the pipeline
		result = archivePipeline(authenticatedUserId, null, pipelineId, actionType);

		logger.debug("archivePipelineUnderProject() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	private Pipeline archivePipeline(String authenticatedUserId, String projectId, String pipelineId,
			String actionType) {
		logger.debug("archivePipeline() Begin");
		Pipeline result;
		// 1. Validate the input
		// Check authenticatedUserId should be present
		inputValidationService.isValueExists("AuthenticatedUserId", authenticatedUserId);
		// Check projectId is present
		if (null != projectId) {
			inputValidationService.isValueExists("Project Id", projectId);
			// Check if the project exists or not and archived or not
			pipeLineValidationService.validateProject(authenticatedUserId, projectId);
		}
		// Check pipelineId is present
		inputValidationService.isValueExists("Pipeline Id", pipelineId);
		// Check if the user is the owner of the pipeline or has the permission
		// to archive/un archive the project.(call to CDS).
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);

		// TODO : Check if the Pipeline is referenced by other Users or in Other Projects(Out of Boreas). Need to discuss how to get this done.
		// 4. Mark the pipeline as archived/un archived (call to CDS).
		result = pipeLineService.archivePipeline(authenticatedUserId, projectId, pipelineId, actionType);
		logger.debug("archivePipeline() End");
		return result;
	}

	private Pipeline createPipeline(String authenticatedUserId, String projectId, Pipeline pipeLine) {
		logger.debug("createPipeline() Begin");
		// 1. Validation for the Input Data
		pipeLineValidationService.validateInputData(authenticatedUserId, pipeLine);
		if (null != projectId) {
			// 2. Check if the project exists or not and project is archived or not.
			pipeLineValidationService.validateProject(authenticatedUserId, projectId);
		}
		// 3. Check if the pipeline Name and Version already present for the userId in Workspace. (Call to CDS)
		pipeLineService.pipeLineExists(authenticatedUserId, projectId, pipeLine);

		// 4. Service call to create new pipeline (Call to CDS)
		Pipeline result = pipeLineService.createPipeLine(authenticatedUserId, projectId, pipeLine);
		logger.debug("createPipeline() End");
		return result;
	}

}
