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
import moment from 'moment';

import { ValidationMixin, DataMixin, BaseElementMixin } from "./@workbenchcommon/mixins";
import { style } from "./project-catalog-styles.js";

export class ProjectCatalogLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
  get dependencies() {
    return [OmniModal, OmniDialog];
  }

  static get properties() {
    return {
      isOpenModal: { type: Boolean },
      isOpenArchiveDialog: { type: Boolean },
      isOpenDeleteDialog: { type: Boolean },
      isOpenRestoreDialog: { type: Boolean },
      projects: [],
      activeFilter: {},
      activeSort: "",
      currentPage: 0,
      totalPages: 0,
      mSurl: { type: String },
      view: { type: String },
      successMessage: { type: String },
      errorMessage: { type: String },
      createerrorMessage: { type: String },
      activeProjectCount: { type: Number },
      archiveProjectCount: { type: Number },
      allProjectCount: { type: Number },
      componenturl: { type: String, notify: true },
      alertOpen: { type: Boolean },
      cardShow: { type: Boolean },
      projectWikiURL: {type: String},
      userName: {type: String, notify: true},
      authToken: {type: String, notify: true},
      allProjects: [],
      sharedFilter: { type: Boolean },
      sharedProjectsList: [],
      sharedProjectCount: { type: Number },
    };
  }

  static get styles() {
    return [style];
  }

  constructor() {
    super();
    this.view = '';
    this.initializeCreateProjectForm();
    this.$validations.init({
      validations: {
        newProject: {
          projectId: {
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
    this.cardShow = false;
    this.sortOptions = [
      { value: "created", label: "Sort By Created Date" },
      { value: "name", label: "Sort By Name" },
      { value: "id", label: "Sort By ID" }
    ];

    this.projectLists = [];
    this.allProjects = [];
    this.sharedProjectsList = [];
    this.requestUpdate().then(() => {
      this.onLoad();
      this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
      this.getConfig();
    })
  }

  initializeCreateProjectForm() {
    this.data = {
      createErrorMessage: "",
      newProject: {
        projectId: {
          name: "",
          versionId: {
            comment: "",
            label: ""
          }
        },
        description: ""
      }
    };

    this.$data.snapshot('newProject');

    this.$data.set('createErrorMessage', '');
    this.$data.set('newProject.projectId.name', '');
    this.$data.set('newProject.projectId.versionId.label', '');
    this.$data.set('newProject.description', '');
  }

  connectedCallback() {
    super.connectedCallback();
    window.addEventListener('hashchange', this._boundListener);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    window.removeEventListener('hashchange', this._boundListener);
  }

  getConfig() {
    const url = this.componenturl + '/api/config';
    this.resetMessage();
    fetch(url, {
      method: 'GET',
      mode: 'cors',
      cache: 'default'
    }).then(res => res.json())
      .then((envVar) => {
        this.mSurl = envVar.msconfig.projectmSURL;
        this.projectWikiURL = envVar.projectWikiURL;

        let username = envVar.userName;
        let token = envVar.authToken;
        
        if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
          this.resetActiveFilter = true;
          this.getProjectsList();
        } else if(username && username !== '' && token && token !== '') {
          this.authToken = token;
          this.userName = username;
          this.resetActiveFilter = true;
          this.getProjectsList();
        } else {
          this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
          this.alertOpen = true;
          this.view = 'error';        
        }
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Unable to retrive configuration information. Error is: ' + error;
        this.alertOpen = true;
        this.view = 'error';
      });
  }

  resetMessage() {
    this.successMessage = '';
    this.errorMessage = '';
  }

  getProjectsList() {
    const url = this.componenturl + '/api/projects';
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
        if (n.status === 'Error') {
          this.errorMessage = n.message;
          this.alertOpen = true;
          this.view = 'error';
        } else {
          this.projectLists = [];
          this.projects = [];
          this.cardShow = true;
          this.allProjects = this.convertProjectObject(n.data);
          this.getSharedProjects();
          //this.convertProjectObject(n.data);
        }
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Project fetch request failed with error: ' + error;
        this.alertOpen = true;
        this.view = 'error';
      });
  }

  getSharedProjects(){
    const url = this.componenturl + '/api/project/sharedProjects';
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
        if (n.status === 'Error') {
          this.errorMessage = n.message;
          this.alertOpen = true;
          this.view = 'error';
        } else {
          this.projectLists = [];
          this.projects = [];
          this.cardShow = true;
          this.sharedProjectsList = this.convertProjectObject(n.data);
          this.projectLists = this.allProjects.concat(this.sharedProjectsList);
        }
        this.displayProjects();
        //this.convertProjectObject(this.allprojects);
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Shared Project fetch request failed with error: ' + error;
        this.alertOpen = true;
        this.view = 'error';
      });
  }

  convertProjectObject(projectsInfo) {
    let tempProject;
    let projectLists = [];
    projectsInfo.forEach(item => {
      tempProject = {};
      tempProject.projectId = item.projectId.uuid;
      tempProject.name = item.projectId.name;
      tempProject.version = item.projectId.versionId.label;
      tempProject.createdTimestamp = item.projectId.versionId.timeStamp;
      tempProject.createdBy = item.owner.authenticatedUserId;
      tempProject.description = item.description;
      tempProject.status = item.artifactStatus.status;
      if(item.collaborators !== undefined){
        if(item.collaborators.users.length > 0){
          tempProject.collaborators = item.collaborators.users;
        }else{
          tempProject.collaborators = [];
        }
      }
      projectLists.push(tempProject);
    });
    //this.displayProjects();
    return projectLists;
  }

  displayProjects() {
    if(this.resetActiveFilter) {
      this.resetActiveFilter = false;
      this.activeFilter = { status: "ACTIVE" };
    }
    this.activeSort = "created";

    this.dataSource = new DataSource({
      data: this.projectLists,
      filter: this.activeFilter,
      sort: this.activeSort,
      pageSize: 8
    });
    this.sortProjects(this.activeSort);
    this.projects = this.dataSource.data;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
    this.totalProjects = this.projectLists.length;
    this.allProjectCount = this.getFilteredCount();
    this.activeProjectCount = this.getFilteredCount({ status: "ACTIVE" });
    this.archiveProjectCount = this.getFilteredCount({ status: "ARCHIVED" });
    this.sharedProjectCount = this.sharedProjectsList.length;

    if (this.totalProjects > 0) {
      this.view = 'view';
    } else {
      this.view = 'add';
    }
  }

  createProject() {
    const url = this.componenturl + '/api/project/create';
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
        "newProjectDetails": this.data.newProject,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if (n.status === 'Success') {
          this.successMessage = n.message;
          this.alertOpen = true;
          this.$data.revert('newProject');
          this.$validations.resetValidation('newProject');
          this.initializeCreateProjectForm();
          this.getProjectsList();
          this.isOpenModal = false;
        } else {
          this.$data.set('createErrorMessage', n.message);
        }
      }).catch((error) => {
        console.error('Request failed', error);
        this.$data.set('createErrorMessage', 'Project create request failed with error: ' + error);
        this.alertOpen = true;
      });
  }

  deleteProject() {
    const url = this.componenturl + '/api/project/delete';
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
        "projectId": this.selectedProjectId,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if (n.status === 'Success') {
          this.successMessage = n.message;
          this.getProjectsList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenDeleteDialog = false;
      }).catch((error) => {
        console.error('Request failed', error);
        this.errorMessage = 'Project delete request failed with error: ' + error;
        this.alertOpen = true;
      });
  }

  archiveProject() {
    const url = this.componenturl + '/api/project/archive';
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
        "projectId": this.selectedProjectId,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if (n.status === 'Success') {
          this.successMessage = n.message;
          this.getProjectsList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenArchiveDialog = false;
      }).catch((error) => {
        console.error('Request failed', error);
        this.errorMessage = 'Project archive request failed with error: ' + error;
        this.alertOpen = true;
      });
  }

  restoreProject() {
    const url = this.componenturl + '/api/project/restore';
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
        "projectId": this.selectedProjectId,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if (n.status === 'Success') {
          this.successMessage = n.message;
          this.getProjectsList();
        } else {
          this.errorMessage = n.message;
        }
        this.alertOpen = true;
        this.isOpenRestoreDialog = false;
      }).catch((error) => {
        console.error('Request failed', error);
        this.errorMessage = 'Project unarchive request failed with error: ' + error;
        this.alertOpen = true;
      });
  }

  sharedProjects(){
    this.sharedFilter = true;
    this.activeFilter = "";
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.projects = this.sharedProjectsList;
    this.totalPages = this.projects.length % 8 !== 0 ? Math.floor(this.projects.length/8) + 1 : this.projects.length/8;
  }

  filterProjects(criteria) {
    this.sharedFilter = false;
    this.activeFilter = criteria;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.dataSource.filter(criteria);
    this.projects = this.dataSource.data;
    this.totalPages = this.dataSource.totalPages;
  }

  sortProjects(key) {
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
    this.projects = this.dataSource.data;
  }

  navigatePage(direction) {
    this.dataSource.navigatePage(direction);
    this.currentPage = this.dataSource.page + 1;
    this.projects = this.dataSource.data;
  }

  getFilteredCount(criteria) {
    return filter(this.dataSource._rawData, criteria).length;
  }

  searchProjects(searchCriteria) {
    this.dataSource.search(searchCriteria);
    this.projects = this.dataSource.data;
    this.dataSource.page = 0;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
  }

  userAction(action, projectId, projectName) {
    this.dispatchEvent(
      new CustomEvent("catalog-project-event", {
        detail: {
          data: {
            action: action,
            projectId: projectId,
            projectName: projectName
          }
        }
      })
    );
  }

  onLoad() {
    this.dispatchEvent(
      new CustomEvent("on-load-event", {
      })
    );
  }

  modalDismissed() {
    this.$data.revert('newProject');
    this.$validations.resetValidation('newProject');
    this.$data.set('createErrorMessage', '');
    this.isOpenModal = false;
  }

  modalClosed() {
    this.requestUpdate();
    this.createProject();
  }

  onDataCommit(data) {
    console.log(data);
  }

  openModal() {
    this.isOpenModal = true;
  }

  archiveDialogDismissed() {
    this.isOpenArchiveDialog = false;
  }

  restoreDialogDismissed() {
    this.isOpenRestoreDialog = false;
  }

  deleteDialogDismissed() {
    this.isOpenDeleteDialog = false;
  }

  openArchiveDialog(projectId, projectName) {
    this.selectedProjectId = projectId;
    this.selectedProjectName = projectName;
    this.isOpenArchiveDialog = true;
  }

  openRestoreDialog(projectId, projectName) {
    this.selectedProjectId = projectId;
    this.selectedProjectName = projectName;
    this.isOpenRestoreDialog = true;
  }

  openDeleteDialog(projectId, projectName) {
    this.selectedProjectId = projectId;
    this.selectedProjectName = projectName;
    this.isOpenDeleteDialog = true;
  }

  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
        .hide {
          display: none;
        }
        .show {
          display: block;
        }
      </style>
      <omni-dialog title="Archive ${this.selectedProjectName}" close-string="Archive Project" dismiss-string="Cancel"
        is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archiveProject}" type="warning">
        <form><P>Are you sure want to archive ${this.selectedProjectName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Unarchive ${this.selectedProjectName}" close-string="Unarchive Project" dismiss-string="Cancel"
        is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restoreProject}" type="warning">
        <form><P>Are you sure want to unarchive ${this.selectedProjectName}?</p></form>
      </omni-dialog>

      <omni-dialog title="Delete ${this.selectedProjectName}" close-string="Delete Project" dismiss-string="Cancel"
        is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
        @omni-dialog-closed="${this.deleteProject}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedProjectName}?</p></form>
      </omni-dialog>

      <omni-modal title="Create Project" close-string="Create Project" dismiss-string="Cancel"
        is-open="${this.isOpenModal}" @omni-modal-dimissed="${this.modalDismissed}" 
        @omni-modal-closed="${this.modalClosed}"
        canClose="${this.$validations.$valid('newProject') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Project Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Project Name" 
                  .value="${this.data.newProject.projectId.name}"
                  @keyup="${ e => {
                    this.$data.set('newProject.projectId.name', e); 
                    this.$validations.validate('newProject.projectId.name');
                  }}"
                />
                ${
                  this.$validations.getValidationErrors('newProject.projectId.name').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Project Name is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Project Name should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number</div>`
                    }
                  })
                }
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Project Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Project Version" 
                  .value="${this.data.newProject.projectId.versionId.label}"
                  @keyup="${ e => {
                      this.$data.set('newProject.projectId.versionId.label', e);
                      this.$validations.validate('newProject.projectId.versionId.label');
                    }
                  }"
                />
                ${
                  this.$validations.getValidationErrors('newProject.projectId.versionId.label').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Project Version is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Project Version should contain only 1-14 numeric characters, may include "_" and "."</div>`
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
                <label>Project Description</label>
                <textarea class="form-control" placeholder="Enter Project Description" .value="${this.data.newProject.description}"
                  @keyup="${e => this.$data.set('newProject.description', e)}"></textarea>
              </div>
            </div>
          </div>
        </form>
      </omni-modal>

      ${this.view === 'view'
        ? html`
            <div class="row" style="margin:5px 0">
              <div class="col-lg-12">
                <div class="row">
                  <div class="col-lg-12">
                    ${this.successMessage !== ''
                      ? html`
                        <div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
                          <a  class="close" @click=${e => this.alertOpen = false}>
                            <span aria-hidden="true">&nbsp;&times;</span>
                          </a>
                          <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
                        </div>
                      `
                    : ``
                    }
                    ${this.errorMessage !== ''
                      ? html`
                        <div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
                          <a class="close" @click=${e => this.alertOpen = false}>
                              <span aria-hidden="true">&nbsp;&times;</span>
                          </a>
                          <mwc-icon>error</mwc-icon> &nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
                        </div>
                      `
                      : ``
                    }
                  </div>
                </div>
                <div class="row" style="margin:15px 0; margin-bottom:45px;">
                  <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                    <div class="btn-group mr-2">
                      <button type="button" class="btn btn-primary"
                        @click="${this.openModal}">
                        Create Project</button>&nbsp;&nbsp;

                      <div class="input-group-append">
                        <a
                          href=${this.projectWikiURL} target="_blank" 
                          class="btnIconTop btn btn-sm btn-secondary mr-1"
                          data-toggle="tooltip"
                          data-placement="top"
                          title="Click here for wiki help">
                          <mwc-icon class="help-icon">help</mwc-icon>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="row" style="margin:5px 0; margin-top:20px;">
                  <div class="btn-toolbar mb-2 mb-md-0">
                    <ul class="nav nav-pills mb-3">
                      <li class="nav-item mr-2">
                        <a href="javascript:void" @click=${e => this.filterProjects({ status: "ACTIVE" })}
                          class="nav-link ${(get(this.activeFilter, "status", "") === "ACTIVE" && !this.sharedFilter)? "active" : ""}">
                          Active Projects&nbsp;&nbsp;
                          <span class="badge ${(get(this.activeFilter, "status", "") === "ACTIVE" && !this.sharedFilter)? "badge-light" : "badge-secondary"}"">
                            ${this.activeProjectCount}</span>
                        </a>
                      </li>
                      <li class="nav-item mr-2">
                        <a href="javascript:void"  @click=${e => this.filterProjects({ status: "ARCHIVED" })}
                          class="nav-link ${(get(this.activeFilter,"status", "") === "ARCHIVED" && !this.sharedFilter)? "active": ""}">
                          Archived Projects&nbsp;&nbsp;
                          <span class="badge ${(get(this.activeFilter, "status", "") === "ARCHIVED" && !this.sharedFilter)? "badge-light" : "badge-secondary"}"">
                            ${this.archiveProjectCount}</span>

                        </a>
                      </li>
                      <li class="nav-item mr-2">
                        <a href="javascript:void" @click=${e => this.filterProjects()}
                          class="nav-link ${(get(this.activeFilter, "status","") === "" && !this.sharedFilter)? "active": ""}">
                          All Projects&nbsp;&nbsp;
                          <span class="badge ${(get(this.activeFilter, "status", "") === "" && !this.sharedFilter)? "badge-light" : "badge-secondary"}"">
                            ${this.allProjectCount}</span>
                        </a>
                      </li>
                      <li class="nav-item mr-2">
                        <a href="javascript:void" @click=${e => this.sharedProjects()}
                          class="nav-link ${(get(this.activeFilter, "status","") === "" && this.sharedFilter)? "active": ""}">
                          Shared Projects&nbsp;&nbsp;
                          <span class="badge ${(get(this.activeFilter, "status","") === "" && this.sharedFilter)? "badge-light" : "badge-secondary"}"">
                            ${this.sharedProjectCount}</span>
                        </a>
                      </li>
                    </ul>
                    <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                      <div class="dropdown">
                        <select class="custom-select mr-sm-2" id="template" @change=${e => this.sortProjects(e.target.value)}>
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
                        <input type="text" class="form-control w-100" placeholder="Search Project"
                          @input=${e => this.searchProjects(e.target.value)}/>
                        <div class="input-group-append">
                          <a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Project">
                            <mwc-icon class="mwc-icon">search</mwc-icon>
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row" style="margin-top:10px;">
                  ${this.projects.map(item =>
                    html`
                      <div class="col-md-3">
                        <div class="card-shadow card card-link mb-3 mb-5 bg-white">
                          <div class="card-body">
                            <div>
                              <a href="javascript:void" @click=${e => this.userAction("view-project", item.projectId, item.name)}>
                                <h4 class="project-name">${item.name}
                                &nbsp;
                                ${item.collaborators !== undefined && item.collaborators.length > 0
                                  ? html`
                                    <a href="javascript:void"
                                      class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Shared" >
                                      <mwc-icon class="mwc-icon-gray">group</mwc-icon>
                                    </a>
                                    `
                                    :``
                                  }
                                  </h4>
                                <span><strong>ID</strong>: &nbsp; ${item.projectId}</span>
                                <br />
                                <span><strong>Version</strong>: &nbsp;
                                  ${item.version}</span>
                                <br />
                                <strong>Status</strong>: &nbsp;
                                ${item.status === "ACTIVE"
                                  ? html`
                                    <span class="active-status">${item.status}</span>
                                  `
                                  : html`
                                    <span class="inactive-status">${item.status}</span>
                                  `}
                                <br />
                                <span><strong>Creation Date</strong>: &nbsp; ${moment(item.createdTimestamp).format('YYYY-MM-DD')}</span>
                                <br />
                                <span><strong>Modified Date</strong>: &nbsp; ${moment(item.createdTimestamp).format('YYYY-MM-DD')}</span>
                                <br />
                                <br />
                              </a>
                            </div>

                            <div class="gray-light pt-2 pb-2 pl-2 pr-2">
                              <div class="d-flex justify-content-between align-middle">
                                <div style="margin-top:8px;">
                                  ${item.createdBy !== this.userName
                                  ? html`
                                  <span title="${item.createdBy}"><mwc-icon class="mwc-icon-gray">account_circle</mwc-icon>
                                  </span>
                                  <span title="${this.userName}"><mwc-icon class="mwc-icon-gray">account_circle</mwc-icon>
                                  </span>
                                    `
                                    :html`
                                    <span title="${item.createdBy}"><mwc-icon class="mwc-icon-gray">account_circle</mwc-icon>
                                  </span>
                                  `
                                }
                                  
                                </div>
                              <div>
                              
                              ${item.status === "ACTIVE"
                                ? html`
                                  <a href="javascript:void" @click="${e => this.openArchiveDialog(item.projectId, item.name)}"
                                    class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Project" >
                                    <mwc-icon class="mwc-icon-gray">archive</mwc-icon>
                                  </a>
                                `
                                : html`
                                  <a href="javascript:void" @click="${e => this.openRestoreDialog(item.projectId, item.name)}"
                                    class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Project">
                                    <mwc-icon class="mwc-icon-gray">restore_from_trash</mwc-icon>
                                  </a>
                                  <a href="javascript:void" @click="${e => this.openDeleteDialog(item.projectId, item.name)}"
                                    class="btnIcon btn btn-sm my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Project">
                                    <mwc-icon class="mwc-icon-gray">delete</mwc-icon>
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
                    <div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
                      <a class="close" @click=${e => this.alertOpen = false}>
                        <span aria-hidden="true">&nbsp;&times;</span>
                      </a><mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
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
            <div class="row" style="margin:5px 0;">
              <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0; padding-right: 5px;">
                <div class="btn-group mr-2">
                  <div class="input-group-append">
                    <a href=${this.projectWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1" 
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
                <div class="card mb-124  shadow mb-5 bg-white">
                  <div class="card-header">
                    <div class="row" style="margin:5px 0; margin-top: 0px;">
                      <mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;&nbsp;
                      <h4 class="textColor card-title">Projects</h4>
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
                  <div class="card-body ${this.cardShow ? 'show' : 'hide'}">
                    <div class="row" style="margin:10px 0;margin-bottom:20px;">
                      <h7>No Projects, get started with ML Workbench by creating your first project.</h7>
                    </div>
                    <div class="row" style="margin:10px 0">
                      <button type="button" class="btn btn-primary" @click="${this.openModal}">
                        Create Project
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
          <p class="success-status"> Loading ..</p>
        `
        : html`
      `}
    `;
  }
}

customElements.define("project-catalog-element", ProjectCatalogLitElement);
