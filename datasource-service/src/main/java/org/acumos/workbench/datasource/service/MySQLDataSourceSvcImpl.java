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
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import javax.ws.rs.core.Response.Status;

import org.acumos.workbench.common.vo.ColumnMetadataInfo;
import org.acumos.workbench.common.vo.DataSourceMetadata;
import org.acumos.workbench.common.vo.NameValue;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.model.MySQLConnectorModel;
import org.acumos.workbench.datasource.util.CustomConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MySQLDataSourceSvcImpl")
public class MySQLDataSourceSvcImpl implements IMySQLDataSourceSvc{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	@Autowired
	private CustomConfigProperties customconfigProps;
	

	@Override
	public String getConnectionStatus(MySQLConnectorModel mySqlConnectorModel, String query)
			throws ClassNotFoundException, SQLException, IOException, DataSourceNotFoundException {
		logger.debug(" getConnectionStatus() Begin");
		logger.debug(" getConnectionStatus(), checking required parameters for identifying null values ");
		
		if (mySqlConnectorModel.getHostname() != null && mySqlConnectorModel.getPort() != null
				&& mySqlConnectorModel.getDbName() != null && mySqlConnectorModel.getUserName() != null
				&& mySqlConnectorModel.getPassword() != null) {
			
			Connection connection = getConnection(mySqlConnectorModel.getHostname(), mySqlConnectorModel.getPort(),
					mySqlConnectorModel.getDbName(), mySqlConnectorModel.getUserName(),
					mySqlConnectorModel.getPassword());
			if(query != null) {
				logger.debug(" The SQL Query is " + query );
				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet results = null;
				results = statement.executeQuery(query);
				
				if (results != null) {
					//mySqlConnectorModel.setMetaData(getMySqlMetadata(results));
					connection.close();
					return "success";
				} else {
					connection.close();
					return "failed";
				} // end if
			} else if (connection != null) {
				connection.close();
				return "success";
			}
				
		}
		logger.debug(" getConnectionStatus() End");
		return "failed";
	}

	

	@Override
	public Connection getConnection(String server, String port, String dbName, String username, String password)
			throws ClassNotFoundException, IOException, SQLException, DataSourceNotFoundException {
		// registering driver
		logger.debug(" getConnection() Begin");
		Class.forName(customconfigProps.getSqlDriverName());
		Connection connection = null;

		// preparing URL
		String dbUrl = "jdbc:mysql://" + server + ":" + port + "/" + dbName;
		// jdbc:mysql://127.0.0.1:3306/AcumosDB
		logger.debug("connection url for mysql: " + dbUrl);
		connection = DriverManager.getConnection(dbUrl, username, password);
		logger.debug(" getConnection() End");
		return connection;
	}
	
	public long calculateJdbcResultSizeByNotSampling(ResultSet results) throws SQLException, DataSourceNotFoundException {
		long size = 0L;
		logger.debug("calculateJdbcResultSizeByNotSampling() Begin");

		ResultSetMetaData rsmd = results.getMetaData();
		int columnCount = rsmd.getColumnCount();
		// countRow
		int rowCount = getJdbcRowCount(results);
		logger.debug("Number of Records: " + rowCount);
		
		StringBuilder resultString = new StringBuilder();
		int rowNumber = 1;

		while (results.next()) {
			for (int i = 1; i <= columnCount; i++) {
				if (i > 1) {
					resultString.append(",");
				}
				int type = rsmd.getColumnType(i);
				if (type == Types.VARCHAR || type == Types.CHAR || type == Types.LONGNVARCHAR || type == Types.LONGVARCHAR) {
					resultString.append(results.getString(i));
				} else if (type == Types.BIT) {
					resultString.append(String.valueOf(results.getBoolean(i)));
				} else if (type == Types.BIGINT) {
					resultString.append(String.valueOf(results.getLong(i)));
				} else if (type == Types.NUMERIC || type == Types.DECIMAL) {
					resultString.append(String.valueOf(results.getBigDecimal(i)));
				} else if (type == Types.TINYINT || type == Types.SMALLINT || type == Types.INTEGER) {
					resultString.append(String.valueOf(results.getInt(i)));
				} else if (type == Types.REAL) {
					resultString.append(String.valueOf(results.getFloat(i)));
				} else if (type == Types.FLOAT || type == Types.DOUBLE) {
					resultString.append(String.valueOf(results.getDouble(i)));
				} else if (type == Types.BINARY || type == Types.LONGVARBINARY || type == Types.VARBINARY) {
					resultString.append(String.valueOf(results.getByte(i)));
				} else if (type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP) {
					resultString.append(String.valueOf(results.getTime(i)));
				} else if (type == Types.BLOB || type == Types.CLOB || type == Types.NCLOB) {
					return -1L;
				} else {
					return -1L;
				}
			}
			resultString.append("\r\n");
			rowNumber++;
		}

		logger.debug("Past Last Record: " + rowNumber);
		size = resultString.toString().getBytes().length * 1L;
		logger.debug("\nSize of 100% dataset: " + size + " bytes");
		logger.debug("calculateJdbcResultSizeByNotSampling() End");
		return size;
	}
	
