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
public class Notebook implements Serializable {

	private static final long serialVersionUID = 8750101370417986166L;
	private Identifier noteBookId;
	private String description;
	private String notebookType;
	private String kernelType;
	private User owner;
	private Users collaborators;
	private DataSets dataSets;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	
	/**
	 * Parameterized Constructor
	 * 
	 * @param noteBookId
	 * 		The Identifier for Notebook 
	 * @param description
	 * 		Description of the Notebook 
	 * @param notebookType
	 * 		Type of Note book. Either "Jupyter" or "Zeppelin"
	 * @param kernelType
	 * 		The Kernel Type of Notebook
	 * @param owner
	 * 		The user as owner of Notebook
	 * @param collaborators
	 * 		The list of Users as Collaborator of Notebook
	 * @param dataSets
	 * 		The dataSet of Notebook 
	 * @param projects
	 * 		The Projects associated to a Notebook.
	 * @param serviceStatus
	 * 		The serviceState of a Notebook.
	 * @param artifactStatus
	 * 		The ArtifactState of Notebook
	 */
	public Notebook(Identifier noteBookId, String description,
			String notebookType, String kernelType, User owner,
			Users collaborators, DataSets dataSets, Projects projects,
			ServiceState serviceStatus, ArtifactState artifactStatus) {
		super();
		this.noteBookId = noteBookId;
		this.description = description;
		this.notebookType = notebookType;
		this.kernelType = kernelType;
		this.owner = owner;
		this.collaborators = collaborators;
		this.dataSets = dataSets;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}
	
	public Notebook() { 
		this(null, null, null, null, null, null, null, null, null, null);
	}

	/**
	 * @return the noteBookId
	 */
	public Identifier getNoteBookId() {
		return noteBookId;
	}

	/**
	 * @param noteBookId the noteBookId to set
	 */
	public void setNoteBookId(Identifier noteBookId) {
		this.noteBookId = noteBookId;
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
	 * @return the notebookType
	 */
	public String getNotebookType() {
		return notebookType;
	}

	/**
	 * @param notebookType the notebookType to set
	 */
	public void setNotebookType(String notebookType) {
		this.notebookType = notebookType;
	}

	/**
	 * @return the kernelType
	 */
	public String getKernelType() {
		return kernelType;
	}

	/**
	 * @param kernelType the kernelType to set
	 */
	public void setKernelType(String kernelType) {
		this.kernelType = kernelType;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		return Objects.hash(
				((artifactStatus == null) ? 0 : artifactStatus),
				((collaborators == null) ? 0 : collaborators),
				((dataSets == null) ? 0 : dataSets),
				((description == null) ? 0 : description),
				((kernelType == null) ? 0 : kernelType),
				((noteBookId == null) ? 0 : noteBookId),
				((notebookType == null) ? 0 : notebookType),
				((owner == null) ? 0 : owner),
				((projects == null) ? 0 : projects),
				((serviceStatus == null) ? 0 : serviceStatus));
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
		if (!(obj instanceof Notebook)) {
			return false;
		}
		Notebook other = (Notebook) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (collaborators == null) {
			if (other.collaborators != null) {
				return false;
			}
		} else if (!collaborators.equals(other.collaborators)) {
			return false;
		}
		if (dataSets == null) {
			if (other.dataSets != null) {
				return false;
			}
		} else if (!dataSets.equals(other.dataSets)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (kernelType == null) {
			if (other.kernelType != null) {
				return false;
			}
		} else if (!kernelType.equals(other.kernelType)) {
			return false;
		}
		if (noteBookId == null) {
			if (other.noteBookId != null) {
				return false;
			}
		} else if (!noteBookId.equals(other.noteBookId)) {
			return false;
		}
		if (notebookType == null) {
			if (other.notebookType != null) {
				return false;
			}
		} else if (!notebookType.equals(other.notebookType)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		if (projects == null) {
			if (other.projects != null) {
				return false;
			}
		} else if (!projects.equals(other.projects)) {
			return false;
		}
		if (serviceStatus == null) {
			if (other.serviceStatus != null) {
				return false;
			}
		} else if (!serviceStatus.equals(other.serviceStatus)) {
			return false;
		}
		return true;
	}

	
}
