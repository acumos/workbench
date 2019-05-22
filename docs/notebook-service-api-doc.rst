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
ML Workbench Notebook Service Engine Application Programming Interfaces
====================================================================


API 
====

1.	Create Notebook
--------------------
	**Operation Name**
		createNotebook
	**Trigger**
		This API is called when the user:
		
		1.	Clicks on the “Create Notebook” icon under the Notebook folder under the user’s Project space in ML Workbench, or,
		
		2.	Clicks on the “Create Notebook” icon under the Notebook folder  in the user’s ML Workbench space (outside the project)

	**Request**
		{
		   notebook:Notebook;//mandatory

		}
	**Response**
		{
		 notebook:Notebook; 
			
		}

	**Behavior**
	
		1.	The Notebook Service must check if the request JSON structure is valid, otherwise it should return
			
			a.	serviceStatus.status.SERVICE_STATUS=ERROR 
			
			b.	serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
			
			c.	Send the response to client with Http response code – 4xx 
		
		2.	The Notebook Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Acumos User Id missing”.
			
			c.	Send response to client with Http response code – 4xx
			
		3.	The Notebook Service must check that notebook name is provided in the request otherwise it must return: 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Notebook Name missing” or “Invalid Notebook name” or “Invalid Notebook version” 
			
			c.	Send response to client with Http response code – 4xx
			
			**Validation:**
			
			a.	Notebook name may contain alphanumeric characters and “_” character – no other any other characters are allowed. The name must start with an alpha character. 
			
			b.	Notebook version is optional. It is a string which can contain alphanumeric characters, zero or more “_” character, and zero or more period character. The version must start with a numeric character. Other characters are not allowed.
			
		4.	Check notebook type is provided in the request: The Notebook Service must check that notebook type is provided in the request and it is one of the pre-defined types (defined below) otherwise it must return
			
			a.	serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Missing mandatory field – notebook type” or  “Invalid notebook type provided” 
			
			c.	Send response to client with Http response code – 4xx
			
			**Note:** The following notebook types are supported:
				
				a.	Jupyter
				
				b.	Zeppelin
			
		5.	Check if project exists: If projectId.uuid is included in the request, then the Notebook Service must call Common Data Service (CDS) to make sure that the projectId.uuid exists in the Project Table, otherwise it must return:
			
			a.	serviceStatus.SERVICE_STATUS =ERROR
			
			b.	serviceStatus.statusMessage = “ProjectId does not exist. 
			
			c.	Send response to client with Http response code – 4xx
			
		6.	Check if Notebook name and version already exists in the user’s notebook space: The Notebook Service must call Common Data Service (CDS) to make sure that the combination of the notebook name and version provided  in the request does not already exist in the User Table for the specified authenticatedUserId requesting the notebook creation, otherwise it must return:
			
			a.	serviceStatus.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Notebook name and version already exists for this user”. 
			
			c.	Send response to client with Http response code – 4xx
			
			**CDS Dependency:**
			
			a.	CDS REST API that returns a Boolean if the specified notebook name and version already exists (I.e. associated with the user in) 
			
		7.	Add the user (i.e., authenticatedUserId) in the (Workbench) User Table: The Notebook service must add this user requesting the creation of notebook to the Workbench User Table if the user is not already populated in the table. 
			
			a.	Check if the authenticatedUserId is present in the Workbench User Table (the project owner should be in the Boreas User Table but if the user requesting the creation of the notebook is not the project owner rather is a project collaborator then his authenticatedUserId may not be in the Workbench User Table)). 
			
			b.	Generate a UUID for this new (ML Workbench) user and populate in userId.uuid, if not already populated
			
			c.	Populate userId.IdentifierType = “USER”, if not already populated. 
			
			d.	Keep other fields such as userId.metrics and userId.version empty
			
			**CDS Dependency:**
			
			a.	CDS REST API that returns a Boolean if the authenticatedUserId exists in Workbench User Table.
			
			b.	CDS REST API to add a new row entry in the Workbench User Table. 
			
			c.	CDS REST API to update an entry in Workbench User Table.
			
		8.	Create new Notebook: The Notebook Service must create a new entry in the Notebook Table
			
			a.	Generate a new uuid for the Notebook.
			
			b.	Populate the above uuid into notebookId.uuid
			
			c.	Populate the notebook name supplied in the request into notebookId.name
			
			d.	Populate the notebook version, if supplied in the request, into notebookId.versionId.label
			
			e.	Populate the notebookId.identiierType = NOTEBOOK
			
			f.	Populate the notebookId.versionId.timestamp with the current timestamp
			
			g.	Populate the owner field of the Notebook table with the reference to the authenticatedUserId row in the (Workbench) User Table [The owner field is a FK to the User Table]. 
			
			h.	Populate the notebookType with the notebook type information provided in the request.
			
			i.	Populate the description with the notebook description provided in the REST call. 
			
			j.	Create the JSON formatted Notebook object.
			
		9.	Associate the user with the Notebook: The Notebook Service must populate the owner field of the Notebook Table with the reference to this user entry in the Workbench User Table.
			
			**CDS Dependency:**
			
			a.	CDS REST API that updates an entry in the Notebook table.
			
		10.	Add the notebook to the (Workbench) User Table: The Notebook Service must populate the notebooks field of the User Table with the reference to the new Notebook record/entry just created in the Notebook table. Note that a User may have multiple notebooks associated with him. 
			
		11.	Add the notebook to the Project Table: If projectId.uuid was provided in the request, then the Notebook Service must populate the notebooks field in the Project Table with the reference to the new Notebook record/entry just created in the Notebook table. 
			
		12.	Add the location of notebook in Git Repository to the Notebook table: The Notebook Service must populate the notebookId.repositoryUrl field of the Notebook table with the relative URL of the notebook where it will be stored in Git repository. 
		
			**Note:** In Git, the notebooks are identified by their notebookId.uuid. Since a notebook may belong to multiple projects and also to multiple users, the notebook file will be stored in Git at the following path:
		
			a.	/notebooks/notebookId.uuid ((make sure to delete this path when deleting the notebook))
			
		13.	The Notebook Service must return the following to the UI Layer:
			
			a.	The JSON formatted Notebook object in the body of the response. 
			
			b.	Http response code 201 – created.
			
			**Note:** The Notebook is not yet launched. When the user clicks on the “Launch Notebook” icon in the Notebook space, then the Jupyter Notebook server will be created and the notebook is launched in a separate browser tab.


