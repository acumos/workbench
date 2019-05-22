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
ML Workbench Pipeline Service Developer Guide
=================================================

1.    Overview
=================

         This is the developers guide to ML Workbench Pipeline Service.

1.1. What is ML Workbench Pipeline Service\?
---------------------------------------------

    ML Workbench Pipeline Service expose API to allow to perform CRUD operation on Pipeline in ML Workbench.

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

- Gerrit repo: `workbench/pipeline-service <https://gerrit.acumos.org/r/#/admin/projects/workbench>`_
- `Jira <https://jira.acumos.org/browse/ACUMOS-2504>`_  Create Pipeline
- `Jira <https://jira.acumos.org/browse/ACUMOS-2505>`_  Launch Pipeline
- `Jira <https://jira.acumos.org/browse/ACUMOS-2506>`_  Update Pipeline
- `Jira <https://jira.acumos.org/browse/ACUMOS-2507>`_  List Pipeline
- `Jira <https://jira.acumos.org/browse/ACUMOS-2508>`_  Delete Pipeline

5. Development Setup
=====================

    1. Clone or download code from "**Gerrit repo**" mentioned above.

    2. Import pipeline-service Project in IDE (viz., Eclipse or STC)

    3. Once successfully imported, set the required properties in application.properties file.

    4. Run as Springboot application.

    5. Access using **Swagger UI** : http://localhost:9089/mlWorkbench/v1/pipeline/swagger-ui.html#

    6. Once you get the Swagger UI, click Authorize button and provide JWT token as below :
        Bearer <JWT token for Acumos User>

        **Note:** JWT token value can be obtained after successful login in Acumos.

    7. After successfully setting Authorize value, API are available to access.  Following are the sample inputs :

        1. Create Pipeline

            1.1 : Create Independent Pipeline

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipeline**" :
                {
                      "pipelineId": {
                        "name": "<Pipeline name>",
                        "versionId": {
                          "label": "<pipeline version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of pipeline>",
                      "pipelineType":"<Jupyter|Zeplin>"
                }

            1.2 : Create Pipeline associated to Project

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipeline**" :
                {
                      "pipelineId": {
                        "name": "<Pipeline name>",
                        "versionId": {
                          "label": "<pipeline version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of pipeline>",
                      "pipelineType":"<Jupyter|Zeplin>"
                }

            "**projectId**" : <Project UUID to associate Pipeline with>

        2. Launch Pipeline

            2.1 Launch Independent Pipeline :

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipelineId**" : <Pipeline UUID>

            2.2 Launch Pipeline associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipelineId**" : <Pipeline UUID>

            "**projectId**" : <Project UUID associated to Pipeline>

        3. List Pipeline

            3.1 List of Independent Pipeline :

            "**authenticatedUserId**" : <Acumos User login ID>

            3.2 List of Pipeline associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**projectId**" : <Project UUID associated to Pipeline>


        4. Get Pipeline

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipelineId**" : <Pipeline UUID>


        5. Update Pipeline

            5.1 Update Independent Pipeline :

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipeline**" :
                {
                      "pipelineId": {
                        "name": "<Pipeline name>",
                        "versionId": {
                          "label": "<pipeline version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of pipeline>",
                      "pipelineType":"<Jupyter|Zeplin>"
                }

            "**pipelineId**" : <Pipeline UUID>

            5.2 Update Pipeline associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipeline**" :
                {
                      "pipelineId": {
                        "name": "<Pipeline name>",
                        "versionId": {
                          "label": "<pipeline version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of pipeline>",
                      "pipelineType":"<Jupyter|Zeplin>"
                }

            "**pipelineId**" : <Pipeline UUID>

            "**projectId**" : <Project UUID associated to Pipeline>

        6. Archive Pipeline

            6.1 Archive Independent Pipeline :

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipeline**" :
                {
                      "pipelineId": {
                        "name": "<Pipeline name>",
                        "versionId": {
                          "label": "<pipeline version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of pipeline>",
                      "pipelineType":"<Jupyter|Zeplin>"
                }

            **pipelineId** : <Pipeline UUID>

            3.2 Archive Pipeline associated to a Project:

            "**authenticatedUserId**" : <Acumos User login ID>

            "**pipeline**" :
                {
                      "pipelineId": {
                        "name": "<Pipeline name>",
                        "versionId": {
                          "label": "<pipeline version e.g., 0.0.1 or 1"
                        }
                      },
                      "description": "<Description of pipeline>",
                      "pipelineType":"<Jupyter|Zeplin>"
                }

            "**pipelineId**" : <Pipeline UUID>

            "**projectId**" : <Project UUID associated to Pipeline>
