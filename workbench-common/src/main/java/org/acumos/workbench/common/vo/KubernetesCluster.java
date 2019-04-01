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

import org.acumos.workbench.common.util.Environment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class KubernetesCluster implements Serializable {

	private static final long serialVersionUID = -2733480517603139528L;
	private Identifier kubernetesClusterId;
	private String namespace;
	private KVPairs configuration;
	private Environment type;
	
	
	
	/**
	 * Parameterized Constructor 
	 * 
	 * @param kubernetesClusterId
	 * 		The Identifier object for Kuberneties Cluster Id 
	 * @param namespace
	 * 		name of Kubernetes Cluster 
	 * @param configuration
	 * 		KVPairs object for Kubernetes Cluster 
	 * @param type
	 * 		Environment object for Kubernetes Cluster 
	 */
	public KubernetesCluster(Identifier kubernetesClusterId, String namespace,
			KVPairs configuration, Environment type) {
		super();
		this.kubernetesClusterId = kubernetesClusterId;
		this.namespace = namespace;
		this.configuration = configuration;
		this.type = type;
	}

	/**
	 * Default Constructor
	 */
	public KubernetesCluster() { 
		this(null, null, null, null);
	}
	
	/**
	 * @return the kubernetesClusterId
	 */
	public Identifier getKubernetesClusterId() {
		return kubernetesClusterId;
	}

	/**
	 * @param kubernetesClusterId the kubernetesClusterId to set
	 */
	public void setKubernetesClusterId(Identifier kubernetesClusterId) {
		this.kubernetesClusterId = kubernetesClusterId;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
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
	 * @return the type
	 */
	public Environment getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Environment type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(((configuration == null) ? 0 : configuration),
		((kubernetesClusterId == null) ? 0 : kubernetesClusterId),
		((namespace == null) ? 0 : namespace),
		((type == null) ? 0 : type));
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
		if (!(obj instanceof KubernetesCluster)) {
			return false;
		}
		KubernetesCluster other = (KubernetesCluster) obj;
		if (configuration == null) {
			if (other.configuration != null) {
				return false;
			}
		} else if (!configuration.equals(other.configuration)) {
			return false;
		}
		if (kubernetesClusterId == null) {
			if (other.kubernetesClusterId != null) {
				return false;
			}
		} else if (!kubernetesClusterId.equals(other.kubernetesClusterId)) {
			return false;
		}
		if (namespace == null) {
			if (other.namespace != null) {
				return false;
			}
		} else if (!namespace.equals(other.namespace)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	
	
}