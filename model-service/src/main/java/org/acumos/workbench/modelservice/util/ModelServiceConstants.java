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

package org.acumos.workbench.modelservice.util;

public class ModelServiceConstants {
	
	public static final String MODELAUTHENTICATEDUSERID = "AuthenticatedUserId";
	public static final String DELETED = "DELETED";
	public static final String MODELISACTIVE = "Model is Active";
	public static final String CATALOGNAMES = "CATALOG_NAMES";
	public static final String UNARCHIVE = "UA";
	public static final String ARCHIVE = "A";
	public static final String PROJECTID = "projectId";
	public static final String ASSOCIATIONID = "ASSOCIATION_ID";
	public static final String MODELTYPECODE = "MODEL_TYPE_CODE";
	public static final String MODELPUBLISHSTATUS = "MODEL_PUBLISH_STATUS";
	public static final String REVISIONID = "REV_ID";
	public static final String ASSOCIATIONEXISTSINCOUCHQUERY = "{\"selector\":{\"$and\":[{\"ProjectId\":{\"$eq\":\"%s\"}},{\"SolutionId\":{\"$eq\":\"%s\"}},{\"RevisionId\":{\"$eq\":\"%s\"}},{\"status\":{\"$eq\":\"%s\"}}]}}";
	public static final String GETMODELSASSOCIATEDTOPROJECTQUERY = "{\"selector\":{\"$and\":[{\"ProjectId\":{\"$eq\":\"%s\"}},{\"status\":{\"$ne\":\"%s\"}}]}}";
	public static final String GETDOCUMENTSQUERY = "{\"selector\":{\"$and\":[{\"_id\":{\"$eq\":\"%s\"}},{\"_rev\":{\"$eq\":\"%s\"}}]}}";
	
}