2.	Launch (Get) Notebook
--------------------
	**Operation Name**
		launchNotebook
	**Trigger**
		
		This API is called when the user:
			
			1.	Clicks on the “Launch Notebook” icon in the Notebook folder (space) under the user’s Project in ML Workbench, or,
			
			2.	Clicks on the “Launch Notebook” icon in the user’s ML Workbench space, outside of any project.


	**Request**
		{
		   notebook:Notebook;//mandatory

		}
	**Response**
		{
		 notebook:Notebook; 
			
		}

	**Behavior**
	
		1.	The Notebook Service must check if the request JSON structure is valid, otherwise it should return
			
			a.	serviceStatus.status.SERVICE_STATUS=ERROR 
			
			b.	serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
			
			c.	Send the response to client with Http response code – 4xx
			
		2.	The Notebook Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Acumos User Id missing”.
			
			c.	Send response to client with Http response code – 4xx (404)
			
		3.	The Notebook Service must check that notebookId.uuid entry exists in the request body otherwise it must return: 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Notebook Id missing”
			
			c.	Send response to client with Http response code – 4xx (404)
			
		4.	Check if the notebook is archived: The Notebook Service must call CDS to check if the notebook is archived, and if so it should return:
			
			a.	status.SERVICE_STATUS =ERROR 
			
			b.	statusMessage = “Cannot launch  – notebook is archived”.
			
			c.	Send response to client with Http response code – 4xx
			
		5.	The Notebook Service must check that the requested notebookId.uuid exists in the Notebook table, otherwise it must return: 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Notebook not found”
			
			c.	Send response to client with Http response code – 4xx(404)
			
		6.	Check if the user is authorized to launch the notebook: The Notebook service must check if the user is the owner of the notebook (or in future release it must check if the user is otherwise authorized by the Permission table to perform such an action), otherwise it must return:
			
			a.	serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Permissions denied”
			
			c.	Send response to client with Http response code – 4xx
			
		7.	Call JupyterHub Server to start an instance of the Notebook Server for the user: The Notebook Service must:
			
			a.	Check if the user specific Notebook Server instance is already running.
			
			b.	If not, call the JupyterHub Server to start a user specific Notebook Server instance
			
			c.	The Notebook Service must populate notebookId.serviceUrl field with above URL. 
			
			d.	The Notebook Service must create a JSON formatted notebook object with the URL populated. 
			
		8.	The Notebook Service must retrieve the notebookId.repositoryUrl field (which was populated during create notebook operation) from the Notebook table and pass this to Notebook Server so that when the user presses SAVE in the notebook page the Notebook Server stores the notebook file at that url in Git repository. (discuss with Mukesh)
			
		9.	The Notebook Service must return the following to the UI Layer:
			
			a.	The JSON formatted notebook object in the body of the response. 
			
			b.	Http response code 200 – OK

			
