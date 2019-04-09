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
import { OmniModal, OmniDialog } from "./@workbenchcommon/components";

import { ValidationMixin, DataMixin, BaseElementMixin } from "./@workbenchcommon/mixins";
import {style} from './notebook-styles.js';
import { Forms, DataSource } from "./@workbenchcommon/core";

export class NotebookLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement)))  {
		get dependencies() {
			return [OmniModal, OmniDialog];
		}	
		
		static get properties() {
			return {
				view: {type: String, notify: true},
				isEdit:{type: Boolean, notify: true },
				componenturl: { type: String, notify: true },
				isOpenArchiveDialog: { type: Boolean },
				isOpenDeleteDialog: { type: Boolean },
				isOpenRestoreDialog: { type: Boolean },
				successMessage: {type: String},
				errorMessage: {type: String},
				alertOpen: {type: Boolean},
				cardShow: {type: Boolean},
				notebookWikiURL: {type: String},
				userName: {type: String, notify: true},
				authToken: {type: String, notify: true},
				noteBookId: { type: String, notify: true },
				notebookName: { type: String, notify: true },
			};
		}
		
		static get styles() {
			return [style];
		}

		constructor() {
			super();

			this.$validations.init({
        validations: {
          notebook: {
            notebookName: { 
							isNotEmpty: Forms.validators.isNotEmpty,
              pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_ ]{6,30}$')
						},
            notebookVersion: { 
							isNotEmpty: Forms.validators.isNotEmpty,
							pattern: Forms.validators.pattern('[a-zA-Z0-9_.]{1,14}$')
						},
          }
        }
			})
			
			this.data = {
				notebook: {
					notebookName: '',
					notebookDesc: '',
					notebookVersion: '',
					notebookStatus: '',
          noteBookId: '',
          notebookCreateDate: '',
          notebookModifyDate: ''
				}
			}

			this.view = '';
			this.isEdit = false; 
			
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
					this.notebookmSURL = envVar.msconfig.notebookmSURL;
					let username = envVar.userName;
        	let token = envVar.authToken;
					this.notebookWikiURL = envVar.notebookWikiURL;

