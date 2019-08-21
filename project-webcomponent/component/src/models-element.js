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
import { style } from './models-styles.js';
import { filter, get, differenceWith } from "lodash-es";
import { OmniModal, OmniDialog } from "./@workbenchcommon/components";
import { Forms, DataSource } from "./@workbenchcommon/core";
import { ValidationMixin, DataMixin, BaseElementMixin } from "./@workbenchcommon/mixins";

class ProjectModelsLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
	get dependencies() {
		return [OmniModal, OmniDialog];
	}

	static get properties() {
		return {
			message: { type: String, notify: true },
			modelsList: { type: Array, notify: true },
			cardShow: { type: Boolean },
			modelmSURL: { type: String },
			models: [],
      activeFilter: {},
      activeSort: "",
      currentPage: 0,
      totalPages: 0,
      allModelCount: {type: Number},
      componenturl: {type: String, notify: true},
			isOpenDeleteAssociationDialog: { type: Boolean },
			successMessage: {type: String },
			errorMessage: {type: String },
			alertOpen: {type: Boolean },
			projectId: { type: String, notify: true },
			isEdit:{type: Boolean, notify: true },
			view: {type: String, notify: true },
			isOpenModal: { type: Boolean },
			isOpenModalLink: { type: Boolean },
			isOpenModalEdit: { type: Boolean },
			unassociatedModels: [],
			modelWikiURL: {type: String},
			cardShow: {type: Boolean},
			userName: {type: String, notify: true},
			authToken: {type: String, notify: true},
			isAssociateModelModalOpen: {type: Boolean},
			modelCategories : [],
			isAssociateEditModalOpen: {type: Boolean},
			modelVersionsArray: [],
			selectedModelArray: [],
			modelCatalogs : [],
			selectedCatalog : {type: String },
			selectedCategory : {type: String },
		};
	}

	static get styles() {
		return [style];
	}

	constructor() {
		super();
		this.cardShow = true;
		this.view = '';
		this.models = [];
		this.modelCategories = [];
		this.modelCatalogs = [];
		this.unassociatedModels = [];
		this.modelVersionsArray = [];
		this.selectedModelArray = [];
		this.initializeAssociateModelForm();
    this.$validations.init({
      validations: {
				linkModel: {
					modelId : { 
						name: {
							isNotEmpty: Forms.validators.isNotEmpty,
						},
						versionId : {
							label: {
								isNotEmpty: Forms.validators.isNotEmpty,
							}
						}
					}
				},
				editModel: {
					modelId : { 
						versionId : {
							label: {
								isNotEmpty: Forms.validators.isNotEmpty,
							}
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
		this.models = [];
		this.modalDismissed();
		this.requestUpdate().then(() => {
		  this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
			this.getConfig();
		});
	}

	initializeAssociateModelForm(){
    this.data = {
			associateErrorMessage : "",
			editErrorMessage : "",
			linkModel:{
				modelId: {
					name : "",
					versionId : {
						comment : "",
						label : ""
					},
					metrics: {
						kv: [
							{
								key: "MODEL_TYPE_CODE",
								value: ""
							},
							{
								key: "MODEL_PUBLISH_STATUS",
								value: ""
							},
							{
								key: "CATALOG_NAMES",
								value: ""
							}		
						]
					}
				},
			},
			editModel:{
				modelId: {
					name : "",
					uuid : "",
					versionId : {
						comment : "",
						label : ""
					}
				},
				modelCatalog: "",
				modelType: ""
			},
			deleteModel:{
				modelId: {
					name : "",
					uuid : "",
					versionId: {
						comment : "",
						label : ""
					}
				},
				modelType : ""
			}
		};

		this.$data.snapshot('linkModel');
		this.$data.snapshot('editModel');

		this.$data.set('associateErrorMessage', '');
		this.$data.set('editErrorMessage', '');
		this.$data.set('linkModel.modelId.name', '');
		this.$data.set('linkModel.modelId.versionId.label', '');
		this.$data.set('linkModel.modelId.metrics.kv[2].value', '');
		this.$data.set('linkModel.modelId.metrics.kv[0].value', '');
		this.$data.set('editModel.modelId.name', '');
		this.$data.set('editModel.modelId.uuid', '');
		this.$data.set('editModel.modelId.versionId.label', '');
		this.$data.set('editModel.modelCatalog', '');
		this.$data.set('editModel.modelType', '');
		this.$data.set('deleteModel.modelId.name', '');
		this.$data.set('deleteModel.modelId.uuid', '');
    this.$data.set('deleteModel.modelId.versionId.label', '');
		this.$data.set('deleteModel.modelType', '');
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
			this.modelmSURL = envVar.msconfig.modelmSURL;
			this.modelWikiURL = envVar.wikiConfig.modelWikiURL;
			this.portalBEUrl = envVar.portalBEUrl;
			let username = envVar.userName;
			let token = envVar.authToken;
			this.getModelList();
			if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
				this.getModelCatalogs();
				
			} else if(username && username !== '' && token && token !== '') {
				this.authToken = token;
				this.userName = username;
				this.getModelCatalogs();
			} else {
				this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
				this.alertOpen = true;
				this.view = 'error';        
			}
		}).catch((error) => {
			console.info('Request failed', error);
			this.errorMessage = 'Unable to retrive model configuration information. Error is: '+ error;
			this.view = 'error';
			this.alertOpen = true;
		});
	}
	
	resetMessage(){
		this.successMessage = '';
		this.errorMessage = '';
	}

	getModelCategories(){
		const url = this.componenturl + '/api/models/category';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken
      },
      body: JSON.stringify({
        "url": this.portalBEUrl,
        "userName": this.userName
      }),
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
					this.errorMessage = n.message;
        } else {
					this.modelCategories = n.data;
					this.getModelDetailsForProject(true);
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Model count fetch request failed with error: '+ error;
    });
	};

	getModelCatalogs(){
		const url = this.componenturl + '/api/models/catalog';
		fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken
			},
      body: JSON.stringify({
        "url": this.portalBEUrl,
				"userName": this.userName,
				"request_body": {
					"fieldToDirectionMap": {
						"created":"ASC"
					},
					"page":0
				}
      }),
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
					this.errorMessage = n.message;
					this.getModelCategories();
        } else {
					this.modelCatalogs = n.data;
					this.getModelCategories();
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Model count fetch request failed with error: '+ error;
    });
	};

	getModelDetailsForProject(reset){
		const url = this.componenturl + '/api/models/getProjectModels';
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
				"url": this.modelmSURL,
				"projectId" : this.projectId,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((response) => {
				if(response.status === 'Error'){
          this.errorMessage = response.message;
					this.alertOpen = true;
					//change
					this.view = 'add';
        } else {
          this.modelsList = [];
					this.models = [];
					this.cardShow = true;
          this.convertModelObject(response.data);
        }
	    }).catch((error) => {
		      console.info('Request failed', error);
		      this.errorMessage = 'Models fetch request for project failed with error: '+ error;
		      this.view = 'error';
		      this.alertOpen = true;
	    });
	}
	
	convertModelObject(modelsInfo){
    let tempModel;
    modelsInfo.forEach(item => {
      tempModel = {};
      tempModel.modelId = item.modelId.uuid;
      tempModel.name = item.modelId.name;
      tempModel.version = item.modelId.versionId.label;
      tempModel.createdTimestamp = item.modelId.versionId.timeStamp;
      tempModel.createdBy = item.owner.authenticatedUserId;
      tempModel.description = item.description;
			tempModel.status = item.artifactStatus.status;
			if(item.modelId.metrics.kv[0].value === undefined){
				tempModel.modelType = "Others";
			} else{
				tempModel.modelType = this.modelCategories.filter(category => {
					return category.code === item.modelId.metrics.kv[0].value;
				});
			}
			if(item.modelId.metrics.kv[2].value === "null"){
				tempModel.modelCatalog = "None";
			} else{
				tempModel.modelCatalog = item.modelId.metrics.kv[2].value;
			}
			tempModel.publishStatus = item.modelId.metrics.kv[1].value;
      this.modelsList.push(tempModel);
    });
    this.displayModels();
	}

	associateModel(){
		const url = this.componenturl + '/api/models/associateModel';
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
				"userName": this.userName,
				"url": this.modelmSURL,
				"projectId" : this.projectId,
				"modelId" : this.selectedModelId,
				"modelPayload": this.data.linkModel
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success'){
        	this.successMessage = n.message;
          this.alertOpen = true;
          this.initializeAssociateModelForm();
          this.getModelDetailsForProject();
					this.$data.revert('linkModel');
					this.$validations.resetValidation('linkModel');
					this.isOpenModalLink = false;
        } else {
          this.$data.set('associateErrorMessage', n.message);
        }
		}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessge = 'Update Model request failed with error: '+ error;
				this.alertOpen = true;
		});
	}

	deleteModelAssociation() {
    const url = this.componenturl + '/api/models/deleteAssociateModel';
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
        "url": this.modelmSURL,
        "modelId" : this.selectedModelId,
        "userName": this.userName      		  
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.getModelDetailsForProject();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenDeleteDialog = false;
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Model delete request failed with error: '+ error;
      this.alertOpen = true;
    });
  }

	getModelList(){
    const url = this.componenturl + '/api/models/getAllModels';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
					"Content-Type": "application/json",
					"auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.modelmSURL,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessage = n.message;
          this.view = 'error';
          this.alertOpen = true;
        } else {
          this.allModelss = [];
          this.convertAllModelObject(n.data);
				}
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessage = 'Model fetch request failed with error: '+ error;
      this.view = 'error';
      this.alertOpen = true;
    });
  }

	updateModel(){
		const url = this.componenturl + '/api/models/updateAssociateModel';
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
				"url": this.modelmSURL,
				"modelId" : this.selectedModelId,
				"modelPayload": this.data.editModel
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success'){
					this.successMessage = n.message;
					this.initializeAssociateModelForm();
					this.getModelDetailsForProject();
					
					this.$data.revert('editModel');
					this.$validations.resetValidation('editModel');
					this.isOpenModalEdit = false;
				} else{
					this.$data.set('editErrorMessage', n.message);
				}
				this.alertOpen = true;
		}).catch((error) => {
			console.info('Request failed', error);
			this.errorMessage = 'Update model request failed with error: '+ error;
			this.alertOpen = true;
		});
	}

	convertAllModelObject(allModelsInfo){
		let tempModel;
		let allModels = [];
    allModelsInfo.forEach(item => {
      tempModel = {};
      tempModel.modelId = item.modelId.uuid;
      tempModel.name = item.modelId.name;
      tempModel.version = item.modelId.versionId.label;
      tempModel.createdTimestamp = item.modelId.versionId.creationTimeStamp;
      tempModel.createdBy = item.owner.authenticatedUserId;
      tempModel.description = item.description;
      tempModel.status = item.artifactStatus.status;
      if(item.modelId.metrics.kv[0].value === undefined){
				tempModel.modelType = "Others";
			} else{
				tempModel.modelType = this.modelCategories.filter(category => {
					return category.code === item.modelId.metrics.kv[0].value;
				});
			}
			tempModel.modelCatalog = item.modelId.metrics.kv[2].value;
			tempModel.publishStatus = item.modelId.metrics.kv[1].value;
      allModels.push(tempModel);
    });
    this.getAllModels(allModels);
	}

	getAllModels(nbList){
		this.unassociatedModels = differenceWith(nbList, this.models, function(model, projectModel) {
			return (model.modelId === projectModel.modelId && model.version === projectModel.version) ;
		});
	}

	filterModelsFromCatalog(selectedModelCatalog){
		this.selectedCatalog = selectedModelCatalog;
		if(selectedModelCatalog !== "null"){
			this.modelsDropdown = this.unassociatedModels.filter(model => {
				if(model.modelCatalog !== "null"){
					model.modelCatalog = model.modelCatalog.split(",");
					for (let catalog of model.modelCatalog){
						if(catalog === selectedModelCatalog)
							return true;
					}
				} 
			});
		} else{
			this.modelsDropdown = this.unassociatedModels.filter(model => {
				return model.modelCatalog === "null";
			});
		}
	}

	filterModelsToSelect(selectedModelCategory){
		this.modelsDropdown = this.modelsDropdown.filter(model => {
			return model.modelType[0].name === selectedModelCategory;
		});
	}

	filterVersionsToSelect(modelSelected){
		this.selectedModelArray = this.modelsDropdown.filter(model => {
			return model.name === modelSelected;
		});
	}

	linkModel(versionSelected){
		let selectedModel = this.selectedModelArray.filter(model => {
			return model.version === versionSelected;
		});
		
		this.selectedModelId = selectedModel[0].modelId;
		this.$data.set('linkModel.modelId.name', selectedModel[0].name);
    this.$data.set('linkModel.modelId.versionId.label', selectedModel[0].version);
		this.$data.set('linkModel.description', selectedModel[0].description);
		this.$data.set('linkModel.modelId.metrics.kv[0].value', selectedModel[0].modelType[0].code);
	}

	userAction(action, modelId, modelName) {
  }

 displayModels() {
    this.activeFilter = {};
    this.activeSort = "created";

    this.dataSource = new DataSource({
      data: this.modelsList,
      filter: this.activeFilter,
      sort: this.activeSort,
      pageSize: 5
    });
    this.sortModels( this.activeSort);
    this.models = this.dataSource.data;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
    this.totalModels = this.modelsList.length;
    this.allModelCount = this.getFilteredCount();

    if(this.totalModels > 0){
      this.view = 'view';
    }else {
      this.view = 'add';
    }
  }

 filterModels(criteria) {
   this.activeFilter = criteria;
   this.dataSource.page = 0;
   this.currentPage = this.dataSource.page + 1;
   this.dataSource.filter(criteria);
   this.models = this.dataSource.data;
   this.totalPages = this.dataSource.totalPages;
 }
 
 sortModels(key) {
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
   this.models = this.dataSource.data;
 }

	searchModels(searchCriteria) {
    this.dataSource.search(searchCriteria);
    this.models = this.dataSource.data;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
  }
	
	navigatePage(direction) {
    this.dataSource.navigatePage(direction);

    this.currentPage = this.dataSource.page + 1;
    this.models = this.dataSource.data;
  }

  getFilteredCount(criteria) {
    return filter(this.dataSource._rawData, criteria).length;
  }

  modalDismissed() {		
		this.$data.revert('editModel');
		this.$validations.resetValidation('editModel');
		
		this.$data.revert('linkModel');
		this.$validations.resetValidation('linkModel');

		this.$data.set('associateErrorMessage', '');
		this.$data.set('editErrorMessage', '');

    this.isAssociateModelModalOpen = false;
		this.isAssociateEditModalOpen = false;
  }
	
	associateModelModalOpen() {
		this.isAssociateModelModalOpen = true;
		this.isAssociateEditModalOpen = false;
	}
	
	modalClosedLink(){
		this.requestUpdate();
    this.associateModel();
	}
	
	modalClosedEdit(){
		this.requestUpdate();
    this.updateModel();
	}
  
  openModalEdit(item) {
		this.selectedModelId = item.modelId;
	  this.$data.set('editModel.modelId.name', item.name, true);
		this.$data.set('editModel.modelId.versionId.label', item.version, true);
		this.$data.set('editModel.modelId.uuid', item.modelId, true);
	  this.$data.set('editModel.description', item.description, true);
		this.$data.set('editModel.modelType', item.modelType, true);
		
		this.modelVersionsArray = this.unassociatedModels.filter(model => {
			return model.name === item.name;
		});
	  this.isOpenModalEdit = true;
	  this.isAssociateModelModalOpen = false;
  }

  deleteAssociationDialogDismissed(){
    this.isOpenDeleteAssociationDialog = false;
  }

	openDeleteAssociationDialog(modelId, modelName){
		this.selectedModelId = modelId;
		this.selectedModelName = modelName;
		this.isOpenDeleteAssociationDialog = true;
	}

	deleteModelAssociationClosed(){
		this.requestUpdate();
    this.deleteModelAssociation();
	}

	getStatusView(state) {
		switch (state.type) {
			case 'error':
				return html`
					<div class="col-md-12">
						<div class="alert alert-danger">Error: ${state.msg}</div>
					</div>`
		}
	}
	getModelsView(state) {
		switch (state.type) {
			case 'loading':
				return html`<div class="col-md-12">${state.msg}</div>`
			case 'empty':
				return html`
					<div class="col-md-12">${state.msg}</div>
					<div class="col-md-12"><button class="btn btn-sm btn-outline-dark mt-4" @click=${() => this.associateModelModalOpen()}>Associate Model</button></div>`
		}
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
			
			<omni-dialog  title="Delete ${this.selectedModelName} Association" close-string="Delete Model Association" dismiss-string="Cancel"
		 		is-open="${this.isOpenDeleteAssociationDialog}" @omni-dialog-dimissed="${this.deleteAssociationDialogDismissed}"
        @omni-dialog-closed="${this.deleteModelAssociationClosed}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedModelName} Association?</p></form>
			</omni-dialog>
			
		<omni-modal title="Associate Model" close-string="Associate Model" dismiss-string="Reset"
			is-open="${this.isAssociateModelModalOpen}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedLink}"
			canClose="">
			<form novalidate>
				<p class="text-danger">{{error}}</p>
				<div class="row">
					<div class="col">
						<div class="form-group">
							<label>Model Catalog <small class="text-danger">*</small></label>
							<select class="form-control" id="mySelectCatalog" @change="${e => {
								this.filterModelsFromCatalog(e.target.value);
								this.$data.set('linkModel.modelId.metrics.kv[2].value', e.target.value);
								this.$validations.validate('linkModel.modelId.metrics.kv[2].value');
								}}">
								<option value="">Select Model Catalog</option>	
								${this.modelCatalogs.map((item, index) =>
									html`															
										<option value="${item.name}">${item.name}</option>
									`
								)} 
								<option value="null">None</option>                 	
							</select>
							${
								this.$validations.getValidationErrors('linkModel.modelId.metrics.kv[2].value').map(error => {
									switch (error) {
										case 'isNotEmpty':
											return html`<div class="invalid-feedback d-block">Model Catalog is required</div>`
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
							<label>Model Category <small class="text-danger">*</small></label>
							${(this.data.linkModel.modelId.metrics.kv[2].value !== undefined && this.data.linkModel.modelId.metrics.kv[2].value !== '')
							? html`
								<select class="form-control" id="mySelect" @change="${e => {
									this.filterModelsToSelect(e.target.value);
									this.$data.set('linkModel.modelId.metrics.kv[0].value', e.target.value);
									this.$validations.validate('linkModel.modelId.metrics.kv[0].value');
									}}">
									<option value="">Select Model Category</option>	
									${this.modelCategories.map((item, index) =>
										html`															
											<option value="${item.name}">${item.name}</option>
										`
									)} 
									<option value="Others">Others</option>                 	
								</select>
								${
									this.$validations.getValidationErrors('linkModel.modelId.metrics.kv[0].value').map(error => {
										switch (error) {
											case 'isNotEmpty':
												return html`<div class="invalid-feedback d-block">Model Category is required</div>`
										}
									})
								}
							`
								: html`
								<select class="form-control" id="mySelect" disabled/>
							`
						}
						</div>
					</div>
				</div>
				<br/>
				<div class="row">
					<div class="col">
						<div class="form-group">
							<label>Model Name <small class="text-danger">*</small></label>
							${(this.data.linkModel.modelId.metrics.kv[0].value !== undefined && this.data.linkModel.modelId.metrics.kv[0].value !== "null" && this.data.linkModel.modelId.metrics.kv[0].value !== '')
							? html`
								<select class="form-control" id="selectModel" @change="${e => { 
									this.filterVersionsToSelect(e.target.value);
									this.$data.set('linkModel.modelId.name', e);
									this.$validations.validate('')
								}}">
									<option value="">Enter Model Name</option>
									${this.modelsDropdown.map((item, index) =>
										html`															
											<option value="${item.name}">${item.name}</option>
										`
									)}
								</select>
								${
									this.$validations.getValidationErrors('linkModel.modelId.name').map(error => {
											switch (error) {
												case 'isNotEmpty':
													return html`<div class="invalid-feedback d-block">Please select Model from dropdown</div>`
						
											}
										})
										}
								`
								: html`
								<select class="form-control" id="selectModel" disabled/>
							`
						}
					</div>
				</div>
									
					<div class="col">
						<div class="form-group">
							<label>Model Version <small class="text-danger">*</small></label>
							${(this.data.linkModel.modelId.name !== undefined && this.data.linkModel.modelId.name !== "null" && this.data.linkModel.modelId.name !== '')
							? html`
							<select class="form-control" id="selectVersion" @change="${e => { 
								this.linkModel(e.target.value);
								this.$data.set('linkModel.modelId.versionId.label', e);
								this.$validations.validate('')
							}}">
								<option value="">Enter Model Version</option>
								${this.selectedModelArray.map((item, index) =>
									html`															
										<option value="${item.version}">${item.version}</option>
									`
								)}
							</select>
							${
								this.$validations.getValidationErrors('linkModel.modelId.versionId.label').map(error => {
										switch (error) {
											case 'isNotEmpty':
												return html`<div class="invalid-feedback d-block">Please select Model version from dropdown</div>`
					
										}
									})
									}
							`
							: html`
								<input type="text" class="form-control" placeholder="Model Version" value="${this.data.linkModel.modelId.versionId.label}"  disabled/>
								`
		}
						</div>
					</div>
				</div>	
			</form>
		</omni-modal>

		<omni-modal title="Edit Model Association" close-string="Save Model Association" dismiss-string="Reset"
		is-open="${this.isAssociateEditModalOpen}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedEdit}"
		canClose="${this.$validations.$valid('editModel') && this.$validations.$dirty}">
		<form novalidate>
			<p class="text-danger">{{error}}</p>
			<div class="row">
			<div class="col">
				<div class="form-group">
					<label>Model Catalog <small class="text-danger">*</small></label>
					<input type="text" class="form-control" placeholder="Model Catalog" value="${this.data.editModel.modelCatalog}"  disabled/>
				</div>
			</div>
		</div>
		<br/>
			<div class="row">
				<div class="col">
					<div class="form-group">
						<label>Model Category <small class="text-danger">*</small></label>
						<input type="text" class="form-control" placeholder="Model Category" value="${this.data.editModel.modelType}"  disabled/>
					</div>
				</div>
			</div>
			<br/>
			<div class="row">
				<div class="col">
					<div class="form-group">
						<label>Model Name <small class="text-danger">*</small></label>
						<input type="text" class="form-control" placeholder="Model Name" value="${this.data.editModel.modelId.name}"  disabled/>
				</div>
			</div>
								
				<div class="col">
					<div class="form-group">
						<label>Model Version <small class="text-danger">*</small></label>
						<select class="form-control" id="selectVersion" @change="${e => { 
							this.editModelAssociation(e.target.value);
							this.$data.set('linkModel.modelId.versionId.label', e);
							this.$validations.validate('')
						}}">
							<option value="">Enter Model Version</option>
							${this.modelVersionsArray.map((item, index) =>
								html`															
									<option value="${item.version}">${item.version}</option>
								`
							)}
						</select>
						${
							this.$validations.getValidationErrors('linkModel.modelId.versionId.label').map(error => {
									switch (error) {
										case 'isNotEmpty':
											return html`<div class="invalid-feedback d-block">Please select Model version from dropdown</div>`
				
									}
								})
								}
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
									<mwc-icon class="textColor">import_contacts</mwc-icon>&nbsp;&nbsp;&nbsp;
									<h4 class="textColor card-title">Models</h4>
									<div style="position: absolute; right:0" >
										<a href=${this.modelWikiURL} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
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
												</a> <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
											</div>
										`: ``
									}
				        </div>
				      </div>
							<div  class="row" style="margin:5px 0; margin-top: 0px; position: absolute; right:0">								
								<div class="btn-toolbar mb-2 mb-md-0">
									<div class="dropdown">
										<select class="custom-select mr-sm-2" id="template" @change=${e => this.sortModels(e.target.value)}>
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
										<input type="text" class="form-control w-100" placeholder="Search Model"
												@input=${e => this.searchModels(e.target.value)}/>
										<div class="input-group-append">
											<a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Model Instance" >
												<mwc-icon class="mwc-icon-primary white-color">search</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.associateModelModalOpen()} class="btnIcon btn btn-sm btn-secondary  mr-1" data-toggle="tooltip" data-placement="top" title="Associate Model">
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
											<th class="col-2" >Model Name</th>
											<th class="col-1" >Status</th>
											<th class="col-2" >Model Category</th>
											<th class="col-2" >Version</th>
											<th class="col-2" >Model Publish Status</th>
											<th class="col-2">Actions</th>
										</tr>
									</thead>
									<tbody>
										${this.models.length !== 'undefined' 
										? html`
											${this.models.map((item, index) => 
												html`
												<tr class="d-flex">
													<td class="col-1">${(this.currentPage-1) * 5 + ++index}</td>
													<td class="col-2">${item.name}</td>
													<td class="col-1">${item.status}</td>
													<td class="col-2">${item.modelType[0].name}</td>
													<td class="col-2">${item.version}</td>
													<td class="col-2">${item.publishStatus === 'true'
													? html`
													<mwc-icon class="mwc-icon-primary">backup</mwc-icon>
													`
													: html`
													<mwc-icon class="mwc-icon-secondary">backup</mwc-icon>
													`
												}</td>
													<td class="col-2" style="padding: .05rem; padding-left: 20px;">
													${item.status == 'ACTIVE' 
													? html`
														<a href="javascript:void" @click=${(e) => this.openModalEdit(item)}   class="btnIcon btn btn-sm btn-primary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Edit Model Association">
																<mwc-icon class="mwc-icon-primary">edit</mwc-icon>
														</a>&nbsp;
														<a href="javascript:void" @click=${e => this.userAction(item)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1 " data-toggle="tooltip" data-placement="top" title="view Model" disabled>
																<mwc-icon class="mwc-icon-secondary">visibility</mwc-icon>
														</a>&nbsp;
														<a href="javascript:void" @click=${(e) => this.openDeleteAssociationDialog(item.modelId, item.name)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Model Association">
																<mwc-icon class="mwc-icon-secondary">link_off</mwc-icon>
														</a>
														`
														: html`
															
															<a href="javascript:void" @click="${e => this.openDeleteAssociationDialog(item.modelId, item.name)}"
																class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Model Association">
																<mwc-icon class="mwc-icon-secondary">link_off</mwc-icon>
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
								<div class="card mb-124  shadow mb-5 bg-white">
									<div class="card-header">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
											<mwc-icon class="textColor">import_contacts</mwc-icon>&nbsp;&nbsp;&nbsp;
											<h4 class="textColor card-title">Models</h4>
											<div style="position: absolute; right:0" >
												<a href=${this.modelWikiURL} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
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
											<h7>No Models</h7>
										</div>
										<div class="row" style="margin:10px 0">
											<button type="button" class="btn btn-primary" @click=${e => this.associateModelModalOpen()}>Associate Model</button>											
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
          <p class="success-status"> Loading Models..</p>
        `
        : html`
      `}  	
    `;
  }
}
customElements.define('project-models-element', ProjectModelsLitElement);
