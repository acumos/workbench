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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Project implements Serializable {

	private static final long serialVersionUID = -2793384729170778423L;
	private Identifier projectId;
	private User owner;
	private Users collaborators;
	private String description;
	private Template template;
	private Pipelines pipelines;
	private DataSources dataSources;
	private DataSets dataSets;
	private Notebooks notebooks;
	private Models models;
	private Topics topics;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	
	
	/**
	 * Parameterized Constructor
	 * @param projectId
	 * 		Identifier of Project
	 * @param owner
	 * 		User as a owner of Project
	 * @param collaborators
	 * 		Users as collaborators of Project
	 * @param description
	 * 		Description of Project
	 * @param template
	 * 		Template associated to Project
	 * @param pipelines
	 * 		Pipelines associated to Project
	 * @param dataSources
	 * 		DataSources associated to Project
	 * @param dataSets
	 * 		DataSets associated to Project
	 * @param notebooks
	 * 		Notebooks associated to Project
	 * @param models
	 * 		Models associated to Project
	 * @param topics
	 * 		Topics associated to Project
	 * @param serviceStatus
	 * 		ServiceStat of Project
	 * @param artifactStatus
	 * 		ArtifactState of Project
	 */
	public Project(Identifier projectId, User owner, Users collaborators,
			String description, Template template, Pipelines pipelines,
			DataSources dataSources, DataSets dataSets, Notebooks notebooks,
			Models models, Topics topics, ServiceState serviceStatus,
			ArtifactState artifactStatus) {
		super();
		this.projectId = projectId;
		this.owner = owner;
		this.collaborators = collaborators;
		this.description = description;
		this.template = template;
		this.pipelines = pipelines;
		this.dataSources = dataSources;
		this.dataSets = dataSets;
		this.notebooks = notebooks;
		this.models = models;
		this.topics = topics;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}
	
	/**
	 * Default Constructor
	 */
	public Project() { 
		super();
	}

	/**
	 * @return the projectId
	 */
	public Identifier getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Identifier projectId) {
		this.projectId = projectId;
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
	 * @return the collaborators
	 */
	public Users getCollaborators() {
		return collaborators;
	}

	/**
	 * @param collaborators the collaborators to set
	 */
	public void setCollaborators(Users collaborators) {
		this.collaborators = collaborators;
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
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(Template template) {
		this.template = template;
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
	 * @return the dataSources
	 */
	public DataSources getDataSources() {
		return dataSources;
	}

	/**
	 * @param dataSources the dataSources to set
	 */
	public void setDataSources(DataSources dataSources) {
		this.dataSources = dataSources;
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

	/**
	 * @return the topics
	 */
	public Topics getTopics() {
		return topics;
	}

	/**
	 * @param topics the topics to set
	 */
	public void setTopics(Topics topics) {
		this.topics = topics;
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
				((collaborators == null) ? 0 : collaborators),
				((dataSets == null) ? 0 : dataSets),
				((dataSources == null) ? 0 : dataSources),
				((description == null) ? 0 : description),
				((models == null) ? 0 : models),
				((notebooks == null) ? 0 : notebooks),
				((owner == null) ? 0 : owner),
				((pipelines == null) ? 0 : pipelines),
				((projectId == null) ? 0 : projectId),
				((serviceStatus == null) ? 0 : serviceStatus),
				((template == null) ? 0 : template),
				((topics == null) ? 0 : topics));
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
		if (!(obj instanceof Project)) {
			return false;
		}
		Project other = (Project) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (collaborators == null) {
			if (other.collaborators != null) {
				return false;
			}
		} else if (!collaborators.equals(other.collaborators)) {
			return false;
		}
		if (dataSets == null) {
			if (other.dataSets != null) {
				return false;
			}
		} else if (!dataSets.equals(other.dataSets)) {
			return false;
		}
		if (dataSources == null) {
			if (other.dataSources != null) {
				return false;
			}
		} else if (!dataSources.equals(other.dataSources)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
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
		if (projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!projectId.equals(other.projectId)) {
			return false;
		}
		if (serviceStatus == null) {
			if (other.serviceStatus != null) {
				return false;
			}
		} else if (!serviceStatus.equals(other.serviceStatus)) {
			return false;
		}
		if (template == null) {
			if (other.template != null) {
				return false;
			}
		} else if (!template.equals(other.template)) {
			return false;
		}
		if (topics == null) {
			if (other.topics != null) {
				return false;
			}
		} else if (!topics.equals(other.topics)) {
			return false;
		}
		return true;
	}

	
}
