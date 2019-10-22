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
ML Workbench Pipeline Service Engine Application Programming Interfaces
====================================================================


API
====

1.    Create Pipeline
--------------------
    **Operation Name**
        createPipeline
    **Trigger**
        This API is called when the user:

        1.    Clicks on the “Create Pipeline” icon under the Pipeline folder under the user’s Project space in ML Workbench, or,

        2.    Clicks on the “Create Pipeline” icon under the Pipeline folder  in the user’s ML Workbench space (outside the project)

    **Request**
        {
           pipeline:Pipeline;//mandatory

        }
    **Response**
        {
         pipeline:Pipeline;

        }

    **Behavior**

        1.    The Pipeline Service must check if the request JSON structure is valid, otherwise it should return

            a.    serviceStatus.status.SERVICE_STATUS=ERROR

            b.    serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

            c.    Send the response to client with Http response code – 4xx

        2.    The Pipeline Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Acumos User Id missing”.

            c.    Send response to client with Http response code – 4xx

        3.    The Pipeline Service must check that pipeline name is provided in the request otherwise it must return:

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Pipeline Name missing” or “Invalid Pipeline name” or “Invalid Pipeline version”

            c.    Send response to client with Http response code – 4xx

            **Validation:**

            a.    Pipeline name may contain alphanumeric characters and “_” character – no other any other characters are allowed. The name must start with an alpha character.

            b.    Pipeline version is optional. It is a string which can contain alphanumeric characters, zero or more “_” character, and zero or more period character. The version must start with a numeric character. Other characters are not allowed.

        4.    Check pipeline type is provided in the request: The Pipeline Service must check that pipeline type is provided in the request and it is one of the pre-defined types (defined below) otherwise it must return

            a.    serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Missing mandatory field – pipeline type” or  “Invalid pipeline type provided”

            c.    Send response to client with Http response code – 4xx

            **Note:** The following pipeline types are supported:

                a.    Jupyter

                b.    Zeppelin

        5.    Check if project exists: If projectId.uuid is included in the request, then the Pipeline Service must call Common Data Service (CDS) to make sure that the projectId.uuid exists in the Project Table, otherwise it must return:

            a.    serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “ProjectId does not exist.

            c.    Send response to client with Http response code – 4xx

        6.    Check if Pipeline name and version already exists in the user’s pipeline space: The Pipeline Service must call Common Data Service (CDS) to make sure that the combination of the pipeline name and version provided  in the request does not already exist in the User Table for the specified authenticatedUserId requesting the pipeline creation, otherwise it must return:

            a.    serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Pipeline name and version already exists for this user”.

            c.    Send response to client with Http response code – 4xx

            **CDS Dependency:**

            a.    CDS REST API that returns a Boolean if the specified pipeline name and version already exists (I.e. associated with the user in)

        7.    Add the user (i.e., authenticatedUserId) in the (Workbench) User Table: The Pipeline service must add this user requesting the creation of pipeline to the Workbench User Table if the user is not already populated in the table.

            a.    Check if the authenticatedUserId is present in the Workbench User Table (the project owner should be in the Boreas User Table but if the user requesting the creation of the pipeline is not the project owner rather is a project collaborator then his authenticatedUserId may not be in the Workbench User Table)).

            b.    Generate a UUID for this new (ML Workbench) user and populate in userId.uuid, if not already populated

            c.    Populate userId.IdentifierType = “USER”, if not already populated.

            d.    Keep other fields such as userId.metrics and userId.version empty

            **CDS Dependency:**

            a.    CDS REST API that returns a Boolean if the authenticatedUserId exists in Workbench User Table.

            b.    CDS REST API to add a new row entry in the Workbench User Table.

            c.    CDS REST API to update an entry in Workbench User Table.

        8.    Create new Pipeline: The Pipeline Service must create a new entry in the Pipeline Table

            a.    Generate a new uuid for the Pipeline.

            b.    Populate the above uuid into pipelineId.uuid

            c.    Populate the pipeline name supplied in the request into pipelineId.name

            d.    Populate the pipeline version, if supplied in the request, into pipelineId.versionId.label

            e.    Populate the pipelineId.identiierType = PIPELINE

            f.    Populate the pipelineId.versionId.timestamp with the current timestamp

            g.    Populate the owner field of the Pipeline table with the reference to the authenticatedUserId row in the (Workbench) User Table [The owner field is a FK to the User Table].

            h.    Populate the pipelineType with the pipeline type information provided in the request.

            i.    Populate the description with the pipeline description provided in the REST call.

            j.    Create the JSON formatted Pipeline object.

        9.    Associate the user with the Pipeline: The Pipeline Service must populate the owner field of the Pipeline Table with the reference to this user entry in the Workbench User Table.

            **CDS Dependency:**

            a.    CDS REST API that updates an entry in the Pipeline table.

        10.    Add the pipeline to the (Workbench) User Table: The Pipeline Service must populate the pipelines field of the User Table with the reference to the new Pipeline record/entry just created in the Pipeline table. Note that a User may have multiple pipelines associated with him.

        11.    Add the pipeline to the Project Table: If projectId.uuid was provided in the request, then the Pipeline Service must populate the pipelines field in the Project Table with the reference to the new Pipeline record/entry just created in the Pipeline table.

        12.    Add the location of pipeline in Git Repository to the Pipeline table: The Pipeline Service must populate the pipelineId.repositoryUrl field of the Pipeline table with the relative URL of the pipeline where it will be stored in Git repository.

            **Note:** In Git, the pipelines are identified by their pipelineId.uuid. Since a pipeline may belong to multiple projects and also to multiple users, the pipeline file will be stored in Git at the following path:

            a.    /pipelines/pipelineId.uuid ((make sure to delete this path when deleting the pipeline))

        13.    The Pipeline Service must return the following to the UI Layer:

            a.    The JSON formatted Pipeline object in the body of the response.

            b.    Http response code 201 – created.

            **Note:** The Pipeline is not yet launched. When the user clicks on the “Launch Pipeline” icon in the Pipeline space, then the Jupyter Pipeline server will be created and the pipeline is launched in a separate browser tab.


