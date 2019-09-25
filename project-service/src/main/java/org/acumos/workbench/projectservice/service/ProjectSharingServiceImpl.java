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
import org.acumos.workbench.common.vo.Permission;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Role;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
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
	public Project ShareProject(String authenticatedUserId, String projectId, Users collaborators) {
		logger.debug("ShareProject() Begin");
		DatasetCollaborator datasetCollaborator = couchDBService.shareProject(authenticatedUserId, projectId,collaborators);
		MLPProject mlpProject = null;
		MLPUser mlpUser = null;
		Project result = null;
		List<User> userList = new ArrayList<User>();
		User user = null;
		mlpProject = cdsClient.getProject(projectId);
		mlpUser = projectServiceImpl.getUserDetails(authenticatedUserId);
		if (null != mlpProject && null != mlpUser) {
			result = ProjectServiceUtil.getProjectVO(mlpProject, mlpUser);
			List<User> usersList = collaborators.getUsers();
			MLPUser response = null;
			Identifier userId  = null;
			for (User iterateUser : usersList) {
				iterateUser.getUserId().getUuid();
				response = cdsClient.getUser(iterateUser.getUserId().getUuid());
				userId = new Identifier();
				userId.setUuid(response.getUserId());
				userId.setName(response.getLoginName());
				user = new User();
				user.setUserId(userId);
				user.setRoles(iterateUser.getRoles());
				userList.add(user);
			}
			collaborators.setUsers(userList);
			result.setCollaborators(collaborators);
		}
		logger.debug("ShareProject() End");
		return result;
	}
	@Override
	public Project removeCollaborator(String authenticatedUserId, String projectId, Users collaborators) {
		logger.debug("removeCollaborator() Begin");
		DatasetCollaborator oldDataSetCollaborator = null;
		Project result = null;
		oldDataSetCollaborator = couchDBService.getCollaboratorDocument(authenticatedUserId, projectId);
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

	private Project getSharedProjectsVO(String authenticatedUserId, String projectId,DatasetCollaborator newDatasetCollaborator) {
		logger.debug("getSharedProjectsVO() Begin");
		MLPProject mlpProject = null;
		MLPUser mlpUser = null;
		Project result = null;
		mlpProject = cdsClient.getProject(projectId);
		mlpUser = projectServiceImpl.getUserDetails(authenticatedUserId);
		if (null != mlpProject && null != mlpUser) {
			result = ProjectServiceUtil.getProjectVO(mlpProject, mlpUser);
			Users updatedUsers = new Users();
			HashMap<String, String> updatedProjectCollaborator = newDatasetCollaborator.getProjectCollaborator();
			List<User> users = new ArrayList<User>();
			List<Role> roles = new ArrayList<Role>();
			Permission permission = new Permission();
			Role role = new Role();
			List<Permission> permissions = new ArrayList<Permission>();
			Set<String> keySet = updatedProjectCollaborator.keySet();
			for (String userKey : keySet) {
				MLPUser collaborator = cdsClient.getUser(userKey);
				User user = new User();
				Identifier userId = new Identifier();
				userId.setIdentifierType(IdentifierType.PROJECT);
				userId.setUuid(userKey);
				userId.setName(collaborator.getLoginName());
				user.setUserId(userId);
				Identifier permissionName = new Identifier();
				permissionName.setName(updatedProjectCollaborator.get(userKey));
				permission.setPermission(permissionName);
				permissions.add(permission);
				role.setPermissions(permissions);
				roles.add(role);
				user.setRoles(roles);
				users.add(user);
			}
			updatedUsers.setUsers(users);
			result.setCollaborators(updatedUsers);
		}
		logger.debug("getSharedProjectsVO() End");
		return result;
	}
}
