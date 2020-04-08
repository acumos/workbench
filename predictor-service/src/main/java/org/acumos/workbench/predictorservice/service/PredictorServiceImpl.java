/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.predictorservice.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.logging.LoggingConstants;
import org.acumos.workbench.common.security.SecurityConstants;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.Environment;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Predictor;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Projects;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.predictorservice.deployClient.DeploymentResponse;
import org.acumos.workbench.predictorservice.deployClient.DeploymentServiceImpl;
import org.acumos.workbench.predictorservice.deployClient.K8sConfig;
import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.lightcouch.DataSetPredictor;
import org.acumos.workbench.predictorservice.utils.ConfigurationProperties;
import org.acumos.workbench.predictorservice.utils.PredictorServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.utils.StringEscapeUtils;

@Service("PredictorServiceImpl")	
public class PredictorServiceImpl implements PredictorService {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private CommonDataServiceRestClientImpl cdsClient;

	@Autowired
	private CouchDBService couchDBService;
	
	@Autowired
	private ConfigurationProperties configurationProperties;
	
	@Autowired
	private DeploymentServiceImpl deploymentServiceImpl;
	
	@Override
	public Predictor createPredictor(String authenticatedUserId,Predictor predictor,String revisionId, String authToken,String K8S_ID,String predictorKey,String projectId) {
		logger.debug("CreatePredictor() Begin");
		Predictor result=null;
		try {
			DataSetPredictor dataSetPredictor = couchDBService.createPredictor(authenticatedUserId, predictor,revisionId, authToken, K8S_ID, predictorKey, projectId);
			deployPredictorToK8s(dataSetPredictor);
			result = getPredictorVO(dataSetPredictor);
		} catch (Exception e) {
			logger.debug("CreatePredictor() Begin");
			throw new PredictorException("Exception occurred while creating predictor", e);
		}
		return result;
	}

