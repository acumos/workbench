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

package org.acumos.workbench.notebookservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.notebookservice.exception.ArchivedException;
import org.acumos.workbench.notebookservice.exception.DuplicateNotebookException;
import org.acumos.workbench.notebookservice.exception.NotOwnerException;
import org.acumos.workbench.notebookservice.exception.NotebookNotFoundException;
import org.acumos.workbench.notebookservice.exception.ProjectNotFoundException;
import org.acumos.workbench.notebookservice.exception.UserNotFoundException;
import org.acumos.workbench.notebookservice.util.ConfigurationProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.acumos.workbench.notebookservice.util.ServiceStatus;
import org.acumos.workbench.notebookservice.vo.Identifier;
import org.acumos.workbench.notebookservice.vo.Notebook;
import org.acumos.workbench.notebookservice.vo.ServiceState;
import org.acumos.workbench.notebookservice.vo.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("NotebookServiceImpl")
public class NotebookServiceImpl implements NotebookService {

	
	@Autowired
	CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	ConfigurationProperties confprops;
	
	@Override
	public void projectExists(String authenticatedUserId,String projectId) throws ProjectNotFoundException { 
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		//  CDS call to check if project-version already exists for the authenticated UserId 
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("projectId",projectId);
		queryParameters.put("userId",userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPProject> response = cdsClient.searchProjects(queryParameters, false, pageRequest);
		if(null != response) { 
			List<MLPProject> projects = response.getContent();
			if(null == projects || projects.size() == 0 ) { 
				throw new ProjectNotFoundException();
			}
		} else { 
			throw new ProjectNotFoundException();
		}
		
	}

	@Override
	public void notebookExists(String authenticatedUserId,String projectId, Notebook notebook) throws DuplicateNotebookException {
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		// CDS call to check if Notebook name, type and version already exists for the authenticated UserId and projectId. 
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		
		if(null != projectId) { 
			queryParameters.put("projectId", projectId);
		}
		
		if(null != notebook.getNoteBookId()) { 
			Identifier notebookIdentifier = notebook.getNoteBookId();
			queryParameters.put("name", notebookIdentifier.getName());
			if(null != notebookIdentifier.getVersionId()) { 
				Version notebookVersion = notebookIdentifier.getVersionId();
				if(null != notebookVersion.getLabel() && !notebookVersion.getLabel().trim().equals("")){
					queryParameters.put("version", notebookVersion.getLabel());
				}
			}
		}
		
		queryParameters.put("notebookTypeCode", notebook.getNotebookType());
		queryParameters.put("userId", userId);
		
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters, false, pageRequest);
		if(null != response && response.getContent().size() > 0 ) { 
			throw new DuplicateNotebookException();
		}
		
	}
	
