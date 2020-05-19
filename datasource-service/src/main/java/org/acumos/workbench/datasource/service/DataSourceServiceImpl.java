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
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.EntityNotFoundException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.exception.ServiceConnectivityException;
import org.acumos.workbench.datasource.model.CouchConnectionModel;
import org.acumos.workbench.datasource.model.KerberosLogin;
import org.acumos.workbench.datasource.model.MongoDBConnectionModel;
import org.acumos.workbench.datasource.model.MySQLConnectorModel;
import org.acumos.workbench.datasource.util.MLWBCipherSuite;
import org.acumos.workbench.datasource.vo.DataSourceAssociationModel;
import org.acumos.workbench.datasource.vo.KerberosConfigInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("DataSourceServiceImpl")
public class DataSourceServiceImpl implements IDataSourceService{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private CouchDBService couchService;
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	@Qualifier("MySQLDataSourceSvcImpl")
	private IMySQLDataSourceSvc mySQLSvc;
	
	@Autowired
	@Qualifier("MongoDataSourceSvcImpl")
	private IMongoDataSourceSvc mongoSvc;
	
	@Autowired
	@Qualifier("HiveDataSourceSvcImpl")
	private IHiveDataSourceSvc hiveSvc;
	
	@Autowired
	@Qualifier("CouchDataSourceSvcImpl")
	private ICouchDataSourceSvc couchSvc;

	@Autowired
	private ProjectServiceRestClientImpl psClient;
	
	@Override
	public DataSource createDataSource(String authenticatedUserId, DataSource dataSource)
			throws DataSourceNotFoundException, ClassNotFoundException, IOException, SQLException {
		logger.debug("createDataSource() Begin");
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		User owner = new User();
		Identifier identifier = new Identifier();
		identifier.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
		identifier.setIdentifierType(IdentifierType.USER);
		identifier.setUuid(mlpUser.getUserId());
		owner.setUserId(identifier);
		owner.setAuthenticatedUserId(authenticatedUserId);
		dataSource.setOwner(owner);
		String serverName = "";
		if (dataSource.getCommonDetails() != null && dataSource.getCommonDetails().getServerName() != null) {
			serverName = dataSource.getCommonDetails().getServerName();
		}
		Identifier id = new Identifier();
		id.setUuid(dataSource.getCategory() + "_" + authenticatedUserId + "_" + serverName + "_"
				+ Instant.now().toEpochMilli());
		Version version = new Version();
		version.setCreationTimeStamp(Instant.now().toString());
		version.setModifiedTimeStamp(Instant.now().toString());
		version.setLabel(dataSource.getDatasourceId().getVersionId().getLabel());
		id.setVersionId(version);
		id.setName(dataSource.getDatasourceId().getName());
		dataSource.setDatasourceId(id);
		dataSource.setActive(true);
		// check null values in the parameters that are required for connection
		String resultsForConnection = checkDataSourceConnectionStatus(authenticatedUserId, dataSource, null, "create");
		DataSource result = null;
		// Check if the connection status is success then save the data source details in Cocuh db
		if (resultsForConnection.equals("success")) {
			result = couchService.createDataSource(authenticatedUserId, dataSource);
		}
		logger.debug("createDataSource() End");
		return result;
	}


