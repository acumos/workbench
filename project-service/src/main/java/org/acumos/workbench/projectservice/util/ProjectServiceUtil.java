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

package org.acumos.workbench.projectservice.util;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.projectservice.service.CouchDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
public class ProjectServiceUtil {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Converts Project View Object input to MLPProject CDS Domain Object.
	 * 
	 * @param userId
	 * 		the User Id 
	 * @param project
	 * 		the Project object
	 * @return MLPProject object
	 * 
	 */
	public static MLPProject getMLPProject(String userId, Project project) {
		logger.debug("getMLPProject() Begin");
		//TODO : To implement Null check for each field. 
		MLPProject mlpProject = null;
		if(null != project) {
			mlpProject = new MLPProject();
			mlpProject.setActive(true);
			Identifier projectIdentifier = project.getProjectId();
			if(null != projectIdentifier) { 
				mlpProject.setName(projectIdentifier.getName());
			}
			
			mlpProject.setDescription(project.getDescription());
			mlpProject.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
			mlpProject.setUserId(userId);
			mlpProject.setVersion(project.getProjectId().getVersionId().getLabel());
		}
		logger.debug("getMLPProject() End");
		return mlpProject;
	}
	
	/**
	 * Converts MLPProject CDS Domain input Object to Project View Object
	 * 
	 * @param mlpProject
	 * 		CDS MLPProject object
	 * @return Project
	 * 		returns Project view object.
	 */
	public static Project getProjectVO(MLPProject mlpProject, MLPUser mlpUser) {
		logger.debug("getProjectVO() Begin");
		//TODO : Include null checks
		Project result = new Project(); 
		
		Identifier userIdentifier = new Identifier();
		userIdentifier.setIdentifierType(IdentifierType.USER);
		userIdentifier.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
		userIdentifier.setUuid(mlpUser.getUserId());
		User owner = new User();
		owner.setAuthenticatedUserId(mlpUser.getLoginName());
		owner.setUserId(userIdentifier);
		Identifier projectIdentifier = new Identifier();
		Version version = new Version();
		Timestamp timestamp =  null;
		if(null == mlpProject.getModified()) { 
			timestamp = Timestamp.from(mlpProject.getCreated());
			version.setModifiedTimeStamp(timestamp.toString());
		} else { 
			timestamp = Timestamp.from(mlpProject.getModified());
			version.setModifiedTimeStamp(timestamp.toString());
		}
		version.setCreationTimeStamp(mlpProject.getCreated().toString());
		version.setUser(mlpProject.getUserId());
		version.setLabel(mlpProject.getVersion());
		projectIdentifier.setIdentifierType(IdentifierType.PROJECT);
		projectIdentifier.setUuid(mlpProject.getProjectId());
		projectIdentifier.setName(mlpProject.getName());
		projectIdentifier.setRepositoryUrl(mlpProject.getRepositoryUrl());
		//projectIdentifier.setServiceUrl(responseMLPProject.get);
		projectIdentifier.setVersionId(version);
		result.setProjectId(projectIdentifier);
		result.setDescription(mlpProject.getDescription());
		result.setOwner(owner);
		ArtifactState artifactStatus = new ArtifactState();
		if(mlpProject.isActive()) { 
			artifactStatus.setStatus(ArtifactStatus.ACTIVE);
		} else { 
			artifactStatus.setStatus(ArtifactStatus.ARCHIVED);
		}
		result.setArtifactStatus(artifactStatus);
		ServiceState serviceStatus = new ServiceState();
		serviceStatus.setStatus(ServiceStatus.get(mlpProject.getServiceStatusCode()));
		result.setServiceStatus(serviceStatus);
		//Getting collaborator for Project from couchdb
		
		
		logger.debug("getProjectVO() End");
		return result;
	}

	/**
	 * To get the list of Project for corresponding input list of MLPProjects.
	 * @param mlpProjects
	 * 		the mlpProjects list
	 * @return List<Project>
	 * 		return list of Projects.
	 */
	public static List<Project> getMLPProjects(List<MLPProject> mlpProjects, MLPUser mlpUser) {
		logger.debug("getMLPProjects() Begin");
		List<Project> result = new ArrayList<Project>();
		
		for(MLPProject mlpProject : mlpProjects) {
			result.add(getProjectVO(mlpProject, mlpUser));
		}
		logger.debug("getMLPProjects() End");
		return result;
	}

	public static MLPProject updateMLPProject(MLPProject mlpProject, Project project) {
		logger.debug("updateMLPProject() Begin");
		MLPProject result = mlpProject;
		if(null != project) {
			ArtifactState artifactStatus =  project.getArtifactStatus();
			if(null != artifactStatus) { 
				if(artifactStatus.getStatus().equals(ArtifactStatus.ARCHIVED)) {
					result.setActive(false);
				} else {
					result.setActive(true);
				}
			}
			Identifier projectIdentifier = project.getProjectId();
			if(null != projectIdentifier) { 
				result.setName(projectIdentifier.getName());
				result.setRepositoryUrl(projectIdentifier.getServiceUrl());
				result.setVersion(projectIdentifier.getVersionId().getLabel());
			}
			result.setDescription(project.getDescription());
			//TODO Check value and then set serviceStatusCode.
			//mlpProject.setServiceStatusCode("");
			mlpProject.setModified(Instant.now());
		}
		logger.debug("updateMLPProject() End");
		return result;
	}
}
