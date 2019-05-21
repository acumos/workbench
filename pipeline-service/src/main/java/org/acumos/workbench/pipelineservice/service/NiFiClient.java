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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.pipelineservice.exception.DuplicatePipeLineException;
import org.acumos.workbench.pipelineservice.exception.SecurityValidationException;
import org.acumos.workbench.pipelineservice.k8s.AcumosRegistryData;
import org.acumos.workbench.pipelineservice.k8s.BucketData;
import org.acumos.workbench.pipelineservice.k8s.PipelineData;
import org.acumos.workbench.pipelineservice.util.ConfigurationProperties;
import org.acumos.workbench.pipelineservice.util.PipelineServiceConstants;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class NiFiClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private RestTemplate restTemplate;
	
	@Autowired
	private ConfigurationProperties configProps;
	
	@Autowired
	private PipeLineServiceImpl plServiceImpl;
	
	//TODO : Raman to complet the Java Doc. 
	/**
	 * 
	 * @param acumosLoginId
	 * @param pipelineName
	 * @return
	 */
	public String createPipeline(String acumosLoginId, String pipelineName) {
		String flowURL = null;
		String nifiURL = null;
		boolean acumosRegistryConfiguredInNiFi = false;
		boolean userBucketExistsInRegistry = false;
		boolean pipelineNameExistsInBucket = false;
		String rootProcessGroupId = null;// NiFi has a root process group
		String acumosRegistryId = null;
		String userBucketId = null;
		String userFlowName = null;// aka pipelineName
		String userFlowId = null;// aka pipelineId
		AcumosRegistryData acumosRegistryData = null;
		BucketData bucketData = null;
		PipelineData pipelineData = null;
		
		// STEP - 0: SET UP REST CLIENT - common for all methods called from  this method
		String nifiRegistryBaseURL = configProps.getRegistryBaseUrl();

		// PART -A: CREATE NIFI INSTANCE FOR THE USER
		// STEP-1: CHECK IF THE NIFi CONTAINER FOR THE USER IS ALREADY CREATED
		// Get the list of pipelines for user.  If pipeline with service URL exists then NIFI container exists else create new one.
		nifiURL = getNifiURL(acumosLoginId);
				
		// PART - B: CHECK IF THE NIFI REGISTRY IS CONFIGURED IN user's NIFI INSTANCE
		// STEP - 2: CHECK IF ACUMOS REGISTRY IS CONFIGGURED IN NIFI
		// Note that ideally this step needs to be done only once after the NiFi
		// instance is created for the user, but it is possible that the user might
		// delete the NiFi Registry from his NiFi instance, and the newly created
		// pipeline will not be configured in the NiFi.
		if(null == restTemplate){
			restTemplate =  initRestTemplate();
		}
		acumosRegistryData = checkIfAcumosRegistryIsConfiguredInNiFi(restTemplate, nifiURL);
		acumosRegistryConfiguredInNiFi = acumosRegistryData.isRegistryConfigured();
		if (!acumosRegistryConfiguredInNiFi) {
			// Step: 2.1 If Registry is not configured, then configure it in NiFi instance
			acumosRegistryData = configureAcumosRegistryInNiFi(restTemplate, nifiURL);
		}

		// STEP-C: GET ROOT PROCESS GROUP ID
		// this is required to Provision the pipeline in user’s NiFi
		rootProcessGroupId = getNiFiRootProcessGroupId(restTemplate, nifiURL);

		// STEP-1: CHECK IF THE BUCKET EXISTS FOR THIS USER IN THE NIFI REGISTRY
		// NOTE: Each acumos user has a bucket in the NiFi-registry
		// The bucket is named using acumos user login id

		bucketData = checkIfBucketExistsForUserInNiFiRegistry(restTemplate, nifiRegistryBaseURL, acumosLoginId);
		userBucketExistsInRegistry = bucketData.isBucketExists();
		String nifiRegistryUserId = null;
		if (!userBucketExistsInRegistry) {
			// STEP-2: CREATE USER'S BUCKET IN NIFI REGISTRY
			bucketData = createUserBucketInNiFiRegistry(restTemplate, nifiRegistryBaseURL, acumosLoginId);
			// STEP-2.1: CREATE A USER IN NIFI REGISTRY WITH THE SAME NAME AS ACUMOS LOGIN ID
			nifiRegistryUserId = createUserInNiFiRegistry(restTemplate, nifiRegistryBaseURL, acumosLoginId);
			// STEP-2.2: ALLOW USER TO READ HIS BUCKET - UPDATES authorizers.xml in /conf dir
			
			updateUserPolicyInNiFiRegistry(restTemplate, nifiRegistryBaseURL, acumosLoginId, bucketData.getBucketId(), nifiRegistryUserId, "read");
			// STEP-2.3: ALLOW USER TO WRITE TO THIS BUCKET - UPDATES authorizers.xml in /conf dir
			updateUserPolicyInNiFiRegistry(restTemplate, nifiRegistryBaseURL, acumosLoginId, bucketData.getBucketId(), nifiRegistryUserId, "write");
			updateUserPolicyInNiFiRegistry(restTemplate, nifiRegistryBaseURL, acumosLoginId, bucketData.getBucketId(), nifiRegistryUserId, "delete");
		} else {
			
			logger.debug("Skipping bucket creation...bucket already exists for user " + acumosLoginId + " in NiFi Registry.");
			
		}

		// STEP - 3: CHECK IF PIPELINE (aka flow) ALREADY EXISTS IN USER'S BUCKET
		// note: This may happen if the user has created a pipeline/flow directly in
		// NiFi Registry outside of MLWB THEREFORE THIS CHECK
		userBucketId = bucketData.getBucketId();
		pipelineData = checkIfPipelineExistsInUserBucket(restTemplate, nifiRegistryBaseURL, acumosLoginId,
				userBucketId, pipelineName);
		pipelineNameExistsInBucket = pipelineData.isPipelineExists();
		if (!pipelineNameExistsInBucket) {
			// STEP-4: CREATE FLOW (I.E., MLWB PIPELINE) IF IT DOES NOT EXISTS IN USER'S BUCKET IN REGISTRY
			pipelineData = createFlowInNiFiRegistry(restTemplate, nifiRegistryBaseURL, userBucketId, pipelineName);
			userFlowId = pipelineData.getPipelineId();
			// STEP-5: UPGRADE FLOW VERSION FROM 0 TO 1
			upgradeFlowVersionInNiFiRegistry(restTemplate, nifiRegistryBaseURL, userBucketId, userFlowId);
			// STEP-6: PROVISION FLOW IN NIFI - last step
			acumosRegistryId = acumosRegistryData.getRegistryId();
			userFlowName = pipelineData.getPipelineName();
			
			flowURL = provisionFlowInNiFi(restTemplate, nifiURL, rootProcessGroupId, acumosRegistryId, userBucketId,
					userFlowId, userFlowName, acumosLoginId);
		} else {
			logger.debug("Skipping flow creation...flow " + pipelineName + " already exists for user " + acumosLoginId
					+ " in NiFi Registry.");
			// THROW AN EXCEPTION TO MLWB-UI - REQUESTED PIPELINE NAME ALREADY EXISTS BOTH IN NIFI IN AND REGISTRY
			throw new DuplicatePipeLineException("Request PipeLine Name : " + pipelineName + " already Exists in Both NiFi Server and NiFi Registry");
			
		}
		return flowURL;
	}

	/**
	 * @param acumosLoginId
	 * @param nifiURL
	 */
	private String getNifiURL(String acumosLoginId) {
		String nifiURL = null;
		List<Pipeline> pipelines = plServiceImpl.getPipelines(acumosLoginId, null);
		if(null != pipelines && pipelines.size() > 0 ) {
			String serviceURL = null;
			URL url = null;
			String nifihost = null;
			int nifiport;
			String nifiProtocol = null;
			for(Pipeline pipeline :  pipelines) {
				serviceURL = pipeline.getPipelineId().getServiceUrl();
				if(null != serviceURL && !serviceURL.trim().equals("")){
					try {
						url = buildURI(serviceURL, null).toURL();
						nifihost = url.getHost();
						nifiport = url.getPort();
						nifiProtocol = url.getProtocol();
						nifiURL = String.format("%s://%s:%d%s", nifiProtocol, nifihost, nifiport,PipelineServiceConstants.NIFI_API);
						break;
					} catch (MalformedURLException e) {
						logger.error("Exception while constructing NIFI URL from NIFI flow URL ", e);
						//TODO : Raman to throw approptirate exception.
					}
				}
			}
		}
		if (nifiURL == null) {
			// Step-1.1 Create NiFi instance for the user
			nifiURL = createNiFiInstance(acumosLoginId);
		}
		logger.debug("Nifi Server URL : " + nifiURL);
		return nifiURL;
	}

	@SuppressWarnings("unused")
	private String getNiFiUrlFromCDS(String acumosLoginId) {
		String nifiURL = null;
		// Check CDS USER Table if user's NiFi is populated in userId.serviceURL
		return nifiURL;
	}

	
	private String createNiFiInstance(String acumosLoginId) {
		String nifiURL = null;
		// Call the Kubernetes API to create a NiFi Instance
		CreateNiFiAndApachePod createNiFi = new CreateNiFiAndApachePod();
		
		nifiURL = createNiFi.createNiFiInstanceForUser(acumosLoginId);
		
		return nifiURL;
	}

	@SuppressWarnings("unused")
	private void storeNiFiURLInCDS(String acumosLoginId, String url) {
		// Store NiFi url in userId. ServiceURL field of CDS
	}

	private AcumosRegistryData checkIfAcumosRegistryIsConfiguredInNiFi(RestTemplate restTemplate, String nifiBaseURL) {
		// STEP-A: CHECK IF THE NIFI REGISTRY IS CONFIGURED IN user's NIFI INSTANCE
		AcumosRegistryData acumosRegistryData = new AcumosRegistryData();
		try {
			acumosRegistryData.setRegistryConfigured(false);// initialize as false

			String url = nifiBaseURL + PipelineServiceConstants.FLOW_REGISTRIES; 
			URI uri = buildURI(url, null);
			logger.debug("PATH : " + uri);
			
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("authuser", configProps.getNifiAdminUser());
			
			HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
			// get the list of configured registries from NiFi instance Response
			ResponseEntity<String> getAllRegistriesInNiFiResponse = restTemplate.exchange(uri, HttpMethod.GET, entity,
					String.class);
			String allRegistriesInNiFiString = getAllRegistriesInNiFiResponse.getBody();
			// Parse the registry list as JSON Object
			Object allRegistriesInNiFiObject = null;
			try {
				allRegistriesInNiFiObject = new JSONParser().parse(allRegistriesInNiFiString);
			} catch (ParseException e) {
				logger.error("Exception occured while parsing " + allRegistriesInNiFiString);
			}
			// the response is a single JSON object with a single element registries.
			// Registries is a JSON Array
			JSONObject responseObject = (JSONObject) allRegistriesInNiFiObject;

			JSONArray allRegistriesInNiFiJsonArray = (JSONArray) responseObject.get("registries");
			// Each element of registries list is a JSON Object
			// Retrieve JSON object from registries list

			// Search and retreive the Acumos-Registry if it exists in the registry list
			Iterator<?> registryIterator = allRegistriesInNiFiJsonArray.iterator();
			JSONObject registry;
			String registryName;
			String registryIdentifier;
			String registryUri;
			while (registryIterator.hasNext()) {
				registry = (JSONObject) ((JSONObject) registryIterator.next()).get("registry");
				registryName = (String) registry.get("name");
				registryIdentifier = (String) registry.get("id");
				registryUri = (String) registry.get("uri");
				logger.debug("Registry : " + registry);
				if ((registryName).equalsIgnoreCase(configProps.getRegistryName())) {
					acumosRegistryData.setRegistryConfigured(true);
					acumosRegistryData.setRegistryName(registryName);
					acumosRegistryData.setRegistryUri(registryUri);
					acumosRegistryData.setRegistryId(registryIdentifier);
					logger.debug("Acumos Registry Name:" + registryName);
					logger.debug("Acumos Registry Id:" + registryIdentifier);
					logger.debug("Acumos Registry URI:" + registryUri);
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Exception while NiFi Registry Configuration ",e);
			throw new TargetServiceInvocationException("Exception while NiFi Registry Configuration ",e);
		}
		return acumosRegistryData;
	}


	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private AcumosRegistryData configureAcumosRegistryInNiFi(RestTemplate client, String nifiBaseURL) {
		AcumosRegistryData acumosRegistryData = new AcumosRegistryData();

		// Create, i.e., configure Acumos Registry in NiFi via POST request
		// Create JSONObject for POST Request
		JSONObject configureRegistryInNiFiPostRequestBody = new JSONObject();

		// Populate the config registry POST data in the JSON object
		LinkedHashMap versionHashMap = new LinkedHashMap<String, String>(1);
		versionHashMap.put("version", 0);
		configureRegistryInNiFiPostRequestBody.put("revision", versionHashMap);

		versionHashMap = new LinkedHashMap<String, String>(2);
		versionHashMap.put("name", configProps.getRegistryName());
		versionHashMap.put("uri", configProps.getRegistryBaseUrl());
		configureRegistryInNiFiPostRequestBody.put("component", versionHashMap);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json"));
		headers.set("authuser", configProps.getNifiAdminUser());
		String configureRegistryInNiFiPostRequestString = new String(configureRegistryInNiFiPostRequestBody.toString());

		HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(configureRegistryInNiFiPostRequestBody, headers);

		String url = nifiBaseURL + PipelineServiceConstants.CONTROLLER_REGISTRY_CLIENTS; 
				
		URI uri = buildURI(url, null);
		logger.debug("PATH : " + uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

		String configureRegistryInNIFiPostResponseString = response.getBody();
		
		// NOTE: STORE THIS IN CDS IN USER TABLE - EACH  USER HAS ONE NIFI
		boolean acumosRegistryConfiguredInNiFi = true;
		//LOCAL INSTANCE. SO WE DON'T HAVE TO RUN THIS ULTIPLE TIMES
		logger.debug("Acumos NiFi Registry configured in NiFi");

		// Parse the returned Registry response string as JSON Object
		Object acumosNiFiRegistryObject = null;
		try {
			acumosNiFiRegistryObject = new JSONParser().parse(configureRegistryInNIFiPostResponseString);
		} catch (ParseException e) {
			logger.error("Exception occured while parsing " + configureRegistryInNIFiPostResponseString);
		} 

		// type casting Java Object to JSSONArray because allBucketsInRegistryJsonObject
		// is essentially an array of buckets
		JSONObject acumosNiFiRegistryJSONObject = (JSONObject) acumosNiFiRegistryObject;

		JSONObject component = (JSONObject) acumosNiFiRegistryJSONObject.get("component");
		// LOCAL - 3 below
		String acumosRegistryName = (String) component.get("name");
		String acumosRegistryId = (String) component.get("id");
		String acumosRegistryUri = (String) component.get("uri");
		logger.debug("Acumos regostry Name:" + acumosRegistryName);
		logger.debug("Acumos regostry Id:" + acumosRegistryId);
		logger.debug("Acumos regostry URI:" + acumosRegistryUri);

		acumosRegistryData.setRegistryConfigured(acumosRegistryConfiguredInNiFi);
		acumosRegistryData.setRegistryName(acumosRegistryName);
		acumosRegistryData.setRegistryId(acumosRegistryId);
		acumosRegistryData.setRegistryUri(acumosRegistryUri);
		return acumosRegistryData;
	}

	private String getNiFiRootProcessGroupId(RestTemplate client, String nifiBaseURL) {
		//STEP-C: GET ROOT PROCESS GROUP ID
		String rootProcessGroupId = null;
		String url = nifiBaseURL + PipelineServiceConstants.PROCESS_GROUPS_ROOT;
		URI uri = buildURI(url, null);
		logger.debug("PATH : " + uri);
		
		//create headers you need to send
		HttpHeaders headers = new HttpHeaders();
		headers.set("authuser", configProps.getNifiAdminUser());

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> getRootProcessGroupIdResponse = restTemplate.exchange(uri, HttpMethod.GET, entity,
				String.class);
		// Retrieve the root process group id from received response
		String rootProcessGroupResponseString = getRootProcessGroupIdResponse.getBody();
		logger.debug("Root Process Group Response : " + rootProcessGroupResponseString);
		// Parse the returned root process group id response string as JSON Object
		Object rootProcessGroupResponseObject = null;
		try {
			rootProcessGroupResponseObject = new JSONParser().parse(rootProcessGroupResponseString);
		} catch (ParseException e) {
			logger.error("Exception occured while Parsing " + rootProcessGroupResponseString);
		} 
		// type casting Java Object to JSON Object
		JSONObject rootProcessGroupResponseJSONObject = (JSONObject) rootProcessGroupResponseObject;
		rootProcessGroupId = (String) rootProcessGroupResponseJSONObject.get("id");
		logger.debug("NiFi Root Process Group Id:" + rootProcessGroupId);
		return rootProcessGroupId;
	}

	private BucketData checkIfBucketExistsForUserInNiFiRegistry(RestTemplate client, String nifiRegistryBaseURL,
			String acumosLoginId) {
		BucketData bucketData = new BucketData();
		bucketData.setBucketExists(false);
		String url = nifiRegistryBaseURL + PipelineServiceConstants.BUCKETS; 
		URI uri = buildURI(url, null);
		logger.debug("PATH : " + uri);
		
		//create headers you need to send
		HttpHeaders headers = new HttpHeaders();
		headers.set("authuser", configProps.getNifiAdminUser());

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> getAllBucketsInRegistryResponse = restTemplate.exchange(uri, HttpMethod.GET, entity,
				String.class);
		String allBucketsInRegistryString = getAllBucketsInRegistryResponse.getBody();
		logger.debug("All Buckets in Registry : "+ allBucketsInRegistryString);
		// Parse the bucketList as JSON Object
		Object allBucketsInRegistryObject = null;
		try {
			allBucketsInRegistryObject = new JSONParser().parse(allBucketsInRegistryString);
		} catch (ParseException e) {
			logger.error("Exception occured while Parsing " + allBucketsInRegistryString);
		}
		// type casting Java Object to JSSONArray because allBucketsInRegistryJsonObject
		// is essentially an array of buckets
		JSONArray allBucketsInRegistryJsonArray = (JSONArray) allBucketsInRegistryObject;

		Iterator<?> itr = allBucketsInRegistryJsonArray.iterator();
		JSONObject bucket;
		String bucketName;
		String bucketIdentifier;
		while (itr.hasNext()) {
			bucket = (JSONObject) itr.next();
			bucketName = (String) bucket.get("name");
			bucketIdentifier = (String) bucket.get("identifier");
			logger.debug("Bucket Name : " + bucketName);
			logger.debug("Bucket Identifier : " + bucketIdentifier);
			if ((bucketName).equalsIgnoreCase(acumosLoginId)) {
				bucketData.setBucketExists(true);
				bucketData.setBucketName(bucketName);
				bucketData.setBucketId(bucketIdentifier);
				break;
			} 
		} 
		return bucketData;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private BucketData createUserBucketInNiFiRegistry(RestTemplate client, String nifiRegistryBaseURL,
			String acumosLoginId) {
		BucketData bucketData = new BucketData();
		bucketData.setBucketExists(false);
		// Create Bucket POST request JSON body creating JSONObject
		logger.debug("Creating bucket for user: " + acumosLoginId);
		JSONObject createBucketPostRequestBody = new JSONObject();
		// Put data inside JSON object
		createBucketPostRequestBody.put("name", acumosLoginId);
		String createBucketPostRequestObject = new String(createBucketPostRequestBody.toString());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json"));
		headers.set("authuser", configProps.getNifiAdminUser());
		
		HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(createBucketPostRequestBody, headers);
		String url = nifiRegistryBaseURL + PipelineServiceConstants.BUCKETS; 
		URI uri = buildURI(url, null);
		logger.debug("PATH : " + uri);
		ResponseEntity<String> createBucketPostResponse = restTemplate.exchange(uri, HttpMethod.POST, entity,
				String.class);
		String newBucketString = createBucketPostResponse.getBody();
		logger.debug("New Bucket Details : " + newBucketString);
		// Parse the newly created bucket as JSON Object
		Object newlyCreatedBucketObject = null;
		try {
			newlyCreatedBucketObject = new JSONParser().parse(newBucketString);
		} catch (ParseException e) {
			logger.error("Exception occured while Parsing " + newBucketString);
		}
		// Parse the Java Object to JSONObject
		JSONObject newlyCreatedBucketJsonObject = (JSONObject) newlyCreatedBucketObject;
		// Add the newly created bucket's name and identifier to userBucketName
		// and userBucketId field
		String userBucketName = (String) newlyCreatedBucketJsonObject.get("name");
		String userBucketId = (String) newlyCreatedBucketJsonObject.get("identifier");
		bucketData.setBucketExists(true);
		bucketData.setBucketName(userBucketName);
		bucketData.setBucketId(userBucketId);
		return bucketData;
	}

	private PipelineData checkIfPipelineExistsInUserBucket(RestTemplate client, String nifiRegistryBaseURL,
			String acumosLoginId, String userBucketId, String pipelineName) {
		PipelineData pipelineData = new PipelineData();
		pipelineData.setPipelineExists(false);
		// 3.1 Get the list of all flows (i.e. MLWB pipelines) under the user's bucket
		// Retrieve bucket list from received response
		String url = nifiRegistryBaseURL + PipelineServiceConstants.USER_BUCKET_FLOWS;  
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("userBucketId", userBucketId);
		
		URI uri = buildURI(url, uriParams);
		logger.debug("PATH : " + uri); 
		
		//create headers you need to send
		HttpHeaders headers = new HttpHeaders();
		headers.set("authuser", configProps.getNifiAdminUser());

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> getAllFlowsUnderBucketResponse = restTemplate.exchange(uri, HttpMethod.GET, entity,
				String.class);
		String allFlowsInBucketString = getAllFlowsUnderBucketResponse.getBody();
		logger.debug("All Flows in Bucket : " + allFlowsInBucketString);
		// 3.1.1 Parse the response as JSONArray
		// Parse the list of flows as JSON Object
		Object allFlowsInBucketObject = null;
		try {
			allFlowsInBucketObject = new JSONParser().parse(allFlowsInBucketString);
		} catch (ParseException e) {
			logger.error("Exception occured while Parsing " + allFlowsInBucketString);
		}
		// Type casting Java Object to JSSONArray because allFlowsInBucketObject is
		// essentially an array of flows
		JSONArray allFlowsInBucketJsonArray = (JSONArray) allFlowsInBucketObject;

		Iterator<?> flowIterator = allFlowsInBucketJsonArray.iterator();
		JSONObject flow;
		String flowName;
		String flowIdentifier;
		while (flowIterator.hasNext()) {
			flow = (JSONObject) flowIterator.next();
			flowName = (String) flow.get("name");
			flowIdentifier = (String) flow.get("identifier");
			logger.debug("Flow Name : " + flowName);
			logger.debug("Flow Identifier : " + flowIdentifier);
			if ((flowName).equalsIgnoreCase(pipelineName)) {
				pipelineData.setPipelineExists(true);
				pipelineData.setPipelineName(flowName);
				pipelineData.setPipelineId(flowIdentifier);
				break;
			}
		} 

		return pipelineData;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private PipelineData createFlowInNiFiRegistry(RestTemplate client, String nifiRegistryBaseURL, String userBucketId,
			String pipelineName) {
		// Create Flow POST request JSON body
		// create JSONObject
		PipelineData pipelineData = new PipelineData();

		JSONObject createFlowPostRequestBody = new JSONObject();
		// Put data inside JSON object
		createFlowPostRequestBody.put("name", pipelineName);
		createFlowPostRequestBody.put("type", "Flow");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json"));
		headers.set("authuser", configProps.getNifiAdminUser());
		
		HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(createFlowPostRequestBody, headers);

		String createFlowPostRequestObject = new String(createFlowPostRequestBody.toString());

		String url = nifiRegistryBaseURL + PipelineServiceConstants.USER_BUCKET_FLOWS; 
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("userBucketId", userBucketId);
		URI uri = buildURI(url, uriParams);
		logger.debug("PATH : " + uri); 
		ResponseEntity<String> createFlowPostResponse = restTemplate.exchange(uri, HttpMethod.POST, entity,
				String.class);
		String newFlowString = createFlowPostResponse.getBody();
		logger.debug("NiFi Flow Response : " + newFlowString);

		// Parse the newly created flow as JSON Object
		Object newlyCreatedFlowObject = null;
		try {
			newlyCreatedFlowObject = new JSONParser().parse(newFlowString);
		} catch (ParseException e) {
			logger.error("Exception occured while Parsing " + newFlowString);
		}
		// Parse the Java Object to JSONObject
		JSONObject newlyCreatedFlowJsonObject = (JSONObject) newlyCreatedFlowObject;
		// Add the newly created bucket's name and identifier to userBucketName and userBucketId field
		String userFlowName = (String) newlyCreatedFlowJsonObject.get("name");
		String userFlowId = (String) newlyCreatedFlowJsonObject.get("identifier");
		pipelineData.setPipelineExists(true);
		pipelineData.setPipelineName(userFlowName);
		pipelineData.setPipelineId(userFlowId);
		logger.debug("User Flow Name : " + userFlowName);
		logger.debug("User Flow Id : " + userFlowId);
		return pipelineData;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private void upgradeFlowVersionInNiFiRegistry(RestTemplate client, String nifiRegistryBaseURL, String userBucketId,
			String userFlowId) {
		// ***STEP-5: CHANGE THE FLOW VERSION TO 1
		// Creste JSONObject for POSTing Flow Version
		JSONObject changeFlowVersionPostRequestBody = new JSONObject();
		// Put data inside JSON object
		changeFlowVersionPostRequestBody.put("latest", true);

		// JSONObject snapshotMetadata = new JSONObject();
		LinkedHashMap m = new LinkedHashMap<String, String>(4);
		m.put("bucketIdentifier", userBucketId);
		m.put("flowIdentifier", userFlowId);
		m.put("version", 1);
		m.put("comments", "version changed from 0 to 1 and ChangeMe process group is added");
		changeFlowVersionPostRequestBody.put("snapshotMetadata", m);

		// JSONObject flowContents = new JSONObject();
		m = new LinkedHashMap<String, String>(2);
		m.put("componentType", "PROCESS_GROUP");
		m.put("name", "ChangeMe");
		changeFlowVersionPostRequestBody.put("flowContents", m);

		String changeFlowVersionPostRequestString = new String(changeFlowVersionPostRequestBody.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json"));
		headers.set("authuser", configProps.getNifiAdminUser());
		
		HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(changeFlowVersionPostRequestBody, headers);

		String url = nifiRegistryBaseURL + PipelineServiceConstants.USER_BUCKET_FLOW_VERSION; 
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("userBucketId", userBucketId);
		uriParams.put("userFlowId", userFlowId);
		URI uri = buildURI(url, uriParams);
		logger.debug("PATH : " + uri); 
		ResponseEntity<String> changeFlowVersionPostResponse = restTemplate.exchange(uri, HttpMethod.POST, entity,
				String.class);
		String changeFlowVersionPostResponseString = changeFlowVersionPostResponse.getBody();
		logger.debug("Flow Version Post Response : " + changeFlowVersionPostResponseString);

	}

	@SuppressWarnings({ "unchecked", "unused" })
	private String provisionFlowInNiFi(RestTemplate client, String nifiBaseURL, String rootProcessGroupId,
			String acumosRegistryId, String userBucketId, String userFlowId, String userFlowName,
			String acumosLoginId) {
		String flowURL = null;
		try {
			// PROVISION THE FLOW (aka MLWB Pipeline) IN USER'S NIFI INSTANCE (POST) create JSONObject
			JSONObject provisionFlowInNiFiRequestBody = new JSONObject();

			// Put data inside JSON object
			JSONObject revision = new JSONObject();
			revision.put("version", 0);

			JSONObject component = new JSONObject();

			JSONObject versionControlInformation = new JSONObject();
			versionControlInformation.put("groupId", rootProcessGroupId);
			versionControlInformation.put("registryId", acumosRegistryId);
			versionControlInformation.put("bucketId", userBucketId);
			versionControlInformation.put("flowId", userFlowId);
			versionControlInformation.put("version", 1);

			component.put("versionControlInformation", versionControlInformation);
			provisionFlowInNiFiRequestBody.put("revision", revision);
			provisionFlowInNiFiRequestBody.put("component", component);

			String provisionFlowInNiFiRequestString = new String(provisionFlowInNiFiRequestBody.toString());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json"));
			headers.set("authuser", configProps.getNifiAdminUser());
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(provisionFlowInNiFiRequestBody, headers);

			String url = nifiBaseURL + PipelineServiceConstants.PROCESS_GROUPS_ROOT_PROCESSID; 
			
			Map<String, String> uriParams = new HashMap<String, String>();
			uriParams.put("rootProcessGroupId", rootProcessGroupId);
			URI uri = buildURI(url, uriParams);
			logger.debug("PATH : " + uri);
			ResponseEntity<String> provisionFlowInNiFiPostResponse = restTemplate.exchange(uri, HttpMethod.POST, entity,
					String.class);
			String provisionFlowInNiFiPostResponseString = provisionFlowInNiFiPostResponse.getBody();
			logger.debug("Provision FLow in NiFi Post Response : " + provisionFlowInNiFiPostResponseString);
			logger.debug("Flow: " + userFlowName + " for user: " + acumosLoginId + " provisioned in NiFi");
			
			Object provisionFlowInNiFiResponse = null;
			try {
				provisionFlowInNiFiResponse = new JSONParser().parse(provisionFlowInNiFiPostResponseString);
			} catch (ParseException e) {
				logger.error("Exception occured while Parsing " + provisionFlowInNiFiPostResponseString);
			}
			// Type casting Java Object to JSSONArray because allFlowsInBucketObject is
			// essentially an array of flows 
			JSONArray provisionFlowResponse = new JSONArray();
			provisionFlowResponse.add(provisionFlowInNiFiResponse);

			Iterator<?> flowIterator = provisionFlowResponse.iterator();
			JSONObject flow;
			String URI;
			String id;
			while (flowIterator.hasNext()) {
				flow = (JSONObject) flowIterator.next();
				URI = (String) flow.get("uri"); //https://localhost:8443/nifi-api/process-groups/d92aede0-016a-1000-e0f4-4aceff33bcb8
				String[] stringArray = URI.split("-api");
				String urlString = stringArray[0];
				id = (String) flow.get("id");
				flowURL = urlString + PipelineServiceConstants.PROCESS_ROOT_COMPONENTS + id; //https://localhost:8443/nifi/?processGroupId=root&componentIds=d92aede0-016a-1000-e0f4-4aceff33bcb8
				break;
			} 
			
		} catch(Exception e) { 
			logger.error("Provision Flow in NIFI Exception",e);
			throw new TargetServiceInvocationException("Provision Flow in NIFI Exception",e);
		}
		return flowURL;
	}
	
	@SuppressWarnings("unchecked")
	private String createUserInNiFiRegistry(RestTemplate client, String nifiRegistryBaseURL, String acumosLoginId) {
		String nifiRegistryUserId = null;
		
		try {
			// Create User POST request JSON body
			// creating JSONObject
			logger.debug("Creating user in NiFi Registry: " + acumosLoginId);
			JSONObject createUserPostRequestBody = new JSONObject();
			// Put data inside JSON object
			createUserPostRequestBody.put("identity", acumosLoginId);
			String createUserPostRequestObject = new String(createUserPostRequestBody.toString());
			logger.debug(createUserPostRequestObject);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json"));
			headers.set("authuser", configProps.getNifiAdminUser());
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(createUserPostRequestBody, headers);
			String url = nifiRegistryBaseURL +  PipelineServiceConstants.TENANTS_USERS; 
			URI uri = buildURI(url, null);
			logger.debug("PATH : " + uri);
			ResponseEntity<String> createUserPostResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			
			String newUserString = createUserPostResponse.getBody();// READ-ONCE only
			logger.debug(newUserString);
			// Parse the newly created user as JSON Object
			Object newlyCreatedUserObject = null;
			try {
				newlyCreatedUserObject = new JSONParser().parse(newUserString);
			} catch (ParseException e) {
				logger.error("Exception occured while Parsing " + newUserString);
			}
			// Parse the Java Object to JSONObject
			JSONObject newlyCreatedUserJsonObject = (JSONObject) newlyCreatedUserObject;
			nifiRegistryUserId = (String) newlyCreatedUserJsonObject.get("identifier");
		} catch(Exception e) { 
			logger.error("NIFIRegistry User creation Exception",e);
			throw new TargetServiceInvocationException("NIFIRegistry User creation Exception",e);
		}

		return nifiRegistryUserId;
	}// end - createUserInNiFiRegistry
	
	@SuppressWarnings("unchecked")
	private void updateUserPolicyInNiFiRegistry(RestTemplate client, String nifiRegistryBaseURL, String acumosLoginId, String nifiRegistryBucketId, 
			String nifiRegistryUserId, String action) {
		try {
			// Create Policy POST request JSON body
			// creating JSONObject
			logger.debug("Updating user priveleges in NiFi Registry: " + acumosLoginId);
			JSONObject createUserPrivelegesPostRequestBody = new JSONObject();
			String resource = PipelineServiceConstants.BUCKETS_SLASH + nifiRegistryBucketId;
			// Put data inside JSON object
			createUserPrivelegesPostRequestBody.put("resource", resource);//bucket name is same as login id
			createUserPrivelegesPostRequestBody.put("action", action);
			JSONObject user = new JSONObject();
			user.put("identifier", nifiRegistryUserId);//user Id in Registry
			user.put("identity", acumosLoginId);//user name in Registry
			user.put("type","user");
			JSONArray users = new JSONArray();
			users.add(user);
			createUserPrivelegesPostRequestBody.put("users", users);
			String createUserPrivelegesPostRequestObject = new String(createUserPrivelegesPostRequestBody.toString());
			logger.debug(createUserPrivelegesPostRequestObject);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json"));
			headers.set("authuser", configProps.getNifiAdminUser());
			
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(createUserPrivelegesPostRequestBody, headers);
			String url = nifiRegistryBaseURL + PipelineServiceConstants.POLICIES; 
			URI uri = buildURI(url, null);
			logger.debug("PATH : " + uri);
			ResponseEntity<String> createUserPrivelegePostResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			logger.debug(createUserPrivelegePostResponse.toString());
			String newUserPrivelegeString = createUserPrivelegePostResponse.getBody();// READ-ONCE only
			logger.debug(newUserPrivelegeString);
		} catch(Exception e) {
			logger.error("Exception in Update UserPolicy in NIFIRegistry ",e);
			throw new TargetServiceInvocationException("Exception in Update UserPolicy in NIFIRegistry ",e);
		}
	}
	
	
	public void deletePipeline(String acumosLoginId, String pipelineName) {
		if(null == restTemplate){
			restTemplate =  initRestTemplate();
		}
		// STEP - 0: SET UP REST Template
		String nifiRegistryBaseURL = configProps.getRegistryBaseUrl(); // Retreive NiFI registry url from properties file
		// STEP- 1: Get the bucket Id of the user (acumosLoginId) from NiFi-Registry
		BucketData bucketData = checkIfBucketExistsForUserInNiFiRegistry(restTemplate, nifiRegistryBaseURL,
				acumosLoginId);
		if (bucketData.isBucketExists()) {
			String userBucketId = bucketData.getBucketId();
			// STEP-2: Get flow Id for the given pipeline name from NiFi Registry
			PipelineData pipelineData = checkIfPipelineExistsInUserBucket(restTemplate, nifiRegistryBaseURL,
					acumosLoginId, userBucketId, pipelineName);
			if (pipelineData.isPipelineExists()) {
				String pipelineId = pipelineData.getPipelineId();// flow Id
				// STEP-3: Delete the pipeline (aka flow) from bucket
				String url = nifiRegistryBaseURL + PipelineServiceConstants.BUCKET_FLOWS_PIPELINE; 
				
				Map<String, String> uriParams = new HashMap<String, String>();
				uriParams.put("bucketId", userBucketId);
				uriParams.put("pipelineId", pipelineId);
				URI uri = buildURI(url, uriParams);
				try {
					logger.debug("PATH : " + uri); 
					
					//create headers you need to send
					HttpHeaders headers = new HttpHeaders();
					headers.set("authuser", configProps.getNifiAdminUser());

					HttpEntity<String> entity = new HttpEntity<String>(headers);
					
					ResponseEntity<String> deleteFlowResponse = restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
					logger.debug(deleteFlowResponse.toString());
					String deleteFlowString = deleteFlowResponse.getBody();
					logger.debug("Flow Details :" + deleteFlowString);
				} catch (Exception e) {
					logger.error("Exception while deleting Pipeline from NIFI ", e);
					throw new TargetServiceInvocationException("Exception while deleting Pipeline from NIFI");
				}
				logger.debug("Pipeline: " + pipelineName + " deleted" + " for user " + acumosLoginId);
			} else {
				logger.error("Pipeline does not exist for user: " + acumosLoginId);
				throw new TargetServiceInvocationException("Pipeline does not exist for user: " + acumosLoginId);
			} 
		} else {
			logger.error("Bucket does not exist for user: " + acumosLoginId);
			throw new TargetServiceInvocationException("Bucket does not exist for user: " + acumosLoginId);
		} 
	}

	@SuppressWarnings("unchecked")
	public void updatePipeline(String acumosLoginId, String currentPipelineName, String newPipelineName) {
		// STEP - 0: SET UP REST Template
		if(null == restTemplate){
			restTemplate =  initRestTemplate();
		}
		String nifiRegistryBaseURL = configProps.getRegistryBaseUrl(); 
		// STEP- 1: Get the bucket Id of the user (acumosLoginId) from NiFi-Registry
		BucketData bucketData = checkIfBucketExistsForUserInNiFiRegistry(restTemplate, nifiRegistryBaseURL,
				acumosLoginId);
		if (bucketData.isBucketExists()) {
			String userBucketId = bucketData.getBucketId();
			// STEP-2: Get flow Id of the current pipeline name from NiFi Registry
			PipelineData pipelineData = checkIfPipelineExistsInUserBucket(restTemplate, nifiRegistryBaseURL,
					acumosLoginId, userBucketId, currentPipelineName);
			if (pipelineData.isPipelineExists()) {
				String pipelineId = pipelineData.getPipelineId();// flow Id
				// STEP-3: Update the pipeline (aka flow) in bucket
				JSONObject updatePipleineNameRequestBody = new JSONObject();
				// Put data inside JSON object
				updatePipleineNameRequestBody.put("name", newPipelineName);
				updatePipleineNameRequestBody.put("type", "Flow");

				String updateFlowPutRequestObject = new String(updatePipleineNameRequestBody.toString());
				logger.debug("Updated Flow Request Object : " + updateFlowPutRequestObject);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(new MediaType("application", "json"));
				headers.set("authuser", configProps.getNifiAdminUser());
				HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(updatePipleineNameRequestBody, headers);
				String url = nifiRegistryBaseURL + PipelineServiceConstants.BUCKET_FLOWS_PIPELINE; 
				Map<String, String> uriParams = new HashMap<String, String>();
				uriParams.put("bucketId", userBucketId);
				uriParams.put("pipelineId", pipelineId);
				URI uri = buildURI(url, uriParams);
				logger.debug("PATH : " + uri); 
				
				try {
					ResponseEntity<String> updateFlowResponse = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
					String updateFlowString = updateFlowResponse.getBody();
					logger.debug("Update Flow : " + updateFlowString);
				} catch (Exception e) {
					logger.error("Exception while updating Pipeline from NIFI ", e);
					throw new TargetServiceInvocationException("Exception while updating Pipeline from NIFI");
				}
				logger.debug("Pipeline: " + currentPipelineName + " updated" + " for user " + acumosLoginId
						+ " to " + newPipelineName);
			} else {
				logger.error("Pipeline does not exist for user: " + acumosLoginId);
				throw new TargetServiceInvocationException("Pipeline does not exist for user: " + acumosLoginId);
			}
		} else {
			logger.error("Bucket does not exist for user: " + acumosLoginId);
			throw new TargetServiceInvocationException("Bucket does not exist for user: " + acumosLoginId);
		}
	}
	
	private RestTemplate initRestTemplate() {
		RestTemplate template = null;
		if(!configProps.getCreatePod()){
			try {
				KeyStore clientStore = KeyStore.getInstance("PKCS12");
				clientStore.load(new FileInputStream(configProps.getClientCertificatePath()),
						configProps.getClientPassword().toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(clientStore, configProps.getClientPassword().toCharArray());
				KeyManager[] kms = kmf.getKeyManagers();

				KeyStore trustStore = KeyStore.getInstance("JKS");
				trustStore.load(new FileInputStream(configProps.getTrustStorePath()),
						configProps.getTrustStorePassword().toCharArray());

				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(trustStore);
				TrustManager[] tms = tmf.getTrustManagers();

				SSLContext sslContext = null;
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(kms, tms, new SecureRandom());

				// String requestRestApiUri = "http://localhost:18080/nifi/";
				String requestRestApiUri = configProps.getServiceBaseUrl() + PipelineServiceConstants.FLOW_REGISTRIES;
				CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				requestFactory.setHttpClient(httpClient);
				template = new RestTemplate(requestFactory);
				template.exchange(requestRestApiUri, HttpMethod.GET, null, String.class);
				
			} catch (NoSuchAlgorithmException e) {
				logger.error("NIFI Security : NoSuch Algorithm Exception Occured",e);
				throw new TargetServiceInvocationException("NIFI Security : NoSuch Algorithm Exception Occured");
			} catch (KeyStoreException e) {
				logger.error("NIFI Security : Key Store Exception Occured",e);
				throw new SecurityValidationException("NIFI Security : Key Store Exception Occured");
			} catch (CertificateException e) {
				logger.error("NIFI Security : CertificateException Occured",e);
				throw new SecurityValidationException("NIFI Security : CertificateException Occured");
			} catch (FileNotFoundException e) {
				logger.error("NIFI Security : FileNotFoundException Occured",e);
				throw new SecurityValidationException("NIFI Security : FileNotFoundException Occured");
			} catch (IOException e) {
				logger.error("NIFI Security : IOException Occured",e);
				throw new SecurityValidationException("NIFI Security : IOException Occured");
			} catch (UnrecoverableKeyException e) {
				logger.error("NIFI Security : UnrecoverableKeyException Occured",e);
				throw new SecurityValidationException("NIFI Security : UnrecoverableKeyException Occured");
			} catch (KeyManagementException e) {
				logger.error("NIFI Security : KeyManagementException Occured",e);
				throw new SecurityValidationException("NIFI Security : KeyManagementException Occured");
			}
		} else {
			template = new RestTemplate();
		}
			
		
		return template;
	}

	/**
	 * Builds the rest API URI
	 * 
	 * @param url
	 *            URL of the rest API to be invoked.
	 * @param uriParams
	 *            URI Path variable key-value map. Map<String, String>
	 * @return URI Return UIR constructed based on the input parameters.
	 */
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
