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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Role implements Serializable {

	private static final long serialVersionUID = -8558190408941066945L;
	private Identifier roleId;
	private List<Permission> permissions;
	
	
	/**
	 * Parameterized Constructor
	 * @param roleId
	 * 		Identifier of Role
	 * @param permissions
	 * 		Permissions on Role
	 */
	public Role(Identifier roleId, List<Permission> permissions) {
		super();
		this.roleId = roleId;
		this.permissions = permissions;
	}

	public Role() { 
		super();
		permissions = new ArrayList<Permission>();
	}
	
	/**
	 * @return the roleId
	 */
	public Identifier getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(Identifier roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the permissions
	 */
	public List<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				((permissions == null) ? 0 : permissions),
				((roleId == null) ? 0 : roleId));
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
		if (!(obj instanceof Role)) {
			return false;
		}
		Role other = (Role) obj;
		if (permissions == null) {
			if (other.permissions != null) {
				return false;
			}
		} else if (!permissions.equals(other.permissions)) {
			return false;
		}
		if (roleId == null) {
			if (other.roleId != null) {
				return false;
			}
		} else if (!roleId.equals(other.roleId)) {
			return false;
		}
		return true;
	}

	
	
}
