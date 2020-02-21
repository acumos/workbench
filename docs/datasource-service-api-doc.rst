.. ===============LICENSE_START=======================================================
.. Acumos
.. ===================================================================================
.. Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

====================================================================
ML Workbench DataSource Service Application Programming Interfaces
====================================================================

API
====

1.Get DataSource List
----------------------
		**Request Method**
			GET
		**Operation Name**
			getDataSourcesList
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}
		**Trigger**
			This API is called when the login user wants to get the list of dataSources.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				namespace : datasource namespace,
				
				category : category,
				
				textSearch : textSearch
			}
			
		**Response**
			{
			 dataSourceModelList: List of DataSourceModel

			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx

			2) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			3) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			4) DataSource service must validate the input parameters such as category, namespace, textSearch fields. If the category is not in the specified DataSource category list such as [file, cassandra, mongo, jdbc, hive, hive batch, hdfs, hdfs batch, mysql, Spark Standalone, Spark on Yarn] then need to throw the appropriate exception which  includes error description "DataSource has invalid category validate. Please send the correct value." and along with the error code 4xx.

			5) If the user wants to get the DataSources details with textSearch only, query to couch db with parameters like testSearch, userId and isActive status as true then get the results accordingly, if no results are these then return empty response.

			6) If the user wants to  get the DataSources details with input parameters like userId, namespace, category then add the necessary parameters in Couch query with isActive status as true, then get the results in JSON format, if no results are these then return empty response.

			7)  While fetching the DataSources details if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			8) The Json formatted List<DataSourceModel> objects should get in the body of the response with HTTP Status code 200 OK.
			
			
2.Get DataSource
----------------------
		**Request Method**
			GET
		**Operation Name**
			getDataSource
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasources/{datasourceKey}
		**Trigger**
			This API is called when the login user search for a particular datasourceKey to get the DataSource Details.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				datasourceKey : DataSource Key,
			}
			
		**Response**
			{
			 dataSourceModel: DataSourceModel
			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx

			2) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			3) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below as a Json response

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			4) DataSource service must validate the input parameters such as Authenticated UserId, datasourceKey. Such as query to couch db by using userId and datasourceKey and isActive status as true then get the records in json formatted object (DataSourceModel). If no records are available then throw the appropriate exception such as DataSourceException with error description "Invalid data. No Datasource info. Please send valid dataSourceKey." with error code 4xx. If the datasource exits but the login user doesn't have the access for that datasource then throw the exception such as DataSourceException with error description "please check dataset key provided and user permission for this operation" with error code 4xx.

			5)  While fetching the DataSources details if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			6) The Json formatted DataSourceModel object should get in the body of the response with HTTP Status code 200 OK.
			
3. Save DataSource Details
---------------------------
		**Request Method**
			POST
		**Operation Name**
			saveDataSourceDetails
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}
		**Trigger**
			This API is called when the login user wants to save the dataSource details.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				DataSourceModel : DataSource Model object,
				
				proxyHost : proxyHost,
				
				proxyPort : proxyPort,
				
				proxyUsername : proxyUsername,
				
				proxyPassword : proxyPassword
			}
			
		**Response**
			{
			 dataSourceModel: DataSourceModel
			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx
				
			2) DataSource service must check the input request(@RequestBody DataSourceModel dataSourceModel) has the valid json structure and correctly formatted or not. if not throw the error message accordingly.
			
			3) Check the dataSourceModel request body has mandatory fields like category, namespace, datasourceName, readWriteDescriptor are there or not, if not there then throw the InvalidInputJSONException with error description as "Incorrectly formatted input Invalid JSON." with error code 4xx.
			
			4) DataSource service must validate the dataSourceModel request body input parameter such as category field. If the category is not in the specified DataSource category list which is there in CategoryTypeEnum such as [file, cassandra, mongo, jdbc, hive, hive batch, hdfs, hdfs batch, mysql, Spark Standalone, Spark on Yarn] then need to throw the appropriate exception which includes error description "DataSource has invalid category value. Please send the correct value." and along with the error code 4xx.
			
			5) DataSource service must validate the dataSourceModel request body input parameter such as readWriteDescriptor value contains as per the enum values in ReadWriteTypeEnum(read,write). If not there then need to throw the appropriate exception which includes error description "DataSource has invalid readWriteDescriptor value. Please send the correct value." and along with the error code 4xx.
			
			6) DataSource service must check the dataSourceModel request body input connection parameters such as CommonDetailsInfo, DBDetailsInfo which includes serverName, portNumber, environment, databaseName, dbServerUsername, dbServerPassword, jdbcURL, dbQuery. If none of them are missing then throw the DataSourceException with error description "Datasource Model has missing mandatory information. Please send all the required information." with error code 4xx.

			7) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			8) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below as a Json response

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			9) Check for the DataSource category connection status such as for MySql/Mongo,.. connectivity is there or not, by using userId, DataSource's CommonDetailsInfo and DBDetailsInfo establish the connection and run the query and set the required appropriate data in dataSourceModel object. if the connection is success then return response as "success" else throw the exception with error description "Couldn't establish the connection for MySql/Mongo services" and error code 4xx.
			
			10) If the connection status is success then encrypt the DbServerUsername, DbServerPassword which is there in DBDetailsInfo and set encrypted user name and password in DataSourceModel object and store it in Couch DB.
			
			11)  While saving the DataSources details in couch db if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			12) The Json formatted DataSourceModel object should get in the body of the response with HTTP Status code 200 OK.
			

