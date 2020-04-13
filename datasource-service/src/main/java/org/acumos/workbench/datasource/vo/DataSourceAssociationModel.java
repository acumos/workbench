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

package org.acumos.workbench.datasource.vo;

public class DataSourceAssociationModel {
	private String _id; // AssociationId
	private String _rev;
	private String authenticatedUserId;
	private String dataSourceKey;
	private String associationStatus;
	private String projectId;
	private String dataSourceVersion;
	private String createdTimestamp;
	private String updatedTimestamp;


	/**
	 * @return the associationID
	 */
	public String getAssociationID() {
		return _id;
	}
	/**
	 * @param associationID the associationID to set
	 */
	public void setAssociationID(String associationID) {
		_id = associationID;
	}
	
	/**
	 * @return the _rev
	 */
	public String get_rev() {
		return _rev;
	}
	/**
	 * @param _rev the _rev to set
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	
	/**
	 * @return the authenticatedUserId
	 */
	public String getAuthenticatedUserId() {
		return authenticatedUserId;
	}
	/**
	 * @param authenticatedUserId the authenticatedUserId to set
	 */
	public void setAuthenticatedUserId(String authenticatedUserId) {
		this.authenticatedUserId = authenticatedUserId;
	}
	
	/**
	 * @return the dataSourceKey
	 */
	public String getDataSourceKey() {
		return dataSourceKey;
	}
	/**
	 * @param dataSourceKey the dataSourceKey to set
	 */
	public void setDataSourceKey(String dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}
	/**
	 * @return the associationStatus
	 */
	public String getAssociationStatus() {
		return associationStatus;
	}
	/**
	 * @param associationStatus the associationStatus to set
	 */
	public void setAssociationStatus(String associationStatus) {
		this.associationStatus = associationStatus;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the dataSourceVersion
	 */
	public String getDataSourceVersion() {
		return dataSourceVersion;
	}
	/**
	 * @param dataSourceVersion the dataSourceVersion to set
	 */
	public void setDataSourceVersion(String dataSourceVersion) {
		this.dataSourceVersion = dataSourceVersion;
	}
	/**
	 * @return the createdTimestamp
	 */
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}
	/**
	 * @param createdTimestamp the createdTimestamp to set
	 */
	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	/**
	 * @return the updatedTimestamp
	 */
	public String getUpdatedTimestamp() {
		return updatedTimestamp;
	}
	/**
	 * @param updatedTimestamp the updatedTimestamp to set
	 */
	public void setUpdatedTimestamp(String updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}

	
}
