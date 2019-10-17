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
ML Workbench Predictor Service Engine Application Programming Interfaces
====================================================================


API
====

1.   Associate Predictor to a Project (create association)
-------------------------------------------------------------
		**Method**
			POST
		**Context Path** 
			 /mlWorkbench/v1/predictor/users/{authenticatedUserId}/projects/{projectId}/predictors
		**Trigger**
			This API will be invoked when the user selects appropriate Predictor and clicks on Associate Predictor button under the user’s project details space in ML workbench.
		**Operation Name**
			associatePredictorToProject
			 
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id
				predictorId : predictor.predictorId.uuid  //Only if predictor already exists
				projectId : project.projectId.uuid
			}
			
		**Response**
			{
				Predictor : PredictorVO (with KVPair containing "associationId" key and corresponding value.) 
			}
			
			HTTPStatus : 201 
			
		**Error/Exception**
			Predictor VO with Service Status error Message : "Not able to associate Predictor to a Project"
			
			HTTPStatus : 4xx
		
		**Behavior**
			1) Input Validation
				Mandatory Field Check
					
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			2) Existence Validation
				Check if authenticated User exists 
				
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if Project for input ProjectId exists (i.e., status Active)
				
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if Predictor for input PredictorId exists (with status Active)
				
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			3) Access Validation
				Check if logged in user has access to the input ProjectId
				
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if logged in user has access to the input Predictor
				
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			4) Restriction : 
				Check if model of the selected predictor is associate to a Project 
				
					If not : 
						Error Message : "Cannot Associate Predictor as corresponding model is not associated to a Project"
						
						HTTPStatus : As per the existing implementation

				Predictor deployment status should be Active.  User should not be allowed to associate Predictor to a Project if predictor deployment status is other than Active.
					
					If deployment status is not Active : 
					
					Error Message : "Predictor is not in Active state so cannot associate to a Project"
					
					HTTPStatus : As per the existing implementation

			5) Check if PredictorId is not present in input then create new Predictor in CouchDB with "Active" state. 
			
			6) Insert PredictorProjectAssociation in CouchDB with status "Active"

			7) Return Predictor VO  (with KVPair containing "associationId")  along with HTTPStatus 201. 
			

2.   Get Predictors associated to a Project
------------------------------------------------
		**Method**
			GET
		**Context Path** 
			/mlWorkbench/v1/predictor/users/{authenticatedUserId}/projects/{projectId}/predictors
		**Trigger**
			 This API will be invoked when user clicks on any project tile, on project-catalog page in ML workbench, to view project details.
		**Operation Name**
			getPredictorsAssociatedToProject
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id
				projectId : project.projectId.uuid
			}
			
		**Response**
			{
				PredictorVOs : List of PredictorVO 
			}
			
			HTTPStatus : 200
			
		**Error/Exception**
			As per the existing implementation and message : "Not able to fetch associated Predictors"
			
			HTTPStatus : 4xx
		
		**Behavior**
			1) Input Validation
				Mandatory Field Check

					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			2) Existence Validation
				Check if authenticated User exists 

					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if Project for input ProjectId exists (with status Active)
				
					On Failure :
						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			3) Access Validation
				Check if logged in user has access to the input ProjectId.  For now check if user is owner of the Project.

					On Failure :
						Error Message : As per the existing implementation

						HTTPStatus : As per the existing implementation



			4) Get the PredictorProjectAssociation from CouchDB with status "Active"

			5) For each associated Predictor and version check if the corresponding deployment is available (i.e., deployed K8s environment is not down) <future enhancement> : 

				If in case deployment is down, then need to update the Predictor Deployment status Error/Failed, update all PredictorProjectAssociation status as Invalid in Couch DB for respective predictorId and version and in current list of PredictorProjectAssociation. 

			6) For PredictorProjectAssociation construct Predictor VO (with KVPair containing "associationId") and add to the list.  Return the Predictor VO list along with HTTPStatus 200. 
			

