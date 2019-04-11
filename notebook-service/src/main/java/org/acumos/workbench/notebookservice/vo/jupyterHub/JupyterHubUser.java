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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class JupyterHubUser implements Serializable{

	private static final long serialVersionUID = -7938216145231932568L;
	private String kind;
	private String name;
	private boolean admin;
	private String[] groups;
	private String server;
	private String pending;
	private String created;	
	@JsonProperty(value="last_activity")
	private String lastActivity;
	private Object servers;
	@JsonProperty(value = "auth_state")
	private String authState;
	
	/**
	 * 
	 * Parameterized Constructor
	 * @param kind
	 * 		Type of user 
	 * @param name
	 * 		The user's name
	 * @param admin
	 * 		Whether user is an admin
	 * @param groups
	 * 		The names of groups where this user is a member
	 * @param server
	 * 		The user's notebook server's base URL, if running; null if not
	 * @param pending
	 * 		The currently pending action, if any, { "spawn" , "stop" , "" }
	 * @param created 
	 * 		Timestamp of user created
	 * @param lastActivity
	 * 		Timestamp of last-seen activity from the user
	 * @param servers
	 * 		The active servers for this user.
	 * @param authState
	 */
	public JupyterHubUser(String kind, String name, boolean admin,
			String[] groups, String server, String pending, String created,
			String lastActivity, Object servers, String authState) {
		super();
		this.kind = kind;
		this.name = name;
		this.admin = admin;
		this.groups = groups;
		this.server = server;
		this.pending = pending;
		this.created = created;
		this.lastActivity = lastActivity;
		this.servers = servers;
		this.authState = authState;
	}
	
	/**
	 * Default constructor.
	 */
	public JupyterHubUser() { 
		super();
	}

	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * @return the groups
	 */
	public String[] getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the pending
	 */
	public String getPending() {
		return pending;
	}

	/**
	 * @param pending the pending to set
	 */
	public void setPending(String pending) {
		this.pending = pending;
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

	/**
	 * @return the servers
	 */
	public Object getServers() {
		return servers;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setServers(Object servers) {
		this.servers = servers;
	}

	/**
	 * @return the authState
	 */
	public String getAuthState() {
		return authState;
	}

	/**
	 * @param authState the authState to set
	 */
	public void setAuthState(String authState) {
		this.authState = authState;
	}
	
	
	
}
