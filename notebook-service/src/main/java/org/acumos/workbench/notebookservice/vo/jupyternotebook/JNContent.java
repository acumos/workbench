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

package org.acumos.workbench.notebookservice.vo.jupyternotebook;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JNContent implements Serializable {

	private static final long serialVersionUID = -8531988375050618048L;
	
	private List<Object> cells;
	private Object metadata;
	private int nbformat;
	@JsonProperty(value="nbformat_minor")
	private int nbformatMinor;
	
	/**
	 * @return the cells
	 */
	public List<Object> getCells() {
		return cells;
	}
	/**
	 * @param cells the cells to set
	 */
	public void setCells(List<Object> cells) {
		this.cells = cells;
	}
	/**
	 * @return the metadata
	 */
	public Object getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}
	/**
	 * @return the nbformat
	 */
	public int getNbformat() {
		return nbformat;
	}
	/**
	 * @param nbformat the nbformat to set
	 */
	public void setNbformat(int nbformat) {
		this.nbformat = nbformat;
	}
	/**
	 * @return the nbformatMinor
	 */
	public int getNbformatMinor() {
		return nbformatMinor;
	}
	/**
	 * @param nbformatMinor the nbformatMinor to set
	 */
	public void setNbformatMinor(int nbformatMinor) {
		this.nbformatMinor = nbformatMinor;
	}
	
}