3.   Edit Predictor association to a Project
------------------------------------------------
		**Method**
			PUT
		**Context Path** 
			/mlWorkbench/v1/predictor/users/{authenticatedUserId}/predictors/{predictorId}/associations/{associationId}
		**Trigger**
			 This API will be invoked when user clicks on edit Predict Project Association button for the associated predictor actions and edits the predictor details.
		**Operation Name**
			modifyPredictorAssociationToProject
		
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				predictorPorjectAssociationId : PredictorProjectAssociation.associationId.uuid,
				predictorId : Predictor.predictorId.uuId
			}
			
		**Response**
			{
				PredictorProjectAssociation : with new details 
			}
			
			HTTPStatus : 200
			
		**Error/Exception**
			PredictorProjectAssociation VO with Service Status error Message : "Not able to associate Predictor to a Project"
			
			HTTPStatus : 4xx
		
		**Behavior**
			1) Input Validation
				Mandatory Field Check

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			2) Existence Validation

				Check if authenticated User exists 

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if Project for input ProjectId exists (i.e., status Active)

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if Predictor for input PredictorId exists (with status Active)

					On Failure :

						Error Message : As per the existing implementation

						HTTPStatus : As per the existing implementation

			3) Access Validation

				Check if logged in user has access to the input ProjectId.  For now check if user is owner of the Project.

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if logged in user has access to the input Predictor.  Either user has to be owner or collaborator of the Predictor's model. <Need to check further>

					On Failure :

						Error Message : As per the existing implementation

						HTTPStatus : As per the existing implementation

			4) Restriction : 
				Check if model of the selected predictor is associate to a Project

					If not : 

						Error Message : "Cannot Associate Predictor as corresponding model is not associated to a Project"

						HTTPStatus : As per the existing implementation

				Predictor deployment status should be Active.  User should not be allowed to associate Predictor to a Project if predictor deployment status is other than Active.

					If deployment status is not Active : 

						Error Message : "Predictor is not in Active state so cannot associate to a Project"

						HTTPStatus : As per the existing implementation

			5) Update Predictor in CouchDB with status "Active"
			
			6) Update PredictorProjectAssociation  for the input AssociationId in CouchDB with status "Active"

			7) Construct the Predictor VO ( with KVPair containing "associationId") from PredictorProjectAssociation.  Return Predictor VO along with HTTPStatus 200. 

			
4.   Delete Predictor Project association
------------------------------------------------
		**Method**
			DELETE
		**Context Path** 
			/mlWorkbench/v1/predictor/users/{authenticatedUserId}/predictors/associations/{associationId}
		**Trigger**
			 This API will be invoked when user clicks on the Delete Predictor Project Association button for the associated predictor actions.
		**Operation Name**
			deletePredictorAssociation
				
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				predictorPorjectAssociationId : PredictorProjectAssociation.associationId.uuid
			}
			
		**Response**
			{
				ServiceStatus with success message. 
			}
			
			HTTPStatus : 200
			
		**Error/Exception**
			ServiceStatus with error Message : "Not able to delete specified Predictor Project association"
			
			HTTPStatus : 4xx
		
		**Behavior**
			1) Input Validation

				Mandatory Field Check

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			2) Existence Validation

				Check if authenticated User exists 

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if PredictorProjectAssociation for input AsscoaitionId exists 

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			3) Access Validation

				Check if logged in user has access to the input AssociationId.  For now check if user is owner of the Association

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			4) Delete PredictorProjectAssociation  for the input AssociationId

			6) Return ServiceStatus along with HTTPStatus 200.
			
			
5.   Get Predictor Details for given input Model
--------------------------------------------------
		**Method**
			GET
		**Context Path** 
			/mlWorkbench/v1/predictor/users/{authenticatedUserId}/models/{modelId}/version/{version}
		**Trigger**
			 This API will be invoked after user selects one of model and version associated to a Project.
		**Operation Name**
			getPredictorDetails
				
		**Request**
			JWT authentication token (in request header)
			
			{
				authenticatedUserId : User login Id,
				modelId : solutionId  //model UUID.
				version : version //version of the model.
			}
			
		**Response**
			{
				Predictor : Predictor VO with required details.  
			}
			
			HTTPStatus : 200
			
		**Error/Exception**
			As per the existing implementation and message : "No Predictor Found"
			
			HTTPStatus : 4xx
		
		**Behavior**
			1) Input Validation

				Mandatory Field Check

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			2) Existence Validation

				Check if authenticated User exists 

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

				Check if the input model and version exists in CDS

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			3) Access Validation

				Check if logged in user has access to the input Model and Version.  For now check if user is owner of the Model:version

					On Failure :

						Error Message : As per the existing implementation
						
						HTTPStatus : As per the existing implementation

			4) Get the predictor details from Couch DB for the specified Model Id and version.

			6) Return Predictor with details if found for the input model Id and version or empty Predictor.