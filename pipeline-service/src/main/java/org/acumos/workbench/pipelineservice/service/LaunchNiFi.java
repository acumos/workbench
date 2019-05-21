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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.acumos.workbench.pipelineservice.util.PipelineServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ConfigMapVolumeSource;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1ContainerPort;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1Volume;
import io.kubernetes.client.models.V1VolumeMount;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;


public class LaunchNiFi {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private RestTemplate restTemplate;

	public String launchNiFi(String k8Namespace, String acumosLoginId) throws ApiException {
		ApiClient client = null;
		try {

			client = Config.defaultClient();
			client.setDebugging(true);
			logger.debug("Contacted API Server");

		} catch (IOException e) {
			logger.error("Exception occured while configuring the Kubernetes ApiClient",e);
		}
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api(client);
		logger.debug("Contacted V1API:" + api.toString());
		V1Pod pod = null;
		V1ConfigMap configMap = null;

		try {
			// INIT. Set up Kong to redirect url - call Kong REST API
			Boolean includeUninitialized = new Boolean(true);
			// 1. CREATE CONFIG MAP - for every user
			configMap = new V1ConfigMap();
			V1ObjectMeta configMapMetaData = new V1ObjectMeta();
			configMapMetaData.setName("nificonfigmap".concat("-").concat(acumosLoginId));//
			configMapMetaData.setNamespace(k8Namespace);// from Sping ENV file
			configMap.setMetadata(configMapMetaData);
			HashMap<String, String> configMapData = new HashMap<String, String>();
			configMapData.put("userId", acumosLoginId);
			configMapData.put("host", "localhost");// TBD
			configMapData.put("port", "9443");// TBD
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
			configMapData.put("CONTEXT-PATH1", "/user/kazi");// placeholder for sample path
			configMapData.put("key1", "value1");// placeholder
			configMap.setData(configMapData);
			configMap = api.createNamespacedConfigMap(k8Namespace, configMap, includeUninitialized, null, null);
			logger.debug("CONFIG MAP YAML: ");
			logger.debug(Yaml.dump(configMap));
			logger.debug("END: CONFIG MAP YAML: ");
			// 2. CREATE POD
			// 2.1 Create pod
			pod = new V1Pod();

			// 2.2 Set POD metadata
			V1ObjectMeta podMetadata = new V1ObjectMeta();
			podMetadata.setName(acumosLoginId.concat("-pod"));// each pod is identifed by loginId-pod
			HashMap<String, String> podHashmap = new HashMap<String, String>();
			podHashmap.put("app", "nifi"); // pod template labels - same for all pods
			podMetadata.setLabels(podHashmap);
			podMetadata.setNamespace(k8Namespace);// Spring env varuable - for now acumos
			pod.setMetadata(podMetadata);

			// 2.3 Set PodSpec (behavior)
			V1PodSpec podSpec = new V1PodSpec();
			// 2.3.1 create container inside pod
			V1Container container = new V1Container();
			container.setName(acumosLoginId.concat("-container"));// container inside the pod is identified by
																	// loginId-container
			container.setImage("apache/nifi:1.9.0");// will stick to this release
			ArrayList<String> commandList = new ArrayList<String>();
			String command = new String("/bin/sh, -c, env"); // no need to set command as of now
			commandList.add(command);
			// container.setCommand(commandList); For now let us comment this out
			V1ContainerPort containerPort = new V1ContainerPort();

			containerPort.setContainerPort(8080);// internal port is always 8080
			// containerPort.setHostPort(9087);// different for each user - We are NOT
			// using host ports - using context path so above line is commented
			ArrayList<V1ContainerPort> containerPortList = new ArrayList<V1ContainerPort>();
			containerPortList.add(containerPort);
			container.setPorts(containerPortList);
			V1VolumeMount volumeMount = new V1VolumeMount();
			volumeMount.setName("configmap-volume");
			volumeMount.setMountPath("/maven/config/nifi/");// /maven/config/

			ArrayList<V1VolumeMount> volumeMountList = new ArrayList<V1VolumeMount>();
			volumeMountList.add(volumeMount);
			container.setVolumeMounts(volumeMountList);

			// NOTE: Set Volume Pod
			V1Volume volume = new V1Volume();
			volume.setName("configmap-volume");
			V1ConfigMapVolumeSource configMapVolumeSource = new V1ConfigMapVolumeSource();
			configMapVolumeSource.setName("nificonfigmap".concat("-").concat(acumosLoginId));
			volume.setConfigMap(configMapVolumeSource);
			ArrayList<V1Volume> volumeList = new ArrayList<V1Volume>();
			volumeList.add(volume);

			ArrayList<V1Container> containerList = new ArrayList<V1Container>();
			containerList.add(container);
			podSpec.setContainers(containerList);
			podSpec.setVolumes(volumeList);
			// Note there is a volume at Pod level too
			pod.setSpec(podSpec);
			logger.debug(Yaml.dump(pod));
			
			// 1.4 Now create namespaced pod
			V1Pod nifiPOD = api.createNamespacedPod(k8Namespace, pod, includeUninitialized, null, null);
			logger.debug("Pod YAML:");
			logger.debug(Yaml.dump(nifiPOD));

		} catch (ApiException e) {
			logger.error("Exception occured while creating NameSpaced POD",e);
		}
		return "url";
	}// end-method-launchNiFi
	
