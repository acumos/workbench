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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationProperties {

	@Value("${cmndatasvc.endpointurl}")
	private String cmndatasvcendpointurl;

	@Value("${cmndatasvc.user}")
	private String cmndatasvcuser;

	@Value("${cmndatasvc.pwd}")
	private String cmndatasvcpwd;

	@Value("${resultsetSize}")
	private int resultsetSize;
	
	@Value("${projectservice.url}")
	private String projectServiceURL;
	
	@Value("${jupyterhub.url}")
	private String jupyterhubURL;
	
	@Value("${jupyterhub.token}")
	private String jupyterhubToken;
	
	@Value("${jupyterhub.storepass}")
	private String jupyterhubStorepass;
	
	@Value("${jupyterhub.installcert}")
	private boolean installCert;
	
	@Value("${jwt.secretkey}")
	private String jwtSecretKey;
	
	@Value("${outputfolder}")
	private String outputFolder;
	
	@Value("${jupyternotebook.url}")
	private String jupyterNotebookURL;
	
	/**
	 * @return the resultsetSize
	 */
	public int getResultsetSize() {
		return resultsetSize;
	}

	/**
	 * @return the cmndatasvcendpointurl
	 */
	public String getCmndatasvcendpointurl() {
		return cmndatasvcendpointurl;
	}

	/**
	 * @return the cmndatasvcuser
	 */
	public String getCmndatasvcuser() {
		return cmndatasvcuser;
	}

	/**
	 * @return the cmndatasvcpwd
	 */
	public String getCmndatasvcpwd() {
		return cmndatasvcpwd;
	}

	/**
	 * @return the projectServiceURL
	 */
	public String getProjectServiceURL() {
		return projectServiceURL;
	}

	/**
	 * @return the jupyerHubURL
	 */
	public String getJupyterhubURL() {
		return jupyterhubURL;
	}
	
	/**
	 * @return the jupyterhubToken
	 */
	public String getJupyterhubToken() {
		return "token " + jupyterhubToken;
	}

	/**
	 * @return the jwtSecretKey
	 */
	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

	/**
	 * @return the outputFolder
	 */
	public String getOutputFolder() {
		return outputFolder;
	}

	/**
	 * @return the jupyterhubStorepass
	 */
	public String getJupyterhubStorepass() {
		return jupyterhubStorepass;
	}

	/**
	 * @return the jupyterNotebookURL
	 */
	public String getJupyterNotebookURL() {
		return jupyterNotebookURL;
	}

	/**
	 * @return the installCert
	 */
	public boolean isInstallCert() {
		return installCert;
	}

	
}
