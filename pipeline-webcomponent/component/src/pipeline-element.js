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
import {style} from './pipeline-styles.js';

export class PipelineLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement)))  {
		get dependencies() {
			return [OmniModal, OmniDialog];
		}	
		
		static get properties() {
			return {
				view: {type: String, notify: true},
				pipelineName: { type: String, notify: true },
				pipelineDesc: { type: String, notify: true },
				pipelineType: { type: String, notify: true },
				pipelineId: { type: String, notify: true },
				pipelineCreateDate: { type: String, notify: true },
				pipelineModifyDate: { type: String, notify: true },
				pipelineVersion: {type: String, notify: true},
				isEdit:{type: Boolean, notify: true },
				pipelineStatus: { type: String, notify: true },
				componenturl: { type: String, notify: true },
				userName : {type: String},
				isOpenArchiveDialog: { type: Boolean },
				isOpenDeleteDialog: { type: Boolean },
				isOpenRestoreDialog: { type: Boolean },
				successMessage: {type: String},
				errorMessage: {type: String},
				alertOpen: {type: Boolean},
				cardShow: {type: Boolean},
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
			this.pipelineName = '';
			this.pipelineDesc = '';
			this.pipelineVersion = '';
			this.isEdit = false; 
			this.pipelineStatus = 'ACTIVE';
			
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
					this.pipelinemSURL = envVar.msconfig.pipelinemSURL;
					this.wikiUrl = envVar.wikiUrl;

					let username = envVar.userName;
					let token = envVar.authToken;
					
					if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
						this.getPipelineDetails(true);
					} else if(username && username !== '' && token && token !== '') {
						this.authToken = token;
						this.userName = username;
						this.getPipelineDetails(true);
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
		
		getPipelineDetails(reset){
			const url = this.componenturl + '/api/pipeline/details';
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
					"url": this.pipelinemSURL,
					"pipelineId" : this.pipelineId,
					"userName": this.userName
				})
			}).then(res => res.json())
				.then((response) => {
					if(response.status === 'Success'){
						this.renderViewPipeline(response.data);
						this.view = 'view';
						this.cardShow = true;
					}else{
						this.errorMessage = response.message;
						this.view = 'error';
						this.alertOpen = true;
					}
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Viewing the pipeline request failed with error: '+ error;
				this.view = 'error';
				this.alertOpen = true;
			});
		}

		renderViewPipeline(pipeline){
			this.pipelineName = pipeline.noteBookId.name;
			this.pipelineDesc = pipeline.description;
			this.pipelineCreateDate = pipeline.noteBookId.versionId.timeStamp;
			this.pipelineModifyDate = pipeline.noteBookId.versionId.timeStamp;
			this.pipelineVersion = pipeline.noteBookId.versionId.label;
			this.pipelineStatus = pipeline.artifactStatus.status;
			this.pipelineType = pipeline.pipelineType;
		}
		
		createUdpateFormData(){
			let pipeline = {};
			pipeline.noteBookId = {};
			pipeline.artifactStatus = {};
			pipeline.noteBookId.versionId = {};
			pipeline.noteBookId.uuid = this.pipelineId;
			pipeline.noteBookId.name = this.pipelineName;
			pipeline.description = this.pipelineDesc;
			pipeline.pipelineType = this.pipelineType;
			pipeline.noteBookId.versionId.timeStamp = this.pipelineCreateDate ;
			pipeline.noteBookId.versionId.label = this.pipelineVersion;
			pipeline.artifactStatus.status = this.pipelineStatus;
			return pipeline;
		}

		redirectToPipelineCatalog() {
			this.dispatchEvent(new CustomEvent('pipeline-event', {
				detail: {
					data: 'catalog-pipeline'
				}
			}));
		}
		
		setName(e) {
			this.pipelineName = e.currentTarget.value;
		}
		
		setVersion(e) {
			this.pipelineVersion = e.currentTarget.value;
		}
		
		setDescription(e) {
			this.pipelineDesc = e.currentTarget.value;
		}
		
		openEditWindow(){
			this.isEdit = true;
		}

		updatePipeline(){
			const url = this.componenturl + '/api/pipeline/update';
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
					"url": this.pipelinemSURL,
					"pipelineId" : this.pipelineId,
					"pipelinePayload": this.createUdpateFormData()
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
				this.errorMessage = 'Update pipeline request failed with error: '+ error;
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
					"url": this.pipelinemSURL,
					"pipelineId" : this.pipelineId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.successMessage = n.message;
						this.getPipelineDetails();
					} else {
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
					this.isOpenArchiveDialog = false;         
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Pipeline archive request failed with error: '+ error;
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
					"url": this.pipelinemSURL,
					"pipelineId" : this.pipelineId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.redirectToPipelineCatalog();
					} else {
						this.errorMessage = n.message;
						this.alertOpen = true;
					}
					this.isOpenDeleteDialog = false;
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Pipeline delete request failed with error: '+ error;
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
					"url": this.pipelinemSURL,
					"pipelineId" : this.pipelineId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						this.successMessage = n.message;
						this.getPipelineDetails();
					} else {
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
					this.isOpenRestoreDialog = false;        
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Pipeline unarchive request failed with error: '+ error;
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
				<omni-dialog title="Archive ${this.pipelineName}" close-string="Archive Pipeline" dismiss-string="Cancel"
					is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
	        @omni-dialog-closed="${this.archivePipeline}" type="warning">
	        <form><P>Are you sure want to archive ${this.pipelineName}?</p></form>
	      </omni-dialog>
	
				<omni-dialog title="Unarchive ${this.pipelineName}" close-string="Unarchive Pipeline" dismiss-string="Cancel"
					is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
	        @omni-dialog-closed="${this.restorePipeline}" type="warning">
	        <form><P>Are you sure want to unarchive ${this.pipelineName}?</p></form>
	      </omni-dialog>
	
				<omni-dialog title="Delete ${this.pipelineName}" close-string="Delete Pipeline" dismiss-string="Cancel"
					is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
	        @omni-dialog-closed="${this.deletePipeline}" type="warning">
	        <form><P>Are you sure want to delete ${this.pipelineName}?</p></form>
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
										${this.pipelineStatus === 'ACTIVE'
										? html`
											<a href="javascript:void" class="btnIcon btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Launch Pipeline">
												<mwc-icon class="mwc-icon-gray">launch</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click="${e => this.openArchiveDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1" 
												data-toggle="tooltip" data-placement="top" title="Archive Pipeline">
												<mwc-icon class="mwc-icon-gray">archive</mwc-icon>
											</a>&nbsp;
											<a href=${this.wikiUrl} target="_blank" class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
													<mwc-icon>help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
											`
										: html`
											<a href="javascript:void" @click="${e => this.openRestoreDialog()}" class="btnIcon btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Unarchive Pipeline">
												<mwc-icon class="mwc-icon-gray">
													restore
												</mwc-icon>
											</a>&nbsp;
											<a href=${this.wikiUrl} target="_blank" class="btnIcon btn btn-sm btn-secondary mr-1"
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
										<mwc-icon class="textColor">device_hub</mwc-icon>&nbsp;&nbsp;&nbsp;
										<h4 class="textColor card-title">${this.pipelineName}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
										${this.pipelineStatus === 'ACTIVE'
											? html`
												${this.isEdit 
													? html`
														<a href="javascript:void" @click=${(e) => this.updatePipeline()} 
															class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Pipeline">
															<mwc-icon>save</mwc-icon>
														</a>&nbsp;
													`
													: html`
														<a href="javascript:void" @click=${(e) => this.openEditWindow()} 
															class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Pipeline">
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
									<table class="table table-bordered table-sm">
										<tbody>
											<tr>
												<td class="highlight">Pipeline Name</td>
												${this.isEdit 
													? html`
														<td>
															<input type="text" value=${this.pipelineName} class="form-control" @input=${(e) => this.setName(e)} 
																id="name" placeholder="Enter pipeline name">
														</td>
													`
													: html`
														<td>${this.pipelineName}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Pipeline ID</td>
												<td>${this.pipelineId}</td>
											</tr>
											<tr>
												<td class="highlight">Pipeline Version</td>
												${this.isEdit 
													? html`
														<td>
															<input type="text" value=${this.pipelineVersion} class="form-control" @input=${(e) => this.setVersion(e)} 
																id="version" placeholder="Enter pipeline version">
														</td>
													`
													: html`
														<td>${this.pipelineVersion}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Pipeline Type</td>
												<td>${this.pipelineType}</td>
											</tr>
											<tr>
												<td class="highlight">Pipeline Status</td>
												${this.pipelineStatus === 'ACTIVE'
													? html`
														<td class="active-status">${this.pipelineStatus}</td>
													`
													: html`
														<td class="inactive-status">${this.pipelineStatus}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Pipeline Creation Date</td>
												<td>${this.pipelineCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Modification Date</td>
												<td>${this.pipelineCreateDate}</td>
											</tr>
											<tr>
												<td class="highlight">Pipeline Description</td>
												${this.isEdit 
													? html`
													<td>
														<textarea class="form-control" id="description" rows="2" @input=${(e) => this.setDescription(e)} 
															value=${this.pipelineDesc}>${this.pipelineDesc}</textarea>
													</td>
													`
													: html`
														<td>${this.pipelineDesc}</td>
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

customElements.define("pipeline-element", PipelineLitElement);