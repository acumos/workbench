/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Predictor;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.predictorservice.deployClient.K8sConfig;
import org.acumos.workbench.predictorservice.service.InputValidationServiceImpl;
import org.acumos.workbench.predictorservice.service.PredictorServiceImpl;
import org.acumos.workbench.predictorservice.service.PredictorValidationImpl;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/")
public class PredictorManagerController {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("InputValidationServiceImpl")
	private InputValidationServiceImpl inputValidationServiceImpl;
	@Autowired
	@Qualifier("PredictorServiceImpl")
	private PredictorServiceImpl predictorServiceImpl;
	@Autowired
	@Qualifier("PredictorValidationImpl")
	private PredictorValidationImpl predictorValidationImpl;

	@ApiOperation(value = "Create Predictor for the User")
	@RequestMapping(value = "/users/{authenticatedUserId}/{projectId}/predictors", method = RequestMethod.POST)
	public ResponseEntity<?> createPredictor(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Project Id", required = true) @PathVariable("projectId") String projectId,
			@RequestBody(required = true) Predictor predictor) {

		logger.debug("createPredictor() Begin");
		
		String authToken = getAuthJWTToken(request);
		String revisionId = null; 
		String k8s_id = null;
		String predictorKey=null;
		List<KVPair> kvPairList = predictor.getModel().getModelId().getMetrics().getKv();
		for (KVPair kvPair : kvPairList) {
			switch (kvPair.getKey()) {
			case "K8S_ID":
				k8s_id = kvPair.getValue();
			case "REVISION_ID":
				revisionId = kvPair.getValue();
			case "PREDICTOR_KEY":
				predictorKey = kvPair.getValue();
			default:
				break;
			}
		}
		// Input Validation Check
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.SOLUTIONID, predictor.getModel().getModelId().getUuid());
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.REVISIONID, revisionId);
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.PREDICTORNAME, predictor.getPredictorId().getName());
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.VERSION, predictor.getPredictorId().getVersionId().getLabel());
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.K8S_ID, k8s_id);
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.MODELID, predictor.getModel().getModelId().getUuid());
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.PREDICTORKEY, predictorKey);
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.PROJECTID, projectId);
		
		predictorValidationImpl.isUserExists(authenticatedUserId);
		
		predictorValidationImpl.isSolutionRevisionExists(predictor.getModel().getModelId().getUuid(), revisionId);

		predictorValidationImpl.isPredictorExists(authenticatedUserId, predictor.getPredictorId().getName());
		
		predictorValidationImpl.isSolutionAccessible(authenticatedUserId, predictor.getModel().getModelId().getUuid());
		
		Predictor result = predictorServiceImpl.createPredictor(authenticatedUserId, predictor, revisionId, authToken, k8s_id,predictorKey,projectId);
		
		logger.debug("createPredictor() Ends");

		return new ResponseEntity<Predictor>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Get Predictor Deployment Status ")
	@RequestMapping(value = "/users/{authenticatedUserId}/predictors/{predictorId}", method = RequestMethod.GET)
	public ResponseEntity<?> getPredictordeploymentStatus(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "PredictorId", required = true) @PathVariable("predictorId") String predictorId) {
		
		logger.debug("getPredictordeploymentStatus() Begin");
		
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.PREDICTORID, predictorId);
		
		// check if user exists
		predictorValidationImpl.isUserExists(authenticatedUserId);
		
		predictorValidationImpl.isPredictorAccessibleToUser(authenticatedUserId,predictorId);
		
		Predictor result=predictorServiceImpl.getDeploymentStatus(authenticatedUserId, predictorId);

		logger.debug("getPredictordeploymentStatus() Ends");

		return new ResponseEntity<Predictor>(result, HttpStatus.OK);
	
	}
	
	@ApiOperation(value = "Get K8S site Config")
	@RequestMapping(value = "/users/{authenticatedUserId}/siteConfig", method = RequestMethod.GET)
	public ResponseEntity<?> getK8sSiteConfig(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId) {
		logger.debug("getK8sSiteConfig() Begin");
		
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		
		// check if user exists
		predictorValidationImpl.isUserExists(authenticatedUserId);
		
		List<K8sConfig> result=predictorServiceImpl.getK8sSiteConfig();

		logger.debug("getK8sSiteConfig() Ends");

		return new ResponseEntity<List<K8sConfig>>(result, HttpStatus.OK);
	
	}
	
	
	@ApiOperation(value = "Get the list of Predictor for a User")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects{projectId}/predictorList", method = RequestMethod.GET)
	public ResponseEntity<?> getPredictor(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Project Id", required = false) @RequestParam("projectId") String projectId) {
		logger.debug("getPredictor() Begin"); // check if user exists
		
		predictorValidationImpl.isUserExists(authenticatedUserId);
		
		if (null != projectId && !"".equals(projectId)) {
			predictorValidationImpl.isProjectExists(authenticatedUserId, projectId);
		}

		List<Predictor> result = predictorServiceImpl.getPredictorByUser(authenticatedUserId,projectId);

		logger.debug("getPredictor() Ends");

		return new ResponseEntity<List<Predictor>>(result, HttpStatus.OK);

	}
	 

	@ApiOperation(value = "Delete Predictor")
	@RequestMapping(value = "/users/{authenticatedUserId}/predictors/{predictorId}/project/{projectId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePredictor(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "PredictorId", required = true) @PathVariable("predictorId") String predictorId,
			@ApiParam(value = "ProjectId", required = false) @PathVariable("projectId") String projectId) {
		logger.debug("getPredictorDeployValidation() Begin");
		
		// input Validation check
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.AUTHENTICATEDUSERID, authenticatedUserId);
		inputValidationServiceImpl.isValuePresent(PredictorServiceConstants.PREDICTORID, predictorId);
	
		// check if user exists
		predictorValidationImpl.isUserExists(authenticatedUserId);
		// check if Predictor exists
		predictorValidationImpl.predictorExists(authenticatedUserId, predictorId);
		
		ServiceState result = predictorServiceImpl.deletePredictor(authenticatedUserId, predictorId, projectId);

		logger.debug("getPredictorDeployValidation() Ends");

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
