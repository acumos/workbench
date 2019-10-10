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

package org.acumos.workbench.predictorservice.lightcouch;

import java.io.Serializable;

public class PredictorProjectAssociation implements Serializable{
	
	private static final long serialVersionUID = -45691552444699060L;
	
	private String _id;  						//(Auto) UUID	Project to predictor association this is UUID
	private String _rev;						// revisionId
	private String userId; 						//User, who created project mapping. This ID will be pointing to Maria DB and use API for additional details
	private String projectId;					//ProjectId from Maria DB and use API for additional details… we can duplicate data later
	private String solutionId; 					//SolutionId from Maria DB and use API for additional details… we can duplicate data later
	private String version;						// Solution Version
	private String revisionId; 					//RevisionId from Maria DB and use API for additional details… we can duplicate data later
	private String createdTimestamp; 			//Creation time stamp
	private String updateTimestamp; 			//Modification time stamp
	private String associationStatus;			// PredictorProjectAssociationStatus ()
	private String predictorDeploymentStatus; 	//active/error/inprogress (green/red/yellow) ???
	private String predictorId;					//UUID auto generated
	private String predictorName; 				//(Key)Modelname_<service name >_<env (dev/prod)> [recommended naming format]
	private String predictorDescription;		//Give some desc on what this predictor does
	private String predictorVersion;			//Auto Generated (1,2,3…)
	private String environmentPath;				//http://FQDN:port/<operation name>
	private String metadata1;					//Additional data as needed
	private String metadata2;					//Additional data as needed
	private String predictorkey;				//Predictor Key given by UI
	
	
	/**
	 * @return the associationID
	 */
	public String getAssociationID() {
		return _id;
	}
	/**
	 * @param associationID the associationID to set
	 */
	public void setAssociationID(String associationID) {
		_id = associationID;
	}
	
	/**
	 * @return the _rev
	 */
	public String get_rev() {
		return _rev;
	}
	/**
	 * @param _rev the _rev to set
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the solutionId
	 */
	public String getSolutionId() {
		return solutionId;
	}
	/**
	 * @param solutionId the solutionId to set
	 */
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	/**
	 * @return the revisionId
	 */
	public String getRevisionId() {
		return revisionId;
	}
	/**
	 * @param revisionId the revisionId to set
	 */
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	/**
	 * @return the createdTimestamp
	 */
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}
	/**
	 * @param createdTimestamp the createdTimestamp to set
	 */
	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	/**
	 * @return the updateTimestamp
	 */
	public String getUpdateTimestamp() {
		return updateTimestamp;
	}
	/**
	 * @param updateTimestamp the updateTimestamp to set
	 */
	public void setUpdateTimestamp(String updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	
	
	/**
	 * @return the associationStatus
	 */
	public String getAssociationStatus() {
		return associationStatus;
	}
	/**
	 * @param associationStatus the associationStatus to set
	 */
	public void setAssociationStatus(String associationStatus) {
		this.associationStatus = associationStatus;
	}
	/**
	 * @return the predictorDeploymentStatus
	 */
	public String getPredictorDeploymentStatus() {
		return predictorDeploymentStatus;
	}
	/**
	 * @param predictorDeploymentStatus the predictorDeploymentStatus to set
	 */
	public void setPredictorDeploymentStatus(String predictorDeploymentStatus) {
		this.predictorDeploymentStatus = predictorDeploymentStatus;
	}
	/**
	 * @return the predictorName
	 */
	public String getPredictorName() {
		return predictorName;
	}
	/**
	 * @param predictorName the predictorName to set
	 */
	public void setPredictorName(String predictorName) {
		this.predictorName = predictorName;
	}
	/**
	 * @return the predictorDescription
	 */
	public String getPredictorDescription() {
		return predictorDescription;
	}
	/**
	 * @param predictorDescription the predictorDescription to set
	 */
	public void setPredictorDescription(String predictorDescription) {
		this.predictorDescription = predictorDescription;
	}
	/**
	 * @return the predictorVersion
	 */
	public String getPredictorVersion() {
		return predictorVersion;
	}
	/**
	 * @param predictorVersion the predictorVersion to set
	 */
	public void setPredictorVersion(String predictorVersion) {
		this.predictorVersion = predictorVersion;
	}
	/**
	 * @return the environmentPath
	 */
	public String getEnvironmentPath() {
		return environmentPath;
	}
	/**
	 * @param environmentPath the environmentPath to set
	 */
	public void setEnvironmentPath(String environmentPath) {
		this.environmentPath = environmentPath;
	}
	/**
	 * @return the predictorkey
	 */
	public String getPredictorkey() {
		return predictorkey;
	}
	/**
	 * @param predictorkey the predictorkey to set
	 */
	public void setPredictorkey(String predictorkey) {
		this.predictorkey = predictorkey;
	}
	/**
	 * @return the metadata1
	 */
	public String getMetadata1() {
		return metadata1;
	}
	/**
	 * @param metadata1 the metadata1 to set
	 */
	public void setMetadata1(String metadata1) {
		this.metadata1 = metadata1;
	}
	/**
	 * @return the metadata2
	 */
	public String getMetadata2() {
		return metadata2;
	}
	/**
	 * @param metadata2 the metadata2 to set
	 */
	public void setMetadata2(String metadata2) {
		this.metadata2 = metadata2;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the predictorId
	 */
	public String getPredictorId() {
		return predictorId;
	}
	/**
	 * @param predictorId the predictorId to set
	 */
	public void setPredictorId(String predictorId) {
		this.predictorId = predictorId;
	}
	
	
	
	
	
	
}
