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
import { OmniModal, OmniDialog } from "./@workbenchcommon/components";
import { Forms, DataSource } from "./@workbenchcommon/core";
import { ValidationMixin, DataMixin, BaseElementMixin } from "./@workbenchcommon/mixins";

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
			isOpenModalEdit: { type: Boolean },
			unassociatedPipelines: [],
			pipelineWikiURL: {type: String},
			cardShow: {type: Boolean},
			userName: {type: String, notify: true},
			authToken: {type: String, notify: true},
			createTimeout: {type: Number, notify: true},
      pipelineCreated: [],
      creationMessage: {type: String, notify: true}
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
				pipelineId: {
					name: {
						isNotEmpty: Forms.validators.isNotEmpty,
						pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_]{5,29}$')
					}
				}
			},
			linkPipeline: {
				pipelineId: {
					name: {
						isNotEmpty: Forms.validators.isNotEmpty,
					}
				}
			},
			editPipeline: {
				pipelineId: {
					name: {
						isNotEmpty: Forms.validators.isNotEmpty,
						pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_]{5,29}$')
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
		this.modalDismissed();
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
          name : ""
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
		
		this.$data.snapshot('newPipeline');
		this.$data.snapshot('linkPipeline');
		this.$data.snapshot('editPipeline');

		this.$data.set('createErrorMessage', '');
		this.$data.set('associateErrorMessage', '');
		this.$data.set('editErrorMessage', '');
		this.$data.set('newPipeline.pipelineId.name', '');
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
			this.pipelineWikiURL = envVar.wikiConfig.pipelineWikiURL;
			this.createTimeout = envVar.createTimeout;
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
		      this.errorMessage = 'Data Pipelines fetch request for project failed with error: '+ error;
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

	getPipelineCreationStatus(pipelineId){
    const url = this.componenturl + '/api/project/createPipelineStatus';
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
        "url": this.mSurl,
				"pipelineId": pipelineId,
				"projectId" : this.projectId,
        "userName": this.userName 
      })
    }).then(res => res.json())
    .then((n) => {
      if(n.status === 'Error'){
        this.errorMessage = n.message;
        this.view = 'error';
        this.alertOpen = true;
      } else {
        this.pipelineCreated = n.data;
        this.creationMessage = n.message;
      }
		}).catch((error) => {
			console.info('Request failed', error);
			this.errorMessage = 'Data Pipeline Creation Status request failed with error: '+ error;
			this.view = 'error';
			this.alertOpen = true;
		});
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
					this.$data.revert('newPipeline');
          this.initializeCreatePipelineForm();
          this.getPipelineDetailsForProject();
					this.successMessage = n.message;
					this.alertOpen = true;
					this.$validations.resetValidation('newPipeline');
					this.isOpenModal = false;
					this.pipelineCreated = n.data;
					if(n.code === '202'){            
            var creationInterval = setInterval( () => {
              this.getPipelineCreationStatus(this.pipelineCreated.pipelineId.uuid);
              if(this.pipelineCreated.artifactStatus.status === "ACTIVE"){
                this.successMessage = "The pipeline "+this.pipelineCreated.pipelineId.name+" is successfully created";
                this.alertOpen = true;
                this.getPipelineDetailsForProject();
                clearInterval(creationInterval);
              } else if(this.pipelineCreated.artifactStatus.status === "FAILED"){
                this.errorMessage = "Your pipeline "+this.pipelineCreated.pipelineId.name+" request got failed";
                this.alertOpen = true;
                this.getPipelineDetailsForProject();
                clearInterval(creationInterval);
              } 
            }, this.createTimeout);
          }
        } else {
          this.$data.set('createErrorMessage', n.message);
        }
	    }).catch((error) => {
		      console.error('Request failed', error);
		      this.$data.set('createErrorMessage', 'Data Pipeline create request failed with error: '+ error);
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
					this.errorMessage = 'Data Pipeline archive request failed with error: '+ error;
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
					this.$data.revert('linkPipeline');
          this.initializeCreatePipelineForm();
					this.getPipelineDetailsForProject();
		
					this.$data.revert('linkPipeline');
					this.$validations.resetValidation('linkPipeline');
          this.isOpenModalLink = false;
        } else {
          this.$data.set('associateErrorMessage', n.message);
        }
		}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessge = 'Update Data Pipeline request failed with error: '+ error;
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
	      this.errorMessage = 'Data Pipeline unarchive request failed with error: '+ error;
	      this.alertOpen = true;
    });
  }

	deletePipeline() {
    const url = this.componenturl + '/api/pipeline/delete';
    this.resetMessage();
	  fetch(url, {
		  method: 'DELETE',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
          "auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.pipelinemSURL,
        "pipelineId" : this.selectedPipelineId,
        "userName": this.userName      		  
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.getPipelineList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenDeleteDialog = false;
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Data Pipeline delete request failed with error: '+ error;
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
      this.errorMessage = 'Data Pipeline fetch request failed with error: '+ error;
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
				"pipelineId" : this.selectedPipelineId,
				"pipelinePayload": this.data.editPipeline
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success') {
					this.successMessage = n.message;
					this.$data.revert('editPipeline');
					this.initializeCreatePipelineForm();
					this.getPipelineDetailsForProject();
					
					this.$data.revert('editPipeline');
					this.$validations.resetValidation('editPipeline');
					this.isOpenModalEdit = false;
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
	
	launchPipeline(pipelineId){
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
				"pipelineId" : pipelineId,
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

	 this.activeSort = key;
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
		this.$data.revert('newPipeline');
		this.$validations.resetValidation('newPipeline');
		
		this.$data.revert('editPipeline');
		this.$validations.resetValidation('editPipeline');
		
		this.$data.revert('linkPipeline');
		this.$validations.resetValidation('linkPipeline');
		
		this.$data.set('createErrorMessage', '');
		this.$data.set('associateErrorMessage', '');
		this.$data.set('editErrorMessage', '');

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
		
		this.$data.set('linkPipeline.pipelineId.name', '');
		this.$data.set('linkPipeline.pipelineId.versionId.label', '');
  }
  
  openModalEdit(item) {
		this.selectedPipelineId = item.pipelineId;
	  this.$data.set('editPipeline.pipelineId.name', item.name, true);
    this.$data.set('editPipeline.pipelineId.versionId.label', item.version, true);
		this.$data.set('editPipeline.description', item.description, true);

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
				.hide {
          display: none;
        }
        .show {
          display: block;
        }
			</style>
			<omni-dialog  title="Archive ${this.selectedPipelineName}" close-string="Archive Data Pipeline" dismiss-string="Cancel"
		 		is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archivePipeline}" type="warning">
       		<form><P>Are you sure want to archive ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

			<omni-dialog title="Unarchive ${this.selectedPipelineName}" close-string="Unarchive Data Pipeline" dismiss-string="Cancel"
				is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restorePipeline}" type="warning">
        	<form><P>Are you sure want to unarchive ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

			<omni-dialog title="Delete ${this.selectedPipelineName}" close-string="Delete Data Pipeline" dismiss-string="Cancel"
				is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
        @omni-dialog-closed="${this.deletePipeline}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-modal title="Create Data Pipeline" close-string="Create Data Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenModal}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosed}"
        canClose="${this.$validations.$valid('newPipeline') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Data Pipeline Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Data Pipeline Name" .value="${this.data.newPipeline.pipelineId.name}"
                  @keyup="${ e => {
                    this.$data.set('newPipeline.pipelineId.name', e);
                    this.$validations.validate('newPipeline.pipelineId.name');
                  }
                }"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.pipelineId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Data Pipeline Name is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Data Pipeline Name should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number</div>`
                    }
                  })
                }
              </div>
            </div>
					</div>
					<br/>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Data Pipeline Description</label>
                <textarea class="form-control" placeholder="Enter Data Pipeline Description" .value="${this.data.newPipeline.description}"
									@keyup="${e => {
										this.$data.set('newPipeline.description', e)
										this.$validations.validate('newPipeline.description');
									}}">
								</textarea>
              </div>
            </div>
          </div>
        </form>
      </omni-modal>
      
      <omni-modal title="Associate Data Pipeline" close-string="Associate Data Pipeline" dismiss-string="Cancel"
				is-open="${this.isOpenModalLink}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedLink}"
				canClose="${this.$validations.$valid('linkPipeline') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.associateErrorMessage} </p>

					<div class="row">
						<div class="col">
							<div class="form-group">
								<label>Data Pipeline Name <small class="text-danger">*</small></label>
									<select class="form-control" id="selectPipeline" 
										@change="${e => {
											this.linkPipeline(e.target.value);
											this.$data.set('linkPipeline.pipelineId.name', e);
											this.$validations.validate('linkPipeline.pipelineId.name');
										}}">
										<option value="" ?selected="${this.data.linkPipeline.pipelineId.name === ''}">Select a Pipeline</option>
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
												return html`<div class="invalid-feedback d-block">Data Pipeline Type is required</div>`

										}
									})
								}
							</div>
						</div>
						<div class="col">
							<div class="form-group">
								<label>Data Pipeline Version <small class="text-danger">*</small></label>
									<input type="text" class="form-control" placeholder="${this.data.linkPipeline.pipelineId.versionId.label}" 
									.value="${this.data.linkPipeline.pipelineId.versionId.label}" disabled/>
							</div>
						</div>
					</div>	
				</form>
			</omni-modal>
	
			<omni-modal title="Edit Data Pipeline" close-string="Update Data Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenModalEdit}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedEdit}"
        canClose="${this.$validations.$valid('editPipeline') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.editErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Data Pipeline Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Data Pipeline Name" .value="${this.data.editPipeline.pipelineId.name}"
                  @keyup="${ e => {
                    this.$data.set('editPipeline.pipelineId.name', e);
                    this.$validations.validate('editPipeline.pipelineId.name');
                  }
                }"
                />
                ${
                  this.$validations.getValidationErrors('editPipeline.pipelineId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Data Pipeline Name is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Data Pipeline Name should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number</div>`
                    }
                  })
                }
              </div>
						</div>
						<div class="col">
              <div class="form-group">
                <label>Data Pipeline Version</label>
								<input type="text" class="form-control" placeholder="Enter Data Pipeline Version" .value="${this.data.editPipeline.pipelineId.versionId.label}" disabled />
							</div>
						</div>
					</div>
					<br/>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Data Pipeline Description</label>
                <textarea class="form-control" placeholder="Enter Data Pipeline Description" .value="${this.data.editPipeline.description}"
									@keyup="${e => {
										this.$data.set('editPipeline.description', e)
										this.$validations.validate('editPipeline.description');
									}}">
								</textarea>
              </div>
            </div>
          </div>
        </form>
      </omni-modal>
      
      ${this.view === 'view'
        ? html`
        	<div class="row ">
					<div class="col-md-12 py-3">
						<div class="card mb-124 shadow mb-5 bg-white">
							<div class="card-header">
								<div class="row" style="margin:5px 0; margin-top: 0px;">
									<mwc-icon class="textColor">device_hub</mwc-icon>&nbsp;&nbsp;&nbsp;
									<h4 class="textColor card-title">Data Pipelines</h4>
									<div style="position: absolute; right:0" >
										<a href=${this.pipelineWikiURL} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
										${
											this.cardShow === false
											? html`
												<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = true}>
													<span class="toggle-plus-span toggle-span">+</span>
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
						<div class="card-body ${this.cardShow ? 'show' : 'hide'}">
							<div class="row" style="margin:5px 0; margin-top: 0px;">
								<div class="col-lg-12">
									${this.successMessage !== ''
										? html`
											<div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
												<a class="close" @click=${e => this.alertOpen = false}>
													<span aria-hidden="true">&nbsp;&times;</span>
												</a> <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
											</div>
										`: ``
									}
									${this.errorMessage !== ''
										? html`
											<div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
												<a class="close" @click=${e => this.alertOpen = false}>
														<span aria-hidden="true">&nbsp;&times;</span>
												</a>  <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
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
										<input type="text" class="form-control w-100" placeholder="Search Data Pipeline"
                        @input=${e => this.searchPipelines(e.target.value)}/>
										<div class="input-group-append">
											<a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Data Pipeline Instance" >
												<mwc-icon class="mwc-icon-primary white-color">search</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.openModal()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Create Data Pipeline">
													<mwc-icon class="mwc-icon-primary">add</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.openModalLink()} class="btnIcon btn btn-sm btn-secondary  mr-1" data-toggle="tooltip" data-placement="top" title="Associate Existing Data Pipeline">
													<mwc-icon class="mwc-icon-secondary">link</mwc-icon>
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
											<th class="col-3" >Data Pipeline Name</th>
											<th class="col-2" >Version</th>
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
													<td class="col-3">${item.name}</td>
													<td class="col-2">${item.version}</td>
													<td class="col-2">
														${item.status === 'ACTIVE'
															? html`
																<span class="active-status">${item.status}</span>
															`
															: html`
																${item.status === 'IN PROGRESS'
																? html`
																	<span class="inprogress-status">${item.status}</span>
																`
																: html`
																	<span class="inactive-status">${item.status}</span>
																`}
															`
														}
													</td>
													<td class="col-2">${item.createdTimestamp}</td>
													<td class="col-2" style="padding: .05rem; padding-left: 20px;">
														${item.status == 'ACTIVE' 
														? html`
														<a href="javascript:void" @click=${(e) => this.launchPipeline(item.pipelineId)}   class="btnIcon btn btn-sm btn-primary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Data Pipeline">
																<mwc-icon class="mwc-icon-primary">launch</mwc-icon>
														</a>&nbsp;&nbsp;
														<a href="javascript:void" @click=${e => this.openModalEdit(item)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1 " data-toggle="tooltip" data-placement="top" title="Edit Data Pipeline">
																<mwc-icon class="mwc-icon-secondary">edit</mwc-icon>
														</a>&nbsp;&nbsp;
														<a href="javascript:void" @click=${(e) => this.openArchiveDialog(item.pipelineId, item.name)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Data Pipeline">
																<mwc-icon class="mwc-icon-secondary">archive</mwc-icon>
														</a>
														`
														: html`
															${item.status == 'ARCHIVED' 
															? html`
																<a href="javascript:void" @click="${e => this.openRestoreDialog(item.pipelineId, item.name)}"
																	class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Data Data Pipeline">
																	<mwc-icon class="mwc-icon-secondary">restore_from_trash</mwc-icon>
																</a>
																<a href="javascript:void" @click=${(e) => this.openDeleteDialog(item.pipelineId, item.name)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Data Pipeline">
																	<mwc-icon class="mwc-icon-secondary">delete</mwc-icon>
																</a>
															`
															: html`
																${item.status == "FAILED"
																? html`
																	<a href="javascript:void" @click=${(e) => this.openDeleteDialog(item.pipelineId, item.name)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Data Pipeline">
																		<mwc-icon class="mwc-icon-secondary">delete</mwc-icon>
																	</a>
																`
																: ``}
															`
															}
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
								<div class="card mb-124  shadow mb-5 bg-white">
									<div class="card-header">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
											<mwc-icon class="textColor">device_hub</mwc-icon>&nbsp;&nbsp;&nbsp;
											<h4 class="textColor card-title">Data Pipelines</h4>
											<div style="position: absolute; right:0" >
												<a href=${this.pipelineWikiURL} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
												${
													this.cardShow === false
													? html`
														<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = true}>
															<span class="toggle-plus-span toggle-span">+</span>
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
									<div class="card-body ${this.cardShow ? 'show' : 'hide'}">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
											<div class="col-lg-12">
												${this.successMessage !== ''
													? html`
														<div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
															<a class="close" @click=${e => this.alertOpen = false}>
																<span aria-hidden="true">&nbsp;&times;</span>
															</a> <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
														</div>
													`: ``
												}
												${this.errorMessage !== ''
													? html`
														<div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
															<a class="close" @click=${e => this.alertOpen = false}>
																	<span aria-hidden="true">&nbsp;&times;</span>
															</a>  <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
														</div>
													`: ``
												}
											</div>
										</div>
										<div class="row" style="margin:5px 0; margin-top: 0px;margin-bottom:20px;">
											<h7 >No Data Pipelines, get started by creating your first Data Pipeline</h7>
										</div>
										<div class="row" style="margin:10px 0">
											<button type="button" class="btn btn-primary" @click=${(e) => this.openModal()}>Create Data Pipeline</button>&nbsp;&nbsp;&nbsp;
											<button type="button" class="btn btn-secondary-button" @click=${(e) => this.openModalLink()}>Associate Existing Data Pipeline</button>											
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
          <div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
            <a class="close" @click=${e => this.alertOpen = false}>
                <span aria-hidden="true">&nbsp;&times;</span>
            </a>
            <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
          </div>
        `
        : html`
      `}

      ${this.view === ''
        ? html`
          <p class="success-status"> Loading Data Pipelines..</p>
        `
        : html`
      `}  	
    `;
  }
}
customElements.define('project-pipeline-element', ProjectPipelineLitElement);
