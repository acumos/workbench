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
public class Solution implements Serializable {

	private static final long serialVersionUID = -36575960552205381L;
	private Identifier solutionId;
	private User owner;
	private Users users;
	private Pipelines pipelines;
	private CompositeModel models;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	/**
	 * Parameterized Constructor 
	 * 
	 * @param solutionId
	 * 		Identifier of Solution
	 * @param owner
	 * 		User as owner of Solution
	 * @param users
	 * 		Users associated to Solution 
	 * @param pipelines
	 * 		Pipelines associated to Solution
	 * @param models
	 * 		Models associated to Solution
	 * @param projects
	 * 		Projects to which Solution is associated with.
	 * @param serviceStatus
	 * 		ServiceState of Solution
	 * @param artifactStatus
	 * 		ArtifactState of Solution
	 */
	public Solution(Identifier solutionId, User owner, Users users,
			Pipelines pipelines, CompositeModel models, Projects projects,
			ServiceState serviceStatus, ArtifactState artifactStatus) {
		super();
		this.solutionId = solutionId;
		this.owner = owner;
		this.users = users;
		this.pipelines = pipelines;
		this.models = models;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}

	public Solution() { 
		super();
	}
	
	/**
	 * @return the solutionId
	 */
	public Identifier getSolutionId() {
		return solutionId;
	}

	/**
	 * @param solutionId the solutionId to set
	 */
	public void setSolutionId(Identifier solutionId) {
		this.solutionId = solutionId;
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
	public CompositeModel getModels() {
		return models;
	}

	/**
	 * @param models the models to set
	 */
	public void setModels(CompositeModel models) {
		this.models = models;
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
		return Objects.hash(
				((artifactStatus == null) ? 0 : artifactStatus),
				((models == null) ? 0 : models),
				((owner == null) ? 0 : owner),
				((pipelines == null) ? 0 : pipelines),
				((projects == null) ? 0 : projects),
				((serviceStatus == null) ? 0 : serviceStatus),
				((solutionId == null) ? 0 : solutionId),
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
		if (!(obj instanceof Solution)) {
			return false;
		}
		Solution other = (Solution) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (models == null) {
			if (other.models != null) {
				return false;
			}
		} else if (!models.equals(other.models)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
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
		if (serviceStatus == null) {
			if (other.serviceStatus != null) {
				return false;
			}
		} else if (!serviceStatus.equals(other.serviceStatus)) {
			return false;
		}
		if (solutionId == null) {
			if (other.solutionId != null) {
				return false;
			}
		} else if (!solutionId.equals(other.solutionId)) {
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
