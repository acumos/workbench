.. ===============LICENSE_START=======================================================
.. Acumos
.. ===================================================================================
.. Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
				
				category : category,
				
				namespace : datasource namespace,
				
				textSearch : textSearch
			}
			
		**Response**
			{
			 dataSourceModelList: List of DataSourceModel
			}

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

			5) If the user wants to get the DataSources details with textSearch only, query to couch db with parameters like textSearch, userId then get the results accordingly, if no results are these then return empty response.

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
				
				dataSourceKey : DataSource Key,
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
				
			4) DataSource service must validate the input parameters such as Authenticated UserId, dataSourceKey. Then query to couch db by using userId and dataSourceKey then get the records in json formatted object (DataSourceModel). If no records are available then throw the appropriate exception such as DataSourceException with error description "DataSource is not available for the given DataSourceKey : cac684accjb " with error code 4xx.

			5)  While fetching the DataSources details if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			6) The Json formatted DataSourceModel object should get in the body of the response with HTTP Status code 200 OK.
			
3. Create New DataSource
---------------------------
		**Request Method**
			POST
		**Operation Name**
			createDataSource
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}
		**Trigger**
			This API is called when the login user wants to creare the new datasource.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				DataSource : DataSource Model object,
			
			}
			
		**Response**
			{
			 DataSource: DataSource Model object
			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields exists or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx
				
			2) DataSource service must check the input request(@RequestBody DataSource dataSource) has the valid json structure and correctly formatted or not. if not throw the error message accordingly.
			
			3) Check the dataSource request body has mandatory fields like category, namespace, datasourceName, readWriteDescriptor are there or not, if not there then throw the InvalidInputJSONException with error description as "Incorrectly formatted input Invalid JSON." with error code 4xx.
			
			4) DataSource service must validate the dataSource request body input parameter such as category field. If the category is not in the specified DataSource category list which is there in CategoryTypeEnum such as [file, cassandra, mongo, jdbc, hive, hive batch, hdfs, hdfs batch, mysql, Spark Standalone, Spark on Yarn] then need to throw the appropriate exception which includes error description "DataSource has invalid category value. Please send the correct value." and along with the error code 4xx.
			
			5) DataSource service must validate the dataSource request body input parameter such as readWriteDescriptor value contains as per the enum values in ReadWriteTypeEnum(read,write). If not there then need to throw the appropriate exception which includes error description "DataSource has invalid readWriteDescriptor value. Please send the correct value." and along with the error code 4xx.
			
			6) DataSource service must check the dataSourceModel request body input connection parameters such as CommonDetailsInfo, DBDetailsInfo which includes serverName, portNumber, environment, databaseName, dbServerUsername, dbServerPassword, jdbcURL, dbQuery. If one of them are missing then throw the DataSourceException with error description "Datasource Model has missing mandatory information. Please send all the required information." with error code 4xx.

			7) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			8) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below as a Json response

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			9) Check for the DataSource category connection status such as for Couch/MySql/Mongo,.. connectivity is there or not, by using userId, DataSource's CommonDetailsInfo and DBDetailsInfo establish the connection and run the query and set the required appropriate data in dataSource object. if the connection is success then return response as "success" else throw the exception with error description "Couldn't establish the connection for Couch/MySql/Mongo services" and error code 4xx.
			
			10) If the connection status is success then encrypt the DbServerUsername, DbServerPassword which is there in DBDetailsInfo and set encrypted user name and password in DataSource object and store it in Couch DB.
			
			11)  While saving the DataSources details in couch db if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			12) The Json formatted DataSource object should get in the body of the response with HTTP Status code 200 OK.
			

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
				
				dataSourceKey : DataSource Key (DataSource Id),
				
				DataSource : DataSource object,
				
			}
			
		**Response**
			{
			 dataSource: DataSource
			}

			HTTPStatus : 201

		**Behavior**

			1) DataSource service must check the input request has all the mandatory fields exists or not. If not throw the error message accordingly with error details as below

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “Missing Mandatory Field : UserId”

				c.Send the response to client with Http response code – 4xx
				
			2) DataSource service must check the input request(@RequestBody DataSource dataSource) has the valid json structure and correctly formatted or not. if not throw the error message accordingly.
			
			3) Check the dataSource input request body has mandatory fields like category, namespace, datasourceName, readWriteDescriptor are there or not, if not there then throw the InvalidInputJSONException with error description as "Incorrectly formatted input Invalid JSON." with error code 4xx.
			
			4) DataSource service must validate the dataSource request body input parameter such as category field exists or not. If the category is not in the specified DataSource category list which is there in CategoryTypeEnum such as [file, cassandra, mongo, jdbc, hive, hive batch, hdfs, hdfs batch, mysql, Spark Standalone, Spark on Yarn] then need to throw the appropriate exception which includes error description "DataSource has invalid category value. Please send the correct value." and along with the error code 4xx.
			
			5) DataSource service must validate the datasource input request body input parameter such as readWriteDescriptor value contains as per the enum values in ReadWriteTypeEnum(read,write). If not there then need to throw the appropriate exception which includes error description "DataSource has invalid readWriteDescriptor value. Please send the correct value." and along with the error code 4xx.
			
			6) DataSource service must check the dataSource request body input connection parameters such as CommonDetailsInfo, DBDetailsInfo which includes serverName, portNumber, environment, databaseName, dbServerUsername, dbServerPassword, jdbcURL, dbQuery. If any one of them are missing then throw the DataSourceException with error description "Datasource input request has missing mandatory information. Please send all the required information." with error code 4xx.

			7) Get the JWT authToken from HttpServletRequest request as per the existing implementation.

			8) Check the login user exists in DataBase (MariaDB by calling CDS) or not, if not throw the error message like below as a Json response

				a.serviceStatus.status.SERVICE_STATUS=ERROR

				b.serviceStatus.statusMessage= “techmdev User not found”

				c.Send the response to client with Http response code – 4xx
				
			9) DataSource Service must check in the DataBase, for the given dataSourceKey is exists or not. If exists then return the record, else throw the exception as DataSourceException with error description "DataSource for the specified DataSourceKey : " + dataSourceKey + " Not Available" with error code 4xx.
			
			10) DataSource must check the login user has the access for the DataSource, query to data base by using dataSourceKey and userId. If the records are available then return it, otherwise throw the exception as DataSourceException with error description as "please check dataset key provided and user permission for this operation", with error code 4xx.
				
			11) Check for the DataSource category connection status such as for Couch/MySql/Mongo,.. connectivity is there or not, by using userId, DataSource's CommonDetailsInfo and DBDetailsInfo establish the connection and run the query and set the required appropriate data in datasource object. if the connection is success then return response as "success" else throw the exception with error description "Couldn't establish the connection for Couch/MySql/Mongo services" and error code 4xx.
			
			12) While updating the DataSource details, the user need to send the encrypted DbServerUsername and DbServerPassword otherwise user will get the exception while decrypting, then DataSource service must decrypt the DbServerUsername and DbServerPassword and it will check for the connection establishment status as true/false.
			
			12) If the connection status is success then encrypt the DbServerUsername, DbServerPassword which is there in the updated DBDetailsInfo and set the encrypted user name and password in DataSource object and store it in DataBase.
			
			13)  While saving the DataSources details in DataBase if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			14) The Json formatted updated DataSource object should get in the body of the response with HTTP Status code 200 OK.
			
