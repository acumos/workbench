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
import {style} from './project-styles.js';

export class ProjectLitElement extends LitElement {
    static get properties() {
      return {
				message: { type: String, notify: true },
				view: {type: String, notify: true},
				projectName: { type: String, notify: true },
				projectTemplate: { type: Array, notify: true },
				projectDesc: { type: String, notify: true },
				selectedTemplate: { type: String, notify: true },
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
      		};
		}
		
		static get styles() {
			return [style];
	  }

    constructor() {
			super();
			this.message = "Cutom message placeholder";
			this.projectName = '';
			this.projectDesc = '';
			this.projectVersion = '';
			this.isEdit = false; 
			this.projectStatus = 'active';

			this.requestUpdate().then(() => {
				this.view = '';
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
					this.getProjectDetails(this.projectId);
			}).catch(function (error) {
				console.info('Request failed', error);
				this.errorMessage = 'Unable to retrive configuration information. Error is: '+ error;
			    this.view = 'error';
			});
		}

		resetMessage(){
		    this.successMessage = '';
		    this.errorMessage = '';
		}
		
		getProjectDetails(projectId){
			const url = this.componenturl + '/api/project/details';
			this.resetMessage();
			fetch(url, {
				method: 'POST',
				mode: 'cors',
				cache: 'default',
				headers: {
						"Content-Type": "application/json"
				},
				body: JSON.stringify({
					"url": this.projectmSURL,
					"projectId" : projectId,
					"user_name": this.user_name
				})
			}).then(res => res.json())
				.then((response) => {
					if(response.status === 'Success'){
						this.project = response.data;
						this.view = 'view';
						// this.renderViewProject(response.data);
					}else{
						this.errorMessage = n.message;
					}
			}).catch(function (error) {
				console.info('Request failed', error);
				this.errorMessage = 'Viewing the project request failed with error: '+ error;
				this.view = 'error';
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
		
		submitProject(){
			this.renderViewProject();
		}
    
    back() {
		this.dispatchEvent(new CustomEvent('project-event', {
			detail: {
				data: 'catalog-project'
			}
		}));
    }
    
    setName(e) {
    	this.project.projectId.name = e.currentTarget.value;
    }
    
    setVersion(e) {
    	this.project.projectId.versionId.label = e.currentTarget.value;
    }
    
    setDescription(e) {
    	this.project.description = e.currentTarget.value;
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
				"projectPayload": this.project 
			})
		}).then(res => res.json())
			.then((n) => {
				if(n.status === 'Success'){
					this.isEdit = false;
					this.successMessage = n.message;
					setTimeout(() => {
						this.successMessage = '';
					}, 5000);
				}else{
					this.errorMessage = n.message;
					setTimeout(() => {
						this.errorMessage = '';
					}, 5000);
					
				}
		}).catch(function (error) {
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
	          
	          this.requestUpdate().then(() => {
					this.view = '';
					this.getProjectDetails(this.projectId);
				});
	        } else {
	          this.errorMessage = n.message;
	        }
	        this.isOpenArchiveDialog = false;         
	    }).catch(function (error) {
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
	          this.successMessage = n.message;
	          setTimeout(() => {
					this.successMessage = '';
					this.back();
				}, 5000);
	          
	        } else {
	          this.errorMessage = n.message;
	        }
	        this.isOpenDeleteDialog = false;
	    }).catch(function (error) {
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
	          this.requestUpdate().then(() => {	        	  
					this.view = '';
					this.getProjectDetails(this.projectId);
				});
	        } else {
	          this.errorMessage = n.message;
	        }
	        this.isOpenRestoreDialog = false;        
	    }).catch(function (error) {
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
      </style>
      ${this.project !== undefined
      ? html`
	      <omni-dialog is-open="${this.isOpenArchiveDialog}"  
	        @omni-dialog-dimissed="${this.archiveDialogDismissed}"
	        @omni-dialog-closed="${this.archiveProject}"
	        type="warning">
	        <form>
	          <P>Are you sure want to archive project: ${this.project.projectId.name}?</p>
	        </form>
	      </omni-dialog>
	
	      <omni-dialog is-open="${this.isOpenRestoreDialog}"  
	        @omni-dialog-dimissed="${this.restoreDialogDismissed}"
	        @omni-dialog-closed="${this.restoreProject}"
	        type="warning">
	        <form>
	          <P>Are you sure want to restore project: ${this.project.projectId.name}?</p>
	        </form>
	      </omni-dialog>
	
	      <omni-dialog is-open="${this.isOpenDeleteDialog}"  
	        @omni-dialog-dimissed="${this.deleteDialogDismissed}"
	        @omni-dialog-closed="${this.deleteProject}"
	        type="warning">
	        <form>
	          <P>Are you sure want to delete project: ${this.project.projectId.name}?</p>
	        </form>
	      </omni-dialog>
	     `
	     :``
	     }

		${this.view === 'view'
		  ? html`
			<div class="row">
			   <div class="col-md-12 py-3">
				  <div class="row" style="margin:10px 0">
				   ${this.successMessage !== ''
                    ? html`
                      <div class="alert alert-success alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
      						<span aria-hidden="true">&times;</span>
      					</button>
                        ${this.successMessage}
                      </div>
                    `
                    :``
                  }
                  ${this.errorMessage !== ''
                    ? html`
                      <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
      						<span aria-hidden="true">&times;</span>
      					</button>
                        ${this.errorMessage}
                      </div>
                    `
                    :``
                  }
					<div style="position: absolute; right:0" >
							
						<div class="input-group-append">
							${this.project.artifactStatus.status === 'ACTIVE'
							? html`
								<a
									href="javascript:void"
									@click="${e => this.openArchiveDialog()}"
									class="btnIcon btn btn-sm btn-secondary mr-1"
									data-toggle="tooltip"
									data-placement="top"
									title="Archive Project"
								>
									<mwc-icon class="mwc-icon-gray">
										archive
									</mwc-icon>
								</a>&nbsp;
								<a 
									href="javascript:void"
									@click=${e => this.redirectWikiPage()}
									class="btnIcon btn btn-sm btn-secondary mr-1"
									data-toggle="tooltip"
									data-placement="top"
									title="Click here for wiki help"  >
										<mwc-icon>help</mwc-icon>
								</a>&nbsp;&nbsp;&nbsp;
								`
							: html`
								<a
									href="javascript:void"
									@click="${e => this.openRestoreDialog()}"
									class="btnIcon btn btn-sm btn-secondary mr-1"
									data-toggle="tooltip"
									data-placement="top"
									title="Restore Project"
								>
								<mwc-icon class="mwc-icon-gray">
									restore
								</mwc-icon>
								</a>&nbsp;
								<a
									href="javascript:void"
									@click="${e => this.openDeleteDialog()}"
									class="btnIcon btn btn-sm btn-secondary mr-1"
									data-toggle="tooltip"
									data-placement="top"
									title="Delete Project"
								>
								<mwc-icon class="mwc-icon-gray">
									delete
								</mwc-icon>
								</a>&nbsp;
								<a 
								href="javascript:void"
								@click=${e => this.redirectWikiPage()}
								class="btnIcon btn btn-sm btn-secondary mr-1"
								data-toggle="tooltip"
								data-placement="top"
								title="Click here for wiki help"  >
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
							<h4 class="textColor card-title">${this.project.projectId.name}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
							${this.project.artifactStatus.status === 'ACTIVE'
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
								<a  class="btn btn-sm btn-secondary my-2">-</a> 
								&nbsp;&nbsp;&nbsp;&nbsp;
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
													<input type="text" value=${this.project.projectId.name} class="form-control" @input=${(e) => this.setName(e)} id="name" placeholder="Enter project name">
												</td>
											`
											: html`
												<td>${this.project.projectId.name}</td>
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
													<input type="text" value=${this.project.projectId.versionId.label} class="form-control" @input=${(e) => this.setVersion(e)} id="version" placeholder="Enter project version">
												</td>
											`
											: html`
												<td>${this.project.projectId.versionId.label}</td>
											`
										}
									</tr>
									<tr>
										<td class="highlight">Project Status</td>
										${this.project.artifactStatus.status === 'ACTIVE'
											? html`
												<td class="active-status">${this.project.artifactStatus.status}</td>
											`
											: html`
												<td class="inactive-status">${this.project.artifactStatus.status}</td>
											`
										}
									</tr>
									<tr>
										<td class="highlight">Project Creation Date</td>
										<td>${this.project.projectId.versionId.timeStamp}</td>
									</tr>
									<tr>
										<td class="highlight">Modification Date</td>
										<td>${this.project.projectId.versionId.timeStamp}</td>
									</tr>
									<tr>
										<td class="highlight">Project Description</td>
										${this.isEdit 
											? html`
											<td>
												<textarea class="form-control" id="description" rows="2" @input=${(e) => this.setDescription(e)} value=${this.project.description}>${this.project.description}</textarea>
											</td>
											`
											: html`
												<td>${this.project.description}</td>
											`
										}
									</tr>
								</tbody>
							</table>  
												
						</div>
					</div>
				</div>
			</div>
			<notebook-element></notebook-element>
			<pipeline-element message='this is pipeline message'></pipeline-element>
			
		`
		: html``
	}
	
	${this.view === 'update'
		? html`
		<div class="row ">
			<div class="col-md-12 py-3">
				<div class="card mb-124  shadow mb-5 bg-white rounded">
					<div class="card-body">
						<form>
							<div class="form-row">
								<div class="form-group col-md-6">
									<label for="name">Project Name</label>
									<input type="text" value=${this.projectName} class="form-control" @input=${(e) => this.setName(e)} id="name" placeholder="Enter project name">
								</div>
								<div class="form-group col-md-6">
									<label for="version">Project Version</label>
									<input type="text" value=${this.projectVersion} class="form-control" @input=${(e) => this.setVersion(e)} id="version" placeholder="Enter project version">
								</div>
							</div>
							<div class="form-row">
								<div class="form-group col-md-6">
									<label for="description">Project Description</label>
									<textarea class="form-control" id="description" rows="2" @input=${(e) => this.setDescription(e)} value=${this.projectDesc}></textarea>
								</div>
								<div class="form-group col-md-6">
									<label for="template">Project Template</label>
									<select class="custom-select mr-sm-2" id="template" @change=${(e) => this.selectTemplate(e)}>
										${this.projectTemplates.map(item => html`<option>${item}</option>`)}
									</select>
								</div>
							</div>
							<hr/>
							<button type="reset" class="btn btn-outline-primary" @click=${this.resetProject}>Reset</button>
							<button type="button" class="btn btn-primary" @click=${this.submitProject}>Create Project</button>
						</form>
					</div>
				</div>
			</div>
		</div>
		`
		: html``
	}
	
	${this.view === 'delete'
		? html`
			In the delete function
		`
		: html``
	}
	
	${this.view === 'error'
      ? html`
        <div class="alert alert-danger alert-dismissible">
          <a class="close" data-dismiss="alert" aria-label="close">&times;</a>
          ${this.errorMessage}
        </div>
      `   
		  : html`
      
      `}

  	${this.view === ''
      ? html`
         <div class="alert alert-success alert-dismissible">
           <a class="close" data-dismiss="alert" aria-label="close">&times;</a>
          		Success!!! Please wait while the page is being updated...
        </div>    
                
      `   
		  : html`
      
      `}
	`;
	}
}

customElements.define("project-element", ProjectLitElement);