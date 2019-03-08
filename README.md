# Workbench

This repository holds projects that together comprise the Workbench User interface along with backend microservices.

## Build Prerequisites

* JDK 1.8
* Spring STS 3.8.x (https://spring.io/tools/sts/all) 
* Git Shell (https://git-for-windows.github.io/) or SourceTree (https://www.sourcetreeapp.com/) for Cloning & pushing the code changes. 
* Maven 3.x
* Proxy setup to download dependencies from open source repositories
* Open Source or GitShell Command Line Interface

## Build Instructions

1. Browse to your preferred directory and run below command:

    git clone https://<userId>@gerrit.acumos.org/workbench.git

2. Build via this command:
   
    mvn clean install
