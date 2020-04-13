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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Permission;
import org.acumos.workbench.common.vo.Role;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.datasource.exception.CollaboratorExistsException;
import org.acumos.workbench.datasource.vo.DataSourceCollaboratorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("DSSharingServiceImpl")
public class DSSharingServiceImpl implements IDSSharingService{
	
	@Autowired
	CouchDBService couchDBService;
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	@Qualifier("DataSourceServiceImpl")
	private IDataSourceService dataSourceService;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Override
	public void collaboratorExists(String dataSourceKey, Users collaborators) throws CollaboratorExistsException {
		logger.debug("collaboratorExists() Begin");
		HashMap<String, String> collaboratorList = null;
		List<User> userList = collaborators.getUsers();
		DataSourceCollaboratorModel model = couchDBService.getDataSourceCollaborators(dataSourceKey);
		if (null != model && null != model.getDataSourceCollaborator()) {
			for (User user : userList) {
				collaboratorList = model.getDataSourceCollaborator();
				if (collaboratorList.containsKey(user.getUserId().getUuid())) {
					logger.error("Collaborator already Exists");
					throw new CollaboratorExistsException("Collaborator already Exists");
				}
			}
		}
		logger.debug("collaboratorExists() End");
	}

	@Override
	public DataSource shareDataSource(String authenticatedUserId, String dataSourceKey,
			Users collaborators,DataSource dataSource) {
		logger.debug("shareDataSource() Begin");
		DataSourceCollaboratorModel response = couchDBService.shareDataSourceToCollaborator(authenticatedUserId,dataSourceKey,collaborators);
		Users updatedUsers = convertDataSourceVO(response);
		dataSource.setUsers(updatedUsers);
		logger.debug("shareDataSource() End");
		return dataSource;
	}

	@Override
	public void isCollaboratorRemovable(String dataSourceKey, Users collaborators) throws CollaboratorExistsException {
		logger.debug("isCollaboratorRemovable() Begin");
		HashMap<String, String> collaboratorList = null;
		List<User> userList = collaborators.getUsers();
		DataSourceCollaboratorModel model = couchDBService.getDataSourceCollaborators(dataSourceKey);
		if (null != model && null != model.getDataSourceCollaborator()) {
			for (User user : userList) {
				collaboratorList = model.getDataSourceCollaborator();
				if (!collaboratorList.containsKey(user.getUserId().getUuid())) {
					logger.error("User is not a collaborator");
					throw new CollaboratorExistsException("User is not a collaborator");
				}
			}
		}
		logger.debug("isCollaboratorRemovable() End");
	}

	@Override
	public DataSource removeCollaborator(String authenticatedUserId, String dataSourceKey, Users collaborators,DataSource datasource) {
		logger.debug("removeCollaborator() Begin");
		DataSourceCollaboratorModel oldDataSourceCollaboratorModel = null;
		// Get the old DataSourceCollaboratorModel details by using datasource key
		oldDataSourceCollaboratorModel = couchDBService.getDataSourceCollaborators(dataSourceKey);
		// Remove the collaborator
		DataSourceCollaboratorModel newDataSourceCollaboratorModel = couchDBService.removeCollaborator(authenticatedUserId,dataSourceKey,oldDataSourceCollaboratorModel,collaborators);
		// Convert the DataSourceCollaboratorModel into DataSource Object type
		Users users = convertDataSourceVO(newDataSourceCollaboratorModel);
		// Set the Updated the User details into DataSource object
		datasource.setUsers(users);
		logger.debug("removeCollaborator() End");
		return datasource;
	}
	
	@Override
	public List<DataSource> getSharedDataSources(String authenticatedUserId) {
		logger.debug("getSharedDataSources() Begin");
		List<DataSource> dataSourceVOList = new ArrayList<DataSource>();
		Set<String> collaboratorList = null;
		MLPUser loginUser = null;
		loginUser = dataSourceService.getUserDetails(authenticatedUserId);
		List<DataSourceCollaboratorModel> dataSourceList = couchDBService.sharedDataSources();
		for(DataSourceCollaboratorModel dataSourceDetails : dataSourceList) {
			collaboratorList = dataSourceDetails.getDataSourceCollaborator().keySet();
			for(String collaborator : collaboratorList) {
				// call to DataSource Couch DB getDataSources for a particular datasourcekey
				if(collaborator.equals(loginUser.getUserId())) {
					List<DataSource> list = couchDBService.getDataSourceForKey(dataSourceDetails.getDataSourceKey());
					dataSourceVOList.addAll(list);
				}
			}
		}
		logger.debug("getSharedDataSources() End");
		return dataSourceVOList;
	}

	
	private Users convertDataSourceVO(DataSourceCollaboratorModel response) {
		HashMap<String, String> datasourceCollaborator = response.getDataSourceCollaborator();
		Users updatedUsers = new Users();
		Set<String> keySet = datasourceCollaborator.keySet();
		List<User> users = new ArrayList<User>();
		for (String userKey : keySet) {
			MLPUser mlpCollaborator = cdsClient.getUser(userKey);
			Identifier userIdentifier = new Identifier();
			userIdentifier.setIdentifierType(IdentifierType.USER);
			userIdentifier.setUuid(userKey);
			KVPairs metrics=new KVPairs();
			List<KVPair> kv=new ArrayList<KVPair>();
			KVPair firstName=new KVPair();
			KVPair lastName=new KVPair();
			firstName.setKey("firstname");
			firstName.setValue(mlpCollaborator.getFirstName());
			kv.add(firstName);
			lastName.setKey("lastname");
			lastName.setValue(mlpCollaborator.getLastName());
			kv.add(lastName);
			metrics.setKv(kv);
			userIdentifier.setMetrics(metrics);
			userIdentifier.setName(mlpCollaborator.getLoginName());
			List<Role> roles = new ArrayList<Role>();
			List<Permission> permissions = new ArrayList<Permission>();
			Permission permission = new Permission();
			Identifier Identifierpermission = new Identifier();
			Identifierpermission.setIdentifierType(IdentifierType.USER);
			Identifierpermission.setName(datasourceCollaborator.get(userKey));
			permission.setPermission(Identifierpermission);
			permissions.add(permission);
			Role roleList = new Role();
			roleList.setPermissions(permissions);
			roles.add(roleList);
			User user = new User();
			user.setUserId(userIdentifier);
			user.setAuthenticatedUserId(mlpCollaborator.getLoginName());
			user.setRoles(roles);
			users.add(user);
		}
		updatedUsers.setUsers(users);
		return updatedUsers;
	}

}
