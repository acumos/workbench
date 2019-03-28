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

package org.acumos.workbench.vo;

import java.io.Serializable;

import org.acumos.workbench.util.IdentifierType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Identifier implements Serializable {

	private static final long serialVersionUID = 8234723185906836734L;
	private String uuid;
	private String name;
	private IdentifierType identifierType;
	private KVPairs metrics;
	private Version versionId;
	private String repositoryUrl;
	private String serviceUrl;
	
	
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the identifierType
	 */
	public IdentifierType getIdentifierType() {
		return identifierType;
	}

	/**
	 * @param identifierType the identifierType to set
	 */
	public void setIdentifierType(IdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	/**
	 * @return the metrics
	 */
	public KVPairs getMetrics() {
		return metrics;
	}

	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(KVPairs metrics) {
		this.metrics = metrics;
	}

	/**
	 * @return the versionId
	 */
	public Version getVersionId() {
		return versionId;
	}

	/**
	 * @param versionId the versionId to set
	 */
	public void setVersionId(Version versionId) {
		this.versionId = versionId;
	}

	/**
	 * @return the repositoryUrl
	 */
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * @param repositoryUrl the repositoryUrl to set
	 */
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * @return the serviceUrl
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * @param serviceUrl the serviceUrl to set
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}