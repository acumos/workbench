/*-
 * ===============LICENSE_START=======================================================
 * Acumos
  * ===================================================================================
 * Copyright (C) 2019 - 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Solution implements Serializable {

	private static final long serialVersionUID = -36575960552205381L;
	private Identifier solutionId;
	private User owner;
	private Users users;
	private Pipeline pipelines;
	private CompositeModel models;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
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
	public Pipeline getPipelines() {
		return pipelines;
	}

	/**
	 * @param pipelines the pipelines to set
	 */
	public void setPipelines(Pipeline pipelines) {
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

}
