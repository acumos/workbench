# Workbench

This repository holds projects that together comprise the Workbench User interface along with backend microservices.

## Build Prerequisites

* JDK 1.8
* Spring STS 3.8.x (https://spring.io/tools/sts/all) 
* Git Shell (https://git-for-windows.github.io/) or SourceTree (https://www.sourcetreeapp.com/) for Cloning & pushing the code changes. 
* Maven 3.x
* Proxy setup to download dependencies from open source repositories
* Open Source or GitShell Command Line Interface
* Node JS v10.15.1
* Angular CLI v7.0.3
* Polymer CLI v1.9.6

## Checkout Instructions

Browse to your preferred directory and run below command:
	
	git clone --depth 1 https://<userId>@gerrit.acumos.org/workbench.git

## Framework details

* This repository contains one Angular application named as 'home-webcomponent' which need to be build and deployed as Angular application. Instructions are provided below for same.
* There are several polymer components present in this repository such as 'dashboard-webcomponent', 'project-webcomponent', 'project-catalog-webcomponent' etc. which need to be build and deployed as Polymer application. Instructions are provided below for same.
* This repository contains 3 microservices named as 'project-service', 'notebook-service' and 'pipeline-service' which are based on Spring Boot based framework and need to be deployed as a Java application. 


## Special Note
* Part of Boreas release, Node Express server is being implemented in the Angular/Polymer components for reverse proxy and sharing environment properties to the UI components. This implementation will be revisit in next release to seek other best approach.

## Build and deploy Instructions for Angular and Polymer Components

A. Build Polymer application
* Navigate to Each polymer component root directory and install all npm dependencies via below command:
   
    npm install --prod 

* Build each polymer component with below command

	npm run build:component
	
B. Build Angular application
* Navigate to Angular Application root directory and install all npm dependencies via below command:
   
    npm install

* Build angular application with below command

	npm run build:component
	
C. Run/Deploy Polymer/Angular application with below command. [[Application will be running and accessible to the port as defined in server.js file present in API folder] 

	npm run run:api

	http://localhost:<port_num>/

## License

Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
Acumos is distributed by AT&T and Tech Mahindra under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
express or implied.  See the License for the specific language governing permissions and limitations 
under the License.