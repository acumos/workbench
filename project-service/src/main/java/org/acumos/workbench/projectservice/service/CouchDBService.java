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

package org.acumos.workbench.projectservice.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.acumos.workbench.common.vo.Permission;
import org.acumos.workbench.common.vo.Role;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.projectservice.exception.CouchDBException;
import org.acumos.workbench.projectservice.lightcouch.DatasetCollaborator;
import org.acumos.workbench.projectservice.util.ConfigurationProperties;
import org.acumos.workbench.projectservice.util.ProjectServiceConstants;
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
	private ConfigurationProperties configurationProperties;

	public DatasetCollaborator getCollaborator(String projectId) {
		logger.debug("getCollaborator() Begin");
		CouchDbClient dbClient = null;
		List<DatasetCollaborator> documentObj= null;
		DatasetCollaborator datasetCollaborator=new DatasetCollaborator(); ;
		try {
			String jsonQuery = String.format(ProjectServiceConstants.GETCOLLABORATOR,projectId);
			System.out.println(jsonQuery);

			dbClient = getLightCouchdbClient();
			documentObj= dbClient.findDocs(jsonQuery, DatasetCollaborator.class);
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(documentObj);
			String modelString = jsonStr.replace("[", "").replace("]", "");
			if (!modelString.isEmpty()) {
				datasetCollaborator = mapper.readValue(modelString, DatasetCollaborator.class);
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
		

		logger.debug("getCollaborator() End");
		return datasetCollaborator;

	}
	
	public DatasetCollaborator removeCollaborator(String authenticatedUserId, String projectId,DatasetCollaborator olddatasetCollaborator,Users collaborators) {
		logger.debug("removeCollaborator() Begin");
		CouchDbClient dbClient = null;
		DatasetCollaborator newDatasetCollaborator = new DatasetCollaborator();
		HashMap<String, String> oldprojectCollaborator = new HashMap<>();
		oldprojectCollaborator = olddatasetCollaborator.getProjectCollaborator();
		Set<String> keySet = oldprojectCollaborator.keySet();
		try {
			for (String userKey : keySet) {
				Iterator<String> iterator = oldprojectCollaborator.keySet().iterator();
				while (iterator.hasNext()) {
					String user = iterator.next();
					if (user.contains(userKey)) {
						iterator.remove();
					}
				}
			}
			newDatasetCollaborator.set_id(olddatasetCollaborator.get_id());
			newDatasetCollaborator.set_rev(olddatasetCollaborator.get_rev());
			newDatasetCollaborator.setCreatedTimestamp(olddatasetCollaborator.getCreatedTimestamp());
			newDatasetCollaborator.setUpdateTimestamp(Instant.now().toString());
			newDatasetCollaborator.setProjectId(olddatasetCollaborator.getProjectId());
			newDatasetCollaborator.setProjectOwner(olddatasetCollaborator.getProjectOwner());
			newDatasetCollaborator.setProjectCollaborator(oldprojectCollaborator);
			dbClient = getLightCouchdbClient();
			dbClient.update(newDatasetCollaborator);
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
		logger.debug("removeCollaborator() End");
		return newDatasetCollaborator;

	}

	public DatasetCollaborator getCollaboratorDocument(String authenticatedUserId,String projectId ){
		logger.debug("getCollaboratorDocument() Begin");
		CouchDbClient dbClient = null;
		Object documentObj = null;
		DatasetCollaborator datasetCollaborator=new DatasetCollaborator() ;

		try {
			String jsonQuery = String.format(ProjectServiceConstants.GETCOLLABORATORDOCUMENT, projectId);
			dbClient = getLightCouchdbClient();
			documentObj = dbClient.findDocs(jsonQuery, DatasetCollaborator.class);
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(documentObj);
			String docString = jsonStr.replace("[", "").replace("]", "");
			datasetCollaborator = mapper.readValue(docString, DatasetCollaborator.class);
			
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
		logger.debug("getCollaboratorDocument() End");
		return datasetCollaborator;
		
	}

	public DatasetCollaborator shareProject(String authenticatedUserId, String projectId, Users collaborators) {
		logger.debug("shareProject() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DatasetCollaborator datasetCollaborator = new DatasetCollaborator();
		HashMap<String, String> projectCollaborator = new HashMap<String, String>();
		Response response = new Response();
		try {
			datasetCollaborator.setCreatedTimestamp(Instant.now().toString());
			datasetCollaborator.setUpdateTimestamp(Instant.now().toString());
			datasetCollaborator.setProjectId(projectId);
			datasetCollaborator.setProjectOwner(authenticatedUserId);
			List<User> userList = collaborators.getUsers();
			for (User user : userList) {
				List<Role> rolelist = user.getRoles();
				for (Role role : rolelist) {
					List<Permission> permissionList = role.getPermissions();
					for (Permission permission : permissionList) {
						projectCollaborator.put(user.getUserId().getUuid(), permission.getPermission().getName());
					}
				}
			}
			datasetCollaborator.setProjectCollaborator(projectCollaborator);
			// Save the metaData in Couch DB
			response = dbClient.save(datasetCollaborator);
			System.out.println("RESPONSE ID---"+ response.getId());
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
		return datasetCollaborator;
	}

	public List<DatasetCollaborator> getSharedProjects(String authenticatedUserId) {
		logger.debug("getSharedProjects() Begin");
		CouchDbClient dbClient = null;
		List<DatasetCollaborator> datasetCollaboratorList=null;
		try {
			String jsonQuery = String.format(ProjectServiceConstants.GETSHAREDPROJECTS, authenticatedUserId);
			dbClient = getLightCouchdbClient();
			datasetCollaboratorList = dbClient.findDocs(jsonQuery, DatasetCollaborator.class);
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
		return datasetCollaboratorList;
		
	}

	private CouchDbClient getLightCouchdbClient() {
		logger.debug("getLightCouchdbClient() Begin");
		return new CouchDbClient(configurationProperties.getCouchDbName(),
				configurationProperties.isCreateIfnotExists(), configurationProperties.getCouchdbProtocol(),
				configurationProperties.getCouchdbHost(), configurationProperties.getCouchdbPort(),
				configurationProperties.getCouchdbUser(), configurationProperties.getCouchdbPwd());
	}
}
