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
import {style} from './project-catalog-styles.js';

export class CatalogProjectLitElement extends LitElement {
    static get properties() {
        return {
			message: { type: String, notify: true },
			projectLists: { type: Array, notify: true },
			oProjectLists: { type: Array, notify: true },
			displayProjectLists: { type: Array, notify: true },
			totalRecords: { type: Number },
			startIndex: { type: Number },
			endIndex: { type: Number },
			recordsPerPage: { type: Number },
			filterText: { type: String, notify: true },
			sortCriterias: { type: Array, notify: true },
			sortCriteria: {type: String, notify: true}
        };
	}
	
	static get styles() {
		return [style];
  	}

    constructor() {
        super();
		this.message = "Cutom message placeholder";
		this.sortCriterias = ['Sort By Name', 'Sort By ID', 'Sort By Created Date'];
		this.projectLists = [];
		
		this.oProjectLists = this.projectLists;
		this.totalRecords = this.projectLists.length;
		this.recordsPerPage = 8;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = this.recordsPerPage;
		} else {
			this.endIndex = this.totalRecords;
		}
		
		this.loadPage('start');
    }
	
	loadPage(action) {
		if (action === 'next' && this.endIndex < this.totalRecords) {
			this.startIndex = this.endIndex + 1;
			this.endIndex = this.startIndex + this.recordsPerPage - 1;
			if (this.endIndex > this.totalRecords){
				this.endIndex = this.totalRecords;
			}
		}else if (action === 'previous' && this.startIndex > this.recordsPerPage) {
			this.endIndex = this.startIndex -1;
			this.startIndex = this.endIndex - this.recordsPerPage + 1;
		}else if (action === 'first'){
			this.startIndex = 1;
			if (this.totalRecords > this.recordsPerPage) {
				this.endIndex = this.recordsPerPage;
			} else {
				this.endIndex = this.totalRecords;
			}
		}else if (action === 'last'){
			if (this.totalRecords > this.recordsPerPage) {
				this.endIndex = this.totalRecords;
				let start = this.totalRecords%this.recordsPerPage;
				this.startIndex = this.totalRecords - start;
			} else {
				this.startIndex = 1;
				this.endIndex = this.totalRecords;
			}
		}
		console.info('for user action: '+ action);
		console.info('stat index: '+ this.startIndex);
		console.info('end index: '+ this.endIndex);
		this.displayProjectLists = this.projectLists.filter((project, index) => index >= (this.startIndex - 1) && index <= (this.endIndex - 1));
	}

    userAction(action, projectId) {
    	console.log('action')
    	console.log(action)
    	this.dispatchEvent(new CustomEvent('catalog-project-event', {
			'detail': {
                data: {
					action: action,
					projectId: projectId
            	}
			}
		}));
    }
	
	filterProjects(e) {
		console.info('inside the filkter porjects method')
		this.filterText = e.currentTarget.value;
		console.info(this.filterText);
		if(this.filterText){
			this.projectLists =  this.oProjectLists.filter(project => {
				const filterTextLower = this.filterText.toLowerCase();
        		return JSON.stringify(project).toLowerCase().includes(filterTextLower);
			});
		}else {
			this.projectLists = this.oProjectLists;
		}	
		this.totalRecords = this.projectLists.length;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = this.recordsPerPage;
		} else {
			this.endIndex = this.totalRecords;
		}
		this.loadPage('start');
	}
	
	sortProjects(e){
		this.sortCriteria = e.currentTarget.value;
		if (this.sortCriteria === 'Sort By Name') {
			this.projectLists.sort((n1, n2) => {
				if (n1.name.toLowerCase() > n2.name.toLowerCase()) {
					return 1;
				}else if (n1.name.toLowerCase() < n2.name.toLowerCase()) {
					return -1;
				}else {
					return 0;
				}
			});
		} else if (this.sortCriteria === 'Sort By ID') {
			this.projectLists.sort((n1, n2) => {
				if (n1.id > n2.id) {
					return 1;
				}else if (n1.id < n2.id) {
					return -1;
				}else {
					return 0;
				}
			});
		} else {
			this.projectLists.sort((n1, n2) => {
				if (n1.createdTimestamp < n2.createdTimestamp) {
					return 1;
				}else if (n1.createdTimestamp > n2.createdTimestamp) {
					return -1;
				}else {
					return 0;
				}
			});
		}
		
		this.totalRecords = this.projectLists.length;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = this.recordsPerPage;
		} else {
			this.endIndex = this.totalRecords;
		}
		this.loadPage('start');
	}

    render() {
        return html`
		<style> 
			@import url('https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
		</style> 
		${this.oProjectLists.length > 0
			? html`
				<div class="row">
					<div class="col-lg-12">
						<div class="row" style="margin:5px 0;">
							<div class="btn-toolbar mb-2 mb-md-0">
								<mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;&nbsp;
								<h4 class="textColor card-title">Projects</h4>
								
								<div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
									<div class="dropdown">
										<select class="custom-select mr-sm-2" id="template" @change=${(e) => this.sortProjects(e)}>
											${this.sortCriterias.map(item => html`<option>${item}</option>`)}
										</select>
									</div>
									<div class="btn-group mr-2">
										&nbsp;
										<input type="text" style="height: 30px" class="form-control w-100" @input=${(e) => this.filterProjects(e)} placeholder="Search Project" aria-label="Search Project" aria-describedby="button-addon2">
										
										<div class="input-group-append">
											<a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Project">
												<mwc-icon>search</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Create Project">
													<mwc-icon>add</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Click here for wiki help">
													<mwc-icon>help</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
										</div>
									</div>
								</div>
							</div>
						</div>
						
						<div class="row" style="margin-top:10px;">
							${this.displayProjectLists.map(item => 
								html`
								<div class="col-md-3">
									<div class="card mb-3 shadow mb-5 bg-white rounded">
										<div class="card-body">
											<h4 class="project-name">${item.name}</h4>
											<span class="small"><strong>Project ID</strong>: &nbsp; ${item.id}</span><br/>
											<span class="small"><strong>Project Version</strong>: &nbsp; ${item.id}</span><br/>
											<span class="small"><strong>Creation Date</strong>: &nbsp; ${item.id}</span><br/>
											<span class="small"><strong>Modified Date</strong>: &nbsp; ${item.id}</span><br/><br/>
											
											<p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. </p>
											<div class="d-flex align-items-center pb-2"> 
												<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary my-2 mr-2" data-toggle="tooltip" data-placement="top" title="Stop Pipeline Instance">
													<mwc-icon>remove_red_eye</mwc-icon>
												</a>
												<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary my-1 mr-1 " data-toggle="tooltip" data-placement="top" title="Edit Pipeline Instance">
														<mwc-icon>edit</mwc-icon>
												</a>&nbsp;
												<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Pipeline Instance">
														<mwc-icon>delete</mwc-icon>
												</a> 	
											</div>
											<div class="bg-light pt-2 pb-2 pl-2 pr-2">
												<div class="d-flex justify-content-between  align-middle">
													<div><span>Public</span></div>
													<div><span class="badge badge-success">Active</span></div>
												</div>
											</div>
										</div>
									</div>
								</div>
								`
							)}
						</div>

						<div class="row">
							<h7 >&nbsp;&nbsp;&nbsp;&nbsp;Showing ${this.startIndex} to ${this.endIndex} of ${this.totalRecords} Projects</h7>
							<div style="position: absolute; right:0;" >
								<nav  aria-label="Page navigation example">
									<ul class="pagination justify-content-end">
										<li class="page-item"><a class="page-link" href="javascript:void" @click=${(e) => this.loadPage('first')}>First</a></li>
										<li class="page-item"><a class="page-link" href="javascript:void" @click=${(e) => this.loadPage('previous')}>Previous</a></li>
										<li class="page-item"><a class="page-link" href="javascript:void" @click=${(e) => this.loadPage('next')}>Next</a></li>
										<li class="page-item"><a class="page-link" href="javascript:void" @click=${(e) => this.loadPage('last')}>Last</a></li>
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
				<div class="row">
					<div class="col-md-12 py-3">
						<div class="card mb-124  shadow mb-5 bg-white rounded">
							<div class="card-body">
								
								<div class="row" style="margin:10px 0; margin-top: 0px;">
									<mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;
									<h4 class="textColor card-title">Projects</h4>
									
									<div style="position: absolute; right:0" >
										<a  class="btn btn-sm btn-secondary my-2">-</a> 
										&nbsp;&nbsp;&nbsp;&nbsp;
									</div>
								</div>
								<hr>
								<div class="row" style="margin:10px 0; margin-bottom:20px;">
									<h7 >You currently have no Projects. Get started with ML Workbench by creating your first project.</h7>
									
								</div>
								<div class="row" style="margin:10px 0">
									<button type="button" class="btn btn-primary" @click=${this.submitProject}>Create Project</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			`
		}		
		
    `;
    }
}

customElements.define("catalog-project-element", CatalogProjectLitElement);