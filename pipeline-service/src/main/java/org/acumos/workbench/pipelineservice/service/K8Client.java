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
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodTemplate;
import io.kubernetes.client.models.V1PodTemplateSpec;
import io.kubernetes.client.models.V1ResourceRequirements;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1APIResource;
import io.kubernetes.client.models.V1Binding;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ConfigMapEnvSource;
import io.kubernetes.client.models.V1ConfigMapKeySelector;
import io.kubernetes.client.models.V1ConfigMapVolumeSource;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1ContainerPort;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentSpec;
import io.kubernetes.client.models.V1DeploymentStrategy;
import io.kubernetes.client.models.V1EnvVar;
import io.kubernetes.client.models.V1EnvVarSource;
import io.kubernetes.client.models.V1KeyToPath;
import io.kubernetes.client.models.V1LabelSelector;
import io.kubernetes.client.models.V1ObjectFieldSelector;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1RollingUpdateDeployment;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServicePort;
import io.kubernetes.client.models.V1ServiceSpec;
import io.kubernetes.client.models.V1Volume;
import io.kubernetes.client.models.V1VolumeDevice;
import io.kubernetes.client.models.V1VolumeMount;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.kerberos.KerberosTicket;

import com.google.protobuf.Field;

/**
 * Hello world!
 *
 */
public class K8Client {
	public static void main(String[] args) {

		//LaunchNiFi nifiCLient = new LaunchNiFi();
		//nifiCLient.createPipeline("tes-flow-file");
		
		//Pipeline pipeline = new Pipeline();
		//pipeline.oldCreatePipeline("dd1234", "pipeline-3");
		
		//CreateNiFiAndApachePod nifiAndApachePod = new CreateNiFiAndApachePod();
		//nifiAndApachePod.createNiFiInstances("pod-from-template-via-k8java-api");
		
		NiFiClient pipeline = new NiFiClient();
		pipeline.createPipeline("rs521b", "My-Pipeline-521");
	}//end-main