4. Update DataSource Details
-----------------------------
		**Request Method**
			PUT
		**Operation Name**
			updateDataSourceDetail
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasources/{datasourceKey}
		**Trigger**
			This API is called when the login user want to update the registered dataSource details for a particular dataSourceKey.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				dataSourceKey : DataSource Key,
				
				DataSourceModel : DataSource Model object,
				
				proxyHost : proxyHost,
				
				proxyPort : proxyPort,
				
				proxyUsername : proxyUsername,
				
				proxyPassword : proxyPassword
			}
			
		**Response**
			{
			 dataSourceModel: DataSourceModel
			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx
				
			2) DataSource service must check the input request(@RequestBody DataSourceModel dataSourceModel) has the valid json structure and correctly formatted or not. if not throw the error message accordingly.
			
			3) Check the dataSourceModel request body has mandatory fields like category, namespace, datasourceName, readWriteDescriptor are there or not, if not there then throw the InvalidInputJSONException with error description as "Incorrectly formatted input Invalid JSON." with error code 4xx.
			
			4) DataSource service must validate the dataSourceModel request body input parameter such as category field. If the category is not in the specified DataSource category list which is there in CategoryTypeEnum such as [file, cassandra, mongo, jdbc, hive, hive batch, hdfs, hdfs batch, mysql, Spark Standalone, Spark on Yarn] then need to throw the appropriate exception which includes error description "DataSource has invalid category value. Please send the correct value." and along with the error code 4xx.
			
			5) DataSource service must validate the dataSourceModel request body input parameter such as readWriteDescriptor value contains as per the enum values in ReadWriteTypeEnum(read,write). If not there then need to throw the appropriate exception which includes error description "DataSource has invalid readWriteDescriptor value. Please send the correct value." and along with the error code 4xx.
			
			6) DataSource service must check the dataSourceModel request body input connection parameters such as CommonDetailsInfo, DBDetailsInfo which includes serverName, portNumber, environment, databaseName, dbServerUsername, dbServerPassword, jdbcURL, dbQuery. If one of them are missing then throw the DataSourceException with error description "Datasource Model has missing mandatory information. Please send all the required information." with error code 4xx.

			7) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			8) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below as a Json response

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			9) DataSource Service must check in DataBase for the given dataSourceKey is exists or not. If exists then return the record, else throw the exception as DataSourceException with error description "DataSource for DataSourceKey : " + dataSourceKey + " Not Available" with error code 4xx.
			
			10) DataSource must check the login user has the access for the DataSource, query to data base by using dataSourceKey and userId. If the records are available then return it, otherwise throw the exception as DataSourceException with error description as "please check dataset key provided and user permission for this operation", with error code 4xx.
				
			11) Check for the DataSource category connection status such as for MySql/Mongo,.. connectivity is there or not, by using userId, DataSource's CommonDetailsInfo and DBDetailsInfo establish the connection and run the query and set the required appropriate data in dataSourceModel object. if the connection is success then return response as "success" else throw the exception with error description "Couldn't establish the connection for MySql/Mongo services" and error code 4xx.
			
			12) If the connection status is success then encrypt the DbServerUsername, DbServerPassword which is there in DBDetailsInfo and set encrypted user name and password in DataSourceModel object and store it in DataBase.
			
			13)  While saving the DataSources details in DataBase if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			14) The Json formatted updated DataSourceModel object should get in the body of the response with HTTP Status code 200 OK.
			
5. Delete DataSource Details
---------------------------
		**Request Method**
			DELETE
		**Operation Name**
			deleteDataSourceDetail
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasources/{datasourceKey}
		**Trigger**
			This API is called when the login user want to delete the registered dataSource details for a particular dataSourceKey in database.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				dataSourceKey : DataSource Key,
				
				proxyHost : proxyHost,
				
				proxyPort : proxyPort,
				
				proxyUsername : proxyUsername,
				
				proxyPassword : proxyPassword
			}
			
		**Response**
			{
			 Service Status : Service Status object
			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx

			2) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			3) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below as a Json response

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			4) DataSource Service must check in DataBase for the given dataSourceKey is exists or not. If exists then return the record, else throw the exception as DataSourceException with error description "DataSource for DataSourceKey : " + dataSourceKey + " Not Available" with error code 4xx.
			
			5) DataSource must check the login user has the access for the DataSource, query to data base by using dataSourceKey and userId. If the records are available then return it, otherwise throw the exception as DataSourceException with error description as "please check dataset key provided and user permission for this operation", with error code 4xx.
			
			6) After successful checks if the record is available in database then delete the record by querying to DataBase by using dataSourceKey. If deleted successfully then return the result.
			
			7)  While deleting the DataSources details in couch db if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			8) The Json formatted Service Status object should get in the body of the response with HTTP Status code 200 OK.



