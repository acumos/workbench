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
public class Publisher implements Serializable {

	private static final long serialVersionUID = 6129104192079938567L;
	private Identifier publisherId;
	private DataSet dataset;
	private Topic topic;
	private User owner;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	/**
	 * Parameterized Constructor
	 * @param publisherId
	 * 		Identifier of Publisher
	 * @param dataset
	 * 		DataSet associated with Publisher
	 * @param topic
	 * 		Topic associated with Publisher
	 * @param owner
	 * 		User as owner of Publisher
	 * @param projects
	 * 		Publisher associated to Projects
	 * @param serviceStatus
	 * 		ServiceState of Publisher
	 * @param artifactState
	 * 		ArtifactState of Publisher
	 */
	public Publisher(Identifier publisherId, DataSet dataset, Topic topic,
			User owner, Projects projects, ServiceState serviceStatus,
			ArtifactState artifactStatus) {
		super();
		this.publisherId = publisherId;
		this.dataset = dataset;
		this.topic = topic;
		this.owner = owner;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}
	
	public Publisher() { 
		super();
	}

	/**
	 * @return the publisherId
	 */
	public Identifier getPublisherId() {
		return publisherId;
	}

	/**
	 * @param publisherId the publisherId to set
	 */
	public void setPublisherId(Identifier publisherId) {
		this.publisherId = publisherId;
	}

	/**
	 * @return the dataset
	 */
	public DataSet getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return the topic
	 */
	public Topic getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(Topic topic) {
		this.topic = topic;
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
				((dataset == null) ? 0 : dataset),
				((owner == null) ? 0 : owner),
				((projects == null) ? 0 : projects),
				((publisherId == null) ? 0 : publisherId),
				((serviceStatus == null) ? 0 : serviceStatus),
				((topic == null) ? 0 : topic));
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
		if (!(obj instanceof Publisher)) {
			return false;
		}
		Publisher other = (Publisher) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (dataset == null) {
			if (other.dataset != null) {
				return false;
			}
		} else if (!dataset.equals(other.dataset)) {
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
		if (publisherId == null) {
			if (other.publisherId != null) {
				return false;
			}
		} else if (!publisherId.equals(other.publisherId)) {
			return false;
		}
		if (serviceStatus == null) {
			if (other.serviceStatus != null) {
				return false;
			}
		} else if (!serviceStatus.equals(other.serviceStatus)) {
			return false;
		}
		if (topic == null) {
			if (other.topic != null) {
				return false;
			}
		} else if (!topic.equals(other.topic)) {
			return false;
		}
		return true;
	}
	
	
}
