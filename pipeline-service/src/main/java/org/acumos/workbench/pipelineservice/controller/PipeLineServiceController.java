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

import static org.acumos.workbench.common.security.SecurityConstants.AUTHORIZATION_HEADER_KEY;
import static org.acumos.workbench.common.security.SecurityConstants.JWT_TOKEN_HEADER_KEY;
import static org.acumos.workbench.pipelineservice.util.PipelineServiceConstants.FIELD_AUTHENTICATED_USER_ID;
import static org.acumos.workbench.pipelineservice.util.PipelineServiceConstants.FIELD_PIPELINE_ID;
import static org.acumos.workbench.pipelineservice.util.PipelineServiceConstants.FIELD_PROJECT_ID;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.pipelineservice.service.InputValidationService;
import org.acumos.workbench.pipelineservice.service.PipeLineCacheService;
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

@RestController
@RequestMapping(value = "/")
public class PipeLineServiceController {

	private static final String PIPELINE_INPROGRESS = "IP";

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
	
	@Autowired
	private PipeLineCacheService pipelineCacheService;
	
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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {

		logger.debug("createIndependentPipeline() Begin");

		Pipeline result = createPipeline(authenticatedUserId, null, pipeLine, request);
		String serviceStatus = result.getServiceStatus().getStatus().getServiceStatusCode();
		logger.debug("createIndependentPipeline() End");
		
		if(serviceStatus.equals(PIPELINE_INPROGRESS)){
			return new ResponseEntity<Pipeline>(result, HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<Pipeline>(result, HttpStatus.CREATED);
		}
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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {

		logger.debug("createPipelineUnderProject() Begin");
	
		Pipeline result = createPipeline(authenticatedUserId, projectId, pipeLine, request);

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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId) {

		logger.debug("launchPipelineUnderProject() Begin");
		String authToken = getAuthJWTToken(request);
		Pipeline result = new Pipeline();
		// 1. Check authenticatedUserId should be present
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		
		// 2. projectId should be present
		inputValidationService.isValueExists(FIELD_PROJECT_ID, projectId);
		// validate the project
		pipeLineValidationService.validateProject(authenticatedUserId, projectId, authToken);
		
		// 3. pipelineId should be present
		inputValidationService.isValueExists(FIELD_PIPELINE_ID, pipelineId);
		
		// 4. Check if authenticated user is the owner of the Pipeline and PipeLineExists or not. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		
		// 5. pipeline should not be archived
		pipeLineService.isPipelineArchived(pipelineId);
		
		// 6. check if pipeline is associated to a project.
		pipeLineService.isPipelineAssociatedUnderProject(projectId,pipelineId);
		
		// 7. Call Nifi Server to start an instance of the Pipeline Server for the user
		result = pipeLineService.launchPipeline(authenticatedUserId, projectId, pipelineId);
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
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		
		// 2. pipelineId should be present
		inputValidationService.isValueExists(FIELD_PIPELINE_ID, pipelineId);
		
		// 3. Check if authenticated user is the owner of the Pipeline and PipeLineExists or not. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		
		// 4. pipeline should not be archived
		pipeLineService.isPipelineArchived(pipelineId);
		
		// 5. Call Nifi Server to start an instance of the Pipeline Server for the user
		result = pipeLineService.launchPipeline(authenticatedUserId, null, pipelineId);
		
		logger.debug("launchPipeline() End");
		
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Gets list of Pipelines associated to a project")
	@RequestMapping(value = "users/{authenticatedUserId}/projects/{projectId}/pipelines/", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPipelines(
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId", required = true) @PathVariable("projectId") String projectId) {

		logger.debug("getPipelines() Begin");
		// 1. validate the Input data
		String authToken = getAuthJWTToken(request);
		// Check authenticatedUserId should be present
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);

		// project Id should be present
		inputValidationService.isValueExists(FIELD_PROJECT_ID, projectId);
		// validate the project
		pipeLineValidationService.validateProject(authenticatedUserId, projectId, authToken);

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
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {
		logger.debug("updatePipelineUnderProject() Begin");
		Pipeline result = null;
		String requestId = defaultToUUID(request.getHeader(LoggingConstants.Headers.REQUEST_ID));
		pipelineCacheService.putUpdateRequest(requestId, pipeLine);
		
		String authToken = getAuthJWTToken(request);
		inputValidationService.isValueExists("projectId", projectId);
		// validate project
		pipeLineValidationService.validateProject(authenticatedUserId, projectId, authToken);
		
		// Validation
		pipeLineValidationService.validateInputData(authenticatedUserId, pipeLine);

		// Check if authenticated user is the owner of the Pipeline. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		result = pipeLineService.updatePipeline(authenticatedUserId, projectId, pipelineId, pipeLine);
		pipelineCacheService.removeUpdateRequest(requestId, pipeLine);
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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "Pipeline Details", required = true) @RequestBody Pipeline pipeLine) {
		logger.debug("updatePipeline() Begin");
		String requestId = defaultToUUID(request.getHeader(LoggingConstants.Headers.REQUEST_ID));
		pipelineCacheService.putUpdateRequest(requestId, pipeLine);
		
		Pipeline result = null;
		// Validation
		pipeLineValidationService.validateInputData(authenticatedUserId, pipeLine);
		// Check if authenticated user is the owner of the Notebook. (Call toCDS)

		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);

		result = pipeLineService.updatePipeline(authenticatedUserId, null, pipelineId, pipeLine);
		pipelineCacheService.removeUpdateRequest(requestId, pipeLine);
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
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		// 3. Pipeline Id should be present
		inputValidationService.isValueExists(FIELD_PIPELINE_ID, pipelineId);
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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId) {
		logger.debug("deletePipeLine() Begin");
		String requestId = defaultToUUID(request.getHeader(LoggingConstants.Headers.REQUEST_ID));
		pipelineCacheService.putDeleteRequest(requestId, pipelineId);
		ServiceState result = null;
		// 1. Validate the input
		// 2. Check authenticatedUserId should be present
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		// 3. Check if the user is the owner of the Pipeline or has the permission to archive the Pipeline.(call to CDS).
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		// 4. Delete Notebook
		result = pipeLineService.deletePipeline(authenticatedUserId,pipelineId);
		pipelineCacheService.removeDeleteRequest(requestId, pipelineId);
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
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value= "projectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "actionType", allowableValues = "A,UA", required = true) @PathVariable("actionType") String actionType) {
		logger.debug("archivePipelineUnderProject() Begin");
		
		Pipeline result = null;
		// Archive the pipeline
		result = archivePipeline(authenticatedUserId, null, pipelineId, actionType, request);

		logger.debug("archivePipelineUnderProject() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	/**
	 * Archive or UnArchive Independent Pipeline
	 * @param request
	 * 			Accepts HttpServletRequest
	 * @param authenticatedUserId
	 * 			Accepts authenticatedUserId
	 * @param pipelineId
	 * 			Accepts pipelineId
	 * @param actionType
	 * 			Accepts actionType
	 * @return
	 * 			ResponseEntity<Pipeline>
	 */
	@ApiOperation(value = "Archive/UnArchive Independent Pipeline")
	@RequestMapping(value = "/users/{authenticatedUserId}/pipelines/{pipelineId}/{actionType}", method = RequestMethod.PUT)
	public ResponseEntity<?> archivePipeline(
			HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "pipelineId", required = true) @PathVariable("pipelineId") String pipelineId,
			@ApiParam(value = "actionType", allowableValues = "A,UA", required = true) @PathVariable("actionType") String actionType) {
		logger.debug("archivePipelineUnderProject() Begin");

		Pipeline result = null;
		// Archive the pipeline
		result = archivePipeline(authenticatedUserId, null, pipelineId, actionType, request);

		logger.debug("archivePipelineUnderProject() End");
		return new ResponseEntity<Pipeline>(result, HttpStatus.OK);

	}
	
	/**
	 * Check the Pipeline Creation Status With Project
	 * @param request
	 * 			Accepts HttpServletRequest
	 * @param authenticatedUserId
	 * 			Accepts authenticatedUserId
	 * @param projectId
	 * 			Accepts projectId
	 * @param pipelineId
	 * 			Accepts pipelineId
	 * @return
	 * 			Returns ResponseEntity<Pipeline>
	 */
	@ApiOperation(value = "Check Pipeline Creation Status which Associated with Project")
	@RequestMapping(value = "users/{authenticatedUserId}/projects/{projectId}/pipelines/{pipelineId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> checkPipelineCreationStatusWithProject(HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "The Project Id", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "The Pipeline Id", required = true) @PathVariable("pipelineId") String pipelineId) {

		logger.debug("checkPipelineCreationStatusWithProject() Begin");
		String authToken = getAuthJWTToken(request);
		Pipeline result = checkPipelineCreationStatus(authenticatedUserId, projectId, authToken, pipelineId);
		logger.debug("checkPipelineCreationStatusWithProject() End");
		return new ResponseEntity<Pipeline>(result,
				getServiceStatus(result.getServiceStatus().getStatus().getServiceStatusCode()));

	}
	
	/**
	 * Checks the Independent Pipeline Creation Status
	 * @param request
	 * 			Accepts HttpServletRequest
	 * @param authenticatedUserId
	 * 			Accepts authenticatedUserId
	 * @param pipelineId
	 * 			Accepts pipelineId
	 * @return
	 * 			returns ResponseEntity<Pipeline>
	 */
	@ApiOperation(value = "Check Independent Pipeline Creation Status")
	@RequestMapping(value = "users/{authenticatedUserId}/pipelines/{pipelineId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> checkIndependentPipelineCreationStatus(HttpServletRequest request,
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "The Pipeline Id", required = true) @PathVariable("pipelineId") String pipelineId) {

		logger.debug("checkIndependentPipelineCreationStatus() Begin");
		String authToken = getAuthJWTToken(request);
		Pipeline result = checkPipelineCreationStatus(authenticatedUserId, null, authToken, pipelineId);
		logger.debug("checkIndependentPipelineCreationStatus() End");
		return new ResponseEntity<Pipeline>(result,
				getServiceStatus(result.getServiceStatus().getStatus().getServiceStatusCode()));
	}
	
	private Pipeline checkPipelineCreationStatus(String authenticatedUserId, String projectId, String authToken,
			String pipelineId) {
		logger.debug("checkPipelineCreationStatus() Begin");
		// 1. Check authenticatedUserId should be present
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		// 2. pipelineId should be present
		inputValidationService.isValueExists(FIELD_PIPELINE_ID, pipelineId);
		// 3. Check if authenticated user is the owner of the Pipeline and PipeLineExists or not. (Call to CDS)
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);
		// 4. pipeline should not be archived
		pipeLineService.isPipelineArchived(pipelineId);
		if (null != projectId) {
			// 5. Check if the project exists or not and project is archived or not.
			pipeLineValidationService.validateProject(authenticatedUserId, projectId, authToken);
			// 6. check if pipeline is associated to a project.
			pipeLineService.isPipelineAssociatedUnderProject(projectId, pipelineId);
		}
		// 7. Service call to check PipeLine Creation Status (Call to CDS)
		Pipeline result = pipeLineService.getPipeline(authenticatedUserId, pipelineId);
		logger.debug("checkPipelineCreationStatus() End");
		return result;
	}

	private HttpStatus getServiceStatus(String serviceStatusCode) {
		logger.debug("getServiceStatus() Begin");
		HttpStatus httpStatus = null;
		ServiceStatus serviceStatus = ServiceStatus.get(serviceStatusCode);
		switch (serviceStatus) {
		case ACTIVE:
		case COMPLETED:
			httpStatus = HttpStatus.CREATED;
			break;
		case INPROGRESS:
			httpStatus = HttpStatus.OK;
			break;
		default:
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		logger.debug("getServiceStatus() End");
		return httpStatus;
	}
	
	private Pipeline archivePipeline(String authenticatedUserId, String projectId, String pipelineId,
			String actionType, HttpServletRequest request) {
		logger.debug("archivePipeline() Begin");
		Pipeline result;
		String requestId = defaultToUUID(request.getHeader(LoggingConstants.Headers.REQUEST_ID));
		pipelineCacheService.putArchiveRequest(requestId, pipelineId);
		
		// 1. Validate the input
		// Check authenticatedUserId should be present
		inputValidationService.isValueExists(FIELD_AUTHENTICATED_USER_ID, authenticatedUserId);
		// Check projectId is present
		if (null != projectId) {
			inputValidationService.isValueExists(FIELD_PROJECT_ID, projectId);
			// Check if the project exists or not and archived or not
			String authToken = getAuthJWTToken(request);
			pipeLineValidationService.validateProject(authenticatedUserId, projectId, authToken);
		}
		// Check pipelineId is present
		inputValidationService.isValueExists(FIELD_PIPELINE_ID, pipelineId);
		// Check if the user is the owner of the pipeline or has the permission
		// to archive/un archive the project.(call to CDS).
		pipeLineService.isOwnerOfPipeline(authenticatedUserId, pipelineId);

		// TODO : Check if the Pipeline is referenced by other Users or in Other Projects(Out of Boreas). Need to discuss how to get this done.
		// 4. Mark the pipeline as archived/un archived (call to CDS).
		result = pipeLineService.archivePipeline(authenticatedUserId, projectId, pipelineId, actionType);
		pipelineCacheService.removeArchiveRequest(requestId, pipelineId);
		logger.debug("archivePipeline() End");
		return result;
	}

	private Pipeline createPipeline(String authenticatedUserId, String projectId, Pipeline pipeLine, HttpServletRequest request) {
		logger.debug("createPipeline() Begin");

		String requestId = defaultToUUID(request.getHeader(LoggingConstants.Headers.REQUEST_ID));
		pipelineCacheService.putCreateRequest(requestId, pipeLine);
		
		// 1. Validation for the Input Data
		pipeLineValidationService.validateInputData(authenticatedUserId, pipeLine);
		if (null != projectId) {
			// 2. Check if the project exists or not and project is archived or not.
			String authToken = getAuthJWTToken(request);
			pipeLineValidationService.validateProject(authenticatedUserId, projectId, authToken);
		}
		// 3. Check if the pipeline Name a nd Version already present for the userId in Workspace. (Call to CDS)
		pipeLineService.pipeLineExists(authenticatedUserId, projectId, pipeLine);
		
		// 4. Service call to create new pipeline (Call to CDS)
		Pipeline result = pipeLineService.createPipeLine(authenticatedUserId, projectId, pipeLine);
		
		pipelineCacheService.removeCreateRequest(requestId, pipeLine);
		logger.debug("createPipeline() End");
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
	
	private String defaultToUUID(final String in) {
		if (in == null) {
			return UUID.randomUUID().toString();
		}
		return in;
	}
}
