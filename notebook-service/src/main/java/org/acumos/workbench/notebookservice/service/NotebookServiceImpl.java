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

import java.lang.invoke.MethodHandles;
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
import org.acumos.designstudio.ce.util.DSLogConstants;
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
import org.acumos.workbench.notebookservice.util.NotebookServiceLoggingConstants;
import org.acumos.workbench.notebookservice.util.NotebookServiceProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.acumos.workbench.notebookservice.util.NotebookType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service("NotebookServiceImpl")
public class NotebookServiceImpl implements NotebookService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
	@Qualifier("JupyterHubRestClientImpl")
	private JupyterHubRestClient jhClient;
	

	@Override
	public void notebookExists(String notebookId) throws NotebookNotFoundException, TargetServiceInvocationException {
		//CDS call to get MLPNotebook 
		logger.debug("notebookExists() Begin");
		try {
			logger.debug("getNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			MLPNotebook response = cdsClient.getNotebook(notebookId);
			logger.debug("getNotebook() End");
			if (null == response) {
				logger.error("Requested Notebook Not found");
				throw new NotebookNotFoundException();
			}

		} catch (RestClientResponseException e) { 
			logger.error("CDS - Get Notebook");
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		logger.debug("notebookExists() End");
	}

	@Override
	public void notebookExists(String authenticatedUserId, String projectId, Notebook notebook)
			throws DuplicateNotebookException {
		logger.debug("notebookExists() Begin");
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
			logger.debug("searchNotebooks() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters,
					false, pageRequest);
			logger.debug("searchNotebooks() End");
			if (null != response && response.getContent().size() > 0) {
				logger.error("Notebook name and type [and version] already exists");
				throw new DuplicateNotebookException();
			}
		} catch (RestClientResponseException e) {
			logger.error("CDS - Search Notebooks");
			throw new TargetServiceInvocationException(props.getCdsSearchNotebooksExcp());
		}
		logger.debug("notebookExists() End");
	}

	@Override
	public Notebook createNotebook(String authenticatedUserId, String projectId, Notebook notebook) throws TargetServiceInvocationException {
		logger.debug("createNotebook() Begin");
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();

		Notebook result = null;
		MLPNotebook responseMLPNotebook = null;
		MLPNotebook mlpNotebook = null;
		try {
			mlpNotebook = NotebookServiceUtil.getMLPNotebook(userId, notebook);
			logger.debug("CDS : createNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			responseMLPNotebook = cdsClient.createNotebook(mlpNotebook);
			logger.debug("CDS : createNotebook() End");
		} catch (RestClientResponseException e) {
			logger.error("CDS - Create Notebook");
			throw new TargetServiceInvocationException(props.getCdsCreateNotebookExcp());
		}
		
		try {
			if (null != projectId) {
				logger.debug("CDS : addProjectNotebook() Begin");
				cdsClient.addProjectNotebook(projectId, responseMLPNotebook.getNotebookId());
				logger.debug("CDS : addProjectNotebook() End");
			}
		} catch (Exception e) {
			logger.error("CDS - Add Project Notebook");
			throw new TargetServiceInvocationException(props.getCdsAddProjectNotebookExcp());
		}
		mlpNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		result = NotebookServiceUtil.getNotebookVO(responseMLPNotebook, mlpUser);

		//Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " created successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
		logger.debug("createNotebook() End");
		return result;
	}

	@Override
	public boolean isOwnerOfNotebook(String authenticatedUserId, String notebookId)
			throws NotOwnerException {
		logger.debug("isOwnerOfNotebook() Begin");
		
		try {
			// Call to CDS to check if user is the owner of the project.
			MLPUser mlpUser = getUserDetails(authenticatedUserId);
			String userId = mlpUser.getUserId();
			logger.debug("CDS : getNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			MLPNotebook response = cdsClient.getNotebook(notebookId);
			logger.debug("CDS : getNotebook() End");
			if ((null == response)) {
				logger.error("Requested Notebook Not found");
				throw new NotebookNotFoundException();
			} else if (!response.getUserId().equals(mlpUser.getUserId())) {
				logger.error("Permission denied");
				throw new NotOwnerException();
			}
		} catch (RestClientResponseException e) { 
			logger.error("CDS - Get Notebook");
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		logger.debug("isOwnerOfNotebook() End");
		return true;
	}

	@Override
	public boolean isNotebookArchived(String notebookId) throws ArchivedException {
		logger.debug("isNotebookArchived() Begin");
		boolean result = false;
		try {
			// CDS call to check if project is archived 
			logger.debug("CDS : getNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			MLPNotebook mlpNotebook = cdsClient.getNotebook(notebookId);
			logger.debug("CDS : getNotebook() End");
			if (null != mlpNotebook && !mlpNotebook.isActive()) {
				logger.error("Specified notebook is archived");
				throw new ArchivedException("Specified notebook is archived");
			}
		} catch (RestClientResponseException e) { 
			logger.error("CDS - Get Notebook");
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		logger.debug("isNotebookArchived() End");
		return result;
	}

	@Override
	public Notebook updateNotebook(String authenticatedUserId, String projectId, String notebookId,
			Notebook notebook) throws DuplicateNotebookException {
		logger.debug("updateNotebook() Begin");
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
			logger.debug("getNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			oldmlpNotebook = cdsClient.getNotebook(notebookId);
			logger.debug("getNotebook() End");
		} catch (Exception e) { 
			logger.error("CDS - Get Notebook");
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
				logger.debug("searchNotebooks() Begin");
				response = cdsClient.searchNotebooks(queryParameters, false, pageRequest);
				logger.debug("searchNotebooks() End");
			} catch (Exception e) { 
				logger.error("CDS - Search Notebooks");
				throw new TargetServiceInvocationException(props.getCdsSearchNotebooksExcp());
			}
			List<MLPNotebook> notebooks = response.getContent();
			if (null != notebook && notebooks.size() > 0) {
				logger.error("Notebook name and type [and version] already exists");
				throw new DuplicateNotebookException();
			}
		}
		MLPNotebook newMLPNotebook = NotebookServiceUtil.updateMLPNotebook(oldmlpNotebook, notebook);
		try {
			logger.debug("updateNotebook() Begin");
			cdsClient.updateNotebook(newMLPNotebook);
			logger.debug("updateNotebook() End");
		} catch (Exception e) { 
			logger.error("CDS - Update Notebook");
			throw new TargetServiceInvocationException(props.getCdsUpdateNotebookExcp());
		}
		
		try {
			if (null != projectId) {
				logger.debug("addProjectNotebook() Begin");
				cdsClient.addProjectNotebook(projectId, newMLPNotebook.getNotebookId());
				logger.debug("addProjectNotebook() End");
			}
		} catch (Exception e) {
			logger.error("CDS - Add Project Notebook");
			throw new TargetServiceInvocationException(props.getCdsAddProjectNotebookExcp());
		}
		
		newMLPNotebook.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		
		
		result = NotebookServiceUtil.getNotebookVO(newMLPNotebook, mlpUser);

		//TODO : Need to discuss and finalize Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " updated successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
		logger.debug("updateNotebook() End");
		return result;
	}

	@Override
	public Notebook getNotebook(String authenticatedUserId, String notebookId)
			throws NotebookNotFoundException {
		logger.debug("getNotebook() Begin");
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		try {
			//CDS call to get MLPNotebook 
			logger.debug("CDS : getNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			MLPNotebook response = cdsClient.getNotebook(notebookId);
			logger.debug("CDS : getNotebook() End");
			if (null == response) {
				logger.error("Requested Notebook Not found");
				throw new NotebookNotFoundException();
			}
			result = NotebookServiceUtil.getNotebookVO(response, mlpUser);
		} catch (RestClientResponseException e) { 
			logger.error("CDS - Get Notebook");
			throw new TargetServiceInvocationException(props.getCdsGetNotebookExcp());
		}
		logger.debug("getNotebook() End");
		return result;
	}

	@Override
	public List<Notebook> getNotebooks(String authenticatedUserId, String projectId) {
		logger.debug("getNotebooks() Begin");
		List<Notebook> result = new ArrayList<Notebook>();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		List<MLPNotebook> mlpNotebooks = null;
		if (null == projectId) {
			try {
				Map<String, Object> queryParameters = new HashMap<String, Object>();
				queryParameters.put("userId", userId);
				RestPageRequest pageRequest = new RestPageRequest(0, confprops.getResultsetSize());
				logger.debug("searchNotebooks() Begin");
				cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
				RestPageResponse<MLPNotebook> response = cdsClient.searchNotebooks(queryParameters,
						false, pageRequest);
				logger.debug("searchNotebooks() End");
				mlpNotebooks = response.getContent();
			} catch (Exception e) { 
				logger.error("CDS - Search Notebooks");
				throw new TargetServiceInvocationException(props.getCdsSearchNotebooksExcp());
			}

		} else {
			try {
				logger.debug("getProjectNotebooks() Begin");
				mlpNotebooks = cdsClient.getProjectNotebooks(projectId);
				logger.debug("getProjectNotebooks() End");
			} catch (Exception e) { 
				logger.error("CDS - Get Project Notebooks");
				throw new TargetServiceInvocationException(props.getCdsGetProjectNotebooksExcp());
			}
		}

		if (null != mlpNotebooks && mlpNotebooks.size() > 0) {
			result = NotebookServiceUtil.getNotebookVOs(mlpNotebooks, mlpUser);
		}
		logger.debug("getNotebooks() End");
		return result;
	}

	@Override
	public ServiceState deleteNotebook(String notebookId) {
		logger.debug("deleteNotebook() Begin");
		ServiceState result = null;
		List<MLPProject> mlpProjects = null;
		
		//Delete any association with Project
		try {
			logger.debug("getNotebookProjects() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			mlpProjects = cdsClient.getNotebookProjects(notebookId);
			logger.debug("getNotebookProjects() End");
		} catch (Exception e) { 
			logger.error("CDS - Get Notebook Projects");
			throw new TargetServiceInvocationException(props.getCdsGetNotebookProjectsExcp()); 
		}
		
		try {
			if (null != mlpProjects && mlpProjects.size() > 0) {
				for (MLPProject mlpProject : mlpProjects) {
					logger.debug("dropProjectNotebook() Begin");
					cdsClient.dropProjectNotebook(mlpProject.getProjectId(), notebookId);
					logger.debug("dropProjectNotebook() End");
				}
			}
		} catch (Exception e) {
			logger.error("CDS - Drop Project Notebook");
			throw new TargetServiceInvocationException(props.getCdsDropProjectNotebookExcp()); //TODO : need to change the Exception message
		}
		
		try {
			logger.debug("deleteNotebook() Begin");
			cdsClient.deleteNotebook(notebookId);
			logger.debug("deleteNotebook() End");
		} catch (Exception e) { 
			logger.error("CDS - Delete Notebook");
			throw new TargetServiceInvocationException(props.getCdsDeleteNotebookExcp()); //TODO : need to change the Exception message
		}
		result = new ServiceState();
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("Notebook Deleted successfully.");
		logger.debug("deleteNotebook() End");
		return result;
	}

	@Override
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException,
			TargetServiceInvocationException {
		logger.debug("getUserDetails() Begin");
		MLPUser mlpUser = null;
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("loginName", authenticatedUserId);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			logger.debug("searchUsers() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false,
					pageRequest);
			logger.debug("searchUsers() End");
			List<MLPUser> mlpUsers = response.getContent();
			if (null != mlpUsers && mlpUsers.size() > 0) {
				mlpUser = mlpUsers.get(0);
			} else {
				logger.error("User not found");
				throw new UserNotFoundException(authenticatedUserId);
			}

		} catch (RestClientResponseException e) {
			logger.error("CDS - Get User Details");
			throw new TargetServiceInvocationException(props.getCdsGetUserExcp());
		}
		logger.debug("getUserDetails() End");
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
		logger.debug("archiveNotebook() Begin");
		Notebook result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		MLPNotebook mlpNotebook = null;
		try {
			logger.debug("getNotebook() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			mlpNotebook = cdsClient.getNotebook(notebookId);
			logger.debug("getNotebook() End");
		} catch (Exception e) {
			logger.error("CDS - Get Notebook");
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
			logger.debug("updateNotebook() Begin");
			cdsClient.updateNotebook(mlpNotebook);
			logger.debug("updateNotebook() End");
		} catch (Exception e) { 
			logger.error("CDS - Update Notebook");
			throw new TargetServiceInvocationException(props.getCdsUpdateNotebookExcp());
		}
		result = NotebookServiceUtil.getNotebookVO(mlpNotebook, mlpUser);

		//Add success or error message to Notification. (Call to CDS)
		String statusCode = "SU";
		String taskName = "Notebook : ";
		String resultMsg = result.getNoteBookId().getName() + " archived successfully";
		//saveNotification(authenticatedUserId, statusCode, taskName, resultMsg);
		logger.debug("archiveNotebook() End");
		return result;
	}

	@Override
	public Notebook launchNotebook(String authenticatedUserId, String projectId, String notebookId) {
		logger.debug("launchNotebook() Begin");
		String url = null;
		Notebook notebook = getNotebook(authenticatedUserId, notebookId);
		if (null != notebook) {
			NotebookType notebookType = NotebookType.valueOf(notebook.getNotebookType());
			
			switch(notebookType) {
				case JUPYTER : 
					logger.debug("launchJupyterNotebook() Begin");
					url = jhClient.launchJupyterNotebook(authenticatedUserId, projectId, notebookId);
					logger.debug("launchJupyterNotebook() End");
					break;
				case ZEPPELIN :
					logger.error("Zeppelin - Service Not Found");
					throw new TargetServiceInvocationException("Zeppelin - Service Not Found");
				default:
					logger.error("Anonymous - Service Not Found");
					throw new TargetServiceInvocationException("Anonymous - Service Not Found");
			}
			
			notebook.getNoteBookId().setServiceUrl(url);
		}
		logger.debug("launchNotebook() End");
		return notebook;
	}

	@Override
	public void isNotebookProjectAssociated(String projectId, String notebookId)
			throws AssociationNotFoundException {
		logger.debug("isNotebookProjectAssociated() Begin");
		
		//TODO : Need CDS API instead
		boolean associated = false;
		try {
			logger.debug("getProjectNotebooks() Begin");
			cdsClient.setRequestId(MDC.get(NotebookServiceLoggingConstants.MDCs.REQUEST_ID));
			List<MLPNotebook> mlpNotebooks = cdsClient.getProjectNotebooks(projectId);
			logger.debug("getProjectNotebooks() End");
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
			logger.error("CDS - Get Project Notebooks");
			throw new TargetServiceInvocationException(props.getCdsGetProjectNotebooksExcp());
		}
		if (!associated) {
			logger.error("Specified Project and Notebook are not associated");
			throw new AssociationNotFoundException(
					"Specified Project and Notebook are not associated");
		}
		logger.debug("isNotebookProjectAssociated() End");
	}

}
