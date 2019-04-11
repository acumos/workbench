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

package org.acumos.workbench.notebookservice.vo.jupyterHub;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JupyterHubToken implements Serializable {
	
	private static final long serialVersionUID = 8448386540219880300L;
	private String token;
	private String id;
	private String user;
	private String service;
	private String note;
	private String created;
	
	
	
	/**
	 * Parameterized Constructo
	 * @param token
	 * 		The token itself. Only present in responses to requests for a new token.
	 * @param id
	 * 		The id of the API token. Used for modifying or deleting the token.
	 * @param user
	 * 		The user that owns a token (undefined if owned by a service)
	 * @param service
	 * 		The service that owns the token (undefined of owned by a user)
	 * @param note
	 * 		A note about the token, typically describing what it was created for.
	 * @param created
	 * 		Timestamp when this token was created
	 * @param expiresAt
	 * 		Timestamp when this token expires. Null if there is no expiry.
	 * @param lastActivity
	 * 		Timestamp of last-seen activity using this token. Can be null if token has never been used.
	 */
	public JupyterHubToken(String token, String id, String user,
			String service, String note, String created, String expiresAt,
			String lastActivity) {
		super();
		this.token = token;
		this.id = id;
		this.user = user;
		this.service = service;
		this.note = note;
		this.created = created;
		this.expiresAt = expiresAt;
		this.lastActivity = lastActivity;
	}

	public JupyterHubToken() {
		super();
	}
	
	@JsonProperty(value="expires_ad")
	private String expiresAt;
	
	@JsonProperty(value="last_activity")
	private String lastActivity;

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the created
	 */
	public String getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(String created) {
		this.created = created;
	}

	/**
	 * @return the expiresAt
	 */
	public String getExpiresAt() {
		return expiresAt;
	}

	/**
	 * @param expiresAt the expiresAt to set
	 */
	public void setExpiresAt(String expiresAt) {
		this.expiresAt = expiresAt;
	}

	/**
	 * @return the lastActivity
	 */
	public String getLastActivity() {
		return lastActivity;
	}

	/**
	 * @param lastActivity the lastActivity to set
	 */
	public void setLastActivity(String lastActivity) {
		this.lastActivity = lastActivity;
	}
	
	
	
}
