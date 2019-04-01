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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Workbench implements Serializable {

	private static final long serialVersionUID = -2451498408817673318L;
	private Identifier workbenchId;
	private Projects projects;
	
	
	
	/**
	 * Parameterized Constructor
	 * @param workbenchId
	 * 		Identifier of Workbench
	 * @param projects
	 * 		Projects associated to Workbench
	 */
	public Workbench(Identifier workbenchId, Projects projects) {
		super();
		this.workbenchId = workbenchId;
		this.projects = projects;
	}

	
	/**
	 * Default Constructor
	 */
	public Workbench() { 
		super();
	}
	
	/**
	 * @return the workbenchId
	 */
	public Identifier getWorkbenchId() {
		return workbenchId;
	}

	/**
	 * @param workbenchId the workbenchId to set
	 */
	public void setWorkbenchId(Identifier workbenchId) {
		this.workbenchId = workbenchId;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				((projects == null) ? 0 : projects),
				((workbenchId == null) ? 0 : workbenchId));
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
		if (!(obj instanceof Workbench)) {
			return false;
		}
		Workbench other = (Workbench) obj;
		if (projects == null) {
			if (other.projects != null) {
				return false;
			}
		} else if (!projects.equals(other.projects)) {
			return false;
		}
		if (workbenchId == null) {
			if (other.workbenchId != null) {
				return false;
			}
		} else if (!workbenchId.equals(other.workbenchId)) {
			return false;
		}
		return true;
	}

	
}
