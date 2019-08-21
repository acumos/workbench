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

package org.acumos.workbench.modelservice.service;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.NotOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Projects;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.exceptionhandling.ModelNotFoundException;
import org.acumos.workbench.modelservice.lightcouch.DataSetModel;
import org.acumos.workbench.modelservice.util.AssociationStatus;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.acumos.workbench.modelservice.util.ModelServiceProperties;
import org.acumos.workbench.modelservice.util.ModelServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;


@Service("ModelServiceImpl")
public class ModelServiceImpl implements ModelService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	private ModelServiceProperties props;
	
	@Autowired
	private ConfigurationProperties confprops;
	
	@Autowired
	@Qualifier("LightCouchService")
	private LightCouchService couchService;
	
	@Override
	public List<Model> getModels(String authenticatedUserId, String projectId) {
		logger.debug("getModels() Begin");
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		List<MLPSolution> publishedMlpSolutions = new ArrayList<MLPSolution>();
		List<MLPSolution> privateMlpSolutions = new ArrayList<MLPSolution>();
		List<MLPSolutionRevision> mlpSolutionRevisionList = new ArrayList<MLPSolutionRevision>();
		List<Model> result = new ArrayList<Model>();
		List<MLPSolution> commonMLPSolutions = new ArrayList<MLPSolution>();
		List<MLPCatalog> mlpCatalogList = new ArrayList<MLPCatalog>();
		if (null == projectId) {
			try {
				Map<String, Object> queryParameters = new HashMap<String, Object>();
				queryParameters.put("userId", userId);
				String[] nameKeyword = {};
				String[] descriptionKeywords = {};
				String[] modelTypeCodes = {};
				String[] tags = {};
				String[] anyTags = {};
				String[] catalogIds = {};
				boolean active = true;
				boolean isPublished = false;
				String[] ownerIds = {};
				RestPageRequest pageRequest = new RestPageRequest(0, confprops.getResultsetSize());
				cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
				// This will get User Specific Solutions which are PrivateSolutions.(Get the Published and Unpublished models also)
				RestPageResponse<MLPSolution> userPrivateSolutions = cdsClient.findUserSolutions(active, isPublished,
						userId, nameKeyword, descriptionKeywords, modelTypeCodes, anyTags, pageRequest);
				// This will get only the published solutions (independent of User)
				RestPageResponse<MLPSolution> publishedSolutions = cdsClient.findPublishedSolutionsByKwAndTags(
						nameKeyword, active, ownerIds, modelTypeCodes, tags, anyTags, catalogIds, pageRequest);
				
				publishedMlpSolutions = publishedSolutions.getContent();
				privateMlpSolutions = userPrivateSolutions.getContent();

				commonMLPSolutions.addAll(publishedMlpSolutions);
				commonMLPSolutions.addAll(privateMlpSolutions);
			} catch (Exception e) {
				logger.error(props.getCdsFindUserSolutionsExcp());
				throw new TargetServiceInvocationException(props.getCdsFindUserSolutionsExcp());
			}
			if (null != commonMLPSolutions && commonMLPSolutions.size() > 0) {
				for (MLPSolution mlpSolution : commonMLPSolutions) {
					mlpCatalogList = cdsClient.getSolutionCatalogs(mlpSolution.getSolutionId());
					mlpSolutionRevisionList = cdsClient.getSolutionRevisions(mlpSolution.getSolutionId());
					// Check if any error in the model, need not to add erroneous models or revision
					for (MLPSolutionRevision revision : mlpSolutionRevisionList) {
						boolean errorInModel = checkErrorInModel(mlpSolution.getSolutionId(), revision.getRevisionId());
						if (!errorInModel) {
							result.add(ModelServiceUtil.getModelVO(mlpSolution, revision, mlpCatalogList, mlpUser));
						}
					}
				}
			}
		} else {
			try {
				//Retrieve all the models associated with the given user and the project 
				// Rest API to get list of models associated to a project, user
				// and status cdsClient.getListofModelsAssocaitedtoProject(projectId);
				List<DataSetModel> modelAssociations = couchService.getModelsAssociatedToProject(authenticatedUserId, projectId);
				if(null != modelAssociations && !modelAssociations.isEmpty()){
					AssociationStatus associationStatus = null;
					MLPSolution mlpSolution =  null;
					MLPSolutionRevision mlpRevision = null;
					Model associatedModel = null;
					Identifier modelIdentifier = null;
					List<KVPair> kvPairs = null;
					KVPair kvPair = null;
					KVPairs metrics = null;
					MLPUser mlpModelOwner = null;
					User modelOwner = null;
					Identifier modelOwnerIdentifier = null;
					ArtifactState artifactStatus = null;
					ServiceState serviceState = null;
					
					Version modelVersion = null;
					for(DataSetModel modelAssociation : modelAssociations){
						associatedModel = new Model();
						//associationStatus = AssociationStatus.valueOf(modelAssociation.getStatus());
						mlpSolution = cdsClient.getSolution(modelAssociation.getSolutionId());
						mlpRevision = cdsClient.getSolutionRevision(modelAssociation.getSolutionId(), modelAssociation.getRevisionId());
						artifactStatus = new ArtifactState();
						serviceState = new ServiceState();
						//If status is active check if mode is not deleted in ACUMOS
						if(modelAssociation.getStatus().equals(AssociationStatus.ACTIVE.getAssociationStatusCode()) && !mlpSolution.isActive()) {
							artifactStatus.setStatus(ArtifactStatus.FAILED);
							serviceState.setStatus(ServiceStatus.INACTIVE);
							//TODO : Ramn - include in try/catch
							modelAssociation.setStatus(AssociationStatus.INVALID.getAssociationStatusCode());
							//Update the association in CouchDB
							//TODO : Raman - to work with Pushbendra to get it implemented. 
							//couchService.updateAssociationStatus(modelAssociation.getAssociationID(), AssociationStatus.INVALID.getAssociationStatusCode());
						} else if(modelAssociation.getStatus().equals(AssociationStatus.ACTIVE.getAssociationStatusCode()) && mlpSolution.isActive()) {
							artifactStatus.setStatus(ArtifactStatus.ACTIVE);
							serviceState.setStatus(ServiceStatus.ACTIVE);
						} else if(modelAssociation.getStatus().equals(AssociationStatus.INVALID.getAssociationStatusCode())) {
							artifactStatus.setStatus(ArtifactStatus.FAILED);
							serviceState.setStatus(ServiceStatus.INACTIVE);
						}
						associatedModel.setArtifactStatus(artifactStatus);
						associatedModel.setServiceStatus(serviceState);
						
						modelIdentifier = new Identifier();
						modelIdentifier.setUuid(mlpSolution.getSolutionId());
						modelIdentifier.setName(mlpSolution.getName());
						modelVersion = new Version();
						modelVersion.setLabel(mlpRevision.getVersion());
						modelVersion.setCreationTimeStamp(modelAssociation.getCreatedTimestamp()); //association creation time 
						modelVersion.setModifiedTimeStamp(modelAssociation.getUpdateTimestamp());//association update time
						modelIdentifier.setVersionId(modelVersion);
						//TODO : Need to check 
						//modelIdentifier.getVersionId().setUser(mlpUser.getLoginName());
						modelIdentifier.setIdentifierType(IdentifierType.MODEL);
						
						
						kvPairs = new ArrayList<KVPair>();
						kvPair = new KVPair(ModelServiceConstants.MODELTYPECODE, modelAssociation.getModelType());
						kvPairs.add(kvPair);
						if(modelAssociation.getCatalogName().equals("None")) {
							kvPair = new KVPair(ModelServiceConstants.MODELPUBLISHSTATUS, "false");
						} else {
							kvPair = new KVPair(ModelServiceConstants.MODELPUBLISHSTATUS, "true");
						}
						kvPairs.add(kvPair);
						kvPair = new KVPair(ModelServiceConstants.CATALOGNAMES, modelAssociation.getCatalogName());
						kvPairs.add(kvPair);
						kvPair = new KVPair(ModelServiceConstants.ASSOCIATIONID, modelAssociation.getAssociationID());
						kvPairs.add(kvPair);
						
						metrics = new KVPairs();
						metrics.setKv(kvPairs);
						modelIdentifier.setMetrics(metrics);
						associatedModel.setModelId(modelIdentifier);
						
						//Get the Model Owner Details 
						//TODO : Raman - put in try/catch for CDS call. 
						mlpModelOwner = cdsClient.getUser(mlpSolution.getUserId());
						modelOwner = new User();
						modelOwner.setAuthenticatedUserId(mlpModelOwner.getLoginName());
						modelOwnerIdentifier = new Identifier();
						modelOwnerIdentifier.setIdentifierType(IdentifierType.USER);
						modelOwnerIdentifier.setName(mlpModelOwner.getFirstName() + " " + mlpModelOwner.getLastName());
						modelOwnerIdentifier.setUuid(mlpModelOwner.getUserId());
						modelOwner.setUserId(modelOwnerIdentifier);
						associatedModel.setOwner(modelOwner);
						
						// Need to set the Projects which are associated to Model
						Project project = new Project();
						Identifier projectIdentifier = new Identifier();
						projectIdentifier.setUuid(modelAssociation.getProjectId());
						projectIdentifier.setIdentifierType(IdentifierType.PROJECT);
						project.setProjectId(projectIdentifier);
						Projects projects = new Projects();
						List<Project> projectList = new ArrayList<Project>();
						projectList.add(project);
						projects.setProjects(projectList);
						associatedModel.setProjects(projects);
						result.add(associatedModel);
					}
				}				
			} catch (Exception e) {
				logger.error(props.getCdsGetProjectModelsExcp());
				throw new TargetServiceInvocationException(props.getCdsGetProjectModelsExcp());
			}
		}

		logger.debug("getModels() End");
		return result;
	}
	

	@Override
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException,
			TargetServiceInvocationException {
		logger.debug("getUserDetails() Begin");
		MLPUser mlpUser = null;
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("loginName", authenticatedUserId);
			queryParameters.put("active", true);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
			RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false,
					pageRequest);
			List<MLPUser> mlpUsers = response.getContent();
			if (null != mlpUsers && mlpUsers.size() > 0) {
				mlpUser = mlpUsers.get(0);
			} else {
				logger.error("User not found");
				throw new UserNotFoundException(authenticatedUserId);
			}

		} catch (RestClientResponseException e) {
			logger.error("CDS - Get User Details");
			throw new TargetServiceInvocationException(props.getCdsGetUserExcp());
		}
		logger.debug("getUserDetails() End");
		return mlpUser;
	}

	@Override
	public boolean isUserAccessibleProject(String authenticatedUserId, String projectId) throws NotOwnerException {
		logger.debug("isOwnerOfProject() Begin");
		// Call to CDS to check if user is the owner of the project.
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("projectId", projectId);
		queryParameters.put("userId", userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
		RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
		if ((null == response) || (null != response && response.getContent().size() == 0)) {
			logger.warn("Permission denied");
			throw new NotOwnerException();
		}
		logger.debug("isOwnerOfProject() End");
		return true;
	}

	@Override
	public void projectExists(String projectId) throws ProjectNotFoundException {
		logger.debug("projectExists(String) Begin");
		MLPProject mlpProject = cdsClient.getProject(projectId);
		if (null == mlpProject) {
			logger.error("Project Not Found Exception occured in projectExists()");
			throw new ProjectNotFoundException();
		}
		logger.debug("projectExists(String) End");
	}

	@Override
	public boolean userAccessableModel(String authenticatedUserId, String modelId) throws NotOwnerException {
		logger.debug("isOwnerofModel() Begin");
		// Call to CDS to check if user is the owner of the project.
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
		// To search the SolutionId/ ModelId with userId is there or not in CDS
		MLPSolution response = cdsClient.getSolution(modelId);
		if(!response.getUserId().equals(userId) && null != response){
			logger.warn("Permission denied");
			throw new NotOwnerException();
		}
		logger.debug("isOwnerofModel() End");
		return true;
		
	}

	@Override
	public void checkModelExists(String modelId) {
		logger.debug("checkModelExistsinCDS() Begin");
		cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
		MLPSolution response = cdsClient.getSolution(modelId);
		if(null == response){
			logger.error("ModelNotFoundException occured in checkModelExistsinCDS()");
			throw new ModelNotFoundException();
		}
		logger.debug("checkModelExistsinCDS() End");
		
	}

	@Override
	public Model insertProjectModelAssociation(String authenticatedUserId, String projectId, String modelId,
			Model model){
		logger.debug("insertProjectModelAssociation() Begin");
		// The Model Service must insert new entry in Project Model association Table
		// Make sure that the Association should not exists, if exists it should be in Deleted State only
		// Get the Catalog Details for the modelId and the Version
		DataSetModel projectModelAssociation = new DataSetModel();
		projectModelAssociation.setUserId(authenticatedUserId);
		projectModelAssociation.setProjectId(projectId);
		projectModelAssociation.setSolutionId(modelId);
		
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String version = model.getModelId().getVersionId().getLabel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String catalogName = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.CATALOGNAMES)) {
				catalogName = kv.getValue();
			}
		}
		projectModelAssociation.setCatalogName(catalogName);
		if(!"None".equals(catalogName)) {
			// Set catalog Details in Model
			String catalogId = null;
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("name", catalogName);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			logger.debug("Calling CDS searchCatalogs()");
			RestPageResponse<MLPCatalog> catResponse = cdsClient.searchCatalogs(queryParams, false, pageRequest);
			if (null != catResponse && !catResponse.getContent().isEmpty()) {
				List<MLPCatalog> catList = catResponse.getContent();
				for (MLPCatalog cat : catList) {
					catalogId = cat.getCatalogId();
				}
				projectModelAssociation.setCatalogId(catalogId);
			}
		}
		// Get MLPSolution and MLPSolutionRevision corresponding to the modelId and version
		// Set the ModelTypeCode from MLPSolution
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the model or not
		logger.debug("Calling CDS getSolution()");
		MLPSolution mlpSolution = cdsClient.getSolution(modelId);
		if (null == mlpSolution) {
			logger.error("ModelNotFoundException occured in checkModelExistsinCDS()");
			throw new ModelNotFoundException();
		} 
		
		if ( null != mlpSolution.getModelTypeCode()) {
			projectModelAssociation.setModelType(mlpSolution.getModelTypeCode());
		}else {
			projectModelAssociation.setModelType("None");
		}
		// Set the RevId from the MLPSolutionRevision
		String mlpRevisionId = null;
		logger.debug("Calling CDS getSolutionRevisions()");
		List<MLPSolutionRevision> mlpSolRevisions = cdsClient.getSolutionRevisions(modelId);
		if (null != mlpSolRevisions && !mlpSolRevisions.isEmpty()) {
			for (MLPSolutionRevision rev : mlpSolRevisions) {
				if (version.equals(rev.getVersion())) {
					mlpRevisionId = rev.getRevisionId();
					projectModelAssociation.setRevisionId(mlpRevisionId);
				}
			}
		}
		projectModelAssociation = couchService.insertProjectModelAssociation(projectModelAssociation, model, mlpUser);
		Model result = getModelVO(model, projectModelAssociation, mlpUser, mlpSolution);
		logger.debug("insertProjectModelAssociation() End");
		return result;
		
	}


	


	@Override
	public Model updateProjectModelAssociation(String authenticatedUserId, String projectId, String modelId,
			Model model) {
		logger.debug("updateProjectModelAssociation() Begin");
		// TODO : (WIP) Update the project model association details in CDS table by calling cds method cdsClient.
		Model updatedModel = new Model();
		Identifier id = new Identifier();
		id.setUuid(modelId);
		id.setName("wordembeddings04");
		Version version = new Version();
		version.setComment("Comment");
		version.setLabel("1.0.0");
		version.setCreationTimeStamp(Instant.now().toString());
		version.setModifiedTimeStamp(Instant.now().toString());
		version.setUser(authenticatedUserId);
		id.setVersionId(version);
		updatedModel.setModelId(id);
		ArtifactState state = new ArtifactState();
		state.setStatus(ArtifactStatus.ACTIVE);
		updatedModel.setArtifactStatus(state);
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.COMPLETED);
		updatedModel.setServiceStatus(serviceState);
		logger.debug("updateProjectModelAssociation() End");
		return updatedModel;
	}

	@Override
	public ServiceState deleteProjectModelAssociation(String authenticatedUserId, String projectId, String modelId,
			Model model) {
		logger.debug("deleteProjectModelAssociation() Begin");
		ServiceState result = new ServiceState();
		// TODO : (WIP) Delete the project model association details in CDS table by calling cds method
		// cdsClient.
		
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("Project Model Association Deleted successfully.");
		logger.debug("deleteProjectModelAssociation() End");
		return result;
	}
	
	
	/**
	 * @param model
	 * @param projectModelAssociation
	 * @param mlpUser
	 * @param mlpSolution
	 * @return
	 */
	private Model getModelVO(Model model, DataSetModel projectModelAssociation, MLPUser mlpUser,
			MLPSolution mlpSolution) {
		Model result = new Model();
		Identifier modelIdentifier = model.getModelId();
		modelIdentifier.setUuid(mlpSolution.getSolutionId());
		modelIdentifier.setName(mlpSolution.getName());
		modelIdentifier.getVersionId().setCreationTimeStamp(projectModelAssociation.getCreatedTimestamp());
		modelIdentifier.getVersionId().setModifiedTimeStamp(projectModelAssociation.getUpdateTimestamp());
		modelIdentifier.getVersionId().setUser(mlpUser.getLoginName());
		modelIdentifier.setIdentifierType(IdentifierType.MODEL);
		//Include AssociationID in the metrics 
		KVPair kvPairAssociationID = new KVPair(ModelServiceConstants.ASSOCIATIONID, projectModelAssociation.getAssociationID());
		modelIdentifier.getMetrics().getKv().add(kvPairAssociationID);
		result.setModelId(modelIdentifier);
		
		//Get the Model Owner Details 
		//TODO : Raman - put in try/catch for CDS call. 
		MLPUser mlpModelOwner = cdsClient.getUser(mlpSolution.getUserId());
		User modelOwner = new User();
		modelOwner.setAuthenticatedUserId(mlpModelOwner.getLoginName());
		Identifier modelOwnerIdentifier = new Identifier();
		modelOwnerIdentifier.setIdentifierType(IdentifierType.USER);
		modelOwnerIdentifier.setName(mlpModelOwner.getFirstName() + " " + mlpModelOwner.getLastName());
		modelOwnerIdentifier.setUuid(mlpModelOwner.getUserId());
		modelOwner.setUserId(modelOwnerIdentifier);
		result.setOwner(modelOwner);
		ArtifactState artifactStatus = new ArtifactState();
		artifactStatus.setStatus(ArtifactStatus.ACTIVE);
		result.setArtifactStatus(artifactStatus);
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ACTIVE);
		result.setServiceStatus(serviceState);
		// Need to set the Projects which are associated to Model
		Project project = new Project();
		Identifier projectIdentifier = new Identifier();
		projectIdentifier.setUuid(projectModelAssociation.getProjectId());
		projectIdentifier.setIdentifierType(IdentifierType.PROJECT);
		project.setProjectId(projectIdentifier);
		Projects projects = new Projects();
		List<Project> projectList = new ArrayList<Project>();
		projectList.add(project);
		projects.setProjects(projectList);
		result.setProjects(projects);
		return result;
	}
	
	private boolean checkErrorInModel(String solutionId, String string) {
		boolean errorInModel = false;
		String stepStatusFailed = "FA";
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("solutionId", solutionId);
		queryParameters.put("revisionId", string);
		queryParameters.put("statusCode", stepStatusFailed);
		RestPageResponse<MLPTask> taskResponse = cdsClient.searchTasks(queryParameters, false,
				new RestPageRequest(0, 10));
		if (null != taskResponse && !taskResponse.isEmpty()) {
			for (MLPTask task : taskResponse) {
				List<MLPTaskStepResult> stepResultList = cdsClient.getTaskStepResults(task.getTaskId());
				if (null !=stepResultList && !stepResultList.isEmpty()) {
					for (MLPTaskStepResult step : stepResultList) {
						if (stepStatusFailed.equals(step.getStatusCode())) {
							errorInModel = true;
							break;
						}
					}
				}
			}
		}
		return errorInModel;
	}

}