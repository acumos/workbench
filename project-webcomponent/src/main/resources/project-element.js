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

import { LitElement, html } from "./node_modules/lit-element"
import './pipeline-element.js';
import './notebook-element.js';
import {style} from './project-styles.js';

export class ProjectLitElement extends LitElement {
    static get properties() {
    	console.log('inside get properties method');
      return {
				message: { type: String, notify: true },
				view: {type: String, notify: true},
							projectName: { type: String, notify: true },
							projectTemplate: { type: Array, notify: true },
							projectDesc: { type: String, notify: true },
							selectedTemplate: { type: String, notify: true },
							projectId: { type: Number, notify: true },
							projectCreateDate: { type: String, notify: true },
							projectModifyDate: { type: String, notify: true },
							projectVersion: {type: String, notify: true}
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
			this.projectTemplates = ['Template-A', 'Template-B', 'Template-C'];
			this.selectedTemplate = '';
			this.projectVersion = '';
    }
    
		connectedCallback() {
			super.connectedCallback();
			if(this.view === 'view'){
				this.renderViewProject();
			}
			window.addEventListener('hashchange', this._boundListener);
		}

		disconnectedCallback() {
			super.disconnectedCallback();
			window.removeEventListener('hashchange', this._boundListener);
		}
    
  	submitProject(){
			console.log('inside the submit method');
			console.log(this.projectName);
			console.log(this.projectDesc);
			console.log(this.selectedTemplate);
			this.renderViewProject();
    }
    
    renderViewProject(){
    	this.projectId  = 101;
    	this.projectName = 'Test Project';
			this.projectDesc = 'This is a wider card with supporting text below as a natural lead-in to additional content.';
			this.projectTemplates = ['Template-A', 'Template-B', 'Template-C'];
			this.selectedTemplate = 'Template-A';
			this.projectCreateDate = '2019/01/10 04:49 PM';
			this.projectModifyDate = '2019/01/10 05:49 PM';
			this.projectVersion = 'v1.0.0';
      this.view = 'view';
    }
    
    resetProject(){
    	this.projectName = '';
    	this.projectDesc = '';
    	this.electedTemplate = '';
    }
    
    back() {
			this.dispatchEvent(new CustomEvent('project-event', {
				'detail': {
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
    
    selectTemplate(e) {
    	this.selectedTemplate = e.currentTarget.value;
    }
    
    
  render() {
    return html`
      <style> 
				@import url('https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
      </style>

		${this.view === 'add'
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


		${this.view === 'view'
			? html`
			<div class="row ">
				<div class="col-md-12 py-3">
					
						<div class="row" style="margin:10px 0">
							<div style="position: absolute; right:0" >
								<div class="input-group-append">
									<a  href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="disabled btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="View Project">
											<mwc-icon>remove_red_eye</mwc-icon>
									</a>&nbsp;
									<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Project">
											<mwc-icon>edit</mwc-icon>
									</a>&nbsp;
									<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Delete Project">
											<mwc-icon>delete</mwc-icon>
									</a>&nbsp;
									<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Click here for wiki help">
											<mwc-icon>help</mwc-icon>
									</a>&nbsp;&nbsp;&nbsp;
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
								<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Edit Project">
											<mwc-icon>edit</mwc-icon>
									</a>&nbsp;
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
											<td>${this.projectName}</td>
										</tr>
										<tr>
											<td class="highlight">Project ID</td>
											<td>${this.projectId}</td>
										</tr>
										<tr>
											<td class="highlight">Project Version</td>
											<td>${this.projectVersion}</td>
										</tr>
										<tr>
											<td class="highlight">Project Creation Date</td>
											<td>${this.projectCreateDate}</td>
										</tr>
										<tr>
											<td class="highlight">Modification Date</td>
											<td>${this.projectModifyDate}</td>
										</tr>
										<tr>
											<td class="highlight">Project Description</td>
											<td>${this.projectDesc}</td>
										</tr>
									</tbody>
								</table>  
								           
							</div>
						</div>
					</div>
				</div>
	
				<pipeline-element></pipeline-element>
				<notebook-element></notebook-element>
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

	 `;
    }
}

customElements.define("project-element", ProjectLitElement);