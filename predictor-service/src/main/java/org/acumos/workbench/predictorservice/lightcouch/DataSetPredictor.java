package org.acumos.workbench.predictorservice.lightcouch;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DataSetPredictor implements Serializable{
	
	private static final long serialVersionUID = -3151260834226542723L;
	
	private String userId;
	private String solutionId; 
	private String revisionId; 	
	private String createdTimestamp; 			
	private String updateTimestamp; 			
	private String modelStatus;
	private String predictorDeploymentStatus; 
	private String predictorId;	
	private String predictorName; 	
	private String predictorDescription;		
	private String predictorVersion;			
	private String environmentPath;				//http://FQDN:port/<operation name>
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
		this.predictorId = predcitorId;
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
	
	

}
