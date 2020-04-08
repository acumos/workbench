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

public class DeploymentResponse {

	private String taskId;
	private String status;
	private String trackingId;
	private String jenkinUrl;
	private String deploymentUrl;
	

	/**
	 * 
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @return the trackingId
	 */
	public String getTrackingId() {
		return trackingId;
	}

	/**
	 * 
	 * @return the jenkinUrl
	 */
	public String getJenkinUrl() {
		return jenkinUrl;
	}

	/**
	 * 
	 * @return the deploymentUrl
	 */
	public String getDeploymentUrl() {
		return deploymentUrl;
	}

}
