/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
===================================================================================
This Acumos software file is distributed by AT&T and Tech Mahindra
under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
This file is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
===============LICENSE_END=========================================================
*/

import { LitElement, html } from 'lit-element';
import {style} from './pipeline-styles.js';
import { filter, get, differenceWith } from "lodash-es";
import { OmniModal, OmniDialog } from "./@omni/components";
import { Forms, DataSource } from "./@omni/core";
import { ValidationMixin, DataMixin, BaseElementMixin } from "./@omni/mixins";

class ProjectPipelineLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
	get dependencies() {
	  return [OmniModal, OmniDialog];
	 }
	
	static get properties() {
		return {
			message: { type: String, notify: true },
			pipelinesList: { type: Array, notify: true },
			pipelines: [],
			activeFilter: {},
			activeSort: "",
			currentPage: 0,
			totalPages: 0,
			pipelinemSURL: {type: String},
			allPipelineCount: {type: Number},
			componenturl: {type: String, notify: true},
			isOpenArchiveDialog: { type: Boolean },
			isOpenDeleteDialog: { type: Boolean },
			isOpenRestoreDialog: { type: Boolean },
			successMessage: {type: String },
			errorMessage: {type: String },
			alertOpen: {type: Boolean },
			projectId: { type: String, notify: true },
			isEdit:{type: Boolean, notify: true },
			view: {type: String, notify: true },
			isOpenModal: { type: Boolean },
			isOpenModalLink: { type: Boolean },
			unassociatedPipelines: [],
			wikiUrl: {type: String},
			cardShow: {type: Boolean},
			userName: {type: String, notify: true},
			authToken: {type: String, notify: true}
		};
	}

	static get styles() {
		return [style];
	}

	constructor() {
		super();
		this.view = '';
		this.pipelines = [];
		this.unassociatedPipelines = [];
		this.initializeCreatePipelineForm();
		this.$validations.init({
		validations: {
			newPipeline: {
			name: {
				isNotEmpty: Forms.validators.isNotEmpty,
				pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_ ]{6,30}$')
			},
			versionId : {
				label: {
				isNotEmpty: Forms.validators.isNotEmpty,
				pattern: Forms.validators.pattern('[a-zA-Z0-9_.]{1,14}$')
				}
			}
			}
		}
    });
    this.sortOptions = [
      { value: "created", label: "Sort By Created Date" },
      { value: "name", label: "Sort By Name" },
      { value: "id", label: "Sort By ID" }
    ];
		this.requestUpdate().then(() => {
		  this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
			this.getConfig();
		})
	}

	initializeCreatePipelineForm(){
    this.data = {
      createErrorMessage : "",
      associateErrorMessage : "",
      editErrorMessage : "",
      newPipeline:{
        pipelineId : {    
          name : "",    
          versionId : {      
            comment : "",      
            label : ""    
          }  
        },
        description : ""
		},
		linkPipeline:{
			pipelineId: {
				name : "",
				versionId : {
					comment : "",
					label : ""
				}
			},
			description : ""
		},
		editPipeline:{
			pipelineId: {
				name : "",
				versionId : {
					comment : "",
					label : ""
				}
			},
			description : ""
		}
    };

	this.$data.set('createErrorMessage', '');
	this.$data.set('associateErrorMessage', '');
	this.$data.set('editErrorMessage', '');
	this.$data.set('newPipeline.pipelineId.name', '');
	this.$data.set('newPipeline.pipelineId.versionId.label', '');
	this.$data.set('newPipeline.description', '');
	this.$data.set('linkPipeline.pipelineId.name', '');
	this.$data.set('linkPipeline.pipelineId.versionId.label', '');
	this.$data.set('linkPipeline.description', '');
	this.$data.set('editPipeline.pipelineId.name', '');
	this.$data.set('editPipeline.pipelineId.versionId.label', '');
	this.$data.set('editPipeline.description', '');
  }
	
	connectedCallback() {
		super.connectedCallback();			
		window.addEventListener('hashchange', this._boundListener);
	}
	
	disconnectedCallback() {
		super.disconnectedCallback();
		window.removeEventListener('hashchange', this._boundListener);
	}
	
	getConfig(){
		const url = this.componenturl + '/api/config';
		this.resetMessage();
		fetch(url, {
			method: 'GET',
			mode: 'cors',
			cache: 'default'
		}).then(res => res.json())
			.then((envVar) => {
			this.pipelinemSURL = envVar.msconfig.pipelinemSURL;
			this.wikiUrl = envVar.wikiUrl;

			let username = envVar.userName;
			let token = envVar.authToken;
			
			if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
				this.getPipelineDetailsForProject(true);
			} else if(username && username !== '' && token && token !== '') {
				this.authToken = token;
				this.userName = username;
				this.getPipelineDetailsForProject(true);
			} else {
				this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
				this.alertOpen = true;
				this.view = 'error';        
			}
		}).catch((error) => {
			console.info('Request failed', error);
			this.errorMessage = 'Unable to retrive pipeline configuration information. Error is: '+ error;
			this.view = 'error';
			this.alertOpen = true;
		});
	}
	
	resetMessage(){
		this.successMessage = '';
		this.errorMessage = '';
	}

	getPipelineDetailsForProject(reset){
		const url = this.componenturl + '/api/project/pipelinesList';
		if(reset) {
			this.resetMessage();
		}

		fetch(url, {
			method: 'POST',
			mode: 'cors',
			cache: 'default',
			headers: {
					"Content-Type": "application/json",
					"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.pipelinemSURL,
				"projectId" : this.projectId,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((response) => {
				if(response.status === 'Error'){
          this.errorMessage = response.message;
          this.alertOpen = true;
          this.view = 'error';
        } else {
          this.pipelinesList = [];
					this.pipelines = [];
					this.cardShow = true;
          this.convertPipelineObject(response.data);
        }
	    }).catch((error) => {
		      console.info('Request failed', error);
		      this.errorMessage = 'Pipelines fetch request for project failed with error: '+ error;
		      this.view = 'error';
		      this.alertOpen = true;
	    });
	}
	
	convertPipelineObject(pipelinesInfo){
    let tempPipeline;
    pipelinesInfo.forEach(item => {
      tempPipeline = {};
      tempPipeline.pipelineId = item.pipelineId.uuid;
      tempPipeline.name = item.pipelineId.name;
      tempPipeline.version = item.pipelineId.versionId.label;
      tempPipeline.createdTimestamp = item.pipelineId.versionId.timeStamp;
      tempPipeline.createdBy = item.owner.authenticatedUserId;
      tempPipeline.description = item.description;
      tempPipeline.status = item.artifactStatus.status;
      tempPipeline.pipelineType = item.pipelineType;
      this.pipelinesList.push(tempPipeline);
    });
    this.displayPipelines();
	}
	
	createUpdateFormData(){
		let pipeline = {};
		pipeline.pipelineId = {};
		pipeline.artifactStatus = {};
		pipeline.pipelineId.versionId = {};
		pipeline.pipelineId.uuid = this.pipelineId;
		pipeline.pipelineId.name = this.Name;
		pipeline.description = this.description;
		pipeline.pipelineId.versionId.timeStamp = this.createdTimestamp ;
		pipeline.pipelineId.versionId.label = this.version;
		pipeline.artifactStatus.status = this.status;
		pipeline.pipelineType = this.pipelineType;
		return pipeline;
	}
	
	createPipeline(){
		const url = this.componenturl + '/api/project/createPipeline';
    this.resetMessage();
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.pipelinemSURL,
        "projectId" : this.projectId,
        "newPipelineDetails": this.data.newPipeline,
        "userName": this.userName 
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.initializeCreatePipelineForm();
          this.getPipelineDetailsForProject();
          this.isOpenModal = false;
        } else {
          this.$data.set('createErrorMessage', n.message);
        }
	    }).catch((error) => {
		      console.error('Request failed', error);
		      this.$data.set('createErrorMessage', 'Pipeline create request failed with error: '+ error);
	    });
	}

	archivePipeline(){
		 const url = this.componenturl + '/api/project/archivePipeline';
	    this.resetMessage();
		  fetch(url, {
			  method: 'PUT',
				mode: 'cors',
				cache: 'default',
				headers: {
						"Content-Type": "application/json",
						"auth": this.authToken,
				},
				body: JSON.stringify({
					"url": this.pipelinemSURL,
					"projectId" : this.projectId,
					"pipelineId" : this.selectedPipelineId,
					    		  
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.successMessage = n.message;
						this.getPipelineDetailsForProject();
					} else {
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
					this.isOpenArchiveDialog = false;                  
			}).catch((error) => {
					console.error('Request failed', error);
					this.errorMessage = 'Pipeline archive request failed with error: '+ error;
					this.alertOpen = true;
			});
		}

	associatePipeline(){
		const url = this.componenturl + '/api/project/associatePipeline';
		this.resetMessage();
		fetch(url, {
			method: 'PUT',
			mode: 'cors',
			cache: 'default',
			headers: {
					"Content-Type": "application/json",
					"auth": this.authToken,
			},
			body: JSON.stringify({
				"userName": this.userName,
				"url": this.pipelinemSURL,
				"projectId" : this.projectId,
				"pipelineId" : this.selectedPipelineId,
				"pipelinePayload": this.data.linkPipeline
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success'){
        	this.successMessage = n.message;
          this.alertOpen = true;
          this.initializeCreatePipelineForm();
          this.getPipelineDetailsForProject();
          this.isOpenModalLink = false;
        } else {
          this.$data.set('associateErrorMessage', n.message);
        }
		}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessge = 'Update Pipeline request failed with error: '+ error;
				this.alertOpen = true;
		});
	}
	
	restorePipeline() {
    const url = this.componenturl + '/api/project/restorePipeline';
    this.resetMessage();
	  fetch(url, {
		  method: 'PUT',
      mode: 'cors',
      cache: 'default',
      headers: {
					"Content-Type": "application/json",
					"auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.pipelinemSURL,
        "projectId" : this.projectId,
        "pipelineId" : this.selectedPipelineId,
        "userName": this.userName      		  
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.getPipelineDetailsForProject();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenRestoreDialog = false;        
    }).catch((error) => {
	      console.error('Request failed', error);
	      this.errorMessage = 'Pipeline unarchive request failed with error: '+ error;
	      this.alertOpen = true;
    });
  }

	getPipelineList(){
    const url = this.componenturl + '/api/pipelines';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
					"Content-Type": "application/json",
					"auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.pipelinemSURL,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessage = n.message;
          this.view = 'error';
          this.alertOpen = true;
        } else {
          this.allPipelines = [];
          this.convertAllPipelineObject(n.data);
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessage = 'Pipeline fetch request failed with error: '+ error;
      this.view = 'error';
      this.alertOpen = true;
    });
  }
	
	updatePipeline(){
		const url = this.componenturl + '/api/pipeline/update';
		this.resetMessage();
		fetch(url, {
			method: 'PUT',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"userName": this.userName,
				"url": this.pipelinemSURL,
				"pipelineId" : this.pipelineId,
				"pipelinePayload": this.data.editPipeline
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success') {
					this.isOpenModalEdit = false;
					this.successMessage = n.message;
					 this.initializeCreateNotebookForm();
					 this.getPipelineDetailsForProject();
					this.pipelineName = this.data.pipeline.pipelineName;
					
				} else {
					this.$data.set('editErrorMessage', n.message);
				}
				this.alertOpen = true;
		}).catch((error) => {
			console.info('Request failed', error);
			this.errorMessage = 'Update Data pipeline request failed with error: '+ error;
			this.alertOpen = true;
		});
	}
	
	launchPipeline(){
		const url = this.componenturl + '/api/pipeline/launch';
		this.resetMessage();
		fetch(url, {
			method: 'POST',
			mode: 'cors',
			cache: 'default',
			headers: {
					"Content-Type": "application/json",
					"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.pipelinemSURL,
				"pipelineId" : this.pipelineId,
				"userName": this.userName,
				"projectId": this.projectId
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success'){
					let launchURL = n.data.pipelineId.serviceUrl;
					window.open(launchURL, '_blank');
				} else {
					this.errorMessage = n.message;
					this.alertOpen = true; 
				}         
		}).catch((error) => {
			console.info('Request failed', error);
			this.errorMessage = 'Data Pipeline launch request failed with error: '+ error;
			this.alertOpen = true;
		});
	}

	convertAllPipelineObject(allPipelinesInfo){
		let tempPipeline;
		let allPipelines = [];
    allPipelinesInfo.forEach(item => {
      tempPipeline = {};
      tempPipeline.pipelineId = item.pipelineId.uuid;
      tempPipeline.name = item.pipelineId.name;
      tempPipeline.version = item.pipelineId.versionId.label;
      tempPipeline.createdTimestamp = item.pipelineId.versionId.timeStamp;
      tempPipeline.createdBy = item.owner.authenticatedUserId;
      tempPipeline.description = item.description;
      tempPipeline.status = item.artifactStatus.status;
      allPipelines.push(tempPipeline);
    });
    this.getAllPipelines(allPipelines);
	}

	getAllPipelines(nbList){
		this.unassociatedPipelines = differenceWith(nbList, this.pipelines, function(pipeline, projectPipeline) {
			return pipeline.pipelineId === projectPipeline.pipelineId;
		});

	}

	userAction(action, pipelineId, pipelineName) {
    this.dispatchEvent(
      new CustomEvent("catalog-pipeline-event", {
        detail: {
          data: {
            action: action,
            pipelineId: pipelineId,
            pipelineName: pipelineName
          }
        }
      })
    );
  }

 displayPipelines() {
    this.activeFilter = {};
    this.activeSort = "created";

    this.dataSource = new DataSource({
      data: this.pipelinesList,
      filter: this.activeFilter,
      sort: this.activeSort,
      pageSize: 5
    });
    this.sortPipelines( this.activeSort);
    this.pipelines = this.dataSource.data;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
    this.totalPipelines = this.pipelinesList.length;
    this.allPipelineCount = this.getFilteredCount();
    

    if(this.totalPipelines > 0){
      this.view = 'view';
    }else {
      this.view = 'add';
    }
  }

 filterPipelines(criteria) {
   this.activeFilter = criteria;
   this.dataSource.page = 0;
   this.currentPage = this.dataSource.page + 1;
   this.dataSource.filter(criteria);
   this.pipelines = this.dataSource.data;
   this.totalPages = this.dataSource.totalPages;
 }
 
 sortPipelines(key) {
   if (key === "created") {
     this.dataSource.sort((n1, n2) => {
       if (n1.createdTimestamp < n2.createdTimestamp) {
         return 1;
       } else if (n1.createdTimestamp > n2.createdTimestamp) {
         return -1;
       } else {
         return 0;
       }
     });
   } else {
     this.dataSource.sort(key);
   }

   this.dataSource.page = 0;
   this.currentPage = this.dataSource.page + 1;
   this.pipelines = this.dataSource.data;
 }

	searchPipelines(searchCriteria) {
    this.dataSource.search(searchCriteria);
    this.pipelines = this.dataSource.data;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
  }
	
	navigatePage(direction) {
    this.dataSource.navigatePage(direction);

    this.currentPage = this.dataSource.page + 1;
    this.pipelines = this.dataSource.data;
  }

  getFilteredCount(criteria) {
    return filter(this.dataSource._rawData, criteria).length;
  }
	
	linkPipeline(pipeline){
 
		pipeline = this.unassociatedPipelines.filter(unassociatedPipeline => {
			return unassociatedPipeline.name === pipeline;
		});
 
		this.selectedPipelineId = pipeline[0].pipelineId;
		this.$data.set('linkPipeline.pipelineId.name', pipeline[0].name);
    this.$data.set('linkPipeline.pipelineId.versionId.label', pipeline[0].version);
		this.$data.set('linkPipeline.description', pipeline[0].description);
		this.$data.set('linkPipeline.pipelineType', pipeline[0].pipelineType);
	}

  modalDismissed() {
    this.isOpenModal = false;
    this.isOpenModalLink = false;
    this.isOpenModalEdit = false;
  }

  modalClosed() {
    this.requestUpdate();
    this.createPipeline();
	}
	
	modalClosedLink(){
		this.requestUpdate();
    this.associatePipeline();
	}
	
	modalClosedEdit(){
		this.requestUpdate();
    this.updatePipeline();
	}
  
  openModal() {
		this.isOpenModal = true;
		this.isOpenModalLink = false;
		 this.isOpenModalEdit = false;
  }
  
  openModalLink() {
		this.getPipelineList();		
  	this.isOpenModalLink = true;
  	this.isOpenModal = false;
  	 this.isOpenModalEdit = false;
  }
  
  openModalEdit(item) {
	  this.$data.set('editPipeline.pipelineId.name', item.name);
      this.$data.set('editPipeline.pipelineId.versionId.label', item.version);
	  this.$data.set('editPipeline.description', item.description);
	  this.isOpenModalEdit = true;
	  this.isOpenModalLink = false;
	  this.isOpenModal = false;
  }
  
  archiveDialogDismissed(){
    this.isOpenArchiveDialog = false;
  }
  
  restoreDialogDismissed(){
    this.isOpenRestoreDialog = false;
  }

  deleteDialogDismissed(){
    this.isOpenDeleteDialog = false;
  }

  openArchiveDialog(pipelineId, pipelineName) { 
    this.selectedPipelineId = pipelineId;
    this.selectedPipelineName = pipelineName;
    this.isOpenArchiveDialog = true;
  }
  
  openRestoreDialog(pipelineId, pipelineName) { 
    this.selectedPipelineId = pipelineId;
    this.selectedPipelineName = pipelineName;
    this.isOpenRestoreDialog = true;
  }

  openDeleteDialog(pipelineId, pipelineName) { 
    this.selectedPipelineId = pipelineId;
    this.selectedPipelineName = pipelineName;
    this.isOpenDeleteDialog = true;
  }

  render() {
    return html`
    	<style> 
				@import url('https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
				.alertmessage {
					display: ${this.alertOpen ? "block" : "none"};
				}
				.card-show {
					display: ${this.cardShow ? "block" : "none"};
				}
			</style>
		<omni-dialog  title="Archive ${this.selectedPipelineName}" close-string="Archive Pipeline" dismiss-string="Cancel"
		 		is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archivePipeline}" type="warning">
       		<form><P>Are you sure want to archive ${this.selectedPipelineName}?</p></form>
      	</omni-dialog>

		<omni-dialog title="Unarchive ${this.selectedPipelineName}" close-string="Unarchive Pipeline" dismiss-string="Cancel"
				is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restorePipeline}" type="warning">
        	<form><P>Are you sure want to unarchive ${this.selectedPipelineName}?</p></form>
      	</omni-dialog>

			<omni-dialog title="Delete ${this.selectedPipelineName}" close-string="Delete Pipeline" dismiss-string="Cancel"
				is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
        @omni-dialog-closed="${this.deletePipeline}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-modal title="Create Pipeline" close-string="Create Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenModal}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosed}"
        canClose="${this.$validations.$valid && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Pipeline Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Pipeline Name" value="${this.data.newPipeline.pipelineId.name}"
                  @blur="${ e => {
                    this.$data.set('newPipeline.pipelineId.name', e.target.value);
                    this.$validations.validate('newPipeline.pipelineId.name');
                  }
                }"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.pipelineId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Pipeline name is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Pipeline Name is not valid. </div>`
                    }
                  })
                }
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Pipeline Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Pipeline Version" value="${this.data.newPipeline.pipelineId.versionId.label}"
                  @blur="${ e => {
                      this.$data.set('newPipeline.pipelineId.versionId.label', e.target.value);
                      this.$validations.validate('newPipeline.pipelineId.versionId.label');
                    }
                  }"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.pipelineId.versionId.label').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Pipeline version is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Pipeline version is not valid. </div>`
                    }
                  })
                }
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Pipeline Description</label>
                <textarea class="form-control" placeholder="Enter Pipeline Description"
                  @blur="${e => this.$data.set('newPipeline.description', e.target.value)}">${this.data.newPipeline.description}</textarea>
              </div>
            </div>
          </div>
        </form>
      </omni-modal>
      
      <omni-modal title="Associate Pipeline" close-string="Associate Pipeline" dismiss-string="Cancel"
				is-open="${this.isOpenModalLink}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedLink}"
				canClose="${this.$validations.$valid && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>

					<div class="row">
						<div class="col">
							<div class="form-group">
								<label>Pipeline Name <small class="text-danger">*</small></label>
									<select class="form-control" id="selectPipeline" 
										@change="${e => {
											this.linkPipeline(e.target.value);
											this.$validations.validate('linkPipeline.pipelineId.name');
										}}">
										<option value="null">Select a Pipeline</option>
										${this.unassociatedPipelines.map((item, index) => 
											html`															
												<option value="${item.name}">${item.name}</option>
											`
										)}
									</select>
							
								${
									this.$validations.getValidationErrors('linkPipeline.pipelineId.name').map(error => {
										switch (error) {
											case 'isNotEmpty':
												return html`<div class="invalid-feedback d-block">Pipeline Type is required</div>`

										}
									})
								}
							</div>
						</div>
						<div class="col">
							<div class="form-group">
								<label>Pipeline Version <small class="text-danger">*</small></label>
									<input type="text" class="form-control" placeholder="${this.data.linkPipeline.pipelineId.versionId.label}" 
									value="${this.data.linkPipeline.pipelineId.versionId.label}" disabled/>
							</div>
						</div>
					</div>	
				</form>
			</omni-modal>
	
	<omni-modal title="Edit Pipeline" close-string="Edit Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenModalEdit}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedEdit}"
        canClose="${this.$validations.$valid && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.editErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Pipeline Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Pipeline Name" value="${this.data.editPipeline.pipelineId.name}"
                  @blur="${ e => {
                    this.$data.set('editPipeline.pipelineId.name', e.target.value);
                    this.$validations.validate('editPipeline.pipelineId.name');
                  }
                }"
                />
                ${
                  this.$validations.getValidationErrors('editPipeline.pipelineId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Pipeline name is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Pipeline Name is not valid. </div>`
                    }
                  })
                }
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Pipeline Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Pipeline Version" value="${this.data.editPipeline.pipelineId.versionId.label}"
                  @blur="${ e => {
                      this.$data.set('editPipeline.pipelineId.versionId.label', e.target.value);
                      this.$validations.validate('editPipeline.pipelineId.versionId.label');
                    }
                  }"
                />
                ${
                  this.$validations.getValidationErrors('editPipeline.pipelineId.versionId.label').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Pipeline version is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Pipeline version is not valid. </div>`
                    }
                  })
                }
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Pipeline Description</label>
                <textarea class="form-control" placeholder="Enter Pipeline Description"
                  @blur="${e => this.$data.set('editPipeline.description', e.target.value)}">${this.data.editPipeline.description}</textarea>
              </div>
            </div>
          </div>
        </form>
      </omni-modal>
      
      ${this.view === 'view'
        ? html`
        	<div class="row ">
					<div class="col-md-12 py-3">
						<div class="card mb-124 shadow mb-5 bg-white rounded">
							<div class="card-header">
								<div class="row" style="margin:5px 0; margin-top: 0px;">
									<mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
									<h4 class="textColor card-title">Pipelines</h4>
									<div style="position: absolute; right:0" >
										<a href=${this.wikiUrl} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
										${
											this.cardShow === false
											? html`
												<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = true}>
													<span class="toggle-span">+</span>
												</a>
											`
											: html`
												<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = false}>
													<span class="toggle-span">-</span>
												</a>
											`
										} 
										&nbsp;&nbsp;&nbsp;&nbsp;
									</div>
								</div>
							</div>			
						<div class="card-body card-show">
							<div class="row" style="margin:5px 0; margin-top: 0px;">
							<div class="col-lg-12">
				              ${this.successMessage !== ''
				                ? html`
				                  <div class="alertmessage alert alert-success">
				                    <a class="close" @click=${e => this.alertOpen = false}>
				                      <span aria-hidden="true">&nbsp;&times;</span>
				                    </a> ${this.successMessage}
				                  </div>
				                `: ``
				              }
				              ${this.errorMessage !== ''
				                ? html`
				                  <div class="alertmessage alert alert-danger">
				                    <a class="close" @click=${e => this.alertOpen = false}>
				                        <span aria-hidden="true">&nbsp;&times;</span>
				                    </a>  ${this.errorMessage}
				                  </div>
				                `: ``
				              }
				              </div>
				              </div>
							<div class="row" style="margin:5px 0; margin-top: 0px;position: absolute; right:0">								
								<div class="btn-toolbar mb-2 mb-md-0">
									<div class="dropdown">
										<select class="custom-select mr-sm-2" id="template" @change=${e => this.sortPipelines(e.target.value)}>
				                        ${this.sortOptions.map(item => item.value === this.activeSort
				                          ? html`
				                            <option value="${item.value}" selected>${item.label}</option>
				                          `
				                          : html`
				                            <option value="${item.value}">${item.label}</option>
				                          `
				                        )}
				                      </select>
									</div>
									<div class="btn-group mr-2">
										&nbsp;
										<input type="text" style="height: 30px" class="form-control w-100" placeholder="Search Pipeline"
                        @input=${e => this.searchPipelines(e.target.value)}/>
										<div class="input-group-append">
											<a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Pipeline Instance" >
												<mwc-icon class="white-color">search</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.openModal()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Create Pipeline">
													<mwc-icon>add</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.openModalLink()} class="btnIcon btn btn-sm btn-secondary  mr-1" data-toggle="tooltip" data-placement="top" title="Associate Existing Pipeline">
													<mwc-icon>link</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
										</div>
									</div>
								</div>
							</div>
							<br/>

							<div class="row" style="margin:40px 0; margin-bottom: 10px;">
								<table class="table table-bordered table-sm" id="cssTable">
									<thead class="thead-light">
										<tr class="d-flex">
											<th class="col-1" >#</th>
											<th class="col-2" >Pipeline Name</th>
											<th class="col-1" >Version</th>
											<th class="col-2" >Status</th>
											<th class="col-2" >Creation Date</th>
											<th class="col-2">Actions</th>
										</tr>
									</thead>
									<tbody>
										${this.pipelines.length !== 'undefined' 
										? html`
											${this.pipelines.map((item, index) => 
												html`
												<tr class="d-flex">
													<td class="col-1">${(this.currentPage-1) * 5 + ++index}</td>
													<td class="col-2">${item.name}</td>
													<td class="col-1">${item.version}</td>
													<td class="col-2">
														${item.status === 'ACTIVE'
															? html`
																<span class="active-status">${item.status}</span>
															`
															: html`
																<span class="inactive-status">${item.status}</span>
															`
														}
													</td>
													<td class="col-2">${item.createdTimestamp}</td>
													<td class="col-2" style="padding: .05rem; padding-left: 20px;">
														${item.status == 'ACTIVE' 
														? html`
														<a href="javascript:void" @click=${(e) => this.launchPipeline(item.pipelineId)}   class="btnIcon btn btn-sm btn-primary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Pipeline">
																<mwc-icon>launch</mwc-icon>
														</a>&nbsp;&nbsp;
														<a href="javascript:void" @click=${e => this.openModalEdit(item)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1 " data-toggle="tooltip" data-placement="top" title="Edit Pipeline">
																<mwc-icon>edit</mwc-icon>
														</a>&nbsp;&nbsp;
														<a href="javascript:void" @click=${(e) => this.openArchiveDialog(item.pipelineId, item.name)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Pipeline">
																<mwc-icon>archive</mwc-icon>
														</a>
														`
														: html`
															<a href="javascript:void" @click="${e => this.openRestoreDialog(item.pipelineId, item.name)}"
							                                  class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Pipeline">
							                                  <mwc-icon class="mwc-icon-gray">restore</mwc-icon>
							                                </a>
														`}
													</td>
												</tr>
												`
											)}`
											: ``
											}
										
									</tbody>
								</table>
							</div>

							<div class="row">
                <h7>&nbsp;&nbsp;&nbsp;&nbsp;Showing ${this.currentPage} of ${this.totalPages === 0 ? 1 : this.totalPages} pages</h7>
                <div style="position: absolute; right:0;">
                  <nav aria-label="Page navigation example">
                    <ul class="pagination justify-content-end">
											<li class="page-item">
												<a href="javascript:void" @click=${e => this.navigatePage("first")}
													class="page-link ${this.currentPage !== 1? "active" : "inactive"}">First</a>                          
											</li>
											<li class="page-item">
												<a class="page-link ${this.currentPage !== 1? "active" : "inactive"}" href="javascript:void" 
													@click=${e => this.navigatePage("previous")} >Previous</a>
											</li>
											<li class="page-item">
												<a class="page-link ${this.currentPage < this.totalPages? "active" : "inactive"}" href="javascript:void" 
													@click=${e => this.navigatePage("next")} >Next</a>
											</li>
											<li class="page-item">
												<a class="page-link ${this.currentPage < this.totalPages? "active" : "inactive"}" href="javascript:void" 
													@click=${e => this.navigatePage("last")} >Last</a>
											</li>
                      &nbsp;&nbsp;&nbsp;&nbsp;
                    </ul>
                  </nav>
                </div>
              </div>
							<br/>
						</div>
					</div>
				</div>
			</div>
			`
	      : html`
	      `}
	      
	      ${this.view === 'add'
	        ? html`
						<div class="row">
							<div class="col-md-12 py-3">
								<div class="card mb-124  shadow mb-5 bg-white rounded">
									<div class="card-header">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
											<mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
											<h4 class="textColor card-title">Pipelines</h4>
											<div style="position: absolute; right:0" >
												<a href=${this.wikiUrl} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
												<a class="btn btn-sm btn-secondary my-2">-</a> 
												&nbsp;&nbsp;&nbsp;&nbsp;
											</div>
										</div>
									</div>
									<div class="card-body">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
				              ${this.successMessage !== ''
				                ? html`
				                  <div class="alertmessage alert alert-success">
				                    <a class="close" @click=${e => this.alertOpen = false}>
				                      <span aria-hidden="true">&nbsp;&times;</span>
				                    </a> ${this.successMessage}
				                  </div>
				                `: ``
				              }
				              ${this.errorMessage !== ''
				                ? html`
				                  <div class="alertmessage alert alert-danger">
				                    <a class="close" @click=${e => this.alertOpen = false}>
				                        <span aria-hidden="true">&nbsp;&times;</span>
				                    </a>  ${this.errorMessage}
				                  </div>
				                `: ``
											}
										</div>
										<div class="row" style="margin:5px 0; margin-top: 0px;margin-bottom:20px;">
											<h7 >No Pipelines, get started by creating your first Pipeline</h7>
										</div>
										<div class="row" style="margin:10px 0">
											<button type="button" class="btn btn-primary" @click=${(e) => this.openModal()}>Create Pipeline</button>&nbsp;&nbsp;&nbsp;
											<button type="button" class="btn btn-secondary-button" @click=${(e) => this.openModalLink()}>Associate Existing Pipeline</button>											
										</div>
									</div>
							</div>
						</div>
					</div>
					`
       : html`
     `}
          
      ${this.view === 'error'
        ? html`
          <div class="alertmessage alert alert-danger">
            <a class="close" @click=${e => this.alertOpen = false}>
                <span aria-hidden="true">&nbsp;&times;</span>
            </a>
            ${this.errorMessage}
          </div>
        `
        : html`
      `}

      ${this.view === ''
        ? html`
          <p class="success-status"> Loading Pipelines..</p>
        `
        : html`
      `}  	
    `;
  }
}
customElements.define('project-pipeline-element', ProjectPipelineLitElement);
