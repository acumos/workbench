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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.projectservice.exception.ArchivedException;
import org.acumos.workbench.projectservice.exception.DuplicateProjectException;
import org.acumos.workbench.projectservice.exception.NotOwnerException;
import org.acumos.workbench.projectservice.exception.ProjectNotFoundException;
import org.acumos.workbench.projectservice.exception.UserNotFoundException;
import org.acumos.workbench.projectservice.util.ArtifactStatus;
import org.acumos.workbench.projectservice.util.ConfigurationProperties;
import org.acumos.workbench.projectservice.util.ProjectServiceUtil;
import org.acumos.workbench.projectservice.util.ServiceStatus;
import org.acumos.workbench.projectservice.vo.ArtifactState;
import org.acumos.workbench.projectservice.vo.Identifier;
import org.acumos.workbench.projectservice.vo.Project;
import org.acumos.workbench.projectservice.vo.ServiceState;
import org.acumos.workbench.projectservice.vo.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ProjectServiceImpl")
public class ProjectServiceImpl implements ProjectService {

	
	@Autowired
	CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	ConfigurationProperties confprops;
	
	@Override
	public void projectExists(String authenticatedUserId,Project project) throws DuplicateProjectException { 
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		//  CDS call to check if project-version already exists for the authenticated UserId 
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("name",project.getProjectId().getName());
		queryParameters.put("version",project.getProjectId().getVersionId().getLabel());
		queryParameters.put("userId",userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
		List<MLPProject> projects = response.getContent();
		if(null != projects && projects.size() > 0 ) { 
			throw new DuplicateProjectException();
		}
	}

	@Override
	public Project createProject(String authenticatedUserId, Project project) {
		Project result = null; 
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		
		MLPProject mlpProject = ProjectServiceUtil.getMLPProject(userId, project);
		
		// Call to CDS to create new Project
		MLPProject responseMLPProject = cdsClient.createProject(mlpProject);
		
		result = ProjectServiceUtil.getProjectVO(responseMLPProject, mlpUser);
		
		// 8. Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Create Project";
		String resultMsg = result.getProjectId().getName() + " created successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
		
		return result;
	}

	@Override
	public boolean isOwnerOfProject(String authenticatedUserId, String projectId) throws NotOwnerException { 
		// Call to CDS to check if user is the owner of the project.
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("projectId",projectId);
		queryParameters.put("userId",userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
		
		if((null == response) || (null != response && response.getContent().size() == 0 )) { 
			throw new NotOwnerException();
		}
		return true;
	}
	
	@Override
	public boolean isProjectArchived(String projectId) throws ArchivedException { 
		boolean result = false;
		// CDS call to check if project is archived 
		MLPProject mlpProject = cdsClient.getProject(projectId);
		if(null != mlpProject && mlpProject.isActive()){
			throw new ArchivedException("Update not allowed – project is archived");
		}
		return result;
	}
	
	@Override
	public Project updateProject(String authenticatedUserId, Project project) throws DuplicateProjectException {
		Project result = new Project(); 
		Identifier projectId  = project.getProjectId();
		String newName = projectId.getName();
		Version projectVersion = projectId.getVersionId();
		String newversion = projectVersion.getLabel();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		 
		MLPProject old_mlpProject = cdsClient.getProject(project.getProjectId().getUuid());
		if(!newName.equals(old_mlpProject.getName()) || !newversion.equals(old_mlpProject.getVersion())){
			// Check if new project name and version is not same as previous, then it should not already exists in the DB. (Call to CDS).
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("name",newName);
			queryParameters.put("version",newversion);
			queryParameters.put("userId",userId);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
			List<MLPProject> projects = response.getContent();
			if(null != projects && projects.size() > 0 ) { 
				throw new DuplicateProjectException();
			}
		}
		// 8. If UserId not present then insert an entry in Workbench User Table. In case of collaborator (Call to CDS).
		 
		// 9. CDS call to update existing project  (Call to CDS)
		MLPProject newMLPProject = ProjectServiceUtil.updateMLPProject(old_mlpProject, project);
		cdsClient.updateProject(newMLPProject);
		
		result = ProjectServiceUtil.getProjectVO(newMLPProject, mlpUser);
		
		// 10. Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Update Project";
		String resultMsg = result.getProjectId().getName() + " updated successfully";
		saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
		  		
		return result;
	}
	
	@Override
	public Project getProject(String authenticatedUserId, String projectId) throws ProjectNotFoundException {
		Project result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		//CDS call to get the project details. 
		MLPProject mlpProject = cdsClient.getProject(projectId);
		if(null != mlpProject) { 
			result = ProjectServiceUtil.getProjectVO(mlpProject, mlpUser);
		}
		
		if(null == result) { 
			throw new ProjectNotFoundException();
		}
		return result;
	}
	
	@Override
	public List<Project> getProjects(String authenticatedUserId) {
		List<Project> result = new ArrayList<Project>();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		//CDS call to get all the projects for a user
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("userId", userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
		List<MLPProject> mlpProjects = response.getContent();
		if(null != mlpProjects && mlpProjects.size() > 0 ){
			result = ProjectServiceUtil.getMLPProjects(mlpProjects, mlpUser);
		}
		return result;
	}
	
	@Override
	public ServiceState deleteProject(String projectId) { 
		ServiceState result = new ServiceState(); 
		
		//4.Delete the association between the project and its child artifacts (call to CDS).
		List<MLPPipeline> mlpPipelines = cdsClient.getProjectPipelines(projectId);
		List<MLPNotebook> mlpNotebooks = cdsClient.getProjectNotebooks(projectId);
		
		//delete Pipelines 
		if(null != mlpPipelines && mlpPipelines.size() > 0 ) { 
			for(MLPPipeline mlpPipeline : mlpPipelines ){
				cdsClient.dropProjectPipeline(projectId, mlpPipeline.getPipelineId());
			}
		}
		
		//delete Notebooks 
		if(null != mlpNotebooks && mlpNotebooks.size() > 0 ) { 
			for(MLPNotebook mlpNotebook : mlpNotebooks ){
				cdsClient.dropProjectPipeline(projectId, mlpNotebook.getNotebookId());
			}
		}
		
		// 5. Delete the Project entry in User Table
		
		//6.Delete the Project
		cdsClient.deleteProject(projectId);
		
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("Project Deleted successfully.");
		
		return result;
	}
	
	@Override
	public Project archiveProject(String authenticatedUserId, String projectId) { 
		Project result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		// Call to CDS to mark project as archived. 
		MLPProject mlpProject = cdsClient.getProject(projectId);
		mlpProject.setActive(false);
		cdsClient.updateProject(mlpProject);
		
		result = ProjectServiceUtil.getProjectVO(mlpProject, mlpUser);
		
		ServiceState serviceStatus = new ServiceState();
		serviceStatus.setStatus(ServiceStatus.COMPLETED);
		result.setServiceStatus(serviceStatus);
		ArtifactState artifactStatus = new ArtifactState();
		artifactStatus.setStatus(ArtifactStatus.ARCHIVED);
		result.setArtifactStatus(artifactStatus);
		
		return result;
	}
	
	@Override
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", authenticatedUserId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false, pageRequest);
		
		List<MLPUser> mlpUsers = response.getContent();
		MLPUser mlpUser = null;
		
		if(null != mlpUsers && mlpUsers.size() > 0) {
			mlpUser = mlpUsers.get(0);
			
		} else {
			throw new UserNotFoundException(authenticatedUserId);
		}
		return mlpUser;
	}
	
	private void saveNotification(String authenticatedUserId,
			String statusCode, String taskName, String resultMsg) {
		
		
		MLPNotification mlpNotification = new MLPNotification();
		
		//TODO : Set Notification
	}

}
