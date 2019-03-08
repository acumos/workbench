# Workbench

This repository holds projects that together comprise the Workbench User interface along with backend microservices.

## Build Prerequisites

* JDK 1.8
* Spring STS 3.8.x (https://spring.io/tools/sts/all) 
* Git Shell (https://git-for-windows.github.io/) or SourceTree (https://www.sourcetreeapp.com/) for Cloning & pushing the code changes. 
* Maven 3.x
* Proxy setup to download dependencies from open source repositories
* Open Source or GitShell Command Line Interface

## Checkout Instructions

Browse to your preferred directory and run below command:
	
	git clone https://<userId>@gerrit.acumos.org/workbench.git


## Build and deploy Instructions

A. Build and deploy as a Java application
* Navigate to Each component root directory and build component via below command:
   
    mvn clean install


* Either run each component as Java Jar file using below command:
	
	java -jar <jar_file_name>

* Or open the project in Spring tool suite IDE (or Eclipse) and run it as a java application [Application will be running and accessible to the port defined in application.properties file] 

	http://localhost:<port_num>/