	@Override
	public MLPUser getUserDetails(String authenticatedUserId)throws UserNotFoundException, TargetServiceInvocationException {
		logger.debug("getUserDetails() Begin");
		MLPUser mlpUser = null;
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("loginName", authenticatedUserId);
			queryParameters.put("active", true);
			RestPageRequest pageRequest = new RestPageRequest(0, 1);
			cdsClient.setRequestId(MDC.get(LoggingConstants.MDCs.REQUEST_ID));
			RestPageResponse<MLPUser> response = cdsClient.searchUsers(queryParameters, false,
					pageRequest);
			List<MLPUser> mlpUsers = response.getContent();
			if (null != mlpUsers && mlpUsers.size() > 0) {
				mlpUser = mlpUsers.get(0);
			} else {
				logger.error("User not found");
				throw new UserNotFoundException(authenticatedUserId);
			}

		} catch (RestClientResponseException e) {
			logger.error("CDS - Get User Details");
			throw new UserNotFoundException("Error occurred while getting user details from CDS");
		}
		logger.debug("getUserDetails() End");
		return mlpUser;
	}

	@Override
	public Predictor getPredictor(String authenticatedUserId, String predictorId) {
		logger.debug("getPredictor() End");
		Predictor result = null;
		List<DataSetPredictor> dataSetPredictorList = null;
		dataSetPredictorList = couchDBService.getPreditor(authenticatedUserId, predictorId);
		if (null != dataSetPredictorList && !dataSetPredictorList.isEmpty() && !(dataSetPredictorList.size() > 1)) {
			for (DataSetPredictor dataSetPredictor : dataSetPredictorList) {
				result = getPredictorVO(dataSetPredictor);
			}
		}
		logger.debug("getPredictor() End");
		return result;
	}

	@Override
	public Predictor getDeploymentStatus(String authenticatedUserId, String predictorId) {
		logger.debug("getDeploymentStatus() Begin");
		Predictor result = null;
		try {
		List<DataSetPredictor> dataSetPredictorList = null;
		dataSetPredictorList = couchDBService.getPredictorById(authenticatedUserId, predictorId);
		if (null != dataSetPredictorList && !dataSetPredictorList.isEmpty() && !(dataSetPredictorList.size() > 1)) {
			for (DataSetPredictor dataSetPredictor : dataSetPredictorList) {
				dataSetPredictor = getEnvironmentPath(dataSetPredictor);
				dataSetPredictor.setUpdateTimestamp(Instant.now().toString());
				couchDBService.updatePredictor(dataSetPredictor);
				result = getPredictorVO(dataSetPredictor);
			}
		}
		}
		catch (Exception e) {
			logger.debug("getDeploymentStatus() End");
			throw new PredictorException("Exception ocuured while getting deployment status",e);
		}
		return result;
	}
	@Override
	public List<Predictor> getPredictorByUser(String authenticatedUserId,String projectId) {
		logger.debug("getPredictorByUserProject() Begin");
		Predictor predictor = null;
		List<DataSetPredictor> dataSetPredictorList = null;
		List<Predictor> result = new ArrayList<Predictor>();
		try {
		dataSetPredictorList = couchDBService.getPredictorforUser(authenticatedUserId,projectId);
		if (null != dataSetPredictorList && !dataSetPredictorList.isEmpty()) {
			for (DataSetPredictor dataSetPredictor : dataSetPredictorList) {
				predictor = getPredictorVO(dataSetPredictor);
				result.add(predictor);
			}
		}
		}
		catch (Exception e) {
			logger.debug("getPredictorByUserProject() End");
			throw new PredictorException("Exception occured while getting predictors list fro User/Project", e);
		}
		return result;
	}

	@Override
	public String getProtobufSignature(String authenticatedUserId, String solutionId, String revisionId,String authToken) {
		logger.debug("getProtobufSignature() Begin");
		String version = null;
		String response = null;
		try {
			MLPSolutionRevision mlpSolutionRevision = cdsClient.getSolutionRevision(solutionId, revisionId);
			version = mlpSolutionRevision.getVersion();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(configurationProperties.getDSURL() + PredictorServiceConstants.GET_PROTBUF_API);
			logger.debug(configurationProperties.getDSURL() + PredictorServiceConstants.GET_PROTBUF_API);
			builder.queryParam("userId", authenticatedUserId);
			builder.queryParam("solutionId", solutionId);
			builder.queryParam("version", version);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(SecurityConstants.AUTHORIZATION_HEADER_KEY, authToken);
			URI uri = builder.build().encode().toUri();
			response = restTemplate.getForObject(uri, String.class);
		} catch (Exception e) {
			logger.error("Error while getting protobuf signature from DS API" + e);
			throw new PredictorException("Error while getting protobuf signature from DS API" + e);
		}
		logger.debug("getProtobufSignature() End");
		return response;
	}
	
	@Override
	public List<K8sConfig> getK8sSiteConfig() {
		logger.debug("getK8sSiteConfig() Begin");
		List<K8sConfig> siteConfigValueList = null;
		MLPSiteConfig mlpSiteConfig = cdsClient.getSiteConfig(PredictorServiceConstants.K8CLUSTER_CONFIG_KEY);
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (null != mlpSiteConfig.getConfigValue()) {
				siteConfigValueList = Arrays.asList(mapper.readValue(mlpSiteConfig.getConfigValue(), K8sConfig[].class));
			}
		} catch (Exception e) {
			logger.error("Exception occured while geting K8S config list" + e);
			throw new PredictorException("Exception occured while geting K8S config list" + e);
		}
		logger.debug("getK8sSiteConfig() End");
		return siteConfigValueList;
	}
	
	@Override
	public ServiceState deletePredictor(String authenticatedUserId, String predictorId, String projectId) {
		logger.debug("deletePredictor() Start");
		ServiceState result = new ServiceState();
		try {
		List<DataSetPredictor> dataSetPredictorList = couchDBService.getPredictorById(authenticatedUserId, predictorId);
//		if (null != dataSetPredictorList && !dataSetPredictorList.isEmpty() && !(dataSetPredictorList.size() > 1)) {
			for (DataSetPredictor dataSetPredictor : dataSetPredictorList) {
				if (dataSetPredictor.getPredictorDeploymentStatus().equals(PredictorServiceConstants.deplyomentStatus.ACTIVE.toString())) {
//					deleteK8SNode(dataSetPredictor); // This is Pending
				}
				couchDBService.deletePredictor(dataSetPredictor);
			}
		
		result.setStatusMessage("Predictor is deleted successfully");
		result.setStatus(ServiceStatus.COMPLETED);
		}
		catch (Exception e) {
			logger.debug("deletePredictor() End");
			throw new PredictorException("Exception occurred while deleting predictor from couch db", e);
		}
		return result;
	}
	
	@Override
	public ServiceState checkDeploymentStatusonK8S(String deploymentId, String predictorId, String projectId) {
		logger.debug("checkDeploymentStatusonK8S() Begin");
		// TODO: Blocked due to Jenkins Jobs issue,Once this issue is resolved,need to complete the same.
		logger.debug("checkDeploymentStatusonK8S() End");
		return null;
	}
	
	private Predictor getPredictorVO(DataSetPredictor dataSetPredictor) {
		logger.debug("getPredictorVO() Begin");
		Predictor predictor = new Predictor();
		try {
			Identifier predictorIdentifier = new Identifier();
			predictorIdentifier.setIdentifierType(IdentifierType.PREDICTOR);
			predictorIdentifier.setName(dataSetPredictor.getPredictorName());
			predictorIdentifier.setServiceUrl(dataSetPredictor.getEnvironmentPath());
			predictorIdentifier.setUuid(dataSetPredictor.getPredictorId());
			Version version = new Version();
			version.setCreationTimeStamp(dataSetPredictor.getCreatedTimestamp());
			version.setModifiedTimeStamp(dataSetPredictor.getUpdateTimestamp());
			version.setLabel(dataSetPredictor.getPredictorVersion());
			predictorIdentifier.setVersionId(version);
			KVPair kvPairRevisionID = new KVPair(PredictorServiceConstants.REVISIONID,dataSetPredictor.getRevisionId());
			KVPair kvPairDeploymentStatus = new KVPair(PredictorServiceConstants.DEPLOYMENTSTATUS,dataSetPredictor.getPredictorDeploymentStatus());
			KVPair kvPairK8S_ID= new KVPair(PredictorServiceConstants.K8S_ID,dataSetPredictor.getK8sId());
			KVPair kvPairPREDICTORKEY= new KVPair(PredictorServiceConstants.PREDICTORKEY,dataSetPredictor.getPredictorkey());
			DeploymentResponse deploymentResponse = null;
			KVPair kvPairJenkinsURL=null;
			KVPair kvPairDeploymentURL=null;
			KVPair kvPairDeploymentId=null;
			KVPairs metrics = new KVPairs();
			List<KVPair> kv = new ArrayList<KVPair>();
			
			if (null != dataSetPredictor.getEnvironmentPath() && ! "null".equalsIgnoreCase(dataSetPredictor.getEnvironmentPath()) && isJSONValid(dataSetPredictor.getEnvironmentPath())) {
				deploymentResponse = new ObjectMapper().readValue(dataSetPredictor.getEnvironmentPath(), DeploymentResponse.class);
				 kvPairJenkinsURL = new KVPair(PredictorServiceConstants.JENKINSURL,deploymentResponse.getJenkinUrl());
				 kvPairDeploymentURL = new KVPair(PredictorServiceConstants.DEPLOYMENTURL,deploymentResponse.getDeploymentUrl());
				 kv.add(kvPairDeploymentURL);
				 kv.add(kvPairJenkinsURL);
			}
			
			if (null !=dataSetPredictor.getDeploymentId()) {
				kvPairDeploymentId = new KVPair(PredictorServiceConstants.DEPLOYMENTID,dataSetPredictor.getDeploymentId());
				kv.add(kvPairDeploymentId);
			}
			predictorIdentifier.setServiceUrl(dataSetPredictor.getEnvironmentPath());
			kv.add(kvPairRevisionID);
			kv.add(kvPairDeploymentStatus);
			kv.add(kvPairK8S_ID);
			kv.add(kvPairPREDICTORKEY);
			metrics.setKv(kv);
			predictorIdentifier.setMetrics(metrics);
			predictor.setPredictorId(predictorIdentifier);
			ArtifactState state = new ArtifactState();
			state.setStatus(ArtifactStatus.ACTIVE);
			predictor.setArtifactStatus(state);
			predictor.setEnvironment(Environment.DEVELOPMENT);
			Model model = new Model();
			Identifier modelIdentifier = new Identifier();
			modelIdentifier.setIdentifierType(IdentifierType.MODEL);
			modelIdentifier.setUuid(dataSetPredictor.getSolutionId());
			Version versionId = new Version();
			modelIdentifier.setVersionId(versionId);
			model.setModelId(modelIdentifier);
			predictor.setModel(model);
			ServiceState serviceState = new ServiceState();
			serviceState.setStatus(ServiceStatus.ACTIVE);
			predictor.setServiceStatus(serviceState);
			Project project=new Project();
			Identifier projectIdentifier = new Identifier();
			projectIdentifier.setIdentifierType(IdentifierType.PROJECT);
			projectIdentifier.setUuid(dataSetPredictor.getProjectId());
			project.setProjectId(projectIdentifier);
			Projects projects=new Projects();
			List<Project> projectsList=new ArrayList<Project>();
			projectsList.add(project);
			projects.setProjects(projectsList);
			predictor.setProjects(projects);
			User user = new User();
			user.setAuthenticatedUserId(dataSetPredictor.getUserId());
			predictor.setUser(user);
			logger.debug("getPredictorVO() End");
		} catch (Exception e) {
			logger.error("Exception Occurred while creating Predictor object" + e);
			throw new PredictorException("Exception Occurred while creating Predictor object" + e);
		}
		return predictor;
	}

	

	private void deleteK8SNode(DataSetPredictor dataSetPredictor) {
		logger.debug("deleteK8SNode() Start");
		String environmentPath = null;
		String jenkinsUrl = null;
		String deployUrl = null;
		try {
			environmentPath = StringEscapeUtils.escapeJava(dataSetPredictor.getEnvironmentPath());
			DeploymentResponse deploymentResponse = new ObjectMapper().readValue(environmentPath,DeploymentResponse.class);
			deployUrl = deploymentResponse.getDeploymentUrl();
			// TODO: NEED TO DELETE POD USING KUBERNETES API
			
			
		} catch (Exception e) {
			logger.error("Error while deleting Predictor" + e);
			throw new PredictorException("Error while deleting Predictor", e);
		}
		logger.debug("deleteK8SNode() End");
	}

	
	
	private void deployPredictorToK8s(DataSetPredictor dataSetPredictor) {
		// Async call
		CompletableFuture.supplyAsync(() -> {
			logger.debug(" Async call triggerDeployment() ");
			triggerDeployment(dataSetPredictor);
			return null;
		});
	}

	private void triggerDeployment(DataSetPredictor dataSetPredictor) {
		ResponseEntity<String> deployToK8Response = null;
		MLPUser mlpUser = null;
		mlpUser = getUserDetails(dataSetPredictor.getUserId());
		try {
			deployToK8Response = deploymentServiceImpl.deployModelToK8s(mlpUser.getUserId(), dataSetPredictor.getSolutionId(), dataSetPredictor.getRevisionId(), dataSetPredictor.getK8sId());
		} catch (IOException e1) {
			throw new PredictorException("Exception ocuured while deploying Model to K8S", e1);
		}
		DataSetPredictor predictor = couchDBService.getPredictor(dataSetPredictor.get_id());
		if (null != deployToK8Response.getBody() && deployToK8Response.getStatusCodeValue() == 202) {
			String responseBody=deployToK8Response.getBody();
			DeploymentResponse deploymentResponse=null;
			try {
				deploymentResponse = new ObjectMapper().readValue(responseBody, DeploymentResponse.class);
			} catch (Exception e) {
				throw new PredictorException("Exception ocuured while Parsing Json", e);
			}
			if (null != deploymentResponse) {
				predictor.setDeploymentId(deploymentResponse.getTrackingId());
				predictor.setPredictorDeploymentStatus(PredictorServiceConstants.deplyomentStatus.INPROGRESS.toString());
				predictor = getEnvironmentPath(predictor);
			}
			couchDBService.updatePredictor(predictor);
		}
	}

	private DataSetPredictor getEnvironmentPath(DataSetPredictor predictor ) {
		logger.debug("getEnvironmentPath() Begin");
		RestPageRequest pageRequest = new RestPageRequest(0, configurationProperties.getResultsetSize());
		RestPageResponse<MLPSolutionDeployment> mlpSolutionDeploymentResponse = cdsClient.getSolutionDeployments(predictor.getSolutionId(), predictor.getRevisionId(), pageRequest);
		predictor.setPredictorDeploymentStatus(PredictorServiceConstants.deplyomentStatus.FAILED.toString());
		if (null != mlpSolutionDeploymentResponse) {
			for (MLPSolutionDeployment mlpSolutionDeployment : mlpSolutionDeploymentResponse) {
				if (null != predictor.getDeploymentId() && null != mlpSolutionDeployment.getDeploymentId()
						&& predictor.getDeploymentId().equals(mlpSolutionDeployment.getDeploymentId())) {
					predictor.setEnvironmentPath(mlpSolutionDeployment.getDetail());
					if (mlpSolutionDeployment.getDeploymentStatusCode().equals("DP")) {
						predictor.setPredictorDeploymentStatus(PredictorServiceConstants.deplyomentStatus.ACTIVE.toString());
					}
				}
			}
		}
		logger.debug("getEnvironmentPath() End");
		return predictor;
	}
	
	private boolean isJSONValid(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