5. Delete DataSource Details
---------------------------
		**Request Method**
			DELETE
		**Operation Name**
			deleteDataSource
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasources/{datasourceKey}
		**Trigger**
			This API is called when the login user want to delete the registered dataSource details for a particular dataSourceKey in database.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				
				dataSourceKey : DataSource Key,
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
			
			5) DataSource service must check the login user has the access for the DataSource, query to data base by using dataSourceKey and userId. If the records are available then return it, otherwise throw the exception as DataSourceException with error description as "please check datasource key provided and user permission for this operation", with error code 4xx.
			
			6) After successful checks if the record is available in database then delete the record by querying to DataBase by using dataSourceKey. If deleted successfully then return the result.
			
			7)  While deleting the DataSources details in couch database if any database exception occurs then throw the DataSourceException with appropriate error description and error code as 4xx as per the existing implementation.

			8) The Json formatted Service Status object should get in the body of the response with HTTP Status code 200 OK.
			
			
6. Associate the DataSource to the Project
-------------------------------------------
		**Request Method**
			POST
		**Operation Name**
			associateDataSourcetoProject
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/projects/{projectId}/datasource/{datasourceKey}
		**Trigger**
			This API is called when the login user want to associate the datasource to a particular project.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id,
				
				projectId : Project Id,
				
				datasourceKey : DataSource Key,
				
				DataSource : DataSource object
			}
			
		**Response**
			{
			 DataSourceAssociationModel : DataSource Association Model Object
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
				
			4) The DataSource Service must check the given projectId is valid or not, by connecting to project service and get the Project object, else if the project is not available then throw the ProjectNotFoundException with error description "Project doesn't exists". If the Project is archived then throw the ArchivedException saying "Update Not Allowed, Specified Project : " + projectId + " is archived", else return the project object and also need to check the user has the permission to access the given projectId or not if not then throw the exception sayig NotProjectOwnerException.
			
			5) While associating the datasource to the project, datasource service need to check the combination of datasourceKey and userId, the DataSource exists or not in the DataBase. if the DataSource not found then throw the CouchDBException saying "DataSource is not available for the given DataSourceKey : " + dataSourceKey" with error code 4xx. If the datasource is available but its not accessible to login user then throw the appropriate exception like NotOwnerException with message "Permission Denied".
			
			5) DataSource service must check the given dataSourceKey is exists in the database or not, if not exists then throw the exception saying DataSourceNotFoundException with error description "DataSource is not available for the given DataSourceKey :  " + dataSourceKey" with error status code 4xx. If the dataSource available in database then return the same.
			
			6) While creating the new association, dataSource service need to verify that the association already exists or not. If the association already exists then throw the AssociationException with error description "Association already exists in Couch DB". If any failure occurs while connecting to the database then throw the exception saying CouchDBException with error message "Exception occured while finding the documents in couchDB".

			7) After doing all the successful validations, DataSource service must create the DataSourceAssociationModel object in the database, then return the json formatted object with HTTP Status code 200 OK.
			
			
