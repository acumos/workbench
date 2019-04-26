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

import org.acumos.workbench.common.vo.Notebook;

public interface NotebookRestClient {

	/**
	 * Launch the Notebook Server for a user by invoking Rest API.
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @return
	 * 		Launched Notebook URL
	 */
	String launchNotebookServer(String authenticatedUserId);
	
	/**
	 * Launch the Notebook by invoking Rest API.
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @param projectId
	 * 		project Id to which Notebook is associated to
	 * @param Notebook
	 * 		Notebook to be launched.
	 * @return
	 * 		returns URL of Notebook. If in case notebook is not found then returns URL of Notebook Server.
	 */
	String launchNotebook(String authenticatedUserId, String projectId, Notebook notebook);
	
	
	/**
	 * Stops the Notebook Server for a user by invoking Rest API.
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @return
	 * 		true if Notebook server is stopped successfully 
	 */
	 boolean stopNotebookServer(String authenticatedUserId);
	
	/**
	 * Creates the notebook in Notebook Server
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @param notebookName
	 * 		name of the notebook to be created
	 * @return
	 * 		String path to access
	 */
	String createNotebookInNotebookServer(String authenticatedUserId, String notebookName);
	
	/**
	 * Updates the notebook in Notebook Server
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @param newNotebookName
	 * 		new name of the notebook 
	 * @param oldNotebookName
	 * 		old name of the notebook to be updated
	 * @return
	 * 		String path to access
	 */
	String updateNotebookInNotebookServer(String authenticatedUserId, String newNotebookName, String oldNotebookName);
	
	/**
	 * Deletss the notebook in Notebook Server
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @param notebookName
	 * 		name of the notebook to be created
	 * @return
	 * 		true after successfully deleting notebook from Notebook server
	 */
	boolean deleteNotebookFromNotebookServer(String authenticatedUserId, String notebookName);
}
