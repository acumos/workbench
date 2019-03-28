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

import org.acumos.workbench.common.util.IdentifierType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author vs00485966
 *
 */
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
	 * Parameterized Constructor
	 * @param uuid
	 * 		The ussi for Identifier
	 * @param name
	 * 		The name of the Ientifier
	 * @param identifierType
	 * 		The type of the Identifier object
	 * @param metrics
	 * 		the metrics object
	 * @param versionId
	 * 		the Version Object 
	 * @param repositoryUrl
	 * 		the repository URL of Identifier
	 * @param serviceUrl
	 * 		the service URL of the Identifier.
	 */
	public Identifier(String uuid, String name, IdentifierType identifierType,
			KVPairs metrics, Version versionId, String repositoryUrl,
			String serviceUrl) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.identifierType = identifierType;
		this.metrics = metrics;
		this.versionId = versionId;
		this.repositoryUrl = repositoryUrl;
		this.serviceUrl = serviceUrl;
	}
	
	/**
	 * Default Constructor
	 */
	public Identifier() { 
		this(null, null, null, null, null, null, null);
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				((identifierType == null) ? 0 : identifierType),
				((metrics == null) ? 0 : metrics),
				((name == null) ? 0 : name),
				((repositoryUrl == null) ? 0 : repositoryUrl),
				((serviceUrl == null) ? 0 : serviceUrl),
				((uuid == null) ? 0 : uuid),
				((versionId == null) ? 0 : versionId));
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
		if (!(obj instanceof Identifier)) {
			return false;
		}
		Identifier other = (Identifier) obj;
		if (identifierType != other.identifierType) {
			return false;
		}
		if (metrics == null) {
			if (other.metrics != null) {
				return false;
			}
		} else if (!metrics.equals(other.metrics)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (repositoryUrl == null) {
			if (other.repositoryUrl != null) {
				return false;
			}
		} else if (!repositoryUrl.equals(other.repositoryUrl)) {
			return false;
		}
		if (serviceUrl == null) {
			if (other.serviceUrl != null) {
				return false;
			}
		} else if (!serviceUrl.equals(other.serviceUrl)) {
			return false;
		}
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		if (versionId == null) {
			if (other.versionId != null) {
				return false;
			}
		} else if (!versionId.equals(other.versionId)) {
			return false;
		}
		return true;
	}

	
	
}