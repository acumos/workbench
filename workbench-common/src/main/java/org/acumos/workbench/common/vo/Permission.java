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
public class Permission implements Serializable {

	private static final long serialVersionUID = 4543848571418160114L;
	private Identifier permission;

	
	
	/**
	 * Parameterized Constructor.
	 * @param permission
	 * 		The Identifier of Permission
	 */
	public Permission(Identifier permission) {
		super();
		this.permission = permission;
	}

	/**
	 * Default Constructor.
	 */
	public Permission(){
		this(null);
	}
	
	/**
	 * @return the permission
	 */
	public Identifier getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Identifier permission) {
		this.permission = permission;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((permission == null) ? 0 : permission.hashCode());
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
		if (!(obj instanceof Permission)) {
			return false;
		}
		Permission other = (Permission) obj;
		if (permission == null) {
			if (other.permission != null) {
				return false;
			}
		} else if (!permission.equals(other.permission)) {
			return false;
		}
		return true;
	}

	
	
}