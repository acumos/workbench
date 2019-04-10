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

public interface JupyterhubRestClient {

	/**
	 * Launch the Jupyter Notebook by invoking the JupyterHub Rest API.
	 * @param authenticatedUserId
	 * 		Acumos User Login Id
	 * @param projectId
	 * 		project Id to which Notebook is associated to
	 * @param notebookId
	 * 		Notebook Id tobe launched.
	 * @return
	 * 		Launched Notebook URL
	 */
	String launchJupyterNotebook(String authenticatedUserId, String projectId, String notebookId);
}
