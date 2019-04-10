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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.notebookservice.exception.InvalidConfiguration;
import org.acumos.workbench.notebookservice.exception.TargetServiceInvocationException;
import org.acumos.workbench.notebookservice.util.ConfigurationProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceConstants;
import org.acumos.workbench.notebookservice.util.NotebookServiceProperties;
import org.acumos.workbench.notebookservice.util.NotebookServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service("ProjectServiceRestClientImpl")
public class ProjectServiceRestClientImpl implements ProjectServiceRestClient {

	

	@Autowired
	private ConfigurationProperties confprops;
	
	@Autowired
	private NotebookServiceProperties props;
	
	private RestTemplate restTemplate;
	private String basePSURL;
	
	
	@PostConstruct
	public void initializeRestClient() { 
		
		
		String surl = confprops.getProjectServiceURL();
		try {
			URL url = new URL(surl);
			this.basePSURL = surl;
		} catch (MalformedURLException e) {
			throw new InvalidConfiguration("Invalid project-service URL " + surl, e);
		}
				
		restTemplate = new RestTemplate();
	}
	
	@Override
	public ResponseEntity<Project> getProject(String authenticatedUserId,String projectId) throws TargetServiceInvocationException {
		ResponseEntity<Project> response = null;
		try { 
			Map<String, String> uriParams = new HashMap<String, String>();
			uriParams.put(NotebookServiceConstants.PATH_VAR_AUTHENTICATED_USER_ID_KEY, authenticatedUserId);
			uriParams.put(NotebookServiceConstants.PATH_VAR_PROJECT_ID_KEY, projectId);
			URI uri = NotebookServiceUtil.buildURI(this.basePSURL + NotebookServiceConstants.GET_PROJECT_PATH, uriParams);
			response = restTemplate.exchange(uri, HttpMethod.GET, null, Project.class);
		} catch (Exception e) { 
			//TODO : log error.
			throw new TargetServiceInvocationException(props.getProjectServiceGetProjectExcp());
		}
		return response;
	}
	
	
}
