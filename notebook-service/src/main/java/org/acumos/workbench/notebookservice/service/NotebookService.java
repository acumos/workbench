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

package org.acumos.workbench.notebookservice.service;

import java.util.List;

import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.notebookservice.exception.ArchivedException;
import org.acumos.workbench.notebookservice.exception.AssociationNotFoundException;
import org.acumos.workbench.notebookservice.exception.DuplicateNotebookException;
import org.acumos.workbench.notebookservice.exception.NotOwnerException;
import org.acumos.workbench.notebookservice.exception.NotebookNotFoundException;
import org.acumos.workbench.notebookservice.exception.TargetServiceInvocationException;
import org.acumos.workbench.notebookservice.exception.UserNotFoundException;

public interface NotebookService {

	
	/**
	 * Checks if Notebook for given project ID exists in DB. 
	 * 
	 * @param notebookId
	 * 		the notebookId
	 * @throws NotebookNotFoundException
	 * 		throws NotebookNotFoundException if Notebook does not exists.
	 * @throws TargetServiceInvocationException
	 * 		Throws TargetServiceInvocationException in case of any error while accessing CDS
	 */
	void notebookExists(String notebookId) throws NotebookNotFoundException, TargetServiceInvocationException;
	
	
	/**
	 * Checks if Notebook with same name and type already exists in DB. 
	 * If projectId is null then for a user and if projectId is not null then for the project.
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the Project Id
	 * @param notebook
	 * 		the notebook Object
	 * @throws DuplicateProjectException
	 * 		throws DuplicateNotebookException if Notebook already exists.
	 */
	void notebookExists(String authenticatedUserId,String projectId, Notebook notebook) throws DuplicateNotebookException;
	
	/**
	 * Create Notebook. 
	 * @param authenticatedUserId
	 * 		the authenticated user Id. 
	 * @param projectId
	 * 		the projectId to which notebook is associated to, if not then null.
	 * @param notebook
	 * 		the notebook object instance.
	 * @return Notebook
	 * 		returns Notebook object with additional details.
	 * @throws TargetServiceInvocationException
	 * 		Throws TargetServiceInvocationException in case of any error while accessing CDS
	 */
	Notebook createNotebook(String authenticatedUserId, String projectId, Notebook notebook) throws TargetServiceInvocationException;
	
	
	/**
	 * Checks whether user is owner of the Notebook.
	 * 
	 * @param userId
	 * 		the user Id
	 * @param notebookId
	 * 		the Notebook Id
	 * @return boolean 
	 * 		returns true or false, indicating whether user with input userId is owner of the Notebook.
	 * @throws NotOwnerException
	 * 		throws NotOwnerException if user is not owner of the Notebook
	 */
	boolean isOwnerOfNotebook(String authenticatedUserId, String notebookId) throws NotOwnerException;
	
	
	/**
	 * Checks whether Notebook is archived.
	 * @param notebookId
	 * 		the notebook Id to be verified.
	 * @return boolean
	 * 		return true or false, indicating whether Notebook is archived or not. 
	 * @throws ArchivedException
	 * 		throws ArchivedException if the Notebook is archived.
	 */
	boolean isNotebookArchived(String notebookId) throws ArchivedException;
	
	/**
	 * Updates existing Notebook. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the project Id to which Notebook is associated with, if not then null.
	 * @param notebookId
	 * 		the notebook Id to be updated. 
	 * @return Notebook
	 * 		returns Notebook object with additional details. 
	 * @throws DuplicateNotebookException
	 * 		throws DuplicateNotebookException in the Notebook already exists with same name, type and version.
	 */
	Notebook updateNotebook(String authenticatedUserId, String projectId, String notebookId, Notebook notebook) throws DuplicateNotebookException;
	
	/**
	 * Gets Notebook details. 
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param notebookId
	 * 		the Notebook Id.
	 * @return Notebook 
	 * 		returns Notebook object with all required details. 
	 * @throws NotebookNotFoundException
	 * 		throws NotebookNotFoundException in case if Notebook not found.
	 */
	Notebook getNotebook(String authenticatedUserId, String notebookId) throws NotebookNotFoundException;
	
	/**
	 * Get list of Notebook for a input userId and projetId.  if projecId is null then return all the notebooks accessible to the user.  
	 * 
	 * @param authenticatedUserId
	 * 		the authenticated User Id. 
	 * @param projectId
	 * 		the project Id
	 * @return List<Notebook> 
	 * 		returns list of Notebook objects for a input userId [and projectId]
	 */
	List<Notebook> getNotebooks(String authenticatedUserId, String projectId);
	
	/**
	 * Deletes Notebook
	 * @param notebookId
	 * 		the Notebook Id. 
	 * @return ServiceState 
	 * 		returns ServiceState indicating Notebook is deleted successfully. 
	 */
	ServiceState deleteNotebook(String notebookId);
	
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
	 * Archives or un archive the Notebook 
	 * @param authenticatedUserId
	 * 		the user login id.
	 * @param projectId
	 * 		Associated ProjectId, if not associated then specify null.
	 * @param notebookId
	 * 		Notebook Id to be archived
	 * @param actionType
	 * 		To archive :  actionType = A (default)
	 * 		To un archive :  actionType = UA
	 * @return
	 * 		returns Notebook object indication that Notebook is archived successfully.
	 */
	Notebook archiveNotebook(String authenticatedUserId, String projectId, String notebookId, String actionType);
	
	/**
	 * Launch the Notebook based on the type of the Notebook
	 * @param authenticatedUserId
	 * 		the user login id.
	 * @param projectId
	 * 		Associated ProjectId, if not associated then specify null.
	 * @param notebookId
	 * 		Notebook Id to be archived
	 * @return Notebook
	 * 		Notebook with URL
	 */
	Notebook launchNotebook(String authenticatedUserId, String projectId, String notebookId);
	
	/**
	 * Checks if Notebook (for input notbookId) is associated with Project (for input projectId)
	 * @param projectId
	 * 		Project Id
	 * @param notebookId
	 * 		Notebook Id
	 @throws AssociationNotFoundException
	 * 		Throws AssociationNotFoundException if Notebook is not associated to specified Project.
	 */
	void isNotebookProjectAssociated(String projectId, String notebookId) throws AssociationNotFoundException;
	
}
