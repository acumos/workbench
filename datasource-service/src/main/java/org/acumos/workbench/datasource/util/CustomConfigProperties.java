/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.datasource.util;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomConfigProperties implements Serializable{
	
	private static final long serialVersionUID = -8126018792123106583L;

	@Value("${cmndatasvc.url}")
	private String cmndatasvcurl;

	@Value("${cmndatasvc.user}")
	private String cmndatasvcuser;

	@Value("${cmndatasvc.pwd}")
	private String cmndatasvcpwd;

	@Value("${resultsetSize}")
	private int resultsetSize;
	
	@Value("${jwt.secretkey}")
	private String jwtSecretKey;
	
	@Value("${couchdb.user}")
	private String couchdbUser;
	
	@Value("${couchdb.pwd}")
	private String couchdbPwd;

	@Value("${couchdb.name}")
	private String couchDbName;

	@Value("${couchdb.createdb.if-not-exist}")
	private boolean createIfnotExists;

	@Value("${couchdb.protocol}")
	private String couchdbProtocol;

	@Value("${couchdb.host}")
	private String couchdbHost;

	@Value("${couchdb.port}")
	private int couchdbPort;
	
	@Value("${sqldrivername}")
	private String sqlDriverName;
	
	@Value("${componentpropertyvalue}")
	private String componentPropertyValue;
	
	@Value("${projectservice.url}")
	private String projectServiceURL;
	

	/**
	 * @return the cmndatasvcurl
	 */
	public String getCmndatasvcurl() {
		return cmndatasvcurl;
	}

	/**
	 * @return the cmndatasvcuser
	 */
	public String getCmndatasvcuser() {
		return cmndatasvcuser;
	}

	/**
	 * @return the cmndatasvcpwd
	 */
	public String getCmndatasvcpwd() {
		return cmndatasvcpwd;
	}

	/**
	 * @return the resultsetSize
	 */
	public int getResultsetSize() {
		return resultsetSize;
	}

	/**
	 * @return the jwtSecretKey
	 */
	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

	/**
	 * @return the couchdbUser
	 */
	public String getCouchdbUser() {
		return couchdbUser;
	}

	/**
	 * @return the couchdbPwd
	 */
	public String getCouchdbPwd() {
		return couchdbPwd;
	}

	/**
	 * @return the couchDbName
	 */
	public String getCouchDbName() {
		return couchDbName;
	}

	/**
	 * @return the createIfnotExists
	 */
	public boolean isCreateIfnotExists() {
		return createIfnotExists;
	}

	/**
	 * @return the couchdbProtocol
	 */
	public String getCouchdbProtocol() {
		return couchdbProtocol;
	}

	/**
	 * @return the couchdbHost
	 */
	public String getCouchdbHost() {
		return couchdbHost;
	}

	/**
	 * @return the couchdbPort
	 */
	public int getCouchdbPort() {
		return couchdbPort;
	}

	/**
	 * @return the sqlDriverName
	 */
	public String getSqlDriverName() {
		return sqlDriverName;
	}

	/**
	 * @param sqlDriverName the sqlDriverName to set
	 */
	public void setSqlDriverName(String sqlDriverName) {
		this.sqlDriverName = sqlDriverName;
	}

	/**
	 * @return the componentPropertyValue
	 */
	public String getComponentPropertyValue() {
		return componentPropertyValue;
	}

	/**
	 * @param componentPropertyValue the componentPropertyValue to set
	 */
	public void setComponentPropertyValue(String componentPropertyValue) {
		this.componentPropertyValue = componentPropertyValue;
	}

	/**
	 * @return the projectServiceURL
	 */
	public String getProjectServiceURL() {
		return projectServiceURL;
	}

	/**
	 * @param projectServiceURL the projectServiceURL to set
	 */
	public void setProjectServiceURL(String projectServiceURL) {
		this.projectServiceURL = projectServiceURL;
	}
	
	
	
	

}
