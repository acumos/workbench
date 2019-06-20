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

package org.acumos.workbench.pipelineservice.service;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ConfigMapList;
import io.kubernetes.client.models.V1ContainerState;
import io.kubernetes.client.models.V1ContainerStatus;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1PodStatus;
import io.kubernetes.client.models.V1Secret;
import io.kubernetes.client.models.V1SecretList;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServiceList;
import io.kubernetes.client.models.V1beta1Ingress;
import io.kubernetes.client.models.V1beta1IngressList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.pipelineservice.exception.NiFiInstanceCreationException;
import org.acumos.workbench.pipelineservice.util.ConfigurationProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateNiFiAndApachePod {

	private static final String METADATA_NAME = "metadata.name=%s";

	private static final String NIFIPOD_NAME_PART = "nifi-";

	private static final String USER = "USER";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ConfigurationProperties confProps;
	
	private CoreV1Api coreAPI;
	
	private ApiClient client;
	
	private Boolean includeUninitialized;
	
	private String namespace;
	
	
	
	@PostConstruct
	public void initCreateNiFiAndApachePod() {
		logger.debug("InitCreateNiFiAndApachePod() Begin ");
		// STEP-0: CREATE K8 CLIENT
		client = getApiClient();
		Configuration.setDefaultApiClient(client);
		coreAPI = new CoreV1Api(client);
		
		includeUninitialized = new Boolean(true);
		 
		namespace = confProps.getNamespace(); // "default";
		logger.debug("InitCreateNiFiAndApachePod() End ");
	}

	/**
	 * Creates the Nifi-Service pod for the User.
	 * 
	 * @param acumosLoginId
	 *            accepts acumosLognId
	 * @return return nifiUrl
	 */
	@SuppressWarnings("resource")
	public String createNiFiInstanceForUser(String acumosLoginId) {
		logger.debug("createNiFiInstanceForUser() begin");
		// NOTE: THIS METHOD CREATES BOTH A NIFI AND APACHE INSTANCE INSIDE THE USER'S POD
		String nifiURL = null;
		if (confProps.getCreatePod()) {
			
			// STEP-3.7: CREATE ONE KUBERNETES SECRET PER USER
			createSecret(acumosLoginId);
			
			// create and initialize the Velocity engine
			String userTemplatePath = confProps.getTemplatePath() + "conf" + File.separatorChar + acumosLoginId + File.separatorChar; // /maven/templates/conf/$User/
			logger.debug(" userTemplatePath : " + userTemplatePath);
			VelocityEngine velocityEngine = new VelocityEngine();
			Properties velocityProp = new Properties();
			velocityProp.setProperty("file.resource.loader.path", userTemplatePath);
			velocityEngine.init(velocityProp);
			
			// STEP-1: CREATE ONE APACHE CONFIGMAP PER USER
			// Read the YAML template
			createApacheConfigMap(acumosLoginId, velocityEngine);
			
			// STEP-2: CREATE ONE NIFI CONFIGMAP PER USER
			// Read the YAML template
			createNifiConfigMap(acumosLoginId, velocityEngine);
			
			// STEP-3: CREATE ONE KUBERNETES SERVICE PER USER - POINTS TO APCHE
			// INGRESS CONTROLLER AND ANOTHER POINTS DIRECTLY TO NIFI CONTAINER IN THE POD
			// Read the YAML template
			createService(acumosLoginId, velocityEngine);
			
			// STEP-3.5: CREATE ONE KUBERNETES SERVICE PER USER - POINTS TO APCHE INGRESS
			// CONTROLLER POINTS DIRECTLY TO NIFI CONTAINER IN THE POD
			// Read the YAML template
			createServiceAdmin(acumosLoginId, velocityEngine);

			// STEP-4: CREATE ONE KUBERNETES INGRESS PER USER
			// Read the YAML template
			createIngress(acumosLoginId, velocityEngine);

			// STEP-5: CREATE ONE DEPLOYMENT WITH NIFI AND APACHE CONTAINERS IN IT
			// Read the YAML template
			createDeployment(acumosLoginId, velocityEngine);
			
			//Wait for pod to get created and Ready 
			int nifiWaitTime = confProps.getNifiWaitTime();
			if(nifiWaitTime > 0) {
				logger.debug("As nifi.waittime is set to greater than 0 value, pipeline-service will sleep for specified time");
				try {
					Thread.sleep(nifiWaitTime);
				} catch (InterruptedException e) {
					logger.error("Interrupted Exception occured while Creating the K8S CreateWatch at NiFi Wait Time", e);
					throw new NiFiInstanceCreationException("Interrupted Exception occured while Creating the K8S CreateWatch at NiFi Wait Time ");
				}
			}
			
		}
		// STEP-: CREATE USER'S POD WITH NIFI AND APACHE CONTAINERS INSIDE IT
		nifiURL = MessageFormat.format(confProps.getServiceBaseUrl(), acumosLoginId);
		logger.debug("createNiFiInstanceForUser() end");
		return nifiURL;
	}

	/**
	 * Check if Nifi-service pod for a user already exists and is running. But if nifi.createpod is set to false, then return true always.
	 * @param acumosLoginId
	 * 		acumos loginId of user.
	 * @return boolean
	 * 		returns true if Nifi-service pod for a user already exists and is running or else false.
	 * 		returns true, if nifi.createpod is set to false.
	 */
	public boolean checkifNifiPodRunning(String acumosLoginId) {
		logger.debug("CheckifNifiPodRunning() Begin");
		boolean nifiPodRunning = true;
		boolean podFound = false;
		String labelSelector = "app="+NIFIPOD_NAME_PART + acumosLoginId;
		if (confProps.getCreatePod()) {
			try {
				V1PodList podList = coreAPI.listNamespacedPod(confProps.getNamespace(), includeUninitialized, null, null, null, labelSelector, null, null, null, Boolean.FALSE);
				List<V1Pod> pods = podList.getItems();
				if(pods == null || pods.size() == 0) {
					nifiPodRunning = false;
				} else {
					for(V1Pod pod : pods) {
						V1PodStatus podStatus = pod.getStatus();
						String name = pod.getMetadata().getName();
						String status = podStatus.getPhase();
						String kind = pod.getKind();
						String details = podStatus.toString();
						logger.debug(String.format(
								"NAME: %s | KIND: %s | STATUS: %s | DETAILS: %n%s%n====================%n", name, kind,
								status, details));
						if (name.contains(NIFIPOD_NAME_PART + acumosLoginId)) {
							logger.debug("Pod found ");
							podFound = true;
							List<V1ContainerStatus> containerStatuses =  podStatus.getContainerStatuses();
							for(V1ContainerStatus cStatus : containerStatuses) {
								V1ContainerState state = cStatus.getState();
								logger.debug(String.format("Container %s Status : %s", cStatus.getImage(), state.toString())); 
								if(state.getRunning() == null) {
									nifiPodRunning = false;
									break;
								}
							}
						}
						if(nifiPodRunning) { 
							break;
						}
					}
					if(!podFound) {
						nifiPodRunning = false;
					}
				}
			} catch (ApiException e) {
				logger.error("Api Exception occured while checking if Nifi Service Pod exists for User", e);
				throw new TargetServiceInvocationException("Api Exception occured while checking if Nifi Service Pod exists for User", e);
			}
		}
		logger.debug("CheckifNifiPodRunning() End");
		return nifiPodRunning;
	}
	
	private void createDeployment(String acumosLoginId, VelocityEngine velocityEngine) {
		logger.debug("CreateDeployment() Begin");
		Template deploymentTemplate = velocityEngine.getTemplate(confProps.getDeployment());// under /maven/templates
		String deploymentName = MessageFormat.format(confProps.getNifiDeploymentName(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, deploymentName);
		boolean deploymentExists = checkK8sDeployment(fieldSelector);
		if(!deploymentExists) { 
			// create a context and add data
			VelocityContext deploymentContext = new VelocityContext();
			deploymentContext.put(USER, acumosLoginId);
			// now render the template into a StringWriter
			StringWriter deploymentWriter = new StringWriter();
			deploymentTemplate.merge(deploymentContext, deploymentWriter);

			// Create Deployment
			V1Deployment deploymentBody = Yaml.loadAs(deploymentWriter.toString(), V1Deployment.class);
			V1Deployment deployment = null;
			AppsV1Api api2 = new AppsV1Api(client);

			try {
				deployment = api2.createNamespacedDeployment(namespace, deploymentBody, includeUninitialized, null,null);
			} catch (ApiException e) {
				logger.error("Api Exception occured while Creating the NameSpaced Deployment", e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced Deployment", e);
			}
			logger.debug("Deployment Name: " + deployment.getMetadata().getName());
			logger.debug("Deployment Namespace: " + deployment.getMetadata().getNamespace());
			logger.debug("Deployment UUID: " + deployment.getMetadata().getUid());
		} else {
			logger.debug("Nifi Deployment already exists for a user");
		}
		logger.debug("CreateDeployment() End");
	}

	private boolean checkK8sDeployment(String fieldSelector) {
		logger.debug("CheckK8sDeployment() Begin");
		boolean exists = false;
		AppsV1Api api = new AppsV1Api(client);
		try {
			V1DeploymentList deploymentList = api.listNamespacedDeployment("acumos", includeUninitialized, null, null, fieldSelector, null, null, null, null, Boolean.FALSE);
			List<V1Deployment> deployments = 	deploymentList.getItems();
			if(null != deployments && deployments.size() > 0) {
				V1Deployment deployment = deployments.get(0);
				logger.debug("Version :"+ deployment.getApiVersion());
				logger.debug("Name :"+ deployment.getMetadata().getName());
				logger.debug("creationTimestamp :"+ deployment.getMetadata().getCreationTimestamp());
				logger.debug("resourceVersion :"+ deployment.getMetadata().getResourceVersion());
				logger.debug("uuid :"+ deployment.getMetadata().getUid());
				if(null != deployment.getMetadata().getCreationTimestamp()) {
					exists = true;
				}
					
			} else {
				logger.debug("Ingress not found : " + fieldSelector);
			}
		} catch (ApiException e) {
			logger.error("Exception occured while checking K8s Nifi Deployment : ", e);
			throw new TargetServiceInvocationException("Exception occured while checking K8s Nifi Deployment : ", e);
		}
		logger.debug("CheckK8sDeployment() End");
		return exists;
	}
	private void createIngress(String acumosLoginId, VelocityEngine velocityEngine) {
		logger.debug("CreateIngress() Begin");
		Template ingressTemplate = velocityEngine.getTemplate(confProps.getIngress());// under /maven/templates
		String ingressName = MessageFormat.format(confProps.getNifiIngressName(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, ingressName);
		boolean ingressExists = checkK8sIngress(fieldSelector);
		if(!ingressExists) {
			// create a context and add data
			VelocityContext ingressContext = new VelocityContext();
			ingressContext.put(USER, acumosLoginId);
			// now render the template into a StringWriter
			StringWriter ingressWriter = new StringWriter();
			ingressTemplate.merge(ingressContext, ingressWriter);

			// Create Ingress
			V1beta1Ingress ingressBody = Yaml.loadAs(ingressWriter.toString(), V1beta1Ingress.class);
			V1beta1Ingress ingress = null;
			ExtensionsV1beta1Api api3 = new ExtensionsV1beta1Api(client);

			try {
				ingress = api3.createNamespacedIngress(namespace, ingressBody, includeUninitialized, null, null);
			} catch (ApiException e) {
				logger.error("Api Exception occured while Creating the NameSpaced Ingress", e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced Ingress", e);
			}

			logger.debug("Ingress Name: " + ingress.getMetadata().getName());
			logger.debug("Ingress Namespace: " + ingress.getMetadata().getNamespace());
			logger.debug("Ingress UUID: " + ingress.getMetadata().getUid());
			logger.debug("Ingress API Version: " + ingress.getApiVersion());
			logger.debug("Ingress Status: " + ingress.getStatus().toString());
		} else {
			logger.debug("Nifi Ingress alreay exists for a user");
		}
		logger.debug("CreateIngress() End");
	}

	private boolean checkK8sIngress(String fieldSelector) {
		logger.debug("CheckK8sIngress() Begin");
		boolean exists = false;
		ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(client);
		try {
			V1beta1IngressList ingressList = api.listNamespacedIngress("acumos", includeUninitialized, null, null, fieldSelector, null, null, null, null, Boolean.FALSE);
			List<V1beta1Ingress> ingresslst = ingressList.getItems();
			if(null != ingresslst && ingresslst.size() > 0) {
				V1beta1Ingress ingress = ingresslst.get(0);
				logger.debug("Version :"+ ingress.getApiVersion());
				logger.debug("Name :"+ ingress.getMetadata().getName());
				logger.debug("creationTimestamp :"+ ingress.getMetadata().getCreationTimestamp());
				logger.debug("resourceVersion :"+ ingress.getMetadata().getResourceVersion());
				logger.debug("uuid :"+ ingress.getMetadata().getUid());
				if(null != ingress.getMetadata().getCreationTimestamp()) {
					exists = true;
				}
			} else {
				logger.debug("Ingress not found : " + fieldSelector);
			}
		} catch (ApiException e) {
			logger.error("Exception occured while checking K8s Nifi Ingress : ", e);
			throw new TargetServiceInvocationException("Exception occured while checking K8s Nifi Ingress : ", e);
		}
		logger.debug("CheckK8sIngress() End");
		return exists;
	}
	private void createServiceAdmin(String acumosLoginId, VelocityEngine velocityEngine) {
		logger.debug("CreateServiceAdmin() Begin");
		Template serviceAdminTemplate = velocityEngine.getTemplate(confProps.getServiceAdmin());// under /maven/templates
		String serviceAdminName = MessageFormat.format(confProps.getNifiServiceAdminName(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, serviceAdminName);
		boolean serviceAdminExists = checkK8sService(fieldSelector);
		if(!serviceAdminExists) {
			// create a context and add data
			VelocityContext serviceAdminContext = new VelocityContext();
			serviceAdminContext.put(USER, acumosLoginId);
			// now render the template into a StringWriter
			StringWriter serviceAdminWriter = new StringWriter();
			serviceAdminTemplate.merge(serviceAdminContext, serviceAdminWriter);
			// Create Service
			V1Service serviceAdminBody = Yaml.loadAs(serviceAdminWriter.toString(), V1Service.class);
			V1Service serviceAdmin = null;

			try {
				serviceAdmin = coreAPI.createNamespacedService(namespace, serviceAdminBody, includeUninitialized, null,null);
			} catch (ApiException e) {
				logger.error("Exception occured while Creating the ServiceAdmin Template", e);
				throw new TargetServiceInvocationException("Exception occured while Creating the ServiceAdmin Template",e);
			}
			logger.debug("Admin Service Name: " + serviceAdmin.getMetadata().getName());
			logger.debug("Admin Service Namespace: " + serviceAdmin.getMetadata().getNamespace());
			logger.debug("Admin Service UUID: " + serviceAdmin.getMetadata().getUid());
		} else {
			logger.debug("ServiceAdmin already exists for a user");
		}
		logger.debug("CreateServiceAdmin() End");
	}

	private void createService(String acumosLoginId, VelocityEngine velocityEngine) {
		logger.debug("CreateService() Begin");
		Template serviceTemplate = velocityEngine.getTemplate(confProps.getService());// under /maven/templates
		String serviceName = MessageFormat.format(confProps.getNifiServiceName(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, serviceName);
		boolean serviceExists = checkK8sService(fieldSelector);
		if(!serviceExists) {
			// create a context and add data
			VelocityContext serviceContext = new VelocityContext();
			serviceContext.put(USER, acumosLoginId);
			/* now render the template into a StringWriter */
			StringWriter serviceWriter = new StringWriter();
			serviceTemplate.merge(serviceContext, serviceWriter);

			// Create Service
			V1Service serviceBody = Yaml.loadAs(serviceWriter.toString(), V1Service.class);
			V1Service service = null;

			try {
				service = coreAPI.createNamespacedService(namespace, serviceBody, includeUninitialized, null, null);
			} catch (ApiException e) {
				logger.error("Exception occured while Creating the service Template", e);
				throw new TargetServiceInvocationException("Exception occured while Creating the service Template", e);
			}
			logger.debug("Service Name: " + service.getMetadata().getName());
			logger.debug("Service Namespace: " + service.getMetadata().getNamespace());
			logger.debug("Service UUID: " + service.getMetadata().getUid());
		} else {
			logger.debug("Nifi Service already exists for a user");
		}
		logger.debug("CreateService() End");
	}

	private boolean checkK8sService(String fieldSelector) {
		logger.debug("CheckK8sService() Begin");
		boolean exists = false;
		try {
			V1ServiceList serviceList = coreAPI.listNamespacedService("acumos", includeUninitialized, null, null, fieldSelector, null, null, null, null, Boolean.FALSE);
			List<V1Service> services = serviceList.getItems();
			if(null != services && services.size() > 0) {
				V1Service service = services.get(0);
				logger.debug("Version :"+ service.getApiVersion());
				logger.debug("Name :"+ service.getMetadata().getName());
				logger.debug("creationTimestamp :"+ service.getMetadata().getCreationTimestamp());
				logger.debug("resourceVersion :"+ service.getMetadata().getResourceVersion());
				logger.debug("uuid :"+ service.getMetadata().getUid());
				if(null != service.getMetadata().getCreationTimestamp()) {
					exists = true;
				}
			} else {
				logger.debug("service not found : " + fieldSelector);
			}
		} catch (ApiException e) {
			logger.error("Exception occured while checking K8s Nifi Service : ", e);
			throw new TargetServiceInvocationException("Exception occured while checking K8s Nifi Service : ", e);
		}
		logger.debug("CheckK8sService() End");
		return exists;
	}
	
	private void createNifiConfigMap(String acumosLoginId, VelocityEngine velocityEngine) {
		logger.debug("CreateNifiConfigMap() Begin");
		Template nifiConfigmapTemplate = velocityEngine.getTemplate(confProps.getNifiConfigMap());// under /maven/templates
		String secretName = MessageFormat.format(confProps.getNifiConfigMap(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, secretName);
		boolean nifiConfigMapExists = checkK8sConfigMap(fieldSelector);
		if(!nifiConfigMapExists) {
			// create a context and add data
			VelocityContext nifiConfigmapContext = new VelocityContext();
			nifiConfigmapContext.put(USER, acumosLoginId);
			// now render the template into a StringWriter
			StringWriter nifiConfigmapWriter = new StringWriter();
			nifiConfigmapTemplate.merge(nifiConfigmapContext, nifiConfigmapWriter);

			// Create configmap
			V1ConfigMap nifiConfigmapBody = Yaml.loadAs(nifiConfigmapWriter.toString(), V1ConfigMap.class);
			V1ConfigMap nifiConfigmap = null;
			try {
				nifiConfigmap = coreAPI.createNamespacedConfigMap(namespace, nifiConfigmapBody, includeUninitialized, null,
						null);
			} catch (ApiException e) {
				logger.error("Exception occured while Creating the Nifi ConfigMap", e);
				throw new TargetServiceInvocationException("Exception occured while Creating the Nifi ConfigMap", e);
			}
			logger.debug("ConfigMap Name: " + nifiConfigmap.getMetadata().getName());
			logger.debug("ConfigMap Namespace: " + nifiConfigmap.getMetadata().getNamespace());
			logger.debug("ConfigMap UUID: " + nifiConfigmap.getMetadata().getUid());
			logger.debug("ConfigMap Data: " + nifiConfigmap.getData().toString());
		} else {
			logger.debug("Nifi ConfigMap already exists for a user");
		}
		logger.debug("CreateNifiConfigMap() End");
	}

	private void createApacheConfigMap(String acumosLoginId, VelocityEngine velocityEngine) {
		logger.debug("CreateApacheConfigMap() Begin");
		Template apacheConfigmapTemplate = null;
		String apacheConfigMapName = MessageFormat.format(confProps.getNifiApacheConfigMapName(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, apacheConfigMapName);
		boolean apacheConfigMapExists = checkK8sConfigMap(fieldSelector);
		if(!apacheConfigMapExists) { 
			try {
				apacheConfigmapTemplate = velocityEngine.getTemplate(confProps.getApacheConfigMap()); // under /maven/templates/conf/$User
			} catch (Exception e) {
				logger.error("Exception occured while creating the ApacheConfigMap",e);
				throw new TargetServiceInvocationException("Exception occured while creating the ApacheConfigMap",e);
			}
			
			// create a context and add data
			VelocityContext apacheConfigmapContext = new VelocityContext();
			apacheConfigmapContext.put(USER, acumosLoginId);
			// now render the template into a StringWriter
			StringWriter apacheConfigmapWriter = new StringWriter();
			apacheConfigmapTemplate.merge(apacheConfigmapContext, apacheConfigmapWriter);
			logger.debug("ApacheConfigmap : " + apacheConfigmapWriter.toString());

			// Create configmap
			V1ConfigMap apacheConfigmapBody = Yaml.loadAs(apacheConfigmapWriter.toString(), V1ConfigMap.class);
			V1ConfigMap apacheConfigmap = null;
			try {
				apacheConfigmap = coreAPI.createNamespacedConfigMap(namespace, apacheConfigmapBody, includeUninitialized,null, null);
			} catch (ApiException e) {
				logger.error("Exception occured while Creating the Apache ConfigMap", e);
				throw new TargetServiceInvocationException("Exception occured while Creating the Apache ConfigMap", e);
			}
			logger.debug("ConfigMap Name: " + apacheConfigmap.getMetadata().getName());
			logger.debug("ConfigMap Namespace: " + apacheConfigmap.getMetadata().getNamespace());
			logger.debug("ConfigMap UUID: " + apacheConfigmap.getMetadata().getUid());
			logger.debug("ConfigMap Data: " + apacheConfigmap.getData().toString());
		} else {
			logger.debug("Nifi Appache Config Map already exists for a user");
		}
		logger.debug("CreateApacheConfigMap() End");
	}

	private boolean checkK8sConfigMap(String fieldSelector) {
		logger.debug("CheckK8sConfigMap() Begin");
		boolean exists = false;
		try {
			V1ConfigMapList configMapList = coreAPI.listNamespacedConfigMap("acumos", includeUninitialized, null, null, fieldSelector, null, null, null, null, Boolean.FALSE);
			List<V1ConfigMap> configMaps = configMapList.getItems();
			if(null != configMaps && configMaps.size() > 0) {
				V1ConfigMap configMap = configMaps.get(0);
				logger.debug("Version :"+ configMap.getApiVersion());
				logger.debug("Name :"+ configMap.getMetadata().getName());
				logger.debug("creationTimestamp :"+ configMap.getMetadata().getCreationTimestamp());
				logger.debug("resourceVersion :"+ configMap.getMetadata().getResourceVersion());
				logger.debug("uuid :"+ configMap.getMetadata().getUid());
				if(null != configMap.getMetadata().getCreationTimestamp()) {
					exists = true;
				}
				
			} else {
				logger.debug("config Map not found : " + fieldSelector);
			}
		} catch (ApiException e) {
			logger.error("Exception occured while checking K8s Nifi Configmap : ", e);
			throw new TargetServiceInvocationException("Exception occured while checking K8s Nifi ConfigMap : ", e);
		}
		logger.debug("CheckK8sConfigMap() End");
		return exists;
	}
	
	private void createSecret(String acumosLoginId) {
		logger.debug("CreateSecret() Begin");
		logger.debug("Executing Shell script to create required certificat, set the Kuberenetes secret and update the templated and store it in user template dir ."); // /maven/templaets/conf/$User
		String templatepath = confProps.getTemplatePath();
		String[] cmd = { templatepath + confProps.getCertShellFile(), acumosLoginId };
		String secretName = MessageFormat.format(confProps.getNifiSecretName(), acumosLoginId);
		String fieldSelector = String.format(METADATA_NAME, secretName);
		boolean secretExists = checkK8sSecret(fieldSelector);
		if(!secretExists) {
			try {
				logger.debug("Executing shell script : " + templatepath + confProps.getCertShellFile() + " "+ acumosLoginId);
				Process process = Runtime.getRuntime().exec(cmd);
				InputStream stderr = process.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				logger.debug("<ERROR>");
				while ((line = br.readLine()) != null)
					logger.debug(" Line : " + line);
				int exitVal = process.waitFor();
				logger.debug("Process exitValue: " + exitVal);
			} catch (IOException | InterruptedException e) {
				logger.error("Exception occured while executing shell script : " + confProps.getTemplatePath()+ File.separatorChar + confProps.getCertShellFile(), e);
				throw new TargetServiceInvocationException("Exception occured while executing shell script : "+ confProps.getTemplatePath() + File.separatorChar + confProps.getCertShellFile(), e);
			}
		} else {
			logger.debug("Secret already exists for a User");
		}
		logger.debug("CreateSecret() End");
	}
	
	private boolean checkK8sSecret(String fieldSelector) {
		logger.debug("CheckK8sSecret() Begin");
		boolean exists = false;
		try {
			V1SecretList secretList = coreAPI.listNamespacedSecret("acumos", includeUninitialized, null, null, fieldSelector, null, null, null, null, Boolean.FALSE);
			List<V1Secret> secrets = secretList.getItems();
			if(null != secrets && secrets.size() > 0) {
				V1Secret secret = secrets.get(0);
				logger.debug("Version :"+ secret.getApiVersion());
				logger.debug("Secret Name :"+ secret.getMetadata().getName());
				logger.debug("Secret creationTimestamp :"+ secret.getMetadata().getCreationTimestamp());
				logger.debug("Secret resourceVersion :"+ secret.getMetadata().getResourceVersion());
				logger.debug("Secret uuid :"+ secret.getMetadata().getUid());
				if(null != secret.getMetadata().getCreationTimestamp()) {
					exists = true;
				}
				
			} else {
				logger.debug("Secret not found : " + fieldSelector);
			}
		} catch (ApiException e) {
			logger.error("Exception occured while checking K8s Nifi Secret : ", e);
			throw new TargetServiceInvocationException("Exception occured while checking K8s Nifi Secret : ", e);
		}
		logger.debug("CheckK8sSecret() End");
		return exists;
	}
	
	
	private ApiClient getApiClient() {
		logger.debug("getApiClient() Begin");
		ApiClient client = null;
		try {
			client = Config.defaultClient();
			client.setDebugging(true);
			logger.debug("Contacted API Server");
		} catch (IOException e) {
			logger.error("Exception occured while configuring the K8S ApiClient", e);
			throw new TargetServiceInvocationException("Exception occured while configuring the K8S ApiClient", e);
		}
		logger.debug("getApiClient() End");
		return client;
	}

}
