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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Projects;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.modelservice.exceptionhandling.AssociationExistsException;
import org.acumos.workbench.modelservice.lightcouch.DataSetModel;
import org.acumos.workbench.modelservice.util.AssociationStatus;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("LightCouchService")
public class LightCouchService {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("ModelServiceImpl")
	private ModelService modelService;

	public Model insertProjectModelAssociation(DataSetModel modelData,String modelName,
			Model model) {
		Model modelDetails = null;
		CouchDbClient dbClient = new CouchDbClient();
		logger.debug("InsertProjectModelAssociation() Begin");
		DataSetModel metaData = new DataSetModel();
		metaData.setAssociationID(UUID.randomUUID().toString());
		metaData.setUserId(modelData.getUserId());
		metaData.setProjectId(modelData.getProjectId());
		metaData.setSolutionId(modelData.getSolutionId());
		metaData.setCreatedTimestamp(Instant.now().toString());
		metaData.setUpdateTimestamp(Instant.now().toString());
		metaData.setStatus(AssociationStatus.ACTIVE.getServiceStatusCode());

		// Need to check the ProjectId and SolutionId, RevisionId already exists in Cocuh DB or not if exists then throw the error
		associationExistsInCouch(metaData.getProjectId(), metaData.getSolutionId(), modelData.getRevisionId());
		Response response = null;
		try {
			// Save the metaData in Couch DB
			response = dbClient.save(metaData);
		} catch (Exception e) {
			logger.error("Exception occured while saving in DB");
		} finally {
			try {
				// Close the resources in finally block
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
			}
		}

		// Need to get the ModelId and SolutionId and ProjectId to check the duplicates.
		// Need to get the details of the document and populate over Model Object and return it.
		modelDetails = getDocuments(response.getId(), response.getRev(), modelData.getUserId(), modelData.getProjectId(), modelName,
				modelData.getSolutionId(), model);
		logger.debug("insertProjectModelAssociation() End");
		return modelDetails;
	}
	
	

	/**
	 * Retrieve the list of models associated to a project, owner as input user and status is not “Deleted”
	 * @param authenticatedUserId
	 * @param projectId
	 */
	public void getModelsAssociatedToProject(String authenticatedUserId, String projectId) {
		String jsonQuery = "{\"selector\":{\"ProjectId\":{\"$eq\":\"" + projectId + "\"}}}";

		Model model = new Model();
		CouchDbClient dbClient = new CouchDbClient();
		Object documentObj = dbClient.findDocs(jsonQuery, DataSetModel.class);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonStr = mapper.writeValueAsString(documentObj);
			String modelString = jsonStr.replace("[", "").replace("]", "");
			DataSetModel dataSetModel = mapper.readValue(modelString, DataSetModel.class);
			dataSetModel.getAssociationID();
		} catch (Exception e) {
		}

	}
	
	
	
	
	private Model getDocuments(String id, String rev, String authenticatedUserId, String projectId,String modelName, String modelId, Model transportModel) {
		String jsonQuery = "{\"selector\":{\"$and\":[{\"_id\":{\"$eq\":\"" + id + "\"}},{\"_rev\":{\"$eq\":\"" + rev
				+ "\"}}]}}";
		MLPUser mlpUser = modelService.getUserDetails(authenticatedUserId);
		CouchDbClient dbClient = new CouchDbClient();
		Object documentObj = dbClient.findDocs(jsonQuery, DataSetModel.class);
		ObjectMapper mapper = new ObjectMapper();
		// Return the Model VO to the end user.
		Model model = new Model();
		try {
			String jsonStr = mapper.writeValueAsString(documentObj);
			String modelString = jsonStr.replace("[", "").replace("]", "");
			DataSetModel dataSetModel  = mapper.readValue(modelString, DataSetModel.class);
			dataSetModel.getAssociationID();
			// Set the Artifact Status
			ArtifactState state = new ArtifactState();
			state.setStatus(ArtifactStatus.ACTIVE);
			model.setArtifactStatus(state);
			// Set the Identifier to the Model
			Identifier identifier = new Identifier();
			identifier.setIdentifierType(IdentifierType.MODEL);
			identifier.setName(modelName);
			identifier.setMetrics(transportModel.getModelId().getMetrics());
			identifier.setUuid(modelId);
			identifier.setVersionId(transportModel.getModelId().getVersionId());
			model.setModelId(identifier);
			// Set the Owner Details to Model
			User modelOwner = new User();
			modelOwner.setAuthenticatedUserId(mlpUser.getLoginName());
			Identifier userId = new Identifier();
			userId.setIdentifierType(IdentifierType.USER);
			userId.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
			userId.setUuid(mlpUser.getUserId());
			modelOwner.setUserId(userId);
			model.setOwner(modelOwner);
			// Set the Service Status details to Model
			ServiceState serviceState = new ServiceState();
			serviceState.setStatus(ServiceStatus.ACTIVE);
			serviceState.setStatusMessage(ModelServiceConstants.MODEL_IS_ACTIVE);
			model.setServiceStatus(serviceState);
			// Need to set the Projects which are associated to Model
			Project project = new Project();
			Identifier projectIdentifier = new Identifier();
			projectIdentifier.setUuid(dataSetModel.getProjectId());
			projectIdentifier.setIdentifierType(IdentifierType.PROJECT);
			project.setProjectId(projectIdentifier);
			Projects projects = new Projects();
			List<Project> projectList = new ArrayList<Project>();
			projectList.add(project);
			projects.setProjects(projectList);
			model.setProjects(projects);
			// Set the USer Details to the Model
			Users users = new Users();
			List<User> userList = new ArrayList<User>();
			userList.add(modelOwner);
			users.setUsers(userList);
			model.setUsers(users);
		}
		catch (IOException e) {
			logger.error("IOException Occured while parsing the Json String to DataSetModel");
		}finally {
			try {
				// Close the resources in finally block
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
			}
		}
		return model;
	}
	
	
	private void associationExistsInCouch(String projectId, String modelId, String revId) throws AssociationExistsException {
		logger.debug("associationExistsInCouch() Begin");
		String jsonQuery = "{\"selector\":{\"$and\":[{\"ProjectId\":{\"$eq\":\"" + projectId + "\"}},{\"SolutionId\":{\"$eq\":\"" + modelId + "\"}},{\"RevisionId\":{\"$eq\":\"" + revId + "\"}}]}}";
		CouchDbClient dbClient = new CouchDbClient();
		Object documentObj = dbClient.findDocs(jsonQuery, DataSetModel.class);
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr;
		DataSetModel dataSetModel = null;
		try {
			jsonStr = mapper.writeValueAsString(documentObj);
			String modelString = jsonStr.replace("[", "").replace("]", "");
			dataSetModel  = mapper.readValue(modelString, DataSetModel.class);
		} catch (IOException e) {
			logger.error("IOException occured while reading the json String");
		}finally {
			try {
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
			}
		}
		if(null != dataSetModel && !dataSetModel.getStatus().equals(ModelServiceConstants.DELETED)){
			logger.error("Association Exists Exception Occured in associationExistsInCouch()");
			throw new AssociationExistsException();
		}
		logger.debug("associationExistsInCouch() End");
	}


}
