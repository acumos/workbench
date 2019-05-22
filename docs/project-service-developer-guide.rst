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
ML(Machine Learning) Workbench Project Service Developer Guide
=================================================

1.Overview
=================

         This is the developers guide to ML Workbench Project Service

1.1. What is ML Workbench Project Service\?
---------------------------------------------

ML Workbench Project Service expose API to allow to perform CRUD operation on Project in ML Workbench.


2.Architecture and Design
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

4.Project Resources
==========================

- Gerrit repo: `workbench/project-service <https://gerrit.acumos.org/r/#/admin/projects/workbench>`_
- `Jira <https://jira.acumos.org/browse/ACUMOS-2480>`_  Create Project
- `Jira <https://jira.acumos.org/browse/ACUMOS-2481>`_  Update Project
- `Jira <https://jira.acumos.org/browse/ACUMOS-2482>`_  List Project
- `Jira <https://jira.acumos.org/browse/ACUMOS-2483>`_  View Project
- `Jira <https://jira.acumos.org/browse/ACUMOS-2484>`_  Delete Project

5. Development Setup
=====================

1. Clone or download code from "**Gerrit repo**" mentioned above.

2. Import project-service Project in IDE (viz., Eclipse or STC)

3. Once successfully imported, set the required properties in application.properties file.

4. Run as Springboot application.

5. Access using **Swagger UI** : http://localhost:9088/mlWorkbench/v1/project/swagger-ui.html#

6. Once you get the Swagger UI, click Authorize button and provide JWT token as below :
Bearer <JWT token for Acumos User>

**Note:** JWT token value can be obtained after successful login in Acumos.

7. After successfully setting Authorize value, API are available to access.  Following are the sample inputs :

1. Create Project :

"**authenticatedUserId**" : <Acumos User login ID>

"**project**" :
{
  "projectId": {
"name": "<Project name e.g. TestProject>",
"versionId": {
  "label": "<version e.g. 0.0.1>"
}
  },
  "description": "<Project description>"
}

2. Update Project :

"**authenticatedUserId**" : <Acumos User login ID>

"**project**" :
{
  "projectId": {
"name": "<Project name e.g. TestProject>",
"versionId": {
  "label": "<version e.g. 0.0.1>"
}
  },
  "description": "<Project description>"
}

"**projectId**" : <Project UUID to be updated>

3. List Project :

"**authenticatedUserId**" : <Acumos User login ID>

4. View (Get) Project :

"**authenticatedUserId**" : <Acumos User login ID>

"**projectId**" : <Project UUID>


5. Delete Project :

"**authenticatedUserId**" : <Acumos User login ID>

"**projectId**" : <Project UUID>



