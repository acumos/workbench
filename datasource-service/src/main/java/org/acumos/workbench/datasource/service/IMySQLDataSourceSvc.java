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

package org.acumos.workbench.datasource.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.model.MySQLConnectorModel;

public interface IMySQLDataSourceSvc {

	/**
	 * To get the MySQL Connection status as success/failure 
	 * @param MysqlConnectorModel
	 * 			The MySQLConnectorModel model object
	 * @param query
	 * 			The MySQL query
	 * @return
	 * 			returns the connection status as success/failure 
	 * @throws ClassNotFoundException
	 * 			throws ClassNotFoundException in case of any failure
	 * @throws SQLException
	 * 			throws SQLException in case of any failure
	 * @throws IOException
	 * 			throws IOException in case of any failure
	 * @throws DataSourceNotFoundException
	 * 			throws DataSourceException in case of any failure
	 */
	public String getConnectionStatus(MySQLConnectorModel MysqlConnectorModel, String query) throws ClassNotFoundException, SQLException, IOException, DataSourceNotFoundException;
	
	
	/**
	 * To get the MqSQL connection Object
	 * @param server
	 * 		the input server
	 * @param port
	 * 		the input port
	 * @param dbName
	 * 		the database name
	 * @param username
	 * 		database username
	 * @param password
	 * 		database password
	 * @return
	 * 		returns connection object
	 * @throws ClassNotFoundException
	 * 		throws ClassNotFoundException in case of any failure
	 * @throws IOException
	 * 		throws IOException in case of any failure
	 * @throws SQLException
	 * 		throws SQLException in case of any failure
	 * @throws DataSourceNotFoundException
	 * 		throws DataSourceException in case of any failure
	 */
	public Connection getConnection(String server, String port, String dbName, String username, String password) throws ClassNotFoundException, IOException, SQLException, DataSourceNotFoundException;
}
