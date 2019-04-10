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
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.notebookservice.exception.ArchivedException;
import org.acumos.workbench.notebookservice.exception.AssociationNotFoundException;
import org.acumos.workbench.notebookservice.exception.DuplicateNotebookException;
import org.acumos.workbench.notebookservice.exception.NotOwnerException;
import org.acumos.workbench.notebookservice.exception.NotebookNotFoundException;
import org.acumos.workbench.notebookservice.exception.TargetServiceInvocationException;
import org.acumos.workbench.notebookservice.exception.UserNotFoundException;
import org.acumos.workbench.notebookservice.util.ConfigurationProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.acumos.workbench.notebookservice.util.NotebookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service("NotebookServiceImpl")
public class NotebookServiceImpl implements NotebookService {

	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;

	@Autowired
	private ConfigurationProperties confprops;

	@Autowired
	private NotebookServiceProperties props;
	
	@Autowired
	@Qualifier("ProjectServiceRestClientImpl")
	private ProjectServiceRestClient psClient;
	
	@Autowired
	@Qualifier("JupyterhubRestClientImpl")
	private JupyterhubRestClient jhClient;
	

	@Override
	public void notebookExists(String notebookId) throws NotebookNotFoundException, TargetServiceInvocationException {
		//CDS call to get MLPNotebook 
		try {
			MLPNotebook response = cdsClient.getNotebook(notebookId);
			if (null == response) {
				throw new NotebookNotFoundException();
			}

		} catch (RestClientResponseException e) { 
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
	}

	@Override
	public void notebookExists(String authenticatedUserId, String projectId, Notebook notebook)
			throws DuplicateNotebookException {
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		// CDS call to check if Notebook name, type and version already exists for the authenticated UserId and projectId. 
		Map<String, Object> queryParameters = new HashMap<String, Object>();

		if (null != projectId) {
			queryParameters.put("projectId", projectId);
		}

		if (null != notebook.getNoteBookId()) {
			Identifier notebookIdentifier = notebook.getNoteBookId();
			queryParameters.put("name", notebookIdentifier.getName());
			if (null != notebookIdentifier.getVersionId()) {
				Version notebookVersion = notebookIdentifier.getVersionId();
				if (null != notebookVersion.getLabel()
						&& !notebookVersion.getLabel().trim().equals("")) {
					queryParameters.put("version", notebookVersion.getLabel());
				}
			}
		}

		queryParameters.put("notebookTypeCode", notebook.getNotebookType());
		queryParameters.put("userId", userId);

		try {
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters,
					false, pageRequest);
			if (null != response && response.getContent().size() > 0) {
				throw new DuplicateNotebookException();
			}
		} catch (RestClientResponseException e) {
			//TODO : Log the error details 
			throw new TargetServiceInvocationException(props.getCdsSearchNotebooksExcp());
		}

	}