	public void createPipeline(String name) {
		//1. Create a JAX-RS client
		//ClientConfig config = new ClientConfig();
		//Client client = ClientBuilder.newClient(config);
		restTemplate = new RestTemplate();
		String localurl = "http://localhost:9090/nifi-api";
		String url = localurl + PipelineServiceConstants.PROCESS_GROUPS_ROOT;
		URI uri = buildURI(url, null);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		String responseAsString = response.getBody();
		//WebTarget target = client.target("http://localhost:9090/nifi-api");
		//Get the id of the root process group. 
		//Response getResponse = target.path("process-groups/root").request().accept(MediaType.APPLICATION_JSON).get(Response.class);
		
		String jsonInString = "{\"age\":33,\"messages\":[\"msg 1\",\"msg 2\"],\"name\":\"mkyong\"}";//sample not used
		String jsonString = "{\r\n" + 
				"			  \"revision\": {\r\n" + 
				"			    \"version\": 0\r\n" + 
				"			  },\r\n" + 
				"			  \"component\": {\r\n" + 
				"			    \"name\": \"JavaPG2\",\r\n" + 
				"			    \"comments\": \"test-process-group-2\"\r\n" + 
				"			  }\r\n" + 
				"			}";
		//String jsonString = "{\"name\":\"JavaPG1\",\"description\":\"value\",\"snippetId\":\"value\", \"disconnectedNodeAcknowledged\": true}";
		String jsonObject = new String (jsonString);	
		//2.0 POST a new process group under the root process group
		//4.0 Using the snippet id from the previous step create template
		String templatesURL = localurl + "process-groups/3804d837-016a-1000-411c-381650a99577/templates";
		//target = target.path("process-groups/3804d837-016a-1000-411c-381650a99577/templates");
		jsonString = "{\r\n" + 
				"    \"name\": \"JavaTemplate\",\r\n" + 
				"    \"description\": \"my  template from java\",\r\n" + 
				"    \"snippetId\": \"3dc7b0cd-016a-1000-816c-37bdf6d6f020\",\r\n" + 
				"    \"disconnectedNodeAcknowledged\": true\r\n" + 
				"}";
		jsonObject = new String (jsonString);
		URI latestURI = buildURI(templatesURL, null);
		ResponseEntity<String> response1 = restTemplate.exchange(latestURI, HttpMethod.POST, null, String.class);
		String responseAsString1 = response1.getBody();
	}

	private static URI buildURI(String url, Map<String, String> uriParams) {
		URI resultURI = null;
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);

		if (null != uriParams) {
			resultURI = uriBuilder.buildAndExpand(uriParams).encode().toUri();
		} else {
			resultURI = uriBuilder.build().encode().toUri();
		}
		return resultURI;
	}
}