					if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
						this.resetActiveFilter = true;
						this.getNotebookDetails(true);
					} else if(username && username !== '' && token && token !== '') {
						this.authToken = token;
						this.userName = username;
						this.getNotebookDetails(true);
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
		
		getNotebookDetails(reset){
			const url = this.componenturl + '/api/notebook/details';
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
					"url": this.notebookmSURL,
					"noteBookId" : this.noteBookId,
					"userName": this.userName
				})
			}).then(res => res.json())
				.then((response) => {
					if(response.status === 'Success'){
						this.renderViewNotebook(response.data);
						this.view = 'view';
						this.cardShow = true;
					}else{
						this.errorMessage = response.message;
						this.view = 'error';
						this.alertOpen = true;
					}
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Viewing the notebook request failed with error: '+ error;
				this.view = 'error';
				this.alertOpen = true;
			});
		}

		renderViewNotebook(notebook){
			this.data = {
        notebook: {
					noteBookId: notebook.noteBookId.uuid,
					notebookName : notebook.noteBookId.name,
					notebookDesc : notebook.description,
					notebookCreateDate : notebook.noteBookId.versionId.timeStamp,
					notebookModifyDate : notebook.noteBookId.versionId.timeStamp,
					notebookVersion : notebook.noteBookId.versionId.label,
					notebookStatus : notebook.artifactStatus.status,
					notebookType : notebook.notebookType,
        }
			}
			this.notebookName = this.data.notebook.notebookName;
		}
		
		createUdpateFormData(){
			let notebook = {};
			notebook.noteBookId = {};
			notebook.artifactStatus = {};
			notebook.noteBookId.versionId = {};
			notebook.noteBookId.uuid = this.data.notebook.noteBookId;
			notebook.noteBookId.name = this.data.notebook.notebookName;
			notebook.description = this.data.notebook.notebookDesc;
			notebook.notebookType = this.data.notebook.notebookType;
			notebook.noteBookId.versionId.timeStamp = this.data.notebook.notebookCreateDate ;
			notebook.noteBookId.versionId.label = this.data.notebook.notebookVersion;
			notebook.artifactStatus.status = this.data.notebook.notebookStatus;
			return notebook;
		}

		redirectToNotebookCatalog() {
			this.dispatchEvent(new CustomEvent('notebook-event', {
				detail: {
					data: 'catalog-notebook'
				}
			}));
		}
		
		openEditWindow(){
			this.isEdit = true;
		}

		updateNotebook(){
			const url = this.componenturl + '/api/notebook/update';
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
					"url": this.notebookmSURL,
					"noteBookId" : this.noteBookId,
					"notebookPayload": this.createUdpateFormData()
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.isEdit = false;
						this.successMessage = n.message;
						this.notebookName = this.data.notebook.notebookName;
					}else{
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Update notebook request failed with error: '+ error;
				this.alertOpen = true;
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
						"auth": this.authToken,
				},
				body: JSON.stringify({
					"url": this.notebookmSURL,
					"noteBookId" : this.noteBookId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.successMessage = n.message;
						this.getNotebookDetails();
					} else {
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
					this.isOpenArchiveDialog = false;         
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Notebook archive request failed with error: '+ error;
				this.alertOpen = true;
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
						"auth": this.authToken,
				},
				body: JSON.stringify({
					"url": this.notebookmSURL,
					"noteBookId" : this.noteBookId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.redirectToNotebookCatalog();
					} else {
						this.errorMessage = n.message;
						this.alertOpen = true;
					}
					this.isOpenDeleteDialog = false;
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Notebook delete request failed with error: '+ error;
				this.alertOpen = true;
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
						"auth": this.authToken,
				},
				body: JSON.stringify({
					"url": this.notebookmSURL,
					"noteBookId" : this.noteBookId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.successMessage = n.message;
						this.getNotebookDetails();
					} else {
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
					this.isOpenRestoreDialog = false;        
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Notebook unarchive request failed with error: '+ error;
				this.alertOpen = true;
			});
		}

		launchNotebook() {
			const url = this.componenturl + '/api/notebook/launch';
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
					"url": this.notebookmSURL,
					"noteBookId" : this.noteBookId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						let launchURL = n.data.noteBookId.serviceUrl;
						window.open(launchURL, '_blank');
					} else {
						this.errorMessage = n.message;
						this.alertOpen = true; 
					}      
			}).catch((error) => {
				console.error('Request failed', error);
				this.errorMessage = 'Notebook launch request failed with error: '+ error;
				this.alertOpen = true;
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
					@import url('https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
					.alertmessage {
						display: ${this.alertOpen ? "block" : "none"};
					}
					.card-show {
						display: ${this.cardShow ? "block" : "none"};
					}
				</style>
				<omni-dialog title="Archive ${this.notebookName}" close-string="Archive Notebook" dismiss-string="Cancel"
					is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
	        @omni-dialog-closed="${this.archiveNotebook}" type="warning">
	        <form><P>Are you sure want to archive ${this.notebookName}?</p></form>
	      </omni-dialog>
	
				<omni-dialog title="Unarchive ${this.notebookName}" close-string="Unarchive Notebook" dismiss-string="Cancel"
					is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
	        @omni-dialog-closed="${this.restoreNotebook}" type="warning">
	        <form><P>Are you sure want to unarchive ${this.notebookName}?</p></form>
	      </omni-dialog>
	
				<omni-dialog title="Delete ${this.notebookName}" close-string="Delete Notebook" dismiss-string="Cancel"
					is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
	        @omni-dialog-closed="${this.deleteNotebook}" type="warning">
	        <form><P>Are you sure want to delete ${this.notebookName}?</p></form>
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
					<div class="row">
						<div class="col-md-12 py-3">
							<div class="row" style="margin:10px 0">
								<div style="position: absolute; right:0" >
									<div class="input-group-append">
										${this.data.notebook.notebookStatus === 'ACTIVE'
										? html`
											<a href="javascript:void" @click="${e => this.launchNotebook()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Launch Notebook">
												<mwc-icon class="mwc-icon-gray">launch</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click="${e => this.openArchiveDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1" 
												data-toggle="tooltip" data-placement="top" title="Archive Notebook">
												<mwc-icon class="mwc-icon-gray">archive</mwc-icon>
											</a>&nbsp;
											<a href=${this.notebookWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
													<mwc-icon class="mwc-icon-gray">help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
											`
										: html`
											<a href="javascript:void" @click="${e => this.openRestoreDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Unarchive Notebook">
												<mwc-icon class="mwc-icon-gray">
													restore
												</mwc-icon>
											</a>&nbsp;
											<a href=${this.notebookWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
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
										<mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
										<h4 class="textColor card-title">${this.notebookName}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
										${this.data.notebook.notebookStatus === 'ACTIVE'
											? html`
												${this.isEdit 
													? html`
														${this.$validations.$valid && this.$validations.$dirty
                              ? html`
                                <button href="javascript:void" @click=${(e) => this.updateNotebook()} 
																	class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Update Notebook">
                                    <mwc-icon>save</mwc-icon>
                                </button>&nbsp;
                              `:
                              html`
                                <button disabled href="javascript:void" @click=${(e) => this.updateNotebook()} 
																	class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Update Notebook">
                                    <mwc-icon>save</mwc-icon>
                                </button>&nbsp;
                            `}
													`
													: html`
														<a href="javascript:void" @click=${(e) => this.openEditWindow()} 
															class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Notebook">
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
												<td class="highlight">Notebook Name</td>
												${this.isEdit 
													? html`
														<td>
                              <input type="text" value=${this.data.notebook.notebookName} class="form-control" id="name" placeholder="Enter Notebook name"
                                @blur=${e => {
																  this.$data.set('notebook.notebookName', e);
																  this.$validations.validate('notebook.notebookName');
                                }}
                              >
                              ${
                                this.$validations.getValidationErrors('notebook.notebookName').map(error => {
                                  switch (error) {
                                    case 'isNotEmpty':
																			return html`<div class="invalid-feedback d-block">Notebook name is required.</div>`
																		case 'pattern':
																			return html`<div class="invalid-feedback d-block">Notebook name should contain between 6 to 30 char inlcudes only alphanumeric and '_'. It should start from alphabetic character.</div>`
                                  }
                                })
                              }
                            </td>
													`
													: html`
														<td>${this.data.notebook.notebookName}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Notebook ID</td>
												<td>${this.data.notebook.noteBookId}</td>
											</tr>
											<tr>
												<td class="highlight">Notebook Version</td>
												${this.isEdit 
													? html`
														<td>
                              <input type="text" value=${this.data.notebook.notebookVersion} class="form-control" id="version" 
                                placeholder="Enter notebook version"
                                @blur=${(e) => {
                                  this.$data.set('notebook.notebookVersion', e);
                                  this.$validations.validate('notebook.notebookVersion');
                                }}
                              >
                              ${
                                this.$validations.getValidationErrors('notebook.notebookVersion').map(error => {
                                  switch (error) {
                                    case 'isNotEmpty':
																			return html`<div class="invalid-feedback d-block">Notebook version is required.</div>`
																		case 'pattern':
																			return html`<div class="invalid-feedback d-block">Notebook version should contain between 1 to 14 char includes only alphanumeric, '.' and '_'.</div>`
                                  }
                                })
                              }
                            </td>
													`
													: html`
														<td>${this.data.notebook.notebookVersion}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Notebook Type</td>
												<td>${this.data.notebook.notebookType}</td>
											</tr>
											<tr>
												<td class="highlight">Notebook Status</td>
												${this.data.notebook.notebookStatus === 'ACTIVE'
													? html`
														<td class="active-status">${this.data.notebook.notebookStatus}</td>
													`
													: html`
														<td class="inactive-status">${this.data.notebook.notebookStatus}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Notebook Creation Date</td>
												<td>${this.data.notebook.notebookCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Modification Date</td>
												<td>${this.data.notebook.notebookCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Notebook Description</td>
												${this.isEdit 
													? html`
													<td>
														<textarea class="form-control" id="description" rows="2" .value=${this.data.notebook.notebookDesc}
                              @blur=${(e) => this.$data.set('notebook.notebookDesc', e)}>
                            </textarea>
													</td>
													`
													: html`
														<td>${this.data.notebook.notebookDesc}</td>
													`
												}
											</tr>
										</tbody>
									</table>  					
								</div>
							</div>
						</div>
					</div>
				`
				: html``
			}
		
			${this.view === 'error'
      	? html`
					<div class="alertmessage alert alert-danger">
						<a class="close" @click=${e => this.alertOpen=false}>
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

customElements.define("notebook-element", NotebookLitElement);