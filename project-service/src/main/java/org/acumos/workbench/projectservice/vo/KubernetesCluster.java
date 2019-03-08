/*-
 * ===============LICENSE_START=======================================================
 * Acumos
  * ===================================================================================
 * Copyright (C) 2017 - 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import org.acumos.workbench.projectservice.util.Environment;

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

}