3.	List Notebooks
--------------------
	**Operation Name**
		ListNotebooks
	**Trigger**
		This API is called when the user clicks on “My Notebooks” catalog in his ML Workbench User space or when the user clicks on the “My Notebooks” catalog under a particular project.

	**Request**
		{
		   user:User;//mandatory

		}
	**Response**
		{
		 notebookList:Notebooks; 
			
		}

	**Behavior**
	
		1.	The Notebook Service must check if the request JSON structure is valid, otherwise it should return
			
			a.	serviceStatus.status.SERVICE_STATUS=ERROR 
			
			b.	serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
			
			c.	Send the response to client with Http response code – 4xx (404)
			
		2.	The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Acumos User Id missing”.
			
			c.	Send response to client with Http response code – 4xx (404)
			
		3.	Check if the user is authorized to request this operation: The Notebook service must check if the user is authorized by the Permission table to perform such an action), otherwise it must return:
			
			a.	serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Permissions denied”
			
			c.	Send response to client with Http response code – 4xx
			
			**Note:** The test is out of Boreas scope. 
			
		4.	Check if the Project Id exists: If projectId.uuid is populated then the Notebook service must call CDS to check if the project exists in the Project Table, otherwise it must return:
			
			a.	serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Project Id does not exists”
			
			c.	Send response to client with Http response code – 4xx (404)
			
		5.	Retrieve all notebooks associated with the user and the project: The Notebook Service must:
			
			a.	Call CDS to retrieve all notebooks, active and archived, associated (both owner and collaborator) with the user and if the projectId.uuid is also populated in the request to retrieve a list of notebooks associated with the given user and project. Each notebook object is populated with the notebook name, version, notebookId.uuid, description, notebookType and kernelType
			
			b.	Create a list of JSON formatted Notebook objects with the above information populated. 
			
			**CDS Dependency:**
			
			a.	CDS must implement a REST Call that returns a list of notebook object objects (populated with the above information) associated with the user. 
			
			b.	CDS must implement a REST Call that returns a list of notebook object objects (populated with the above information) associated with a given user and project. 
			
		6.	The Notebook  Service must return the following to the UI Layer:
			
			a.	The list of JSON formatted Notebook objects in the body of the response. 
			
			b.	Http response code – 200 OK. 

			
