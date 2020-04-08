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

package org.acumos.workbench.predictorservice.deployClient;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

public interface DeploymentService {
	/**
	 * This method will deploy a model to kubernetes Environment 
	 * @param userId
	 * 			the userId
	 * @param solutionId
	 * 			the solutionId
	 * @param revisionId
	 * 			the revisionId
	 * @param envId
	 * 			the envId
	 * @return
	 * 		   response String
	 * @throws IOException 
	 */
	ResponseEntity<String> deployModelToK8s(String userId,String solutionId,String revisionId,String envId) throws IOException;

}
