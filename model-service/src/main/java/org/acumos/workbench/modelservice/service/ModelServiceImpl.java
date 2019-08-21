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
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.exceptionhandling.ModelNotFoundException;
import org.acumos.workbench.modelservice.lightcouch.DataSetModel;
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
				// TODO : (WIP) Retrieve all the models associated with the
				// given user and the project
				// Rest API to get list of models associated to a project, user
				// and status
				// cdsClient.getListofModelsAssocaitedtoProject(projectId);

				couchService.getModelsAssociatedToProject(authenticatedUserId, projectId);

				MLPSolution mlpSolution = new MLPSolution();
				
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
		DataSetModel modelData = new DataSetModel();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		
		String version = model.getModelId().getVersionId().getLabel();
		
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String catalogValue = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.CATALOG_NAMES)) {
				catalogValue = kv.getValue();
			}
		}
		// Set catalog Details in Model
		String catalogId = null;
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("name", catalogValue);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		logger.debug("Calling CDS searchCatalogs()");
		RestPageResponse<MLPCatalog> catResponse = cdsClient.searchCatalogs(queryParams, false, pageRequest);
		if (null != catResponse && !catResponse.getContent().isEmpty()) {
			List<MLPCatalog> catList = catResponse.getContent();
			for (MLPCatalog cat : catList) {
				catalogId = cat.getCatalogId();
			}
			modelData.setCatalogId(catalogId);
			modelData.setCatalogName(catalogValue);
		}
		// Get MLPSolution and MLPSolutionRevision corresponding to the modelId and version
		// Set the ModelTypeCode from MLPSolution
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the model or not
		logger.debug("Calling CDS getSolution()");
		String modelName = null;
		MLPSolution mlpSolution = cdsClient.getSolution(modelId);
		if (null == mlpSolution) {
			logger.error("ModelNotFoundException occured in checkModelExistsinCDS()");
			throw new ModelNotFoundException();
		} else if (!mlpSolution.getUserId().equals(userId)) {
			logger.warn("Permission denied");
			throw new NotOwnerException();
		} else if (null != mlpSolution && null != mlpSolution.getModelTypeCode()) {
			modelData.setModelType(mlpSolution.getModelTypeCode());
			modelName = mlpSolution.getName();
		}
		// Set the RevId from the MLPSolutionRevision
		String mlpRevisionId = null;
		logger.debug("Calling CDS getSolutionRevisions()");
		List<MLPSolutionRevision> mlpSolRevisions = cdsClient.getSolutionRevisions(modelId);
		if (null != mlpSolRevisions && !mlpSolRevisions.isEmpty()) {
			for (MLPSolutionRevision rev : mlpSolRevisions) {
				if (version.equals(rev.getVersion())) {
					mlpRevisionId = rev.getRevisionId();
					modelData.setRevisionId(mlpRevisionId);
				}
			}
		}
		modelData.setUserId(userId);
		modelData.setProjectId(projectId);
		modelData.setSolutionId(modelId);
		
		Model response = couchService.insertProjectModelAssociation(modelData, modelName, model);
		logger.debug("insertProjectModelAssociation() End");
		return response;
		
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