4.	Update Notebook
--------------------
	**Operation Name**
		updateNotebooks
	**Trigger**
		This API is called when the user request the update of an existing Notebook in his ML Workbench workspace. The notebook name, version or description may be changed with this call.

	**Request**
		{
		   notebook:Notebook;//mandatory

		}
	**Response**
		{
		 notebook:Notebook; 
			
		}

	**Behavior**
	
		1.	The Notebook Service must check if the request JSON structure is valid, otherwise it should return
			
			a.	serviceStatus.status.SERVICE_STATUS=ERROR 
			
			b.	serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
			
			c.	Send the response to client with Http response code – 4xx
			
		2.	The Notebook Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return 
			
			a.	 serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Acumos User Id missing”.
			
			c.	Send response to client with Http response code – 4xx (404)
			
		3.	Check requestor permissions: The Notebook Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the notebook (or in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it must return:
			
			a.	status.SERVICE_STATUS =ERROR 
			
			b.	statusMessage = “Permission denied”.
			
			c.	Send response to client with Http response code – 4xx.
			
		4.	Check if the notebook is archived: The Notebook Service must call CDS to check if the notebook is archived, and if so it should return:
			
			a.	status.SERVICE_STATUS =ERROR 
			
			b.	statusMessage = “Update not allowed – notebook is archived”.
			
			c.	Send response to client with Http response code – 4xx
			
		5.	Check if new Notebook name and version already exists for the user: The Notebook Service must call Common Data Service (CDS) to make sure that the combination of the requested new notebook name and version provided  in the request does not already exist for the authenticatedUserId in the Workbench User Table, otherwise it must return:
			
			a.	serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Notebook name and version already exists for user”. 
			
			c.	Send response to client with Http response code – 4xx
			
			**CDS Dependency:**
			
			a.	CDS must implement a REST API that returns a Boolean if the notebook name and version already exists, i.e. associated with the user in Workbench User Table, 
			
		6.	Check if project id exist: If projectId.uuid is provided in the request object then check if this project exist. Call CDS to check if projectId.uuid exist in Project table. If it does then return the following
			
			a.	serviceStatus.status.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Project Id is invalid”. 
			
			c.	Send response to client with Http response code – 4xx (404)
			
		7.	Assign the notebook to an existing project: If the projectId.uuid is populated in the request object and if the notebook is not part of any existing project then assign this notebook to the requested projectId.uuid
			
			a.	Call CDS to check if the notebook is part of any other project. CDS will return a project UUID. If this returned project Id matches the one that was provided in the request object, then it is not a request to assign the notebook to a project – may be a request to update the name or version or description. 
			
			b.	If CDS returns a null project Id then it is a request to assign the notebook to a project. 
			
			c.	Call CDS to add the notebookId.uuid to the Project Table. 
			
			**CDS Dependency:**
			
			a. CDS should expose a REST API to check if the project Id is valid
			
			b. CDS should expose a REST API to return the projectId.uuid with which a notebookId.uuid is associated with.
			
		9.	Update the Notebook table with the user : Add the user as the collaborator of Notebook.
			
		10.	Update the existing entry in Notebook Table: The Project Service must update the existing notebookId.uuid entry in Notebook Table.
			
			a.	Populate the notebook name, if supplied in the request, into notebookId.name
			
			b.	Populate the notebook version, if supplied in the request, into notebookId.versionId.label
			
			c.	Populate the notebookId.versionId.timestamp with the current timestamp. 
			
			d.	Note that owner of the notebook is still the original notebook creator. 
			
			e.	Populate the description with the notebook description provided in the REST call
			
			f.	(Note: Previous notebook name and version is overwritten and hence lost). (May be we should save the old name/version in the Notebook revision history – History Table)
			
			**Note:** If this notebook was shared with other users, then the other user(s) will see the revised name and version.
			
		11.	The Notebook Service must return:
			
			a.	JSON formatted Notebook Object in body of the response
			
			b.	 Http response code 200 – OK. 


5.	Archive Notebook
--------------------
	**Operation Name**
		archiveNotebooks
	**Trigger**
		This API is called when the user request the archival of an existing Notebook in his notebook catalog in (either under the Project or Users Notebook folder) in ML Workbench. 

	**Request**
		{
		   notebook:Notebook;//mandatory

		}
	**Response**
		{
		 notebook:Notebook; 
			
		}

	**Behavior**
	
		1.	The Notebook Service must check if the request JSON structure is valid, otherwise it should return
			
			a.	serviceStatus.SERVICE_STATUS=ERROR 
			
			b.	serviceStatus.statusMessage = “Incorrectly formatted input – Invalid JSON”
			
			c.	Send the response to client with Http response code – 4xx
			
		2.	The Notebook Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return 
			
			a.	 serviceStatus.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Acumos User Id missing”.
			
			c.	Send response to client with Http response code – 4xx (404)
			
		3.	Check if the requestor is the owner of the notebook or is authorized to archive the notebook: The Notebook Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the project (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:
			
			a.	serviceStatus.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Permission denied”.
			
			c.	Send response to client with Http response code – 4xx.  
			
		4.	Check if the Notebook is referenced by other Users or in Other Projects: The Notebook service must check if this notebookId.uuid is referenced (i.e., in use) by any other users by following the links to the Notebooks in each entry of the User Table. If yes it must return:
			
			a.	serviceStatus.SERVICE_STATUS =ERROR 
			
			b.	serviceStatus.statusMessage = “Notebook is referenced by other users / projects”.
			
			c.	Send response to client with Http response code – 4xx
			Note: This check is out of scope of Boreas Release – because artifact sharing is out of scope. 
			
		5.	Mark the Notebook “Archived”: The Notebook Service must call the CDS to update the artifactStatus of the notebook to “Archived”.
			
			**CDS Dependency:**
			
			a.	CDS must implement a REST API to add, delete and update an artifact entry in the (Notebook, Pipeline, Solution, etc.) artifact table.
			
		6.	Construct a JSON formatted Notebook object with serviceStatus.status=COMPLETED and artifactStatus = ARCHIVED.
			
		7.	The Notebook Service must return:
			
			a.	Notebook object as the body of the response
			
			b.	Http response code 200.

