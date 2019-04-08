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
import { style } from "./notebook-catalog-styles.js";

export class NotebookCatalogLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
  get dependencies() {
    return [OmniModal, OmniDialog];
  }

  static get properties() {
    return {
      isOpenModal: { type: Boolean },
      isOpenArchiveDialog: { type: Boolean },
      isOpenDeleteDialog: { type: Boolean },
      isOpenRestoreDialog: { type: Boolean },
      notebooks: [],
      activeFilter: {},
      activeSort: "",
      currentPage: 0,
      totalPages: 0,
      mSurl: {type: String},
      view: {type: String},
      zeppelinNotebookCount: {type: Number},
      jupyterNotebookCount: {type: Number},
      allNotebookCount: {type: Number},
      componenturl: {type: String, notify: true},
      user_name : {type: String},
      alertOpen: { type: Boolean },
      successMessage: { type: String },
      errorMessage: { type: String }
    };
  }

  static get styles() {
    return [style];
  }

  constructor() {
    super();
    this.view = '';
    this.initializeCreateNotebookForm();
    this.$validations.init({
      validations: {
        newNotebook: {
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

    this.notebookLists = [];
    this.requestUpdate().then(() => {
      console.info('update componenturl : ' + this.componenturl);
      this.componenturl = (this.componenturl === undefined || this.componenturl === null)? '' : this.componenturl;
      this.view = '';
      this.getConfig();
    })
  }

  initializeCreateNotebookForm(){
    this.data = {
      createErrorMessage : "",
      newNotebook:{
        noteBookId : {    
          name : "",    
          versionId : {         
            label : ""    
          }  
        },  
        description : "",
        notebookType : "ZEPPELIN"
      }
    };

    this.$data.set('createErrorMessage', '');
    this.$data.set('newNotebook.noteBookId.name', '');
    this.$data.set('newNotebook.noteBookId.versionId.label', '');
    this.$data.set('newNotebook.description', '');
    this.$data.set('newNotebook.notebookType', 'ZEPPELIN');
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
        this.mSurl = envVar.msconfig.notebookmSURL;
        this.user_name = envVar.user_name;
        if(this.user_name === undefined || this.user_name === null || this.user_name === ''){
          this.errorMessage = 'Unable to retrieve User ID from Session Cookie. Pls login to Acumos portal and come back here..';
          this.view = 'error';
        } else {
          this.getNotebookList();
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

  getNotebookList(){
    const url = this.componenturl + '/api/notebooks';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "user_name": this.user_name
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessage = n.message;
          this.view = 'error';
          this.alertOpen = true;
        } else {
          this.notebookLists = [];
          this.notebooks = [];
          this.convertNotebookObject(n.data);
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessage = 'Notebook fetch request failed with error: '+ error;
      this.view = 'error';
      this.alertOpen = true;
    });
  }

  convertNotebookObject(notebooksInfo){
    let tempNotebook;
    notebooksInfo.forEach(item => {
      tempNotebook = {};
      tempNotebook.notebookId = item.noteBookId.uuid;
      tempNotebook.name = item.noteBookId.name;
      tempNotebook.version = item.noteBookId.versionId.label;
      tempNotebook.createdTimestamp = item.noteBookId.versionId.timeStamp;
      tempNotebook.createdBy = item.owner.authenticatedUserId;
      tempNotebook.description = item.description;
      tempNotebook.notebookType = item.notebookType;
      tempNotebook.status = item.artifactStatus.status;
      this.notebookLists.push(tempNotebook);
    });
    this.displayNotebooks();
  }

  displayNotebooks() {
    this.activeFilter = { notebookType: "ZEPPELIN" };
    this.activeSort = "created";

    this.dataSource = new DataSource({
      data: this.notebookLists,
      filter: this.activeFilter,
      sort: this.activeSort,
      pageSize: 8
    });
    this.sortNotebooks( this.activeSort);
    this.notebooks = this.dataSource.data;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
    this.totalNotebooks = this.notebookLists.length;
    this.allNotebookCount = this.getFilteredCount();
    this.zeppelinNotebookCount = this.getFilteredCount({ notebookType: "ZEPPELIN" });
    this.jupyterNotebookCount = this.getFilteredCount({ notebookType: "JUPYTER" });

    if(this.totalNotebooks > 0){
      this.view = 'view';
    }else {
      this.view = 'add';
    }
  }

  createNotebook(){
    const url = this.componenturl + '/api/notebook/create';
    this.resetMessage();
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "newNotebookDetails": this.data.newNotebook,
        "user_name": this.user_name 
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.alertOpen = true;
          this.initializeCreateNotebookForm();
          this.getNotebookList();
          this.isOpenModal = false;
        } else {
          this.$data.set('createErrorMessage', n.message);
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.$data.set('createErrorMessage', 'Notebook create request failed with error: '+ error);
    });
  }
  
  deleteNotebook() {
    const url = this.componenturl + '/api/notebook/delete';
    this.resetMessage();
	  fetch(url, {
		  method: 'DELETE',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "notebookId" : this.selectedNotebookId,
        "user_name": this.user_name      		  
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.getNotebookList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenDeleteDialog = false;
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Notebook delete request failed with error: '+ error;
    });
  }

  archiveNotebook() {
    const url = this.componenturl + '/api/notebook/archive';
    this.resetMessage();
	  fetch(url, {
		  method: 'PUT',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "notebookId" : this.selectedNotebookId,
        "user_name": this.user_name      		  
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.getNotebookList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenArchiveDialog = false;         
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Notebook archive request failed with error: '+ error;
    });
  }

  restoreNotebook() {
    const url = this.componenturl + '/api/notebook/restore';
    this.resetMessage();
	  fetch(url, {
		  method: 'PUT',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "notebookId" : this.selectedNotebookId,
        "user_name": this.user_name      		  
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Success'){
          this.successMessage = n.message;
          this.getNotebookList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenRestoreDialog = false;        
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Notebook unarchive request failed with error: '+ error;
    });
  }

  filterNotebooks(criteria) {
    this.activeFilter = criteria;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.dataSource.filter(criteria);
    this.notebooks = this.dataSource.data;
    this.totalPages = this.dataSource.totalPages;
  }

  sortNotebooks(key) {
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
    this.notebooks = this.dataSource.data;
  }

  navigatePage(direction) {
    this.dataSource.navigatePage(direction);

    this.currentPage = this.dataSource.page + 1;
    this.notebooks = this.dataSource.data;
  }

  getFilteredCount(criteria) {
    return filter(this.dataSource._rawData, criteria).length;
  }

  searchNotebooks(searchCriteria) {
    this.dataSource.search(searchCriteria);
    this.notebooks = this.dataSource.data;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
  }

  modalDismissed() {
    this.isOpenModal = false;
  }

  modalClosed() {
    this.requestUpdate();
    this.createNotebook();
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

  openArchiveDialog(notebookId, notebookName) { 
    this.selectedNotebookId = notebookId;
    this.selectedNotebookName = notebookName;
    this.isOpenArchiveDialog = true;
  }
  
  openRestoreDialog(notebookId, notebookName) { 
    this.selectedNotebookId = notebookId;
    this.selectedNotebookName = notebookName;
    this.isOpenRestoreDialog = true;
  }

  openDeleteDialog(notebookId, notebookName) { 
    this.selectedNotebookId = notebookId;
    this.selectedNotebookName = notebookName;
    this.isOpenDeleteDialog = true;
  }
  
  userAction(action, notebookId, notebookName) {
    this.dispatchEvent(
      new CustomEvent("catalog-notebook-event", {
        detail: {
          data: {
            action: action,
            notebookId: notebookId,
            notebookName: notebookName
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
      </style>
      <omni-dialog title="Archive ${this.selectedNotebookName}" close-string="Archive Notebook" dismiss-string="Cancel"
        is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archiveNotebook}" type="warning">
        <form><P>Are you sure want to archive ${this.selectedNotebookName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Unarchive ${this.selectedNotebookName}" close-string="Unarchive Notebook" dismiss-string="Cancel"
        is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restoreNotebook}" type="warning">
        <form><P>Are you sure want to unarchive ${this.selectedNotebookName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Delete ${this.selectedNotebookName}" close-string="Delete Notebook" dismiss-string="Cancel"
        is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
        @omni-dialog-closed="${this.deleteNotebook}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedNotebookName}?</p></form>
      </omni-dialog>

      <omni-modal title="Create Notebook" close-string="Create Notebook" dismiss-string="Cancel"
        is-open="${this.isOpenModal}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosed}"
        canClose="${this.$validations.$valid && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Notebook Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Notebook Name" value="${this.data.newNotebook.noteBookId.name}"
                  @blur="${ e => {
                    this.$data.set('newNotebook.noteBookId.name', e.target.value);
                    this.$validations.validate('newNotebook.noteBookId.name');
                  }
                }"
                />
                ${
                  this.$validations.getValidationErrors('newNotebook.noteBookId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Notebook name is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Notebook Name is not valid. </div>`
                    }
                  })
                }
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Notebook Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Notebook Version" value="${this.data.newNotebook.noteBookId.versionId.label}"
                  @blur="${ e => {
                      this.$data.set('newNotebook.noteBookId.versionId.label', e.target.value);
                      this.$validations.validate('newNotebook.noteBookId.versionId.label');
                    }
                  }"
                />
                ${
                  this.$validations.getValidationErrors('newNotebook.noteBookId.versionId.label').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Notebook version is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Notebook version is not valid. </div>`
                    }
                  })
                }
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Notebook Type</label>
                <select class="form-control" id="mySelect"
                  @change="${e => this.$data.set('newNotebook.notebookType', e.target.value)}">
                  <option value="ZEPPELIN">Zeppelin Notebook</option>
                  <option value="JUPYTER">Jupyter Notebook</option>
                </select>
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Notebook Description</label>
                <textarea class="form-control" placeholder="Enter Notebook Description"
                  @blur="${e => this.$data.set('newNotebook.description', e.target.value)}">${this.data.newNotebook.description}</textarea>
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
                        Create Notebook
                      </button>&nbsp;&nbsp;
                      <div class="input-group-append">
                          <a href="javascript:void" @click=${e => this.redirectWikiPage()}
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
                      <a href="javascript:void" @click=${e => this.filterNotebooks({ notebookType: "ZEPPELIN" })}
                        class="nav-link ${get(this.activeFilter, "notebookType", "") === "ZEPPELIN"? "active" : ""}">
                        Zeppelin Notebooks&nbsp;&nbsp;
                        <span class="badge ${get(this.activeFilter, "notebookType", "") === "ZEPPELIN"? "badge-light" : "badge-secondary"}"">${this.zeppelinNotebookCount}</span>
                      </a>
                    </li>
                    <li class="nav-item mr-2">
                      <a href="javascript:void"  @click=${e => this.filterNotebooks({ notebookType: "JUPYTER" })}
                        class="nav-link ${get(this.activeFilter,"notebookType", "") === "JUPYTER"? "active": ""}">
                        Jupyter Notebooks&nbsp;&nbsp;
                        
                        <span class="badge ${get(this.activeFilter, "notebookType", "") === "JUPYTER"? "badge-light" : "badge-secondary"}"">${this.jupyterNotebookCount}</span>
                      </a>
                    </li>
                    <li class="nav-item mr-2">
                      <a href="javascript:void" @click=${e => this.filterNotebooks()}
                        class="nav-link ${get(this.activeFilter, "notebookType","") === ""? "active": ""}">
                        All Notebooks&nbsp;&nbsp;
                        <span class="badge ${get(this.activeFilter, "notebookType", "") === ""? "badge-light" : "badge-secondary"}"">${this.allNotebookCount}</span>
                      </a>
                    </li>
                  </ul>
                  <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                    <div class="dropdown">
                      <select class="custom-select mr-sm-2" id="template" @change=${e => this.sortNotebooks(e.target.value)}>
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
                      <input type="text" style="height: 30px" class="form-control w-100" placeholder="Search Notebook"
                        @input=${e => this.searchNotebooks(e.target.value)}/>
                      <div class="input-group-append">
                        <a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Notebook">
                          <mwc-icon class="mwc-icon">search</mwc-icon>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row" style="margin-top:10px;">
                ${this.notebooks.map(
                  item =>
                    html`
                    <div class="col-md-3">
                      <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                        <div class="card-body">
                          <div>
                            <a href="javascript:void" @click=${e => this.userAction("view-notebook", item.notebookId, item.name)}>
                              <h4 class="name">${item.name}</h4>
                              <span><strong>Notebook Type</strong>: &nbsp; ${item.notebookType}</span>
                              <br />
                              <span><strong>Notebook Version</strong>: &nbsp;
                                ${item.version}</span>
                              <br />
                              <strong>Notebook Status</strong>: &nbsp;
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
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Notebook">
                                  <mwc-icon class="mwc-icon-gray">launch</mwc-icon>
                                </a>
                                <a href="javascript:void" @click="${e => this.openArchiveDialog(item.notebookId, item.name)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Notebook" >
                                  <mwc-icon class="mwc-icon-gray">archive</mwc-icon>
                                </a>
                              `
                              : html`
                                <a href="javascript:void" @click="${e => this.openRestoreDialog(item.notebookId, item.name)}"
                                  class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Notebook">
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
                        <a class="page-link" href="javascript:void" @click=${e => this.navigatePage("first")}>First</a>
                      </li>
                      <li class="page-item">
                        <a class="page-link" href="javascript:void" @click=${e => this.navigatePage("previous")} >Previous</a>
                      </li>
                      <li class="page-item">
                        <a class="page-link" href="javascript:void" @click=${e => this.navigatePage("next")} >Next</a>
                      </li>
                      <li class="page-item">
                        <a class="page-link" href="javascript:void" @click=${e => this.navigatePage("last")} >Last</a>
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
                  <a href="javascript:void" @click=${e => this.redirectWikiPage()} class="btnIconTop btn btn-sm btn-secondary mr-1" 
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
                    <mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
                    <h4 class="textColor card-title">Notebooks</h4>
                    <div style="position: absolute; right:0">
                      <a class="btn btn-sm btn-secondary my-2">-</a>
                      &nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                  </div>
                </div>
                <div class="card-body">
                  <div class="row" style="margin:10px 0;margin-bottom:20px;">
                    <h7>No Notebooks, get started with ML Workbench by creating your first Notebook.</h7>
                  </div>
                  <div class="row" style="margin:10px 0">
                    <button type="button" class="btn btn-primary" @click="${this.openModal}">
                      Create Notebook
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

customElements.define("notebook-catalog-element", NotebookCatalogLitElement);