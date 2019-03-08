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

package org.acumos.workbench.projectservice.vo;

import java.io.Serializable;

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

}
