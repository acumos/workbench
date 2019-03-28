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
public class DataSource implements Serializable {

	private static final long serialVersionUID = 6401829205426213874L;
	private Identifier datasourceId;
	private User owner;
	private Users users;
	private KVPairs datasourceConfiguration;
	private Projects projects;
	
	
	
	/**
	 * Parameterized Constructo 
	 * @param datasourceId
	 * 		the datasourceId
	 * @param owner
	 * 		the owner object
	 * @param users
	 * 		the Users object
	 * @param datasourceConfiguration
	 * 		the KVPairs object
	 * @param projects
	 * 		the Projects object
	 */
	public DataSource(Identifier datasourceId, User owner, Users users,
			KVPairs datasourceConfiguration, Projects projects) {
		super();
		this.datasourceId = datasourceId;
		this.owner = owner;
		this.users = users;
		this.datasourceConfiguration = datasourceConfiguration;
		this.projects = projects;
	}

	/**
	 * Default Constructor
	 */
	public DataSource() {
		this(null,null,null,null,null);
	}
	
	/**
	 * @return the datasourceId
	 */
	public Identifier getDatasourceId() {
		return datasourceId;
	}

	/**
	 * @param datasourceId the datasourceId to set
	 */
	public void setDatasourceId(Identifier datasourceId) {
		this.datasourceId = datasourceId;
	}

	/**
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;
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

	/**
	 * @return the datasourceConfiguration
	 */
	public KVPairs getDatasourceConfiguration() {
		return datasourceConfiguration;
	}

	/**
	 * @param datasourceConfiguration the datasourceConfiguration to set
	 */
	public void setDatasourceConfiguration(KVPairs datasourceConfiguration) {
		this.datasourceConfiguration = datasourceConfiguration;
	}

	/**
	 * @return the projects
	 */
	public Projects getProjects() {
		return projects;
	}

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(Projects projects) {
		this.projects = projects;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(((datasourceConfiguration == null) ? 0
				: datasourceConfiguration), ((datasourceId == null) ? 0
				: datasourceId), ((owner == null) ? 0 : owner),
				((projects == null) ? 0 : projects), ((users == null) ? 0
						: users));
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
		if (!(obj instanceof DataSource)) {
			return false;
		}
		DataSource other = (DataSource) obj;
		if (datasourceConfiguration == null) {
			if (other.datasourceConfiguration != null) {
				return false;
			}
		} else if (!datasourceConfiguration
				.equals(other.datasourceConfiguration)) {
			return false;
		}
		if (datasourceId == null) {
			if (other.datasourceId != null) {
				return false;
			}
		} else if (!datasourceId.equals(other.datasourceId)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		if (projects == null) {
			if (other.projects != null) {
				return false;
			}
		} else if (!projects.equals(other.projects)) {
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