	public void k8Test(String[] args) {
		ApiClient client = null;
		try {
			
			client = Config.defaultClient();
			client.setDebugging(true);
			// client = Config.fromUserPassword("http://135.207.216.63:6443",
			// "kubernetes-admin", "");
			System.out.println("Contacted API Server");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api(client);

		AppsV1Api api2 = new AppsV1Api(client);

		// CoreV1Api api = new CoreV1Api();
		System.out.println("Contacted V1API:" + api.toString());

		V1Pod pod = null;
		V1ConfigMap configMap = null;
		// V1PodList podList = null;
		// V1DeploymentList deploymentList = new V1DeploymentList();
		// V1Namespace nameSpace = null;
		// V1NamespaceList nameSpaceList = null;
		try {

			/*
			 * nameSpace = new V1Namespace(); nameSpace.setApiVersion("V1");
			 * //nameSpace.setKind("namespace"); V1ObjectMeta nameSpaceMetadata = new
			 * V1ObjectMeta(); nameSpaceMetadata.setClusterName("kubernetes");
			 * nameSpaceMetadata.setNamespace("MyNameSpace");
			 * nameSpaceMetadata.setGenerateName("TestNameSpace");
			 * nameSpace.setMetadata(nameSpaceMetadata); api.createNamespace(nameSpace,
			 * null, null, null);
			 */
			Boolean includeUninitialized = new Boolean(true);
			// 1. CREATE CONFIG MAP
			// CONGIG MAP - BEGIN
			configMap = new V1ConfigMap();
			V1ObjectMeta configMapMetaData = new V1ObjectMeta();
			configMapMetaData.setName("nificonfigmap");
			configMapMetaData.setNamespace("default");
			configMap.setMetadata(configMapMetaData);
			HashMap<String, String> configMapData = new HashMap<String, String>();
			configMapData.put("userId", "kazi");
			configMapData.put("host", "localhost");
			configMapData.put("port", "9443");
			configMapData.put("nifi.security.keystore", ".conf/keystore.jks");
			configMapData.put("nifi.security.keystoreType", "jks");
			configMapData.put("nifi.security.keystorePasswd", "value-generated-by-open-ssl-keytool");
			configMapData.put("nifi.security.keyPasswd", "value-generated-by-open-ssl-keytool");
			configMapData.put("nifi.security.truststore", ".conf/keystore.jks");
			configMapData.put("nifi.security.truststoreType", "jks");
			configMapData.put("nifi.security.truststorePasswd", "value-generated-by-open-ssl-keytool");
			configMapData.put("nifi.security.user.oidc.discovery.url", "open id provider url");
			configMapData.put("nifi.security.user.oidc.client.id", "open id provided client id");
			configMapData.put("nifi.security.user.oidc.client.secret", "open id provided client secret");
			configMapData.put("CONTEXT-PATH1", "/user/kazi");
			configMapData.put("key1", "value1");
			configMap.setData(configMapData);
			configMap = api.createNamespacedConfigMap("default", configMap, includeUninitialized, null, null);
			System.out.println("CONFIG MAP YAML: ");
			System.out.println(Yaml.dump(configMap));
			System.out.println("END: CONFIG MAP YAML: ");
			// https://medium.com/@marilyn_2414/kubernetes-configmaps-a-quick-tutorial-e152a7ddc610
			// https://cloud.google.com/kubernetes-engine/docs/concepts/configmap
			// CONGIG MAP - END

			// 2. CREATE POD

			// 2.1 Create pod
			pod = new V1Pod();

			// 2.2 Set POD metadadata
			V1ObjectMeta podMetadata = new V1ObjectMeta();
			podMetadata.setName("user-kazi3");// usewrid-pod
			HashMap<String, String> podHashmap = new HashMap<String, String>();
			podHashmap.put("app", "nifi"); // pod template labels
			podMetadata.setLabels(podHashmap);
			podMetadata.setNamespace("default");// Spring env varuable - for now acumos
			pod.setMetadata(podMetadata);

			// 2.3 Set PodSpec (behavior)
			V1PodSpec podSpec = new V1PodSpec();

			V1Container container = new V1Container();
			container.setName("user-kazi3");// follow convention - TBD
			container.setImage("apache/nifi:1.9.0");
			ArrayList<String> commandList = new ArrayList<String>();
			String command = new String("/bin/sh, -c, env");
			commandList.add(command);
			// container.setCommand(commandList); For now let us comment this out
			V1ContainerPort containerPort = new V1ContainerPort();

			// Investigate setting POD type to a CLuster IP - BEGIN
			V1EnvVar env = new V1EnvVar();// container environment variable
			env.setName("CONTEXT_PATH");
			env.setValue("/user/kf");
			ArrayList<V1EnvVar> envList = new ArrayList<V1EnvVar>();
			envList.add(env);

			// TRYING HOW TO SET CONFIGMAP on POD - begin
			/*
			 * V1APIResource resource = new V1APIResource(); V1EnvVarSource envVarSource =
			 * new V1EnvVarSource();
			 * 
			 * V1ConfigMapKeySelector selector = new V1ConfigMapKeySelector();
			 * selector.setName("some-name"); selector.setKey("key");
			 * envVarSource.setConfigMapKeyRef(selector); V1ObjectFieldSelector
			 * fieldSelector = new V1ObjectFieldSelector();
			 * fieldSelector.setFieldPath("/fd/ss/ww");
			 * envVarSource.setFieldRef(fieldSelector); //
			 * envVarSource.setResourceFieldRef(resourceFieldRef); V1ResourceRequirements
			 * resourceRequirements = new V1ResourceRequirements(); V1PodTemplate
			 * podTemplate = new V1PodTemplate(); V1PodTemplateSpec podTemplateSpec = new
			 * V1PodTemplateSpec(); podTemplateSpec.setSpec(podSpec);
			 * podTemplate.setTemplate(podTemplateSpec);
			 * 
			 * V1Binding binding = new V1Binding();
			 */
			// binding.setTarget(target);
			// TRYING HOW TO SET CONFIGMAP on POD - end

			// Investigate setting POD type to a CLuster IP - END
			
			containerPort.setContainerPort(8080);
			// containerPort.setHostPort(9087);// different for each usser - We are NOT
			// using host ports - using context path so above line is commented
			ArrayList<V1ContainerPort> containerPortList = new ArrayList<V1ContainerPort>();
			containerPortList.add(containerPort);
			container.setPorts(containerPortList);
			// container.setEnv(envList);
			V1VolumeMount volumeMount = new V1VolumeMount();
			volumeMount.setName("configmap-volume"); // old - nificonfigmap
			volumeMount.setMountPath("/etc/config");

			ArrayList<V1VolumeMount> volumeMountList = new ArrayList<V1VolumeMount>();
			volumeMountList.add(volumeMount);
			container.setVolumeMounts(volumeMountList);

			// NOTE: Set Volume Pod
			V1Volume volume = new V1Volume();
			volume.setName("configmap-volume");
			V1ConfigMapVolumeSource configMapVolumeSource = new V1ConfigMapVolumeSource();
			V1KeyToPath keyToPath = new V1KeyToPath();
			keyToPath.setKey("CONTEXT-PATH1");
			keyToPath.setPath("/etc/config");
			ArrayList<V1KeyToPath> keyToPathList = new ArrayList<V1KeyToPath>();
			// configMapVolumeSource.setItems(keyToPathList);
			configMapVolumeSource.setName("nificonfigmap");
			volume.setConfigMap(configMapVolumeSource);
			ArrayList<V1Volume> volumeList = new ArrayList<V1Volume>();
			volumeList.add(volume);

			// We will not set volumes on container
			// V1VolumeDevice volumeDevice = new V1VolumeDevice();
			// volumeDevice.setDevicePath("/device/Path");
			// volumeDevice.setName("configvolumedevice");
			// ArrayList<V1VolumeDevice> volumeDeviceList = new ArrayList<V1VolumeDevice>();
			// container.setVolumeDevices(volumeDeviceList);

			ArrayList<V1Container> containerList = new ArrayList<V1Container>();
			containerList.add(container);
			podSpec.setContainers(containerList);
			podSpec.setVolumes(volumeList);
			// Note there is a volume at Pod level too
			pod.setSpec(podSpec);
			System.out.println(Yaml.dump(pod));

			// 1.4 Now create namespaced pod
			V1Pod nifiPOD = api.createNamespacedPod("default", pod, includeUninitialized, null, null);
			System.out.println("Pod YAML:");
			System.out.println(Yaml.dump(nifiPOD));
			// Test with "All" and check behavior
			// api.createNamespacedPod("default", testPod, includeUninitialized, null,
			// "All");

			// 2.0 CREATE DEPLOYMENT FOR POD - THIS IS NOT REQUIRED TO RUN POD, therefore
			// commented
			// 2.1 Create deployment
			/*
			 * V1Deployment podDeployment = new V1Deployment();
			 * 
			 * //2.2 Set Deployment metadata V1ObjectMeta podDeploymentMetadata = new
			 * V1ObjectMeta(); podDeploymentMetadata.setName("nifi-deployment5");
			 * HashMap<String, String> deploymentHashmap = new HashMap<String, String>();
			 * deploymentHashmap.put("app", "nifi"); //label for this deployment
			 * //deploymentHashmap.put("tier", "backend"); //another label for this
			 * deployment if we have one podDeploymentMetadata.setLabels(deploymentHashmap);
			 * podDeploymentMetadata.setNamespace("default");
			 * podDeployment.setMetadata(podDeploymentMetadata);
			 * 
			 * //2.3 Set DeploymentSpec (behavior) V1DeploymentSpec podDeploymentSpec = new
			 * V1DeploymentSpec(); podDeploymentSpec.setReplicas(1);//by default it is 1 -
			 * but for future changes
			 * 
			 * V1LabelSelector testPodLabelSelector = new V1LabelSelector(); //Add
			 * deployment labels if requireds HashMap<String, String> matchLabels = new
			 * HashMap<String, String>();//use this if there are multiple labels to match
			 * matchLabels.put("app", "nifi"); //matchLabels.put("labelname", "labelvalue");
			 * //another label name and value pair, if exists
			 * testPodLabelSelector.setMatchLabels(matchLabels);
			 * podDeploymentSpec.setSelector(testPodLabelSelector); //Add match labels
			 * 
			 * //comment V1DeploymentStrategy testPodDeploymentStrategy = new
			 * V1DeploymentStrategy();//Add deployment strategy if required
			 * V1RollingUpdateDeployment rollingUpdate = new V1RollingUpdateDeployment();
			 * //Add deployment strategy if required IntOrString iOrS = new IntOrString(1);
			 * //Add deployment strategy if required rollingUpdate.setMaxUnavailable(iOrS);
			 * //Add deployment strategy if required
			 * testPodDeploymentStrategy.setRollingUpdate(rollingUpdate);//Add deployment
			 * strategy if required
			 * podDeploymentSpec.setStrategy(testPodDeploymentStrategy);//Add deployment
			 * strategy if required //comment
			 * 
			 * V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
			 * podTemplateSpec.setSpec(podSpec);
			 * podTemplateSpec.setMetadata(podMetadata);//check - works now
			 * podDeploymentSpec.setTemplate(podTemplateSpec);
			 * 
			 * podDeployment.setSpec(podDeploymentSpec);
			 * System.out.println(Yaml.dump(podDeployment));
			 * 
			 * //2.4 Now create namespaced deployment V1Deployment nifiDeployment =
			 * api2.createNamespacedDeployment("default", podDeployment,
			 * includeUninitialized, null, null); System.out.println("Deployment YAML:");
			 * System.out.println(Yaml.dump(nifiDeployment));
			 */

			// podList = api.listPodForAllNamespaces(null, null, includeUninitialized, null,
			// null, null, null, null, null);
			// deploymentList = api2.listDeploymentForAllNamespaces(null, null,
			// includeUninitialized, null, null, null, null, null, null);

			// 4.0 CREATE SERVICE
			V1Service service = new V1Service();
			// 4.1 Create Service Metadata
			V1ObjectMeta serviceMetadata = new V1ObjectMeta();
			serviceMetadata.setName("kazi3-service");
			serviceMetadata.setNamespace("default");
			HashMap<String, String> serviceHashmap = new HashMap<String, String>();
			serviceHashmap.put("app", "nifi"); // service template labels
			serviceMetadata.setLabels(serviceHashmap);
			service.setMetadata(serviceMetadata);

			// 4.2 Create Service Spec
			V1ServiceSpec serviceSpec = new V1ServiceSpec();
			// V1LabelSelector servuceLabelSelector = new V1LabelSelector();
			HashMap<String, String> serviceLabels = new HashMap<String, String>();
			serviceLabels.put("app", "nifi");
			// servuceLabelSelector.setMatchLabels(serviceLabels);
			serviceSpec.setSelector(serviceLabels);

			V1ServicePort servicePort = new V1ServicePort();
			servicePort.setName("service-port-name");
			servicePort.setPort(80);
			servicePort.setProtocol("TCP");
			ArrayList<V1ServicePort> servicePortList = new ArrayList<V1ServicePort>();
			servicePortList.add(servicePort);
			serviceSpec.setPorts(servicePortList);

			serviceSpec.setType("ClusterIP");
			service.setSpec(serviceSpec);

		} catch (ApiException e) {
			// TODO Auto-generated catch block
			System.out.println("ApiExceltpion: Message");
			System.out.println();
			e.getMessage();
			System.out.println("ApiExceltpion: ResponseBody");
			e.getResponseBody();
			e.printStackTrace();
		} // end - try - catch
		/*
		 * for (V1Pod item : podList.getItems()) {
		 * System.out.println(item.getMetadata().getName()); } for(V1Deployment item:
		 * deploymentList.getItems()) {
		 * System.out.println(item.getMetadata().getName()); }
		 * 
		 * for (V1Namespace item: nameSpaceList.getItems()) {
		 * System.out.println(item.getMetadata().getName() +"   "+
		 * item.getMetadata().getClusterName()); }
		 */
	}
}
//https://howtodoinjava.com/jersey/jersey-restful-client-examples/
//https://restfulapi.net/create-rest-apis-with-jax-rs-2-0/#jax-rs-dependencies
//curl -X POST http://kong-admin-service:8001/apis -d @nifi.json
/*
 * { "strip_uri": false, "name": "nifi", "http_if_terminated": false,
 * "https_only": true, "upstream_url": "http://nifi-service:30039/", "uris": [
 * "/nifi" ], "preserve_host": false, "upstream_connect_timeout": 60000,
 * "upstream_read_timeout": 600000, "upstream_send_timeout": 600000, "retries":
 * 5 }
 */
//READ K8 YAML FILE: https://github.com/kubernetes-client/java/issues/170
//
