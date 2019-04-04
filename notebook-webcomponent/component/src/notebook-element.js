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
import { OmniModal, OmniDialog } from "./@omni/components";

import { ValidationMixin, DataMixin, BaseElementMixin } from "./@omni/mixins";
import {style} from './notebook-styles.js';

export class NotebookLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement)))  {
		get dependencies() {
			return [OmniModal, OmniDialog];
		}	
		
		static get properties() {
			return {
				view: {type: String, notify: true},
				notebookName: { type: String, notify: true },
				notebookDesc: { type: String, notify: true },
				notebookType: { type: String, notify: true },
				notebookId: { type: String, notify: true },
				notebookCreateDate: { type: String, notify: true },
				notebookModifyDate: { type: String, notify: true },
				notebookVersion: {type: String, notify: true},
				isEdit:{type: Boolean, notify: true },
				notebookStatus: { type: String, notify: true },
				componenturl: { type: String, notify: true },
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
			this.notebookName = '';
			this.notebookDesc = '';
			this.notebookVersion = '';
			this.isEdit = false; 
			this.notebookStatus = 'ACTIVE';
			
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
					this.user_name = envVar.user_name;
					if(this.user_name === undefined || this.user_name === null || this.user_name === ''){
						this.errorMessge = 'Unable to retrieve User ID from Session Cookie. Pls login to Acumos portal and come back here..';
						this.alertOpen = true;
						this.view = 'error';
					} else {
						this.getNotebookDetails(true);
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
						"Content-Type": "application/json"
				},
				body: JSON.stringify({
					"url": this.notebookmSURL,
					"notebookId" : this.notebookId,
					"user_name": this.user_name
				})
			}).then(res => res.json())
				.then((response) => {
					if(response.status === 'Success'){
						this.renderViewNotebook(response.data);
						this.view = 'view';
					}else{
						this.errorMessage = n.message;
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
			this.notebookName = notebook.noteBookId.name;
			this.notebookDesc = notebook.description;
			this.notebookCreateDate = notebook.noteBookId.versionId.timeStamp;
			this.notebookModifyDate = notebook.noteBookId.versionId.timeStamp;
			this.notebookVersion = notebook.noteBookId.versionId.label;
			this.notebookStatus = notebook.artifactStatus.status;
			this.notebookType = notebook.notebookType;
		}
		
		createUdpateFormData(){
			let notebook = {};
			notebook.noteBookId = {};
			notebook.artifactStatus = {};
			notebook.noteBookId.versionId = {};
			notebook.noteBookId.uuid = this.notebookId;
			notebook.noteBookId.name = this.notebookName;
			notebook.description = this.notebookDesc;
			notebook.notebookType = this.notebookType;
			notebook.noteBookId.versionId.timeStamp = this.notebookCreateDate ;
			notebook.noteBookId.versionId.label = this.notebookVersion;
			notebook.artifactStatus.status = this.notebookStatus;
			return notebook;
		}

		redirectToNotebookCatalog() {
			this.dispatchEvent(new CustomEvent('notebook-event', {
				detail: {
					data: 'catalog-notebook'
				}
			}));
		}
		
		setName(e) {
			this.notebookName = e.currentTarget.value;
		}
		
		setVersion(e) {
			this.notebookVersion = e.currentTarget.value;
		}
		
		setDescription(e) {
			this.notebookDesc = e.currentTarget.value;
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
					"Content-Type": "application/json"
				},
				body: JSON.stringify({
					"user_name": this.user_name,
					"url": this.notebookmSURL,
					"notebookId" : this.notebookId,
					"notebookPayload": this.createUdpateFormData()
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
				this.errorMessge = 'Update notebook request failed with error: '+ error;
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
					"url": this.notebookmSURL,
					"notebookId" : this.notebookId,
					"user_name": this.user_name      		  
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
					"url": this.notebookmSURL,
					"notebookId" : this.notebookId,
					"user_name": this.user_name      		  
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
					"url": this.notebookmSURL,
					"notebookId" : this.notebookId,
					"user_name": this.user_name      		  
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
				this.errorMessage = 'Notebook restore request failed with error: '+ error;
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
				</style>
        <omni-dialog is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
	        @omni-dialog-closed="${this.archiveNotebook}" type="warning">
	        <form><P>Are you sure want to archive notebook: ${this.notebookName}?</p></form>
	      </omni-dialog>
	
	      <omni-dialog is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
	        @omni-dialog-closed="${this.restoreNotebook}" type="warning">
	        <form><P>Are you sure want to restore notebook: ${this.notebookName}?</p></form>
	      </omni-dialog>
	
	      <omni-dialog is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
	        @omni-dialog-closed="${this.deleteNotebook}" type="warning">
	        <form><P>Are you sure want to delete notebook: ${this.notebookName}?</p></form>
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
										${this.notebookStatus === 'ACTIVE'
										? html`
											<a href="javascript:void" class="btnIcon btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Launch Notebook">
												<mwc-icon class="mwc-icon-gray">launch</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click="${e => this.openArchiveDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1" 
												data-toggle="tooltip" data-placement="top" title="Archive Notebook">
												<mwc-icon class="mwc-icon-gray">archive</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${e => this.redirectWikiPage()} class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
													<mwc-icon>help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
											`
										: html`
											<a href="javascript:void" @click="${e => this.openRestoreDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Restore Notebook">
												<mwc-icon class="mwc-icon-gray">
													restore
												</mwc-icon>
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
										<mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
										<h4 class="textColor card-title">${this.notebookName}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
										${this.notebookStatus === 'ACTIVE'
											? html`
												${this.isEdit 
													? html`
														<a href="javascript:void" @click=${(e) => this.updateNotebook()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Notebook">
															<mwc-icon>save</mwc-icon>
														</a>&nbsp;
													`
													: html`
														<a href="javascript:void" @click=${(e) => this.openEditWindow()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Notebook">
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
												<td class="highlight">Notebook Name</td>
												${this.isEdit 
													? html`
														<td>
															<input type="text" value=${this.notebookName} class="form-control" @input=${(e) => this.setName(e)} id="name" placeholder="Enter notebook name">
														</td>
													`
													: html`
														<td>${this.notebookName}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Notebook ID</td>
												<td>${this.notebookId}</td>
											</tr>
											<tr>
												<td class="highlight">Notebook Version</td>
												${this.isEdit 
													? html`
														<td>
															<input type="text" value=${this.notebookVersion} class="form-control" @input=${(e) => this.setVersion(e)} id="version" placeholder="Enter notebook version">
														</td>
													`
													: html`
														<td>${this.notebookVersion}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Notebook Type</td>
												<td>${this.notebookType}</td>
											</tr>
											<tr>
												<td class="highlight">Notebook Status</td>
												${this.notebookStatus === 'ACTIVE'
													? html`
														<td class="active-status">${this.notebookStatus}</td>
													`
													: html`
														<td class="inactive-status">${this.notebookStatus}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Notebook Creation Date</td>
												<td>${this.notebookCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Modification Date</td>
												<td>${this.notebookCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Notebook Description</td>
												${this.isEdit 
													? html`
													<td>
														<textarea class="form-control" id="description" rows="2" @input=${(e) => this.setDescription(e)} value=${this.notebookDesc}>${this.notebookDesc}</textarea>
													</td>
													`
													: html`
														<td>${this.notebookDesc}</td>
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
						${this.errorMessge}
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