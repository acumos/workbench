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
public class Topic implements Serializable {

	private static final long serialVersionUID = 8609832905181329281L;
	private Identifier topicid;
	private User owner;
	private Users users;
	private KVPairs configuration;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	/**
	 * Parameterized Constructor
	 * @param topicid
	 * 		Identifier of Topic
	 * @param owner
	 * 		User as owner of Topic
	 * @param users
	 * 		Users associated to Topic
	 * @param configuration
	 * 		KVPairs configuration of Topic
	 * @param projects
	 * 		Projects associated to Topic
	 * @param serviceStatus
	 * 		ServiceState of Topic
	 * @param artifactStatus
	 * 		ArtifactState of Topic
	 */
	public Topic(Identifier topicid, User owner, Users users,
			KVPairs configuration, Projects projects,
			ServiceState serviceStatus, ArtifactState artifactStatus) {
		super();
		this.topicid = topicid;
		this.owner = owner;
		this.users = users;
		this.configuration = configuration;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}

	/**
	 * @return the topicid
	 */
	public Identifier getTopicid() {
		return topicid;
	}

	/**
	 * @param topicid the topicid to set
	 */
	public void setTopicid(Identifier topicid) {
		this.topicid = topicid;
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
	 * @return the configuration
	 */
	public KVPairs getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(KVPairs configuration) {
		this.configuration = configuration;
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
				((configuration == null) ? 0 : configuration),
				((owner == null) ? 0 : owner),
				((projects == null) ? 0 : projects),
				((serviceStatus == null) ? 0 : serviceStatus),
				((topicid == null) ? 0 : topicid),
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
		if (!(obj instanceof Topic)) {
			return false;
		}
		Topic other = (Topic) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (configuration == null) {
			if (other.configuration != null) {
				return false;
			}
		} else if (!configuration.equals(other.configuration)) {
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
		if (topicid == null) {
			if (other.topicid != null) {
				return false;
			}
		} else if (!topicid.equals(other.topicid)) {
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