7. Update the Association Details of DataSourceProject
-------------------------------------------------------
		**Request Method**
			PUT
		**Operation Name**
			updateDataSourceProjectAssociationDetails
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/projects/{projectId}/datasource/{datasourceKey}/association/{associationId}
		**Trigger**
			This API is called when the login user want to Update the Association Details of DataSourceProject.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id,
				
				projectId : Project Id,
				
				datasourceKey : DataSource Key,
				
				associationId : Association Id,
				
				DataSource : datasource
			}
			
		**Response**
			{
			 DataSourceAssociationModel : DataSource Association Model Object
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
				
			4) The DataSource Service must check the given projectId is valid or not, by connecting to project service and get the Project object, else if the project is not available then throw the ProjectNotFoundException with error description "Project doesn't exists". If the Project is archived then throw the ArchivedException saying "Update Not Allowed, Specified Project : " + projectId + " is archived", else return the project object and also need to check the user has the permission to access the given projectId or not if not then throw the exception sayig NotProjectOwnerException.
			
			5) Before updating the association details, get the DataSourceAssociationModel object from database by using associationId, if the record not found then throw the exception saying AssociationNotFoundException with error message "No DataSourceProject Association available for Association Id  : " + associationId", else if the record found then return the same with updated details like timestamp and the version.
			
			6) While updating the DataSourceAssociationModel details, update the old DataSourceAssociationModel object to new DataSourceAssociationModel details and update in database then return the same json formatted DataSourceAssociationModel object with HTTP status code 200 OK. If any exception occurs while connecting to the database server then throw the CouchDBException with appropriate error message.
			
			
8. Get the list of DataSources which are associated to a project
-----------------------------------------------------------------
		**Request Method**
			GET
		**Operation Name**
			dataSourceListAssociatedtoProject
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/projects/{projectId}
		**Trigger**
			This API is called when the login user want to get the list of Datasources which are associated to a Project.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id,
				
				projectId : Project Id,
				
			}
			
		**Response**
			{
			 List<DataSourceAssociationModel> : List of DataSource Association Model Object
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
				
			4) The DataSource Service must check the given projectId is valid or not, by connecting to project service and get the Project object, else if the project is not available then throw the ProjectNotFoundException with error description "Project doesn't exists". If the Project is archived then throw the ArchivedException saying "Update Not Allowed, Specified Project : " + projectId + " is archived", else return the project object and also need to check the user has the permission to access the given projectId or not if not then throw the exception sayig NotProjectOwnerException.
			
			5) Get the Association details of DataSource and a Project for a particular projectId then query to the database and return the JSON formatted List of DataSourceAssociationModel with HTTP response code 200 OK. While fetching the Association details of a project and DataSource if the association not found then throw the exception like AssociationNotFoundException with error message "No DataSource is Associated for the Project : " + projectId" with error HTTP Status code 4xx.
						
