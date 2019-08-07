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
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.NotOwnerException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.exceptionhandling.ModelNotFoundException;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.acumos.workbench.modelservice.util.ModelServiceProperties;
import org.acumos.workbench.modelservice.util.ModelServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	private ProjectServiceRestClientImpl psClient;
	
	@Override
	public List<Model> getModels(String authenticatedUserId, String projectId) {
		logger.debug("getModels() Begin");
		List<Model> modelList = new ArrayList<Model>();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		List<MLPSolution> mlpSolutions = null;
		List<MLPSolutionRevision> revList = null;
		List<Model> result = new ArrayList<Model>();
		if(null == projectId){
			try {
				Map<String, Object> queryParameters = new HashMap<String, Object>();
				queryParameters.put("userId", userId);
				RestPageRequest pageRequest = new RestPageRequest(0, confprops.getResultsetSize());
				cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
				RestPageResponse<MLPSolution> response = cdsClient.searchSolutions(queryParameters, false, pageRequest);
				mlpSolutions = response.getContent();
			} catch (Exception e) {
				logger.error(props.getCdsSearchModelsExcp());
				throw new TargetServiceInvocationException(props.getCdsSearchModelsExcp());
			}
		}else {
			try {
				//TODO : (WIP)  Retrieve all the models associated with the given user and the project
				// Rest API to get list of models associated to a project, user and status 
				//cdsClient.getListofModelsAssocaitedtoProject(projectId);
				MLPSolution mlpSolution = new MLPSolution();
				mlpSolution.setActive(true);
				mlpSolution.setCreated(Instant.now());
				mlpSolution.setFeatured(true);
				mlpSolution.setMetadata("MetaData");
				mlpSolution.setModelTypeCode("DS");
				mlpSolution.setModified(Instant.now());
				mlpSolution.setName("wordembeddings04");
				mlpSolution.setOrigin("MLP");
				mlpSolution.setSolutionId("0893bdca-663e-4acd-b35e-acc950c41a90");//0893bdca-663e-4acd-b35e-acc950c41a90//01509c7b-9717-45d0-8980-b533d2cbc07c
				mlpSolution.setToolkitTypeCode("H2");
				mlpSolution.setUserId(userId);
				
				MLPSolution mlpSolution1 = new MLPSolution();
				mlpSolution1.setActive(true);
				mlpSolution1.setCreated(Instant.now());
				mlpSolution1.setFeatured(true);
				mlpSolution1.setMetadata("MetaData");
				mlpSolution1.setModelTypeCode("CL");
				mlpSolution1.setModified(Instant.now());
				mlpSolution1.setName("BVOIP2");
				mlpSolution1.setOrigin("MLP");
				mlpSolution1.setSolutionId("02976c8d-c055-497f-a16b-9181f1e89935");
				mlpSolution1.setToolkitTypeCode("CO");
				mlpSolution1.setUserId(userId);
				
				mlpSolutions = new ArrayList<MLPSolution>();
				mlpSolutions.add(mlpSolution);
				mlpSolutions.add(mlpSolution1);
			} catch (Exception e) {
				logger.error(props.getCdsGetProjectModelsExcp());
				throw new TargetServiceInvocationException(props.getCdsGetProjectModelsExcp());
			}
		}
		List<MLPCatalog> catalogList = null;
		if (null != mlpSolutions && mlpSolutions.size() > 0) {
			for(MLPSolution mlp : mlpSolutions){
				catalogList = cdsClient.getSolutionCatalogs(mlp.getSolutionId());
				revList = cdsClient.getSolutionRevisions(mlp.getSolutionId());
				modelList = ModelServiceUtil.getModelVOs(mlp,revList,catalogList, mlpUser);
				result.addAll(modelList);
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
	public boolean isOwnerOfProject(String authenticatedUserId, String projectId) throws NotOwnerException {
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
			throw new ProjectNotFoundException();
		}
		logger.debug("projectExists(String) End");
	}

	@Override
	public boolean isOwnerofModel(String authenticatedUserId, String modelId) throws NotOwnerException {
		logger.debug("isOwnerofModel() Begin");
		// Call to CDS to check if user is the owner of the project.
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
		// TODO :WIP  CDS Dependency to search the SolutionId/ ModelId with userId is there or not
		MLPSolution response = cdsClient.getSolution(modelId);
		//RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
		if(!response.getUserId().equals(userId) && null != response){
			logger.warn("Permission denied");
			throw new NotOwnerException();
		}
		logger.debug("isOwnerofModel() End");
		return true;
		
	}

	@Override
	public void checkProjectDetails(String authenticatedUserId, String projectId, String authToken) {
		logger.debug("checkProjectDetails() Begin");
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
		logger.debug("checkProjectDetails() End");
	}

	@Override
	public void checkModelExistsinCDS(String modelId) {
		logger.debug("checkModelExistsinCDS() Begin");
		cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
		MLPSolution response = cdsClient.getSolution(modelId);
		if(null == response){
			throw new ModelNotFoundException();
		}
		logger.debug("checkModelExistsinCDS() End");
		
	}

	@Override
	public Model insertProjectModelAssociation(String authenticatedUserId, String projectId, String modelId,
			Model model) {
		logger.debug("insertProjectModelAssociation() Begin");
		// The Model Service must insert new entry in Project Model association Table
		// Make sure that the Association should not exists, if exists it should be in Deleted State only 
		// TODO : Call the CDS method and get the data
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
		logger.debug("insertProjectModelAssociation() End");
		return updatedModel;
		
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
}