	@Override
	public String checkDataSourceConnectionStatus(String user, DataSource datasource, String dataSourceKey, String mode)
			throws DataSourceNotFoundException, IOException, SQLException, ClassNotFoundException {
		logger.debug("checkDataSourceConnectionStatus() Begin");

		String connectionStatus = "failed";
		boolean isCreate = false;
		boolean isUpdate = false;

		if ("create".equalsIgnoreCase(mode))
			isCreate = true;
		else if ("update".equalsIgnoreCase(mode))
			isUpdate = true;

		// For initially will check for MySQL connection status
		if ("mysql".equals(datasource.getCategory())) {
			logger.debug("checkDataSourcesDetails, connection test for mysql datasource will be initiated");
			MySQLConnectorModel sqlConnectorModel = new MySQLConnectorModel();
			sqlConnectorModel.setHostname(datasource.getCommonDetails().getServerName());
			if (isCreate) {
				sqlConnectorModel.setUserName(datasource.getDbDetails().getDbServerUsername());
				sqlConnectorModel.setPassword(datasource.getDbDetails().getDbServerPassword());
			} else if (isUpdate) {
				Map<String, String> encryptionMap = decryptCreds(datasource);
				if (null != encryptionMap) {
					datasource.getDbDetails().setDbServerUsername(encryptionMap.get("DbServerUsername"));
					datasource.getDbDetails().setDbServerPassword(encryptionMap.get("DbServerPassword"));
				}
				sqlConnectorModel.setUserName(datasource.getDbDetails().getDbServerUsername());
				sqlConnectorModel.setPassword(datasource.getDbDetails().getDbServerPassword());
			}
			sqlConnectorModel.setPort(String.valueOf(datasource.getCommonDetails().getPortNumber()));
			sqlConnectorModel.setDbName(datasource.getDbDetails().getDatabaseName());
			sqlConnectorModel.getMetaData();
			// Get the Connection Status (success/failed) by connecting to MySQL Service
			try {
				connectionStatus = mySQLSvc.getConnectionStatus(sqlConnectorModel,
						datasource.getDbDetails().getDbQuery());
			} catch (Exception e) {
				logger.error("checkDataSourcesDetails, Exception occurred while checking connection : " + e.getMessage());
				throw new ServiceConnectivityException(
						"Check DataSources Details, Exception occurred while checking connection", e);
			}

			// If the connection status is success then update the dataSource db details and
			// metadata
			if (connectionStatus.equals("success") && (isCreate || isUpdate)) {
				Map<String, String> encryptionMap = encryptCreds(datasource);
				if (null != encryptionMap) {
					datasource.getDbDetails().setDbServerUsername(encryptionMap.get("DbServerUsername"));
					datasource.getDbDetails().setDbServerPassword(encryptionMap.get("DbServerPassword"));
				}
				datasource.setMetaData(sqlConnectorModel.getMetaData());
			}

		} else if ("mongo".equals(datasource.getCategory())) {
			logger.debug("checkDataSourcesDetails, connection test for mysql datasource will be initiated");
			MongoDBConnectionModel mongoConnectionModel = new MongoDBConnectionModel();
			mongoConnectionModel.setDbName(datasource.getDbDetails().getDatabaseName());
			mongoConnectionModel.setHostname(datasource.getCommonDetails().getServerName());
			mongoConnectionModel.setPort(datasource.getCommonDetails().getPortNumber());
			mongoConnectionModel.setCollectionName(datasource.getDbDetails().getDbCollectionName());

			if (datasource.getDbDetails().getDbServerPassword() != null
					&& datasource.getDbDetails().getDbServerUsername() != null) {
				if (isCreate) {
					mongoConnectionModel.setUsername(datasource.getDbDetails().getDbServerUsername());
					mongoConnectionModel.setPassword(datasource.getDbDetails().getDbServerPassword());
				} else if (isUpdate) {
					Map<String, String> encryptionMap = decryptCreds(datasource);
					if (null != encryptionMap) {
						datasource.getDbDetails().setDbServerUsername(encryptionMap.get("DbServerUsername"));
						datasource.getDbDetails().setDbServerPassword(encryptionMap.get("DbServerPassword"));
					}
					mongoConnectionModel.setUsername(datasource.getDbDetails().getDbServerUsername());
					mongoConnectionModel.setPassword(datasource.getDbDetails().getDbServerPassword());
				}
			}
			try {
				connectionStatus = mongoSvc.getMongoConnectionStatus(mongoConnectionModel,
						datasource.getDbDetails().getDbQuery());
			} catch (Exception e) {
				logger.error(
						"checkDataSourcesDetails, Exception occurred while checking connection : " + e.getMessage());
				throw new ServiceConnectivityException("Check DataSources Details, Exception occurred while checking connection",e);
			}
			// If the connection status is success then update the dataSource db details and metadata
			if (connectionStatus.equals("success") && (isCreate || isUpdate)
					&& datasource.getDbDetails().getDbServerPassword() != null
					&& datasource.getDbDetails().getDbServerUsername() != null) {
				Map<String, String> encryptionMap = encryptCreds(datasource);
				if (null != encryptionMap) {
					datasource.getDbDetails().setDbServerUsername(encryptionMap.get("DbServerUsername"));
					datasource.getDbDetails().setDbServerPassword(encryptionMap.get("DbServerPassword"));
				}
				datasource.setMetaData(mongoConnectionModel.getMetadata());
			}

		} else if ("couch".equals(datasource.getCategory())) {
			logger.debug("checkDataSourcesDetails, connection test for mysql datasource will be initiated");
			CouchConnectionModel couchConnectionModel = new CouchConnectionModel();
			couchConnectionModel.setDbName(datasource.getDbDetails().getDatabaseName());
			couchConnectionModel.setHostname(datasource.getCommonDetails().getServerName());
			couchConnectionModel.setPort(datasource.getCommonDetails().getPortNumber());
			couchConnectionModel.setProtocol("http");
			couchConnectionModel.setCreateIfnotExists(true);
			
			if (datasource.getDbDetails().getDbServerPassword() != null
					&& datasource.getDbDetails().getDbServerUsername() != null) {
				if (isCreate) {
					couchConnectionModel.setUsername(datasource.getDbDetails().getDbServerUsername());
					couchConnectionModel.setPassword(datasource.getDbDetails().getDbServerPassword());
				} else if (isUpdate) {
					Map<String, String> encryptionMap = decryptCreds(datasource);
					if (null != encryptionMap) {
						datasource.getDbDetails().setDbServerUsername(encryptionMap.get("DbServerUsername"));
						datasource.getDbDetails().setDbServerPassword(encryptionMap.get("DbServerPassword"));
					}
					couchConnectionModel.setUsername(datasource.getDbDetails().getDbServerUsername());
					couchConnectionModel.setPassword(datasource.getDbDetails().getDbServerPassword());
				}
			}
			try {
				connectionStatus = couchSvc.getCouchConnectionStatus(couchConnectionModel, datasource.getDbDetails().getDbQuery());
			} catch (Exception e) {
				logger.error("Check DataSources Details, Exception occurred while checking connection : " + e.getMessage());
				throw new ServiceConnectivityException("Check DataSources Details, Exception occurred while checking connection",e);
			}
			// If the connection status is success then update the dataSource db details and  metadata
			if (connectionStatus.equals("success") && (isCreate || isUpdate)
					&& datasource.getDbDetails().getDbServerPassword() != null
					&& datasource.getDbDetails().getDbServerUsername() != null) {
				Map<String, String> encryptionMap = encryptCreds(datasource);
				if (null != encryptionMap) {
					datasource.getDbDetails().setDbServerUsername(encryptionMap.get("DbServerUsername"));
					datasource.getDbDetails().setDbServerPassword(encryptionMap.get("DbServerPassword"));
				}
				datasource.setMetaData(couchConnectionModel.getMetadata());
			}

		} else if ("hive".equals(datasource.getCategory())) {
			logger.debug("checkDataSourcesDetails, connection test for mysql datasource will be initiated");
			KerberosConfigInfo kerberosConfig = null;

			String kerberosConfigFileName = datasource.getHdfsHiveDetails().getKerberosConfigFileId();
			String kerberosKeyTabFileName = datasource.getHdfsHiveDetails().getKerberosKeyTabFileId();

			// TODO : How to get the Kerberos Config file details by passing kerberosConfigFileName, kerberosKeyTabFileName
			// kerberosConfig = applicationUtilities.getKerberosConfigInfo(kerberosConfigFileName, kerberosKeyTabFileName);

			logger.debug("checkDataSourcesDetails, Received following Kerberos config info from ConfigFile : "
					+ datasource.getHdfsHiveDetails().getKerberosConfigFileId());

			KerberosLogin kerberosLogin = new KerberosLogin();
			kerberosLogin.setKerberosDomainName(kerberosConfig.getDomainName());
			kerberosLogin.setKerberosKdc(kerberosConfig.getKerberosKdc());
			kerberosLogin.setKerberosKeyTabContent(kerberosConfig.getKerberosKeyTabContent());
			kerberosLogin.setKerberosLoginUser(datasource.getHdfsHiveDetails().getKerberosLoginUser());
			kerberosLogin.setKerberosPasswordServer(kerberosConfig.getKerberosPasswordServer());
			kerberosLogin.setKerberosRealms(kerberosConfig.getKerberosRealms());
			kerberosLogin.setKerbersoAdminServer(kerberosConfig.getKerberosAdminServer());
			kerberosLogin.setKerberosKeyTabFileName(kerberosKeyTabFileName);
			kerberosLogin.setKerberosConfigFileName(kerberosConfigFileName);

			try {
				connectionStatus = hiveSvc.getConnectionStatusWithKerberos(kerberosLogin,
						datasource.getCommonDetails().getServerName(),
						String.valueOf(datasource.getCommonDetails().getPortNumber()),
						datasource.getDbDetails().getDbQuery());

			} catch (Exception e) {
				logger.error("Exception occured in getConnectionStatusWithKerberos() of Hive Service", e);
			}
		}
		logger.debug("checkDataSourceConnectionStatus() End");
		return connectionStatus;
	}
	
