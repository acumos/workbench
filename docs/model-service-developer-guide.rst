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

=================================================
ML(Machine Learning) Workbench Model Service Developer Guide
=================================================

1.    Overview
=================

         This is the developers guide to ML Workbench Model Service.

1.1. What is ML Workbench Model Service\?
---------------------------------------------

    ML Workbench Model Service expose API to allow to perform CRUD operation on Associated Models in ML Workbench.

2. Technology and Frameworks
=============================
  **List of the development languages, frameworks, etc.**

  #. Springboot 2.1.7.RELEASE
  #. Java 11
  #. Maven 4.0.0
  #. Jackson 2.7.5
  #. JUnit 4.12
  #. LightCouch 0.2.0

3.    Project Resources
==========================

- Gerrit repo: `workbench/model-service <https://gerrit.acumos.org/r/#/admin/projects/workbench>`_
- `Jira <https://jira.acumos.org/browse/ACUMOS-3177>`_  ML Workbench Model Mapping Service

4. Development Setup
=====================

    1. Clone or download code from "**Gerrit repo**" mentioned above.

    2. Import model-service Project in IDE (viz., Eclipse or STC)

    3. Once successfully imported, set the required properties in application.properties file.

    4. Run as Springboot application.

    5. Access using **Swagger UI** : http://localhost:9091/mlWorkbench/v1/modelservice/swagger-ui.html#/

    6. Once you get the Swagger UI, click Authorize button and provide JWT token as below :
        Bearer <JWT token for Acumos User>

        **Note:** JWT token value can be obtained after successful login in Acumos.

    7. After successfully setting Authorize value, API are available to access.  Following are the sample inputs :

        1. List out all the Models that belongs to user

            "**authenticatedUserId**" : <Acumos User login Id>
			

        2. List out all the Models that belongs to User under the Project

            "**authenticatedUserId**" : <Acumos User login Id>

            "**projectId**" : <Project UUID>
			

        3. Associate's the Model to the Project

            "**authenticatedUserId**" : <Acumos User login ID>

            "**projectId**" : <Project UUID>
			
			"**modelId**" : <Model UUID>
			
			"**model**" :
					{
						"modelId": {
							"versionId": {
								"label": "1"
							},
							
							"metrics": {
							  "kv": [
									 {
										"key": "MODEL_TYPE_CODE",
										"value": "<XYZ>"
									 },
									
									 {
										"key": "MODEL_PUBLISH_STATUS",
										"value": "true"
									 },
									
									 {
										"key": "CATALOG_NAMES",
										"value": "xyz"
									 }
								 ]
							 }
						 }
					 }


		4. Update the Model Association with Project
			
			"**authenticatedUserId**" : <Acumos User login Id>
			
			"**projectId**" : <Project UUID>
			
			"**modelId**" : <Model UUID>
			
			"**model**" :
					{
						"modelId": {
							"versionId": {
								"label": "1"
							},
							
							"metrics": {
							  "kv": [
									  {
										"key": "MODEL_TYPE_CODE",
										"value": "<XYZ>"
									  },
									
									  {
										"key": "MODEL_PUBLISH_STATUS",
										"value": "true"
									  },
									
									  {
										"key": "CATALOG_NAMES",
										"value": "<XYZ>"
									  },
									 
									  {
										"key": "ASSOCIATION_ID",
										"value": "<UUID>"
									 }
								  ]
							  }
						  }
					  }
            
			
        5. Delete the Model Association with Project in ML Workbench

            "**authenticatedUserId**" : <Acumos User login Id>
			
			"**projectId**" : <Project UUID>
			
			"**modelId**" : <Model UUID>
			
			"**model**" :
					{
						"modelId": {
							"versionId": {
								"label": "1"
							},
							
							"metrics": {
							  "kv": [
									 {
										"key": "MODEL_TYPE_CODE",
										"value": "<XYZ>"
									 },
									
									 {
										"key": "MODEL_PUBLISH_STATUS",
										"value": "true"
									 },
									
									 {
										"key": "CATALOG_NAMES",
										"value": "<XYZ>"
									 },
									 
									 {
										"key": "ASSOCIATION_ID",
										"value": "<UUID>"
									}
								 ]
							 }
						 }
					 }
					 

					 