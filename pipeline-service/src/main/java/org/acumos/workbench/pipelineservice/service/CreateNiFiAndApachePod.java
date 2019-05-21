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
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1Deployment;
//import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.models.V1beta1Ingress;
//import io.kubernetes.client.apis.AppsV1beta1Api;
//import io.kubernetes.client.apis.AppsV1beta2Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;

//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;

import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.pipelineservice.util.ConfigurationProperties;
import org.acumos.workbench.pipelineservice.util.PipelineServiceConstants;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class CreateNiFiAndApachePod {
	/**
	 * 
	 */
	private static final String USER = "USER";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private ConfigurationProperties confProps;

	//TODO : Raman add java doc
	/**
	 * 
	 * @param acumosLognId
	 * @return
	 */
	public String createNiFiInstanceForUser(String acumosLognId) {
		// NOTE: THIS METHOD CREATES BOTH A NIFI AND APACHE INSTANCE INSIDE THE USER'S POD
		String nifiURL = null;
		if(confProps.getCreatePod()) { 
			boolean error = false;
			// STEP-0: CREATE K8 CLIENT
			ApiClient client = null;
			try {
				client = Config.defaultClient();
				client.setDebugging(true);
				logger.debug("Contacted API Server");
			} catch (IOException e) {
				error = true;
				logger.error("Exception occured while configuring the K8S ApiClient",e);
				throw new TargetServiceInvocationException("Exception occured while configuring the K8S ApiClient", e);
			} 
			Configuration.setDefaultApiClient(client);
			CoreV1Api api = new CoreV1Api(client);

			// create and initialize the Velocity engine
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.init();
			Boolean includeUninitialized = new Boolean(true);
			String namespace = confProps.getNamespace(); //"default"; READ THIS FROM ENV FILE

			// STEP-1: CREATE ONE APACHE CONFIGMAP PER USER
			// Read the YAML template
			Template apacheConfigmapTemplate = velocityEngine.getTemplate(confProps.getTemplatePath()+confProps.getApacheConfigMap()); // under /maven/templates
			// create a context and add data
			VelocityContext apacheConfigmapContext = new VelocityContext();
			apacheConfigmapContext.put(USER, acumosLognId);
			// now render the template into a StringWriter
			StringWriter apacheConfigmapWriter = new StringWriter();
			apacheConfigmapTemplate.merge(apacheConfigmapContext, apacheConfigmapWriter);
			logger.debug("Writer: "+ apacheConfigmapWriter.toString());

			// Create configmap
			V1ConfigMap apacheConfigmapBody = Yaml.loadAs(apacheConfigmapWriter.toString(), V1ConfigMap.class);
			V1ConfigMap apacheConfigmap = null;
			try {
				apacheConfigmap = api.createNamespacedConfigMap(namespace, apacheConfigmapBody, includeUninitialized, null,
						null);
			} catch (ApiException e) {
				error = true;
				logger.error("Api Exception occured while Creating the NameSpace ConfigMap",e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpace ConfigMap", e);
			} 
			logger.debug("ConfigMap Name: " + apacheConfigmap.getMetadata().getName());
			logger.debug("ConfigMap Namespace: " + apacheConfigmap.getMetadata().getNamespace());
			logger.debug("ConfigMap UUID: " + apacheConfigmap.getMetadata().getUid());
			logger.debug("ConfigMap Data: " + apacheConfigmap.getData().toString());
			// STEP-2: CREATE ONE NIFI CONFIGMAP PER USER
			// Read the YAML template
			Template nifiConfigmapTemplate = velocityEngine.getTemplate(confProps.getTemplatePath()+confProps.getNifiConfigMap());// under /maven/templates
			// create a context and add data
			VelocityContext nifiConfigmapContext = new VelocityContext();
			nifiConfigmapContext.put(USER, acumosLognId);
			// now render the template into a StringWriter
			StringWriter nifiConfigmapWriter = new StringWriter();
			nifiConfigmapTemplate.merge(nifiConfigmapContext, nifiConfigmapWriter);

			// Create configmap
			V1ConfigMap nifiConfigmapBody = Yaml.loadAs(nifiConfigmapWriter.toString(), V1ConfigMap.class);
			V1ConfigMap nifiConfigmap = null;
			try {
				nifiConfigmap = api.createNamespacedConfigMap(namespace, nifiConfigmapBody, includeUninitialized, null,
						null);
			} catch (ApiException e) {
				error = true;
				logger.error("Api Exception occured while Creating the NameSpaced ConfigMap",e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced ConfigMap", e);
			} 
			logger.debug("ConfigMap Name: " + nifiConfigmap.getMetadata().getName());
			logger.debug("ConfigMap Namespace: " + nifiConfigmap.getMetadata().getNamespace());
			logger.debug("ConfigMap UUID: " + nifiConfigmap.getMetadata().getUid());
			logger.debug("ConfigMap Data: " + nifiConfigmap.getData().toString());
			// STEP-3: CREATE ONE KUBERNETES SERVICE PER USER - POINTS TO APCHE INGRESS
			// CONTROLLER AND ANOTHER POINTS DIRECTLY TO NIFI CONTAINER IN THE POD
			// Read the YAML template
			Template serviceTemplate = velocityEngine.getTemplate(confProps.getTemplatePath()+confProps.getService());// under /maven/templates
			// create a context and add data
			VelocityContext serviceContext = new VelocityContext();
			serviceContext.put(USER, acumosLognId);
			/* now render the template into a StringWriter */
			StringWriter serviceWriter = new StringWriter();
			serviceTemplate.merge(serviceContext, serviceWriter);
			// System.out.println("Writer: "+ serviceWriter.toString() );

			// Create Service
			V1Service serviceBody = Yaml.loadAs(serviceWriter.toString(), V1Service.class);
			V1Service service = null;

			try {
				service = api.createNamespacedService(namespace, serviceBody, includeUninitialized, null, null);
			} catch (ApiException e) {
				error = true;
				logger.error("Api Exception occured while Creating the NameSpaced Service",e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced Service", e);
			} 
			logger.debug("Service Name: " + service.getMetadata().getName());
			logger.debug("Service Namespace: " + service.getMetadata().getNamespace());
			logger.debug("Service UUID: " + service.getMetadata().getUid());
			// STEP-3.5: CREATE ONE KUBERNETES SERVICE PER USER - POINTS TO APCHE INGRESS
			// CONTROLLER POINTS DIRECTLY TO NIFI CONTAINER IN THE POD
			// Read the YAML template
			Template serviceAdminTemplate = velocityEngine.getTemplate(confProps.getTemplatePath()+confProps.getServiceAdmin());// under /maven/templates
			// create a context and add data
			VelocityContext serviceAdminContext = new VelocityContext();
			serviceAdminContext.put(USER, acumosLognId);
			//now render the template into a StringWriter 
			StringWriter serviceAdminWriter = new StringWriter();
			serviceAdminTemplate.merge(serviceAdminContext, serviceAdminWriter);
			// Create Service
			V1Service serviceAdminBody = Yaml.loadAs(serviceAdminWriter.toString(), V1Service.class);
			V1Service serviceAdmin = null;

			try {
				serviceAdmin = api.createNamespacedService(namespace, serviceAdminBody, includeUninitialized, null, null);
			} catch (ApiException e) {
				error = true;
				logger.error("Api Exception occured while Creating the NameSpaced Service",e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced Service", e);
			} 
			logger.debug("Admin Service Name: " + serviceAdmin.getMetadata().getName());
			logger.debug("Admin Service Namespace: " + serviceAdmin.getMetadata().getNamespace());
			logger.debug("Admin Service UUID: " + serviceAdmin.getMetadata().getUid());
			// STEP-4: CREATE ONE KUBERNETES INGRESS PER USER
			// Read the YAML template
			Template ingressTemplate = velocityEngine.getTemplate(confProps.getTemplatePath()+confProps.getIngress());// under /maven/templates
			// create a context and add data
			VelocityContext ingressContext = new VelocityContext();
			ingressContext.put(USER, acumosLognId);
			//now render the template into a StringWriter
			StringWriter ingressWriter = new StringWriter();
			ingressTemplate.merge(ingressContext, ingressWriter);

			// Create Ingress
			V1beta1Ingress ingressBody = Yaml.loadAs(ingressWriter.toString(), V1beta1Ingress.class);
			V1beta1Ingress ingress = null;
			ExtensionsV1beta1Api api3 = new ExtensionsV1beta1Api(client);

			try {
				ingress = api3.createNamespacedIngress(namespace, ingressBody, includeUninitialized, null, null);
			} catch (ApiException e) {
				error = true;
				logger.error("Api Exception occured while Creating the NameSpaced Ingress",e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced Ingress", e);
			} 
			
			logger.debug("Ingress Name: " + ingress.getMetadata().getName());
			logger.debug("Ingress Namespace: " + ingress.getMetadata().getNamespace());
			logger.debug("Ingress UUID: " + ingress.getMetadata().getUid());
			logger.debug("Ingress API Version: " + ingress.getApiVersion());
			logger.debug("Ingress Status: " + ingress.getStatus().toString());
			
			// STEP-5: CREATE ONE DEPLOYMENT WITH NIFI AND APACHE CONTAINERS IN IT
			// Read the YAML template
			Template deploymentTemplate = velocityEngine.getTemplate(confProps.getTemplatePath()+confProps.getDeployment());// under /maven/templates
			// create a context and add data
			VelocityContext deploymentContext = new VelocityContext();
			deploymentContext.put(USER, acumosLognId);
			// now render the template into a StringWriter
			StringWriter deploymentWriter = new StringWriter();
			deploymentTemplate.merge(deploymentContext, deploymentWriter);

			// Create Deployment
			V1Deployment deploymentBody = Yaml.loadAs(deploymentWriter.toString(), V1Deployment.class);
			V1Deployment deployment = null;
			AppsV1Api api2 = new AppsV1Api(client);

			try {
				deployment = api2.createNamespacedDeployment(namespace, deploymentBody, includeUninitialized, null, null);
			} catch (ApiException e) {
				error = true;
				logger.error("Api Exception occured while Creating the NameSpaced Deployment",e);
				throw new TargetServiceInvocationException("Api Exception occured while Creating the NameSpaced Deployment", e);
			} 
			logger.debug("Deployment Name: " + deployment.getMetadata().getName());
			logger.debug("Deployment Namespace: " + deployment.getMetadata().getNamespace());
			logger.debug("Deployment UUID: " + deployment.getMetadata().getUid());

		}
		// STEP-: CREATE USER'S POD WITH NIFI AND APACHE CONTAINERS INSIDE IT
		nifiURL = MessageFormat.format(confProps.getServiceBaseUrl(), acumosLognId);
		
		return nifiURL;
	}
	
}
