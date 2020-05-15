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

package org.acumos.workbench.datasource.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.acumos.workbench.common.exception.AssociationNotFoundException;
import org.acumos.workbench.common.exception.NotOwnerException;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.Permission;
import org.acumos.workbench.common.vo.Role;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.datasource.constants.DataSourceConstants;
import org.acumos.workbench.datasource.exception.AssociationException;
import org.acumos.workbench.datasource.exception.CouchDBException;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.util.CustomConfigProperties;
import org.acumos.workbench.datasource.vo.DataSourceAssociationModel;
import org.acumos.workbench.datasource.vo.DataSourceCollaboratorModel;
import org.lightcouch.CouchDbClient;
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
	private CustomConfigProperties configurationProperties;

	/**
	 * To Create Data Source in Couch DB
	 * 
	 * @param authenticatedUserId The Acumos User Login Id
	 * @param dataSource          DataSourceModel model object
	 * @return returns DataSourceModel after successful save in DB
	 * @throws CouchDBException in case of any failure
	 */
	public DataSource createDataSource(String authenticatedUserId, DataSource dataSource) throws CouchDBException {
		logger.debug("createDataSource() Begin");
		Response response = new Response();
		CouchDbClient dbClient = getLightCouchdbClient();
		logger.debug("Data Source Object contains DataSourceKey : " + dataSource.getDatasourceId().getUuid());
		try {
			response = dbClient.save(dataSource);
		} catch (Exception e) {
			logger.error("Exception occured in createDataSource()", e);
			throw new CouchDBException(" Exception occured while saving the record in couch db ");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		dataSource.set_id(response.getId());
		dataSource.set_rev(response.getRev());
		logger.debug("createDataSource() End");
		return dataSource;

	}

	/**
	 * To Update the Data Source Details
	 * 
	 * @param authenticatedUserId The Acumos User Login Id
	 * @param dataSource          The Data Source Model Object
	 * @return returns DataSourceModel onject
	 * @throws CouchDBException in case of any failure
	 */
	public DataSource updateDataSourceDetails(String authenticatedUserId, DataSource dataSource) throws CouchDBException {
		logger.debug("updateDataSourceDetails() Begin");
		Response response = new Response();
		CouchDbClient dbClient = getLightCouchdbClient();
		logger.debug("Data Source Object contains DataSourceKey : " + dataSource.getDatasourceId().getUuid());
		try {
			response = dbClient.update(dataSource);
			dataSource.set_id(response.getId());
			dataSource.set_rev(response.getRev());
		} catch (Exception e) {
			logger.error("Exception occured in updateDataSourceDetails()", e);
			throw new CouchDBException(" Exception occured while updating the record in couch db ");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		logger.debug("updateDataSourceDetails() End");
		return dataSource;
	}

	/**
	 * To Check the given DataSourceKey is valid or not
	 * 
	 * @param authenticatedUserId The Acumos User Login Id
	 * @param dataSourceKey       DataSource Key
	 * @return returns boolean value
	 * @throws IOException Throws IOException in case of any failure
	 */
	public boolean isValidDataSource(String authenticatedUserId, String dataSourceKey) throws IOException {
		logger.debug("isValidDataSource() Begin");
		boolean isExists = false;
		if (dataSourceKey == null || dataSourceKey.isEmpty()) {
			return isExists;
		}
		if (authenticatedUserId == null || authenticatedUserId.isEmpty()) {
			return isExists;
		}
		String jsonQuery = String.format(DataSourceConstants.FIND_DATASOURCE_FOR_KEY_AND_USER, authenticatedUserId,
				dataSourceKey);
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSource> dataSourceModel;
		try {
			dataSourceModel = dbClient.findDocs(jsonQuery, DataSource.class);
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents", e);
			throw new CouchDBException("DataSource is not available for the given DataSourceKey : " + dataSourceKey, e);
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (null != dataSourceModel & dataSourceModel.size() > 0) {
			isExists = true;
		} else {
			throw new NotOwnerException();
		}
		logger.debug("isValidDataSource() End");
		return isExists;
	}

	/**
	 * To check if the DataSource is exists or not
	 * 
	 * @param dataSourceKey input datasource key
	 * @return returns true/false
	 * @throws DataSourceNotFoundException in case of any failure
	 */
	public boolean isDataSourceExists(String dataSourceKey) throws DataSourceNotFoundException{
		logger.debug("isDataSourceExists() Begin");
		boolean isExists = false;
		if (dataSourceKey == null || dataSourceKey.isEmpty()) {
			return isExists;
		}
		String jsonQuery = String.format(DataSourceConstants.GET_DATASOURCE_FOR_A_KEY, dataSourceKey);
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSource> dataSourceModels;
		try {
			dataSourceModels = dbClient.findDocs(jsonQuery, DataSource.class);
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents", e);
			throw new CouchDBException(" Exception occured while finding the documents ", e);
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (null != dataSourceModels & dataSourceModels.size() > 0) {
			isExists = true;
		} else {
			throw new DataSourceNotFoundException(
					"DataSource is not available for the given DataSourceKey :  " + dataSourceKey,
					Status.NOT_FOUND.getStatusCode());
		}
		logger.debug("isDataSourceExists() End");
		return isExists;
	}

	/**
	 * To Get the Data Source for a DataSourceKey
	 * 
	 * @param dataSourceKey the datasource key
	 * @param _id           the document id
	 * @return returns DataSourceModel Object
	 * @throws DataSourceNotFoundException in case of any failure
	 */
	public DataSource getDataSource(String dataSourceKey, String _id) throws DataSourceNotFoundException {
		logger.debug("getDataSource() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DataSource dataSourceModel = new DataSource();
		try {
			dataSourceModel = dbClient.find(DataSource.class, _id);
		} catch (Exception e) {
			logger.error(
					"Exception occured while fetching the Data Source Details for Data Source Key : " + dataSourceKey);
			throw new CouchDBException(
					" Exception occured while finding the document for Data Source Key : " + dataSourceKey, e);
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (null == dataSourceModel) {
			throw new DataSourceNotFoundException(
					"DataSource is not available for the given DataSourceKey :  " + dataSourceKey,
					Status.NOT_FOUND.getStatusCode());
		}
		logger.debug("getDataSource() End");
		return dataSourceModel;
	}

	/**
	 * To get the List of DataSource for a data source key
	 * 
	 * @param dataSourceKey The Data Source Key
	 * @return returns List of DataSourceModel
	 * @throws DataSourceNotFoundException in case of any failure
	 */
	public List<DataSource> getDataSourceForKey(String dataSourceKey) throws DataSourceNotFoundException {
		logger.debug("getDataSourceForKey() Begin");
		List<DataSource> dataSourceResult = new ArrayList<DataSource>();
		String jsonQuery = String.format(DataSourceConstants.GET_DATASOURCE_FOR_A_KEY, dataSourceKey);
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dataSourceResult = dbClient.findDocs(jsonQuery, DataSource.class);
		} catch (Exception e) {
			logger.error(
					"Exception occured while fetching the Data Source Details for Data Source Key : " + dataSourceKey);
			throw new DataSourceNotFoundException(
					"DataSource is not available for the given DataSourceKey :  " + dataSourceKey,
					Status.NOT_FOUND.getStatusCode());
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (null == dataSourceResult) {
			throw new DataSourceNotFoundException(
					"DataSource is not available for the given DataSourceKey :  " + dataSourceKey,
					Status.NOT_FOUND.getStatusCode());
		}

		logger.debug("getDataSourceForKey() End");
		return dataSourceResult;
	}

	/**
	 * To get the List of DataSources
	 * 
	 * @param authenticatedUserId The Acumos User Login Id
	 * @param namespace           The Namespace of a DataSource
	 * @param category            The Category of a DataSource
	 * @param textSearch          testSearch of DataSource
	 * @return returns List of DataSource Model
	 * @throws DataSourceNotFoundException in case of any failure
	 */
	public List<DataSource> getListofDataSources(String authenticatedUserId, String namespace, String category,
			String textSearch) throws DataSourceNotFoundException{
		logger.debug("getListofDataSources() Begin");
		List<DataSource> dataSourceModel = new ArrayList<DataSource>();
		CouchDbClient dbClient = getLightCouchdbClient();
		StringBuilder builder = new StringBuilder();
		builder.append("{\"selector\":{\"$and\":[{\"isActive\":{\"$eq\":true}}");
		if (null != authenticatedUserId && !authenticatedUserId.isEmpty()) {
			builder.append(",").append("{\"owner.authenticatedUserId\":{\"$eq\":\"%s\"}}");
			String jsonQuery = String.format(builder.toString(), authenticatedUserId);
			builder = new StringBuilder(jsonQuery);
		}
		if (null != namespace && !namespace.isEmpty()) {
			// TODO : For Future Purpose
			// builder.append(",").append("{\"namespace\":{\"$eq\":" + namespace + "}}");
			// String jsonQuery = String.format(builder.toString(), namespace);
			builder.append(",").append("{\"namespace\":{\"$eq\":\"%s\"}}");
			String jsonQuery = String.format(builder.toString(), namespace);
			builder = new StringBuilder(jsonQuery);
		}
		if (null != category && !category.isEmpty()) {
			// TODO : For Future Purpose
			// "{\"selector\":{\"$and\":[{\"ownedBy\":{\"$eq\":\"%s\"}},{\"datasourceKey\":{\"$eq\":\"%s\"}},{\"isActive\":{\"$eq\":true}}]}}"
			// builder.append(",").append("{\"category\":{\"$eq\":" + category + "}}");
			builder.append(",").append("{\"category\":{\"$eq\":\"%s\"}}");
			String jsonQuery = String.format(builder.toString(), category);
			builder = new StringBuilder(jsonQuery);
		}
		// TODO : For Future Purpose
		/*
		 * if (null != textSearch) {
		 * builder.append(",").append("{\"textSearch\":{\"$eq\":" + textSearch + "}}");
		 * }
		 */
		builder.append("]}}");
		String jsonQuery = builder.toString();
		try {
			dataSourceModel = dbClient.findDocs(jsonQuery, DataSource.class);
		} catch (Exception e) {
			logger.error("Exception occured while fetching the Data Source Details for : " + namespace + category
					+ textSearch);
			throw new CouchDBException(" Exception occured while finding the document ", e);
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (null == dataSourceModel) {
			throw new DataSourceNotFoundException(
					"DataSource is not available for the given inpu details :  " + namespace + category + textSearch,
					Status.NOT_FOUND.getStatusCode());
		}
		logger.debug("getListofDataSources() End");
		return dataSourceModel;
	}

	/**
	 * To Delete the DataSource
	 * 
	 * @param _id  The document Id
	 * @param _rev The document revisionId
	 * @throws CouchDBException in case of any failure
	 */
	public void deleteDataSource(String _id, String _rev) throws CouchDBException {
		logger.debug("deleteDataSource() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.remove(_id, _rev);
		} catch (Exception e) {
			logger.error("Exception occured while deleting the Document from Couch DB");
			throw new CouchDBException("Exception occured while deleting the Document from Couch DB");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		logger.debug("deleteAssocaitedModel() End");
	}

	/**
	 * To Insert the DataSourceProject Association
	 * 
	 * @param authenticatedUserId The Acumos User Login Id
	 * @param projectId           The Project Id
	 * @param datasourceKey       The DataSourceKey
	 * @param dataSourceModel     The DataSource Model Object
	 * @return returns the DataSourceModel object
	 * @throws CouchDBException in case of any failure
	 */
	public DataSourceAssociationModel insertDataSourceProjectAssociation(String authenticatedUserId, String projectId,
			String datasourceKey, DataSource dataSourceModel) throws CouchDBException{
		logger.debug("insertDataSourceProjectAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		// check the association already exists in couch db or not. by using
		// authenticatedUserId, projectId and datasourceKey
		associationExistsInCouch(authenticatedUserId, projectId, datasourceKey,
				dataSourceModel.getDatasourceId().getVersionId().getLabel(), "ACTIVE");
		DataSourceAssociationModel result = new DataSourceAssociationModel();
		result.setAuthenticatedUserId(authenticatedUserId);
		result.setDataSourceKey(datasourceKey);
		result.setDataSourceVersion(dataSourceModel.getDatasourceId().getVersionId().getLabel());
		result.setCreatedTimestamp(Instant.now().toString());
		result.setUpdatedTimestamp(Instant.now().toString());
		result.setAssociationStatus("ACTIVE");
		result.setProjectId(projectId);
		try {
			Response response = dbClient.save(result);
			result.set_rev(response.getRev());
			result.setAssociationID(response.getId());
		} catch (Exception e) {
			logger.error("Exception occured while saving in DB");
			throw new CouchDBException("Exception occured while saving");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		logger.debug("InsertProjectModelAssociation() End");
		return result;
	}
	
	/**
	 * Check if the Project exists in Association
	 * 
	 * @param authenticatedUserId The Acumos User Login Id
	 * @param projectId           The Project Id
	 * @return  returns the list of DataSourceAssociationModel
	 * @throws AssociationNotFoundException in case of any failure
	 */
	public List<DataSourceAssociationModel> getDataSourcesForProject(String authenticatedUserId, String projectId) throws AssociationNotFoundException {
		logger.debug("getDataSourcesForProject() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSourceAssociationModel> dataSourceModels = null;
		String jsonQuery = String.format(DataSourceConstants.PROJECT_EXISTS_IN_ASSOCIATION_QUERY, authenticatedUserId,
				projectId);
		try {
			dataSourceModels = dbClient.findDocs(jsonQuery, DataSourceAssociationModel.class);
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (dataSourceModels.isEmpty()) {
			throw new AssociationNotFoundException("No DataSource is Associated for the Project : " + projectId);
		}
		logger.debug("getDataSourcesForProject() End");
		return dataSourceModels;
	}

	/**
	 * Get the DataSource project Association Details for a given AssociationId
	 * @param associationId
	 * 			The DataSource project Association Id 
	 * @return
	 * 			returns the DataSourceAssociationModel object
	 * @throws AssociationNotFoundException in case of any failure
	 */
	public DataSourceAssociationModel fetchDataSourceProjectAssociationDetails(String associationId) throws AssociationNotFoundException {
		logger.debug("fetchDataSourceProjectAssociationDetails() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DataSourceAssociationModel result = new DataSourceAssociationModel();
		try {
			result = dbClient.find(DataSourceAssociationModel.class, associationId);
		} catch (Exception e) {
			logger.error("No DataSourceProject Association available for Association Id  : " + associationId);
			throw new AssociationNotFoundException(
					"No DataSourceProject Association available for Association Id  : " + associationId);
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		if (null == result) {
			throw new AssociationNotFoundException(
					"No DataSourceProject Association available for Association Id  : " + associationId);
		}
		logger.debug("fetchDataSourceProjectAssociationDetails() End");
		return result;
	}

	/**
	 * Update the DataSource Project Association Details 
	 * @param oldAssociation
	 * @return DataSourceAssociationModel
	 * @throws CouchDBException in case of any failure
	 */
	public DataSourceAssociationModel updateDataSourceProjectAssociationDetails(
			DataSourceAssociationModel oldAssociation) throws CouchDBException {
		logger.debug("updateDataSourceProjectAssociationDetails() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		Response response = new Response();
		try {
			response = dbClient.update(oldAssociation);
			oldAssociation.set_rev(response.getRev());
			oldAssociation.setAssociationID(response.getId());
		} catch (Exception e) {
			logger.error("Exception occured in updateDataSourceDetails()", e);
			throw new CouchDBException(" Exception occured while updating the record in couch db ");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		logger.debug("updateDataSourceProjectAssociationDetails() End");
		return oldAssociation;
	}

	/**
	 * Delete the DataSource Project Association Details for a AssociationId and Document Revision Id
	 * @param associationId
	 * 			The DataSource Project Association Id
	 * @param _rev
	 * 			The Document Revision Id
	 * @return
	 * 			returns ServiceState object
	 * @throws CouchDBException in case of any failure
	 */
	public ServiceState deleteAssociationDetails(String associationId, String _rev) throws CouchDBException {
		logger.debug("deleteAssociationDetails() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		ServiceState result = new ServiceState();
		try {
			dbClient.remove(associationId, _rev);
		} catch (Exception e) {
			logger.error("Exception occured while Deleting the Association Details", e);
			throw new CouchDBException(" Exception occured while Deleting the Association Details for AssociationId : "
					+ associationId + "and DocumentRevisionId : " + _rev);
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("DataSourceProject Association Deleted Successfully.");
		logger.debug("deleteAssociationDetails() End");
		return result;
	}
	
	/**
	 * Share DataSource To a Collaborator
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param dataSourceKey
	 * 			The DataSource Key
	 * @param collaborators
	 * 			The Collaborator Details
	 * @return
	 * 			returns the DataSourceCollaboratorModel object
	 * @throws CouchDBException in case of any failure
	 */
	public DataSourceCollaboratorModel shareDataSourceToCollaborator(String authenticatedUserId, String dataSourceKey,
			Users collaborators) throws CouchDBException {
		logger.debug("shareDataSourceToCollaborator() Begin");
		DataSourceCollaboratorModel dscModel;
		dscModel = getDataSourceCollaborators(dataSourceKey);
		if (null != dscModel) {
			dscModel = updateDataSourceCollaborator(dscModel, collaborators);
		} else {
			CouchDbClient dbClient = getLightCouchdbClient();
			dscModel = new DataSourceCollaboratorModel();
			HashMap<String, String> dataSourceCollaborator = new HashMap<String, String>();
			Response response = new Response();
			try {
				dscModel.setCreatedTimestamp(Instant.now().toString());
				dscModel.setDataSourceKey(dataSourceKey);
				dscModel.setDataSourceOwner(authenticatedUserId);
				dscModel.setUpdateTimestamp(Instant.now().toString());
				List<User> userList = collaborators.getUsers();
				for (User user : userList) {
					List<Role> rolelist = user.getRoles();
					for (Role role : rolelist) {
						List<Permission> permissionList = role.getPermissions();
						for (Permission permission : permissionList) {
							dataSourceCollaborator.put(user.getUserId().getUuid(),
									permission.getPermission().getName());
						}
					}
				}
				dscModel.setDataSourceCollaborator(dataSourceCollaborator);
				// Save the metaData in Couch DB
				response = dbClient.save(dscModel);
				dscModel.set_id(response.getId());
				dscModel.set_rev(response.getRev());
			} catch (Exception e) {
				logger.error("Exception occured while saving");
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
		}
		logger.debug("shareDataSourceToCollaborator() End");

		return dscModel;
	}
	
	
	/**
	 * Get the DataSource Collaborator Details
	 * @param dataSourceKey
	 * 			The DataSource key
	 * @return
	 * 			returns DataSourceCollaboratorModel object
	 * @throws CouchDBException in case of any failure
	 */
	public DataSourceCollaboratorModel getDataSourceCollaborators(String dataSourceKey) throws CouchDBException {
		logger.debug("getDataSourceCollaborators() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DataSourceCollaboratorModel collaboratorModel = null;
		List<DataSourceCollaboratorModel> collaboratorModelList = new ArrayList<DataSourceCollaboratorModel>();
		try {
			String jsonQuery =String.format(DataSourceConstants.DATA_SOURCE_COLLABORATION, dataSourceKey);
			collaboratorModelList = dbClient.findDocs(jsonQuery, DataSourceCollaboratorModel.class);
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(collaboratorModelList);
			String docString = jsonStr.replace("[", "").replace("]", "");
			if (!docString.isEmpty()) {
				collaboratorModel = mapper.readValue(docString, DataSourceCollaboratorModel.class);
			}
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB", e);
			throw new CouchDBException("Exception occured while finding the documents in couchDB", e);
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client", e);
			}
		}
		logger.debug("getSharedProjects() End");
		return collaboratorModel;
	}
	
	/**
	 * To Delete the Collaborator from the DataSource
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param dataSourceKey
	 * 			The Data Source Id
	 * @param oldDataSourceCollaboratorModel
	 * 			Old DataSourceCollaboratorModel Object
	 * @param collaborators
	 * 			The DataSourceCollaborators object
	 * @return
	 * 			returns updated DataSourceCollaboratorModel
	 * @throws CouchDBException in case of any failure
	 */
	public DataSourceCollaboratorModel removeCollaborator(String authenticatedUserId, String dataSourceKey,
			DataSourceCollaboratorModel oldDataSourceCollaboratorModel, Users collaborators) throws CouchDBException{
		logger.debug("removeCollaborator() Begin");
		CouchDbClient dbClient = null;
		DataSourceCollaboratorModel newDataSourceCollaboratorModel = new DataSourceCollaboratorModel();
		HashMap<String, String> oldCollaboratorDetails = new HashMap<>();
		oldCollaboratorDetails = oldDataSourceCollaboratorModel.getDataSourceCollaborator();
		Iterator<String> iterator = oldCollaboratorDetails.keySet().iterator();
		List<User> userList = collaborators.getUsers();
		try {
			while (iterator.hasNext()) {
				String user = iterator.next();
				for (User s : userList) {
					if (s.getUserId().getUuid().equals(user)) {
						iterator.remove();
						break;
					}
				}
			}
			newDataSourceCollaboratorModel.set_id(oldDataSourceCollaboratorModel.get_id());
			newDataSourceCollaboratorModel.set_rev(oldDataSourceCollaboratorModel.get_rev());
			newDataSourceCollaboratorModel.setCreatedTimestamp(oldDataSourceCollaboratorModel.getCreatedTimestamp());
			newDataSourceCollaboratorModel.setDataSourceCollaborator(oldCollaboratorDetails);
			newDataSourceCollaboratorModel.setDataSourceKey(oldDataSourceCollaboratorModel.getDataSourceKey());
			newDataSourceCollaboratorModel.setDataSourceOwner(oldDataSourceCollaboratorModel.getDataSourceOwner());
			newDataSourceCollaboratorModel.setUpdateTimestamp(Instant.now().toString());
			dbClient = getLightCouchdbClient();
			dbClient.update(newDataSourceCollaboratorModel);
		} catch (Exception e) {
			logger.error("Exception occured while updating the documents in couchDB", e);
			throw new CouchDBException("Exception occured while updating the documents in couchDB", e);
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client", e);
			}
		}
		logger.debug("removeCollaborator() End");
		return newDataSourceCollaboratorModel;
	}

	/**
	 * Get all shared DataSources
	 * @return
	 * 		returns List<DataSourceCollaboratorModel> object
	 * @throws CouchDBException in case of any failure
	 */
	public List<DataSourceCollaboratorModel> sharedDataSources() throws CouchDBException {
		logger.debug("removeCollaborator() Begin");
		CouchDbClient dbClient = null;
		List<DataSourceCollaboratorModel> dataSourceCollaboratorList=null;
		try {
			String jsonQuery = DataSourceConstants.GET_ALL_SHARED_DATASOURCES;
			dbClient = getLightCouchdbClient();
			dataSourceCollaboratorList = dbClient.findDocs(jsonQuery, DataSourceCollaboratorModel.class);
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB", e);
			throw new CouchDBException("Exception occured while finding the documents in couchDB", e);
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client", e);
			}
		}
		return dataSourceCollaboratorList;
	}
	
	/**
	 * Get the DataSource Association Details
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param dataSourceKey
	 * 			The DataSource key
	 * @param dsVersion
	 * 			The DataSource Version
	 * @return 
	 * 			returns DataSourceAssociationModel object
	 * @throws CouchDBException in case of any failure
	 */
	public DataSourceAssociationModel getDataSourceAssociationDetails(String authenticatedUserId, String dataSourceKey, String dsVersion) throws CouchDBException {
		logger.debug("getDataSourceAssociationDetails() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		String jsonQuery =String.format(DataSourceConstants.DATA_SOURCE_ASSOCIATION_DETAILS, authenticatedUserId,dataSourceKey,dsVersion);
		DataSourceAssociationModel associationDetails = null;
		List<DataSourceAssociationModel> collaboratorModelList = new ArrayList<DataSourceAssociationModel>();
		try {
			collaboratorModelList = dbClient.findDocs(jsonQuery, DataSourceAssociationModel.class);
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(collaboratorModelList);
			String docString = jsonStr.replace("[", "").replace("]", "");
			if (!docString.isEmpty()) {
				associationDetails = mapper.readValue(docString, DataSourceAssociationModel.class);
			}
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		logger.debug("getDataSourceAssociationDetails() End");
		return associationDetails;
	}
	
	private void associationExistsInCouch(String authenticatedUserId, String projectId, String datasourceKey,
			String dataSourceVersion, String associationStatus) throws AssociationException{
		logger.debug("associationExistsInCouch() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSourceAssociationModel> dataSourceModels = null;
		String jsonQuery = String.format(DataSourceConstants.ASSOCIATION_EXISTS_IN_COUCH_QUERY, authenticatedUserId,
				projectId, datasourceKey, dataSourceVersion, associationStatus);
		try {
			dataSourceModels = dbClient.findDocs(jsonQuery, DataSourceAssociationModel.class);
		} catch (Exception e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			closeLightCouchdbClient(dbClient);
		}
		// similar association already exists and is ACTIVE
		if (null != dataSourceModels && dataSourceModels.size() > 0) {
			logger.error("Association already exists in Couch DB");
			throw new AssociationException("Association already exists in Couch DB");
		}
		logger.debug("associationExistsInCouch() End");
	}
	
	private DataSourceCollaboratorModel updateDataSourceCollaborator(DataSourceCollaboratorModel dscModel,
			Users collaborators) throws CouchDBException {
		logger.debug("updateDataSourceCollaborator() Begin");
		DataSourceCollaboratorModel model = new DataSourceCollaboratorModel();
		HashMap<String, String> updatedDataSourceCollaborator = dscModel.getDataSourceCollaborator();
		Response response = new Response();
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			model.set_id(dscModel.get_id());
			model.set_rev(dscModel.get_rev());
			model.setCreatedTimestamp(dscModel.getCreatedTimestamp());
			model.setUpdateTimestamp(Instant.now().toString());
			model.setDataSourceKey(dscModel.getDataSourceKey());
			model.setDataSourceOwner(dscModel.getDataSourceOwner());
			List<User> userList = collaborators.getUsers();
			for (User user : userList) {
				List<Role> rolelist = user.getRoles();
				for (Role role : rolelist) {
					List<Permission> permissionList = role.getPermissions();
					for (Permission permission : permissionList) {
						updatedDataSourceCollaborator.put(user.getUserId().getUuid(),
								permission.getPermission().getName());
					}
				}
			}
			model.setDataSourceCollaborator(updatedDataSourceCollaborator);
			// Save the metaData in Couch DB
			response = dbClient.update(model);
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
		logger.debug("updateDataSourceCollaborator() End");
		return model;
		
	}
	
	private CouchDbClient getLightCouchdbClient() {
		return new CouchDbClient(configurationProperties.getCouchDbName(),
				configurationProperties.isCreateIfnotExists(), configurationProperties.getCouchdbProtocol(),
				configurationProperties.getCouchdbHost(), configurationProperties.getCouchdbPort(),
				configurationProperties.getCouchdbUser(), configurationProperties.getCouchdbPwd());
	}

	private void closeLightCouchdbClient(CouchDbClient dbClient) throws CouchDBException {
		if (null != dbClient) {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client", e);
				throw new CouchDBException("IOException occured while closing the lightcouch client", e);
			}
		}
	}

	

	
	

	

	
	


	

	


	

	
	
	



}