2.    Launch (Get) Pipeline
--------------------
    **Operation Name**
        launchPipeline
    **Trigger**

        This API is called when the user:

            1.    Clicks on the “Launch Pipeline” icon in the Pipeline folder (space) under the user’s Project in ML Workbench, or,

            2.    Clicks on the “Launch Pipeline” icon in the user’s ML Workbench space, outside of any project.


    **Request**
        {
           pipeline:Pipeline;//mandatory

        }
    **Response**
        {
         pipeline:Pipeline;

        }

    **Behavior**

        1.    The Pipeline Service must check if the request JSON structure is valid, otherwise it should return

            a.    serviceStatus.status.SERVICE_STATUS=ERROR

            b.    serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

            c.    Send the response to client with Http response code – 4xx

        2.    The Pipeline Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Acumos User Id missing”.

            c.    Send response to client with Http response code – 4xx (404)

        3.    The Pipeline Service must check that pipelineId.uuid entry exists in the request body otherwise it must return:

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Pipeline Id missing”

            c.    Send response to client with Http response code – 4xx (404)

        4.    Check if the pipeline is archived: The Pipeline Service must call CDS to check if the pipeline is archived, and if so it should return:

            a.    status.SERVICE_STATUS =ERROR

            b.    statusMessage = “Cannot launch  – pipeline is archived”.

            c.    Send response to client with Http response code – 4xx

        5.    The Pipeline Service must check that the requested pipelineId.uuid exists in the Pipeline table, otherwise it must return:

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Pipeline not found”

            c.    Send response to client with Http response code – 4xx(404)

        6.    Check if the user is authorized to launch the pipeline: The Pipeline service must check if the user is the owner of the pipeline (or in future release it must check if the user is otherwise authorized by the Permission table to perform such an action), otherwise it must return:

            a.    serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Permissions denied”

            c.    Send response to client with Http response code – 4xx

        7.    Call JupyterHub Server to start an instance of the Pipeline Server for the user: The Pipeline Service must:

            a.    Check if the user specific Pipeline Server instance is already running.

            b.    If not, call the JupyterHub Server to start a user specific Pipeline Server instance

            c.    The Pipeline Service must populate pipelineId.serviceUrl field with above URL.

            d.    The Pipeline Service must create a JSON formatted pipeline object with the URL populated.

        8.    The Pipeline Service must retrieve the pipelineId.repositoryUrl field (which was populated during create pipeline operation) from the Pipeline table and pass this to Pipeline Server so that when the user presses SAVE in the pipeline page the Pipeline Server stores the pipeline file at that url in Git repository. (discuss with Mukesh)

        9.    The Pipeline Service must return the following to the UI Layer:

            a.    The JSON formatted pipeline object in the body of the response.

            b.    Http response code 200 – OK


