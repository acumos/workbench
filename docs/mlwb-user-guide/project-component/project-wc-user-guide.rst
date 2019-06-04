.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================


==========================
Project Component Overview
==========================

Project Catalog
===============

Project Catalog screen displays list of projects which are created by user. There are following ways to navigate to the Project Catalog screen.

1. Either click on 'Project' card from Dashboard screen

2. Or click on the 'Project' link from sidebar navigation 

	.. image:: images/project-catalog.png



**From Project Catalog screen, user can perform either of following actions.**

1. **Create Project**  
	
  By clicking on 'Create Project' button present at top right corner, a dialog box will get open. Fill out all required information and 
  click on the 'Create Project' button. After successful project creation, User will be able to see that created Project inside 'Active Project' tab.   

	.. image:: images/Create-project-Dialog.png

2. **Archive Project**

  If user wish to **Archive** any **Active** Project then he can click on the Archive button present at the bottom right corner of that specific project card.
  A confirmation dialog box will appear on click of Archive button. After confirmation, project will be archived and user can see that project inside 'Archive Project' tab.  

	.. image:: images/archive-project-catalog-dialog.png

3. **Unarchive Project**

  If user wish to **Unarchive** any **Archived** Project then he can click on the Unarchive button present at the bottom right corner of that specific project card.
  A confirmation dialog box will appear on click of Unarchive button. After confirmation, project will be unarchived and user can see that project inside 'Active Project' tab.  

	.. image:: images/unarchive-project-catalog-dialog.png


4. **Delete Project** 

  If user wish to **Delete** any **Archived** Project then he can click on the Delete button present at the bottom right corner of that specific project card.
  A confirmation dialog box will appear on click of Delete button. After confirmation, project will be deleted physically from database and can not be restored.  

	.. image:: images/delete-project-catalog-dialog.png


**Project Catalog screen includes following features/capabilities.**

1. **Project Filter**  

  Project Catalog screen has three different tabs named as 'Acitve Project', 'Archive Project' and 'All Project' which basically 
  provides filter capability based on 'Project Status'. If user wish to see only 'Active' project then he can choose 'Active Project' tab. 
  Similarly if user wish to see all projects then he can choose 'All Projects' tab. 

2. **Project Sorting**

  User can sort the project list by choosing either one of following options - By Project Created Date, By Project Name, By Project ID. 

3. **Search with Project metadata** 

  Project Catalog screen provides textual search capability as well where if user wish to search for specific text present in Project metadata 
  then he can enter into the search box (present at the right top corner) and projects matching with the entered search criteria will get displayed. 

4. **Pagination**
 
  In Project catalog screen, at a time only 8 projects will be displayed. User can use Pagination feature to navigate to another page if he wish to see other project lists. 



Project Details
===============

On click on specific Project card in the Project catalog screen, user will be redirected to the Project details screen. In this screen, Project basic details will get
displayed along with associated notebook details.
 
	.. image:: images/project-details.png

In the Project details screen, user can perform following Project relevant actions. 

1. **Edit Project**

  To Edit the Project, click on the 'Edit' icon present on the card-header of the Project. On edit, project name, version and description field will become editable. 
  User can provide new information and save it.

	.. image:: images/edit-project-detail-new.png
	
2. **Archive Project**

  To Archive Project, click on the Archive button. After user confirmation, project will get archived and status will be reflected in project details

  .. image:: images/archive-project-detail-dialog.png

.. note::
  If project is archived then Notebook and Pipeline section will not be displayed for that project in the Project Details screen. 
  
  .. image:: images/project-archive-new.png
    
3. **Unarchive Project**

  To Unarchive Project, click on the Unarchive button. After user confirmation, project will get unarchived and status will be reflected in project details

	.. image:: images/unarchive-project-dialog.png

4. **Delete Project**

  If project is archived then Delete button will be enabled for a user to delete the project. On click of Delete Button, Project will get deleted and 
  user will be redirected to the Project Catalog Screen

	.. image:: images/delete-project-dialog.png
	
5. **Notebook Section**
  
  Notebook section will display all notebooks which are associate with the project. Notebook lists will get displayed in tabular format showing Notebook name, 
  version, type, status, created date etc. 

	.. image:: images/project-notebook.png

  If there is no notebook associated to that project then two options 'Create Notebook' and 'Associate Notebook' will be displayed to the user in this section.
 
	.. image:: images/project-details.png

  **Following are the user actions available in the Notebook section:**
	
  * **Create  Notebook**: 
  
  	If there is no notebook associated already for a project then user can create a notebook by clicking on the 'Create Notebook' button. Or else, user has to click on the '+' sign
  	displayed on the right top corner of the Notebook section for same. On click event, a dialog box will appeared where user has to fill out all required information and submit.
  	On successful Notebook creation, notebook will get displayed in the tablular list.
  	 
  	 	.. image:: images/Create-notebook-project.png
  	  
  * **Associate  Notebook**: 
  
  	If there is no notebook associated already for a project then user can associate an existing notebook by clicking on the 'Associate Notebook' button. Or else, user has to click on the link button
  	displayed on the right top corner of the Notebook section for same. On click event, a dialog box will appeared where user has to select the desired notebook from drop down and submit.
  	On successful Notebook association, notebook will get displayed in the tablular list.

	.. image:: images/associate-notebook-project.png

  * **Archive/Unarchive/Delete Notebook**: 
  
  	User may choose an option of Archiving, Unarchiving or Deleting a notebook by clicking on appropriate button. On confirmation, notebook will get archived/unarchived/deleted based on user action 
  	and status will get reflect into the Notebook list. 
  	
	.. image:: images/archive-notebook-project-dialog.png
	.. image:: images/unarchive-notebook-project-dialog.png
	.. image:: images/delete-notebook-project-dialog.png
	
  * **Launch Notebook** 

    If user wish to **Launch** any **Active** Notebook, then user can click on the Launch button. On click of launch button, notebook will be opened in the user specific Jupyter Instance in a new browser tab.
  	
  .. image:: images/notebook-launch.png