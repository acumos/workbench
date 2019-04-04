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
import { style } from "./project-catalog-styles.js";

export class ProjectCatalogLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
  get dependencies() {
    return [OmniModal, OmniDialog];
  }
  static get properties() {
    return {
      isOpenModal: { type: Boolean },
      isOpenArchiveDialog: { type: Boolean },
      projects: [],
      activeFilter: {},
      activeSort: "",
      currentPage: 0,
      totalPages: 0,
      mSurl: {type: String},
      view: {type: String},
      statusMessge: {type: String},
      activeProjectCount: {type: Number},
      archiveProjectCount: {type: Number},
      allProjectCount: {type: Number},
      componenturl: {type: String, notify: true},
      deleteProjectId:  {type: String},
      deleteProjectName :{type: String},
      self_url: {type: String}
    };
  }

  static get styles() {
    return [style];
  }

  constructor() {
    super();

    this.data = {
    		newProject:{
		  	  projectId : {    
		  		  name : "",    
		  		  versionId : {      
		  			  comment : "",      
		  			  label : ""    
		  			}  
		  	  },  
		  	  description : ""
		    }
    };

    this.$validations.init({
      validations: {
        newProject: {
          name: {
            isNotEmpty: Forms.validators.isNotEmpty,
            minValue: Forms.validators.minValue(5)
          }
        }
      }
    });

    this.sortOptions = [
      { value: "created", label: "Sort By Created Date" },
      { value: "name", label: "Sort By Name" },
      { value: "id", label: "Sort By ID" }
    ];
    
    this.projectLists = [];
    this.view = '';
    this.requestUpdate().then(() => {
      console.log('update componenturl : ' + this.componenturl);
      this.componenturl = (this.componenturl === undefined || this.componenturl === null)? '' : this.componenturl;
      this.getConfig();
    })
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
	  fetch(url, {
		  method: 'GET',
      mode: 'cors',
      cache: 'default'
    }).then(res => res.json())
      .then((envVar) => {
        this.mSurl = envVar.msconfig.projectmSURL;
        this.getProjectsList();
    }).catch(function (error) {
      console.info('Request failed', error);
    });
  }

  getProjectsList(){
    const url = this.componenturl + '/api/projects';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
          "auth": "techmdev"
      },
      body: JSON.stringify({
        "url": this.mSurl
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.statusMessge = n.message;
          this.view = 'error';
        }else {
          this.projectLists = [];
          this.projects = [];
          this.convertProjectObject(n.data);
        }
    }).catch(function (error) {
      console.info('Request failed', error);
      this.statusMessge = 'Request failed with error: '+ error;
      this.view = 'error';
    });
  }
  
  convertProjectObject(projectsInfo){
    let tempProject;
    projectsInfo.forEach(item => {
      tempProject = {};
      tempProject.projectId = item.projectId.uuid;
      tempProject.name = item.projectId.name;
      tempProject.version = item.projectId.versionId.label;
      tempProject.createdTimestamp = item.projectId.versionId.timeStamp;
      tempProject.createdBy = item.owner.authenticatedUserId;
      tempProject.description = item.description;
      tempProject.status = item.artifactStatus.status;
      this.projectLists.push(tempProject);
    });
    this.displayProjects();
  }

  displayProjects() {
    this.activeFilter = { status: "ACTIVE" };
    this.activeSort = "created";

    this.dataSource = new DataSource({
      data: this.projectLists,
      filter: this.activeFilter,
      sort: this.activeSort,
      pageSize: 8
    });
  
    this.projects = this.dataSource.data;
    this.currentPage = this.dataSource.page + 1;
    this.totalPages = this.dataSource.totalPages;
    this.totalProjects = this.projectLists.length;
    this.allProjectCount = this.getFilteredCount();
    this.activeProjectCount = this.getFilteredCount({ status: "ACTIVE" });
    this.archiveProjectCount = this.getFilteredCount({ status: "ARCHIVED" });

    if(this.totalProjects > 0){
      this.view = 'view';
    }else {
      this.view = 'add';
    }
  }

  createProject(){
    const url = this.componenturl + '/api/project/create';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": "techmdev"
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "newProjectDetails": this.data.newProject 
      })
    }).then(res => res.json())
      .then((n) => {
        this.getProjectsList();
        console.info('create project Request successful here.');
          
    }).catch(function (error) {
      console.info('Request failed', error);
    });
  }
  
  deleteProject() {
    const url = this.componenturl + '/api/project/delete';
	  fetch(url, {
		  method: 'DELETE',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
          "auth": "techmdev"
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "projectId" : this.archiveProjectId        		  
      })
    }).then(res => res.json())
      .then((n) => {
        this.isOpenArchiveDialog = false;
        this.getProjectsList();
    }).catch(function (error) {
      console.info('Request failed', error);
    });
  }

  archiveProject(projectId) {
    const url = this.componenturl + '/api/project/archive';
	  fetch(url, {
		  method: 'PUT',
      mode: 'cors',
      cache: 'default',
      headers: {
          "Content-Type": "application/json",
          "auth": "techmdev"
      },
      body: JSON.stringify({
        "url": this.mSurl,
        "projectId" : projectId        		  
      })
    }).then(res => res.json())
      .then((n) => {
        this.getProjectsList();          
    }).catch(function (error) {
      console.info('Request failed', error);
    });
  }

  filterProjects(criteria) {
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
    this.totalPages = this.dataSource.totalPages;
  }

  userAction(action, projectId) {
    this.dispatchEvent(
      new CustomEvent("catalog-project-event", {
        detail: {
          data: {
            action: action,
            projectId: projectId
          }
        }
      })
    );
  }

  modalDismissed() {
    this.isOpenModal = false;
  }

  modalClosed() {
    this.$validations.validate("newProject");
    this.isOpenModal = false;
    this.createProject();
  }

  onDataCommit(data) {
    console.log(data);
  }
  openModal() {
    this.isOpenModal = true;
  }

  archiveProject(projectId) {}

  restoreProject(projectId) {}

  viewProject(projectId) {
    console.info("inside the viewProject methodf");
  }

  redirectWikiPage() {}

  archiveDialogDismissed(){
    this.isOpenArchiveDialog = false;
  }
  
  archiveDialogClosed(){
    this.isOpenArchiveDialog = false;
  }

  openArchiveDialog(projectId, projectName) { 
    this.archiveProjectId = projectId;
    this.archiveProjectName = projectName;
    this.isOpenArchiveDialog = true;
  }
  
  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
      </style>
      <omni-dialog is-open="${this.isOpenArchiveDialog}"  
        @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.deleteProject}"
        type="warning">
        <form>
          <P>Are you sure want to archive project: ${this.archiveProjectName}?</p>
        </form>
      </omni-dialog>

      <omni-modal
        title="Create Project"
        close-string="Create Project"
        dismiss-string="Reset"
        is-open="${this.isOpenModal}"
        @omni-modal-dimissed="${this.modalDismissed}"
        @omni-modal-closed="${this.modalClosed}"
      >
        <form>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Project Name <small class="text-danger">*</small></label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Enter Project Name"
                  value="${this.data.newProject.projectId.name}"
                  @blur="${ e => this.$data.set('newProject.projectId.name', e.target.value)}"
                />
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Project Version</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Enter Project Version"
                  value="${this.data.newProject.projectId.versionId.label}"
                  @blur="${ e => this.$data.set('newProject.projectId.versionId.label', e.target.value)}"
                />
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Project Description</label>
                <textarea
                  class="form-control"
                  placeholder="Enter Project Description"
                  @blur="${e => this.$data.set("newProject.description", e.target.value)}"
                >
                  ${this.data.newProject.description}</textarea
                >
              </div>
            </div>
          </div>
        </form>
      </omni-modal>

      ${this.view === 'view'
        ? html`
            <div class="row" style="margin:5px 0;">
              <div class="col-lg-12">
                <div class="row" style="margin:15px 0;">
                  <div
                    class="btn-toolbar mb-2 mb-md-0"
                    style="position: absolute; right:0;"
                  >
                    <div class="btn-group mr-2">
                      <button
                        type="button"
                        class="btn btn-primary"
                        style="border-top-right-radius:3px; border-bottom-right-radius: 3px"
                        @click="${this.openModal}"
                      >
                        Create Project</button
                      >&nbsp;&nbsp;

                      <div class="input-group-append">
                        <a
                          href="javascript:void"
                          @click=${e => this.redirectWikiPage()}
                          class="btnIconTop btn btn-sm btn-secondary mr-1"
                          data-toggle="tooltip"
                          data-placement="top"
                          title="Click here for wiki help"
                        >
                          <mwc-icon>help</mwc-icon>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
                <br />
                <div class="row" style="margin:5px 0; margin-top:20px;">
                  <div class="btn-toolbar mb-2 mb-md-0">
                    <ul class="nav nav-pills mb-3">
                      <li class="nav-item mr-2">
                        <a
                          href="#"
                          @click=${e => this.filterProjects({ status: "ACTIVE" })}
                          class="nav-link ${get(this.activeFilter, "status", "") ===
                          "ACTIVE"
                            ? "active"
                            : ""}"
                          >Active Projects&nbsp;&nbsp;<span class="badge badge-light"
                            >${this.activeProjectCount}</span
                          ></a
                        >
                      </li>
                      <li class="nav-item mr-2">
                        <a
                          href="#"
                          @click=${e => this.filterProjects({ status: "ARCHIVED" })}
                          class="nav-link btn-outline-secondary ${get(
                            this.activeFilter,
                            "status",
                            ""
                          ) === "ARCHIVED"
                            ? "active"
                            : ""}"
                          >Archived Projects&nbsp;&nbsp;<span
                            class="badge badge-secondary"
                            >${this.archiveProjectCount}</span
                          ></a
                        >
                      </li>
                      <li class="nav-item mr-2">
                        <a
                          href="#"
                          @click=${e => this.filterProjects()}
                          class="nav-link btn-outline-secondary  ${get(
                            this.activeFilter,
                            "status",
                            ""
                          ) === ""
                            ? "active"
                            : ""}"
                          >All Projects&nbsp;&nbsp;<span class="badge badge-secondary"
                            >${this.allProjectCount}</span
                          ></a
                        >
                      </li>
                    </ul>

                    <div
                      class="btn-toolbar mb-2 mb-md-0"
                      style="position: absolute; right:0;"
                    >
                      <div class="dropdown">
                        <select
                          class="custom-select mr-sm-2"
                          id="template"
                          @change=${e => this.sortProjects(e.target.value)}
                        >
                          ${this.sortOptions.map(item =>
                            item.value === this.activeSort
                              ? html`
                                  <option value="${item.value}" selected
                                    >${item.label}</option
                                  >
                                `
                              : html`
                                  <option value="${item.value}">${item.label}</option>
                                `
                          )}
                        </select>
                      </div>
                      <div class="btn-group mr-2">
                        &nbsp;
                        <input
                          type="text"
                          style="height: 30px"
                          class="form-control w-100"
                          @input=${e => this.searchProjects(e.target.value)}
                          placeholder="Search Project"
                        />

                        <div class="input-group-append">
                          <a
                            class="btnIcon btn btn-sm btn-primary  mr-1"
                            data-toggle="tooltip"
                            data-placement="top"
                            title="Search Project"
                          >
                            <mwc-icon class="mwc-icon">search</mwc-icon>
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row" style="margin-top:10px;">
                  ${this.projects.map(
                    item =>
                      html`
                        <div class="col-md-3">
                          <div
                            class="card-shadow card card-link mb-3 mb-5 bg-white rounded"
                          >
                              <div class="card-body">
                                <div>
                                    <a href="javascript:void" @click=${e => this.userAction("view-project", item.projectId)}>
                                      <h4 class="project-name">${item.name}</h4>
                                      <span><strong>Project ID</strong>: &nbsp; ${item.projectId}</span>
                                      <br />
                                      <span><strong>Project Version</strong>: &nbsp;
                                        ${item.version}</span>
                                      <br />
                                      <strong>Project Status</strong>: &nbsp;
                                      ${item.status === "ACTIVE"
                                        ? html`
                                            <span class="active-status">${item.status}</span>
                                          `
                                        : html`
                                            <span class="inactive-status">${item.status}</span>
                                          `}
                                      <br />
                                      <span><strong>Creation Date</strong>: &nbsp; ${item.createdTimestamp }</span>
                                      <br />
                                      <span><strong>Modified Date</strong>: &nbsp; ${item.createdTimestamp }</span>
                                      <br />
                                      <br />
                                    </a>
                                </div>

                                <div class="gray-light pt-2 pb-2 pl-2 pr-2">
                                  <div
                                    class="d-flex justify-content-between  align-middle"
                                  >
                                    <div style="margin-top:8px;">
                                      <span title="${item.projectId}"
                                        ><mwc-icon class="mwc-icon-gray"
                                          >account_circle</mwc-icon
                                        >
                                      </span>
                                    </div>
                                    <div>
                                      ${item.status === "ACTIVE"
                                        ? html`
                                            <a
                                              href="javascript:void"
                                              @click="${e => this.openArchiveDialog(item.projectId, item.name)}"
                                              class="btnIcon btn btn-sm my-1 mr-1"
                                              data-toggle="tooltip"
                                              data-placement="top"
                                              title="Archive Project"
                                            >
                                              <mwc-icon class="mwc-icon-gray"
                                                >archive</mwc-icon
                                              >
                                            </a>
                                          `
                                        : html`
                                            <a
                                              href="javascript:void"
                                              @click="${e => this.openArchiveDialog(item.projectId)}"
                                              
                                              class="btnIcon btn btn-sm my-1 mr-1"
                                              data-toggle="tooltip"
                                              data-placement="top"
                                              title="Restore Project"
                                            >
                                              <mwc-icon class="mwc-icon-gray"
                                                >restore</mwc-icon
                                              >
                                            </a>
                                            <a
                                              href="javascript:void"
                                              @click=${e => this.deleteProject(item.projectId)}
                                              class="btnIcon btn btn-sm my-1 mr-1"
                                              data-toggle="tooltip"
                                              data-placement="top"
                                              title="Delete Project"
                                            >
                                              <mwc-icon class="mwc-icon-gray"
                                                >delete</mwc-icon
                                              >
                                            </a>
                                          `}
                                    </div>
                                  </div>
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
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("first")}
                            >First</a
                          >
                        </li>
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("previous")}
                            >Previous</a
                          >
                        </li>
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("next")}
                            >Next</a
                          >
                        </li>
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("last")}
                            >Last</a
                          >
                        </li>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                      </ul>
                    </nav>
                  </div>
                </div>
                <br />
              </div>
            </div>
          `
    	: html`
    
  			`}

  		${this.view === 'add'
  			? html`
            <div class="row" style="margin:15px 0;">
              <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                <div class="btn-group mr-2">
                  <div class="input-group-append">
                    <a
                      href="javascript:void"
                      @click=${e => this.redirectWikiPage()}
                      class="btnIconTop btn btn-sm btn-secondary  mr-1"
                      data-toggle="tooltip"
                      data-placement="top"
                      title="Click here for wiki help"
                    >
                      <mwc-icon>help</mwc-icon>
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <br />
            <div class="row">
              <div class="col-md-12 py-3">
                <div class="card mb-124  shadow mb-5 bg-white rounded">
                  <div class="card-header">
                    <div class="row" style="margin:5px 0; margin-top: 0px;">
                      <mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;&nbsp;
                      <h4 class="textColor card-title">Projects</h4>
                      <div style="position: absolute; right:0">
                        <a class="btn btn-sm btn-secondary my-2">-</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                      </div>
                    </div>
                  </div>
                  <div class="card-body">
                    <div class="row" style="margin:10px 0;margin-bottom:20px;">
                      <h7
                        >No Projects, get started with ML Workbench by creating your first
                        project.</h7
                      >
                    </div>
                    <div class="row" style="margin:10px 0">
                      <button
                        type="button"
                        class="btn btn-primary"
                        @click="${this.openModal}"
                      >
                        Create Project
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
                   <p>${this.statusMessge} </p>
          `   
    		  : html`
          
          `}


          ${this.view === ''
          ? html`
                    Loading ..
          `   
    		  : html`
          
          `}
  		`;

  }
}

customElements.define("project-catalog-element", ProjectCatalogLitElement);
