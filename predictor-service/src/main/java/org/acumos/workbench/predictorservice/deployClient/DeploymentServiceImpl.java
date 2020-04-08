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

package org.acumos.workbench.predictorservice.deployClient;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.utils.ConfigurationProperties;
import org.acumos.workbench.predictorservice.utils.PredictorServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service("DeploymentServiceImpl")	
public class DeploymentServiceImpl implements DeploymentService {
	
	@Autowired
	private ConfigurationProperties configurationProperties;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Override
	public ResponseEntity<String> deployModelToK8s(String userId, String solutionId, String revisionId, String envId)throws IOException {
		ResponseEntity<String> deployResponse = null;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(configurationProperties.getK8deployURL()+PredictorServiceConstants.DEPLOY_API);
		DeployRequest deployRequest = new DeployRequest();
		deployRequest.setUserId(userId);
		deployRequest.setSolutionId(solutionId);
		deployRequest.setRevisionId(revisionId);
		deployRequest.setEnvId(envId);
		RestTemplate restTemplate = new RestTemplate();
		try {
			URI uri = builder.build().encode().toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DeployRequest> entity = new HttpEntity<DeployRequest>(deployRequest, headers);
			deployResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

		}
		catch (Exception e) {
			logger.error("Error occured while deploying model to k8s environment" + e.getMessage());
			throw new PredictorException("Error occured while deploying model to k8s environment", e);
		}
		return deployResponse;
	}
	
}
