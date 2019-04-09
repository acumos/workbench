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
import './pipeline-element.js';
import './notebook-element.js';
import { OmniModal, OmniDialog } from "./@omni/components";

import { ValidationMixin, DataMixin, BaseElementMixin } from "./@omni/mixins";
import { Forms, DataSource } from "./@omni/core";
import {style} from './project-styles.js';

export class ProjectLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
  get dependencies() {
      return [OmniModal, OmniDialog];
  }

  static get properties() {
      return {
        message: { type: String, notify: true },
        view: {type: String, notify: true},
        isEdit:{type: Boolean, notify: true },
        componenturl: { type: String, notify: true },
        project : {type: Object},
        isOpenArchiveDialog: { type: Boolean },
        isOpenDeleteDialog: { type: Boolean },
        isOpenRestoreDialog: { type: Boolean },
        successMessage: {type: String},
        errorMessage: {type: String},
        alertOpen: {type: Boolean},
        cardShow: {type: Boolean},
        projectWikiURL: {type: String},
        userName: {type: String, notify: true},
        authToken: {type: String, notify: true},
        projectId: { type: String, notify: true },
      };
    }
    
    static get styles() {
      return [style];
    }

    constructor() {
      super();
      this.$validations.init({
        validations: {
          project: {
            projectName: { 
              isNotEmpty: Forms.validators.isNotEmpty,
              pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_ ]{6,30}$')
            },
            projectVersion: { 
              isNotEmpty: Forms.validators.isNotEmpty,
              pattern: Forms.validators.pattern('[a-zA-Z0-9_.]{1,14}$') 
            },
          }
        }
      })

      this.view = '';
      this.isEdit = false; 

      this.data = {
				project: {
					projectName: '',
					projectDesc: '',
					projectVersion: '',
					projectStatus: '',
          projectId: '',
          projectCreateDate: '',
          projectModifyDate: ''
				}
			}

      this.requestUpdate().then(() => {
        this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
        this.$data.set('project.projectId', this.projectId);
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
      this.resetMessage();
      fetch(url, {
        method: 'GET',
        mode: 'cors',
        cache: 'default'
      }).then(res => res.json())
        .then((envVar) => {
          this.projectmSURL = envVar.msconfig.projectmSURL;
          this.projectWikiURL = envVar.wikiConfig.projectWikiURL;

          let username = envVar.userName;
          let token = envVar.authToken;
          
          if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
            this.getProjectDetails(true);
          } else if(username && username !== '' && token && token !== '') {
            this.authToken = token;
            this.userName = username;
            this.getProjectDetails(true);
          } else {
            this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
            this.alertOpen = true;
            this.view = 'error';        
          }
        }).catch((error) => {
            console.info('Request failed', error);
            this.errorMessage = 'Unable to retrive configuration information. Error is: '+ error;
            this.view = 'error';
            this.alertOpen = true;
        });
    }

    resetMessage(){
      this.successMessage = '';
      this.errorMessage = '';
    }
    
    getProjectDetails(reset){
      const url = this.componenturl + '/api/project/details';
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
          "url": this.projectmSURL,
          "projectId" : this.projectId,
          "userName": this.userName
        })
      }).then(res => res.json())
        .then((response) => {
          if(response.status === 'Success'){
            this.renderViewProject(response.data);
            this.view = 'view';
            this.cardShow = true;
          }else{
            this.errorMessage = response.message;
            this.view = 'error';
            this.alertOpen = true;
          }
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Viewing the project request failed with error: '+ error;
        this.view = 'error';
        this.alertOpen = true;
      });
    }

    renderViewProject(project){
      this.data = {
        project: {
          projectName: project.projectId.name,
          projectDesc: project.description,
          projectCreateDate: project.projectId.versionId.timeStamp,
          projectModifyDate: project.projectId.versionId.timeStamp,
          projectVersion: project.projectId.versionId.label,
          projectStatus: project.artifactStatus.status,
          projectId: project.projectId.uuid
        }
      }
      this.projectName = this.data.project.projectName;
      this.view = 'view';
    }
    
    createUpdateFormData(){
      let project = {};
      project.projectId = {};
      project.artifactStatus = {};
      project.projectId.versionId = {};
      
      project.projectId.uuid = this.data.project.projectId;
      project.projectId.name = this.data.project.projectName;
      project.description = this.data.project.projectDesc;
      project.projectId.versionId.timeStamp = this.data.project.projectCreateDate;
      project.projectId.versionId.label = this.data.project.projectVersion;
      project.artifactStatus.status = this.data.project.projectStatus;
      return project;
    }

    redirectToProjectCatalog() {
      this.dispatchEvent(new CustomEvent('project-event', {
        detail: {
          data: 'catalog-project'
        }
      }));
    }
    
    openEditWindow(){
      this.isEdit = true;
    }

    updateProject(){
      const url = this.componenturl + '/api/project/update';
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
          "url": this.projectmSURL,
          "projectId" : this.projectId,
          "projectPayload": this.createUpdateFormData()
        })
      }).then(res => res.json())
        .then((n) => {
          if(n.status === 'Success'){
            this.isEdit = false;
            this.successMessage = n.message;
            this.projectName = this.data.project.projectName;
          } else {
            this.errorMessage = n.message;
          }
          this.alertOpen = true;
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessge = 'Update project request failed with error: '+ error;
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
          "url": this.projectmSURL,
          "projectId" : this.projectId,
          "userName": this.userName           
        })
      }).then(res => res.json())
        .then((n) => {
          if(n.status === 'Success'){
            this.successMessage = n.message;
            this.getProjectDetails();
          } else {
            this.errorMessage = n.message;
          }
          this.alertOpen = true;
          this.isOpenArchiveDialog = false;         
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Project archive request failed with error: '+ error;
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
          "url": this.projectmSURL,
          "projectId" : this.projectId,
          "userName": this.userName           
        })
      }).then(res => res.json())
        .then((n) => {
          if(n.status === 'Success'){
            this.redirectToProjectCatalog();
          } else {
            this.errorMessage = n.message;
            this.alertOpen = true;
          }
          this.isOpenDeleteDialog = false;
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Project delete request failed with error: '+ error;
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
          "url": this.projectmSURL,
          "projectId" : this.projectId,
          "userName": this.userName           
        })
      }).then(res => res.json())
        .then((n) => {
          if(n.status === 'Success'){
            this.successMessage = n.message;
            this.getProjectDetails();
          } else {
            this.errorMessage = n.message;
          }
          this.alertOpen = true;
          this.isOpenRestoreDialog = false;        
      }).catch((error) => {
        console.info('Request failed', error);
        this.errorMessage = 'Project unarchive request failed with error: '+ error;
      });
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

    openArchiveDialog() { 
      this.isOpenArchiveDialog = true;
    }
    
    openRestoreDialog() { 
      this.isOpenRestoreDialog = true;
    }

    openDeleteDialog() { 
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
        <omni-dialog title="Archive ${this.projectName}" close-string="Archive Project" dismiss-string="Cancel"
          is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
          @omni-dialog-closed="${this.archiveProject}" type="warning">
          <form><P>Are you sure want to archive ${this.projectName}?</p></form>
        </omni-dialog>
  
        <omni-dialog title="Unarchive ${this.projectName}" close-string="Unarchive Project" dismiss-string="Cancel"
          is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
          @omni-dialog-closed="${this.restoreProject}" type="warning">
          <form><P>Are you sure want to unarchive ${this.projectName}?</p></form>
        </omni-dialog>
  
        <omni-dialog title="Delete ${this.projectName}" close-string="Delete Project" dismiss-string="Cancel"
          is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
          @omni-dialog-closed="${this.deleteProject}" type="warning">
          <form><P>Are you sure want to delete ${this.projectName}?</p></form>
        </omni-dialog>

        ${this.view === 'view'
          ? html`
          <div class="row">
            <div class="col-lg-12">
              ${this.successMessage !== ''
                ? html`
                  <div class="alertmessage alert alert-success">
                    <a  class="close" @click=${e => this.alertOpen = false}>
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
                    <a class="close" @click=${e => this.alertOpen = false}>
                      <span aria-hidden="true">&nbsp;&times;</span>
                    </a>
                    <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
                  </div>
                `
                :``
              }
            </div>
          </div>
          <div class="row">
            <div class="col-md-12 py-3">
              <div class="row" style="margin:10px 0">
                <div style="position: absolute; right:0" >
                  <div class="input-group-append">
                    ${this.data.project.projectStatus === 'ACTIVE'
                    ? html`
                      <a href="javascript:void" @click="${e => this.openArchiveDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1" 
                        data-toggle="tooltip" data-placement="top" title="Archive Project">
                        <mwc-icon class="mwc-icon-gray">archive</mwc-icon>
                      </a>&nbsp;
                      <a href=${this.projectWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
                        data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
                          <mwc-icon class="mwc-icon-gray">help</mwc-icon>
                      </a>&nbsp;&nbsp;&nbsp;
                      `
                    : html`
                      <a href="javascript:void" @click="${e => this.openRestoreDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
                        data-toggle="tooltip" data-placement="top" title="Unarchive Project">
                        <mwc-icon class="mwc-icon-gray">
                          restore
                        </mwc-icon>
                      </a>&nbsp;
                      <a href="javascript:void" @click="${e => this.openDeleteDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
                          data-toggle="tooltip" data-placement="top" title="Delete Project">
                        <mwc-icon class="mwc-icon-gray">delete</mwc-icon>
                      </a>&nbsp;
                      <a href=${this.projectWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
                        data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
                        <mwc-icon class="mwc-icon-gray">help</mwc-icon>
                      </a>&nbsp;&nbsp;&nbsp;
                    `}
                    </div>
                  </div>
                </div>
            </div>
          </div>
          <br/>
          <div class="row ">
            <div class="col-md-12 py-3">
              <div class="card mb-124  shadow mb-5 bg-white">
                <div class="card-header">
                  <div class="row" style="margin:5px 0; margin-top: 0px;">
                    <mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;&nbsp;
                    <h4 class="textColor card-title">${this.projectName}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
                    ${this.data.project.projectStatus === 'ACTIVE'
                      ? html`
                        ${this.isEdit 
                          ? html`
                            ${this.$validations.$valid && this.$validations.$dirty
                              ? html`
                                <button href="javascript:void" @click=${(e) => this.updateProject()} 
                                    class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Update Project">
                                    <mwc-icon>save</mwc-icon>
                                </button>&nbsp;
                              `:
                              html`
                                <button disabled href="javascript:void" @click=${(e) => this.updateProject()} 
                                    class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Update Project">
                                    <mwc-icon>save</mwc-icon>
                                </button>&nbsp;
                            `}
                          `
                          : html`
                            <a href="javascript:void" @click=${(e) => this.openEditWindow()} 
                              class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Project">
                              <mwc-icon>edit</mwc-icon>
                            </a>&nbsp;
                          `
                        }
                      `
                      : ``
                    }
                    <div style="position: absolute; right:0" >
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
                  <table class="table table-bordered table-sm">
                    <tbody>
                      <tr>
                        <td class="highlight">Project Name</td>
                        ${this.isEdit 
                          ? html`
                            <td>
                              <input type="text" value=${this.data.project.projectName} class="form-control" id="name" placeholder="Enter project name"
                                @blur=${e => {
																  this.$data.set('project.projectName', e);
																  this.$validations.validate('project.projectName');
                                }}
                              >
                              ${
                                this.$validations.getValidationErrors('project.projectName').map(error => {
                                  switch (error) {
                                    case 'isNotEmpty':
                                      return html`<div class="invalid-feedback d-block">Project name is required</div>`
                                    case 'pattern':
																			return html`<div class="invalid-feedback d-block">Project name should contain between 6 to 30 char inlcudes only alphanumeric and '_'. It should start from alphabetic character.</div>`
                                  }
                                })
                              }
                            </td>
                          `
                          : html`
                            <td>${this.data.project.projectName}</td>
                          `
                        }
                      </tr>
                      <tr>
                        <td class="highlight">Project ID</td>
                        <td>${this.data.project.projectId}</td>
                      </tr>
                      <tr>
                        <td class="highlight">Project Version</td>
                        ${this.isEdit 
                          ? html`
                            <td>
                              <input type="text" value=${this.data.project.projectVersion} class="form-control" id="version" 
                                placeholder="Enter project version"
                                @blur=${(e) => {
                                  this.$data.set('project.projectVersion', e);
                                  this.$validations.validate('project.projectVersion');
                                }}
                              >
                              ${
                                this.$validations.getValidationErrors('project.projectVersion').map(error => {
                                  switch (error) {
                                    case 'isNotEmpty':
                                      return html`<div class="invalid-feedback d-block">Project version is required.</div>`
                                    case 'pattern':
																			return html`<div class="invalid-feedback d-block">Project version should contain between 1 to 14 char includes only alphanumeric, '.' and '_'.</div>`
                                  }
                                })
                              }
                            </td>
                          `
                          : html`
                            <td>${this.data.project.projectVersion}</td>
                          `
                        }
                      </tr>
                      <tr>
                        <td class="highlight">Project Status</td>
                        ${this.data.project.projectStatus === 'ACTIVE'
                          ? html`
                            <td class="active-status">${this.data.project.projectStatus}</td>
                          `
                          : html`
                            <td class="inactive-status">${this.data.project.projectStatus}</td>
                          `
                        }
                      </tr>
                      <tr>
                        <td class="highlight">Project Creation Date</td>
                        <td>${this.data.project.projectCreateDate}</td>
                      </tr>
                      <tr>
                        <td class="highlight">Modification Date</td>
                        <td>${this.data.project.projectCreateDate}</td>
                      </tr>
                      <tr>
                        <td class="highlight">Project Description</td>
                        ${this.isEdit 
                          ? html`
                          <td>
                            <textarea class="form-control" id="description" rows="2" .value=${this.data.project.projectDesc}
                              @blur=${(e) => this.$data.set('project.projectDesc', e)}>
                            </textarea>
                          </td>
                          `
                          : html`
                            <td>${this.data.project.projectDesc}</td>
                          `
                        }
                      </tr>
                    </tbody>
                  </table>            
                </div>
              </div>
            </div>
          </div>
          
          ${this.data.project.projectStatus === "ACTIVE"
        	? html`
        	   <project-notebook-element componenturl=${this.componenturl} projectId=${this.projectId} userName=${this.userName} authToken=${this.authToken}></project-notebook-element>
        	   <project-pipeline-element componenturl=${this.componenturl} projectId=${this.projectId} userName=${this.userName} authToken=${this.authToken}></project-pipeline-element>
        	`
        	:``
          }
          
        `
        : html``
      }
  
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

customElements.define("project-element", ProjectLitElement);