9. Delete the Association Details of DataSourceProject
-----------------------------------------------------------------
		**Request Method**
			DELETE
		**Operation Name**
			deleteDataSourceProjectAssocaition
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/projects/{projectId}/datasource/{datasourceKey}/association/{associationId}
		**Trigger**
			This API is called when the login user want to Delete the Association Details of DataSourceProject.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id,
				
				projectId : Project Id,
				
				datasourceKey : Data Source key,
				
				associationId : Association Id
				
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
				
			4) The DataSource Service must check the given projectId is valid or not, by connecting to project service and get the Project object, else if the project is not available then throw the ProjectNotFoundException with error description "Project doesn't exists". If the Project is archived then throw the ArchivedException saying "Update Not Allowed, Specified Project : " + projectId + " is archived", else return the project object and also need to check the user has the permission to access the given projectId or not if not then throw the exception sayig NotProjectOwnerException.
			
			5) While deleting the Association details, make sure that the associationId is exists or not in database, If the Association details are available for a particular associationId then return the same, else throw the exception like AssociationNotFoundException with error message "No DataSourceProject Association available for Association Id  : " + associationId".
			
			6) After fetching the Association Details, get the associationId and revisionId from the DataSourceAssociationModel, then query to database and remove the DataSourceAssociationModel object from the Database. While deleting the Association details from the Database any exception occurs then throw the exception as CouchDBException with error description "Exception occured while Deleting the Association Details for AssociationId : "
			+ associationId + "and DocumentRevisionId : " + _rev". After succesful deletion of the Association return the response as JSON formatted Service Status object with HTTP Status code 200 OK.
			
10. Share Data Source to a Collaborator
-----------------------------------------
		**Request Method**
			POST
		**Operation Name**
			shareDataSource
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasource/{dataSourceKey}
		**Trigger**
			This API is called when the login user want to Share Data Source to a Collaborator.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id,
				
				datasourceKey : Data Source key,
				
				Users : collaborators
				
			}
			
		**Response**
			{
			 DataSource : datasource
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
				
			4) Before sharing the DataSource to the collaborator make sure that the login user is the owner of the DataSource or not and also check the datasource is exists in the database or not for a particular datasourceKey. If the dataSource is not available then throw the exception saying DataSourceNotFoundException with error desciption "DataSource is not available for the given DataSourceKey :  " + dataSourceKey" with exception status code 4xx, else return the datasource details.
			
			5) Make sure that the given input collaborator details are already exists or not, if already exists then throw the exception saying CollaboratorExistsException with error description "Collaborator already Exists".
			
			6) While sharing the datasource to a collaborator fetch the DataSourceCollaboratorModel details for a particular datasourceKey and update the requiured details of a DataSourceCollaboratorModel and fileter out the permissions and save the DataSourceCollaboratorModel object in database and return the same.
			
			7) Now convert the DataSourceCollaboratorModel object to Users object and set the updated collaborator details into DataSource object and return the same with HTTP Status code 200 OK.
			
			
11. Get the Shared DataSources for a User
-----------------------------------------
		**Request Method**
			GET
		**Operation Name**
			getSharedDataSources
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasources/shared
		**Trigger**
			This API is called when the login user want to get the shared datasources for a user.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id
				
			}
			
		**Response**
			{
			 DataSource : datasource
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
				
			4) While fetching the datasources, get the user details, if the user not found the throw the exception as UserNotFoundException else get the MLPUser details. After getting the user details then get the shared datasources then make filter out then return the list of DataSources.
			
			5) After fetching the list of DataSource objects then return the JSON formatted response with HTTP status code 200 OK.
			

12. Remove the User from Collaborator List
-------------------------------------------
		**Request Method**
			DELETE
		**Operation Name**
			removeDataSourceCollaborator
		**Context Path** 
			/mlWorkbench/v1/datasource/users/{authenticatedUserId}/datasource/{dataSourceKey}/collaborators
		**Trigger**
			This API is called when the login user want to Remove the User from Collaborator List.
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : Acumos User login Id,
				
				datasourceKey : Data Source key,
				
				Users : collaborators
				
			}
			
		**Response**
			{
			 DataSource : datasource
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
			
			6) Check whether the given user is active or not by calling CDS API, if the user is not active then throw the exception saying UserNotFoundException with error message "User is not ACTIVE". Also check the user has the role or not, if not have any role then throw the exception like EntityNotFoundException with error desciption "Roles not defined".
			
			7) Check the given user is collaborator or not for the given datasourceKey by fetching DataSourceCollaboratorModel object and get the collaborators details and filter out the user details and cehck if the user is exists as collaborator or not. If the user is not a collaborator then throw the exception like CollaboratorExistsException with error message "User is not a collaborator".
			
			8) While removing the collaborator for the given dataSourceKey get the DataSourceCollaboratorModel object and get the collaborator details from it then filter the collaborator details from the input request Users object. If both the users are same then remove the collaborator details from the DataSourceCollaboratorModel object and update the required fields along with the collaborator details and update the same object in couch database and return the same.
			
			9) After auccessful updation of the DataSourceCollaboratorModel object then get the users object from it, then set the users object into DataSource object. Then return the JSON formatted object with HTTP Status code 200 OK.
			
			
			
			
			
			