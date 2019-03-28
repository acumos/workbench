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
public class Pipeline implements Serializable {

	private static final long serialVersionUID = -8130300172905471154L;
	private Identifier pipelineId;
	private String description;
	private User owner;
	private Users users;
	private Identifier associatedObjects;
	private DataSets dataSets;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	
	/**
	 * Parameterized Constructor 
	 * @param pipelineId
	 * 		The Identifier of Pipeline
	 * @param description
	 * 		Description of Pipeline
	 * @param owner
	 * 		User as a owner of Pipeline
	 * @param users
	 * 		Users of a Pipeline
	 * @param associatedObjects
	 * 		Identifier for associated Objects
	 * @param dataSets
	 * 		DataSets associated with Pipeline
	 * @param projects
	 * 		Projects associated with Pipeline
	 * @param serviceStatus
	 * 		ServiceState of Pipeline
	 * @param artifactStatus
	 * 		ArtifactState of Pipeline
	 */
	public Pipeline(Identifier pipelineId, String description, User owner,
			Users users, Identifier associatedObjects, DataSets dataSets,
			Projects projects, ServiceState serviceStatus,
			ArtifactState artifactStatus) {
		super();
		this.pipelineId = pipelineId;
		this.description = description;
		this.owner = owner;
		this.users = users;
		this.associatedObjects = associatedObjects;
		this.dataSets = dataSets;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}
	
	public Pipeline() { 
		super();
	}

	/**
	 * @return the pipelineId
	 */
	public Identifier getPipelineId() {
		return pipelineId;
	}

	/**
	 * @param pipelineId the pipelineId to set
	 */
	public void setPipelineId(Identifier pipelineId) {
		this.pipelineId = pipelineId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the associatedObjects
	 */
	public Identifier getAssociatedObjects() {
		return associatedObjects;
	}

	/**
	 * @param associatedObjects the associatedObjects to set
	 */
	public void setAssociatedObjects(Identifier associatedObjects) {
		this.associatedObjects = associatedObjects;
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
				((associatedObjects == null) ? 0 : associatedObjects),
				((dataSets == null) ? 0 : dataSets), 
				((description == null) ? 0	: description), 
				((owner == null) ? 0 : owner),
				((pipelineId == null) ? 0 : pipelineId),
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
		if (!(obj instanceof Pipeline)) {
			return false;
		}
		Pipeline other = (Pipeline) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (associatedObjects == null) {
			if (other.associatedObjects != null) {
				return false;
			}
		} else if (!associatedObjects.equals(other.associatedObjects)) {
			return false;
		}
		if (dataSets == null) {
			if (other.dataSets != null) {
				return false;
			}
		} else if (!dataSets.equals(other.dataSets)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		if (pipelineId == null) {
			if (other.pipelineId != null) {
				return false;
			}
		} else if (!pipelineId.equals(other.pipelineId)) {
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
