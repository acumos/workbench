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

package org.acumos.workbench.modelservice.controller;

import static org.acumos.workbench.common.security.SecurityConstants.AUTHORIZATION_HEADER_KEY;
import static org.acumos.workbench.common.security.SecurityConstants.JWT_TOKEN_HEADER_KEY;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.modelservice.service.InputValidationService;
import org.acumos.workbench.modelservice.service.ModelService;
import org.acumos.workbench.modelservice.service.ModelValidationService;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
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
@RequestMapping(value = "/")
public class ModelServiceController {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationService;
	
	@Autowired
	@Qualifier("ModelValidationServiceImpl")
	private ModelValidationService modelValidationService;
	
	@Autowired
	@Qualifier("ModelServiceImpl")
	private ModelService modelService;
	
	@ApiOperation(value = "List out all the Models that belongs to user")
	@RequestMapping(value = "/users/{authenticatedUserId}/models/", method = RequestMethod.GET)
	public ResponseEntity<?> listModels(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId) {
		logger.debug("listModels() Begin");
		//1.  Check the Authenticated User Id is present or not
		inputValidationService.isValuePresent(ModelServiceConstants.MODEL_AUTHENTICATED_USER_ID, authenticatedUserId);
		//2.  Service call to get existing Models (active and archive both). (Call to CDS)
		List<Model> result = modelService.getModels(authenticatedUserId, null);
		logger.debug("listModels() End");
		return new ResponseEntity<List<Model>>(result, HttpStatus.OK);
		
	}
	
	@ApiOperation(value = "List out all the Models that belongs to User under the Project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/models/", method = RequestMethod.GET)
	public ResponseEntity<?> listModelsAssociatedToProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId) {
		logger.debug("listModelsUnderProject() Begin");
		String authToken = getAuthJWTToken(request);
		// 1. Check the Authenticated User Id is present or not
		inputValidationService.isValuePresent(ModelServiceConstants.MODEL_AUTHENTICATED_USER_ID, authenticatedUserId);
		
		//check if project id is present
		inputValidationService.isValuePresent(ModelServiceConstants.PROJECT_ID, projectId);
		// Validate the Project, and check the user can access the project or not.
		modelValidationService.validateProject(authenticatedUserId, projectId, authToken);
		
		List<Model> result = modelService.getModels(authenticatedUserId, projectId);
		return new ResponseEntity<List<Model>>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Associate's the Model to the Project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/models/{modelId}", method = RequestMethod.POST)
	public ResponseEntity<?> associateModeltoProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "projectId",required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "modelId",required = true) @PathVariable("modelId") String modelId,
			@RequestBody(required = true) Model model) {
		logger.debug("associateModeltoProject() Begin");
		// The Model Service must check if the request JSON structure is valid or not and Check for the User Id Missing or not
		modelValidationService.validateInputData(model);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not
		// Check if the projectId and Version already exists or not by calling the Project Service.
		// Check if the Project is Archived or not
		// Get the Authentication Token from the HttpHeaders
		String authToken = getAuthJWTToken(request);
		modelValidationService.validateProject(authenticatedUserId, projectId, authToken);
		// Check if the SolutionId and the version already exists in CDS
		// Insert Project Model Association by calling LightCouch DB
		Model result = modelService.insertProjectModelAssociation(authenticatedUserId,projectId,modelId,model);
		logger.debug("associateModeltoProject() End");
        return new ResponseEntity<Model>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Update the Model Association with Project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/models/{modelId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateModelAssociationWithProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id",required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId",required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "modelId",required = true) @PathVariable("modelId") String modelId,
			@RequestBody(required = true) Model model) {
		logger.debug("updateModelAssociationWithProject() Begin");
		String authToken = getAuthJWTToken(request);
		// The Model Service must check if the request JSON structure is valid or not
		modelValidationService.validateInputData(model);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not
		modelService.isUserAccessibleProject(authenticatedUserId, projectId);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the model or not
		modelService.userAccessableModel(authenticatedUserId, modelId);
		// Update the Association between the Project and Model
		// Model Service must call the CDS to update the project model association
		Model result = modelService.updateProjectModelAssociation(authenticatedUserId,projectId,modelId,model);
		logger.debug("updateModelAssociationWithProject() End");
		return new ResponseEntity<Model>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Delete the Model Association with Project in ML Workbench")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/models/{modelId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteModelAssociationWithProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id",required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId",required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "modelId",required = true) @PathVariable("modelId") String modelId,
			@RequestBody(required = true) Model model){
		logger.debug("deleteModelAssociationWithProject() Begin");
		String authToken = getAuthJWTToken(request);
		// The Model Service must check if the request JSON structure is valid or not
		modelValidationService.validateInputData(model);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not
		modelService.isUserAccessibleProject(authenticatedUserId, projectId);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the model or not
		modelService.userAccessableModel(authenticatedUserId, modelId);
		// Delete the Association between project and model
		// Model Service must call the CDS to delete project model association
		ServiceState result = modelService.deleteProjectModelAssociation(authenticatedUserId,projectId,modelId,model);
		logger.debug("deleteModelAssociationWithProject() End");
		return new ResponseEntity<ServiceState>(result, HttpStatus.OK);
		
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