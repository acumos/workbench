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

package org.acumos.workbench.notebookservice.util;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotebookServiceProperties implements Serializable {
	
	
	private static final long serialVersionUID = -8735607372719158404L;

	@Value("${msg.invalidNotebookName:Invalid Notebook Name}")
	private String invalidNotebookName;
	
	@Value("${msg.missingFieldValue}")
	private String missingFieldValue;
	
	@Value("${msg.invalidNotebookVersion}")
	private String invalidNotebookVersion;
	
	@Value("${msg.invalidNotebookType}")
	private String invalidNotebookType;

	@Value("${jupyternotebook.launch.excp}")
	private String jupyternotebookLaunchExcp;

	@Value("${jupyterhub.launch.excp}")
	private String jupyterhubLaunchExcp;

	@Value("${jupyterhub.get.user.excp}")
	private String jupyterhubGetUser;

	@Value("${jupyterhub.create.user.excp}")
	private String jupyterhubCreateUserExcp;

	@Value("${jsonparser.parse.excp}")
	private String jsonparserParseExcp;

	@Value("${jsonparser.mapping.excp}")
	private String jsonparserMappingExcp;

	@Value("${jsonparser.io.excp}")
	private String jsonparserIoExcp;

	@Value("${cds.get.notebook.excp}")
	private String cdsGetNotebookExcp;

	@Value("${cds.search.notebooks.excp}")
	private String cdsSearchNotebooksExcp;

	@Value("${cds.create.notebook.excp}")
	private String cdsCreateNotebookExcp;

	@Value("${cds.add.project.notebook.excp}")
	private String cdsAddProjectNotebookExcp;

	@Value("${cds.update.notebook.excp}")
	private String cdsUpdateNotebookExcp;

	@Value("${cds.get.project.notebooks.excp}")
	private String cdsGetProjectNotebooksExcp;

	@Value("${cds.get.user.excp}")
	private String cdsGetUserExcp;

	@Value("${project.service.get.project.excp}")
	private String projectServiceGetProjectExcp;
	
	@Value("${cds.get.notebook.projects.excp}")
	private String cdsGetNotebookProjectsExcp;

	@Value("${cds.drop.project.notebook.excp}")
	private String cdsDropProjectNotebookExcp;
	
	@Value("${cds.delete.notebook.excp}")
	private String cdsDeleteNotebookExcp; 
	
	
	@Value("${kubernetes.enabled}")
	private boolean kubernetesEnabled;
	
	@Value("${kubernetes.waittime}")
	private int kubernetesWaitTime;
	
	@Value("${maxtries}")
	private int maxTries;
	
	/**
	 * @return the invalidNotebookName
	 */
	public String getInvalidNotebookName() {
		return invalidNotebookName;
	}

	/**
	 * @return the missingFieldValue
	 */
	public String getMissingFieldValue() {
		return missingFieldValue;
	}

	/**
	 * @return the invalidNotebookVersion
	 */
	public String getInvalidNotebookVersion() {
		return invalidNotebookVersion;
	}

	/**
	 * @return the invalidNotebookType
	 */
	public String getInvalidNotebookType() {
		return invalidNotebookType;
	}

	/**
	 * @return the jupyternotebookLaunchExcp
	 */
	public String getJupyternotebookLaunchExcp() {
		return jupyternotebookLaunchExcp;
	}

	/**
	 * @return the jupyterhubLaunchExcp
	 */
	public String getJupyterhubLaunchExcp() {
		return jupyterhubLaunchExcp;
	}

	/**
	 * @return the jupyterhubGetUser
	 */
	public String getJupyterhubGetUser() {
		return jupyterhubGetUser;
	}

	/**
	 * @return the jupyterhubCreateUserExcp
	 */
	public String getJupyterhubCreateUserExcp() {
		return jupyterhubCreateUserExcp;
	}

	/**
	 * @return the jsonparserParseExcp
	 */
	public String getJsonparserParseExcp() {
		return jsonparserParseExcp;
	}

	/**
	 * @return the jsonparserMappingExcp
	 */
	public String getJsonparserMappingExcp() {
		return jsonparserMappingExcp;
	}

	/**
	 * @return the jsonparserIoExcp
	 */
	public String getJsonparserIoExcp() {
		return jsonparserIoExcp;
	}

	/**
	 * @return the cdsGetNotebookExcp
	 */
	public String getCdsGetNotebookExcp() {
		return cdsGetNotebookExcp;
	}

	/**
	 * @return the cdsSearchNotebooksExcp
	 */
	public String getCdsSearchNotebooksExcp() {
		return cdsSearchNotebooksExcp;
	}

	/**
	 * @return the cdsCreateNotebookExcp
	 */
	public String getCdsCreateNotebookExcp() {
		return cdsCreateNotebookExcp;
	}

	/**
	 * @return the cdsAddProjectNotebookExcp
	 */
	public String getCdsAddProjectNotebookExcp() {
		return cdsAddProjectNotebookExcp;
	}

	/**
	 * @return the cdsUpdateNotebookExcp
	 */
	public String getCdsUpdateNotebookExcp() {
		return cdsUpdateNotebookExcp;
	}

	/**
	 * @return the cdsGetProjectNotebooksExcp
	 */
	public String getCdsGetProjectNotebooksExcp() {
		return cdsGetProjectNotebooksExcp;
	}

	/**
	 * @return the cdsGetUserExcp
	 */
	public String getCdsGetUserExcp() {
		return cdsGetUserExcp;
	}

	/**
	 * @return the projectServiceGetProjectExcp
	 */
	public String getProjectServiceGetProjectExcp() {
		return projectServiceGetProjectExcp;
	}

	/**
	 * @return the cdsGetNotebookProjectsExcp
	 */
	public String getCdsGetNotebookProjectsExcp() {
		return cdsGetNotebookProjectsExcp;
	}

	/**
	 * @return the cdsDropProjectNotebookExcp
	 */
	public String getCdsDropProjectNotebookExcp() {
		return cdsDropProjectNotebookExcp;
	}

	/**
	 * @return the cdsDeleteNotebookExcp
	 */
	public String getCdsDeleteNotebookExcp() {
		return cdsDeleteNotebookExcp;
	}

	/**
	 * @return the kubernetesEnabled
	 */
	public boolean isKubernetesEnabled() {
		return kubernetesEnabled;
	}

	/**
	 * @return the kubernetesWaitTime
	 */
	public int getKubernetesWaitTime() {
		return kubernetesWaitTime;
	}

	/**
	 * @return the maxTries
	 */
	public int getMaxTries() {
		return maxTries;
	}

	
}
