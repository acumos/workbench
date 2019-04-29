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

import static org.acumos.workbench.notebookservice.util.NotebookServiceConstants.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.notebookservice.exception.InvalidConfiguration;
import org.acumos.workbench.notebookservice.exception.TargetServiceInvocationException;
import org.acumos.workbench.notebookservice.util.ConfigurationProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceConstants;
import org.acumos.workbench.notebookservice.util.NotebookServiceProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.acumos.workbench.notebookservice.vo.jupyterHub.JupyterHubUser;
import org.acumos.workbench.notebookservice.vo.jupyternotebook.JNModel;
import org.acumos.workbench.notebookservice.vo.jupyternotebook.JNModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("JupyterHubRestClient")
public class JupyterHubRestClient implements NotebookRestClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String REST_CLIENT_NAME = "JupyterHub";
	private static final String NOTEBOOK_EXT = ".ipynb";
	
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
			logger.error("Invalid JupyterHub URL " + jhurl, e);
			String msg = String.format(NotebookServiceConstants.MALFORMED_URL, REST_CLIENT_NAME, jhurl);
			throw new InvalidConfiguration(msg);
		}
		logger.debug("initializeRestClient() End");
		restTemplate = new RestTemplate();
	}

	@Override
	public String launchNotebook(String authenticatedUserId, String projectId,
			Notebook notebook) {
		logger.debug("launchJupyterNotebook() Begin");
		String result = null;
		result = launchNotebookServer(authenticatedUserId);
		String notebookName = notebook.getNoteBookId().getName()+"_"+ notebook.getNoteBookId().getVersionId().getLabel() + NOTEBOOK_EXT;
		boolean notebookExists = notebookExistInJupyterNotebook(authenticatedUserId, notebookName);
		if(notebookExists) {
			result = notebook.getNoteBookId().getServiceUrl();
		} 
		
		logger.debug("launchJupyterNotebook() End");
		return result;
	}

	@Override
	public String createNotebookInNotebookServer(String authenticatedUserId, String notebookName) {
		logger.debug("createNotebookInNotebooServer() Begin");
		String notebookURL = null;
		//launchJupyterNotebookServer is called before calling this method, so need to do any validation again 
		//https://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8086/user/techmdev/tree?
		
		String notebookNameWithExt = notebookName + NOTEBOOK_EXT;
		boolean notebookExists = notebookExistInJupyterNotebook(authenticatedUserId, notebookNameWithExt);
		Map<String, String> uriParams = null;
		uriParams = new HashMap<String, String>();
		uriParams.put(PATH_VAR_NOTEBOOKNAME_KEY, notebookNameWithExt);
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		if(!notebookExists) {
			// create Notebook 
			URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_API_PATH_NOTEBOOKNAME , uriParams);
			//create headers you need to send
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
			
			JNModel jnModel = new JNModel();
			jnModel.setName(notebookNameWithExt);
			jnModel.setPath(notebookNameWithExt);
			jnModel.setType("notebook");
			jnModel.setWritable(true);
			String nobookJSON = null; 
			
			//Explicitly need to convert to JSON string as API call with JNModel fails.
			try {
				nobookJSON = objMapper.writeValueAsString(jnModel);
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException - Parse", e);
				throw new TargetServiceInvocationException(props.getJsonparserParseExcp());
			} 
			//Create entity to pass on to restTemplate
			HttpEntity<String> entity = new HttpEntity<String>(nobookJSON, httpHeaders);
			ResponseEntity<String> response = null;
			try {
				response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
			} catch (RestClientResponseException e) {
				logger.error(JUPYTERNOTEBOOK_CREATE_NOTEBOOK_EXCEPTION_MSG, e);
				throw new TargetServiceInvocationException(JUPYTERNOTEBOOK_CREATE_NOTEBOOK_EXCEPTION_MSG);
			}
		}
		
		//format the repository path
		notebookURL = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_PATH , uriParams).toString();
		
		logger.debug("createNotebookInNotebooServer() End");
		return notebookURL;
	}


	@Override
	public String updateNotebookInNotebookServer(String authenticatedUserId, String newNotebookName, String oldNotebookName) {
		logger.debug("updateNotebookInNotebookServer() Begin");
		String notebookURL = null;
		//launchJupyterNotebookServer is called before calling this method, so need to do any validation again 
		//https://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8086/user/techmdev/tree?
		
		String oldNotebookNameWithExt = oldNotebookName + NOTEBOOK_EXT;
		String newNotebookNameWithExt = newNotebookName + NOTEBOOK_EXT;
		boolean notebookExists = notebookExistInJupyterNotebook(authenticatedUserId, oldNotebookNameWithExt);
		Map<String, String> uriParams = null;
		uriParams = new HashMap<String, String>();
		uriParams.put(PATH_VAR_NOTEBOOKNAME_KEY, oldNotebookNameWithExt);
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		if(notebookExists) {
			// create Notebook 
			URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_API_PATH_NOTEBOOKNAME , uriParams);
			//create headers you need to send
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
			MediaType mediaType = new MediaType("application", "merge-patch+json");
			httpHeaders.setContentType(mediaType);
			
			JNModel jnModel = new JNModel();
			//jnModel.setName(notebookNameWithExt);
			jnModel.setPath(newNotebookNameWithExt);
			//jnModel.setType("notebook");
			jnModel.setWritable(true);
			String nobookJSON = null; 
			
			//Explicitly need to convert to JSON string as API call with JNModel fails.
			try {
				nobookJSON = objMapper.writeValueAsString(jnModel);
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException - Parse", e);
				throw new TargetServiceInvocationException(props.getJsonparserParseExcp());
			} 
			//Create entity to pass on to restTemplate
			HttpEntity<String> entity = new HttpEntity<String>(nobookJSON, httpHeaders);
			ResponseEntity<String> response = null;
			try {
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				response = restTemplate.exchange(uri, HttpMethod.PATCH, entity, String.class);
			} catch (RestClientResponseException e) {
				logger.error("JupyterNotebook - Update notebook in Jupyter Notebook server", e);
				throw new TargetServiceInvocationException("JupyterNotebook - Update notebook in Jupyter Notebook server");
			}
		}
		
		//format the repository path
		uriParams.put(PATH_VAR_NOTEBOOKNAME_KEY, newNotebookNameWithExt);
		notebookURL = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_PATH , uriParams).toString();
		
		logger.debug("updateNotebookInNotebookServer() End");
		return notebookURL;
	}
	
	@Override
	public boolean stopNotebookServer(String authenticatedUserId) {
		logger.debug("stopJupyterNotebook() Begin");
		boolean result = false;
		JupyterHubUser jbUser = null;
		//Get Jupyter Hub user
		jbUser = getJupyterHubUser(authenticatedUserId);
		if (null != jbUser.getServer() && null != jbUser.getServers()) {
			Map<String, String> uriParams = new HashMap<String, String>();
			uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
			URI uri = NotebookServiceUtil.buildURI(this.baseJHURL
					+ NotebookServiceConstants.JUPYTERHUB_LAUNCH_SERVER_PATH, uriParams);
			//create headers you need send
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY,
					confprops.getJupyterhubToken());
			//Create entity to pass on to restTemplate
			HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
			try {
				ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE,
						entity, String.class);
				if (HttpStatus.NO_CONTENT.equals(response.getStatusCode())
						|| HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
					result = true;
				} else {
					logger.error("JupyterNotebook - Launch Server");
					throw new TargetServiceInvocationException(props.getJupyternotebookLaunchExcp());

				}
			} catch (RestClientResponseException e) {
				logger.error("JupyterNotebook - Launch Server", e);
				throw new TargetServiceInvocationException(props.getJupyterhubLaunchExcp());
			}

		}
		logger.debug("stopJupyterNotebook() End");
		return result;
	}
	
	@Override
	public String launchNotebookServer(String authenticatedUserId) {
		logger.debug("launchJupyterNotebookForUser() Begin");
		String result = null;
		
		JupyterHubUser jbUser = null;
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		
		//Get Jupyter Hub user
		jbUser = getJupyterHubUser(authenticatedUserId);
		if (null == jbUser.getServer()) {
			URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + NotebookServiceConstants.JUPYTERHUB_LAUNCH_SERVER_PATH, uriParams);
			
			//create headers you need send
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
			
			//Create entity to pass on to restTemplate
			HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
			
			try {
				ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
				if(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
					result = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_SERVER_PATH, uriParams).toString() + NotebookServiceConstants.QUESTION_MARK;
				} else {
					logger.error("JupyterNotebook - Launch Server");
					throw new TargetServiceInvocationException(props.getJupyternotebookLaunchExcp());
					
				}
			} catch (RestClientResponseException e) {
				logger.error(props.getJupyterhubLaunchExcp(), e);
				throw new TargetServiceInvocationException(props.getJupyterhubLaunchExcp());
			}
		} else {
			result = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_SERVER_PATH, uriParams).toString() + NotebookServiceConstants.QUESTION_MARK;
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
			logger.error("Jupyterhub - Get User", e);
			throw new TargetServiceInvocationException(props.getJupyterhubGetUser());
		} catch (JsonParseException e) {
			logger.error("JsonParser - Parse", e);
			throw new TargetServiceInvocationException(props.getJsonparserParseExcp());
		} catch (JsonMappingException e) {
			logger.error("JsonParser - Mapping", e);
			throw new TargetServiceInvocationException(props.getJsonparserMappingExcp());
		} catch (IOException e) {
			logger.error("JsonParser - IO", e);
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
			logger.error(props.getJupyterhubCreateUserExcp(), e);
			throw new TargetServiceInvocationException(props.getJupyterhubCreateUserExcp());
		}
		logger.debug("createJupyterHubUser() End");
		return jbUser;
	}
	
	private boolean notebookExistInJupyterNotebook(String authenticatedUserId, String notebookName) {
		boolean result = false;
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_API_PATH_BASE , uriParams);
		
		//create headers you need send
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
		
		//Create entity to pass on to restTemplate
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
		ResponseEntity<JNModels> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, JNModels.class);
		} catch (RestClientResponseException e) {
			logger.error(JUPYTERNOTEBOOK_SERVER_CONTENT_EXCEPTION_MSG, e);
			throw new TargetServiceInvocationException(JUPYTERNOTEBOOK_SERVER_CONTENT_EXCEPTION_MSG);
		}
		
		if (null != response) {
			JNModels jnModels = response.getBody();
			if (null != jnModels) {
				if (null != jnModels.getContent() && jnModels.getContent().size() > 0) {
					List<JNModel> models = jnModels.getContent();
					for (JNModel model : models) {
						if (null != model.getType() && model.getType().equals("notebook")
								&& model.getName().equals(notebookName)) {
							logger.debug("Notebook with name exist in JupyterNotbook server");
							result = true;
							break;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	
	@Override 
	public boolean deleteNotebookFromNotebookServer(String authenticatedUserId, String notebookName) {
		boolean result = false;
		logger.debug("deleteNotebookFromNotebookServer() Begin");
		//launchJupyterNotebookServer is called before calling this method, so need to do any validation again 
		String notebookNameWithExt = notebookName + NOTEBOOK_EXT;
		boolean notebookExists = notebookExistInJupyterNotebook(authenticatedUserId, notebookNameWithExt);
		Map<String, String> uriParams = null;
		uriParams = new HashMap<String, String>();
		uriParams.put(PATH_VAR_NOTEBOOKNAME_KEY, notebookNameWithExt);
		uriParams.put(NotebookServiceConstants.PATH_VAR_USERNAME_KEY, authenticatedUserId);
		if(notebookExists) {
			// create Notebook 
			URI uri = NotebookServiceUtil.buildURI(this.baseJHURL + JUPYTERNOTEBOOK_API_PATH_NOTEBOOKNAME , uriParams);
			//create headers you need to send
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(NotebookServiceConstants.HEADER_AUTHORIZATION_KEY, confprops.getJupyterhubToken());
			//Create entity to pass on to restTemplate
			HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
			ResponseEntity<String> response = null;
			try {
				response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
				result = true;
			} catch (RestClientResponseException e) {
				logger.error(JUPYTERNOTEBOOK_DELETE_NOTEBOOK_EXCEPTION_MSG, e);
				throw new TargetServiceInvocationException(JUPYTERNOTEBOOK_DELETE_NOTEBOOK_EXCEPTION_MSG);
			}
		}
		logger.debug("deleteNotebookFromNotebookServer() End");
		return result;
	}
}