3.    List Pipelines
--------------------
    **Operation Name**
        ListPipelines
    **Trigger**
        This API is called when the user clicks on “My Pipelines” catalog in his ML Workbench User space or when the user clicks on the “My Pipelines” catalog under a particular project.

    **Request**
        {
           user:User;//mandatory

        }
    **Response**
        {
         pipelineList:Pipelines;

        }

    **Behavior**

        1.    The Pipeline Service must check if the request JSON structure is valid, otherwise it should return

            a.    serviceStatus.status.SERVICE_STATUS=ERROR

            b.    serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

            c.    Send the response to client with Http response code – 4xx (404)

        2.    The Project Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Acumos User Id missing”.

            c.    Send response to client with Http response code – 4xx (404)

        3.    Check if the user is authorized to request this operation: The Pipeline service must check if the user is authorized by the Permission table to perform such an action), otherwise it must return:

            a.    serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Permissions denied”

            c.    Send response to client with Http response code – 4xx

            **Note:** The test is out of Boreas scope.

        4.    Check if the Project Id exists: If projectId.uuid is populated then the Pipeline service must call CDS to check if the project exists in the Project Table, otherwise it must return:

            a.    serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Project Id does not exists”

            c.    Send response to client with Http response code – 4xx (404)

        5.    Retrieve all pipelines associated with the user and the project: The Pipeline Service must:

            a.    Call CDS to retrieve all pipelines, active and archived, associated (both owner and collaborator) with the user and if the projectId.uuid is also populated in the request to retrieve a list of pipelines associated with the given user and project. Each pipeline object is populated with the pipeline name, version, pipelineId.uuid, description, pipelineType and kernelType

            b.    Create a list of JSON formatted Pipeline objects with the above information populated.

            **CDS Dependency:**

            a.    CDS must implement a REST Call that returns a list of pipeline object objects (populated with the above information) associated with the user.

            b.    CDS must implement a REST Call that returns a list of pipeline object objects (populated with the above information) associated with a given user and project.

        6.    The Pipeline  Service must return the following to the UI Layer:

            a.    The list of JSON formatted Pipeline objects in the body of the response.

            b.    Http response code – 200 OK.


