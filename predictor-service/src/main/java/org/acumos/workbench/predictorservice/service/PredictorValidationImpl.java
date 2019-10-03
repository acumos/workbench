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
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.service.ModelServiceRestClientImpl;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.predictorservice.exception.AssociationException;
import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.lightcouch.DataSetPredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("PredictorValidationImpl")
public class PredictorValidationImpl implements PredictorValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private CouchDBService couchDBService;
	
	@Autowired
	private ProjectServiceRestClientImpl psClient;
	
	@Autowired
	private ModelServiceRestClientImpl msClient;
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsRestClientImpl;

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
				logger.debug("Project doesn't exists");
				throw new ProjectNotFoundException();
			}
		} else { 
			logger.debug("Project doesn't exists");
			throw new ProjectNotFoundException();
		}
		logger.debug("validateProject() End");
		
	}

	@Override
	public void predictorExists(String authenticatedUserId,String predictorId) {
		logger.debug("predictorExists() Begin");
		String userId = null;
		boolean hasAccess = false;
		List<DataSetPredictor> predictorList = couchDBService.getPredictorDetails(predictorId);
		// Check if Logged in User has the access to the input predictor
		for(DataSetPredictor predictor : predictorList) {
			userId = predictor.getUserId();
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
	public void isModelAssociatedToProject(String authenticatedUserId, String projectId, String solutionId,
			String revisionId, String authToken) {
		logger.debug("isModelAssociatedToProject() Begin");
		// Get the List of models for a particular project by calling Model Service API
		// List out all the Models that belongs to User under the Project
		boolean isAssociated = false;
		ResponseEntity<List<Model>> modelResponse = msClient.getModels(authenticatedUserId, projectId, authToken);
		if (null != modelResponse && !modelResponse.getBody().isEmpty()) {
			List<Model> modelList = modelResponse.getBody();
			for (Model model : modelList) {
				if (model.getModelId().getUuid().equals(solutionId)) {
					List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
					for (KVPair pair : kvPairList) {
						if (pair.getKey().equals("REV_ID")) {
							pair.getValue().equals(revisionId);
							isAssociated = true;
						}
					}
				}
			}
		}

		if (!isAssociated) {
			logger.error("Cannot Associate Predictor, as corresponding model is not associated to a Project");
			throw new AssociationException(
					"Cannot Associate Predictor, as corresponding model is not associated to a Project");
		}
		logger.debug("isModelAssociatedToProject() End");
	}

	@Override
	public String modelExists(String modelId, String version) {
		logger.debug("modelExists() Begin");
		boolean modelExists = false;
		String revisionId = null;
		MLPSolution mlpSolution = cdsRestClientImpl.getSolution(modelId);
		if(null != mlpSolution) {
			List<MLPSolutionRevision> mlpSolutionRevisions = cdsRestClientImpl.getSolutionRevisions(mlpSolution.getSolutionId());
			for(MLPSolutionRevision revision : mlpSolutionRevisions) {
				if(revision.getVersion().equals(version)) {
					revisionId = revision.getRevisionId();
					modelExists = true;
				}
			}
		}
		if(!modelExists) {
			throw new TargetServiceInvocationException("Solution is not avaiable");
		}
		logger.debug("modelExists() End");
		return revisionId;
	}


}