	public long calculateJdbcResultSizeBySampling(ResultSet results) throws SQLException, DataSourceNotFoundException {
		logger.debug("calculateJdbcResultSizeBySampling() Begin");

		ResultSetMetaData rsmd = results.getMetaData();
		int columnCount = rsmd.getColumnCount();

		// countRow
		int rowCount = getJdbcRowCount(results);
		
		int sampleSize = (int) Math.ceil(rowCount / 5.0);
		int batchSize = (int) Math.ceil(sampleSize / 3.0);
		int middle = (int) Math.ceil(rowCount / 2.0);

		logger.debug("Number of Records: " + rowCount);
		logger.debug("SamplingSize: " + sampleSize);
		logger.debug("BatchSize: " + batchSize);
		logger.debug("Middle rowNumber: " + middle);


		StringBuilder resultString = new StringBuilder();
		int rowNumber = 1;
		while (results.next()) {
			if (rowNumber == 1 + batchSize) {
				// System.out.println("rowNumber first batch END: " + rowNumber);
				results.absolute(middle);
				rowNumber = middle;
				logger.debug("\nMOVE CURSOR TO MIDDLE BATCH: " + rowNumber);
			}
			if (rowNumber == middle + batchSize) {
				// System.out.println("rowNumber middle batch END: " + rowNumber);
				results.absolute(rowCount - batchSize + 1);
				rowNumber = rowCount - batchSize + 1;
				logger.debug("MOVE CURSOR TO LAST BATCH: " + rowNumber);
			}

			if ((rowNumber >= 1 && rowNumber < 1 + batchSize) || (rowNumber >= middle && rowNumber < middle + batchSize)
					|| (rowNumber >= (rowCount - batchSize + 1) && rowNumber <= rowCount)) {
				for (int i = 1; i <= columnCount; i++) {
					if (i > 1) {
						resultString.append(",");
					}
					int type = rsmd.getColumnType(i);
					if (type == Types.VARCHAR || type == Types.CHAR || type == Types.LONGNVARCHAR) {
						resultString.append(results.getString(i));
					} else if (type == Types.BIT) {
						resultString.append(String.valueOf(results.getBoolean(i)));
					} else if (type == Types.BIGINT) {
						resultString.append(String.valueOf(results.getLong(i)));
					} else if (type == Types.NUMERIC || type == Types.DECIMAL) {
						resultString.append(String.valueOf(results.getBigDecimal(i)));
					} else if (type == Types.TINYINT || type == Types.SMALLINT || type == Types.INTEGER) {
						resultString.append(String.valueOf(results.getInt(i)));
					} else if (type == Types.REAL) {
						resultString.append(String.valueOf(results.getFloat(i)));
					} else if (type == Types.FLOAT || type == Types.DOUBLE) {
						resultString.append(String.valueOf(results.getDouble(i)));
					} else if (type == Types.BINARY || type == Types.LONGVARBINARY || type == Types.VARBINARY) {
						resultString.append(String.valueOf(results.getByte(i)));
					} else if (type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP) {
						resultString.append(String.valueOf(results.getTime(i)));
					} else if (type == Types.BLOB || type == Types.CLOB || type == Types.NCLOB) {
						return -1L;
					} else {
						//throw new CmlpDataSrcException("OOPS. Dev missed mapping for DATA Type. Please raise a bug.");
						throw new DataSourceNotFoundException("OOPS. Dev missed mapping. Please raise a bug.", Status.INTERNAL_SERVER_ERROR.getStatusCode());
					}
				} // for loop ends
			} // if expression ends
			resultString.append("\r\n");
			// log.info("rowNumber completed: " + rowNumber);
			rowNumber++;
		}
		logger.debug("\nPast Last Record: " + rowNumber);
		long size = 0;
		size = resultString.toString().getBytes().length * 1L;
		logger.debug("\nSize of 20% data: " + size + " bytes");
		logger.debug("calculateJdbcResultSizeBySampling() End");
		return size * 5;
	}
	// TODO : For Future Purpose
	private org.acumos.workbench.common.vo.DataSourceMetadata getMySqlMetadata(ResultSet results) throws SQLException {
		logger.debug("getMySqlMetadata() Begin");
		ResultSetMetaData rsmd = results.getMetaData();
		int columnCount = rsmd.getColumnCount();
		// getting metaData
		ArrayList<NameValue> lstSimpleMetadata = new ArrayList<NameValue>();
		lstSimpleMetadata.add(new NameValue("columnCount", String.valueOf(columnCount)));
		
		results.last();
		
		lstSimpleMetadata.add(new NameValue("rowCount", String.valueOf(results.getRow())));
		// now you can move the cursor back to the top
		results.beforeFirst();
		//lstSimpleMetadata.add(new NameValue("rowCount", String.valueOf(rowCount)));
		
		long datasetSize = getMySqlDatasetSize(results);
		String value = String.valueOf(datasetSize) + " bytes";
		if (datasetSize == -1L) {
			value = "Not Available Due to BLOB columntype";
		}
		lstSimpleMetadata.add(new NameValue("dataSize", value));
		ArrayList<ColumnMetadataInfo> lstcolumnMetadata = new ArrayList<ColumnMetadataInfo>();
		for (int i = 1; i <= columnCount; i++) {
			lstcolumnMetadata.add(new ColumnMetadataInfo(rsmd.getColumnName(i), rsmd.getColumnTypeName(i),
					String.valueOf(rsmd.getColumnDisplaySize(i))));
		}
		DataSourceMetadata metadata = new DataSourceMetadata();
		metadata.setMetaDataInfo(lstSimpleMetadata);
		metadata.setColumnMetaDataInfo(lstcolumnMetadata);
		logger.debug("MySql Metadata: \n" + metadata);
		logger.debug("getMySqlMetadata() End");
		return metadata;
	}

	private long getMySqlDatasetSize(ResultSet results) throws DataSourceNotFoundException, SQLException {
		logger.debug("getMySqlDatasetSize() Begin");
		int MIN_SAMPLING_SIZE; //default to 50
		String strMIN_SAMPLING_SIZE = customconfigProps.getComponentPropertyValue();
		MIN_SAMPLING_SIZE = strMIN_SAMPLING_SIZE != null ? Integer.parseInt(strMIN_SAMPLING_SIZE) : 50;

		long size = 0;
		
		results.last();
		int rowCount = results.getRow();
		results.beforeFirst();
		
		if (rowCount <= MIN_SAMPLING_SIZE) { 
			size = calculateJdbcResultSizeByNotSampling(results);
		} else {
			size = calculateJdbcResultSizeBySampling(results);
		}
		logger.debug("getMySqlDatasetSize() End");
		return size;
	}

	private int getJdbcRowCount(ResultSet results) throws SQLException {
		logger.debug("getJdbcRowCount() Begin");
		int rowCount = 0;
		results.last();
		rowCount = results.getRow();

		results.beforeFirst();
		logger.debug("getJdbcRowCount() End");
		return rowCount;
	}
}
