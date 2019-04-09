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
import { OmniModal, OmniDialog } from "./@omni/components";
import { Forms, DataSource } from "./@omni/core";

import { ValidationMixin, DataMixin, BaseElementMixin } from "./@omni/mixins";
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
      zeppelinPipelineCount: {type: Number},
      jupyterPipelineCount: {type: Number},
      allPipelineCount: {type: Number},
      componenturl: {type: String, notify: true},
      alertOpen: { type: Boolean },
      successMessage: { type: String },
      errorMessage: { type: String },
      cardShow: { type: Boolean },
      wikiUrl: {type: String},
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

    this.pipelineLists = [];
    this.cardShow = false;
    this.requestUpdate().then(() => {
      console.info('update componenturl : ' + this.componenturl);
      this.componenturl = (this.componenturl === undefined || this.componenturl === null)? '' : this.componenturl;
      this.view = '';
      this.getConfig();
    })
  }

  initializeCreatePipelineForm(){
    this.data = {
      createErrorMessage : "",
      newPipeline:{
        noteBookId : {    
          name : "",    
          versionId : {         
            label : ""    
          }  
        },  
        description : "",
        pipelineType : "ZEPPELIN"
      }
    };

    this.$data.set('createErrorMessage', '');
    this.$data.set('newPipeline.noteBookId.name', '');
    this.$data.set('newPipeline.noteBookId.versionId.label', '');
    this.$data.set('newPipeline.description', '');
    this.$data.set('newPipeline.pipelineType', 'ZEPPELIN');
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
        this.wikiUrl = envVar.wikiUrl;

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
      this.errorMessage = 'Pipeline fetch request failed with error: '+ error;
      this.view = 'error';
      this.alertOpen = true;
    });
  }

  convertPipelineObject(pipelinesInfo){
    let tempPipeline;
    pipelinesInfo.forEach(item => {
      tempPipeline = {};
      tempPipeline.pipelineId = item.noteBookId.uuid;
      tempPipeline.name = item.noteBookId.name;
      tempPipeline.version = item.noteBookId.versionId.label;
      tempPipeline.createdTimestamp = item.noteBookId.versionId.timeStamp;
      tempPipeline.createdBy = item.owner.authenticatedUserId;
      tempPipeline.description = item.description;
      tempPipeline.pipelineType = item.pipelineType;
      tempPipeline.status = item.artifactStatus.status;
      this.pipelineLists.push(tempPipeline);
    });
    this.displayPipelines();
  }

  displayPipelines() {
    if(this.resetActiveFilter) {
      this.resetActiveFilter = false;
      this.activeFilter = { pipelineType: "ZEPPELIN" };
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
    this.zeppelinPipelineCount = this.getFilteredCount({ pipelineType: "ZEPPELIN" });
    this.jupyterPipelineCount = this.getFilteredCount({ pipelineType: "JUPYTER" });

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
          this.initializeCreatePipelineForm();
          this.getPipelineList();
          this.isOpenModal = false;
        } else {
          this.$data.set('createErrorMessage', n.message);
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.$data.set('createErrorMessage', 'Pipeline create request failed with error: '+ error);
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
      this.errorMessage = 'Pipeline delete request failed with error: '+ error;
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
      this.errorMessage = 'Pipeline archive request failed with error: '+ error;
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
      this.errorMessage = 'Pipeline unarchive request failed with error: '+ error;
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
    this.isOpenModal = false;
  }

  modalClosed() {
    this.requestUpdate();
    this.createPipeline();
  }

  onDataCommit(data) {
    console.log(data);
  }

  openModal() {
    this.isOpenModal = true;
  }

  redirectWikiPage() {}

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

  redirectWikiPage(){
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
        close-string="Archive Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archivePipeline}" type="warning">
        <form><P>Are you sure want to archive ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Unarchive ${this.selectedPipelineName}" 
        close-string="Unarchive Pipeline" dismiss-string="Cancel"
        is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restorePipeline}" type="warning">
        <form><P>Are you sure want to unarchive ${this.selectedPipelineName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Delete ${this.selectedPipelineName}" close-string="Delete Pipeline" 
        dismiss-string="Cancel" is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
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
                <input type="text" class="form-control" placeholder="Enter Pipeline Name" 
                  value="${this.data.newPipeline.noteBookId.name}"
                  @blur="${ e => {
                    this.$data.set('newPipeline.noteBookId.name', e.target.value);
                    this.$validations.validate('newPipeline.noteBookId.name');
                  }
                }"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.noteBookId.name').map(error => {
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
                <input type="text" class="form-control" placeholder="Enter Pipeline Version" 
                  value="${this.data.newPipeline.noteBookId.versionId.label}"
                  @blur="${ e => {
                      this.$data.set('newPipeline.noteBookId.versionId.label', e.target.value);
                      this.$validations.validate('newPipeline.noteBookId.versionId.label');
                    }
                  }"
                />
                ${
                  this.$validations.getValidationErrors('newPipeline.noteBookId.versionId.label').map(error => {
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
                <label>Pipeline Type</label>
                <select class="form-control" id="mySelect"
                  @change="${e => this.$data.set('newPipeline.pipelineType', e.target.value)}">
                  <option value="ZEPPELIN">Zeppelin Pipeline</option>
                  <option value="JUPYTER">Jupyter Pipeline</option>
                </select>
              </div>
            </div>
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
                        ${this.successMessage}
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
                        ${this.errorMessage}
                      </div>
                    `
                    :``
                  }
                </div>
              </div>
              <div class="row" style="margin:15px 0;">
                  <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                    <div class="btn-group mr-2">
                      <button type="button" class="btn btn-primary" style="border-top-right-radius:3px; border-bottom-right-radius: 3px"
                        @click="${this.openModal}">
                        Create Pipeline
                      </button>&nbsp;&nbsp;
                      <div class="input-group-append">
                          <a href=${this.wikiUrl} target="_blank"
                            class="btnIconTop btn btn-sm btn-secondary mr-1"
                            data-toggle="tooltip"
                            data-placement="top"
                            title="Click here for wiki help">
                            <mwc-icon>help</mwc-icon> </a>
                        </div>
                    </div>
                  </div>
              </div>
              <br/>

              <div class="row" style="margin:5px 0; margin-top:20px;">
                <div class="btn-toolbar mb-2 mb-md-0">
                  <ul class="nav nav-pills mb-3">
                    <li class="nav-item mr-2">
                      <a href="javascript:void" @click=${e => this.filterPipelines({ pipelineType: "ZEPPELIN" })}
                        class="nav-link ${get(this.activeFilter, "pipelineType", "") === "ZEPPELIN"? "active" : ""}">
                        Zeppelin Pipelines&nbsp;&nbsp;
                        <span class="badge ${get(this.activeFilter, "pipelineType", "") === "ZEPPELIN"? "badge-light" : "badge-secondary"}"">
                          ${this.zeppelinPipelineCount}</span>
                      </a>
                    </li>
                    <li class="nav-item mr-2">
                      <a href="javascript:void"  @click=${e => this.filterPipelines({ pipelineType: "JUPYTER" })}
                        class="nav-link ${get(this.activeFilter,"pipelineType", "") === "JUPYTER"? "active": ""}">
                        Jupyter Pipelines&nbsp;&nbsp;
                        
                        <span class="badge ${get(this.activeFilter, "pipelineType", "") === "JUPYTER"? "badge-light" : "badge-secondary"}"">
                          ${this.jupyterPipelineCount}</span>
                      </a>
                    </li>
                    <li class="nav-item mr-2">
                      <a href="javascript:void" @click=${e => this.filterPipelines()}
                        class="nav-link ${get(this.activeFilter, "pipelineType","") === ""? "active": ""}">
                        All Pipelines&nbsp;&nbsp;
                        <span class="badge ${get(this.activeFilter, "pipelineType", "") === ""? "badge-light" : "badge-secondary"}"">
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
                      <input type="text" style="height: 30px" class="form-control w-100" placeholder="Search Pipeline"
                        @input=${e => this.searchPipelines(e.target.value)}/>
                      <div class="input-group-append">
                        <a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Pipeline">
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
                      <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                        <div class="card-body">
                          <div>
                            <a href="javascript:void" @click=${e => this.userAction("view-pipeline", item.pipelineId, item.name)}>
                              <h4 class="name">${item.name}</h4>
                              <span><strong>Pipeline Type</strong>: &nbsp; ${item.pipelineType}</span>
                              <br />
                              <span><strong>Pipeline Version</strong>: &nbsp;
                                ${item.version}</span>
                              <br />
                              <strong>Pipeline Status</strong>: &nbsp;
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
                                <a href="javascript:void"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Pipeline">
                                  <mwc-icon class="mwc-icon-gray">launch</mwc-icon>
                                </a>
                                <a href="javascript:void" @click="${e => this.openArchiveDialog(item.pipelineId, item.name)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Pipeline" >
                                  <mwc-icon class="mwc-icon-gray">archive</mwc-icon>
                                </a>
                              `
                              : html`
                                <a href="javascript:void" @click="${e => this.openRestoreDialog(item.pipelineId, item.name)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Pipeline">
                                  <mwc-icon class="mwc-icon-gray">restore</mwc-icon>
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
          <div class="row">
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
          <div class="row" style="margin:5px 0">
            <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
              <div class="btn-group mr-2">
                <div class="input-group-append">
                  <a href=${this.wikiUrl} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1" 
                    data-toggle="tooltip" data-placement="top" title="Click here for wiki help">
                    <mwc-icon>help</mwc-icon>
                  </a>
                </div>
              </div>
            </div>
          </div>
          <br/>
          <div class="row">
            <div class="col-md-12 py-3">
              <div class="card mb-124 shadow mb-5 bg-white rounded">
                <div class="card-header">
                  <div class="row" style="margin:5px 0; margin-top: 0px;">
                    <mwc-icon class="textColor">device_hub</mwc-icon>&nbsp;&nbsp;&nbsp;
                    <h4 class="textColor card-title">Pipelines</h4>
                    <div style="position: absolute; right:0">
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
                  <div class="row" style="margin:10px 0;margin-bottom:20px;">
                    <h7>No Pipelines, get started with ML Workbench by creating your first Pipeline.</h7>
                  </div>
                  <div class="row" style="margin:10px 0">
                    <button type="button" class="btn btn-primary" @click="${this.openModal}">
                      Create Pipeline
                    </button>
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
          <p class="success-status"> Loading ..</p>
        `
        : html`
      `}  
    `;
  }
}

customElements.define("pipeline-catalog-element", PipelineCatalogLitElement);