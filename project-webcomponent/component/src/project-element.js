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
import {style} from './project-styles.js';

export class ProjectLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
	get dependencies() {
	    return [OmniModal, OmniDialog];
	}

	static get properties() {
      return {
				message: { type: String, notify: true },
				view: {type: String, notify: true},
				projectName: { type: String, notify: true },
				projectDesc: { type: String, notify: true },
				projectId: { type: String, notify: true },
				projectCreateDate: { type: String, notify: true },
				projectModifyDate: { type: String, notify: true },
				projectVersion: {type: String, notify: true},
				isEdit:{type: Boolean, notify: true },
				projectStatus: { type: String, notify: true },
				componenturl: { type: String, notify: true },
				project : {type: Object},
				user_name : {type: String},
				isOpenArchiveDialog: { type: Boolean },
				isOpenDeleteDialog: { type: Boolean },
				isOpenRestoreDialog: { type: Boolean },
				successMessage: {type: String},
				errorMessage: {type: String},
				alertOpen: {type: Boolean}
    	};
		}
		
		static get styles() {
			return [style];
	  }

    constructor() {
			super();
			this.view = '';
			this.projectName = '';
			this.projectDesc = '';
			this.projectVersion = '';
			this.isEdit = false; 
			this.projectStatus = '';

			this.requestUpdate().then(() => {
				this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
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
					this.user_name = envVar.user_name;
					if(this.user_name === undefined || this.user_name === null || this.user_name === ''){
						this.errorMessge = 'Unable to retrieve User ID from Session Cookie. Pls login to Acumos portal and come back here..';
						this.alertOpen = true;
						this.view = 'error';
					} else {
						this.getProjectDetails(true);
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
						"Content-Type": "application/json"
				},
				body: JSON.stringify({
					"url": this.projectmSURL,
					"projectId" : this.projectId,
					"user_name": this.user_name
				})
			}).then(res => res.json())
				.then((response) => {
					if(response.status === 'Success'){
						this.renderViewProject(response.data);
						this.view = 'view';
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
    	this.projectName = project.projectId.name;
			this.projectDesc = project.description;
			this.projectCreateDate = project.projectId.versionId.timeStamp;
			this.projectModifyDate = project.projectId.versionId.timeStamp;
			this.projectVersion = project.projectId.versionId.label;
			this.projectStatus = project.artifactStatus.status;
			this.view = 'view';
    }
		
		createUpdateFormData(){
			let project = {};
			project.projectId = {};
			project.artifactStatus = {};
			project.projectId.versionId = {};
			project.projectId.uuid = this.projectId;
			project.projectId.name = this.projectName;
			project.description = this.projectDesc;
			project.projectId.versionId.timeStamp = this.projectCreateDate ;
			project.projectId.versionId.label = this.projectVersion;
			project.artifactStatus.status = this.projectStatus;
			return project;
		}

    redirectToProjectCatalog() {
			this.dispatchEvent(new CustomEvent('project-event', {
				detail: {
					data: 'catalog-project'
				}
			}));
    }
    
    setName(e) {
    	this.projectName = e.currentTarget.value;
    }
    
    setVersion(e) {
    	this.projectVersion = e.currentTarget.value;
    }
    
    setDescription(e) {
    	this.projectDesc = e.currentTarget.value;
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
						"Content-Type": "application/json"
				},
				body: JSON.stringify({
					"user_name": this.user_name,
					"url": this.projectmSURL,
					"projectId" : this.projectId,
					"projectPayload": this.createUpdateFormData()
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.isEdit = false;
						this.successMessage = n.message;
					}else{
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
	      },
	      body: JSON.stringify({
	        "url": this.projectmSURL,
	        "projectId" : this.projectId,
	        "user_name": this.user_name      		  
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
	      },
	      body: JSON.stringify({
	        "url": this.projectmSURL,
	        "projectId" : this.projectId,
	        "user_name": this.user_name      		  
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
	      },
	      body: JSON.stringify({
	        "url": this.projectmSURL,
	        "projectId" : this.projectId,
	        "user_name": this.user_name      		  
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
	      this.errorMessage = 'Project restore request failed with error: '+ error;
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
				</style>
	      <omni-dialog is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
	        @omni-dialog-closed="${this.archiveProject}" type="warning">
	        <form><P>Are you sure want to archive project: ${this.projectName}?</p></form>
	      </omni-dialog>
	
	      <omni-dialog is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
	        @omni-dialog-closed="${this.restoreProject}" type="warning">
	        <form><P>Are you sure want to restore project: ${this.projectName}?</p></form>
	      </omni-dialog>
	
	      <omni-dialog is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
	        @omni-dialog-closed="${this.deleteProject}" type="warning">
	        <form><P>Are you sure want to delete project: ${this.projectName}?</p></form>
	      </omni-dialog>

				${this.view === 'view'
					? html`
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
					<div class="row">
						<div class="col-md-12 py-3">
							<div class="row" style="margin:10px 0">
								<div style="position: absolute; right:0" >
									<div class="input-group-append">
										${this.projectStatus === 'ACTIVE'
										? html`
											<a href="javascript:void" @click="${e => this.openArchiveDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1" 
												data-toggle="tooltip" data-placement="top" title="Archive Project">
												<mwc-icon class="mwc-icon-gray">archive</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${e => this.redirectWikiPage()} class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
													<mwc-icon>help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
											`
										: html`
											<a href="javascript:void" @click="${e => this.openRestoreDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Restore Project">
												<mwc-icon class="mwc-icon-gray">
													restore
												</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click="${e => this.openDeleteDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Delete Project">
												<mwc-icon class="mwc-icon-gray">delete</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${e => this.redirectWikiPage()} class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
												<mwc-icon>help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
										`}
										</div>
									</div>
								</div>
						</div>
					</div>

					<div class="row ">
						<div class="col-md-12 py-3">
							<div class="card mb-124  shadow mb-5 bg-white rounded">
								<div class="card-header">
									<div class="row" style="margin:5px 0; margin-top: 0px;">
										<mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;&nbsp;
										<h4 class="textColor card-title">${this.projectName}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
										${this.projectStatus === 'ACTIVE'
											? html`
												${this.isEdit 
													? html`
														<a href="javascript:void" @click=${(e) => this.updateProject()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Project">
															<mwc-icon>save</mwc-icon>
														</a>&nbsp;
													`
													: html`
														<a href="javascript:void" @click=${(e) => this.openEditWindow()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Project">
															<mwc-icon>edit</mwc-icon>
														</a>&nbsp;
													`
												}
											`
											: ``
										}
										<div style="position: absolute; right:0" >
											<a  class="btn btn-sm btn-secondary my-2">-</a>&nbsp;&nbsp;&nbsp;&nbsp;
										</div>
									</div>
								</div>
								<div class="card-body ">
									<table class="table table-bordered table-sm">
										<tbody>
											<tr>
												<td class="highlight">Project Name</td>
												${this.isEdit 
													? html`
														<td>
															<input type="text" value=${this.projectName} class="form-control" @input=${(e) => this.setName(e)} id="name" placeholder="Enter project name">
														</td>
													`
													: html`
														<td>${this.projectName}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Project ID</td>
												<td>${this.projectId}</td>
											</tr>
											<tr>
												<td class="highlight">Project Version</td>
												${this.isEdit 
													? html`
														<td>
															<input type="text" value=${this.projectVersion} class="form-control" @input=${(e) => this.setVersion(e)} id="version" placeholder="Enter project version">
														</td>
													`
													: html`
														<td>${this.projectVersion}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Project Status</td>
												${this.projectStatus === 'ACTIVE'
													? html`
														<td class="active-status">${this.projectStatus}</td>
													`
													: html`
														<td class="inactive-status">${this.projectStatus}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Project Creation Date</td>
												<td>${this.projectCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Modification Date</td>
												<td>${this.projectCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Project Description</td>
												${this.isEdit 
													? html`
													<td>
														<textarea class="form-control" id="description" rows="2" @input=${(e) => this.setDescription(e)} value=${this.projectDesc}>${this.projectDesc}</textarea>
													</td>
													`
													: html`
														<td>${this.projectDesc}</td>
													`
												}
											</tr>
										</tbody>
									</table>  					
								</div>
							</div>
						</div>
					</div>
					<notebook-element componenturl=${this.componenturl} projectId=${this.projectId}></notebook-element>
					<pipeline-element message='this is pipeline message'></pipeline-element>
					
				`
				: html``
			}
	
			${this.view === 'error'
      	? html`
					<div class="alertmessage alert alert-danger">
						<a class="close" @click=${e => this.alertOpen=false}>
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

customElements.define("project-element", ProjectLitElement);