	@Override
	public Notebook createNotebook(String authenticatedUserId, String projectId, Notebook notebook) {
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		
		Notebook result = null;
		MLPNotebook mlpNotebook = NotebookServiceUtil.getMLPNotebook(userId, notebook);
		MLPNotebook responseMLPNotebook = cdsClient.createNotebook(mlpNotebook);
		
		if(null != projectId) { 
			cdsClient.addProjectNotebook(projectId, responseMLPNotebook.getNotebookId());
		}
		mlpNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		result = NotebookServiceUtil.getNotebookVO(responseMLPNotebook, mlpUser);
		
		//Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " created successfully";
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
	public boolean isOwnerOfNotebook(String authenticatedUserId, String notebookId) throws NotOwnerException { 
		// Call to CDS to check if user is the owner of the project.
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("notebookId",notebookId);
		queryParameters.put("userId",userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters, false, pageRequest);
		
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
	public boolean isNotebookArchived(String notebookId) throws ArchivedException { 
		boolean result = false;
		// CDS call to check if project is archived 
		MLPNotebook mlpNotebook = cdsClient.getNotebook(notebookId);
		if(null != mlpNotebook && mlpNotebook.isActive()){
			throw new ArchivedException("Update not allowed – notebook is archived");
		}
		return result;
	}
	
	@Override
	public Notebook updateNotebook(String authenticatedUserId, String projectId, String notebookId, Notebook notebook) throws DuplicateNotebookException {
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		Identifier notebookIdentifier = notebook.getNoteBookId();
		String newNotebookName = notebookIdentifier.getName();
		Version newNotebookVersionId = notebookIdentifier.getVersionId();
		String newNotebookVersion = null;
		if(null != newNotebookVersionId && null != newNotebookVersionId.getLabel()) { 
			newNotebookVersion = newNotebookVersionId.getLabel();
		}
		String newnotebookTypeCode = NotebookServiceUtil.getNotebookTypeCode(notebook.getNotebookType());
		
		//Check if notebook name, type and version is not same as previuos, is so then check for duplicate 
		
		MLPNotebook oldmlpNotebook = cdsClient.getNotebook(notebookId);
		if(!newNotebookName.equals(oldmlpNotebook.getName()) || 
				!newnotebookTypeCode.equals(oldmlpNotebook.getNotebookTypeCode()) || 
				( null != newNotebookVersion && null != oldmlpNotebook.getVersion() && !newNotebookVersion.equals(oldmlpNotebook.getVersion()))) {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("name",newNotebookName);
			queryParameters.put("version",newNotebookVersion);
			queryParameters.put("notebookTypeCode",newnotebookTypeCode);
			queryParameters.put("userId",userId);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters, false, pageRequest);
			List<MLPNotebook> notebooks = response.getContent();
			if(null != notebook && notebooks.size() > 0 ){ 
				throw new DuplicateNotebookException();
			}
		}
		MLPNotebook newMLPNotebook = NotebookServiceUtil.updateMLPNotebook(oldmlpNotebook, notebook);
		cdsClient.updateNotebook(newMLPNotebook);
		newMLPNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		result = NotebookServiceUtil.getNotebookVO(newMLPNotebook, mlpUser);
		
		//Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " updated successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
				
		return result;
	}
		
	@Override
	public Notebook getNotebook(String authenticatedUserId, String notebookId)
			throws NotebookNotFoundException {
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		//CDS call to get MLPNotebook 
		MLPNotebook response = cdsClient.getNotebook(notebookId);
		if(null == response) { 
			throw new NotebookNotFoundException();
		}
		result = NotebookServiceUtil.getNotebookVO(response, mlpUser);
		return result;
	}

	@Override
	public List<Notebook> getNotebooks(String authenticatedUserId,
			String projectId) {
		List<Notebook> result = new ArrayList<Notebook>();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		List<MLPNotebook> mlpNotebooks = null;
		if (null == projectId) {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("userId", userId);
			RestPageRequest pageRequest = new RestPageRequest(0, confprops.getResultsetSize());
			RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters, false, pageRequest);
			mlpNotebooks = response.getContent();
			
		} else { 
			mlpNotebooks = cdsClient.getProjectNotebooks(projectId);
		}
		
		if (null != mlpNotebooks && mlpNotebooks.size() > 0) {
			result = NotebookServiceUtil.getNotebookVOs(mlpNotebooks, mlpUser);
		}
		return result;
	}

	
	@Override
	public ServiceState deleteNotebook(String notebookId) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Notebook archiveProject(String authenticatedUserId, String projectId, String notebookId, String actionType) {
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		MLPNotebook mlpNotebook = cdsClient.getNotebook(notebookId);
		switch(actionType) {
		case("UA") : 
			mlpNotebook.setActive(true);
			break;
		case("A") : 
		default : 
			mlpNotebook.setActive(false);
		}
		mlpNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		cdsClient.updateNotebook(mlpNotebook);
		result = NotebookServiceUtil.getNotebookVO(mlpNotebook, mlpUser);
		
		//Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " archived successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
				
		return result;
	}

	

	

}
