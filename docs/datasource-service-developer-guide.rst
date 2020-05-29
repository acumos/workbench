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

======================================================================
ML(Machine Learning) Workbench DataSource Service Developer Guide
======================================================================

1.  Overview
=================

         This is the developers guide to ML Workbench DataSource Service.

1.1. What is ML Workbench DataSource Service\?
---------------------------------------------

    ML Workbench DataSource Service expose API to allow to perform CRUD operation on DataSource in ML Workbench.

2. Technology and Frameworks
=============================
  **List of the development languages, frameworks, etc.**

  #. Springboot 2.1.8.RELEASE
  #. Java 11
  #. Maven 4.0.0
  #. lightcouch 0.2.0
  #. JUnit 4.12

3.    Project Resources
==========================

- Gerrit repo: `workbench/datasource-service <https://gerrit.acumos.org/r/#/admin/projects/workbench>`_
- `Jira <https://jira.acumos.org/browse/ACUMOS-4074>`_  Datasource support in Acumos
- `Jira <https://jira.acumos.org/browse/ACUMOS-4075>`_  Create new MS : Datasource

4. Development Setup
=====================

    1. Clone or download code from "**Gerrit repo**" mentioned above.

    2. Import datasource-service Project in IDE (viz., Eclipse or STC)

    3. Once successfully imported, set the required properties in application.properties file.

    4. Run as Springboot application.

    5. Access using **Swagger UI** : http://localhost:9097/mlWorkbench/v1/datasource/swagger-ui.html#

    6. Once you get the Swagger UI, click Authorize button and provide JWT token as below :
        Bearer <JWT token for Acumos User>

        **Note:** JWT token value can be obtained after successful login in Acumos.

    7. After successfully setting Authorize value, API are available to access.  Following are the sample inputs :

        1.Get DataSource List

            {
				authenticatedUserId : User login Id,
				
				category : category,
				
				namespace : datasource namespace,
				
				textSearch : textSearch
	    }
			

		2.Get DataSource

            {
				authenticatedUserId : User login Id,
				
				dataSourceKey : DataSource Key
	    }
			
		3.Create New DataSource

            {
				authenticatedUserId : User login Id,
				
				DataSource : DataSource Object
	    }
			
		4.Update DataSource Details

            {
				authenticatedUserId : User login Id,
				
				dataSourceKey : DataSource Key (DataSource Id),
				
				DataSource : DataSource Object
	    }
			
		5.Delete DataSource Details

            {
				authenticatedUserId : User login Id,
				
				dataSourceKey : DataSource Key (DataSource Id)
	    }
			
		6.Associate the DataSource to the project

            {
				authenticatedUserId : User login Id,
				
				projectId  : Project Id,
				
				dataSourceKey : DataSource Key (DataSource Id),
				
				DataSource  :  DataSource object
	    }
			
		