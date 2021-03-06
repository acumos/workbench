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

package org.acumos.workbench.predictorservice.controller;

import static org.acumos.workbench.common.security.SecurityConstants.AUTHORIZATION_HEADER_KEY;
import static org.acumos.workbench.common.security.SecurityConstants.JWT_TOKEN_HEADER_KEY;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.vo.Predictor;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.predictorservice.lightcouch.DataSetPredictor;
import org.acumos.workbench.predictorservice.lightcouch.PredictorProjectAssociation;
import org.acumos.workbench.predictorservice.service.InputValidationService;
import org.acumos.workbench.predictorservice.service.PredictorProjectAssociationService;
import org.acumos.workbench.predictorservice.service.PredictorValidation;
import org.acumos.workbench.predictorservice.utils.PredictorServiceConstants;
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
public class PredictorProjectAssociationController {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationService inputValidationService;
	
	@Autowired
	@Qualifier("PredictorValidationImpl")
	private PredictorValidation predictorValidation;
	
	@Autowired
	@Qualifier("PredictorProjectAssociationServiceImpl")
	private PredictorProjectAssociationService predProjAssociationService;
	
	@ApiOperation(value = "Associate Predictor to a Project (Create Association)")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/predictors", method = RequestMethod.POST)
	public ResponseEntity<?> associatePredictorToProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Project Id", required = true) @PathVariable("projectId") String projectId,
			@RequestBody(required = true) DataSetPredictor predictorProjAssociation) {
		
		logger.debug("associatePredictorToProject() Begin");
		
		
		// Check all the mandatory fields are present in the request i.e Acumos User login Id, Predictor Id, Project Id
		inputValidationService.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationService.isValuePresent(PredictorServiceConstants.PROJECTID, projectId);
		
		// Validate the Json Structure for the input data and check the missing fields
		inputValidationService.validateInputData(predictorProjAssociation);
		//Existence Validations 1. Check if Authenticated User Id exists
		predProjAssociationService.getUserDetails(authenticatedUserId);
		String authToken = getAuthJWTToken(request);
		// Check if Project for input ProjectId exists (i.e., status Active)
		predictorValidation.validateProject(authenticatedUserId, projectId, authToken);
		// Check if predictor is already exists with same name.
		predictorValidation.isPredictorExists(authenticatedUserId, predictorProjAssociation.getPredictorName());
		
		// Check if Predictor for input PredictorId exists (with status Active)
		// Check if logged in user has access to the input Predictor
		// Need to call Predictor Manager Service
		if(null != predictorProjAssociation.getPredictorId()) {
			predictorValidation.predictorExists(authenticatedUserId,predictorProjAssociation.getPredictorId());
		} 		
		// Access Validation  : Check if logged in user has access to the input ProjectId
		// Restriction : Check if model of the selected predictor is associated to a Project or not
		// Before Associating the Project to Predictor need to check the input model is already associated to any project or not
		predictorValidation.isModelAssociatedToProject(authenticatedUserId,projectId,predictorProjAssociation.getSolutionId(),predictorProjAssociation.getRevisionId(),authToken);
		
		// Predictor deployment status should be Active.  
		//User should not be allowed to associate Predictor to a Project if predictor deployment status is other than Active.
		Predictor result = predProjAssociationService.associatePredictorToProject(authenticatedUserId,projectId,predictorProjAssociation);
		logger.debug("associatePredictorToProject() Ends");
		return new ResponseEntity<Predictor>(result, HttpStatus.OK);	
	}
	
	
	@ApiOperation(value = "Get Predictor Details for given input Model")
	@RequestMapping(value = "/users/{authenticatedUserId}/models/{modelId}/version/{version}", method = RequestMethod.GET)
	public ResponseEntity<?> getPredictorDetails (HttpServletRequest request,
			@ApiParam(value = "Acumos Login ID", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Model Id", required = true) @PathVariable("modelId") String modelId,
			@ApiParam(value = "Model Version", required = true) @PathVariable("version") String version) {
		logger.debug("getPredictorDetails() Begin");
		// Input field Validation
		inputValidationService.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationService.isValuePresent(PredictorServiceConstants.MODELID, modelId);
		inputValidationService.isValuePresent(PredictorServiceConstants.VERSION, version);
		
		//Existence Validations 1. Check if Authenticated User Id exists
		predProjAssociationService.getUserDetails(authenticatedUserId);
		// Check if the input model and version exists in CDS
		String modelRevisionId = predictorValidation.modelExists(modelId,version);
		
		//Get the predictor Details in Couch DB, if not found anything return empty
		Predictor result = predProjAssociationService.getPredictor(authenticatedUserId,modelId,version,modelRevisionId);
		
		logger.debug("getPredictorDetails() End");
		return new ResponseEntity<Predictor>(result, HttpStatus.OK);	

	}
	
	
	@ApiOperation(value = "Get Predictors Associated to a Project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/predictors", method = RequestMethod.GET)
	public ResponseEntity<?> getPredictorsAssociatedToProject(HttpServletRequest request,
			@ApiParam(value = "Acumos Login ID", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Project Id", required = true) @PathVariable("projectId") String projectId) {
		logger.debug("getPredictorsAssociatedToProject() Begin");
		// Check all the mandatory fields are present in the request i.e Acumos User login Id, Project Id
		inputValidationService.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationService.isValuePresent(PredictorServiceConstants.PROJECTID, projectId);
		
		//Existence Validations 1. Check if Authenticated User Id exists
		predProjAssociationService.getUserDetails(authenticatedUserId);
		
		String authToken = getAuthJWTToken(request);
		// Check if Project for input ProjectId exists (i.e., status Active)
		// Check if logged in user has access to the input ProjectId.  For now check if user is owner of the Project.
		predictorValidation.validateProject(authenticatedUserId, projectId, authToken);
		
		List<Predictor> result = predProjAssociationService.getPredictors(authenticatedUserId,projectId);
		
		logger.debug("getPredictorsAssociatedToProject() End");
		return new ResponseEntity<List<Predictor>>(result, HttpStatus.OK);

	}
	
	
	@ApiOperation(value = "Update Predictor Project Association")
	@RequestMapping(value = "/users/{authenticatedUserId}/predictors/{predictorId}/associations/{associationId}", method = RequestMethod.PUT)
	public ResponseEntity<?> modifyPredictorAssociationToProject(HttpServletRequest request,
			@ApiParam(value = "Acumos Login ID", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Predictor ID", required = true) @PathVariable("predictorId") String predictorId,
			@ApiParam(value = "Predictor Project Association Id", required = true) @PathVariable("associationId") String associationId,
			@RequestBody(required = true) DataSetPredictor associationData) {
		logger.debug("editPredictorAssociationToProject() Begin");
		
		// Check all the mandatory fields are present in the request i.e Acumos User login Id, Predictor Id, Project Id
		inputValidationService.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationService.isValuePresent(PredictorServiceConstants.PREDICTORID, predictorId);
		inputValidationService.isValuePresent(PredictorServiceConstants.ASSOCIATIONID, associationId);
		
		// Validate the Json Structure for the input data and check the missing fields
		inputValidationService.validateInputData(associationData);
		
		//Existence Validations 1. Check if Authenticated User Id exists
		predProjAssociationService.getUserDetails(authenticatedUserId);
		
		String authToken = getAuthJWTToken(request);
		
		// Check if Project for input ProjectId exists (i.e., status Active)
		// Check if logged in user has access to the input ProjectId.  For now check if user is owner of the Project.
		predictorValidation.validateProject(authenticatedUserId, associationData.getProjectId(), authToken);
		
		// Check if Predictor for input PredictorId exists (with status Active)
		// Check if logged in user has access to the input Predictor :  Either user has to be owner or collaborator of the Predictor's model. <Need to check further>
		// Predictor deployment status should be Active.  User should not be allowed to associate Predictor to a Project if predictor deployment status is other than Active.
		predictorValidation.predictorExists(authenticatedUserId,predictorId);
		
		// Restriction : Check if model of the selected predictor is associate to a Project
		predictorValidation.isModelAssociatedToProject(authenticatedUserId,associationData.getProjectId(),associationData.getSolutionId(),associationData.getRevisionId(),authToken);
		
		Predictor result = predProjAssociationService.editPredictorProjectAssociation(authenticatedUserId,associationId, associationData,predictorId);

		logger.debug("editPredictorAssociationToProject() End");
		return new ResponseEntity<Predictor>(result, HttpStatus.OK);
	}

	@ApiOperation(value = "Delete Predictor Association")
	@RequestMapping(value = "/users/{authenticatedUserId}/predictors/associations/{associationId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePredictorAssociation(HttpServletRequest request,
			@ApiParam(value = "Acumos Login ID", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Predictor Project Association Id", required = true) @PathVariable("associationId") String associationId) {
		logger.debug("deletePredictorAssociation() Begin");
		
		// Input Validation
		inputValidationService.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationService.isValuePresent(PredictorServiceConstants.ASSOCIATIONID, associationId);
		
		// Existence Validation : 1. Check if authenticated User exists 
		predProjAssociationService.getUserDetails(authenticatedUserId);
		
		// 2.	Check if PredictorProjectAssociation for input AsscoaitionId exists (covered in serviceImpl)
		
		// Access Validation : Check if logged in user has access to the input AssociationId.  For now check if user is owner of the Association
		
		ServiceState result = predProjAssociationService.deleteAssociation(authenticatedUserId, associationId);
		
		logger.debug("deletePredictorAssociation() Begin");
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