	@Override
	public Notebook createNotebook(String authenticatedUserId, String projectId, Notebook notebook) throws TargetServiceInvocationException {
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();

		Notebook result = null;
		MLPNotebook responseMLPNotebook = null;
		MLPNotebook mlpNotebook = null;
		try {
			mlpNotebook = NotebookServiceUtil.getMLPNotebook(userId, notebook);
			responseMLPNotebook = cdsClient.createNotebook(mlpNotebook);
		} catch (RestClientResponseException e) {
			//TODO : Log error
			throw new TargetServiceInvocationException(props.getCdsCreateNotebookExcp());
		}
		
		try {
			if (null != projectId) {
				cdsClient.addProjectNotebook(projectId, responseMLPNotebook.getNotebookId());
			}
		} catch (Exception e) {
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsAddProjectNotebookExcp());
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
	public boolean isOwnerOfNotebook(String authenticatedUserId, String notebookId)
			throws NotOwnerException {
		
		try {
			// Call to CDS to check if user is the owner of the project.
			MLPUser mlpUser = getUserDetails(authenticatedUserId);
			String userId = mlpUser.getUserId();
			MLPNotebook response = cdsClient.getNotebook(notebookId);
			if ((null == response)) {
				//TODO : log error
				throw new NotebookNotFoundException();
			} else if (!response.getUserId().equals(mlpUser.getUserId())) {
				//TODO : log error
				throw new NotOwnerException();
			}
		} catch (RestClientResponseException e) { 
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		return true;
	}

	@Override
	public boolean isNotebookArchived(String notebookId) throws ArchivedException {
		boolean result = false;
		try {
			// CDS call to check if project is archived 
			MLPNotebook mlpNotebook = cdsClient.getNotebook(notebookId);
			if (null != mlpNotebook && !mlpNotebook.isActive()) {
				//TODO : log error
				throw new ArchivedException("Update not allowed – notebook is archived");
			}
		} catch (RestClientResponseException e) { 
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		return result;
	}

	@Override
	public Notebook updateNotebook(String authenticatedUserId, String projectId, String notebookId,
			Notebook notebook) throws DuplicateNotebookException {
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		Identifier notebookIdentifier = notebook.getNoteBookId();
		String newNotebookName = notebookIdentifier.getName();
		Version newNotebookVersionId = notebookIdentifier.getVersionId();
		String newNotebookVersion = null;
		if (null != newNotebookVersionId && null != newNotebookVersionId.getLabel()) {
			newNotebookVersion = newNotebookVersionId.getLabel();
		}
		String newnotebookTypeCode = NotebookType.valueOf(notebook.getNotebookType().toUpperCase())
				.getNotebookTypeCode();

		//Check if notebook name, type and version is not same as previous, if so then check for duplicate 
		MLPNotebook oldmlpNotebook = null;
		try {
			oldmlpNotebook = cdsClient.getNotebook(notebookId);
		} catch (Exception e) { 
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		
		if (!newNotebookName.equals(oldmlpNotebook.getName())
				|| !newnotebookTypeCode.equals(oldmlpNotebook.getNotebookTypeCode())
				|| (null != newNotebookVersion && null != oldmlpNotebook.getVersion() && !newNotebookVersion
						.equals(oldmlpNotebook.getVersion()))) {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("name", newNotebookName);
			queryParameters.put("version", newNotebookVersion);
			queryParameters.put("notebookTypeCode", newnotebookTypeCode);
			queryParameters.put("userId", userId);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			RestPageResponse<MLPNotebook> response = null;
			try {
				response = cdsClient.searchNotebooks(queryParameters, false, pageRequest);
			} catch (Exception e) { 
				//TODO : log error
				throw new TargetServiceInvocationException(props.getCdsSearchNotebooksExcp());
			}
			List<MLPNotebook> notebooks = response.getContent();
			if (null != notebook && notebooks.size() > 0) {
				//TODO : log error
				throw new DuplicateNotebookException();
			}
		}
		MLPNotebook newMLPNotebook = NotebookServiceUtil.updateMLPNotebook(oldmlpNotebook, notebook);
		try {
			cdsClient.updateNotebook(newMLPNotebook);
		} catch (Exception e) { 
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsUpdateNotebookExcp());
		}
		newMLPNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		
		
		result = NotebookServiceUtil.getNotebookVO(newMLPNotebook, mlpUser);

		//TODO : Need to discuss and finalize Add success or error message to Notification. (Call to CDS)
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
		try {
			//CDS call to get MLPNotebook 
			MLPNotebook response = cdsClient.getNotebook(notebookId);
			if (null == response) {
				//TODO : log error
				throw new NotebookNotFoundException();
			}
			result = NotebookServiceUtil.getNotebookVO(response, mlpUser);
		} catch (RestClientResponseException e) { 
			//TODO : Log error 
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		
		return result;
	}

	@Override
	public List<Notebook> getNotebooks(String authenticatedUserId, String projectId) {
		List<Notebook> result = new ArrayList<Notebook>();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		List<MLPNotebook> mlpNotebooks = null;
		if (null == projectId) {
			try {
				Map<String, Object> queryParameters = new HashMap<String, Object>();
				queryParameters.put("userId", userId);
				RestPageRequest pageRequest = new RestPageRequest(0, confprops.getResultsetSize());
				RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters,
						false, pageRequest);
				mlpNotebooks = response.getContent();
			} catch (Exception e) { 
				//TODO : log error
				throw new TargetServiceInvocationException(props.getCdsSearchNotebooksExcp());
			}

		} else {
			try {
				mlpNotebooks = cdsClient.getProjectNotebooks(projectId);
			} catch (Exception e) { 
				//TODO : log error
				throw new TargetServiceInvocationException(props.getCdsGetProjectNotebooksExcp());
			}
		}

		if (null != mlpNotebooks && mlpNotebooks.size() > 0) {
			result = NotebookServiceUtil.getNotebookVOs(mlpNotebooks, mlpUser);
		}
		return result;
	}

	@Override
	public ServiceState deleteNotebook(String notebookId) {
		ServiceState result = null;
		List<MLPProject> mlpProjects = null;
		
		//Delete any association with Project
		try {
			mlpProjects = cdsClient.getNotebookProjects(notebookId);
		} catch (Exception e) { 
			//TODO : Log error
			throw new TargetServiceInvocationException(props.getCdsGetNotebookProjectsExcp()); 
		}
		
		try {
			if (null != mlpProjects && mlpProjects.size() > 0) {
				for (MLPProject mlpProject : mlpProjects) {
					cdsClient.dropProjectNotebook(mlpProject.getProjectId(), notebookId);
				}
			}
		} catch (Exception e) {
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsDropProjectNotebookExcp()); //TODO : need to change the Exception message
		}
		
		try {
			cdsClient.deleteNotebook(notebookId);
		} catch (Exception e) { 
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsDeleteNotebookExcp()); //TODO : need to change the Exception message
		}
		result = new ServiceState();
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("Notebook Deleted successfully.");
		return result;
	}

	@Override
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException,
			TargetServiceInvocationException {
		MLPUser mlpUser = null;
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("loginName", authenticatedUserId);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false,
					pageRequest);
			List<MLPUser> mlpUsers = response.getContent();
			if (null != mlpUsers && mlpUsers.size() > 0) {
				mlpUser = mlpUsers.get(0);
			} else {
				//TODO : log error
				throw new UserNotFoundException(authenticatedUserId);
			}

		} catch (RestClientResponseException e) {
			//TODO : log the error 
			throw new TargetServiceInvocationException(props.getCdsGetUserExcp());
		}
		return mlpUser;
	}

	private void saveNotification(String authenticatedUserId, String statusCode, String taskName,
			String resultMsg) {

		MLPNotification mlpNotification = new MLPNotification();

		//TODO : Set Notification
	}

	@Override
	public Notebook archiveNotebook(String authenticatedUserId, String projectId,
			String notebookId, String actionType) {
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		MLPNotebook mlpNotebook = null;
		try {
			mlpNotebook = cdsClient.getNotebook(notebookId);
		} catch (Exception e) {
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		switch (actionType) {
		case ("UA"):
			mlpNotebook.setActive(true);
			break;
		case ("A"):
		default:
			mlpNotebook.setActive(false);
		}
		mlpNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		try {
			cdsClient.updateNotebook(mlpNotebook);
		} catch (Exception e) { 
			//TODO : log error
			throw new TargetServiceInvocationException(props.getCdsUpdateNotebookExcp());
		}
		result = NotebookServiceUtil.getNotebookVO(mlpNotebook, mlpUser);

		//Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " archived successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);

		return result;
	}

	@Override
	public Notebook launchNotebook(String authenticatedUserId, String projectId, String notebookId) {
		String url = null;
		Notebook notebook = getNotebook(authenticatedUserId, notebookId);
		if (null != notebook) {
			NotebookType notebookType = NotebookType.valueOf(notebook.getNotebookType());
			
			switch(notebookType) {
				case JUPYTER : 
					url = jhClient.launchJupyterNotebook(authenticatedUserId, projectId, notebookId);
					break;
				case ZEPPELIN :
					break;
				default: 
			}
			
			notebook.getNoteBookId().setServiceUrl(url);
		}
		return notebook;
	}

	@Override
	public void isNotebookProjectAssociated(String projectId, String notebookId)
			throws AssociationNotFoundException {
		boolean associated = false;
		try {
			List<MLPNotebook> mlpNotebooks = cdsClient.getProjectNotebooks(projectId);
			if (null != mlpNotebooks && mlpNotebooks.size() > 0) {
				for (MLPNotebook notebook : mlpNotebooks) {
					//check if equals to input notebookId 
					if (notebook.getNotebookId().equals(notebookId)) {
						associated = true;
						break;
					}
				}
			}
		} catch (Exception e) { 
			throw new TargetServiceInvocationException(props.getCdsGetProjectNotebooksExcp());
		}
		if (!associated) {
			throw new AssociationNotFoundException(
					"Specified Project and Notebook are not associated");
		}
	}

}
