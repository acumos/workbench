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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.acumos.workbench.notebookservice.exception.InvalidConfiguration;
import org.acumos.workbench.notebookservice.exception.TargetServiceInvocationException;
import org.acumos.workbench.notebookservice.util.ConfigurationProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceConstants;
import org.acumos.workbench.notebookservice.util.NotebookServiceProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.acumos.workbench.notebookservice.vo.jupyterHub.JupyterHubUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("JupyterHubRestClientImpl")
public class JupyterHubRestClientImpl implements JupyterHubRestClient {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private String baseJHURL;
	private ObjectMapper objMapper;
	private RestTemplate restTemplate;
	
	@Autowired
	private ConfigurationProperties confprops;
	
	@Autowired
	private NotebookServiceProperties props;
	
	@PostConstruct
	public void initializeRestClient() { 
		logger.debug("initializeRestClient() Begin");
		objMapper = new ObjectMapper();
		String jhurl = confprops.getJupyterhubURL();
		try {
			URL url = new URL(jhurl);
			this.baseJHURL = jhurl;
		} catch (MalformedURLException e) {
			logger.error("Invalid JupyterHub URL " + jhurl);
			throw new InvalidConfiguration("Invalid JupyterHub URL " + jhurl, e);
		}
		logger.debug("initializeRestClient() End");
		restTemplate = new RestTemplate();
	}

	@Override
	public String launchJupyterNotebook(String authenticatedUserId, String projectId,
			String notebookId) {
		logger.debug("launchJupyterNotebook() Begin");
		String result = null;
		JupyterHubUser jbUser = null;
		//Get Jupyter Hub user
		jbUser = getJupyterHubUser(authenticatedUserId);
		if (null != jbUser.getServer() && null != jbUser.getServers()) {
			Map<String, String> uriParams = new HashMap<String, String>();
			uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
			result = NotebookServiceUtil.buildURI(
					this.baseJHURL + NotebookServiceConstants.JUPYTERNOTEBOOK_PATH, uriParams)
					.toString() + NotebookServiceConstants.QUESTION_MARK;
		} else {
			result = launchJupyterNotebookForUser(authenticatedUserId);
		}
		logger.debug("launchJupyterNotebook() End");
		return result;
	}

	private String launchJupyterNotebookForUser(String authenticatedUserId) {
		logger.debug("launchJupyterNotebookForUser() Begin");
		String result = null;
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + NotebookServiceConstants.JUPYTERHUB_LAUNCH_SERVER_PATH, uriParams);
		
		//create headers you need send
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
		
		//Create entity to pass on to restTemplate
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
		
		try {
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			if(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
				result = NotebookServiceUtil.buildURI(this.baseJHURL + NotebookServiceConstants.JUPYTERNOTEBOOK_PATH, uriParams).toString() + NotebookServiceConstants.QUESTION_MARK;
			} else {
				logger.error("JupyterNotebook - Launch Server");
				throw new TargetServiceInvocationException(props.getJupyternotebookLaunchExcp());
				
			}
		} catch (RestClientResponseException e) {
			logger.error("JupyterNotebook - Launch Server");
			throw new TargetServiceInvocationException(props.getJupyterhubLaunchExcp());
		}
		logger.debug("launchJupyterNotebookForUser() End");
		return result;
	}

	private JupyterHubUser getJupyterHubUser(String authenticatedUserId) {
		logger.debug("getJupyterHubUser() Begin");
		JupyterHubUser jbUser = null;
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + NotebookServiceConstants.JUPYTERHUB_USER_PATH, uriParams);
		//create headers 
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
		
		//Create entity to pass on to restTemplate
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
		
		
		//check if user exist, if not then create one.
		try {
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
			String jsonStr = response.getBody();
			jbUser = objMapper.readValue(jsonStr, JupyterHubUser.class);
		} catch (HttpClientErrorException e) {
			if(HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				//Create the user
				logger.debug("createJupyterHubUser() Begin");
				jbUser = createJupyterHubUser(authenticatedUserId);
				logger.debug("createJupyterHubUser() End");
			}
		} catch (RestClientResponseException e) {
			logger.error("Jupyterhub - Get User");
			throw new TargetServiceInvocationException(props.getJupyterhubGetUser());
		} catch (JsonParseException e) {
			logger.error("JsonParser - Parse");
			throw new TargetServiceInvocationException(props.getJsonparserParseExcp());
		} catch (JsonMappingException e) {
			logger.error("JsonParser - Mapping");
			throw new TargetServiceInvocationException(props.getJsonparserMappingExcp());
		} catch (IOException e) {
			logger.error("JsonParser - IO");
			throw new TargetServiceInvocationException(props.getJsonparserIoExcp());
		}
		logger.debug("getJupyterHubUser() End");
		return jbUser;
	}
	
	
	private JupyterHubUser createJupyterHubUser(String authenticatedUserId) {
		logger.debug("createJupyterHubUser() Begin");
		JupyterHubUser jbUser = null;
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + NotebookServiceConstants.JUPYTERHUB_USER_PATH, uriParams);
		//create headers you need send
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
		//Create entity to pass on to restTemplate
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
		try {
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			String jsonStr = response.getBody();
			jbUser = objMapper.readValue(jsonStr, JupyterHubUser.class);
		} catch (Exception e) {
			logger.error("Jupyterhub - Create User");
			throw new TargetServiceInvocationException(props.getJupyterhubCreateUserExcp());
		}
		logger.debug("createJupyterHubUser() End");
		return jbUser;
	}
	
}
