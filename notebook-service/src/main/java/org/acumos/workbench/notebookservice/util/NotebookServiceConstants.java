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

package org.acumos.workbench.notebookservice.util;

public class NotebookServiceConstants {

    public static final String GET_PROJECT_PATH = "/users/{authenticatedUserId}/projects/{projectId}";
	public static final String QUESTION_MARK = "?";
	public static final String JUPYTERHUB_USER_PATH = "/hub/api/users/{name}";
	public static final String JUPYTERHUB_LAUNCH_SERVER_PATH = "/hub/api/users/{name}/server";
	public static final String JUPYTERNOTEBOOK_SERVER_PATH = "/user/{name}/tree";
	public static final String PATH_VAR_USERNAME_KEY = "name";
	public static final String PATH_VAR_PROJECT_ID_KEY = "projectId";
	public static final String PATH_VAR_AUTHENTICATED_USER_ID_KEY = "authenticatedUserId";
	public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
	public static final String NOTEBOOK_ID = "Notebook Id";
	public static final String FIELD_PROJECT_ID = "Project Id";
	public static final String FIELD_AUTHENTICATED_USER_ID = "AuthenticatedUserId";
	//Exception constants 
	public static final String MALFORMED_URL = "Invalid %s URL : %s ";
	//CDS query param key
	public static final String CDS_QUERY_PARAM_NOTEBOOKTYPECODE_KEY = "notebookTypeCode";
	public static final String CDS_QUERY_PARAM_VERSION_KEY = "version";
	public static final String CDS_QUERY_PARAM_NAME_KEY = "name";
	
	public static final String PATH_VAR_NOTEBOOKNAME_KEY = "notebookname";
	public static final String JUPYTERNOTEBOOK_CREATE_NOTEBOOK_EXCEPTION_MSG = "JupyterNotebook - Create notebook in Jupyter Notebook server";
	public static final String JUPYTERNOTEBOOK_SERVER_CONTENT_EXCEPTION_MSG = "JupyterNotebook - Jupyter Notebook server contents";
	public static final String JUPYTERNOTEBOOK_DELETE_NOTEBOOK_EXCEPTION_MSG = "JupyterNotebook - Delete notebook in Jupyter Notebook server";
	public static final String JUPYTERNOTEBOOK_PATH = "/user/{name}/notebooks/{notebookname}";
	public static final String JUPYTERNOTEBOOK_API_PATH_BASE = "/user/{name}/api/contents";
	public static final String JUPYTERNOTEBOOK_API_PATH_NOTEBOOKNAME = JUPYTERNOTEBOOK_API_PATH_BASE+ "/{notebookname}";


	
}
