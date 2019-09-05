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

package org.acumos.workbench.pipelineservice.service;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.AssociationNotFoundException;
import org.acumos.workbench.common.exception.NotOwnerException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.pipelineservice.exception.DuplicatePipeLineException;
import org.acumos.workbench.pipelineservice.exception.NiFiInstanceCreationException;
import org.acumos.workbench.pipelineservice.exception.PipelineNotFoundException;
import org.acumos.workbench.pipelineservice.util.ConfigurationProperties;
import org.acumos.workbench.pipelineservice.util.PipeLineServiceUtil;
import org.acumos.workbench.pipelineservice.util.PipelineServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service("PipeLineServiceImpl")
public class PipeLineServiceImpl implements PipeLineService{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String UNARCHIVE = "UA";
	private static final String ARCHIVE = "A";
	
	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;
	
	@Autowired
	private ConfigurationProperties configProps;
	@Autowired
	private NiFiClient nifiService;
	
	@Override
	public Pipeline createPipeLine(String authenticatedUserId, String projectId, Pipeline pipeLine)
			throws TargetServiceInvocationException {
		logger.debug("createPipeLine() Begin");

		Pipeline result = null;
		MLPPipeline responseMLPPileLine = null;
		MLPPipeline mlpPipeline = null;

		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		// Create Pipeline in DB
		try {
			mlpPipeline = PipeLineServiceUtil.getMLPPipeLine(userId, pipeLine);
			// Call to CDS to create new PipeLine
			logger.debug("CDS createPipeLine() method Begin");
			responseMLPPileLine = cdsClient.createPipeline(mlpPipeline);
			logger.debug("CDS createPipeLine() method End");
		} catch (RestClientResponseException e) {
			logger.error("CDS - Create Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_CREATE_PIPELINE);
		}
		try {
			if (null != projectId) {
				logger.debug("CDS addProjectPipeline() method Begin");
				cdsClient.addProjectPipeline(projectId, responseMLPPileLine.getPipelineId());
				logger.debug("CDS addProjectPipeline() method End");
			}
		} catch (Exception e) {
			logger.error("CDS - addProjectPipeline ", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_ADD_PROJECT_PIPELINE);
		}

		final MLPPipeline newMLPPipeline = responseMLPPileLine;
		// Create Pipeline in NiFi Server
		String nifiURL = MessageFormat.format(configProps.getServiceBaseUrl(), authenticatedUserId);
		logger.debug(" NiFi URL : " + nifiURL);
		// If Nifi Pod already exits and running then
		boolean nifiPodRunning = false;
		// check if nifi Pod exists for the user or not
		nifiPodRunning = nifiService.checkifNifiPodRunning(authenticatedUserId);
		logger.debug("NiFi POD is Running ? : " + nifiPodRunning);
		// PART -A: CREATE NIFI INSTANCE FOR THE USER
		// STEP-1: CHECK IF THE NIFi CONTAINER FOR THE USER IS ALREADY CREATED
		if (!nifiPodRunning) {
			try {
				// Async Call
				CompletableFuture.supplyAsync(() -> {
					try {
						logger.debug(" Calling createPipelineAsync() ");
						createPipelineAsync(authenticatedUserId, newMLPPipeline);
					} catch (NiFiInstanceCreationException e) {
						logger.error("NiFiInstnaceCreationException while creating Nifi Instance ", e);
						newMLPPipeline.setServiceStatusCode(ServiceStatus.FAILED.getServiceStatusCode());
						try {
							// Call to CDS to update PipeLine with URL and Status
							logger.debug("CDS updatePipeLine() method Begin");
							cdsClient.updatePipeline(newMLPPipeline);
							logger.debug("CDS updatePipeLine() method End");
						} catch (RestClientResponseException re) {
							logger.error("CDS - Update Pipeline", re);
							throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_UPDATE_PIPELINE);
						}
					}
					return null;
				});
			} catch (Exception e) {
				logger.error("Async Call failed", e);
				newMLPPipeline.setServiceStatusCode(ServiceStatus.FAILED.getServiceStatusCode());
				try {
					// Call to CDS to update PipeLine with URL and Status
					logger.debug("CDS updatePipeLine() method Begin");
					cdsClient.updatePipeline(newMLPPipeline);
					logger.debug("CDS updatePipeLine() method End");
				} catch (RestClientResponseException re) {
					logger.error("CDS - Update Pipeline", re);
					throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_UPDATE_PIPELINE);
				}

			}
		} else {
			logger.debug("Calling createPipelineInNifiInstance() .....");
			createPipelineInNifiInstance(authenticatedUserId, newMLPPipeline, nifiURL);
		}
		result = PipeLineServiceUtil.getPipeLineVO(newMLPPipeline, mlpUser);
		// Add success or error message to Notification. (Call to CDS)
		String resultMsg = result.getPipelineId().getName() + " created successfully";
		// saveNotification(authenticatedUserId, STATUS_CODE, TASK_NAME, resultMsg);
		logger.debug("createPipeLine() Begin");
		return result;
	}


	@Override
	public void pipeLineExists(String authenticatedUserId, String projectId, Pipeline pipeLine){
		logger.debug("pipeLineExists() Begin");
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		// CDS call to check if project-version already exists for the authenticated UserId.
		Map<String, Object> queryParams = new HashMap<String, Object>();

		if (null != pipeLine.getPipelineId()) {
			Identifier pipeLineIdentifier = pipeLine.getPipelineId();
			queryParams.put("name", pipeLineIdentifier.getName());
		}
		queryParams.put("userId", userId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		logger.debug("CDS searchPipelines() method Begin");
		RestPageResponse<MLPPipeline> response = cdsClient.searchPipelines(queryParams, false, pageRequest);
		logger.debug("CDS searchPipelines() method End");
		if (null != response && response.getContent().size() > 0) {
			logger.error("Pipeline name and version already exists");
			throw new DuplicatePipeLineException();
		}
		logger.debug("pipeLineExists() End");
	}


	@Override
	public List<Pipeline> getPipelines(String authenticatedUserId, String projectId) {
		logger.debug("getPipelines() Begin");
		List<Pipeline> result = new ArrayList<Pipeline>();
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		List<MLPPipeline> mlpPipelines = null;
		if(null == projectId){
			try {
				Map<String, Object> queryParameters = new HashMap<String, Object>();
				queryParameters.put("userId", userId);
				RestPageRequest pageRequest = new RestPageRequest(0, configProps.getResultsetSize());
				logger.debug("CDS searchPipelines() method Begin");
				RestPageResponse<MLPPipeline> response = cdsClient.searchPipelines(queryParameters, false, pageRequest);
				logger.debug("CDS searchPipelines() method End");
				mlpPipelines = response.getContent();
			} catch (Exception e) {
				logger.error("CDS - Search Pipelines", e);
				throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_SEARCH_PIPELINES);
			}
		}else {
			try {
				logger.debug("CDS getProjectPipelines() method Begin");
				mlpPipelines = cdsClient.getProjectPipelines(projectId);
				logger.debug("CDS getProjectPipelines() method End");
			} catch (Exception e) {
				logger.error("CDS - Get Project Pipelines", e);
				throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PROJECT_PIPELINES);
			}
		}
		if(null != mlpPipelines && mlpPipelines.size() > 0){
			result = PipeLineServiceUtil.getPipeLineVOs(mlpPipelines, mlpUser);
		}
		logger.debug("getPipelines() End");
		return result;
	}

	@Override
	public boolean isOwnerOfPipeline(String authenticatedUserId, String pipelineId)throws NotOwnerException {
		logger.debug("isOwnerOfPipeline() Begin");
		try {
			// Call to CDS to check if user is the owner of the pipeline.
			MLPUser mlpUser = getUserDetails(authenticatedUserId);
			String userId = mlpUser.getUserId();
			logger.debug("CDS getPipeline() method Begin");
			MLPPipeline response  = cdsClient.getPipeline(pipelineId);
			logger.debug("CDS getPipeline() method End");
			if(null == response) {
				logger.error("Requested Pipeline Not found");
				throw new PipelineNotFoundException();
			}else if(!userId.equals(response.getUserId())){
				logger.error("Permission denied");
				throw new NotOwnerException();
			}
		} catch (RestClientResponseException e) {
			logger.error("CDS - Get Pipeline");
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}
		logger.debug("isOwnerOfPipeline() End");
		return true;
	}

	@Override
	public Pipeline updatePipeline(String authenticatedUserId, String projectId, String pipelineId, Pipeline pipeLine)
			throws DuplicatePipeLineException {
		logger.debug("updatePipeline() Begin");
		Pipeline result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		String userId = mlpUser.getUserId();
		Identifier pipeLineIdentifier = pipeLine.getPipelineId();
		String newPipeLineName = pipeLineIdentifier.getName();
		Version newPipeLineVersionId = pipeLineIdentifier.getVersionId();
		String newPipeLineVersion = null;
		if (null != newPipeLineVersionId && null != newPipeLineVersionId.getLabel()) {
			newPipeLineVersion = newPipeLineVersionId.getLabel();
		}
		MLPPipeline oldMLPPipeline = null;
		try {
			logger.debug("CDS getPipeline() method Begin");
			oldMLPPipeline = cdsClient.getPipeline(pipelineId);
			logger.debug("CDS getPipeline() method End");
		} catch (Exception e) {
			logger.error("CDS - Get Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}

		if (!newPipeLineName.equals(oldMLPPipeline.getName()) || (null != newPipeLineVersion
				&& null != oldMLPPipeline.getVersion() && !newPipeLineVersion.equals(oldMLPPipeline.getVersion()))) {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("name", newPipeLineName);
			queryParameters.put("version", newPipeLineVersion);
			queryParameters.put("userId", userId);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			RestPageResponse<MLPPipeline> response = null;
			try {
				logger.debug("CDS searchPipelines() method Begin");
				response = cdsClient.searchPipelines(queryParameters, false, pageRequest);
				logger.debug("CDS searchPipelines() method End");
			} catch (Exception e) {
				logger.error("CDS - Search Pipelines", e);
				throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_SEARCH_PIPELINES);
			}

			List<MLPPipeline> pipelines = response.getContent();
			if (null != pipelines && pipelines.size() > 0) {
				logger.error("Pipeline name and version already exists");
				throw new DuplicatePipeLineException();
			}
		}
		// Call NiFi Service to update the Pipeline Details
		if (!configProps.isUseexternalpipeline()) { // old nifi url
			nifiService.updatePipeline(authenticatedUserId, oldMLPPipeline.getName(), newPipeLineName);
		}

		MLPPipeline newMLPPipeline = PipeLineServiceUtil.updateMLPPipeline(oldMLPPipeline, pipeLine);

		if (configProps.isUseexternalpipeline()) {// external pipelineUrl
			try {
				if (null == pipeLine.getPipelineId().getServiceUrl()) {
					throw new TargetServiceInvocationException("Exception occured : PipeLine URL is null ");
				} else {
					new URL(pipeLine.getPipelineId().getServiceUrl());
					newMLPPipeline.setServiceUrl(pipeLine.getPipelineId().getServiceUrl());
				}

			} catch (MalformedURLException ex) {
				throw new TargetServiceInvocationException("Exception occured : Invalid PipelineURL ", ex);
			}
		} else{
			newMLPPipeline.setServiceUrl(pipeLine.getPipelineId().getServiceUrl());
		}

		try {
			logger.debug("CDS updatePipeline() method Begin");
			cdsClient.updatePipeline(newMLPPipeline);
			logger.debug("CDS updatePipeline() method End");
		} catch (Exception e) {
			logger.error("CDS - Update Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_UPDATE_PIPELINE);
		}
		try {
			if (null != projectId) {
				cdsClient.addProjectPipeline(projectId, newMLPPipeline.getPipelineId());
			}
		} catch (Exception e) {
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_ADD_PROJECT_PIPELINE);
		}

		newMLPPipeline.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		result = PipeLineServiceUtil.getPipeLineVO(newMLPPipeline, mlpUser);
		// Add success or error message to Notification. (Call to CDS)
		String resultMsg = result.getPipelineId().getName() + " updated successfully";
		// saveNotification(authenticatedUserId, STATUS_CODE, TASK_NAME,
		// resultMsg);
		logger.debug("updatePipeline() End");
		return result;
	}

	@Override
	public Pipeline getPipeline(String authenticatedUserId, String pipelineId) throws PipelineNotFoundException {
		logger.debug("getPipeline() Begin");
		Pipeline result = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		
		try {
			//CDS call to get MLPPipeline 
			logger.debug("CDS getPipeline() method Begin");
			MLPPipeline response = cdsClient.getPipeline(pipelineId);
			logger.debug("CDS getPipeline() method End");
			if(null == response){
				logger.error("Requested Pipeline Not found");
				throw new PipelineNotFoundException();
			}
			result = PipeLineServiceUtil.getPipeLineVO(response, mlpUser);
		} catch (Exception e) {
			logger.error("CDS - Get Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}
		logger.debug("getPipeline() End");
		return result;
	}

	@Override
	public ServiceState deletePipeline(String authenticatedUserId ,String pipelineId) {
		logger.debug("deletePipeline() Begin");
		ServiceState result = null;
		List<MLPProject> mlpProjects = null;
		try {
			logger.debug("CDS getPipelineProjects() method Begin");
			// Get the list of projects which having the pipelines under it for the specific pipelineId 
			mlpProjects = cdsClient.getPipelineProjects(pipelineId);
			logger.debug("CDS getPipelineProjects() method End");
		} catch (Exception e) {
			logger.error("CDS - Get Pipeline Projects", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE_PROJECTS);
		}
		try {
			if(null != mlpProjects && mlpProjects.size() >0){
				for(MLPProject mlpProject : mlpProjects){
					logger.debug("CDS dropProjectPipeline() method Begin");
					// Drop the association between the pipeline and the project
					cdsClient.dropProjectPipeline(mlpProject.getProjectId(), pipelineId);
					logger.debug("CDS dropProjectPipeline() method End");
				}
			}
		} catch (Exception e) {
			logger.error("CDS - Drop Project Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_DROP_PROJECT_PIPELINE);
		}
		Pipeline pipeline = getPipeline(authenticatedUserId, pipelineId);
		String pipelineName = pipeline.getPipelineId().getName();
		// Call NiFi Service to delete the Pipeline Details inside NiFi Server
		nifiService.deletePipeline(authenticatedUserId, pipelineName);
		try {
			//1.Delete the Pipeline
			cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
			logger.debug("CDS deletePipeline() method Begin");
			cdsClient.deletePipeline(pipelineId);
			logger.debug("CDS deletePipeline() method End");
		} catch (Exception e) {
			logger.error("CDS - Delete Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_DELETE_PIPELINE);
		}
		result = new ServiceState();
		result.setStatus(ServiceStatus.COMPLETED);
		result.setStatusMessage("Pipeline Deleted successfully.");
		logger.debug("deletePipeline() End");
		return result;
	}

	@Override
	public Pipeline archivePipeline(String authenticatedUserId, String projectId, String pipelineId,
			String actionType) {
		logger.debug("archivePipeline() Begin");
		Pipeline result = null;
		MLPPipeline mlpPipeline = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		
		try {
			logger.debug("CDS getPipeline() method Begin");
			mlpPipeline = cdsClient.getPipeline(pipelineId);
			logger.debug("CDS getPipeline() method End");
		} catch (Exception e) {
			logger.error("CDS - Get Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}
		
		switch(actionType) {
		case(UNARCHIVE) : 
			mlpPipeline.setActive(true);
			break;
		case(ARCHIVE) : 
		default : 
			mlpPipeline.setActive(false);
		}
		mlpPipeline.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		try {
			logger.debug("CDS updatePipeline() method Begin");
			cdsClient.updatePipeline(mlpPipeline);
			logger.debug("CDS updatePipeline() method End");
		} catch (Exception e) {
			logger.error("CDS - Update Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_UPDATE_PIPELINE);
		}
		
		result = PipeLineServiceUtil.getPipeLineVO(mlpPipeline, mlpUser);
		//Add success or error message to Notification. (Call to CDS)
		String resultMsg = result.getPipelineId().getName() + " archived successfully";
		//saveNotification(authenticatedUserId, STATUS_CODE, TASK_NAME, resultMsg);
		logger.debug("archivePipeline() End");
		return result;
	}
	

	@Override
	public void isPipelineArchived(String pipelineId) throws ArchivedException{
		logger.debug("isPipelineArchived() Begin");
		try {
			// CDS call to check if project is archived 
			MLPPipeline mlpPipeline = cdsClient.getPipeline(pipelineId);
			if (null != mlpPipeline && !mlpPipeline.isActive()) {
				logger.error("Pipeline is archived, launching Pipeline is not allowed");
				throw new ArchivedException("Cannot launch – pipeline is archived");
			}
		} catch (RestClientResponseException e) { 
			logger.error("CDS - Get Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}
		logger.debug("isPipelineArchived() End");
	}

	@Override
	public void isPipelineAssociatedUnderProject(String projectId, String pipelineId) {
		boolean associated = false;
		try {
			List<MLPPipeline> mlpPipelines = cdsClient.getProjectPipelines(projectId);
			if (null != mlpPipelines && mlpPipelines.size() > 0) {
				for (MLPPipeline pipeline : mlpPipelines) {
					if (pipeline.getPipelineId().equals(pipelineId)) {
						associated = true;
						break;
					}

				}
			}
		} catch (Exception e) {
			logger.error("CDS - Get Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}
		if (!associated) {
			logger.error("Specified Project and Pipeline are not associated");
			throw new AssociationNotFoundException("Specified Project and Pipeline are not associated");
		}

	}
	
	@Override
	public Pipeline launchPipeline(String authenticatedUserId, String projectId, String pipelineId) {
		logger.debug(" LaunchPipeline() Begin " );
		Pipeline result = null;
		MLPPipeline mlpPipeline = null;
		MLPUser mlpUser = getUserDetails(authenticatedUserId);
		try {
			logger.debug("CDS getPipeline() method Begin");
			mlpPipeline = cdsClient.getPipeline(pipelineId);
			logger.debug("CDS getPipeline() method End");
		} catch (Exception e) {
			logger.error("CDS - Get Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_GET_PIPELINE);
		}
		if(null != mlpPipeline.getServiceUrl()){
			String latestServiceURL = mlpPipeline.getServiceUrl().substring(0, mlpPipeline.getServiceUrl().indexOf("?")); //https://localhost:8443/nifi/
			mlpPipeline.setServiceUrl(latestServiceURL); 
			result = PipeLineServiceUtil.getPipeLineVO(mlpPipeline, mlpUser);
		}else {
			logger.error("Can not launch the Pipeline still its not ready to launch");
			throw new TargetServiceInvocationException("Can not launch the Pipeline still its not ready to launch");
		}
		logger.debug(" LaunchPipeline() End " );
		return result;
	}

	

	private void createPipelineAsync(String authenticatedUserId, MLPPipeline responseMLPPileLine) {
		logger.debug(" CreatePipelineAsync() Begin ");
		String nifiURL = null;
		if (configProps.isUseexternalpipeline()) { // external pipelineUrl
			try {
				if (null == responseMLPPileLine.getServiceUrl()) {
					throw new TargetServiceInvocationException("Exception occured : Pipeline URL is null ");
				} else {
					new URL(responseMLPPileLine.getServiceUrl());
					nifiURL = responseMLPPileLine.getServiceUrl();
				}
			} catch (MalformedURLException ex) {
				throw new TargetServiceInvocationException("Exception occured : Invalid PipelineURL ", ex);
			}
		} else {
			nifiURL = nifiService.createNiFiInstance(authenticatedUserId);
		}
		createPipelineInNifiInstance(authenticatedUserId, responseMLPPileLine, nifiURL);
		logger.debug(" CreatePipelineAsync() End ");
	}

	private void createPipelineInNifiInstance(String acumosLoginId, MLPPipeline mlpPileLine, String nifiURL) {
		logger.debug("CreatePipelineInNifiInstance() Begin ");
		String url = null;
		if (!configProps.isUseexternalpipeline()) {
			url = nifiService.createPipeline(acumosLoginId, mlpPileLine.getName(), nifiURL);
			logger.debug("NiFi Service URL : " + url);
		} else {
			String lastestUrl = mlpPileLine.getServiceUrl();
			try {
				if (null == lastestUrl) {
					throw new TargetServiceInvocationException("Exception occured : Pipeline URL is null ");
				} else {
					new URL(lastestUrl);
					url = lastestUrl;
				}
			} catch (MalformedURLException ex) {
				throw new TargetServiceInvocationException("Exception occured : Invalid PipelineURL ", ex);
			}
		}

		mlpPileLine.setServiceUrl(url);
		mlpPileLine.setServiceStatusCode(ServiceStatus.COMPLETED.getServiceStatusCode());
		try {
			// Call to CDS to update PipeLine with URL and Status
			logger.debug("CDS updatePipeLine() method Begin");
			cdsClient.updatePipeline(mlpPileLine);
			logger.debug("CDS updatePipeLine() method End");
		} catch (RestClientResponseException e) {
			logger.error("CDS - Update Pipeline", e);
			throw new TargetServiceInvocationException(PipelineServiceConstants.CDS_UPDATE_PIPELINE);
		}
		logger.debug("CreatePipelineInNifiInstance() End ");
	}

	private MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException {
		logger.debug("getUserDetails() Begin");
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("loginName", authenticatedUserId);
		RestPageRequest pageRequest = new RestPageRequest(0, 1);
		logger.debug("CDS searchUsers() method Begin");
		RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParams, false, pageRequest);
		logger.debug("CDS searchUsers() method End");
		List<MLPUser> mlpUsers = response.getContent();
		MLPUser mlpUser = null;
		if (null != mlpUsers && mlpUsers.size() > 0) {
			mlpUser = mlpUsers.get(0);
		} else {
			logger.warn(authenticatedUserId + " User not found");
			throw new UserNotFoundException(authenticatedUserId);
		}
		logger.debug("getUserDetails() End");
		return mlpUser;
	}


}

