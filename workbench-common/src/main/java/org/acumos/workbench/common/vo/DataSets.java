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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DataSets implements Serializable {

	private static final long serialVersionUID = 7280763266234788570L;
	private List<DataSet> dataSets;

	
	
	/**
	 * Parameterized Constructor
	 * @param dataSets
	 */
	public DataSets(List<DataSet> dataSets) {
		super();
		this.dataSets = dataSets;
	}
	
	/**
	 * Default Constructor
	 */
	public DataSets() { 
		this(null);
	}

	/**
	 * @return the dataSets
	 */
	public List<DataSet> getDataSets() {
		return dataSets;
	}

	/**
	 * @param dataSets the dataSets to set
	 */
	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return dataSets.hashCode();
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
		if (!(obj instanceof DataSets)) {
			return false;
		}
		DataSets other = (DataSets) obj;
		if (dataSets == null) {
			if (other.dataSets != null) {
				return false;
			}
		} else if (!dataSets.equals(other.dataSets)) {
			return false;
		}
		return true;
	}
	
	
}