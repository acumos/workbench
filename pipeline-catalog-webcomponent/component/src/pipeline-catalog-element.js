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

import { LitElement, html } from "lit-element";
import { filter, get } from "lodash-es";
import { OmniModal, OmniDialog } from "./@workbenchcommon/components";
import { Forms, DataSource } from "./@workbenchcommon/core";

import { ValidationMixin, DataMixin, BaseElementMixin } from "./@workbenchcommon/mixins";
import { style } from "./pipeline-catalog-styles.js";

export class PipelineCatalogLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
  get dependencies() {
    return [OmniModal, OmniDialog];
  }

  static get properties() {
    return {
      isOpenModal: { type: Boolean },
      isOpenArchiveDialog: { type: Boolean },
      isOpenDeleteDialog: { type: Boolean },
      isOpenRestoreDialog: { type: Boolean },
      pipelines: [],
      activeFilter: {},
      activeSort: "",
      currentPage: 0,
      totalPages: 0,
      mSurl: {type: String},
      view: {type: String},
      activePipelineCount: {type: Number},
      archivePipelineCount: {type: Number},
      allPipelineCount: {type: Number},
      componenturl: {type: String, notify: true},
      alertOpen: { type: Boolean },
      successMessage: { type: String },
      errorMessage: { type: String },
      cardShow: { type: Boolean },
      pipelineWikiURL: {type: String},
      userName: {type: String, notify: true},
      authToken: {type: String, notify: true}
    };
  }

  static get styles() {
    return [style];
  }

  onLoad() {
    this.dispatchEvent(
      new CustomEvent("on-load-event", {
      })
    );
  }

  constructor() {
    super();
    this.view = '';
    this.initializeCreatePipelineForm();
    this.$validations.init({
      validations: {
        newPipeline: {
	        pipelineId: {
            name: {
              isNotEmpty: Forms.validators.isNotEmpty,
              pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_]{5,29}$')
            },
            versionId : {
              label: {
                isNotEmpty: Forms.validators.isNotEmpty,
                pattern: Forms.validators.pattern('^[a-zA-Z0-9_.]{1,14}$')
              }
            }
          }
        }
     }
    });
    this.alertOpen = false;
    this.sortOptions = [
      { value: "created", label: "Sort By Created Date" },
      { value: "name", label: "Sort By Name" },
      { value: "id", label: "Sort By ID" }
    ];

    this.pipelineLists = [];
    this.cardShow = false;
    this.requestUpdate().then(() => {
      this.onLoad();
      this.componenturl = (this.componenturl === undefined || this.componenturl === null)? '' : this.componenturl;
      this.view = '';
      this.getConfig();
    })
  }

  initializeCreatePipelineForm(){
    this.data = {
      createErrorMessage : "",
      newPipeline:{
        pipelineId : {    
          name : "",    
          versionId : {         
            label : ""    
          }  
        },  
        description : ""
      }
    };
    this.$data.snapshot('newPipeline');
    this.$data.set('createErrorMessage', '');
    this.$data.set('newPipeline.pipelineId.name', '');
    this.$data.set('newPipeline.pipelineId.versionId.label', '');
    this.$data.set('newPipeline.description', '');
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
        this.mSurl = envVar.msconfig.pipelinemSURL;
        this.pipelineWikiURL = envVar.pipelineWikiURL;

        let username = envVar.userName;
        let token = envVar.authToken;
        
        if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
          this.resetActiveFilter = true;
          this.getPipelineList();
        } else if(username && username !== '' && token && token !== '') {
          this.authToken = token;
          this.userName = username;
          this.resetActiveFilter = true;
          this.getPipelineList();
        } else {
          this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
          this.alertOpen = true;
          this.view = 'error';        
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessage = 'Unable to retrive configuration information. Error is: '+ error;
      this.alertOpen = true;
      this.view = 'error';
    });
  }

  resetMessage(){
    this.successMessage = '';
    this.errorMessage = '';
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
        "url": this.mSurl,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessage = n.message;
          this.view = 'error';
          this.alertOpen = true;
        } else {
          this.pipelineLists = [];
          this.pipelines = [];
          this.cardShow = true;
          this.convertPipelineObject(n.data);
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessage = 'Data Pipeline fetch request failed with error: '+ error;
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
      this.pipelineLists.push(tempPipeline);
    });
    this.displayPipelines();
  }

  displayPipelines() {
    if(this.resetActiveFilter) {
      this.resetActiveFilter = false;
      this.activeFilter = { status: "ACTIVE" };
    }
    this.activeSort = "created";

    this.dataSource = new DataSource({
      data: this.pipelineLists,
      filter: this.activeFilter,
      sort: this.activeSort,
      pageSize: 8
    });
    this.sortPipelines( this.activeSort);
    this.pipelines = this.dataSource.data;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
    this.totalPipelines = this.pipelineLists.length;
    this.allPipelineCount = this.getFilteredCount();
    this.activePipelineCount = this.getFilteredCount({ status: "ACTIVE" });
    this.archivePipelineCount = this.getFilteredCount({ status: "ARCHIVED" });

    if(this.totalPipelines > 0){
      this.view = 'view';
    }else {
      this.view = 'add';
    }
  }

  createPipeline(){
    const url = this.componenturl + '/api/pipeline/create';
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
        "newPipelineDetails": this.data.newPipeline,
        "userName": this.userName 
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.alertOpen = true;
          this.$data.revert('newPipeline');
          this.$validations.resetValidation('newPipeline');
          this.initializeCreatePipelineForm();
          this.getPipelineList();
          this.isOpenModal = false;
        } else {
          this.$data.set('createErrorMessage', n.message);
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.$data.set('createErrorMessage', 'Data Pipeline create request failed with error: '+ error);
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
        "url": this.mSurl,
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

  archivePipeline() {
    const url = this.componenturl + '/api/pipeline/archive';
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
        "url": this.mSurl,
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
        this.isOpenArchiveDialog = false;         
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Data Pipeline archive request failed with error: '+ error;
      this.alertOpen = true;
    });
  }

  restorePipeline() {
    const url = this.componenturl + '/api/pipeline/restore';
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
        "url": this.mSurl,
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
        this.isOpenRestoreDialog = false;        
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Data Pipeline unarchive request failed with error: '+ error;
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
        "url": this.mSurl,
        "pipelineId" : pipelineId,
        "userName": this.userName      		  
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

  navigatePage(direction) {
    this.dataSource.navigatePage(direction);

    this.currentPage = this.dataSource.page + 1;
    this.pipelines = this.dataSource.data;
  }

  getFilteredCount(criteria) {
    return filter(this.dataSource._rawData, criteria).length;
  }

  searchPipelines(searchCriteria) {
    this.dataSource.search(searchCriteria);
    this.pipelines = this.dataSource.data;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
  }

  modalDismissed() {
    this.$data.revert('newPipeline');
    this.$validations.resetValidation('newPipeline');
    this.$data.set('createErrorMessage', '');
    this.isOpenModal = false;
  }

  modalClosed() {
    this.requestUpdate();
    this.createPipeline();
  }

  openModal() {
    this.isOpenModal = true;
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

  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
        .alertmessage {
          display: ${this.alertOpen ? "block" : "none"};
        }
        .card-show {
          display: ${this.cardShow ? "block" : "none"};
        }
      </style>
      <omni-dialog title="Archive ${this.selectedPipelineName}" 
        close-string="Archive Data Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archivePipeline}" type="warning">
        <form><P>Are you sure want to archive ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Unarchive ${this.selectedPipelineName}" 
        close-string="Unarchive Data Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restorePipeline}" type="warning">
        <form><P>Are you sure want to unarchive ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Delete ${this.selectedPipelineName}" close-string="Delete Data Pipeline" 
        dismiss-string="Cancel" is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
        @omni-dialog-closed="${this.deletePipeline}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-modal title="Create Data Pipeline" close-string="Create Data Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenModal}" @omni-modal-dimissed="${this.modalDismissed}" 
        @omni-modal-closed="${this.modalClosed}"
        canClose="${this.$validations.$valid('newPipeline') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Data Pipeline Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Data Pipeline Name" 
                  .value="${this.data.newPipeline.pipelineId.name}"
                  @keyup="${ e => {
                    this.$data.set('newPipeline.pipelineId.name', e); 
                    this.$validations.validate('newPipeline.pipelineId.name');
                  }}"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.pipelineId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Data Pipeline name is required.</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Data Pipeline name should contain between 6 to 30 char inlcudes only alphanumeric and '_'. It should start from alphabetic character.</div>`
                    }
                  })
                }
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Data Pipeline Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Data Pipeline Version" 
                  .value="${this.data.newPipeline.pipelineId.versionId.label}"
                  @keyup="${ e => {
                      this.$data.set('newPipeline.pipelineId.versionId.label', e);
                      this.$validations.validate('newPipeline.pipelineId.versionId.label');
                    }
                  }"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.pipelineId.versionId.label').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Data Pipeline version is required.</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Data Pipeline version should contain between 1 to 14 char includes only alphanumeric, '.' and '_'.</div>`
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
                  @keyup="${e => this.$data.set('newPipeline.description', e)}"></textarea>
              </div>
            </div>
          </div>
        </form>
      </omni-modal>

      ${this.view === 'view'
        ? html`
          <div class="row" style="margin:5px 0;">
            <div class="col-lg-12">
              <div class="row">
                <div class="col-lg-12">
                  ${this.successMessage !== ''
                    ? html`
                      <div class="alertmessage alert alert-success">
                        <a  class="close" @click=${e => this.alertOpen=false}>
                          <span aria-hidden="true">&nbsp;&times;</span>
                        </a>
                        <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
                      </div>
                    `
                    :``
                  }
                  ${this.errorMessage !== ''
                    ? html`
                      <div class="alertmessage alert alert-danger">
                        <a class="close" @click=${e => this.alertOpen=false}>
                            <span aria-hidden="true">&nbsp;&times;</span>
                        </a>
                        <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
                      </div>
                    `
                    :``
                  }
                </div>
              </div>
              <div class="row" style="margin:15px 0;">
                  <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                    <div class="btn-group mr-2">
                      <button type="button" class="btn btn-primary"
                        @click="${this.openModal}">
                        Create Data Pipeline
                      </button>&nbsp;&nbsp;
                      <div class="input-group-append">
                          <a href=${this.pipelineWikiURL} target="_blank"
                            class="btnIconTop btn btn-sm btn-secondary mr-1"
                            data-toggle="tooltip"
                            data-placement="top"
                            title="Click here for wiki help">
                            <mwc-icon class="help-icon">help</mwc-icon> </a>
                        </div>
                    </div>
                  </div>
              </div>
              <br/>

              <div class="row" style="margin:5px 0; margin-top:20px;">
                <div class="btn-toolbar mb-2 mb-md-0">
                  <ul class="nav nav-pills mb-3">
                    <li class="nav-item mr-2">
                      <a href="javascript:void" @click=${e => this.filterPipelines({ status: "ACTIVE" })}
                        class="nav-link ${get(this.activeFilter, "status", "") === "ACTIVE"? "active" : ""}">
                        Active Data Pipelines&nbsp;&nbsp;
                        <span class="badge ${get(this.activeFilter, "status", "") === "ACTIVE"? "badge-light" : "badge-secondary"}"">
                          ${this.activePipelineCount}</span>
                      </a>
                    </li>
                    <li class="nav-item mr-2">
                      <a href="javascript:void"  @click=${e => this.filterPipelines({ status: "ARCHIVED" })}
                        class="nav-link ${get(this.activeFilter,"status", "") === "ARCHIVED"? "active": ""}">
                        Archived Data Pipelines&nbsp;&nbsp;
                        
                        <span class="badge ${get(this.activeFilter, "status", "") === "ARCHIVED"? "badge-light" : "badge-secondary"}"">
                          ${this.archivePipelineCount}</span>
                      </a>
                    </li>
                    <li class="nav-item mr-2">
                      <a href="javascript:void" @click=${e => this.filterPipelines()}
                        class="nav-link ${get(this.activeFilter, "status","") === ""? "active": ""}">
                        All Data Pipelines&nbsp;&nbsp;
                        <span class="badge ${get(this.activeFilter, "status", "") === ""? "badge-light" : "badge-secondary"}"">
                          ${this.allPipelineCount}</span>
                      </a>
                    </li>
                  </ul>
                  <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
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
                        <a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Data Pipeline">
                          <mwc-icon class="mwc-icon">search</mwc-icon>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row" style="margin-top:10px;">
                ${this.pipelines.map(
                  item =>
                    html`
                    <div class="col-md-3">
                      <div class="card-shadow card card-link mb-3 mb-5 bg-white">
                        <div class="card-body">
                          <div>
                            <a href="javascript:void" @click=${e => this.userAction("view-pipeline", item.pipelineId, item.name)}>
                              <h4 class="pipeline-name">${item.name}</h4>
                              <span><strong>Data Pipeline ID</strong>: &nbsp; ${item.pipelineId}</span>
                              <br />
                              <span><strong>Data Pipeline Version</strong>: &nbsp;
                                ${item.version}</span>
                              <br />
                              <strong>Data Pipeline Status</strong>: &nbsp;
                              ${item.status === "ACTIVE"
                                ? html`
                                  <span class="active-status">${item.status}</span>
                                `
                                : html`
                                  <span class="inactive-status">${item.status}</span>
                                `}
                              <br/>
                              <span><strong>Creation Date</strong>: &nbsp; ${item.createdTimestamp}</span>
                              <br/>
                              <span><strong>Modified Date</strong>: &nbsp; ${item.createdTimestamp}</span>
                              <br/><br/>
                            </a>
                          </div>

                          <div class="gray-light pt-2 pb-2 pl-2 pr-2">
                            <div class="d-flex justify-content-between align-middle">
                              <div style="margin-top:8px;">
                                <span title="${item.createdBy}"><mwc-icon class="mwc-icon-gray">account_circle</mwc-icon>
                                </span>
                              </div>
                            <div>
                            ${item.status === "ACTIVE"
                              ? html`
                                <a href="javascript:void" @click="${e => this.launchPipeline(item.pipelineId)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Data Pipeline">
                                  <mwc-icon class="mwc-icon-gray">launch</mwc-icon>
                                </a>
                                <a href="javascript:void" @click="${e => this.openArchiveDialog(item.pipelineId, item.name)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Data Pipeline" >
                                  <mwc-icon class="mwc-icon-gray">archive</mwc-icon>
                                </a>
                              `
                              : html`
                                <a href="javascript:void" @click="${e => this.openRestoreDialog(item.pipelineId, item.name)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Data Pipeline">
                                  <mwc-icon class="mwc-icon-gray">restore_from_trash</mwc-icon>
                                </a>
                            `}
                          </div>
                        </div>
                      </div>
                    </div>
                  `
                )}
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
        `
      : html`
      `}
      
      ${this.view === 'add'
        ? html`
        <div class="row" style="margin:5px 0">
          <div class="col-lg-12">
            <div class="row">
              <div class="col-lg-12">
                ${this.successMessage !== ''
                  ? html`
                    <div class="alertmessage alert alert-success">
                      <a class="close" @click=${e => this.alertOpen = false}>
                        <span aria-hidden="true">&nbsp;&times;</span>
                      </a> <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
                    </div>
                  `: ``
                }
                ${this.errorMessage !== ''
                  ? html`
                    <div class="alertmessage alert alert-danger">
                      <a class="close" @click=${e => this.alertOpen = false}>
                          <span aria-hidden="true">&nbsp;&times;</span>
                      </a>  <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
                    </div>
                  `: ``
                }
              </div>
            </div>
            <div class="row" style="margin:5px 0">
              <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0; padding-right: 5px;">
                <div class="btn-group mr-2">
                  <div class="input-group-append">
                    <a href=${this.pipelineWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1" 
                      data-toggle="tooltip" data-placement="top" title="Click here for wiki help">
                      <mwc-icon class="help-icon">help</mwc-icon>
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <br/><br/>
            <div class="row">
              <div class="col-md-12 py-3">
                <div class="card mb-124 shadow mb-5 bg-white">
                  <div class="card-header">
                    <div class="row" style="margin:5px 0; margin-top: 0px;">
                      <mwc-icon class="textColor">device_hub</mwc-icon>&nbsp;&nbsp;&nbsp;
                      <h4 class="textColor card-title">Data Pipelines</h4>
                      <div style="position: absolute; right:0">
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
                  <div class="card-body card-show">
                    <div class="row" style="margin:10px 0;margin-bottom:20px;">
                      <h7>No Data Pipelines, get started with ML Workbench by creating your first Data Pipeline.</h7>
                    </div>
                    <div class="row" style="margin:10px 0">
                      <button type="button" class="btn btn-primary" @click="${this.openModal}">
                        Create Data Pipeline
                      </button>
                    </div>
                  </div>
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
            <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
          </div>
        `
        : html`
      `}

      ${this.view === ''
        ? html`
          <p class="success-status"> Loading ..</p>
        `
        : html`
      `}  
    `;
  }
}

customElements.define("pipeline-catalog-element", PipelineCatalogLitElement);
