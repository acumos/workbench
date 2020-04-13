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

public class DBDetailsInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String databaseName;
	protected String dbServerUsername;
	protected String dbServerPassword;
	protected String jdbcURL;
	protected String dbQuery; //contains query statement for read datasource or insert statement for write
	private String dbCollectionName;
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDbServerUsername() {
		return dbServerUsername;
	}
	public void setDbServerUsername(String dbServerUsername) {
		this.dbServerUsername = dbServerUsername;
	}
	public String getDbServerPassword() {
		return dbServerPassword;
	}
	public void setDbServerPassword(String dbServerPassword) {
		this.dbServerPassword = dbServerPassword;
	}
	public String getJdbcURL() {
		return jdbcURL;
	}
	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}
	public String getDbQuery() {
		return dbQuery;
	}
	public void setDbQuery(String query) {
		this.dbQuery = query;
	}
	public String getDbCollectionName() {
		return dbCollectionName;
	}
	public void setDbCollectionName(String collectionName) {
		this.dbCollectionName = collectionName;
	}
}
