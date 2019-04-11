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

package org.acumos.workbench.pipelineservice.util;

public class PipelineServiceConstants {
	
	public static final String GET_PROJECT_PATH = "/users/{authenticatedUserId}/projects/{projectId}";
	public static final String PATH_VAR_PROJECT_ID_KEY = "projectId";
	public static final String PATH_VAR_AUTHENTICATED_USER_ID_KEY = "authenticatedUserId";
	
	public static final String CDS_GET_PIPELINE = "CDS - Get Pipeline";
	public static final String CDS_SEARCH_PIPELINES = "CDS - Search Pipelines";
	public static final String CDS_CREATE_PIPELINE = "CDS - Create Pipeline";
	public static final String CDS_ADD_PROJECT_PIPELINE = "CDS - Add Project Pipeline";
	public static final String CDS_UPDATE_PIPELINE = "CDS - Update Pipeline";
	public static final String CDS_GET_PROJECT_PIPELINES = "CDS - Get Project Pipelines";
	public static final String CDS_GET_PIPELINE_PROJECTS = "CDS - Get Pipeline Projects";
	public static final String CDS_GET_USER_DETAILS = "CDS - Get User Details";
	public static final String CDS_DROP_PROJECT_PIPELINE = "CDS - Drop Project Pipeline";
	public static final String CDS_DELETE_PIPELINE = "CDS - Delete Pipeline";
	public static final String PROJECT_SERVICE_GET_PROJECT = "Project Service - Get Project";
	
	
	

}
