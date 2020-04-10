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

public interface PredictorValidation {

	/**
	 * To Validate the Project exists and is active or not
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param projectId
	 * 			The Project Id
	 * @param authToken
	 * 			The Authenticated Token
	 */
	public void validateProject(String authenticatedUserId, String projectId, String authToken);

	/**
	 *  TO Check the Predictor is Exists in Couch DataBase
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param predictorId
	 * 			The Predictor Id
	 */
	public void predictorExists(String authenticatedUserId, String predictorId);
	
	/**
	 * To Check if the Input Model and Project is Associated or Not
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param projectId
	 * 			The Project Id
	 * @param solutionId
	 * 			The Solution Id
	 * @param revisionId
	 * 			The Revision Id
	 * @param authToken
	 * 			The Auth Token
	 */
	public void isModelAssociatedToProject(String authenticatedUserId, String projectId, String solutionId, String revisionId,String authToken);

	/**
	 * Check the input model and version exists in CDS
	 * @param modelId
	 * 			the input model Id 
	 * @param version
	 * 			the input version
	 * @return 
	 * 			the revision Id if Model exists
	 */
	public String modelExists(String modelId, String version);
	/**
	 * Check if the user exists in CDS
	 * 
	 * @param authenticatedUserId
	 * 			the authenticatedUserId
	 * 	
	 */
	public void isUserExists(String authenticatedUserId);
	/**
	 * Check if the solution and revision Id is exist in CDS 
	 * 
	 * @param solutionId
	 * 			the solutionId
	 * @param revisionId
	 * 			the revisionId
	 */
	public void isSolutionRevisionExists(String solutionId, String revisionId);

	/**
	 * Check if predictor with input predictor name exists in couch DB
	 * 
	 * @param authenticatedUserId 
	 * 			the authendicatedUserId
	 * 
	 * @param predictorName     
	 * 				  the predictor name
	 */
	public void isPredictorExists(String authenticatedUserId, String predictorName);

	/**
	 * Check if SolutionId(Model) is accessible to user
	 * @param authenticatedUserId
	 * 			the authenticatedUserId
	 * @param solutionId
	 * 			the solutionId
	 */
	public void isSolutionAccessible(String authenticatedUserId, String solutionId);

	/**
	 * Check if the user can access the predictor
	 * @param authenticatedUserId
	 * 			the authenticatedUserId
	 * @param predictorId
	 * 			the predictorId
	 */
	public 	void isPredictorAccessibleToUser(String authenticatedUserId, String predictorId);

	/**
	 * Check if project is exists as active in predictor
	 * 
	 * @param authenticatedUserId 
	 * 			the authenticatedUserId
	 * @param projectId           
	 * 			the projectId
	 */
	public void isProjectExists(String authenticatedUserId, String projectId);
	
	/**
	 * This method verifies if the value is exists i.e, value is not null or empty.
	 * 
	 * @param fieldName 
	 * 				the fieldName
	 * @param value     
	 * 				the value
	 */
	public void isValueExists(String fieldName, String value);
	

	
	
	
	

}
