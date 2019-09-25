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
import org.acumos.workbench.predictorservice.lightcouch.PredictorProjectAssociation;

public interface PredictorProjectAssociationService {
	
	/**
	 * To Get the user details based on authenticationUserId which is the Acumos User LoginId. 
	 * @param authenticatedUserId
	 * 		the user login id.
	 * @return MLPUser
	 * 		returns the MLPUser Instance. 
	 * @throws UserNotFoundException
	 * 		in case if user is not found throws UserNotFoundException.
	 * @throws TargetServiceInvocationException
	 * 		in case of any error while accessing CDS service.
	 */
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException, TargetServiceInvocationException;

	/**
	 * To get the List of Predictors which are Associated to Project
	 * @param authenticatedUserId
	 * 			the acumos user login id.
	 * @param projectId
	 * 			the project Id
	 * @return
	 * 			returns List<Predictor>
	 */
	public List<Predictor> getPredictors(String authenticatedUserId, String projectId);
	
	/**
	 * To Associate the Predictor to a Project
	 * @param authenticatedUserId
	 * 				The Acumos User Login Id
	 * @param predictorId
	 * 				The Predictor Id 
	 * @param projectId
	 * 				The Project Id
	 * @param predProjAssociation
	 * 				accepts PredictorProjectAssociation VO
	 * @return
	 * 				accepts Predictor Details
	 */
	public Predictor associatePredictorToProject(String authenticatedUserId, String predictorId, String projectId, PredictorProjectAssociation predProjAssociation);
	
	/**
	 * To Modify the Predictor Association To Project 
	 * @param authenticatedUserId
	 * 			the acumos user login id.
	 * @param predictorProjAssociation
	 * 			the PredictorProjectAssociation
	 * @return
	 * 			returns Predictor
	 */
	public Predictor editPredictorAssociationToProject(String authenticatedUserId,String associationId,PredictorProjectAssociation predictorProjAssociation);

	/**
	 * To Delete the Predictor Association
	 * @param authenticatedUserId
	 * 			the acumos user login id.
	 * @param associationId
	 * 			the Association ID
	 * @return
	 * 			the ServiceState
	 */
	public ServiceState deleteAssociation(String authenticatedUserId, String associationId);
	
	
}
