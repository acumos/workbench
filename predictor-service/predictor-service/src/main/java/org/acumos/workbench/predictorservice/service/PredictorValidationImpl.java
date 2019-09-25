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

package org.acumos.workbench.predictorservice.service;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.predictorservice.exception.AssociationException;
import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.lightcouch.PredictorProjectAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service("PredictorValidationImpl")
public class PredictorValidationImpl implements PredictorValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	private CouchDBService couchDBService;
	
	@Autowired
	private ProjectServiceRestClientImpl psClient;
	
	@Autowired
	private ModelServiceRestClientImpl msClient;
	
	@Override
	public void loginUserExists(String authenticatedUserId) {
		logger.debug("loginUserExists() Begin");
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("loginName", authenticatedUserId);
			queryParameters.put("active", true);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
			RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false, pageRequest);
			List<MLPUser> mlpUsers = response.getContent();
			if (null != mlpUsers && mlpUsers.size() > 0) {
				MLPUser mlpUser = mlpUsers.get(0);
			} else {
				logger.error("User not found");
				throw new UserNotFoundException(authenticatedUserId);
			}

		} catch (RestClientResponseException e) {
			logger.error("CDS - Get User Details");
			throw new TargetServiceInvocationException();
		}
		logger.debug("loginUserExists() End");
		
	}

	@Override
	public void validateProject(String authenticatedUserId, String projectId, String authToken) {
		logger.debug("validateProject() Begin");
		ResponseEntity<Project> response = psClient.getProject(authenticatedUserId, projectId, authToken);
		if(null != response) {
			Project project = response.getBody();
			if(null != project) { 
				if(!ServiceStatus.ERROR.equals(project.getServiceStatus().getStatus())) {
					if(ArtifactStatus.ARCHIVED.equals(project.getArtifactStatus().getStatus())){
						logger.error("Specified Project : " + projectId + " is archived");
						throw new ArchivedException("Update Not Allowed, Specified Project : " + projectId + " is archived");
					}
				} else {
					if(HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) { //Bad request 
						logger.error("Error Message : " + project.getServiceStatus().getStatusMessage());
						throw new ValueNotFoundException(project.getServiceStatus().getStatusMessage());
					} else if (HttpStatus.FORBIDDEN.equals(response.getStatusCode())) { //Not owner
						logger.error("User is not owner of the Project or authorized to access Project");
						throw new NotProjectOwnerException();
					}
				}
			} else {
				logger.error("Project doesn't exists");
				throw new ProjectNotFoundException();
			}
		} else { 
			logger.error("Project doesn't exists");
			throw new ProjectNotFoundException();
		}
		logger.debug("validateProject() End");
		
	}

	@Override
	public void predictorExists(String authenticatedUserId,String predictorId) {
		logger.debug("predictorExists() Begin");
		String userId = null;
		boolean hasAccess = false;
		List<PredictorProjectAssociation> associationList = couchDBService.predictorExists(predictorId);
		for(PredictorProjectAssociation association : associationList) {
			userId = association.getUserId();
			if(authenticatedUserId.equals(userId)) {
				hasAccess = true;
				break;
			}
		}
		if(!hasAccess) {
			logger.error("Predicotr is Not Accessible to logged in User");
			throw new PredictorException("Predicotr is Not Accessible to logged in User");
		}
		
		logger.debug("predictorExists() End");
	}

	@Override
	public void isModelAssociatedToProject(String authenticatedUserId, String projectId, String solutionId, String revisionId,String authToken) {
		logger.debug("isModelAssociatedToProject() Begin");
		// get the List of models for a particular project by calling Model Service API
		//URL :  /users/{authenticatedUserId}/projects/{projectId}/models/ List out all the Models that belongs to User under the Project
		boolean isAssociated = false;
		ResponseEntity<List<Model>> modelResponse = msClient.getModels(authenticatedUserId, projectId, authToken);
		List<Model> modelList = modelResponse.getBody();
		for(Model model : modelList) {
			if(model.getModelId().getUuid().equals(solutionId)) {
				List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
				for(KVPair pair :kvPairList) {
					if(pair.getKey().equals("REV_ID")) {
						pair.getValue().equals(revisionId);
						isAssociated = true;
					}
				}
			}
		}
		if(!isAssociated) {
			logger.error("Cannot Associate Predictor, as corresponding model is not associated to a Project");
			throw new AssociationException("Cannot Associate Predictor, as corresponding model is not associated to a Project");
		}
		couchDBService.modelProjectAssociationExistsInCouch(projectId,solutionId,revisionId);
		logger.debug("isModelAssociatedToProject() End");
	}


}
