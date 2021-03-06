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

import java.io.File;

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
	
	@Value("${maxtries}")
	private int maxTries;
	
	@Value("${sleeptimems}")
	private int sleepTime; 
	
	@Value("${projectservice.url}")
	private String projectServiceURL;
	
	@Value("${jwt.secretkey}")
	private String jwtSecretKey;
	
	@Value("${nifi.serviceurl}")
	private String serviceBaseUrl;
	
	@Value("${nifi.registryurl}")
	private String registryBaseUrl;
	
	@Value("${nifi.directregistryurl}")
	private String directRegistryBaseUrl;
	
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
	
	@Value("${nifi.waittime}")
	private int nifiWaitTime;
	
	@Value("${nifi.certshellfile}")
	private String certShellFile;
	
	@Value("${k8s.nifisecretname}")
	private String nifiSecretName;
	
	@Value("${k8s.nifiapacheconfigmapname}")
	private String nifiApacheConfigMapName;
	
	@Value("${k8s.nificonfigmapname}")
	private String nifiConfigMapName;
	
	@Value("${k8s.nifiServicename}")
	private String nifiServiceName;
	
	@Value("${k8s.nifiserviceadminname}")
	private String nifiServiceAdminName;
	
	@Value("${k8s.nifiingressname}")
	private String nifiIngressName;
	
	@Value("${k8s.nifideploymentname}")
	private String nifiDeploymentName;
	
	@Value("${useexternalpipeline}")
	private boolean useExternalPipeline;
	
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
		templatePath = (templatePath.charAt(templatePath.length() - 1) != File.separatorChar)
				? templatePath + File.separatorChar : templatePath;
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
	
	/**
	 * @return the certShellFile
	 */
	public String getCertShellFile() {
		return certShellFile;
	}

	/**
	 * @return the nifiWaitTime
	 */
	public int getNifiWaitTime() {
		return nifiWaitTime;
	}

	/**
	 * @return the maxTries
	 */
	public int getMaxTries() {
		return maxTries;
	}

	/**
	 * @return the nifiSecretName
	 */
	public String getNifiSecretName() {
		return nifiSecretName;
	}

	/**
	 * @return the nifiApacheConfigMapName
	 */
	public String getNifiApacheConfigMapName() {
		return nifiApacheConfigMapName;
	}

	/**
	 * @return the nifiConfigMapName
	 */
	public String getNifiConfigMapName() {
		return nifiConfigMapName;
	}

	/**
	 * @return the nifiServiceName
	 */
	public String getNifiServiceName() {
		return nifiServiceName;
	}

	/**
	 * @return the nifiServiceAdminName
	 */
	public String getNifiServiceAdminName() {
		return nifiServiceAdminName;
	}

	/**
	 * @return the sleepTime
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * @return the directRegistryBaseUrl
	 */
	public String getDirectRegistryBaseUrl() {
		return directRegistryBaseUrl;
	}

	/**
	 * @return the nifiIngressName
	 */
	public String getNifiIngressName() {
		return nifiIngressName;
	}

	/**
	 * @return the nifiDeploymentName
	 */
	public String getNifiDeploymentName() {
		return nifiDeploymentName;
	}

	/**
	 * 
	 * @return if Useexternalpipeline is true or false
	 */
	public boolean isUseexternalpipeline() {
		return useExternalPipeline;
	}
	
}
