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
	
	@Value("${nifi.serviceurl}")
	private String serviceBaseUrl;
	
	@Value("${nifi.registryurl}")
	private String registryBaseUrl;
	
	@Value("${nifi.registryname}")
	private String registryName;
	
	@Value("${nifi.namespace}")
	private String namespace;	
	
	@Value("${nifi.template.path}")
	private String templatePath;
	
	@Value("${nifi.template.apacheconfigmap}")
	private String apacheConfigMap;
	
	@Value("${nifi.template.nificonfigmap}")
	private String nifiConfigMap;
	
	@Value("${nifi.template.service}")
	private String service;
	
	@Value("${nifi.template.serviceadmin}")
	private String serviceAdmin;
	
	@Value("${nifi.template.ingress}")
	private String ingress;
	
	@Value("${nifi.template.deployment}")
	private String deployment;
	
	@Value("${nifi.createpod}")
	private boolean createPod;
	
	@Value("${nifi.clientpassword}")
	private String clientPassword;
	
	@Value("${nifi.clientcertificatepath}")
	private String clientCertificatePath;
	
	@Value("${nifi.truststorepath}")
	private String trustStorePath;
	
	@Value("${nifi.truststorepassword}")
	private String trustStorePassword;
	
	@Value("${nifi.adminusername}")
	private String nifiAdminUser;
	
	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the registryName
	 */
	public String getRegistryName() {
		return registryName;
	}

	/**
	 * @return the serviceBaseUrl
	 */
	public String getServiceBaseUrl() {
		return serviceBaseUrl+PipelineServiceConstants.NIFI_API;
	}

	/**
	 * @return the registryBaseUrl
	 */
	public String getRegistryBaseUrl() {
		return registryBaseUrl+PipelineServiceConstants.NIFI_REGISTRY_API_PATH;
	}

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
	 * @return the templatePath
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * @return the apacheConfigMap
	 */
	public String getApacheConfigMap() {
		return apacheConfigMap;
	}

	/**
	 * @return the nifiConfigMap
	 */
	public String getNifiConfigMap() {
		return nifiConfigMap;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @return the serviceAdmin
	 */
	public String getServiceAdmin() {
		return serviceAdmin;
	}

	/**
	 * @return the ingress
	 */
	public String getIngress() {
		return ingress;
	}

	/**
	 * @return the deployment
	 */
	public String getDeployment() {
		return deployment;
	}

	/**
	 * @return the createPod
	 */
	public boolean getCreatePod() {
		return createPod;
	}

	/**
	 * @return the clientPassword
	 */
	public String getClientPassword() {
		return clientPassword;
	}

	/**
	 * @return the clientCertificatePath
	 */
	public String getClientCertificatePath() {
		return clientCertificatePath;
	}

	/**
	 * @return the trustStorePath
	 */
	public String getTrustStorePath() {
		return trustStorePath;
	}

	/**
	 * @return the trustStorePassword
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	/**
	 * @return the nifiAdminUser
	 */
	public String getNifiAdminUser() {
		return nifiAdminUser;
	}

	
}
