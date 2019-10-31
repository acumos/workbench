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
Pipeline Component Overview
===========================

Pipeline Catalog
================

Pipeline Catalog screen displays list of pipelines which are created by user. There are following ways to navigate to the Pipeline Catalog screen.

1. Either click on 'Pipelines' card from Dashboard screen

2. Or click on the 'Pipelines' link from sidebar navigation 

	.. image:: images/pipeline-catalog.png
	   :alt: pipeline-catalog image.



**From pipeline Catalog screen, user can perform either of following actions.**

1. **Create Pipeline**  
	
  By clicking on 'Create Pipeline' button present at top right corner, a dialog box will get open. Fill out all required information and 
  click on the 'Create Pipeline' button. After successful pipeline creation, User will be able to see that created Pipeline.   
  if external pipeline flag is set to true, user will be able to create a external pipeline in acumos by providing its url in the create pipeline form.

	.. image:: images/pipeline-create-url.png
	   :alt: create-pipeline-create-url image.

2. **Archive Pipeline**

  If user wish to **Archive** any **Active** Pipeline then he can click on the Archive button present at the bottom right corner of that specific Pipeline card.
  A confirmation dialog box will appear on click of Archive button. After confirmation, pipeline will be archived.  

	.. image:: images/archive-pipeline.png
	   :alt: archive-pipeline image.

3. **Unarchive Pipeline**

  If user wish to **Unarchive** any **Archived** Pipeline then he can click on the Unarchive button present at the bottom right corner of that specific pipeline card.
  A confirmation dialog box will appear on click of Unarchive button. After confirmation, pipeline will be unarchived.  

	.. image:: images/unarchive-pipeline.png
	   :alt: unarchive-pipeline image.


4. **Delete Pipeline** 

  If user wish to **Delete** any **Archived** Pipeline then he can click on the Delete button present at the bottom right corner of that specific pipeline card.
  A confirmation dialog box will appear on click of Delete button. After confirmation, pipeline will be deleted physically from database and can not be restored.  

	.. image:: images/delete-pipeline.png
	   :alt: delete-pipeline image.

5. **Launch Pipeline** 

  If user wish to **Launch** any **Active** Pipeline, then he can click on the Launch button present at the bottom right corner of that specific pipeline card.
  On click of launch button, pipeline will be opened in the user specific Nifi Instance in a new browser tab.

	.. image:: images/pipeline-launch.png
	   :alt: pipeline-launch image.

**Pipeline Catalog screen includes following features/capabilities.**

1. **Pipeline Sorting**

  User can sort the pipeline list by choosing either one of following options - By Pipeline Created Date, By Pipeline Name. 

2. **Search with Pipeline metadata** 

  Pipeline Catalog screen provides textual search capability as well where if user wish to search for specific text present in Pipeline metadata 
  then he can enter into the search box (present at the right top corner) and pipelines matching with the entered search criteria will get displayed. 

3. **Pagination**
 
  In Pipeline catalog screen, at a time only 8 pipelines will be displayed. User can use Pagination feature to navigate to another page if he wish to see other pipelines lists. 



Pipeline Details
================

On click on specific Pipeline card in the Pipeline catalog screen, user will be redirected to the Pipeline details screen. In this screen, Pipeline basic details will get
displayed.
 
	.. image:: images/pipeline-details.png
	   :alt: pipeline-details image.

If external pipeline flag is set to true, pipeline details including url will be displayed.
    
    .. image:: images/pipeline-details-url.png
	   :alt: pipeline-details-url image.

In the Pipeline details screen, user can perform following Pipeline relevant actions. 

While creating, editing the pipeline, user can give the external pipeline url as well, to save his own nifi pipeline details.

1. **Edit Pipeline**

  To Edit the Pipeline, click on the 'Edit' icon present on the card-header of the Pipeline. On edit, pipeline name, version and description field will become editable. 
  User can provide new information and save it.

	.. image:: images/edit-pipeline.png
	   :alt: edit-pipeline image.

  If external Pipeline flag is set to true, user will be able to edit name, version, description and url as well.

  .. image:: images/pipeline-edit-url.png
	   :alt: pipeline-edit-url image.
	
2. **Archive Pipeline**

  To Archive Pipeline, click on the Archive button. After user confirmation, pipeline will get archived and status will be reflected in pipeline details.

    .. image:: images/archive-pipeline-detail-dialog.png
	   :alt: archive-pipeline-detail-dialog image.
    
3. **Unarchive Pipeline**

  To Unarchive Pipeline, click on the Unarchive button. After user confirmation, pipeline will get unarchived and status will be reflected in pipeline details.

	.. image:: images/unarchive-pipeline-detail-dialog.png
	   :alt: unarchive-pipeline-detail-dialog image.

4. **Delete Pipeline**

  If Pipeline is archived then Delete button will be enabled for a user to delete the pipeline. On click of Delete Button, Pipeline will get deleted and 
  user will be redirected to the Pipeline Catalog Screen.

	.. image:: images/delete-pipeline-detail-dialog.png
	   :alt: delete-pipeline-detail-dialog image.
	
5. **Launch Pipeline**
  
 If Pipeline is active, then launch button will be enabled for the user. On click of launch button, pipeline will be opened in the user specific Nifi Instance in a new browser tab.

    .. image:: images/pipeline-launch.png
	   :alt: pipeline-launch image.
	
  	
  	