	@Override
	public DataSource updateDataSourceDetails(String authenticatedUserId, String dataSourceKey,
			DataSource dataSource) throws IOException, DataSourceNotFoundException, ClassNotFoundException, SQLException {
		logger.debug("updateDataSourceDetails() Begin");
		validateDataSource(authenticatedUserId, dataSourceKey);
		dataSource.getDatasourceId().setUuid(dataSourceKey);
		dataSource.getDatasourceId().setIdentifierType(IdentifierType.DATASOURCE);
		//dataSource.setDatasourceId(identifier);
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		User owner = new User();
		Identifier id = new Identifier();
		id.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
		id.setIdentifierType(IdentifierType.USER);
		id.setUuid(mlpUser.getUserId());
		owner.setUserId(id);
		owner.setAuthenticatedUserId(authenticatedUserId);
		// Get the DataSource Details for a datasourceKey and updated the result with corresponding values which is coming from UI.(request)
		DataSource dataSourceModel = couchService.getDataSource(dataSourceKey,dataSource.get_id());
		dataSourceModel.set_id(dataSource.get_id());
		dataSourceModel.setActive(dataSource.isActive());
		dataSourceModel.setReadWriteDescriptor(dataSource.getReadWriteDescriptor());
		dataSourceModel.getDatasourceId().setIdentifierType(IdentifierType.DATASOURCE);
		dataSourceModel.getDatasourceId().getVersionId().setLabel(dataSource.getDatasourceId().getVersionId().getLabel());
		dataSourceModel.getDatasourceId().getVersionId().setModifiedTimeStamp(Instant.now().toString());
		dataSourceModel.getDatasourceId().setName(dataSource.getDatasourceId().getName());
		dataSourceModel.setCategory(dataSource.getCategory());
		dataSourceModel.setDatasourceDescription(dataSource.getDatasourceDescription());
		dataSourceModel.setOwner(owner);
		
		String connectionStatus = checkDataSourceConnectionStatus(authenticatedUserId,dataSource, dataSourceKey, "update");
		if (connectionStatus.equals("success")) {
			dataSource = couchService.updateDataSourceDetails(authenticatedUserId, dataSourceModel);
		}
		logger.debug("updateDataSourceDetails() End");
		return dataSource;
	}
	
