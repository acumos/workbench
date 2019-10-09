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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Permission;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Role;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.projectservice.exception.DuplicateCollaboratorException;
import org.acumos.workbench.projectservice.lightcouch.DatasetCollaborator;
import org.acumos.workbench.projectservice.util.ProjectServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service("ProjectSharingServiceImpl")
public class ProjectSharingServiceImpl implements ProjectSharingService{
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	@Autowired
	CouchDBService couchDBService;
	@Autowired
	ProjectServiceImpl projectServiceImpl;
	
	@Override
	public Project shareProject(String authenticatedUserId, String projectId, Users collaborators) {
		logger.debug("ShareProject() Begin");
		DatasetCollaborator datasetCollaborator = couchDBService.saveProjectCollaboration(authenticatedUserId, projectId,collaborators);
		MLPProject mlpProject = null;
		MLPUser mlpUser = null;
		Project result = null;
		mlpProject = cdsClient.getProject(projectId);
		mlpUser = projectServiceImpl.getUserDetails(authenticatedUserId);
		if (null != mlpProject && null != mlpUser) {
			result=	getSharedProjectsVO(authenticatedUserId, projectId, datasetCollaborator);
		}
		logger.debug("ShareProject() End");
		return result;
	}
	@Override
	public Project removeCollaborator(String authenticatedUserId, String projectId, Users collaborators) {
		logger.debug("removeCollaborator() Begin");
		DatasetCollaborator oldDataSetCollaborator = null;
		Project result = null;
		oldDataSetCollaborator = couchDBService.getProjectCollaboration(projectId);
		DatasetCollaborator newDatasetCollaborator = couchDBService.removeCollaborator(authenticatedUserId, projectId,oldDataSetCollaborator, collaborators);
		result = getSharedProjectsVO(authenticatedUserId, projectId, newDatasetCollaborator);
		logger.debug("removeCollaborator() End");
		return result;
	}
	@Override
	public List<Project> getSharedProjects(String authenticatedUserId) {
		logger.debug("getSharedProjects() Begin");
		Project result = null;
		List<Project> projectVOList = new ArrayList<Project>();
		Set<String> collaboratorList = null;
		MLPUser mlpUser = null;
		MLPProject mlpProject = null;
		MLPUser loginUser = null;
		loginUser = projectServiceImpl.getUserDetails(authenticatedUserId);
		List<DatasetCollaborator> datasetCollaboratorList = couchDBService.getSharedProjects();
		for (DatasetCollaborator datasetCollaborator : datasetCollaboratorList) {
			collaboratorList = datasetCollaborator.getProjectCollaborator().keySet();
			for (String collaborator : collaboratorList) {
				if (collaborator.equals(loginUser.getUserId())) {
					mlpProject = cdsClient.getProject(datasetCollaborator.getProjectId());
					mlpUser = cdsClient.getUser(mlpProject.getUserId());
					result = ProjectServiceUtil.getProjectVO(mlpProject, mlpUser);
					projectVOList.add(result);
				}
			}
		}
		logger.debug("getSharedProjects() End");
		return projectVOList;
	}
	@Override
	public void isCollaboratorExists(Users collaborators, String projectId) {
		HashMap<String, String> collaboratorList = null;
		List<User> userList = collaborators.getUsers();
		DatasetCollaborator datasetCollaborator = couchDBService.getProjectCollaboration(projectId);
		if (null != datasetCollaborator && null != datasetCollaborator.getProjectCollaborator()) {
			for (User user : userList) {
				collaboratorList = datasetCollaborator.getProjectCollaborator();
				if (collaboratorList.containsKey(user.getUserId().getUuid())) {
					logger.error("Collaborator already Exists");
					throw new DuplicateCollaboratorException("Collaborator already Exists");
				}
			}
		}
		logger.debug("isCollaboratorExists() End");
	}

	/**
	 * To the Project VO for Including collaborators
	 * 
	 * @param authenticatedUserId
	 * @param projectId
	 * @param newDatasetCollaborator
	 * @return Project pbject
	 */
	
	public Project getSharedProjectsVO(String authenticatedUserId, String projectId,DatasetCollaborator newDatasetCollaborator) {
		logger.debug("getSharedProjectsVO() Begin");
		MLPProject mlpProject = null;
		MLPUser mlpProjectOwner = null;
		Project result = null;
		mlpProject = cdsClient.getProject(projectId);
		 mlpProjectOwner=cdsClient.getUser(mlpProject.getUserId());
		if (null != mlpProject && null != mlpProjectOwner) {
			result = ProjectServiceUtil.getProjectVO(mlpProject, mlpProjectOwner);
			if (null != newDatasetCollaborator) {
				HashMap<String, String> updatedProjectCollaborator = newDatasetCollaborator.getProjectCollaborator();
				Users updatedUsers = new Users();
				Set<String> keySet = updatedProjectCollaborator.keySet();
				List<User> users = new ArrayList<User>();
				for (String userKey : keySet) {
					MLPUser mlpCollaborator = cdsClient.getUser(userKey);
					Identifier userIdentifier = new Identifier();
					userIdentifier.setIdentifierType(IdentifierType.PROJECT);
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
					Identifierpermission.setIdentifierType(IdentifierType.PROJECT);
					Identifierpermission.setName(updatedProjectCollaborator.get(userKey));
					permission.setPermission(Identifierpermission);
					permissions.add(permission);
					Role roleList = new Role();
					roleList.setPermissions(permissions);
					roles.add(roleList);
					User user = new User();
					user.setUserId(userIdentifier);
					user.setRoles(roles);
					users.add(user);
				}
				updatedUsers.setUsers(users);
				result.setCollaborators(updatedUsers);
			}
		}
		logger.debug("getSharedProjectsVO() End");
		return result;
	}
}
