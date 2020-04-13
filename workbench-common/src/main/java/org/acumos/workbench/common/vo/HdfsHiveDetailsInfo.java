/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T
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

public class HdfsHiveDetailsInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String kerberosLoginUser;
	protected String kerberosConfigFileId;
	protected String kerberosKeyTabFileId;
	protected String hdfsFoldername;
	protected String hdpVersion;
	protected String batchSize;
	protected String query;
	//HiveServer2 JDBC Connection URL parameters
	protected String transportMode; //binary(default), http - 
	protected String httpPath; //cliservice(default)
	
	public String getKerberosLoginUser() {
		return kerberosLoginUser;
	}
	public void setKerberosLoginUser(String kerberosLoginUser) {
		this.kerberosLoginUser = kerberosLoginUser;
	}
	public String getKerberosConfigFileId() {
		return kerberosConfigFileId;
	}
	public void setKerberosConfigFileId(String kerberosConfigFileId) {
		this.kerberosConfigFileId = kerberosConfigFileId;
	}
	public String getKerberosKeyTabFileId() {
		return kerberosKeyTabFileId;
	}
	public void setKerberosKeyTabFileId(String kerberosKeyTabFileId) {
		this.kerberosKeyTabFileId = kerberosKeyTabFileId;
	}
	public String getHdfsFoldername() {
		return hdfsFoldername;
	}
	public void setHdfsFoldername(String hdfsFoldername) {
		this.hdfsFoldername = hdfsFoldername;
	}
	public String getHdpVersion() {
		return hdpVersion;
	}
	public void setHdpVersion(String hdpVersion) {
		this.hdpVersion = hdpVersion;
	}
	public String getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTransportMode() {
		return transportMode;
	}
	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}
	public String getHttpPath() {
		return httpPath;
	}
	public void setHttpPath(String httpPath) {
		this.httpPath = httpPath;
	}
}
