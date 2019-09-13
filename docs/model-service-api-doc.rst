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
ML Workbench Model Service Engine Application Programming Interfaces
====================================================================


API
====

1.   List(Get)Models
---------------------
		**Operation Name**
			listModels
		**Trigger**
			 This API is called when the user:
			 
			 1. 	When the user clicks on the "My Models" catalog under a particular project.
			 
			 2.		When the user clicks on the "My Models" catalog (with out project)
			 
		**Request**
			{
				user:User;
			}
		**Response**
			{
				modelList:Models;
			}
		**Behavior**
		
			1.		The Model Service must check if the json structure is valid, otherwise it should return
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
				
				c.		Send the response to client with Http response code – 4xx
				
			2.		The Model Service must retrieve the authenticatedUserId from the context of REST call (the REST header contains the authenticatedUserId)  and make sure it is populated otherwise it must return
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Acumos User Id missing”
				
				c.		Send response to client with Http response code – 4xx (404)
				
			3.		Check if the user is authorized to request this operation
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Permissions denied”
				
				c.		Send response to client with Http response code – 4xx (404)
				
			4.		Check if the projectId exists
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Project Id does not exists”
				
				c.		Send response to client with Http response code – 4xx (404)
				
			5.		Retrieve all the models associated with a given user and project to do this Model Service must 
			
				a.		Retrieve the list of models associated to a project (to perform this operation need to call the CocuhDB API), owner as input user and status is not "deleted"
				
				b.		For each model association in above list : Check if the model version is not deleted in the Acumos. If the Model version is deleted, then update the model association in DB(CouchDB) as "Invalid" (call to CouchDB API to update project model association status) and also update the same in the above model association list.
				
			6.		The Model Service must return the following to the UI layer:
			
				a.		The list of JSON formatted Model objects in the body of the response.
				
				b.		Http Response code - 200 OK
				
				
2.    Associate Model to Project
---------------------------------
		**Operation Name**
			associateModeltoProject
		**Trigger**
			This API is called when the user:
			
			1.		Request to associate the model(s) to an existing project in his project catalog in ML Workbench workspace
			
		**Request**
			{
				model:Model;
				
			}
		**Response**
			{
				model:Model;
				
			}
			
		**Behavior**
		
			1.		The Model Service must check if the request JSON structure is valid, otherwise it should return

				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
				
				c.		Send the response to client with Http response code – 4xx
				
			2.		The Model Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Acumos User Id missing”
				
				c.		Send response to client with Http response code – 4xx
				
			3.		Check the requestor permissions: The Model Service must call CDS to check if the requestor (i.e authenticatedUserId) is the owner of the model (or in later releases must check the permissions table if the requestor is allowed to perform this operation). If not it just return :
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Permission Denied”
				
				c.		Send response to client with Http response code – 4xx
				
			4.		Check if the Project and Version already exists : The Model Service must call Project Service to make sure that project and version provided in the request already exists, otherwise it must return :
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Project and version does not exists”
				
				c.		Send response to client with Http response code – 4xx
				
			5.		Check if the project is archived : The Model Service must call Project Service to check if the project is archived, and if so it should return :
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Update not allowed – project is archived”
				
				c.		Send response to client with Http response code – 4xx
				
			6.		Check if the SolutionId(s) and Version(s) already exists : The Model Service must call Common Data Service to make sure that the SolutionId(s) and Version(s) provided in the request already exists, otherwise it should return :
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage = “Solution and Version does not exists”
				
			7.		Insert Project Model Association : The Model Service must insert new entry in project model association table, only if project model association for the input model and project does not exists and but if it exists then it should be in "Deleted" state (call CouchDB API to create new entry in Project Model Association Table).
			
			8.		The Project Service must return :
			
				a.		JSON formatted Project Object as body of the response
				
				b.		Http Response code - 200 OK
				
				
3.    Update Model Association with Project
--------------------------------------------
		**Operation Name**
			updateModelAssociationWithProject
		**Trigger**
			This API is called when the user :
			
			1.	Request to update the model association with Project in ML Workbench workspace.
			
		**Request**
			{
				model:Model;
				
			}
			
		**Response**
			{
				model:Model;
				
			}
			
		**Behavior**
		
		1.		The Model Service must check if the request JSON structure is valid, otherwise it should return
		
			a.		serviceStatus.status.SERVICE_STATUS=ERROR
			
			b.		serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
			
			c.		Send the response to client with Http response code – 4xx
			
		2.		The Model Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return
		
			a.		serviceStatus.status.SERVICE_STATUS=ERROR
			
			b.		serviceStatus.statusMessage= “Acumos User Id missing”
			
			c.		Send the response to client with Http response code – 4xx
			
		3.		Check if the requestor is the owner of the project workbench or is authorized to update the Project Model Association : The Model Service must call CDS to check if the requestor (i.e authenticatedUserId) is the owner of the project (in later must check the permissions table if the requestor is allowed to perform this action). If not it just return :
		
			a.		serviceStatus.status.SERVICE_STATUS=ERROR
			
			b.		serviceStatus.statusMessage= “Permission denied”
			
			c.		Send the response to client with Http response code – 4xx
			
		4.		Check if the requestor is the owner of the Model and is authorized to update the Project Model Association : The Model Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the Model (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return : 
		
			a.		serviceStatus.status.SERVICE_STATUS=ERROR
			
			b.		serviceStatus.statusMessage= “Permission denied”
			
			c.		Send the response to client with Http response code – 4xx
			
		5.		Update the association (link) between the project and model : The Model Service must call the CDS to update the project model association for pojectId and SolutionId/version (call Couch DB API to update project model association)
		
		6.		Construct the JSON formatted ServiceState object with serviceStatus.status=COMPLETED
		
		7.		The Model Service must return : 
		
			a.		JSON formatted Model object as the body of the response
			
			b.		Http response code 200.
			
			
4.    Delete Model Association with Project
--------------------------------------------
		**Operation Name**
			deleteModelAssociationWithProject
		**Trigger**
			This API is called when the user:
			
			1.		Request the deletion of model association with project in MLWorkbench workspace. The Project Model Association can only be deleted (i.e purged) if it is an ARCHIVED state.
			
		**Request**
			{
				model:Model;
				
			}
			
		**Response**
			{
				serviceState:ServiceState;
				
			}
			
		**Behavior**
		
			1.		The Model Service must check if the request JSON structure is valid, otherwise it should return
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
				
				c.		Send the response to client with Http response code – 4xx
				
			2.		The Model Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage= “Acumos User Id missing”
				
				c.		Send the response to client with Http response code – 4xx
				
			3.		Check if the requestor is the owner of the project Workbench or is authorized to delete the Project Model Association: The Model Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the project (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage= “Permission denied”
				
				c.		Send the response to client with Http response code – 4xx
				
			4.		Check if the requestor is the owner of the Model and is authorized to delete the Project Model Association: The Model Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the Model (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:
			
				a.		serviceStatus.status.SERVICE_STATUS=ERROR
				
				b.		serviceStatus.statusMessage= “Permission denied”
				
				c.		Send the response to client with Http response code – 4xx
				
			5.		Delete the association (link) between the project and model: The Model Service must call the CDS to delete the project model association for pojectId and SolutionId/version (call CouchDB API to delete the Project Model Association)
			
			6.		Construct the JSON formatted ServiceState object with serviceStatus.status=COMPLETED. 
			
			7.		The Model Service must return:
			
				a.		JSON formatted project object as the body of the response
				
				b.		b.	Http response code 200.
		
			
		
				
				
		
				
		