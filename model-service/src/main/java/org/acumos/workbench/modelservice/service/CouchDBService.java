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
import org.acumos.workbench.modelservice.exceptionhandling.AssociationException;
import org.acumos.workbench.modelservice.exceptionhandling.CouchDBException;
import org.acumos.workbench.modelservice.lightcouch.DataSetModel;
import org.acumos.workbench.modelservice.util.AssociationStatus;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("CouchDBService")
public class CouchDBService {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private ConfigurationProperties configurationProperties;

	/**
	 * To Insert Project Model Association in Couch DB
	 * @param modelData
	 * 			the DataSetModel
	 * @param model
	 * 			the Model Service Model VO
	 * @param mlpUser
	 * 			the CDS MLPUser
	 * @return
	 * 			returns DataSetModel
	 */
	public DataSetModel insertProjectModelAssociation(DataSetModel modelData, Model model, MLPUser mlpUser) {
		logger.debug("InsertProjectModelAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DataSetModel dataSetModel = new DataSetModel();
		// Need to check the ProjectId and SolutionId, RevisionId already exists in Cocuh DB or not if exists then throw the error
		associationExistsInCouch(modelData.getProjectId(), modelData.getSolutionId(), modelData.getRevisionId());
		Response response = new Response();
		try {
			dataSetModel.setAssociationID(UUID.randomUUID().toString());
			dataSetModel.setUserId(modelData.getUserId());
			dataSetModel.setProjectId(modelData.getProjectId());
			dataSetModel.setSolutionId(modelData.getSolutionId());
			dataSetModel.setRevisionId(modelData.getRevisionId());
			dataSetModel.setCreatedTimestamp(Instant.now().toString());
			dataSetModel.setUpdateTimestamp(Instant.now().toString());
			dataSetModel.setStatus(AssociationStatus.ACTIVE.getAssociationStatusCode());
			dataSetModel.setModelType(modelData.getModelType());
			dataSetModel.setCatalogId(modelData.getCatalogId());
			dataSetModel.setCatalogName(modelData.getCatalogName());
			// Save the metaData in Couch DB
			response = dbClient.save(dataSetModel);
			logger.debug("Response Object from Couch DB  : " + response);
		} catch (Exception e) {
			logger.error("Exception occured while saving in DB");
			throw new CouchDBException("Exception occured while saving");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("InsertProjectModelAssociation() End");
		return dataSetModel;
	}

	/**
	 * To Insert ProejctModel Association in CouchDB 
	 * @param modelData
	 * 			the DataSetModel
	 * @param modelName
	 * 			the Model/Solution Name
	 * @param model
	 * 			the Model Service Model VO
	 * @param mlpUser
	 * 			the CDS MLPUser
	 * @return
	 * 			returns Model VO
	 */
	public Model insertProjectModelAssociation(DataSetModel modelData, String modelName, Model model, MLPUser mlpUser) {
		logger.debug("InsertProjectModelAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		Model modelDetails = null;
		DataSetModel metaData = new DataSetModel();
		metaData.setAssociationID(UUID.randomUUID().toString());
		metaData.setUserId(modelData.getUserId());
		metaData.setProjectId(modelData.getProjectId());
		metaData.setSolutionId(modelData.getSolutionId());
		metaData.setCreatedTimestamp(Instant.now().toString());
		metaData.setUpdateTimestamp(Instant.now().toString());
		metaData.setStatus(AssociationStatus.ACTIVE.getAssociationStatusCode());

		// Need to check the ProjectId and SolutionId, RevisionId already exists in Cocuh DB or not if exists then throw the error
		associationExistsInCouch(metaData.getProjectId(), metaData.getSolutionId(), modelData.getRevisionId());
		Response response = new Response();
		try {
			// Save the metaData in Couch DB
			response = dbClient.save(metaData);
		} catch (Exception e) {
			logger.error("Exception occured while saving in DB");
			throw new CouchDBException("Exception occured while saving in DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		// Need to get the ModelId and SolutionId and ProjectId to check the duplicates.
		// Need to get the details of the document and populate over Model Object and return it.
		modelDetails = getDocuments(response.getId(), response.getRev(), modelData.getUserId(),
				modelData.getProjectId(), modelName, modelData.getSolutionId(), model, mlpUser);
		logger.debug("insertProjectModelAssociation() End");
		return modelDetails;
	}

	/**
	 * To Retrieve the list of models associated to a project, owner as input user
	 * and status is not deleted
	 * 
	 * @param authenticatedUserId
	 * 				the user login Name
	 * @param projectId
	 * 				the project Id
	 */
	public List<DataSetModel> getModelsAssociatedToProject(String authenticatedUserId, String projectId) {
		logger.debug("getModelsAssociatedToProject() Begin");
		String jsonQuery = String.format(ModelServiceConstants.GETMODELSASSOCIATEDTOPROJECTQUERY, projectId, AssociationStatus.DELETED.getAssociationStatusCode());
		List<DataSetModel> dataSetModelList = null;
		CouchDbClient dbClient = null;
		
		try {
			 dbClient = getLightCouchdbClient();
			dataSetModelList = dbClient.findDocs(jsonQuery, DataSetModel.class);
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("getModelsAssociatedToProject() End");
		return dataSetModelList;
	}

	/**
	 * To get the Associated Model for specific ModelId/SolutionId
	 * @param associatedId
	 * 			the associated ID
	 * @return DataSetModel
	 * 			the DataSetModel
	 */
	public DataSetModel getAssociatedModel(String associatedId) {
		logger.debug("getAssociatedModel() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DataSetModel association = null;
		try {
			
			association = dbClient.find(DataSetModel.class, associatedId);
		} catch (NoDocumentException e) {
			logger.error("Association not found in Couch DB");
			throw new AssociationException("Association not found in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("getAssociatedModel() End");
		return association;

	}

	/**
	 * To Delete the Associated Model
	 * @param _id
	 *            Association Id
	 * @param _rev
	 *            Association Revision Id
	 */
	public void deleteAssocaitedModel(String _id, String _rev) {
		logger.debug("deleteAssocaitedModel() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.remove(_id, _rev);
		} catch (Exception e) {
			logger.error("Exception occured while deleting the Document from Couch DB");
			throw new CouchDBException("Exception occured while deleting the Document from Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("deleteAssocaitedModel() End");
	}
	
	/**
	 * To Update Associated Model
	 * @param dataSetModel
	 * 			the DataSet Model
	 */
	public void updateAssocaitedModel(DataSetModel dataSetModel) {
		logger.debug("updateAssocaitedModel() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.update(dataSetModel);
		} catch (Exception e) {
			logger.error("Exception occured while updating the document in Couch DB");
			throw new CouchDBException("Exception occured while updating the document in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("updateAssocaitedModel() End");
	}

	private Model getDocuments(String id, String rev, String authenticatedUserId, String projectId, String modelName,
			String modelId, Model transportModel, MLPUser mlpUser) {
		logger.debug("getDocuments() Begin");
		String jsonQuery = String.format(ModelServiceConstants.GETDOCUMENTSQUERY, id,rev);
		CouchDbClient dbClient = getLightCouchdbClient();
		
		// Return the Model VO to the end user.
		Model model = new Model();
		try {
			Object documentObj = dbClient.findDocs(jsonQuery, DataSetModel.class);
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(documentObj);
			String modelString = jsonStr.replace("[", "").replace("]", "");
			DataSetModel dataSetModel = mapper.readValue(modelString, DataSetModel.class);
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
			serviceState.setStatusMessage(ModelServiceConstants.MODELISACTIVE);
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
		} catch (IOException e) {
			logger.error("Error while fetching association details from CouchDB");
			throw new CouchDBException("Error while fetching association details from CouchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("getDocuments() Begin");
		return model;
	}


	private void associationExistsInCouch(String projectId, String solutionId, String revisionId)
			throws AssociationException {
		logger.debug("associationExistsInCouch() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSetModel> dataSetModels = null;
		String jsonQuery = String.format(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY, projectId, solutionId,revisionId,AssociationStatus.ACTIVE.getAssociationStatusCode());
		try {
			dataSetModels = dbClient.findDocs(jsonQuery, DataSetModel.class);
			// similar association already exists and is ACTIVE
			if (null != dataSetModels && dataSetModels.size() > 0) { 
				logger.error("Association already exists in Couch DB");
				throw new AssociationException("Association already exists in Couch DB");
			}
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("associationExistsInCouch() End");
	}
	
	private CouchDbClient getLightCouchdbClient() {
		return new CouchDbClient(configurationProperties.getCouchDbName(),
				configurationProperties.isCreateIfnotExists(), configurationProperties.getCouchdbProtocol(),
				configurationProperties.getCouchdbHost(), configurationProperties.getCouchdbPort(), null, null);
	}

}