4.    Update Pipeline
--------------------
    **Operation Name**
        updatePipelines
    **Trigger**
        This API is called when the user request the update of an existing Pipeline in his ML Workbench workspace. The pipeline name, version or description may be changed with this call.

    **Request**
        {
           pipeline:Pipeline;//mandatory

        }
    **Response**
        {
         pipeline:Pipeline;

        }

    **Behavior**

        1.    The Pipeline Service must check if the request JSON structure is valid, otherwise it should return

            a.    serviceStatus.status.SERVICE_STATUS=ERROR

            b.    serviceStatus.statusMessage= “Incorrectly formatted input – Invalid JSON”

            c.    Send the response to client with Http response code – 4xx

        2.    The Pipeline Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

            a.     serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Acumos User Id missing”.

            c.    Send response to client with Http response code – 4xx (404)

        3.    Check requestor permissions: The Pipeline Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the pipeline (or in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it must return:

            a.    status.SERVICE_STATUS =ERROR

            b.    statusMessage = “Permission denied”.

            c.    Send response to client with Http response code – 4xx.

        4.    Check if the pipeline is archived: The Pipeline Service must call CDS to check if the pipeline is archived, and if so it should return:

            a.    status.SERVICE_STATUS =ERROR

            b.    statusMessage = “Update not allowed – pipeline is archived”.

            c.    Send response to client with Http response code – 4xx

        5.    Check if new Pipeline name and version already exists for the user: The Pipeline Service must call Common Data Service (CDS) to make sure that the combination of the requested new pipeline name and version provided  in the request does not already exist for the authenticatedUserId in the Workbench User Table, otherwise it must return:

            a.    serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Pipeline name and version already exists for user”.

            c.    Send response to client with Http response code – 4xx

            **CDS Dependency:**

            a.    CDS must implement a REST API that returns a Boolean if the pipeline name and version already exists, i.e. associated with the user in Workbench User Table,

        6.    Check if project id exist: If projectId.uuid is provided in the request object then check if this project exist. Call CDS to check if projectId.uuid exist in Project table. If it does then return the following

            a.    serviceStatus.status.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Project Id is invalid”.

            c.    Send response to client with Http response code – 4xx (404)

        7.    Assign the pipeline to an existing project: If the projectId.uuid is populated in the request object and if the pipeline is not part of any existing project then assign this pipeline to the requested projectId.uuid

            a.    Call CDS to check if the pipeline is part of any other project. CDS will return a project UUID. If this returned project Id matches the one that was provided in the request object, then it is not a request to assign the pipeline to a project – may be a request to update the name or version or description.

            b.    If CDS returns a null project Id then it is a request to assign the pipeline to a project.

            c.    Call CDS to add the pipelineId.uuid to the Project Table.

            **CDS Dependency:**

            a. CDS should expose a REST API to check if the project Id is valid

            b. CDS should expose a REST API to return the projectId.uuid with which a pipelineId.uuid is associated with.

        9.    Update the Pipeline table with the user : Add the user as the collaborator of Pipeline.

        10.    Update the existing entry in Pipeline Table: The Project Service must update the existing pipelineId.uuid entry in Pipeline Table.

            a.    Populate the pipeline name, if supplied in the request, into pipelineId.name

            b.    Populate the pipeline version, if supplied in the request, into pipelineId.versionId.label

            c.    Populate the pipelineId.versionId.timestamp with the current timestamp.

            d.    Note that owner of the pipeline is still the original pipeline creator.

            e.    Populate the description with the pipeline description provided in the REST call

            f.    (Note: Previous pipeline name and version is overwritten and hence lost). (May be we should save the old name/version in the Pipeline revision history – History Table)

            **Note:** If this pipeline was shared with other users, then the other user(s) will see the revised name and version.

        11.    The Pipeline Service must return:

            a.    JSON formatted Pipeline Object in body of the response

            b.     Http response code 200 – OK.


5.    Archive Pipeline
--------------------
    **Operation Name**
        archivePipelines
    **Trigger**
        This API is called when the user request the archival of an existing Pipeline in his pipeline catalog in (either under the Project or Users Pipeline folder) in ML Workbench.

    **Request**
        {
           pipeline:Pipeline;//mandatory

        }
    **Response**
        {
         pipeline:Pipeline;

        }

    **Behavior**

        1.    The Pipeline Service must check if the request JSON structure is valid, otherwise it should return

            a.    serviceStatus.SERVICE_STATUS=ERROR

            b.    serviceStatus.statusMessage = “Incorrectly formatted input – Invalid JSON”

            c.    Send the response to client with Http response code – 4xx

        2.    The Pipeline Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

            a.     serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Acumos User Id missing”.

            c.    Send response to client with Http response code – 4xx (404)

        3.    Check if the requestor is the owner of the pipeline or is authorized to archive the pipeline: The Pipeline Service must call CDS to check if the requestor (i.e., authenticatedUserId) is the owner of the project (in later releases must check the Permissions table if the requestor is allowed to perform this action). If not it just return:

            a.    serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Permission denied”.

            c.    Send response to client with Http response code – 4xx.

        4.    Check if the Pipeline is referenced by other Users or in Other Projects: The Pipeline service must check if this pipelineId.uuid is referenced (i.e., in use) by any other users by following the links to the Pipelines in each entry of the User Table. If yes it must return:

            a.    serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Pipeline is referenced by other users / projects”.

            c.    Send response to client with Http response code – 4xx
            Note: This check is out of scope of Boreas Release – because artifact sharing is out of scope.

        5.    Mark the Pipeline “Archived”: The Pipeline Service must call the CDS to update the artifactStatus of the pipeline to “Archived”.

            **CDS Dependency:**

            a.    CDS must implement a REST API to add, delete and update an artifact entry in the (Pipeline, Pipeline, Solution, etc.) artifact table.

        6.    Construct a JSON formatted Pipeline object with serviceStatus.status=COMPLETED and artifactStatus = ARCHIVED.

        7.    The Pipeline Service must return:

            a.    Pipeline object as the body of the response

            b.    Http response code 200.
			
6.    Delete Project Pipeline Association
------------------------------------------
    **Operation Name**
        deleteProjectPipelineAssociation
    **Trigger**
        This API is called when the user delete the associated pipeline of a project in the project catalog in ML Workbench.

    **Request**
        {
           authenticatedUserId:Acumos User Login Id;//mandatory

	   projectId:ProjectId //mandatory

	   pipelineId:PipelineId //mandatory

        }
    **Response**
        {
         servicestate:ServiceState;

        }

    **Behavior**

        1.    The Pipeline Service must retrieve the authenticatedUserId from the context of the REST call (the REST Header contains the authenticatedUserId) and make sure it is populated otherwise it must return

            a.     serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Acumos User Id missing”.

            c.    Send response to client with Http response code – 4xx (404)

        2.    Check if the requester is the owner of the pipeline : The Pipeline Service must call CDS to check if the requester (i.e., authenticatedUserId) is the owner of the project (in later releases must check the Permissions table if the requester is allowed to perform this action). If not it just return:

            a.    serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Permission denied”.

            c.    Send response to client with Http response code – 4xx.

        3.    Check if the Pipeline is exists in CDS or not, If not then it must return:

            a.    serviceStatus.SERVICE_STATUS =ERROR

            b.    serviceStatus.statusMessage = “Requested Pipeline Not found”.

            c.    Send response to client with Http response code – 4xx

        5.    Delete the Project Pipeline Association : The Pipeline Service must call the CDS to drop the Association between the Project and Pipeline.

            **CDS Dependency:**

            a.    CDS must implement a REST API to drop the association between Project and Pipeline.

        6.    Construct a JSON formatted ServiceState object with serviceStatus.status=COMPLETED and the corresponding message as serviceStatus.statusMessage=Project Pipeline Association Deleted successfully.

        7.    The Pipeline Service must return:

            a.    ServiceState object as the body of the response

            b.    Http response code 200.
