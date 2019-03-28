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
public class Predictor implements Serializable {

	private static final long serialVersionUID = 4244867585635899013L;
	private Identifier predictorId;
	private Environment environment;
	private User user;
	private Users users;
	private Model model;
	private DataSet trainDataset;
	private boolean hasTrainableModel;
	private Projects projects;
	private ServiceState serviceStatus;
	private ArtifactState artifactStatus;
	
	
	
	/**
	 * Parameterized Constructor
	 * @param predictorId
	 * 		Identifier of Predictor
	 * @param environment
	 * 		Environment of Predictor
	 * @param user
	 * 		User of Predictor
	 * @param users
	 * 		Users of Predictor
	 * @param model
	 * 		Model associated to Predictor
	 * @param trainDataset
	 * 		DataSet associated to Predictor
	 * @param hasTrainableModel
	 * 		Indicator indicating where model associated to a Predictor is trained or not.
	 * @param projects
	 * 		Other projects associated to Predictor
	 * @param serviceStatus
	 * 		ServiceState of Predictor
	 * @param artifactStatus
	 * 		ArtifactState of Predictor
	 */
	public Predictor(Identifier predictorId, Environment environment,
			User user, Users users, Model model, DataSet trainDataset,
			boolean hasTrainableModel, Projects projects,
			ServiceState serviceStatus, ArtifactState artifactStatus) {
		super();
		this.predictorId = predictorId;
		this.environment = environment;
		this.user = user;
		this.users = users;
		this.model = model;
		this.trainDataset = trainDataset;
		this.hasTrainableModel = hasTrainableModel;
		this.projects = projects;
		this.serviceStatus = serviceStatus;
		this.artifactStatus = artifactStatus;
	}
	
	public Predictor() {
		super();
	}

	/**
	 * @return the predictorId
	 */
	public Identifier getPredictorId() {
		return predictorId;
	}

	/**
	 * @param predictorId the predictorId to set
	 */
	public void setPredictorId(Identifier predictorId) {
		this.predictorId = predictorId;
	}

	/**
	 * @return the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
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
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the trainDataset
	 */
	public DataSet getTrainDataset() {
		return trainDataset;
	}

	/**
	 * @param trainDataset the trainDataset to set
	 */
	public void setTrainDataset(DataSet trainDataset) {
		this.trainDataset = trainDataset;
	}

	/**
	 * @return the hasTrainableModel
	 */
	public boolean isHasTrainableModel() {
		return hasTrainableModel;
	}

	/**
	 * @param hasTrainableModel the hasTrainableModel to set
	 */
	public void setHasTrainableModel(boolean hasTrainableModel) {
		this.hasTrainableModel = hasTrainableModel;
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
		return Objects.hash(
				((artifactStatus == null) ? 0 : artifactStatus),
				((environment == null) ? 0 : environment),
				((model == null) ? 0 : model),
				((predictorId == null) ? 0 : predictorId),
				((projects == null) ? 0 : projects),
				((serviceStatus == null) ? 0 : serviceStatus),
				((trainDataset == null) ? 0 : trainDataset),
				((user == null) ? 0 : user), 
				((users == null) ? 0 : users));
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
		if (!(obj instanceof Predictor)) {
			return false;
		}
		Predictor other = (Predictor) obj;
		if (artifactStatus == null) {
			if (other.artifactStatus != null) {
				return false;
			}
		} else if (!artifactStatus.equals(other.artifactStatus)) {
			return false;
		}
		if (environment != other.environment) {
			return false;
		}
		if (hasTrainableModel != other.hasTrainableModel) {
			return false;
		}
		if (model == null) {
			if (other.model != null) {
				return false;
			}
		} else if (!model.equals(other.model)) {
			return false;
		}
		if (predictorId == null) {
			if (other.predictorId != null) {
				return false;
			}
		} else if (!predictorId.equals(other.predictorId)) {
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
		if (trainDataset == null) {
			if (other.trainDataset != null) {
				return false;
			}
		} else if (!trainDataset.equals(other.trainDataset)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		if (users == null) {
			if (other.users != null) {
				return false;
			}
		} else if (!users.equals(other.users)) {
			return false;
		}
		return true;
	}

	
}
