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

import java.util.HashMap;

public class DataSourceCollaboratorModel {
	
	private String _id;//Auto generated CouchDb documents _Id(_id)
	private String _rev;// Auto generated CouchDb documents revisionId(_rev)
	private String dataSourceKey;// DataSourceKey from Maria DB and use API for additional details… we can duplicate data later
	private String dataSourceOwner;
	private HashMap<String, String> dataSourceCollaborator;// Nested structure name value pair<userid, role>
	private String createdTimestamp;// Creation time stamp
	private String updateTimestamp;// Modification time stamp
	
	
	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
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
	 * @return the dataSourceOwner
	 */
	public String getDataSourceOwner() {
		return dataSourceOwner;
	}
	/**
	 * @param dataSourceOwner the dataSourceOwner to set
	 */
	public void setDataSourceOwner(String dataSourceOwner) {
		this.dataSourceOwner = dataSourceOwner;
	}
	/**
	 * @return the dataSourceCollaborator
	 */
	public HashMap<String, String> getDataSourceCollaborator() {
		return dataSourceCollaborator;
	}
	/**
	 * @param dataSourceCollaborator the dataSourceCollaborator to set
	 */
	public void setDataSourceCollaborator(HashMap<String, String> dataSourceCollaborator) {
		this.dataSourceCollaborator = dataSourceCollaborator;
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
	 * @return the updateTimestamp
	 */
	public String getUpdateTimestamp() {
		return updateTimestamp;
	}
	/**
	 * @param updateTimestamp the updateTimestamp to set
	 */
	public void setUpdateTimestamp(String updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	
	
}
