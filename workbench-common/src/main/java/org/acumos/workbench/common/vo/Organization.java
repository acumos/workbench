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

package org.acumos.workbench.common.vo;

import java.io.Serializable;
import java.util.Objects;

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
	 * Parameterized Constructor
	 * 
	 * @param organizationId
	 * 		The Identifier for Organization
	 * @param primaryAdmin
	 * 		The user as primary Admin of Organization
	 * @param secondaryAdmin
	 * 		The user as Secondary Admin of Organization
	 * @param users
	 * 		The user of the Organization
	 */
	public Organization(Identifier organizationId, User primaryAdmin,
			User secondaryAdmin, Users users) {
		super();
		this.organizationId = organizationId;
		this.primaryAdmin = primaryAdmin;
		this.secondaryAdmin = secondaryAdmin;
		this.users = users;
	}
	
	/**
	 * Default Constructor
	 */
	public Organization() { 
		this(null, null, null, null);
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				((organizationId == null) ? 0 : organizationId),
				((primaryAdmin == null) ? 0 : primaryAdmin),
				((secondaryAdmin == null) ? 0 : secondaryAdmin),
				((users == null) ? 0 : users));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Organization)) {
			return false;
		}
		Organization other = (Organization) obj;
		if (organizationId == null) {
			if (other.organizationId != null) {
				return false;
			}
		} else if (!organizationId.equals(other.organizationId)) {
			return false;
		}
		if (primaryAdmin == null) {
			if (other.primaryAdmin != null) {
				return false;
			}
		} else if (!primaryAdmin.equals(other.primaryAdmin)) {
			return false;
		}
		if (secondaryAdmin == null) {
			if (other.secondaryAdmin != null) {
				return false;
			}
		} else if (!secondaryAdmin.equals(other.secondaryAdmin)) {
			return false;
		}
		if (users == null) {
			if (other.users != null) {
				return false;
			}
		} else if (!users.equals(other.users)) {
			return false;
		}
		return true;
	}
	
	
}