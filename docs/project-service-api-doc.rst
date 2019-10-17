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
ML Workbench Project Service Engine Application Programming Interfaces
====================================================================


API
====

1.Create Project
--------------------
**Operation Name**
createProject
**Trigger**
This API is called when the user request the creation of a new Project in his ML Workbench workspace UI.
**Request**
{
   project:Project;//mandatory

}
**Response**
{
 project: Project
Note: The projectId.uuid is returned to UI Layer. It is used by the UI Layer later on to populate in the create notebook, create pipeline, create data source, etc. calls, so these artifacts get associated with their parent project if they are created under the project.


}

**Behavior**

1.The Project Service must check if the request JSON structure is valid, otherwise it should return

a.serviceStatus.status.SERVICE_STATUS=ERROR

b.serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

c.Send the response to client with Http response code – 400 Bad Request

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

a. serviceStatus.status.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Acumos User Id missing”.

c.Send response to client with Http response code – 400 Bad Request.

3.The Project Service must check that project name is provided in the request otherwise it must return:

a. serviceStatus.status.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Project Name missing”, or “Project Name Syntax Invalid” or “Project Version Syntax Invalid”

c.Send response to client with Http response code – 400 Bad Request

**Validation:** Project name may contain only alphanumeric characters including space and “_” character but not any other characters. Project version is optional. It is a string which can contain alphanumeric characters, “_” character, and one or more period character. Other characters are not allowed. The version must start with a numeric character.

4.Check if Project name and version already exists: The Project Service must call Common Data Service (CDS) to make sure that the combination of the project name and version provided  in the request does not already exist in the Workbench User Table for the specified authenticatedUserId requesting the project creation, otherwise it must return:

a.serviceStatus.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Project name and version already exists”.

c.Send response to client with Http response code – 400 Bad Request

**CDS Dependency:**

a.CDS REST API that returns a Boolean if the project name and version already exists (I.e. associated with the user in) Workbench User Table,

5.Create new entry in Workbench User Table: The Project Service must call Common Data Service (CDS) to check if the authenticatedUserId exists in the Workbench User Table. If it does not exist it must call CDS to create a new entry for the user in the Workbench User table as follows:

a.Populate the authenticatedUserId of the Workbench User Table row with the authenticatedUserId retrieved from the context of REST call.

b.Generate a UUID for this new (ML Workbench) user and populate in userId.uuid

c.Populate userId.IdentifierType = “USER”

d.Keep other fields such as userId.metrics and userId.version empty

**CDS Dependency:**

a.CDS REST API that returns a Boolean if the authenticatedUserId exists in Workbench User Table. (Not required – we are using Acumos User Table)

b.CDS REST API to add a new row entry in the Workbench User Table which has a FK to an entry in the Identifier Table (Not required – we are using Acumos User Table)

NOTE: We can populate Workbench User Table piecemeal when a user creates a Project or we can populate this table before the Boreas Release by adding all user entries in the existing (Athena) User Table to this table – only authenticatedUserId, Identifier.uuid, and Identifier.IdentifierType will need to be added via a script.

6.Create new entry in Project Table : The Project Service must:

a.Generate the uuid for the new project.

b.Populate the above uuid into projectId.uuid

c.Populate the project name supplied in the request into projectId.name

d.Populate the project version, if supplied in the request, into projectId.versionId.label

e.Populate the projectId.identiierType = PROJECT

f.Populate the projectId.versionId.timestamp with the current timestamp.

g.Populate the owner.authenticatedUserId with the authenticatedUserId retrieved from the context of the REST call.
h.Populate the description with the project description provided in the REST call

**CDS Dependency:**

a.CDS REST API t to create a new entry/row in the Project Table.

7.The Project Service must return the following to the UI Layer:

a.The JSON formatted project object in the body of the response.

b.Http response code 201 – created.


2.Update Project
---------------------
**Operation Name**
updateProject
**Trigger**
This API is called when the user request the update of an existing Project in his ML Workbench workspace. The project name, version or description may be changed with this call.
**Request**
{
   project:Project;//mandatory

}
**Response**
{
 project: Project

}

