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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class User implements Serializable {

	private static final long serialVersionUID = 1561100724301191282L;
	private Identifier userId;
	private String authenticatedUserId;
	private Role roles;
	private Organization organization;
	private Projects projects;
	private NoteBooks notebooks;
	private Pipelines pipelines;
	private Models models;
	
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
	public Role getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Role roles) {
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
	public NoteBooks getNotebooks() {
		return notebooks;
	}

	/**
	 * @param notebooks the notebooks to set
	 */
	public void setNotebooks(NoteBooks notebooks) {
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

}
