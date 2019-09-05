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
import {style} from './pipeline-styles.js';
import { Forms, DataSource } from "./@workbenchcommon/core";
import moment from 'moment';


export class PipelineLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement)))  {
		get dependencies() {
			return [OmniModal, OmniDialog];
		}	
		
		static get properties() {
			return {
				view: {type: String, notify: true},
				isEdit:{type: Boolean, notify: true },
				componenturl: { type: String, notify: true },
				userName : {type: String},
				isOpenArchiveDialog: { type: Boolean },
				isOpenDeleteDialog: { type: Boolean },
				isOpenRestoreDialog: { type: Boolean },
				successMessage: {type: String},
				errorMessage: {type: String},
				alertOpen: {type: Boolean},
				cardShow: {type: Boolean},
				pipelineWikiURL: {type: String},
				userName: {type: String, notify: true},
				authToken: {type: String, notify: true},
				pipelineId: { type: String, notify: true },
				pipelineName: { type: String, notify: true },
				useExternalPipeline: { type: String }
			};
		}
		
		static get styles() {
			return [style];
		}

		onLoad() {
			this.dispatchEvent(
				new CustomEvent("on-load-event", {
				})
			);
		}

		constructor() {
			super();
			this.initializeValidations();
			
			this.data = {
				pipeline: {
					pipelineName: '',
					pipelineDesc: '',
					pipelineVersion: '',
					pipelineStatus: '',
          pipelineId: '',
          pipelineCreateDate: '',
					pipelineModifyDate: '',
					serviceUrl: ''
				}
			}

			this.view = '';
			this.isEdit = false; 
			
			this.requestUpdate().then(() => {
				this.onLoad();
				this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
				this.getConfig();
			})
		}
		
