.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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


===========================
Notebook Component Overview
===========================

Notebook Catalog
================

Notebook Catalog screen displays list of notebooks which are created by user. There are following ways to navigate to the Notebook Catalog screen.

1. Either click on 'Notebooks' card from Dashboard screen

2. Or click on the 'Notebooks' link from sidebar navigation 

	.. image:: images/notebook-catalog.PNG
	   :alt: notebook-catalog image.



**From notebook Catalog screen, user can perform either of following actions.**

1. **Create Notebook**  
	
  By clicking on 'Create Notebook' button present at top right corner, a dialog box will get open. Fill out all required information and 
  click on the 'Create Notebook' button. After successful notebook creation, User will be able to see that created Notebook.   

	.. image:: images/create-notebook-catalog.PNG
	   :alt: create-notebook-catalog image.

  if external notebook flag is set to true, user will be able to create a external notebook in acumos by providing its url in the create notebook form.

	.. image:: images/notebook-create-url.PNG
	   :alt: create-notebook-create-url image.

2. **Archive Notebook**

  If user wish to **Archive** any **Active** Notebook then user can click on the Archive button present at the bottom right corner of that specific Notebook card.
  A confirmation dialog box will appear on click of Archive button. After confirmation, notebook will be archived.  

	.. image:: images/archive-notebook-dialog.PNG
	   :alt: archive-notebook-dialog image.

3. **Unarchive Notebook**

  If user wish to **Unarchive** any **Archived** Notebook then user can click on the Unarchive button present at the bottom right corner of that specific notebook card.
  A confirmation dialog box will appear on click of Unarchive button. After confirmation, notebook will be unarchived.  

	.. image:: images/unarchive-notebook-dialog.PNG
	   :alt: unarchive-notebook-dialog image.


4. **Delete Notebook** 

  If user wish to **Delete** any **Archived** Notebook then user can click on the Delete button present at the bottom right corner of that specific notebook card.
  A confirmation dialog box will appear on click of Delete button. After confirmation, notebook will be deleted physically from database and can not be restored.  

	.. image:: images/delete-notebook-dialog.PNG
	   :alt: delete-notebook-dialog image.

5. **Launch Notebook** 

  If user wish to **Launch** any **Active** Notebook, then user can click on the Launch button present at the bottom right corner of that specific notebook card.
  On click of launch button, notebook will be opened in the user specific Jupyter Instance in a new browser tab.

	.. image:: images/notebook-launch.PNG
	   :alt: notebook-launch image.

**Notebook Catalog screen includes following features/capabilities.**

1. **Notebook Sorting**

  User can sort the notebook list by choosing either one of following options - By Notebook Created Date, By Notebook Name. 
  
  For Notebook Created Date

	.. image:: images/notebook-sorting-by-created-date.PNG
	   :alt: notebook-sorting-by-created-date image.
	   
  For Notebook Name 
	   
	.. image:: images/notebook-sorting-by-name.PNG
	   :alt: notebook-sorting-by-name image.

2. **Search with Notebook metadata** 

  Notebook Catalog screen provides textual search capability as well where if user wish to search for specific text present in Notebook metadata 
  then user can enter into the search box (present at the right top corner) and notebooks matching with the entered search criteria will get displayed.
  
	.. image:: images/notebook-search.PNG
	   :alt: notebook-search image.

3. **Pagination**
 
  In Notebook catalog screen, at a time only 8 notebooks will be displayed. User can use Pagination feature to navigate to another page if the user wishes to see other notebooks lists. 



Notebook Details
================

On click on specific Notebook card in the Notebook catalog screen, user will be redirected to the Notebook details screen. In this screen, Notebook basic details will get
displayed.
 
	.. image:: images/notebook-details.PNG
	   :alt: notebook-details image.

In the Notebook details screen, user can perform following Notebook relevant actions. 

While creating, editing the notebook, user can give the external notebook url as well, to save user's own jupyter or zappelin notebook details.

1. **Edit Notebook**

  To Edit the Notebook, click on the 'Edit' icon present on the card-header of the Notebook. On edit, notebook name, version and description field will become editable. 
  User can provide new information and save it.

	.. image:: images/edit-notebook.PNG
	   :alt: edit-notebook image.

  If external Notebook flag is set to true, user will be able to edit name, version, description and url as well.

  .. image:: images/notebook-edit-url.PNG
	   :alt: notebook-edit-url image.
	
2. **Archive Notebook**

  To Archive Notebook, click on the Archive button. After user confirmation, notebook will get archived and status will be reflected in notebook details.

    .. image:: images/archive-notebook-detail-dialog.PNG
	   :alt: archive-notebook-detail-dialog image.
    
3. **Unarchive Notebook**

  To Unarchive Notebook, click on the Unarchive button. After user confirmation, notebook will get unarchived and status will be reflected in notebook details.

	.. image:: images/unarchive-notebook-detail-dialog.PNG
	   :alt: unarchive-notebook-detail-dialog image.

4. **Delete Notebook**

  If Notebook is archived then Delete button will be enabled for a user to delete the notebook. On click of Delete Button, Notebook will get deleted and 
  user will be redirected to the Notebook Catalog Screen.

	.. image:: images/delete-notebook-detail-dialog.PNG
	   :alt: delete-notebook-detail-dialog image.
	
5. **Launch Notebook**
  
 If Notebook is active, then launch button will be enabled for the user. On click of launch button, notebook will be opened in the user specific Jupyter Instance in a new browser tab.

    .. image:: images/notebook-launch.PNG
	   :alt: notebook-launch image.
	
  	
  	