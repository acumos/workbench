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
import org.acumos.workbench.predictorservice.lightcouch.DataSetPredictor;

public interface PredictorProjectAssociationService {
	
	/**
	 * Returns the User Details for the given Acumos User LoginId
	 * @param authenticatedUserId
	 * 				The Acumos User Login Id
	 * @return
	 * 				returns the MLPUser from CDS
	 * @throws UserNotFoundException
	 * 				In case if user is not found throws UserNotFoundException.
	 * @throws TargetServiceInvocationException
	 * 				In case of any error occurs while accessing CDS service throws TargetServiceInvocationException
	 */
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException, TargetServiceInvocationException;

	/**
	 * Returns the List of Predictors which are Associated to Project for the given Acumos User LoginId
	 * @param authenticatedUserId
	 * 			the Acumos user login id.
	 * @param projectId
	 * 			the project Id
	 * @return
	 * 			returns List of Predictor
	 */
	public List<Predictor> getPredictors(String authenticatedUserId, String projectId);
	
	/**
	 * Associate the Predictor to a Project
	 * @param authenticatedUserId
	 * 				The Acumos User Login Id
	 * @param predProjAssociation
	 * 				accepts PredictorProjectAssociation VO
	 * @return
	 * 				accepts Predictor Details
	 */
	public Predictor associatePredictorToProject(String authenticatedUserId, String predictorId, DataSetPredictor predProjAssociation);
	
	/**
	 * Modify the Predictor Project Association
	 * @param authenticatedUserId
	 * 			the acumos user login id.
	 * @param predictorProjAssociation
	 * 			the PredictorProjectAssociation
	 * @param predictorId
	 * 			the predictorId
	 * @return
	 * 			returns Predictor
	 */
	public Predictor editPredictorProjectAssociation(String authenticatedUserId,String associationId,DataSetPredictor predictorProjAssociation,String predictorId);

	/**
	 * Delete the Predictor Association
	 * @param authenticatedUserId
	 * 			the acumos user login id.
	 * @param associationId
	 * 			the Association ID
	 * @return
	 * 			the ServiceState
	 */
	public ServiceState deleteAssociation(String authenticatedUserId, String associationId);

	/**
	 * Get the Predictor Details for input modelId and version from Coch DB
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param modelId
	 * 			The input ModelId
	 * @param version
	 * 			The input Model Version
	 * @param modelRevisionId 
	 * @return 
	 * 			The Predictor Details
	 */
	public Predictor getPredictor(String authenticatedUserId, String modelId, String version, String modelRevisionId);

	/**
	 * Create new DataSetPredictor in Couch DB using input data
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param projectId
	 * 			The ProjectId
	 * @param predictorProjAssociation
	 * 			The PredictorProjectAssociation Details
	 * @return 	
	 * 			The DataSetPredictor Details
	 */
	public DataSetPredictor createDataSetPredictor(String authenticatedUserId, String projectId,
			DataSetPredictor predictorProjAssociation);
	
	
}
