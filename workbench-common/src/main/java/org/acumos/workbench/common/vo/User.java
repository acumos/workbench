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
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class User implements Serializable {

	private static final long serialVersionUID = 1561100724301191282L;
	private Identifier userId;
	private String authenticatedUserId;
	private List<Role> roles;
	private Organization organization;
	private Projects projects;
	private Notebooks notebooks;
	private Pipelines pipelines;
	private Models models;
	
	
	
	/**
	 * Parameterized Constructor
	 * @param userId
	 * 		Identifier of User
	 * @param authenticatedUserId
	 * 		Acumos User login Id
	 * @param roles
	 * 		Roles associated to User
	 * @param organization
	 * 		organization of User
	 * @param projects
	 * 		Projects accessible to User
	 * @param notebooks
	 * 		Notebooks accessible to User
	 * @param pipelines
	 * 		Pipelines accessible to User
	 * @param models
	 * 		Models accessible to User
	 */
	public User(Identifier userId, String authenticatedUserId, List<Role> roles,
			Organization organization, Projects projects, Notebooks notebooks,
			Pipelines pipelines, Models models) {
		super();
		this.userId = userId;
		this.authenticatedUserId = authenticatedUserId;
		this.roles = roles;
		this.organization = organization;
		this.projects = projects;
		this.notebooks = notebooks;
		this.pipelines = pipelines;
		this.models = models;
	}
	
	/**
	 * Default Constructor
	 */
	public User() { 
		super();
	}

	/**
	 * @return the userId
	 */
	public Identifier getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Identifier userId) {
		this.userId = userId;
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
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
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

	/**
	 * @return the notebooks
	 */
	public Notebooks getNotebooks() {
		return notebooks;
	}

	/**
	 * @param notebooks the notebooks to set
	 */
	public void setNotebooks(Notebooks notebooks) {
		this.notebooks = notebooks;
	}

	/**
	 * @return the pipelines
	 */
	public Pipelines getPipelines() {
		return pipelines;
	}

	/**
	 * @param pipelines the pipelines to set
	 */
	public void setPipelines(Pipelines pipelines) {
		this.pipelines = pipelines;
	}

	/**
	 * @return the models
	 */
	public Models getModels() {
		return models;
	}

	/**
	 * @param models the models to set
	 */
	public void setModels(Models models) {
		this.models = models;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				((authenticatedUserId == null) ? 0 : authenticatedUserId),
				((models == null) ? 0 : models),
				((notebooks == null) ? 0 : notebooks),
				((organization == null) ? 0 : organization),
				((pipelines == null) ? 0 : pipelines),
				((projects == null) ? 0 : projects),
				((roles == null) ? 0 : roles),
				((userId == null) ? 0 : userId));
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (authenticatedUserId == null) {
			if (other.authenticatedUserId != null) {
				return false;
			}
		} else if (!authenticatedUserId.equals(other.authenticatedUserId)) {
			return false;
		}
		if (models == null) {
			if (other.models != null) {
				return false;
			}
		} else if (!models.equals(other.models)) {
			return false;
		}
		if (notebooks == null) {
			if (other.notebooks != null) {
				return false;
			}
		} else if (!notebooks.equals(other.notebooks)) {
			return false;
		}
		if (organization == null) {
			if (other.organization != null) {
				return false;
			}
		} else if (!organization.equals(other.organization)) {
			return false;
		}
		if (pipelines == null) {
			if (other.pipelines != null) {
				return false;
			}
		} else if (!pipelines.equals(other.pipelines)) {
			return false;
		}
		if (projects == null) {
			if (other.projects != null) {
				return false;
			}
		} else if (!projects.equals(other.projects)) {
			return false;
		}
		if (roles == null) {
			if (other.roles != null) {
				return false;
			}
		} else if (!roles.equals(other.roles)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}

	
}
