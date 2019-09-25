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
import java.util.HashMap;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.projectservice.exception.DuplicateCollaboratorException;
import org.acumos.workbench.projectservice.exception.ProjectNotActiveException;
import org.acumos.workbench.projectservice.lightcouch.DatasetCollaborator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service("ProjectSharingValidationSercviceImpl")
public class ProjectSharingValidationSercviceImpl implements ProjectSharingValidationService {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	ProjectServiceImpl projectServiceImpl;
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	CouchDBService couchDBService;

	@Override
	public void isUserExists(String authenticatedUserId) {
		logger.debug("isUserExists() Begin");
		MLPUser mlpUser = null;
		mlpUser = projectServiceImpl.getUserDetails(authenticatedUserId);
		if (mlpUser == null || mlpUser.getUserId().isEmpty()) {
			logger.error("User does not exists");
			throw new UserNotFoundException("Exception occured: User does not Exists ");
		} 
		
		logger.debug("isUserExists() End");

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
		
		}
		logger.debug("isActiveUser() End");

	}

	@Override
	public void isProjectExists(String projectId) {
		logger.debug("isProjectExists() Begin");
		MLPProject mlpProject = cdsClient.getProject(projectId);
		if (null == mlpProject) {
			throw new ProjectNotFoundException();
		} else if (!mlpProject.isActive()) {
			logger.error("Project is not Active");
			throw new ProjectNotActiveException("Project is not Active");
		}

		logger.debug("isProjectExists() End");

	}

	@Override
	public void isCollaboratorExists(Users collaborators, String projectId) {
		HashMap<String, String> collaboratorList=null;
		List<User> userList = collaborators.getUsers();
		DatasetCollaborator datasetCollaborator = couchDBService.getCollaborator(projectId);
		if (null !=datasetCollaborator && null !=datasetCollaborator.getProjectCollaborator() ) {
			
			for (User user : userList) {
				collaboratorList=datasetCollaborator.getProjectCollaborator();
				if (collaboratorList.containsKey(user.getUserId().getUuid())) {
					logger.error("Collaborator already Exists");
					throw new DuplicateCollaboratorException("Collaborator already Exists");
				}
		}
		}
		logger.debug("isCollaboratorExists() End");
	}

	@Override
	public void isOwnerOfProject(String authenticatedUserId, String projectId) {
		logger.debug("isOwnerOfProject() Begin");
		projectServiceImpl.isOwnerOfProject(authenticatedUserId, projectId);
		logger.debug("isOwnerOfProject() End");
	}

	@Override
	public void isCollaboratorRemovable(Users collaborators, String projectId) {
		logger.debug("isCollaboratorRemovable() Begin");
		HashMap<String, String> collaboratorList = null;
		List<User> userList = collaborators.getUsers();
		DatasetCollaborator datasetCollaborator = couchDBService.getCollaborator(projectId);
		for (User user : userList) {
			collaboratorList = datasetCollaborator.getProjectCollaborator();
			if (!collaboratorList.containsKey(user.getUserId().getUuid())) {
				logger.error("User is not a collaborator");
				throw new UserNotFoundException("User is not a collaborator");
			}
		}
		logger.debug("isCollaboratorRemovable() End");

	}
}
