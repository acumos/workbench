package org.acumos.workbench.modelservice.lightcouch;

import java.io.Serializable;

public class DataSetModel implements Serializable{
	
	
	private static final long serialVersionUID = 522655779811182406L;
	
	private String _id;//AssociateId will be auto generated  
	private String _rev;// Auto generated CouchDb documents revisionId(_rev)
	private String UserId;//AcumosLoginId
	private String ProjectId;
	private String SolutionId;
	private String RevisionId;
	private String CreatedTimestamp;
	private String UpdateTimestamp;
	private String status;// (Active/Invalid/Deleted)
	private String modelType;// – once created does not change
	private String CatalogId;
	private String CatalogName;

	public String getAssociationID() {
		return _id;
	}

	public void setAssociationID(String associationID) {
		_id = associationID;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	
	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getProjectId() {
		return ProjectId;
	}

	public void setProjectId(String projectId) {
		ProjectId = projectId;
	}

	public String getSolutionId() {
		return SolutionId;
	}

	public void setSolutionId(String solutionId) {
		SolutionId = solutionId;
	}

	public String getRevisionId() {
		return RevisionId;
	}

	public void setRevisionId(String revisionId) {
		RevisionId = revisionId;
	}

	public String getCreatedTimestamp() {
		return CreatedTimestamp;
	}

	public void setCreatedTimestamp(String createdTimestamp) {
		CreatedTimestamp = createdTimestamp;
	}

	public String getUpdateTimestamp() {
		return UpdateTimestamp;
	}

	public void setUpdateTimestamp(String updateTimestamp) {
		UpdateTimestamp = updateTimestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getCatalogId() {
		return CatalogId;
	}

	public void setCatalogId(String catalogId) {
		CatalogId = catalogId;
	}

	public String getCatalogName() {
		return CatalogName;
	}

	public void setCatalogName(String catalogName) {
		CatalogName = catalogName;
	}

}