	@Override
	public DataSource fetchDataSource(String authenticatedUserId, String dataSourceKey) throws IOException {
		logger.debug("fetchDataSource() Begin");
		validateDataSource(authenticatedUserId, dataSourceKey);
		List<DataSource> dataSourceResult = couchService.getDataSourceForKey(dataSourceKey);
		DataSource dataSourceModel = new DataSource();
		if(null != dataSourceResult && !dataSourceResult.isEmpty()) {
			for(DataSource list : dataSourceResult) {
				dataSourceModel = list;
				break;
			}
		}
		logger.debug("fetchDataSource() End");
		return dataSourceModel;
	}
	
	@Override
	public List<DataSource> fetchDataSourcesList(String authenticatedUserId, String namespace, String category,
			String textSearch) {
		logger.debug("fetchDataSourcesList() Begin");
		List<DataSource> DataSourcesList = couchService.getListofDataSources(authenticatedUserId,namespace,category,textSearch);
		logger.debug("fetchDataSourcesList() End");
		return DataSourcesList;
	}
	
	@Override
	public ServiceState deleteDataSource(String authenticatedUserId, String dataSourceKey) throws IOException {
		logger.debug("deleteDataSource() Begin");
		validateDataSource(authenticatedUserId, dataSourceKey);
		List<DataSource> dataSourceModels = couchService.getDataSourceForKey(dataSourceKey);
		DataSource dataSourceModel = new DataSource();
		String _id = null;
		String _rev = null;
		String dsVersion = null;
		if(null != dataSourceModels && !dataSourceModels.isEmpty()) {
			for(DataSource list : dataSourceModels) {
				dataSourceModel = list;
				_id = dataSourceModel.get_id();
				_rev = dataSourceModel.get_rev();
				dsVersion = dataSourceModel.getDatasourceId().getVersionId().getLabel();
				break;
			}
		}
		// Before deleting the datasource make sure that, datasource is associated to any project or not
		// If its associated then delete the association and delete the datasource
		DataSourceAssociationModel model = couchService.getDataSourceAssociationDetails(authenticatedUserId, dataSourceKey, dsVersion);
		// Delete the DataSource association
		if(null != model) {
			couchService.deleteAssociationDetails(model.getAssociationID(), model.get_rev());
		}
		// Delete the datasource
		couchService.deleteDataSource(_id,_rev);
		ServiceState result = new ServiceState();
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("Data Source Deleted Successfully.");
		logger.debug("deleteDataSource() End");
		return result;
	}


