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


package org.acumos.workbench.common.service;

import java.util.List;

import org.acumos.workbench.common.vo.Model;
import org.springframework.http.ResponseEntity;

public interface ModelServiceRestClient {
	
	/**
	 * Get Models for a particular projectId
	 * 
	 * @param authenticatedUserId
	 * 		Acumos User login Id
	 * @param projectId
	 * 		Project Id 
	 * @param authToken
	 * 		JWT Auth Token 
	 * @return
	 * 		returns List of Models
	 */
	ResponseEntity<List<Model>> getModels(String authenticatedUserId,String projectId, String authToken);

}
