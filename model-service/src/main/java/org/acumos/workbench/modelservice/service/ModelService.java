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

package org.acumos.workbench.modelservice.service;

import java.util.List;

import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.exception.NotOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;

public interface ModelService {

	/**
	 * Get list of Models for a input userId and projectId.  if projectId is null then return all the models accessible to the user.  
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the project Id
	 * @return List<Model> 
	 * 		returns list of Model objects for a input userId [and projectId]
	 */
	List<Model> getModels(String authenticatedUserId, String ProjectId);
	
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
	MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException, TargetServiceInvocationException;
	
	/**
	 * To check whether input userId is owner of the Project.
	 * @param authenticatedUserId
	 * 			the user login id.
	 * @param projectId
	 * 			the project Id
	 * @return
	 * 			returns true or false, indicating whether user with input userId is owner of the Project.
	 * @throws NotOwnerException
	 * 			throws NotOwnerException if user is not owner of the projectId
	 */
	
	public boolean isOwnerOfProject(String authenticatedUserId, String projectId)throws NotOwnerException;
	
	/**
	 * This method check if Project exists for input projectId.
	 * @param projectId
	 * 			projectId of project
	 * @throws ProjectNotFoundException
	 * 			throws ProjectNotFoundException if project not found.
	 */
	public void projectExists(String projectId) throws ProjectNotFoundException;

	/**
	 * To check whether input userId is owner of the modelId
	 * @param authenticatedUserId
	 * 			the Acumos user login id.
	 * @param modelId
	 * 			the model Id
	 */
	public boolean isOwnerofModel(String authenticatedUserId, String modelId)throws NotOwnerException;

	/**
	 * To Check the Project Details which are exists in Project Service
	 * 
	 * @param authenticatedUserId
	 * 			the Acumos user login id.
	 * @param projectId
	 * 			the Project Id 
	 * @param authToken
	 * 			the Authentication Token
	 */
	public void checkProjectDetails(String authenticatedUserId, String projectId, String authToken);

	/**
	 * To Check Model(Soluition) exists in CDS or not
	 * @param modelId
	 * 			the model Id
	 */
	public void checkModelExistsinCDS(String modelId);

	/**
	 * To Insert the Project Model Association in CDS ProjectModelAssociation Table
	 * @param authenticatedUserId
	 * 			the Acumos user login id.
	 * @param projectId
	 * 			the Project Id
	 * @param modelId
	 * 			the Model Id
	 * @param model
	 * 			the Model object
	 * @return Model
	 * 			returns Model
	 */
	public Model insertProjectModelAssociation(String authenticatedUserId, String projectId, String modelId, Model model);

	/**
	 * To update the Project Model Association in CDS
	 * @param authenticatedUserId
	 * 			the Acumos user login id.
	 * @param projectId
	 * 			the Project Id
	 * @param modelId
	 * 			the Model Id
	 * @param model
	 * 			the Model object
	 * @return Model
	 * 			returns Model object
	 * 
	 */
	public Model updateProjectModelAssociation(String authenticatedUserId, String projectId, String modelId, Model model);

	/**
	 * TO delete the Project Model Association in CDS
	 * @param authenticatedUserId
	 * 			the Acumos user login id.
	 * @param projectId
	 * 			the Project Id
	 * @param modelId
	 * 			the Model Id
	 * @param model
	 * 			the Model object
	 * @return ServiceState
	 * 			returns ServiceState object
	 */
	public ServiceState deleteProjectModelAssociation(String authenticatedUserId, String projectId, String modelId, Model model);

}