	@Override
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException {
		logger.debug("getUserDetails() Begin");
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", authenticatedUserId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
		RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false, pageRequest);
		List<MLPUser> mlpUsers = response.getContent();
		MLPUser mlpUser = null;
		
		if(null != mlpUsers && mlpUsers.size() > 0) {
			mlpUser = mlpUsers.get(0);
			
		} else {
			logger.warn(authenticatedUserId + " : User not found");
			throw new UserNotFoundException(authenticatedUserId);
		}
		logger.debug("getUserDetails() End");
		return mlpUser;
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
	public DataSourceAssociationModel linkDataSourcetoProject(String authenticatedUserId, String projectId, String datasourceKey,
			DataSource dataSource) throws IOException {
		logger.debug("linkDataSourcetoProject() Begin");
		// Validate the Associating DataSource has the existence in DB or not 
		validateDataSource(authenticatedUserId, datasourceKey);
		List<DataSource> dataSourceModels = couchService.getDataSourceForKey(datasourceKey);
		if (null != dataSourceModels && !dataSourceModels.isEmpty()) {
			for (DataSource dsModel : dataSourceModels) {
				if(dataSource.getDatasourceId().getVersionId().getLabel().equals(dsModel.getDatasourceId().getVersionId().getLabel())) {
					if(null != dataSource.getDatasourceId().getName() && null != dataSource.getCategory() && null != dataSource.getDatasourceId().getVersionId().getLabel()) {
						dsModel.getDatasourceId().setName(dataSource.getDatasourceId().getName());
						dsModel.setCategory(dataSource.getCategory());
						dsModel.getDatasourceId().getVersionId().setLabel(dataSource.getDatasourceId().getVersionId().getLabel());
						dsModel.setActive(dataSource.isActive());
					}
					dsModel.getDatasourceId().setUuid(datasourceKey);
					dataSource = dsModel;
					break;
				}
				
			}
		}
		DataSourceAssociationModel model = couchService.insertDataSourceProjectAssociation(authenticatedUserId,projectId,datasourceKey,dataSource);
		logger.debug("linkDataSourcetoProject() End");
		return model;
		
	}
	
	@Override
	public List<DataSourceAssociationModel> getDataSourceListAssociatedToProject(String authenticatedUserId,
			String projectId) {
		logger.debug("getDataSourceListAssociatedToProject() Begin");
		// Check the projectId is exists in DB Association Table. If not exists throw the Exception
		List<DataSourceAssociationModel> result = couchService.getDataSourcesForProject(authenticatedUserId,projectId);
		logger.debug("getDataSourceListAssociatedToProject() End");
		return result;
	}
	
	@Override
	public DataSourceAssociationModel updateAssociationDetails(String authenticatedUserId, String projectId,String associationId,
			String datasourceKey, DataSource dataSource) {
		logger.debug("updateAssociationDetails() Begin");
		// Check for the Project and DataSourceKey Association exists or not, if exists then only update is allowed. Else throw the exception
		DataSourceAssociationModel oldAssociation = couchService.fetchDataSourceProjectAssociationDetails(associationId);
		oldAssociation.setUpdatedTimestamp(Instant.now().toString());
		oldAssociation.setDataSourceVersion(dataSource.getDatasourceId().getVersionId().getLabel());
		// Just need to update the DataSource Version and UpdatedTimeStamp
		DataSourceAssociationModel latestAssociation=couchService.updateDataSourceProjectAssociationDetails(oldAssociation);
		logger.debug("updateAssociationDetails() End");
		return latestAssociation;
	}

