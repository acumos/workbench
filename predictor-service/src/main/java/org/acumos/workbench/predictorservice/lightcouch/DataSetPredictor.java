package org.acumos.workbench.predictorservice.lightcouch;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DataSetPredictor implements Serializable {

	private static final long serialVersionUID = -3151260834226542723L;

	private String _id;
	private String _rev;
	private String userId;          
	private String projectId;
	private String associationStatus; // PredictorProjectAssociationStatus ()
	private String associationId;	// PredictorProjectAssociationId
	private String predictorId; 	// UUID auto generated
	private String modelVersion; 	// Solution Version
	private String solutionId;		// Model ID
	private String revisionId;		// Revision Id (Model)
	private String predictorName;	
	private String predictorVersion;
	private String predictorDescription;
	private String environmentPath; // JenkinsURL and DeploymentURL
	private String signature; // (this field will contain protobuf details)
	private String k8sId;		// Cluster	
	private String predictorDeploymentStatus;
	private String createdTimestamp;
	private String updateTimestamp;
	private String modelStatus; 	// Model status (ACTIVE/FAILED)
	private String predictorkey; // Predictor Key given by UI
	private String deploymentId; // deploymentId 
	private String associationUpdateTimestamp;
	private String associationCreatedTimestamp;
	private String metadata1;
	private String metadata2;
	

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
	 * @return the modelStatus
	 */
	public String getModelStatus() {
		return modelStatus;
	}

	/**
	 * @param modelStatus the modelStatus to set
	 */
	public void setModelStatus(String modelStatus) {
		this.modelStatus = modelStatus;
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
	 * @return the predcitorId
	 */
	public String getPredcitorId() {
		return predictorId;
	}

	/**
	 * @param predcitorId the predcitorId to set
	 */
	public void setPredcitorId(String predcitorId) {
		this.predictorId= predcitorId;
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
	 * 
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * 
	 * @param signature the signature
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * 
	 * @return the k8sId
	 */
	public String getK8sId() {
		return k8sId;
	}

	/**
	 * 
	 * @param k8sId the k8sId
	 */
	public void setK8sId(String k8sId) {
		this.k8sId = k8sId;
	}

	/**
	 * 
	 * @return the deploymentId
	 */
	public String getDeploymentId() {
		return deploymentId;
	}

	/**
	 * 
	 * @param deploymentId the deploymentId
	 */
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	/**
	 * 
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * 
	 * @param projectId the projectId
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * 
	 * @return the associationStatus
	 */
	public String getAssociationStatus() {
		return associationStatus;
	}

	/**
	 * 
	 * @param associationStatus the associationStatus
	 */
	public void setAssociationStatus(String associationStatus) {
		this.associationStatus = associationStatus;
	}

	/**
	 * 
	 * @return the modelVersion
	 */
	public String getModelVersion() {
		return modelVersion;
	}

	/**
	 * 
	 * @param version the modelVersion
	 */
	public void setModelVersion(String version) {
		this.modelVersion = version;
	}

	/**
	 * 
	 * @return the associationId
	 */
	public String getAssociationId() {
		return associationId;
	}

	/**
	 * 
	 * @param associationId the associationId
	 */
	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	/**
	 * 
	 * @return the_id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * 
	 * @param _id
	 */
	public void set_id(String _id) {
		this._id = _id;
	}
	/**
	 * 
	 * @return
	 * 		the associationUpdateTimestamp
	 */
	public String getAssociationUpdateTimestamp() {
		return associationUpdateTimestamp;
	}

	/**
	 * 
	 * @param associationUpdateTimestamp the associationUpdateTimestamp
	 */
	public void setAssociationUpdateTimestamp(String associationUpdateTimestamp) {
		this.associationUpdateTimestamp = associationUpdateTimestamp;
	}

	/**
	 * 
	 * @return the getAssociationCreatedTimestamp
	 */
	public String getAssociationCreatedTimestamp() {
		return associationCreatedTimestamp;
	}

	/**
	 * 
	 * @param associationCreatedTimestamp the associationCreatedTimestamp
	 */
	public void setAssociationCreatedTimestamp(String associationCreatedTimestamp) {
		this.associationCreatedTimestamp = associationCreatedTimestamp;
	}

}
