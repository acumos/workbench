/*-
 * ===============LICENSE_START=======================================================
 * Acumos
  * ===================================================================================
 * Copyright (C) 2017 - 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.projectservice.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Organization implements Serializable {

	private static final long serialVersionUID = -6544696360488321151L;
	private Identifier organizationId;
	private User primaryAdmin;
	private User secondaryAdmin;
	private Users users;
	
	/**
	 * @return the organizationId
	 */
	public Identifier getOrganizationId() {
		return organizationId;
	}

	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(Identifier organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * @return the primaryAdmin
	 */
	public User getPrimaryAdmin() {
		return primaryAdmin;
	}

	/**
	 * @param primaryAdmin the primaryAdmin to set
	 */
	public void setPrimaryAdmin(User primaryAdmin) {
		this.primaryAdmin = primaryAdmin;
	}

	/**
	 * @return the secondaryAdmin
	 */
	public User getSecondaryAdmin() {
		return secondaryAdmin;
	}

	/**
	 * @param secondaryAdmin the secondaryAdmin to set
	 */
	public void setSecondaryAdmin(User secondaryAdmin) {
		this.secondaryAdmin = secondaryAdmin;
	}

	/**
	 * @return the users
	 */
	public Users getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Users users) {
		this.users = users;
	}
}