	@Override
	public ServiceState deleteDataSourceAssociationDetails(String authenticatedUserId, String associationId) {
		logger.debug("deleteDataSourceAssociationDetails() Begin");
		DataSourceAssociationModel association = couchService.fetchDataSourceProjectAssociationDetails(associationId);
		ServiceState response = null;
		if(null != association) {
			response = couchService.deleteAssociationDetails(associationId,association.get_rev());
		}
		logger.debug("deleteDataSourceAssociationDetails() End");
		return response;
	}

	@Override
	public void isActiveUser(Users collaborators) {
		logger.debug("isActiveUser() Begin");
		List<User> userList = collaborators.getUsers();
		for (User user : userList) {
			MLPUser mlpUser = null;
			mlpUser = cdsClient.getUser(user.getUserId().getUuid());
			if (mlpUser == null || mlpUser.getUserId().isEmpty()) {
				logger.error("User does not exists");
				throw new UserNotFoundException("Exception occured: User does not Exists ");
			} else if (!mlpUser.isActive()) {
				logger.error("User is not ACTIVE");
				throw new UserNotFoundException("User is not ACTIVE");
			}
			else if (null == user.getRoles()) {
				logger.error("Roles not defined");
				throw new EntityNotFoundException("Roles not defined");
			}
		}
		logger.debug("isActiveUser() End");
	}
	private Map<String, String> encryptCreds(DataSource datasource) {
		logger.debug("encryptCreds() Begin");
		Map<String, String> writeValues = new HashMap<>();
		writeValues.put("DbServerUsername", datasource.getDbDetails().getDbServerUsername());
		writeValues.put("DbServerPassword", datasource.getDbDetails().getDbServerPassword());

		// Convert the DB Server User Name and DB Server User Password into encrypted format
		Map<String, String> encryptionMap = new HashMap<String, String>();

		if (writeValues == null || writeValues.isEmpty()) {
			return null;
		} else {
			String value = null;
			MLWBCipherSuite cipherSuite = new MLWBCipherSuite(datasource.getDatasourceId().getUuid());
			for (String key : writeValues.keySet()) {
				value = writeValues.get(key);
				value = cipherSuite.encrypt(value);
				encryptionMap.put(key, value);
			}
		}
		logger.debug("encryptCreds() End");
		return encryptionMap;
	}
	
	private Map<String, String> decryptCreds(DataSource datasource) {
		logger.debug("decryptCreds() Begin");
		Map<String, String> writeValues = new HashMap<>();
		writeValues.put("DbServerUsername", datasource.getDbDetails().getDbServerUsername());
		writeValues.put("DbServerPassword", datasource.getDbDetails().getDbServerPassword());
		// Convert the DB Server User Name and DB Server User Password into encrypted
		// format
		Map<String, String> decryptionMap = new HashMap<String, String>();

		if (writeValues == null || writeValues.isEmpty()) {
			return null;
		} else {
			String value = null;
			MLWBCipherSuite cipherSuite = new MLWBCipherSuite(datasource.getDatasourceId().getUuid());
			for (String key : writeValues.keySet()) {
				value = writeValues.get(key);
				value = cipherSuite.decrypt(value);
				decryptionMap.put(key, value);
			}
		}
		logger.debug("decryptCreds() End");
		return decryptionMap;
	}

	private void validateDataSource(String authenticatedUserId, String dataSourceKey) throws IOException {
		logger.debug("validateDataSource() Begin");
		boolean isValid = false;
		isValid = couchService.isValidDataSource(authenticatedUserId, dataSourceKey);
		if (!isValid) {
			if (couchService.isDataSourceExists(dataSourceKey)) {
				throw new DataSourceNotFoundException("please check dataset key provided and user permission for this operation", Status.UNAUTHORIZED.getStatusCode());
			} else {
				throw new DataSourceNotFoundException("Invalid data. No Datasource info. Please send valid dataSourceKey.", Status.NOT_FOUND.getStatusCode());
			}
		}
		logger.debug("validateDataSource() End");
	}


	


	


	


	

}
