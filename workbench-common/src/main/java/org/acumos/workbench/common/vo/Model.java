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
public class Model implements Serializable {

	private static final long serialVersionUID = 8724739807638716356L;
	private Identifier modelId;
	private User owner;
	private Users users;
	private DataSets dataSets;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	/**
	 * Parameterized Constructor 
	 * @param modelId
	 * 		The Identifier for model 
	 * @param owner
	 * 		The User as owner for model
	 * @param users
	 * 		The users associated to model
	 * @param dataSets
	 * 		The DataSets associated to a model
	 * @param projects
	 * 		The Projects associated to a model
	 * @param serviceStatus
	 * 		The serviceState of a model. 
	 * @param artifactStatus
	 * 		The ArtifactState of a mode
	 */
	public Model(Identifier modelId, User owner, Users users,
			DataSets dataSets, Projects projects, ServiceState serviceStatus,
			ArtifactState artifactStatus) {
		super();
		this.modelId = modelId;
		this.owner = owner;
		this.users = users;
		this.dataSets = dataSets;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}

	/**
	 * Default Constructor
	 */
	public Model() { 
		this(null, null, null, null, null, null, null);
	}
	/**
	 * @return the modelId
	 */
	public Identifier getModelId() {
		return modelId;
	}

	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(Identifier modelId) {
		this.modelId = modelId;
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
	 * @return the dataSets
	 */
	public DataSets getDataSets() {
		return dataSets;
	}

	/**
	 * @param dataSets the dataSets to set
	 */
	public void setDataSets(DataSets dataSets) {
		this.dataSets = dataSets;
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
	 * @return the serviceStatus
	 */
	public ServiceState getServiceStatus() {
		return serviceStatus;
	}

	/**
	 * @param serviceStatus the serviceStatus to set
	 */
	public void setServiceStatus(ServiceState serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	/**
	 * @return the artifactStatus
	 */
	public ArtifactState getArtifactStatus() {
		return artifactStatus;
	}

	/**
	 * @param artifactStatus the artifactStatus to set
	 */
	public void setArtifactStatus(ArtifactState artifactStatus) {
		this.artifactStatus = artifactStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(((artifactStatus == null) ? 0 : artifactStatus),
				((dataSets == null) ? 0 : dataSets), 
				((modelId == null) ? 0 : modelId), 
				((owner == null) ? 0 : owner),
				((projects == null) ? 0 : projects),
				((serviceStatus == null) ? 0 : serviceStatus),
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
		if (!(obj instanceof Model)) {
			return false;
		}
		Model other = (Model) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (dataSets == null) {
			if (other.dataSets != null) {
				return false;
			}
		} else if (!dataSets.equals(other.dataSets)) {
			return false;
		}
		if (modelId == null) {
			if (other.modelId != null) {
				return false;
			}
		} else if (!modelId.equals(other.modelId)) {
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
		if (serviceStatus == null) {
			if (other.serviceStatus != null) {
				return false;
			}
		} else if (!serviceStatus.equals(other.serviceStatus)) {
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
