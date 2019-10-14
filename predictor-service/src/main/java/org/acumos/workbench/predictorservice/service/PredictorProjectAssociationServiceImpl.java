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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.Environment;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Predictor;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Projects;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.predictorservice.exception.AssociationException;
import org.acumos.workbench.predictorservice.exception.CouchDBException;
import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.lightcouch.DataSetPredictor;
import org.acumos.workbench.predictorservice.lightcouch.PredictorProjectAssociation;
import org.acumos.workbench.predictorservice.utils.PredictorServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service("PredictorProjectAssociationServiceImpl")
public class PredictorProjectAssociationServiceImpl implements PredictorProjectAssociationService {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	private CouchDBService couchDbService;
	
	@Override
	public MLPUser getUserDetails(String authenticatedUserId)
			throws UserNotFoundException, TargetServiceInvocationException {
		logger.debug("getUserDetails() Begin");
		MLPUser mlpUser = null;
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("loginName", authenticatedUserId);
			queryParameters.put("active", true);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
			RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false, pageRequest);
			List<MLPUser> mlpUsers = response.getContent();
			if (null != mlpUsers && mlpUsers.size() > 0) {
				mlpUser = mlpUsers.get(0);
			} else {
				logger.error("User not found");
				throw new UserNotFoundException(authenticatedUserId);
			}

		} catch (RestClientResponseException e) {
			logger.error("CDS - Get User Details");
			throw new TargetServiceInvocationException();
		}
		logger.debug("getUserDetails() End");
		return mlpUser;
	}

	@Override
	public List<Predictor> getPredictors(String authenticatedUserId, String projectId) {
		logger.debug("getPredictors() Begin");
		Predictor predictor = null;
		List<Predictor> predList = new ArrayList<Predictor>();
		try {
			MLPUser mlpUser = getUserDetails(authenticatedUserId);
			List<PredictorProjectAssociation> predictorProjAssociationList = couchDbService.getPredictorsForProject(projectId);
			
			if (null != predictorProjAssociationList && !predictorProjAssociationList.isEmpty()) {
				for (PredictorProjectAssociation association : predictorProjAssociationList) {
					predictor = getPredictorVO(authenticatedUserId, mlpUser, association);
					predList.add(predictor);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		}
		logger.debug("getPredictors() End");
		return predList;
	}
	
	@Override
	public Predictor associatePredictorToProject(String authenticatedUserId, String projectId, PredictorProjectAssociation predProjAssociation) {
		logger.debug("associatePredictorToProject() Begin");
		PredictorProjectAssociation result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		Predictor predictor = null;
		try {
			if(null == predProjAssociation.getPredictorId()) {
				DataSetPredictor predictorDetails = createDataSetPredictor(authenticatedUserId,projectId,predProjAssociation);
				predProjAssociation.setPredictorId(predictorDetails.getPredcitorId());
			}else {
				predProjAssociation.setPredictorId(predProjAssociation.getPredictorId());
			}
			
			predProjAssociation.setProjectId(projectId);
			result = couchDbService.savePredictorProjectAssociation(predProjAssociation);
			predictor = getPredictorVO(authenticatedUserId,mlpUser,result);
		} catch (Exception e) {
			logger.error("Predictor already associated");
			throw new PredictorException("Predictor already associated");
		}
		logger.debug("associatePredictorToProject() End");
		return predictor;
	}

	@Override
	public Predictor editPredictorProjectAssociation(String authenticatedUserId,
			String associationId, PredictorProjectAssociation predictorProjAssociation) {
		logger.debug("editPredictorAssociationToProject() Begin");
		Predictor predictor = null;
		String newVersion = predictorProjAssociation.getPredictorVersion();
		String newPredictorKey = predictorProjAssociation.getPredictorkey();
		String newEnvironmentPath = predictorProjAssociation.getEnvironmentPath();
		boolean changeFound = false;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		try {
			PredictorProjectAssociation oldAssociation = couchDbService.getPredictorProjectAssocaition(associationId);
			if (null != oldAssociation) {
				//1. Get predictor from couch DB 
				DataSetPredictor oldPredictor = couchDbService.getPredictor(oldAssociation.getPredictorId());
				String oldVersion = oldAssociation.getPredictorVersion();
				String oldPredictorKey = oldAssociation.getPredictorkey();
				String oldEnvironmentPath = oldAssociation.getEnvironmentPath();
				if (null != newVersion && !newVersion.equals(oldVersion)) {
					oldAssociation.setPredictorVersion(newVersion);
					oldPredictor.setPredictorVersion(newVersion);
					changeFound = true;
				}
				if (null != newPredictorKey && !newPredictorKey.equals(oldPredictorKey)) {
					oldAssociation.setPredictorkey(newPredictorKey);
					oldPredictor.setPredictorkey(newPredictorKey);
					changeFound = true;
				}
				if (null != newEnvironmentPath && !newEnvironmentPath.equals(oldEnvironmentPath)) {
					oldAssociation.setEnvironmentPath(newEnvironmentPath);
					oldPredictor.setEnvironmentPath(newEnvironmentPath);
					changeFound = true;
				}
				if (changeFound) {
					//Update Predictor details 
					oldPredictor.setPredictorId(predictorProjAssociation.getPredictorId());
					oldPredictor.setUpdateTimestamp(Instant.now().toString());
					couchDbService.updatePredictor(oldPredictor);
					//Update Predictor Association Details 
					oldAssociation.setPredictorId(predictorProjAssociation.getPredictorId());
					oldAssociation.setAssociationID(associationId);
					oldAssociation.setUpdateTimestamp(Instant.now().toString());
					couchDbService.updatePredictorProjectAssociation(oldAssociation);
				}
				predictor = getPredictorVO(authenticatedUserId, mlpUser, oldAssociation);
			}
		} catch (Exception e) {
			logger.error("Error occured in editPredictorAssociationToProject() " + e);
			throw new AssociationException(e.getMessage());
		}
		logger.debug("editPredictorAssociationToProject() End");
		return predictor;
	}
	
	@Override
	public ServiceState deleteAssociation(String authenticatedUserId, String associationId) {
		logger.debug("deleteAssociation() Begin");
		ServiceState result = new ServiceState();
		// Existence Validation : Check if PredictorProjectAssociation for input AsscoaitionId exists
		PredictorProjectAssociation association= couchDbService.getPredictorProjectAssocaition(associationId);
		// Access Validation : Check if logged in user has access to the input AssociationId.  For now check if user is owner of the Association
		if(association.getUserId().equals(authenticatedUserId)){
			couchDbService.deleteAssociation(association.getAssociationID(), association.get_rev());
			result.setStatus(ServiceStatus.COMPLETED);
			result.setStatusMessage("Predictor Project Asssociation Deleted Successfully");
		}else {
			logger.error(authenticatedUserId + ":  User is not Owner of the Association");
			throw new AssociationException(authenticatedUserId + ": User is not Owner of the Association");
		}
		logger.debug("deleteAssociation() Begin");
		return result;
	}
	
	@Override
	public Predictor getPredictor(String authenticatedUserId, String modelId,String modelVersion, String revisionId) {
		logger.debug("getPredictor() Begin");
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		Predictor predictor = null;
		try {
			List<DataSetPredictor> datasetPredictors = couchDbService.fetchPredictorDetails(authenticatedUserId,modelId,revisionId);
			if(null != datasetPredictors && !datasetPredictors.isEmpty()) {
				for(DataSetPredictor pred : datasetPredictors) {
					predictor = convertPredictorVO(authenticatedUserId,modelVersion,mlpUser,pred);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		}
		logger.debug("getPredictor() Begin");
		return predictor;
	}
	
	@Override
	public DataSetPredictor createDataSetPredictor(String authenticatedUserId, String projectId,
			PredictorProjectAssociation predictorProjAssociation) {
		logger.debug("createDataSetPredictor() Begin");
		DataSetPredictor result = couchDbService.saveDataSetPredictor(authenticatedUserId,projectId,predictorProjAssociation);
		logger.debug("createDataSetPredictor() Begin");
		return result;
	}

	private Predictor convertPredictorVO(String authenticatedUserId,String modelVersion, MLPUser mlpUser, DataSetPredictor pred) {
		Predictor predictor = new Predictor();
		Identifier predIdentifier = new Identifier();
		predIdentifier.setIdentifierType(IdentifierType.PREDICTOR);
		predIdentifier.setName(pred.getPredictorName());
		predIdentifier.setUuid(pred.getPredcitorId());
		Version predictorVersion = new Version();
//		predictorVersion.setLabel(pred.getPredictorVersion());
		predictorVersion.setCreationTimeStamp(pred.getCreatedTimestamp());
		predictorVersion.setModifiedTimeStamp(pred.getUpdateTimestamp());
		predIdentifier.setVersionId(predictorVersion);
		predictor.setPredictorId(predIdentifier);
		predIdentifier.setServiceUrl(pred.getEnvironmentPath());

		// Predictor Metrics
		List<KVPair> kvPairList = new ArrayList<KVPair>();
		KVPairs metrics = new KVPairs();
		KVPair kvPair = new KVPair();
		kvPair.setKey(PredictorServiceConstants.PREDICTORKEY);
		kvPair.setValue(pred.getPredictorkey());
		kvPairList.add(kvPair);
		metrics.setKv(kvPairList);
		predIdentifier.setMetrics(metrics);

		ArtifactState artifactState = new ArtifactState();
		artifactState.setStatus(ArtifactStatus.ACTIVE);
		// Predictor Artifact Status
		predictor.setArtifactStatus(artifactState);
		// Predictor Environment Details
		predictor.setEnvironment(Environment.DEVELOPMENT);
		
		Model model = new Model();
		Identifier modelIdentifier = new Identifier();
		modelIdentifier.setIdentifierType(IdentifierType.MODEL);
		modelIdentifier.setUuid(pred.getSolutionId());
		Version version = new Version();
		MLPSolutionRevision mlpSolutionRevision=cdsClient.getSolutionRevision(pred.getSolutionId(), pred.getRevisionId());
		String modelVersionLabel =mlpSolutionRevision.getVersion();
		version.setLabel(modelVersionLabel);
		modelIdentifier.setVersionId(version);
		model.setModelId(modelIdentifier);
		// Predictor associated model details
		predictor.setModel(model);
		
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ACTIVE);
		// Predictor Service Status
		predictor.setServiceStatus(serviceState);
		
		User predictorUser = new User();
		predictorUser.setAuthenticatedUserId(authenticatedUserId);
		Identifier userIdentifier = new Identifier();
		userIdentifier.setIdentifierType(IdentifierType.USER);
		userIdentifier.setUuid(mlpUser.getUserId());
		predictorUser.setUserId(userIdentifier);
		// Predictor User Details
		predictor.setUser(predictorUser);
		
		return predictor;
	}

	private Predictor getPredictorVO(String authenticatedUserId, MLPUser mlpUser,
			PredictorProjectAssociation association) {
		Predictor predictor = new Predictor();
		Identifier predIdentifier = new Identifier();
		predIdentifier.setIdentifierType(IdentifierType.PREDICTOR);
		predIdentifier.setName(association.getPredictorName());
		predIdentifier.setUuid(association.getPredictorId());
		Version version = new Version();
//		version.setLabel(association.getPredictorVersion());
		version.setCreationTimeStamp(association.getCreatedTimestamp());
		version.setModifiedTimeStamp(association.getUpdateTimestamp());
		predIdentifier.setVersionId(version);
		predIdentifier.setServiceUrl(association.getEnvironmentPath());
		
		// Predictor Metrics
		List<KVPair> kvPairList = new ArrayList<KVPair>();
		KVPairs metrics = new KVPairs();
		KVPair kvPair = new KVPair();
		kvPair.setKey(PredictorServiceConstants.ASSOCIATIONID);
		kvPair.setValue(association.getAssociationID());
		kvPairList.add(kvPair);
		kvPair = new KVPair();
		kvPair.setKey(PredictorServiceConstants.PREDICTORKEY);
		kvPair.setValue(association.getPredictorkey());
		kvPairList.add(kvPair);
		metrics.setKv(kvPairList);
		predIdentifier.setMetrics(metrics);
		// Predictor Id
		predictor.setPredictorId(predIdentifier);
		ArtifactState artifactState = new ArtifactState();
		artifactState.setStatus(ArtifactStatus.ACTIVE);
		// Predictor Artifact Status
		predictor.setArtifactStatus(artifactState);
		// Predictor Environment Details
		predictor.setEnvironment(Environment.DEVELOPMENT);
		
		Model model = new Model();
		Identifier modelIdentifier = new Identifier();
		modelIdentifier.setIdentifierType(IdentifierType.MODEL);
		modelIdentifier.setUuid(association.getSolutionId());
		Version modelVersion = new Version();
		MLPSolutionRevision mlpSolutionRevision=cdsClient.getSolutionRevision(association.getSolutionId(), association.getRevisionId());
		String modelVersionLabel =mlpSolutionRevision.getVersion();
		modelVersion.setLabel(modelVersionLabel);
		modelIdentifier.setVersionId(modelVersion);
		
		
		
		KVPairs modelMetrics = new KVPairs();
		KVPair modelKVPair = new KVPair();
		modelKVPair.setKey(PredictorServiceConstants.REVISIONID);
		modelKVPair.setValue(association.getRevisionId());
		List<KVPair> modelKVPairList = new ArrayList<KVPair>();
		modelKVPairList.add(modelKVPair);
		modelMetrics.setKv(modelKVPairList);
		modelIdentifier.setMetrics(modelMetrics);
		model.setModelId(modelIdentifier);
		// Predictor associated model details
		predictor.setModel(model);
		List<Project> projectList = new ArrayList<Project>();
		Project predictorProject = new Project();
		Identifier projectIdentifier = new Identifier();
		projectIdentifier.setUuid(association.getProjectId());
		predictorProject.setProjectId(projectIdentifier);
		projectList.add(predictorProject);
		Projects predictorProjects = new Projects();
		predictorProjects.setProjects(projectList);
		// Predictor Projects
		predictor.setProjects(predictorProjects);
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ACTIVE);
		// Predictor Service Status
		predictor.setServiceStatus(serviceState);
		User predictorUser = new User();
		predictorUser.setAuthenticatedUserId(authenticatedUserId);
		Identifier userIdentifier = new Identifier();
		userIdentifier.setIdentifierType(IdentifierType.USER);
		userIdentifier.setUuid(mlpUser.getUserId());
		predictorUser.setUserId(userIdentifier);
		// Predictor User Details
		predictor.setUser(predictorUser);
		return predictor;
	}
}