**Behavior**

1.The Project Service must check if the request JSON structure is valid, otherwise it should return

a.serviceStatus.status.SERVICE_STATUS=ERROR

b.serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

c.Send the response to client with Http response code – 4xx

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

a. serviceStatus.status.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Acumos User Id missing”.

c.Send response to client with Http response code – 4xx

3.Check requestor permissions: The Project Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the project (or in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:

a.status.SERVICE_STATUS =ERROR

b.statusMessage = “Permission denied”.

c.Send response to client with Http response code – 4xx.

4.Check if the project is archived: The Project Service must call CDS to check if the project is archived, and if so it should return:

a.status.SERVICE_STATUS =ERROR

b.statusMessage = “Update not allowed – project is archived”.

c.Send response to client with Http response code – 4xx.

5.Check if new Project name and version already exists: The Project Service must call Common Data Service (CDS) to make sure that the combination of the requested new project name and version provided  in the request does not already exist in the Workbench User Table for the specified authenticatedUserId requesting the project update, otherwise it must return:

a.serviceStatus.status.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Project name and version already exists”.

c.Send response to client with Http response code – 4xx Bad Request

**CDS Dependency:**

a.CDS REST API that returns a Boolean if the project name and version already exists (I.e. associated with the user in) Workbench User Table,

6.Create new entry in Workbench User Table: The Project Service must call Common Data Service (CDS) to check if the authenticatedUserId exists in the Workbench User Table. If it does not exist it must call CDS to create a new entry for the user in the Workbench User table as follows:

a.Populate the authenticatedUserId of the Workbench User Table row with the authenticatedUserId retrieved from the context of REST call.

b.Generate a UUID for this new (ML Workbench) user and populate in userId.uuid

c.Populate userId.IdentifierType = “USER”

d.Keep other fields such as userId.metrics and userId.version empty

**Note:** The above check is not required for an owner (because the owner is already created during create operation), but it is required if some other user (aka collaborator) issues an update request and collaborator may not exist in the Workbench User Table.

**CDS Dependency:**

a.CDS REST API that returns a Boolean if the authenticatedUserId exists in Workbench User Table. (Not required – we are using the Acumos USER Table)

b.CDS REST API to add a new row entry in the Workbench User Table which has a FK to an entry in the Identifier Table (Not required – we are using the Acumos USER Table)

**NOTE:** We can populate Workbench User Table piecemeal when a user creates a Project or we can populate this table before the Boreas Release by adding all user entries in the existing (Athena) User Table to this table – only authenticatedUserId, Identifier.uuid, and Identifier.IdentifierType will need to be added via a script.

7.Update the existing entry in Project Table: The Project Service must update the existing projectId.uuid entry in Project Table.

a.Populate the project name, if supplied in the request, into projectId.name

b.Populate the project version, if supplied in the request, into projectId.versionId.label

c.Populate the projectId.versionId.timestamp with the current timestamp.

d.Note that owner of the project is still the original project creator.

e.Populate the description with the project description provided in the REST call

f.(Note: Previous project name and version is overwritten and hence lost).

g.(May be we should save the old name/version in the project revision history – History Table)

**Note:** If this project was shared with other users, then the other user(s) will see the revised name and version.

**CDS Dependency:**

a.CDS REST API t to update an existing entry/row in the Project Table.

8.The Project Service must return:

a.JSON formatted Project Object as body of the response

b. Http response code 200 – OK.


3.Get Project
------------------
**Operation Name**
getProject
**Trigger**
This API is called when the user clicks on View Project (eye icon) on a project (under the project catalog space) in his ML Workbench workspace UI.
**Request**
{
   project:Project;//mandatory

}
**Response**
{
 project:Project;

}

**Behavior**

1.The Project Service must check if the request JSON structure is valid, otherwise it should return

a.serviceStatus.status.SERVICE_STATUS=ERROR

b.serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

c.Send the response to client with Http response code – 4xx Bad Request

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

a. serviceStatus.status.SERVICE_STATUS =ERROR
b.serviceStatus.statusMessage = “Acumos User Id missing”.

c.Send response to client with Http response code – 4xx Bad Request.

3.The Project Service must check that projectId.uuid is populated in the request otherwise it must return:

a. serviceStatus.status.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Project Id missing”.

c.Send response to client with Http response code – 4xx Bad Request

4.Check if the project is archived: The Project Service must call CDS to check if the project is archived, and if so it should return:

a.status.SERVICE_STATUS =ERROR

b.statusMessage = “Cannot open – project is archived”.

c.Send response to client with Http response code – 4xx.

5.The Project Service must return the following to the UI Layer:

a.The JSON formatted Project object for which the authenticatedUserId is the owner (with project name, version and description populated) in the body of the response.

b.Http response code 200 – OK.

**CDS Dependency:**

a.CDS REST API t to read and return the content of Project Table entry.


4.List Project
------------------
**Operation Name**
listProject
**Trigger**
This API is called when the user clicks on “Catalog” of “My Project” in his ML Workbench workspace UI.
**Request**
{
   user: User;//mandatory

}
**Response**
{
 projectList:Projects;

}

**Behavior**

1.The Project Service must check if the request JSON structure is valid, otherwise it should return

a.serviceStatus.status.SERVICE_STATUS=ERROR

b.serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

c.Send the response to client with Http response code – 4xx

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

a. serviceStatus.status.SERVICE_STATUS =ERROR

b.serviceStatus.statusMessage = “Acumos User Id missing”.

c.Send response to client with Http response code – 4xx

3.Retrieve all projects associated with the user.: The Project Service must:

a.Call CDS to retrieve all projects, active and archived both, associated (both owner and collaborator) with the user, which returns a list of all projects associated with the user. Each project object is populated with the project name, version, projectId.uuid, and description.

**CDS Dependency:**

CDS REST Call that returns a list of project objects (with project name, version and projectId.uuid, and description populated) associated with the user.

4.The Project Service must return the following to the UI Layer:

a.The list of JSON formatted Project objects for which the authenticatedUserId is the owner in the body of the response.

b.Http response code – 200 OK.


5.Archive Project
---------------------
**Operation Name**
archiveProject
**Trigger**
This API is called when the user request the archival of an existing Project in his project catalog in ML Workbench workspace. This operation only changes the artifactStatus field from ACTIVE to ARCHIVED.
**Request**
{
   project:Project;//mandatory

}
**Response**
{
 project:Project;

}

**Behavior**


1.The Project Service must check if the request JSON structure is valid, otherwise it should return

a.serviceStatus.SERVICE_STATUS=ERROR

b.serviceStatus.statusMessage = “Incorrectly formatted input – Invalid JSON”

c.Send the response to client with Http response code – 4xx

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

a. status.SERVICE_STATUS =ERROR

b.statusMessage = “Acumos User Id missing”.

c.Send response to client with Http response code – 4xx

3.Check if the requestor is the owner of the project Workbench or is authorized to delete the Project: The Project Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the project (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:

a.serviceStatus.SERVICE_STATUS =ERROR

b.serviceStatus .statusMessage = “Permission denied”.

c.Send response to client with Http response code – 4xx.

4.Mark the Project as Archived: The Project Service must call CDS to update the project in the Project Table as follows

a.Update the artifactStatus field of the project entry as “ARCHIVED”

5.Construct the JSON formatted Project object with serviceStatus.status=COMPLETED and artifactStatus as “ARCHIVED”

6.The Project Service must return:

a. JSON formatted project object as the
body of the response

b.Http response code 200.


6.Delete Project
---------------------
**Operation Name**
deleteProject
**Trigger**
This API is called when the user request the deletion of an existing Project in his project catalog in ML Workbench workspace. The project can only be deleted (i.e., purged) if it is in an ARCHIVED state.
**Request**
{
   project:Project;//mandatory

}
**Response**
{
 serviceState:ServiceState;

}

**Behavior**

1.The Project Service must check if the request JSON structure is valid, otherwise it should return

a.serviceStatus.SERVICE_STATUS=ERROR

b.serviceStatus.statusMessage = “Incorrectly formatted input – Invalid JSON”

c.Send the response to client with Http response code – 4xx

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

a.status.SERVICE_STATUS =ERROR

b.statusMessage = “Acumos User Id missing”.

c.Send response to client with Http response code – 4xx

3.Check if the requestor is the owner of the project Workbench or is authorized to delete the Project: The Project Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the project (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:

a.serviceStatus.SERVICE_STATUS =ERROR

b.serviceStatus .statusMessage = “Permission denied”.

c.Send response to client with Http response code – 4xx.

4.Delete the association (link) between the project and its child artifacts: The Project Service must call the CDS to retrieve all artifacts (notebooks and pipelines) currently associated with the projectId.uuid.

a.For each artifact associated with the project, the Project Service must delete the association between that artifact and the project, i.e. erase the projectId.uuid associated with that artifact in the entry in that artifact table.

**CDS Dependency:**

a.CDS REST API to add, delete and update an artifact entry in the (Notebook, Pipeline) artifact table.

5.Delete the Project entry in User Table: The Project Service must call the CDS to retrieve all users (both owner and collaborators) currently associated with the projectId.uuid.

a.For each user associated with the project, the Project Service must delete the association between the individual user and the project in the Workbench User Table.
Note: In Boreas there is no concept of a collaborator or the sharing of project.

**CDS Dependency:**

a.(Future Release) CDS REST API that returns all the users associated with projectId.uuid

b.CDS REST API to add, delete and update an entry in the Workbench User Table.

6.Delete the Project: The Project Service must call Common Data Service (CDS) to remove the projectId.uuid entry/row in the Project Table.

**CDS Dependency:**

a.CDS REST API that creates, updates and deletes an entry in the Project Table.

7.Construct the JSON formatted ServiceState object with serviceStatus.status=COMPLETED.

8.The Project Service must return:

a. JSON formatted project object as the body of the response

b.Http response code 200.

6. Share Project with User:
--------------------
**Operation Name**

 shareProject

**Trigger**

This API is called when the user request the to share his owned project to another user in ML Workbench workspace UI.

 **Request**

  {
     Users:collaborators (list of Users with user.userId.uuid and Role)
  }
 **Response**

  {
     Project:project (with list of collaborators)
  }

**Behavior**

1. The Project Service must check if the request JSON structure is valid, otherwise it should return

  a. serviceStatus.status.SERVICE_STATUS=ERROR
  b. serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
  c. Send the response to client with Http response code – 4xx NOT_FOUND

2. The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = “Acumos User Id missing”.
  c. Send the response to client with Http response code – 4xx NOT_FOUND

3. The Project Service must check that projectId.uuid is populated in the request otherwise it must return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = “Project Id missing”.
  c. Send response to client with Http response code – 4xx NOT_FOUND

4. Check if the user is exists in CDS : Project service must call the CDS and check whether user is present in cds otherwise it must return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Exception occured: User does not Exists "
  c. Send response to client with Http response code – 4xx NOT_FOUND

5. Check if the user is owner of the project: Project service must call CDS and check whether user is the owner of the project otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Permission denied"
  c. Send response to client with Http response code – 4xx NOT_FOUND

6. Check if user is ACTIVE or not : Project service must call CDS and check whether the user is active otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "User is not ACTIVE"
  c. Send response to client with Http response code – 4xx NOT_FOUND

7 . Check if Role is given by the user in input : Project service must check if the given input have roles to the user otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Roles not defined"
  c. Send response to client with Http response code – 4xx NOT_FOUND

8. Check if the related project is exists : Project service must call CDS and check if the given project with projectId  is exists in otherwise it should return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Project Specified Not found"
  c. Send response to client with Http response code – 4xx NOT_FOUND

9. Check if the collaborators is already exists : Project service must call couch db and check if collaborators already exists otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Collaborator already Exists"
  c. Send response to client with Http response code – 4xx NOT_FOUND

10. Share project with collaborator : Project service must access couch db and create a document in it with required details and must return the Project object with the user and role respectively otherwise it should return 

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Exception occured while saving in DB"
  c. Send response to client with Http response code – 4xx NOT_FOUND


7. Remove user from the Collaborator List:
----------------------------------------
**Operation Name**

removeCollaborator

**Trigger**

This API is called when the user request to remove the user from the collaborators from his owned project in ML Workbench workspace UI.

**Request**

  {
     Users:collaborators (list of Users with user.userId.uuid)
  }

**Response**

  {
     Project:project ( with list of updated collaborators after removal)
  }
**Behavior**

1.The Project Service must check if the request JSON structure is valid, otherwise it should return

  a. serviceStatus.status.SERVICE_STATUS=ERROR
  b. serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
  c. Send the response to client with Http response code – 4xx NOT_FOUND

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = “Acumos User Id missing”.
  c. Send the response to client with Http response code – 4xx NOT_FOUND

3.The Project Service must check that projectId.uuid is populated in the request otherwise it must return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = “Project Id missing”.
  c. Send response to client with Http response code – 4xx NOT_FOUND

4. Check if the user is exists in CDS : Project service must call the CDS and check whether user is present in cds otherwise it must return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Exception occured: User does not Exists "
  c. Send response to client with Http response code – 4xx NOT_FOUND

5. Check if the user is owner of the project: Project service must call CDS and check whether user is the owner of the project otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Permission denied"
  c. Send response to client with Http response code – 4xx NOT_FOUND

6. Check if user is ACTIVE or not : Project service must call CDS and check whether the user is active otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "User is not ACTIVE"
  c. Send response to client with Http response code – 4xx NOT_FOUND

7 . check if Role is given by the user in input : Project service must check if the given input have roles to the user otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Roles not defined"
  c. Send response to client with Http response code – 4xx NOT_FOUND

8. check if the related project is exists : Project service must call CDS and check if the given project with projectId  is exists in otherwise it should return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Project Specified Not found"
  c. Send response to client with Http response code – 4xx NOT_FOUND

9. check if the user is exists as a collaborator : Project service must check in couch db if the user exists as a collaborator otherwise it should return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "User is not a collaborator"
  c. Send response to client with Http response code – 4xx NOT_FOUND

10 . Remove user from collaborators list: Project service must call access couch db and get the related document details and remove the user from the collaborators list from couch db. After this successful execution, it must return 
Project object with updates collaborators with roles respectively otherwise it should return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Exception occured while finding the documents in couchDB"
  c. Send response to client with Http response code – 4xx NOT_FOUND

8. Get the list of shared Project for the logged in User:
---------------------------------------------------------
**Operation Name**
getSharedProjects

**Trigger**

This API is called when the user request to the projects which are shared with it in ML Workbench workspace UI.

**Request**

authenticatedUserId

**Response**

  {
     Project:project (with collaborators)
  }
**Behavior**
1.The Project Service must check if the request JSON structure is valid, otherwise it should return

  a. serviceStatus.status.SERVICE_STATUS=ERROR
  b. serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”
  c. Send the response to client with Http response code – 4xx NOT_FOUND

2.The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return :

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = “Acumos User Id missing”.
  c. Send the response to client with Http response code – 4xx NOT_FOUND

3.The Project Service must check that projectId.uuid is populated in the request otherwise it must return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = “Project Id missing”.
  c. Send response to client with Http response code – 4xx NOT_FOUND

4. Check if the user is exists in CDS : Project service must call the CDS and check whether user is present in cds otherwise it must return:

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Exception occured: User does not Exists "
  c. Send response to client with Http response code – 4xx NOT_FOUND

5. Get shareProjects for the logged in user: Project service must access couch db and get the list of projects that is been shared with this logged in user and returns the list of project object with the collaborators otherwise it should return : 

  a. serviceStatus.status.SERVICE_STATUS =ERROR
  b. serviceStatus.statusMessage = "Exception occured while finding the documents in couchDB "
  c. Send response to client with Http response code – 4xx NOT_FOUND
