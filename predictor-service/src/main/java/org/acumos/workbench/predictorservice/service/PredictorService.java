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

package org.acumos.workbench.predictorservice.service;

import java.util.List;

import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.vo.Predictor;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.predictorservice.deployClient.K8sConfig;

public interface PredictorService {
	
	
	/**
	 * To insert the Predictor entry in couchdb
	 * 
	 * @param authenticatedUserId 
	 * 				the authenticatedUserId
	 * @param predictor
	 * 		        the predictor
	 * @param revisionId
	 *              the revisionId
	 * @param authToken
	 * 	            the authToken
	 * @param K8S_ID
	 * 	            the K8S_ID
	 * * @param PredictorKey
	 * 	            the PredictorKey
	 *  * @param projectId
	 * 		        the projectId
	 * @return returns the Predictor Object
	 */
	public Predictor createPredictor(String authenticatedUserId,Predictor predictor,String revisionId, String authToken,String K8S_ID,String predictorKey,String projectId);
	/**
	 * Gets the user details based on authenticationUserId which is the Acumos User LoginId. 
	 * @param authenticatedUserId
	 * 		the user login id.
	 * @return MLPUser
	 * 		returns the MLPUser Instance. 
	 * 
	 * @throws UserNotFoundException
	 * 		in case if user is not found throws UserNotFoundException.
	 * @throws TargetServiceInvocationException
	 * 		in case of any error while accessing CDS service.
	 */
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException, TargetServiceInvocationException;

	/**
	 * Get the predictor for the user
	 * 
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 *            
	 * @param predictorName
	 *            the predictor name
	 * @return the predictor object
	 * 
	 * 	@throws
	 * 			PredictorNotFoundException
	 */
	public Predictor getPredictor(String authenticatedUserId,String predictorName);

	/**
	 *  To get the deployment Status for the Predictor
	 *  
	 * @param authenticatedUserId
	 *            the authenticatedUserId
	 * @param predictorId
	 *            the predictorId         
	 * @return the predictor object
	 * @throws PredictorNotFoundException
	 */
	public Predictor getDeploymentStatus(String authenticatedUserId,String predictorId);
	/**
	 * To get the Predictor for user or/and Project
	 * @param authenticatedUserId
	 * 			the authenticatedUserId
	 * @param projectId
	 * 			the projectId
	 * @return
	 * 			the predictor object
	 */
	public List<Predictor> getPredictorByUser(String authenticatedUserId,String projectId);
	
	/**
	 * This method returns the protobuf signature for the model
	 * @param authenticatedUserId
	 * 			the authenicatedUserId
	 * @param solutionId
	 * 			the solutionID
	 * @param revisionId
	 * 			the revisionId
	 * @param authToken
	 * 			the authToken
	 * @return
	 * 			the protobuf Signature String
	 */
	public String  getProtobufSignature(String authenticatedUserId, String solutionId,String revisionId,String authToken);
	/**
	 * 
	 * @param authenticatedUserId
	 * 			the authenticatedUserId
	 * @param predictorId
	 * 			the predictorId
	 * @param projectId
	 * 			the projectId
	 * @return
	 * 			the ServiceState status
	 */
	public ServiceState deletePredictor(String authenticatedUserId, String predictorId, String projectId);
	
	/**
	 * 
	 * @param authenticatedUserId
	 * @param predictorId
	 * @param projectId
	 * @return
	 */
	public ServiceState checkDeploymentStatusonK8S(String deploymentId , String predictorId, String projectId);

	/**
	 * 
	 * @return
	 * 		the K8S cluster List
	 */
	public List<K8sConfig> getK8sSiteConfig();
}
