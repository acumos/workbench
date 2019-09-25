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
	
	
	
	

}
