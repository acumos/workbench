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

public enum ArtifactStatus {
	ACTIVE(0), // READY FOR USE
	ARCHIVED(1); // Can be Made Active again
	
	private final int artifactStatusCode;
	
	private ArtifactStatus(int artifactStatusCode){
		this.artifactStatusCode = artifactStatusCode;
	}
	
	/**
	 * To int value of ArtifactStatus.
	 * @return int
	 * 		returns int value of ArtifactStatus. 
	 */
	public int getArtifactStatusCode() { 
		return this.artifactStatusCode;
	}
}
