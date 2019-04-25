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

package org.acumos.workbench.pipelineservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationProperties {

	@Value("${cmndatasvc.url}")
	private String cmndatasvcendpointurl;

	@Value("${cmndatasvc.user}")
	private String cmndatasvcuser;

	@Value("${cmndatasvc.pwd}")
	private String cmndatasvcpwd;

	@Value("${resultsetSize}")
	private int resultsetSize;
	
	@Value("${projectservice.url}")
	private String projectServiceURL;
	
	@Value("${jwt.secretkey}")
	private String jwtSecretKey;
	
	/**
	 * @return the jwtSecretKey
	 */
	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

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
	 * @param projectServiceURL the projectServiceURL to set
	 */
	public void setProjectServiceURL(String projectServiceURL) {
		this.projectServiceURL = projectServiceURL;
	}
	
	
	
	
	
}
