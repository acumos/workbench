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

package org.acumos.workbench.projectservice.lightcouch;

import java.util.HashMap;
public class DatasetCollaborator {
	
	private String _id;//Auto generated CouchDb documents _Id(_id)
	private String _rev;// Auto generated CouchDb documents revisionId(_rev)
	private String projectId;// ProjectId from Maria DB and use API for additional details… we can duplicate data later
	private String projectOwner;// User ID from Maria DB <copy>
	private HashMap<String, String> projectCollaborator;// Nested structure name value pair<userid, role>
	private String createdTimestamp;// Creation time stamp
	private String updateTimestamp;// Modification time stamp

	/**
	 * 
	 * @return projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * 
	 * @param projectId
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * 
	 * @return projectOwner
	 */
	public String getProjectOwner() {
		return projectOwner;
	}

	/**
	 * 
	 * @param projectOwner
	 */
	public void setProjectOwner(String projectOwner) {
		this.projectOwner = projectOwner;
	}

	/**
	 * 
	 * @return createdTimestamp
	 */
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}

	/**
	 * 
	 * @param createdTimestamp
	 */
	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	/**
	 * 
	 * @return updateTimestamp
	 */
	public String getUpdateTimestamp() {
		return updateTimestamp;
	}

	/**
	 * 
	 * @param updateTimestamp
	 */
	public void setUpdateTimestamp(String updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	/**
	 * 
	 * @return projectCollaborator
	 */
	public HashMap<String, String> getProjectCollaborator() {
		return projectCollaborator;
	}

	/**
	 * 
	 * @param projectCollaborator
	 */
	public void setProjectCollaborator(HashMap<String, String> projectCollaborator) {
		this.projectCollaborator = projectCollaborator;
	}

	/**
	 * 
	 * @return _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * 
	 * @param _id
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * 
	 * @return _rev
	 */
	public String get_rev() {
		return _rev;
	}

	/**
	 * 
	 * @param _rev
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}

}
