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
ML Workbench Notebook Service Developer Guide
=================================================


1.    Overview
=================

         This is the developers guide to ML Workbench Notebook Service.

1.1. What is ML Workbench Notebook Service\?
---------------------------------------------

    ML Workbench Notebook Service expose API to allow to perform CRUD operation on Notebook in ML Workbench.

2.    Architecture and Design
=================================

2.1. High-Level Flow
----------------------
    Coming soon

2.2. Class Diagrams
----------------------
    Coming soon

2.3. Sequence Diagrams
--------------------------
    Coming soon

3. Technology and Frameworks
=============================
  **List of the development languages, frameworks, etc.**

  #. Springboot 2.1.3.RELEASE
  #. Java 8
  #. Maven 4.0.0
  #. Jackson 2.7.5
  #. JUnit 4.12

4.    Project Resources
==========================

- Gerrit repo: `workbench/notebook-service <https://gerrit.acumos.org/r/#/admin/projects/workbench>`_
- `Jira <https://jira.acumos.org/browse/ACUMOS-2485>`_  Create Notebook
- `Jira <https://jira.acumos.org/browse/ACUMOS-2485>`_  Update Notebook
- `Jira <https://jira.acumos.org/browse/ACUMOS-2496>`_  List Notebook
- `Jira <https://jira.acumos.org/browse/ACUMOS-2503>`_  Delete Notebook
- `Jira <https://jira.acumos.org/browse/ACUMOS-2495>`_  Launch Notebook

5. Development Setup
=====================

    1. Clone or download code from "**Gerrit repo**" mentioned above.

    2. Import notebook-service Project in IDE (viz., Eclipse or STC)

    3. Once successfully imported, set the required properties in application.properties file.

    4. Run as Springboot application.

    5. Access using **Swagger UI** : http://localhost:9089/mlWorkbench/v1/notebook/swagger-ui.html#

    6. Once you get the Swagger UI, click Authorize button and provide JWT token as below :
        Bearer <JWT token for Acumos User>

        **Note:** JWT token value can be obtained after successful login in Acumos.

    7. After successfully setting Authorize value, API are available to access.  Following are the sample inputs :

        1. Create Notebook

            1.1 : Create Independent Notebook

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebook**" :
                {
                      "notebookId": {
                        "name": "<Notebook name>",
                        "versionId": {
                          "label": "<notebook version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of notebook>",
                      "notebookType":"<Jupyter|Zeplin>"
                }

            1.2 : Create Notebook associated to Project

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebook**" :
                {
                      "notebookId": {
                        "name": "<Notebook name>",
                        "versionId": {
                          "label": "<notebook version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of notebook>",
                      "notebookType":"<Jupyter|Zeplin>"
                }

            "**projectId**" : <Project UUID to associate Notebook with>

        2. Launch Notebook

            2.1 Launch Independent Notebook :

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebookId**" : <Notebook UUID>

            2.2 Launch Notebook associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebookId**" : <Notebook UUID>

            "**projectId**" : <Project UUID associated to Notebook>

        3. List Notebook

            3.1 List of Independent Notebook :

            "**authenticatedUserId**" : <Acumos User login ID>

            3.2 List of Notebook associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**projectId**" : <Project UUID associated to Notebook>


        4. Get Notebook

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebookId**" : <Notebook UUID>


        5. Update Notebook

            5.1 Update Independent Notebook :

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebook**" :
                {
                      "notebookId": {
                        "name": "<Notebook name>",
                        "versionId": {
                          "label": "<notebook version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of notebook>",
                      "notebookType":"<Jupyter|Zeplin>"
                }

            "**notebookId**" : <Notebook UUID>

            5.2 Update Notebook associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebook**" :
                {
                      "notebookId": {
                        "name": "<Notebook name>",
                        "versionId": {
                          "label": "<notebook version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of notebook>",
                      "notebookType":"<Jupyter|Zeplin>"
                }

            "**notebookId**" : <Notebook UUID>

            "**projectId**" : <Project UUID associated to Notebook>

        6. Archive Notebook

            6.1 Archive Independent Notebook :

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebook**" :
                {
                      "notebookId": {
                        "name": "<Notebook name>",
                        "versionId": {
                          "label": "<notebook version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of notebook>",
                      "notebookType":"<Jupyter|Zeplin>"
                }

            **notebookId** : <Notebook UUID>

            3.2 Archive Notebook associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**notebook**" :
                {
                      "notebookId": {
                        "name": "<Notebook name>",
                        "versionId": {
                          "label": "<notebook version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of notebook>",
                      "notebookType":"<Jupyter|Zeplin>"
                }

            "**notebookId**" : <Notebook UUID>

            "**projectId**" : <Project UUID associated to Notebook>
