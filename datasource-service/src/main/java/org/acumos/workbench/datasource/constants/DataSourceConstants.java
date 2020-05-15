/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.datasource.constants;

public class DataSourceConstants {

	public static final String FIND_DATASOURCE_FOR_KEY_AND_USER = "{\"selector\":{\"$and\":[{\"owner.authenticatedUserId\":{\"$eq\":\"%s\"}},{\"datasourceId.uuid\":{\"$eq\":\"%s\"}}]}}";
	public static final  String GET_DATASOURCE_FOR_A_KEY = "{\"selector\":{\"$and\":[{\"datasourceId.uuid\":{\"$eq\":\"%s\"}}]}}";
	public static final String ASSOCIATION_EXISTS_IN_COUCH_QUERY ="{\"selector\":{\"$and\":[{\"authenticatedUserId\":{\"$eq\":\"%s\"}},{\"projectId\":{\"$eq\":\"%s\"}},{\"dataSourceKey\":{\"$eq\":\"%s\"}},{\"dataSourceVersion\":{\"$eq\":\"%s\"}},{\"associationStatus\":{\"$eq\":\"%s\"}}]}}";
	public static final String PROJECT_EXISTS_IN_ASSOCIATION_QUERY = "{\"selector\":{\"$and\":[{\"authenticatedUserId\":{\"$eq\":\"%s\"}},{\"projectId\":{\"$eq\":\"%s\"}}]}}";
	public static final String DATA_SOURCE_COLLABORATION = "{\"selector\":{\"$and\":[{\"dataSourceKey\":{\"$eq\":\"%s\"}},{\"dataSourceCollaborator\":{\"$gte\":null}}]}}";
	public static final String GET_ALL_SHARED_DATASOURCES = "{\"selector\":{\"dataSourceCollaborator\":{\"$gt\":null}},\"fields\":[\"dataSourceCollaborator\",\"dataSourceKey\"]}";
	public static final String DATA_SOURCE_ASSOCIATION_DETAILS = "{\"selector\":{\"$and\":[{\"authenticatedUserId\":{\"$eq\":\"%s\"}},{\"dataSourceKey\":{\"$eq\":\"%s\"}},{\"dataSourceVersion\":{\"$eq\":\"%s\"}}]}}";
	
}
