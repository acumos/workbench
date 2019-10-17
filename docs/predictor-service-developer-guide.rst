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
ML(Machine Learning) Workbench Predictor Service Developer Guide
=================================================

1.    Overview
=================

         This is the developers guide to ML Workbench Predictor Service.

1.1. What is ML Workbench Predictor Service\?
---------------------------------------------

    ML Workbench Predictor Service expose API to allow to perform CRUD operation on Predictor in ML Workbench.

2. Technology and Frameworks
=============================
  **List of the development languages, frameworks, etc.**

  #. Springboot 2.1.7.RELEASE
  #. Java 11
  #. Maven 4.0.0
  #. Jackson 2.7.5
  #. JUnit 4.12

3.    Project Resources
==========================

- Gerrit repo: `workbench/predictor-service <https://gerrit.acumos.org/r/#/admin/projects/workbench>`_
- `Jira <https://jira.acumos.org/browse/ACUMOS-3492>`_  Predictor Service API

4. Development Setup
=====================

    1. Clone or download code from "**Gerrit repo**" mentioned above.

    2. Import prdicotr-service Project in IDE (viz., Eclipse or STC)

    3. Once successfully imported, set the required properties in application.properties file.

    4. Run as Springboot application.

    5. Access using **Swagger UI** : http://localhost:9096/mlWorkbench/v1/predictor/swagger-ui.html#

    6. Once you get the Swagger UI, click Authorize button and provide JWT token as below :
        Bearer <JWT token for Acumos User>

        **Note:** JWT token value can be obtained after successful login in Acumos.

    7. After successfully setting Authorize value, API are available to access.  Following are the sample inputs :

        1. Associate Predictor to a Project

            "**authenticatedUserId**" : <Acumos User login Id>

            "**predictorProjAssociation**" :
                {
                    "userId": "<Acumos User Login Id>",
					
                    "solutionId": "<Solution UUID",
					
                    "revisionId": "<Revision UUID>",
					
                    "modelStatus": "ACTIVE",
					
                    "predictorDeploymentStatus": "ACTIVE",
					
                    "predictorName": "<Predictor Name>",
					
                    "predictorkey":"<Predictor Unique Key>",
					
                    "environmentPath": "<URL to access Predictor>",
					
                    "metadata1": "MetaData2",
					
                    "metadata2": "MetaData2"
					
               }
            
            "**projectId**" : <Project Id>
			

        2. Get Predictors associated to a Project

            "**authenticatedUserId**" : <Acumos User login Id>

            "**modelId**" : <Solution UUID>

            "**version**" : <Solution version>
			

        3. Edit Predictor association to a Project

            "**associationData**" :
                {
                    "userId": "<Acumos User Login Id>",
					
					"projectId": "<Project UUID>",
					
                    "solutionId": "<Solution UUID",
					
                    "revisionId": "<Revision UUID>",
					
                    "preidctorId": "<Predictor UUID>",
					
                    "predictorName": "<Predictor name>",
					
					"environmentPath": "<URL to access Predictor>",
					
                    "predictorkey":"<Predictor Unique Key>",
					
               }

            "**associationId**" : <Predictor Project Association UUID>
			
            "**authenticatedUserId**" : <Acumos User login ID>

            "**predictorId**" : <Predictor UUID associated to Project>


		4. Delete Predictor Project association

            "**associationId**" : <Predictor Project Association UUID >
			
			"**authenticatedUserId**" : <Acumos User login Id>
            
			
			
        5. Get Predictor Details for given input Model

            "**authenticatedUserId**" : <Acumos User login Id>

            "**projectId**" : <Project UUID>
