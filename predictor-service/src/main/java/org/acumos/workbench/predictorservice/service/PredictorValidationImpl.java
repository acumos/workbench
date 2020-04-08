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
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
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
	
	@Autowired
	private PredictorServiceImpl predictorServiceImpl;
	

	private MLPSolutionRevision mlpSolutionRevision;

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
		List<DataSetPredictor> predictorList = couchDBService.getPredictorById(authenticatedUserId, predictorId);
		// Check if Logged in User has the access to the input predictor
		for(DataSetPredictor predictor : predictorList) {
			userId = predictor.getUserId();
			if(authenticatedUserId.equals(userId)) {
				hasAccess = true;
				break;
			}
		}
		if(!hasAccess) {
			logger.error("Predictor is Not Accessible to logged in User");
			throw new PredictorException("Predictor is Not Accessible to logged in User");
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

	@Override
	public void isUserExists(String authenticatedUserId) {
		logger.debug("isUserExists() Begin");
		MLPUser mlpUser = null;
		mlpUser = predictorServiceImpl.getUserDetails(authenticatedUserId);
		if (mlpUser == null || mlpUser.getUserId().isEmpty()) {
			logger.error("User does not exists");
			throw new UserNotFoundException("Exception occured: User does not Exists ");
		}
		if (!mlpUser.isActive()) {
			logger.error("User Exists but not Active");
			throw new UserNotFoundException("User Exists but not Active");
		}
		logger.debug("isUserExists() End");
	}

	@Override
	public void isSolutionRevisionExists(String solutionId,String revisionId) {
		//check if SolutionRevisionExists is exists
		mlpSolutionRevision = cdsRestClientImpl.getSolutionRevision(solutionId, revisionId);
		if ((null == mlpSolutionRevision )) {
			logger.error("Solution RevisionId does not exists");
			throw new EntityNotFoundException("Solution RevisionId does not exists");
		}
	}

	@Override
	public void isPredictorExists(String authenticatedUserId, String predictorName) {
		// check if predictor is exists in couch db 
		List<DataSetPredictor> dataSetPredictorList = couchDBService.getPreditor(authenticatedUserId, predictorName);
		if (!dataSetPredictorList.isEmpty() ) {
			logger.error("Predictor name already Exists");
			throw new PredictorException("Predictor name already Exists");
		}
	}
	
	@Override
	public void isSolutionAccessible(String authenticatedUserId, String solutionId) {
		// check if solutionId is accessible to user
		List<MLPUser> mlpUserList;
		mlpUserList=cdsRestClientImpl.getSolutionAccessUsers(solutionId);
		// Need to check how to find if the Model is published or not.
		for (MLPUser mlpUser : mlpUserList) {
			if (!mlpUser.getLoginName().equals(authenticatedUserId)) {
				logger.error("Model is not accessible to the user");
				throw new EntityNotFoundException("Model is not accessible to the user");
			}
		}
	}
	
	@Override
	public void isPredictorAccessibleToUser(String authenticatedUserId, String predictorId) {
		//check if the user can access the predictor	
		List<DataSetPredictor> dataSetPredictorList = couchDBService.getPredictorById(authenticatedUserId, predictorId);
		for (DataSetPredictor dataSetPredictor : dataSetPredictorList) {
			if (!dataSetPredictor.getUserId().equals(authenticatedUserId)) {
				logger.error("User is not authorized to access the predictor");
				throw new UserNotFoundException("User is not authorized to access the predictor");
			}
		}
	}
	@Override
	public void isProjectExists(String authenticatedUserId, String projectId) {
		// check if project is exist in predictor
		MLPProject mlpProject=cdsRestClientImpl.getProject(projectId);
		if (null==mlpProject) {
			logger.error("Project does not Exists");
			throw new EntityNotFoundException("Project does not Exists");
		}
		else if (!mlpProject.isActive()) {
			logger.error("Project is not Active");
			throw new EntityNotFoundException("Project is not Active");
		}

	}

	@Override
	public void isValueExists(String fieldName, String value) {
		// check if value exists
		logger.debug("isValueExists() Begin");
		boolean result = false;
		String msg = "Mandatory field: " + fieldName + " is missing";
		if (null != value && !value.trim().equals("")) {
			result = true;
		}
		if (!result) {
			logger.error("Mandatory field : " + fieldName + " is missing");
			throw new ValueNotFoundException(msg);
		}
		logger.debug("isValueExists() End");
	}

}
