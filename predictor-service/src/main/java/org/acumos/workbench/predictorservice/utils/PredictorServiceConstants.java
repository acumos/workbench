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

package org.acumos.workbench.predictorservice.utils;

public class PredictorServiceConstants {
	

	public static final String AUTHENTICATEDUSERID = "AuthenticatedUserId";
	public static final String PREDICTORID = "predictorId";
	public static final String PROJECTID = "projectId";
	public static final String ASSOCIATIONID = "ASSOCIATION_ID";
	public static final String REVISIONID = "REVISION_ID";
	public static final String SOLUTIONID = "solutionId";
	public static final String K8S_ID = "k8s_id";
	public static final String PREDICTORNAME = "predictorName";
	public static final String MODELID = "modelId";
	public static final String VERSION = "version";
	public static final String PREDICTORKEY = "predictorkey";
	public static final String DEPLOYMENTSTATUS = "DEPLOYMENT_STATUS";
	public static final String DEPLOYMENTID = "DEPLOYMENT_ID";
	public static final String JENKINSURL = "JENKINS_URL";
	public static final String DEPLOYMENTURL = "DEPLOYMENT_URL";
	
	public static final String K8CLUSTER_CONFIG_KEY = "k8sCluster";
	public static final String DEPLOY_API="/deploy";
	public static final String GET_PROTBUF_API="/dsce/artifact/fetchProtoBufJSON";
	
	public static final String GETPREDICTORPROJECTASSOCIATION = "{\"selector\":{\"associationId\":{\"$eq\":\"%s\"}}}";
	public static final String GETPREDICTORBYNAMEQUERY = "{\"selector\":{\"$and\":[{\"userId\":{\"$eq\":\"%s\"}},{\"predictorName\":{\"$eq\":\"%s\"}}]}}";
	public static final String GETPREDICTORBYIDQUERY = "{\"selector\":{\"$and\":[{\"userId\":{\"$eq\":\"%s\"}},{\"predictorId\":{\"$eq\":\"%s\"}}]}}";
	public static final String GETPREDICTOR_FOR_USER = "{\"selector\":{\"$and\":[{\"userId\":{\"$eq\":\"%s\"}},{\"predictorId\":{\"$gte\":null}}]},\"limit\":10000}"; // NEED TO CHECK
	public static final String GETPREDICTOR_FOR_USER_PROJECT = "{\"selector\":{\"$and\":[{\"userId\":{\"$eq\":\"%s\"}},{\"predictorId\":{\"$gte\":null}},{\"projectId\":{\"$eq\":\"%s\"}}]},\"limit\":10000}"; // NEED TO CHECK
	
	public static final String GETPREDICTORPROJECT = "{\"selector\":{\"$and\":[{\"userId\":{\"$eq\":\"%s\"}},{\"projectId\":{\"$eq\":\"%s\"}}]}}"; // NEED TO CHECK
	
	//public static final String ASSOCIATIONEXISTSINCOUCHQUERY = "{\"selector\":{\"$and\":[{\"projectId\":{\"$eq\":\"%s\"}},{\"solutionId\":{\"$eq\":\"%s\"}},{\"revisionId\":{\"$eq\":\"%s\"}}\",{\"associationStatus\":{\"$gte\": null}}]}}";
	public static final String ASSOCIATIONEXISTSINCOUCHQUERY = "{\"selector\":{\"$and\":[{\"predictorId\":{\"$eq\":\"%s\"}},{\"projectId\":{\"$eq\":\"%s\"}},{\"solutionId\":{\"$eq\":\"%s\"}},{\"revisionId\":{\"$eq\":\"%s\"}}]}}";
	
	public static final String GETPREDICTORSQUERY = "{\"selector\":{\"$and\":[{\"projectId\":{\"$eq\":\"%s\"}},{\"associationStatus\":{\"$eq\":\"%s\"}}]}}";
	
	//TODO: To be removed once confirmed
	//public static final String EDITPREDICTORASSOCIATIONTOPROJECTQUERY = "{\"selector\":{\"$and\":[{\"predictorName\":{\"$eq\":\"%s\"}},{\"predcitorId\":{\"$eq\":\"%s\"}},{\"environmentPath\":{\"$eq\":\"%s\"}},{\"predictorVersion\":{\"$eq\":\"%s\"}}]}}";
	
	public static final String PREDICTOREXISTSINCOUCHQUERY = "{\"selector\":{\"$and\":[{\"predictorId\":{\"$eq\":\"%s\"}},{\"predictorDeploymentStatus\":{\"$eq\":\"%s\"}}]}}";
	
	public static final String MODELPROJECTASSOCIATIONEXISTSINCOUCHQUERY = "{\"selector\":{\"$and\":[{\"projectId\":{\"$eq\":\"%s\"}},{\"solutionId\":{\"$eq\":\"%s\"}},{\"revisionId\":{\"$eq\":\"%s\"}},{\"predictorDeploymentStatus\":{\"$eq\":\"%s\"}}]}}";
	
	public static final String GETDATASETPREDICTORQUERY ="{\"selector\":{\"$and\":[{\"userId\":{\"$eq\":\"%s\"}},{\"solutionId\":{\"$eq\":\"%s\"}},{\"revisionId\":{\"$eq\":\"%s\"}},"+"{\"predictorId\":{\"$gte\": null}}]}}";
	
	public static enum deplyomentStatus {
		ACTIVE, INPROGRESS, FAILED
	}
	

}