		initializeValidations() {
			this.$validations.init({
				validations: {
					newPipeline: {
						pipelineId: {
							name: {
								isNotEmpty: Forms.validators.isNotEmpty,
								pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_]{5,29}$')
							},
							serviceUrl: this.useExternalPipeline === 'true' ? {
								isNotEmpty: Forms.validators.isNotEmpty,
								pattern: Forms.validators.pattern('https://.*')
							} : {}
						}
					}
			 }
			});
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
					this.pipelineWikiURL = envVar.pipelineWikiURL;
					this.useExternalPipeline = envVar.useExternalPipeline;
					let username = envVar.userName;
					let token = envVar.authToken;
					this.initializeValidations();
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
				this.errorMessage = 'Viewing the Data pipeline request failed with error: '+ error;
				this.view = 'error';
				this.alertOpen = true;
			});
		}

		renderViewPipeline(pipeline){
			this.$data.set('pipeline.pipelineName', pipeline.pipelineId.name, true);
		  this.$data.set('pipeline.pipelineCreateDate', pipeline.pipelineId.versionId.timeStamp, true);
		  this.$data.set('pipeline.pipelineDesc', pipeline.description, true);
	    this.$data.set('pipeline.pipelineModifyDate', pipeline.pipelineId.versionId.timeStamp, true);
      this.$data.set('pipeline.pipelineVersion', pipeline.pipelineId.versionId.label, true);
      this.$data.set('pipeline.pipelineStatus', pipeline.artifactStatus.status, true);
			this.$data.set('pipeline.pipelineId', pipeline.pipelineId.uuid, true);
			this.$data.set('pipeline.serviceUrl', pipeline.pipelineId.serviceUrl, true);
			this.pipelineName = this.data.pipeline.pipelineName;
		}
		
		createUpdateFormData(){
			let pipeline = {};
			pipeline.pipelineId = {};
			pipeline.artifactStatus = {};
			pipeline.pipelineId.versionId = {};

			pipeline.pipelineId.uuid = this.data.pipeline.pipelineId;
			pipeline.pipelineId.name = this.data.pipeline.pipelineName;
			pipeline.description = this.data.pipeline.pipelineDesc;
			pipeline.pipelineId.versionId.timeStamp = this.data.pipeline.pipelineCreateDate ;
			pipeline.pipelineId.versionId.label = this.data.pipeline.pipelineVersion;
			pipeline.artifactStatus.status = this.data.pipeline.pipelineStatus;
			pipeline.pipelineId.serviceUrl = this.data.pipeline.serviceUrl;
			return pipeline;
		}

		redirectToPipelineCatalog() {
			this.dispatchEvent(new CustomEvent('pipeline-event', {
				detail: {
					data: 'catalog-pipeline'
				}
			}));
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
					"pipelinePayload": this.createUpdateFormData()
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success') {
						this.isEdit = false;
						this.successMessage = n.message;
						this.pipelineName = this.data.pipeline.pipelineName;
						this.$validations.resetValidation('pipeline');
            this.getPipelineDetails();
					} else {
						this.errorMessage = n.message;
					}
					this.alertOpen = true;
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Update Data pipeline request failed with error: '+ error;
				this.alertOpen = true;
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
				this.errorMessage = 'Data Pipeline archive request failed with error: '+ error;
				this.alertOpen = true;
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
				this.errorMessage = 'Data Pipeline delete request failed with error: '+ error;
				this.alertOpen = true;
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
				this.errorMessage = 'Data Pipeline unarchive request failed with error: '+ error;
				this.alertOpen = true;
			});
		}

		launchPipeline(){
			const url = this.componenturl + '/api/pipeline/launch';
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
					"url": this.pipelinemSURL,
					"pipelineId" : this.pipelineId,
					"userName": this.userName      		  
				})
			}).then(res => res.json())
				.then((n) => {
					if(n.status === 'Success'){
						let launchURL = n.data.pipelineId.serviceUrl;
						window.open(launchURL, '_blank');
					} else {
						this.errorMessage = n.message;
						this.alertOpen = true; 
					}         
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Data Pipeline launch request failed with error: '+ error;
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
					.hide {
						display: none;
					}
					.show {
						display: block;
					}
				</style>
				<omni-dialog title="Archive ${this.pipelineName}" close-string="Archive Data Pipeline" dismiss-string="Cancel"
					is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
	        @omni-dialog-closed="${this.archivePipeline}" type="warning">
	        <form><P>Are you sure want to archive ${this.pipelineName}?</p></form>
	      </omni-dialog>
	
				<omni-dialog title="Unarchive ${this.pipelineName}" close-string="Unarchive Data Pipeline" dismiss-string="Cancel"
					is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
	        @omni-dialog-closed="${this.restorePipeline}" type="warning">
	        <form><P>Are you sure want to unarchive ${this.pipelineName}?</p></form>
	      </omni-dialog>
	
				<omni-dialog title="Delete ${this.pipelineName}" close-string="Delete Data Pipeline" dismiss-string="Cancel"
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
									<div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
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
									<div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
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
							<div class="row" style="margin:10px 0;">
								<div style="position: absolute; right:0" >
									<div class="input-group-append">
										${this.data.pipeline.pipelineStatus === 'ACTIVE'
										? html`
											<a href="javascript:void" @click="${e => this.launchPipeline()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Launch Data Pipeline">
												<mwc-icon class="mwc-icon-gray">launch</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click="${e => this.openArchiveDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1" 
												data-toggle="tooltip" data-placement="top" title="Archive Data Pipeline">
												<mwc-icon class="mwc-icon-gray">archive</mwc-icon>
											</a>&nbsp;
											<a href=${this.pipelineWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
												data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
													<mwc-icon class="mwc-icon-gray">help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
											`
										: html`
											${this.data.pipeline.pipelineStatus === 'ARCHIVED'
											? html`
												<a href="javascript:void" @click="${e => this.openRestoreDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Unarchive Data Pipeline">
													<mwc-icon class="mwc-icon-gray">
														restore_from_trash
													</mwc-icon>
												</a>&nbsp;
												<a href="javascript:void" @click="${e => this.openDeleteDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Delete Data Pipeline">
													<mwc-icon class="mwc-icon-gray">delete</mwc-icon>
												</a>&nbsp;
												<a href=${this.pipelineWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
													data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
													<mwc-icon class="mwc-icon-gray">help</mwc-icon>
												</a>&nbsp;&nbsp;&nbsp;
												`
											: html`
												${this.data.pipeline.pipelineStatus === 'FAILED'
												? html`
													<a href="javascript:void" @click="${e => this.openDeleteDialog()}" class="btnIconTop btn btn-sm btn-secondary mr-1"
														data-toggle="tooltip" data-placement="top" title="Delete Data Pipeline">
														<mwc-icon class="mwc-icon-gray">delete</mwc-icon>
													</a>&nbsp;
													<a href=${this.pipelineWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
														data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
														<mwc-icon class="mwc-icon-gray">help</mwc-icon>
													</a>&nbsp;&nbsp;&nbsp;
													`
												: `
													<a href=${this.pipelineWikiURL} target="_blank" class="btnIconTop btn btn-sm btn-secondary mr-1"
														data-toggle="tooltip" data-placement="top" title="Click here for wiki help"  >
														<mwc-icon class="mwc-icon-gray">help</mwc-icon>
													</a>&nbsp;&nbsp;&nbsp;
												`}
											`}
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
									<div class="row" style="margin:5px 0; margin-top: 0px; margin-bottom: 15px;">
										<mwc-icon class="textColor">device_hub</mwc-icon>&nbsp;&nbsp;&nbsp;
										<h4 class="textColor card-title">${this.pipelineName}</h4>&nbsp;&nbsp;&nbsp;&nbsp;
										${this.data.pipeline.pipelineStatus === 'ACTIVE'
											? html`
												${this.isEdit 
													? html`
														${this.$validations.$valid('pipeline') && this.$validations.$dirty
                              ? html`
                                <button href="javascript:void" @click=${(e) => this.updatePipeline()} 
																	class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Update Data Pipeline">
                                    <mwc-icon>save</mwc-icon>
                                </button>&nbsp;
                              `:
                              html`
                                <button disabled href="javascript:void" @click=${(e) => this.updatePipeline()} 
																	class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Update Data Pipeline">
                                    <mwc-icon>save</mwc-icon>
                                </button>&nbsp;
                            `}
													`
													: html`
														<a href="javascript:void" @click=${(e) => this.openEditWindow()} 
															class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Data Pipeline">
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
								
								<div class="card-body ${this.cardShow ? 'show' : 'hide'}">
									<table class="table table-bordered table-sm">
										<tbody>
											<tr>
												<td class="highlight">Data Pipeline Name</td>
												${this.isEdit 
													? html`
														<td>
                              <input type="text" value=${this.data.pipeline.pipelineName} class="form-control" id="name" placeholder="Enter Data pipeline name"
                                @keyup=${e => {
																  this.$data.set('pipeline.pipelineName', e);
																  this.$validations.validate('pipeline.pipelineName');
                                }}
                              >
                              ${
                                this.$validations.getValidationErrors('pipeline.pipelineName').map(error => {
                                  switch (error) {
                                    case 'isNotEmpty':
																			return html`<div class="invalid-feedback d-block">Data Pipeline Name is required</div>`
																		case 'pattern':
																			return html`<div class="invalid-feedback d-block">Data Pipeline Name should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number</div>`
                                  }
                                })
                              }
                            </td>
													`
													: html`
														<td>${this.data.pipeline.pipelineName}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Data Pipeline ID</td>
												<td>${this.data.pipeline.pipelineId}</td>
											</tr>
											<tr>
												<td class="highlight">Data Pipeline Version</td>
												<td>${this.data.pipeline.pipelineVersion}</td>
											</tr>
											<tr>
												<td class="highlight">Data Pipeline Status</td>
												${this.data.pipeline.pipelineStatus === 'ACTIVE'
													? html`
														<td class="active-status">${this.data.pipeline.pipelineStatus}</td>
													`
													: html`
														<td class="inactive-status">${this.data.pipeline.pipelineStatus}</td>
													`
												}
											</tr>
											<tr>
												<td class="highlight">Data Pipeline Creation Date</td>
												<td>${moment(this.data.pipeline.pipelineCreateDate).format('YYYY-MM-DD')}</td>
											</tr>
											<tr>
												<td class="highlight">Data Modification Date</td>
												<td>${moment(this.data.pipeline.pipelineCreateDate).format('YYYY-MM-DD')}</td>
											</tr>
											<tr>
												<td class="highlight">Data Pipeline Description</td>
												${this.isEdit 
													? html`
													<td>
                            <textarea class="form-control" id="description" rows="2" .value=${this.data.pipeline.pipelineDesc}
                              @keyup=${(e) => this.$data.set('pipeline.pipelineDesc', e)}>
                            </textarea>
                          </td>
													`
													: html`
														<td>${this.data.pipeline.pipelineDesc}</td>
													`
												}
											</tr>
											${this.useExternalPipeline === "true"
											? html`
												<tr>
													<td class="highlight">Pipeline URL</td>
													${this.isEdit 
														? html`
															<td>
																<input type="url" value=${this.data.pipeline.serviceUrl} class="form-control" id="serviceUrl" 
																	placeholder="Enter pipeline URL"
																	@keyup=${(e) => {
																		this.$data.set('pipeline.serviceUrl', e);
																		this.$validations.validate('pipeline.serviceUrl');
																	}}
																>
																${
																	this.$validations.getValidationErrors('pipeline.serviceUrl').map(error => {
																		switch (error) {
																			case 'isNotEmpty':
																				return html`<div class="invalid-feedback d-block">Pipeline URL is required</div>`
																			case 'pattern':
																				return html`<div class="invalid-feedback d-block">Pipeline URL should begin with https://</div>`
																		}
																	})
																}
															</td>
														`
														: html`
															<td>${this.data.pipeline.serviceUrl}</td>
														`
													}
												</tr>
											`
											: ``
										}
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
					<div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
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

customElements.define("pipeline-element", PipelineLitElement);
