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
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DataSource implements Serializable {

	private static final long serialVersionUID = 6401829205426213874L;
	private String _id;
	private String _rev;
	private Identifier datasourceId;
	private String category;
	private String namespace;
	private String datasourceDescription;
	private String readWriteDescriptor;
	private String isDataReference;
	private boolean isActive;
	private ArrayList<NameValue> serviceMetaData; // For Future Purpose
	private ArrayList<NameValue> customMetaData;
	private DataSourceMetadata metaData;
	// hdfs/hive
	private HdfsHiveDetailsInfo hdfsHiveDetails;
	// common details info
	private CommonDetailsInfo commonDetails;
	// fileDetails
	private FileDetailsInfo fileDetails;
	// DBDetails
	private DBDetailsInfo dbDetails;
	private User owner;
	private Users users;
	private KVPairs datasourceConfiguration;
	private Projects projects;

	/**
	 * Parameterized Constructor
	 * 
	 * @param datasourceId            the datasourceId
	 * @param owner                   the owner object
	 * @param users                   the Users object
	 * @param datasourceConfiguration the KVPairs object
	 * @param projects                the Projects object
	 */
	public DataSource(Identifier datasourceId, User owner, Users users, KVPairs datasourceConfiguration,
			Projects projects) {
		super();
		this.datasourceId = datasourceId;
		this.owner = owner;
		this.users = users;
		this.datasourceConfiguration = datasourceConfiguration;
		this.projects = projects;
	}

	/**
	 *  Parameterized Constructor
	 *  
	 * @param _id
	 * 		The documentId
	 * @param _rev
	 * 		The Document Revision Id
	 * @param datasourceId
	 * 		The DataSource Id
	 * @param category
	 * 		The DataSource Category
	 * @param namespace
	 * 		The DataSource Namespace
	 * @param datasourceDescription
	 * 		The DataSource Description
	 * @param readWriteDescriptor
	 * 		Data Source permission
	 * @param isDataReference
	 * 		The Data Source data reference
	 * @param isActive
	 * 		Is active DataSource
	 * @param serviceMetaData
	 * 		The DataSource service metadata
	 * @param customMetaData
	 * 		The DataSource Custom Metadata
	 * @param metaData
	 * 		The DataSource Metadata
	 * @param hdfsHiveDetails
	 * 		The DataSource HDFS Hive Details
	 * @param commonDetails
	 * 		The DataSource Common details like hostname,port,env..
	 * @param fileDetails
	 * 		The DataSource File Details
	 * @param dbDetails
	 * 		The DataSource DB Details
	 * @param owner
	 * 		The DataSource Owner Details
	 * @param users
	 * 		The DataSource owner Details
	 * @param datasourceConfiguration
	 * 		The DataSource Configuration Details
	 * @param projects
	 * 		The DataSource Projects
	 */
	public DataSource(String _id, String _rev, Identifier datasourceId, String category, String namespace,
			String datasourceDescription, String readWriteDescriptor, String isDataReference, boolean isActive,
			ArrayList<NameValue> serviceMetaData, ArrayList<NameValue> customMetaData, DataSourceMetadata metaData,
			HdfsHiveDetailsInfo hdfsHiveDetails, CommonDetailsInfo commonDetails, FileDetailsInfo fileDetails,
			DBDetailsInfo dbDetails, User owner, Users users, KVPairs datasourceConfiguration, Projects projects) {
		super();
		this._id = _id;
		this._rev = _rev;
		this.datasourceId = datasourceId;
		this.category = category;
		this.namespace = namespace;
		this.datasourceDescription = datasourceDescription;
		this.readWriteDescriptor = readWriteDescriptor;
		this.isDataReference = isDataReference;
		this.isActive = isActive;
		this.serviceMetaData = serviceMetaData;
		this.customMetaData = customMetaData;
		this.metaData = metaData;
		this.hdfsHiveDetails = hdfsHiveDetails;
		this.commonDetails = commonDetails;
		this.fileDetails = fileDetails;
		this.dbDetails = dbDetails;
		this.owner = owner;
		this.users = users;
		this.datasourceConfiguration = datasourceConfiguration;
		this.projects = projects;
	}

	public DataSource() {
		super();
	}

	/**
	 * @return the datasourceId
	 */
	public Identifier getDatasourceId() {
		return datasourceId;
	}

	/**
	 * @param datasourceId the datasourceId to set
	 */
	public void setDatasourceId(Identifier datasourceId) {
		this.datasourceId = datasourceId;
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
	 * @return the datasourceConfiguration
	 */
	public KVPairs getDatasourceConfiguration() {
		return datasourceConfiguration;
	}

	/**
	 * @param datasourceConfiguration the datasourceConfiguration to set
	 */
	public void setDatasourceConfiguration(KVPairs datasourceConfiguration) {
		this.datasourceConfiguration = datasourceConfiguration;
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
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
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
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
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
	 * @return the datasourceDescription
	 */
	public String getDatasourceDescription() {
		return datasourceDescription;
	}

	/**
	 * @param datasourceDescription the datasourceDescription to set
	 */
	public void setDatasourceDescription(String datasourceDescription) {
		this.datasourceDescription = datasourceDescription;
	}

	/**
	 * @return the readWriteDescriptor
	 */
	public String getReadWriteDescriptor() {
		return readWriteDescriptor;
	}

	/**
	 * @param readWriteDescriptor the readWriteDescriptor to set
	 */
	public void setReadWriteDescriptor(String readWriteDescriptor) {
		this.readWriteDescriptor = readWriteDescriptor;
	}

	/**
	 * @return the isDataReference
	 */
	public String getIsDataReference() {
		return isDataReference;
	}

	/**
	 * @param isDataReference the isDataReference to set
	 */
	public void setIsDataReference(String isDataReference) {
		this.isDataReference = isDataReference;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the serviceMetaData
	 */
	public ArrayList<NameValue> getServiceMetaData() {
		return serviceMetaData;
	}

	/**
	 * @param serviceMetaData the serviceMetaData to set
	 */
	public void setServiceMetaData(ArrayList<NameValue> serviceMetaData) {
		this.serviceMetaData = serviceMetaData;
	}

	/**
	 * @return the customMetaData
	 */
	public ArrayList<NameValue> getCustomMetaData() {
		return customMetaData;
	}

	/**
	 * @param customMetaData the customMetaData to set
	 */
	public void setCustomMetaData(ArrayList<NameValue> customMetaData) {
		this.customMetaData = customMetaData;
	}

	/**
	 * @return the metaData
	 */
	public DataSourceMetadata getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(DataSourceMetadata metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the hdfsHiveDetails
	 */
	public HdfsHiveDetailsInfo getHdfsHiveDetails() {
		return hdfsHiveDetails;
	}

	/**
	 * @param hdfsHiveDetails the hdfsHiveDetails to set
	 */
	public void setHdfsHiveDetails(HdfsHiveDetailsInfo hdfsHiveDetails) {
		this.hdfsHiveDetails = hdfsHiveDetails;
	}

	/**
	 * @return the commonDetails
	 */
	public CommonDetailsInfo getCommonDetails() {
		return commonDetails;
	}

	/**
	 * @param commonDetails the commonDetails to set
	 */
	public void setCommonDetails(CommonDetailsInfo commonDetails) {
		this.commonDetails = commonDetails;
	}

	/**
	 * @return the fileDetails
	 */
	public FileDetailsInfo getFileDetails() {
		return fileDetails;
	}

	/**
	 * @param fileDetails the fileDetails to set
	 */
	public void setFileDetails(FileDetailsInfo fileDetails) {
		this.fileDetails = fileDetails;
	}

	/**
	 * @return the dbDetails
	 */
	public DBDetailsInfo getDbDetails() {
		return dbDetails;
	}

	/**
	 * @param dbDetails the dbDetails to set
	 */
	public void setDbDetails(DBDetailsInfo dbDetails) {
		this.dbDetails = dbDetails;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((_rev == null) ? 0 : _rev.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((commonDetails == null) ? 0 : commonDetails.hashCode());
		result = prime * result + ((customMetaData == null) ? 0 : customMetaData.hashCode());
		result = prime * result + ((datasourceConfiguration == null) ? 0 : datasourceConfiguration.hashCode());
		result = prime * result + ((datasourceDescription == null) ? 0 : datasourceDescription.hashCode());
		result = prime * result + ((datasourceId == null) ? 0 : datasourceId.hashCode());
		result = prime * result + ((dbDetails == null) ? 0 : dbDetails.hashCode());
		result = prime * result + ((fileDetails == null) ? 0 : fileDetails.hashCode());
		result = prime * result + ((hdfsHiveDetails == null) ? 0 : hdfsHiveDetails.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + ((isDataReference == null) ? 0 : isDataReference.hashCode());
		result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((projects == null) ? 0 : projects.hashCode());
		result = prime * result + ((readWriteDescriptor == null) ? 0 : readWriteDescriptor.hashCode());
		result = prime * result + ((serviceMetaData == null) ? 0 : serviceMetaData.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSource other = (DataSource) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (_rev == null) {
			if (other._rev != null)
				return false;
		} else if (!_rev.equals(other._rev))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (commonDetails == null) {
			if (other.commonDetails != null)
				return false;
		} else if (!commonDetails.equals(other.commonDetails))
			return false;
		if (customMetaData == null) {
			if (other.customMetaData != null)
				return false;
		} else if (!customMetaData.equals(other.customMetaData))
			return false;
		if (datasourceConfiguration == null) {
			if (other.datasourceConfiguration != null)
				return false;
		} else if (!datasourceConfiguration.equals(other.datasourceConfiguration))
			return false;
		if (datasourceDescription == null) {
			if (other.datasourceDescription != null)
				return false;
		} else if (!datasourceDescription.equals(other.datasourceDescription))
			return false;
		if (datasourceId == null) {
			if (other.datasourceId != null)
				return false;
		} else if (!datasourceId.equals(other.datasourceId))
			return false;
		if (dbDetails == null) {
			if (other.dbDetails != null)
				return false;
		} else if (!dbDetails.equals(other.dbDetails))
			return false;
		if (fileDetails == null) {
			if (other.fileDetails != null)
				return false;
		} else if (!fileDetails.equals(other.fileDetails))
			return false;
		if (hdfsHiveDetails == null) {
			if (other.hdfsHiveDetails != null)
				return false;
		} else if (!hdfsHiveDetails.equals(other.hdfsHiveDetails))
			return false;
		if (isActive != other.isActive)
			return false;
		if (isDataReference == null) {
			if (other.isDataReference != null)
				return false;
		} else if (!isDataReference.equals(other.isDataReference))
			return false;
		if (metaData == null) {
			if (other.metaData != null)
				return false;
		} else if (!metaData.equals(other.metaData))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (projects == null) {
			if (other.projects != null)
				return false;
		} else if (!projects.equals(other.projects))
			return false;
		if (readWriteDescriptor == null) {
			if (other.readWriteDescriptor != null)
				return false;
		} else if (!readWriteDescriptor.equals(other.readWriteDescriptor))
			return false;
		if (serviceMetaData == null) {
			if (other.serviceMetaData != null)
				return false;
		} else if (!serviceMetaData.equals(other.serviceMetaData))
			return false;
		if (users == null) {
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		return true;
	}

}