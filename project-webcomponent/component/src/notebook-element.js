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

import { LitElement, html } from 'lit-element';
import {style} from './notebook-styles.js';

class NotebookLitElement extends LitElement {
	static get properties() {
		return {
			message: { type: String, notify: true },
			notebookLists: { type: Array, notify: true },
			oNotebookLists: { type: Array, notify: true },
			displayNotebookLists: { type: Array, notify: true },
			fakeNotebookLists: { type: Array, notify: true },
			totalRecords: { type: Number },
			startIndex: { type: Number },
			endIndex: { type: Number },
			recordsPerPage: { type: Number },
			filterText: { type: String, notify: true },
			tableIndex: { type: Number },
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
		this.fakeNotebookLists = [];
		this.notebookLists = [
			{
				name: 'AVProject1',
				id: 1,
				description: 'DSProject1 desc1234',
				tempalte: 'proj1 temoplate1'
			},{
				name: 'ADProject2',
				id: 100,
				description: 'Project2 desc1234',
				tempalte: 'proj2 temoplate1'
			},{
				name: 'ERProject3',
				id: 10,
				description: 'Project3 desc1234',
				tempalte: 'proj3 temoplate1'
			},
			{
				name: 'FDProject4',
				id: 104,
				description: '4Project1 desc1234',
				tempalte: 'proj4 temoplate1'
			},{
				name: 'QWProject5',
				id: 105,
				description: '5Project1 desc1234',
				tempalte: 'proj5 temoplate1'
			},{
				name: 'CSProject6',
				id: 106,	
				description: '6Project1 desc1234',
				tempalte: 'proj6 temoplate1'
			},
			{
				name: 'BFProject7',
				id: 107,
				description: 'P7roject1 desc1234',
				tempalte: 'proj7 temoplate1'
			},{
				name: 'QAProject8',
				id: 108,
				description: 'P8roject1 desc1234',
				tempalte: 'proj8 temoplate1'
			},{
				name: 'AAProject9',
				id: 109,
				description: 'P9roject1 desc1234',
				tempalte: 'proj9 temoplate1'
			},
			{
				name: 'VBProject10',
				id: 110,
				description: 'Pr10oject1 desc1234',
				tempalte: 'proj10 temoplate1'
			},{
				name: 'BAProject11',
				id: 110,
				description: 'Pr11oject1 desc1234',
				tempalte: 'proj11 temoplate1'
			},{
				name: 'Project12',
				id: 110,
				description: 'Pr12oject1 desc1234',
				tempalte: 'proj12 temoplate1'
			},
			{
				name: 'Project4',
				id: 110,
				description: '4Project1 desc1234',
				tempalte: 'proj4 temoplate1'
			},{
				name: 'Project5',
				id: 110,
				description: '5Project1 desc',
				tempalte: 'proj5 temoplate1'
			},{
				name: 'Project6',
				id: 110,
				description: '6Project1 desc',
				tempalte: 'proj6 temoplate1'
			},
			{
				name: 'Project7',
				id: 110,
				description: 'P7roject1 desc',
				tempalte: 'proj7 temoplate1'
			},{
				name: 'Project8',
				id: 110,
				description: 'P8roject1 desc',
				tempalte: 'proj8 temoplate1'
			},{
				name: 'Project9',
				id: 110,
				description: 'P9roject1 desc',
				tempalte: 'proj9 temoplate1'
			},
			{
				name: 'Project10',
				id: 110,
				description: 'Pr10oject1 desc',
				tempalte: 'proj10 temoplate1'
			},{
				name: 'Project11',
				id: 110,
				description: 'Pr11oject1 desc',
				tempalte: 'proj11 temoplate1'
			},{
				name: 'Project12',
				id: 110,
				description: 'Pr12oject1 desc',
				tempalte: 'proj12 temoplate1'
			}
		];
		
		this.getProjectNotebooks();
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
				this.startIndex = this.totalRecords - start + 1;
			} else {
				this.startIndex = 1;
				this.endIndex = this.totalRecords;
			}
		}
		console.info('for user action: '+ action);
		console.info('stat index: '+ this.startIndex);
		console.info('end index: '+ this.endIndex);
		this.tableIndex = this.startIndex;
		this.displayNotebookLists = this.notebookLists.filter((notebook, index) => index >= (this.startIndex - 1) && index <= (this.endIndex - 1));
	}

	userAction(action, projectId) {
		console.log('inside the notebook element');
		fetch('/api1/posts').then(res => res.json())
			.then(function (text) {
			console.log('Request successful here.');
			console.log(text.results);
			}).catch(function (error) {
			console.log('Request failed', error);
			});
	
		this.dispatchEvent(new CustomEvent('catalog-project-event', {
			'detail': {
				data: {
					action: action,
					projectId: projectId
				}
			}
		}));
	}

	filterNotebooks(e) {
		this.filterText = e.currentTarget.value;
		if(this.filterText){
			this.notebookLists =  this.oNotebookLists.filter(notebook => {
				const filterTextLower = this.filterText.toLowerCase();
						return JSON.stringify(notebook).toLowerCase().includes(filterTextLower);
			});
		}else {
			this.notebookLists = this.oNotebookLists;
		}	
		this.totalRecords = this.notebookLists.length;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = this.recordsPerPage;
		} else {
			this.endIndex = this.totalRecords;
		}
		this.loadPage('start');
	}

	sortNotebook(e){
		this.sortCriteria = e.currentTarget.value;
		if (this.sortCriteria === 'Sort By Name') {
			this.notebookLists.sort((n1, n2) => {
				if (n1.name.toLowerCase() > n2.name.toLowerCase()) {
					return 1;
				}else if (n1.name.toLowerCase() < n2.name.toLowerCase()) {
					return -1;
				}else {
					return 0;
				}
			});
		} else if (this.sortCriteria === 'Sort By ID') {
			this.notebookLists.sort((n1, n2) => {
				if (n1.id > n2.id) {
					return 1;
				}else if (n1.id < n2.id) {
					return -1;
				}else {
					return 0;
				}
			});
		} else {
			this.notebookLists.sort((n1, n2) => {
				if (n1.createdTimestamp < n2.createdTimestamp) {
					return 1;
				}else if (n1.createdTimestamp > n2.createdTimestamp) {
					return -1;
				}else {
					return 0;
				}
			});
		}
		
		this.totalRecords = this.notebookLists.length;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = this.recordsPerPage;
		} else {
			this.endIndex = this.totalRecords;
		}
		this.loadPage('start');
	} 

	createNotebook(){

	}

	getProjectNotebooks(){
		this.oNotebookLists = this.notebookLists;
		//this.oNotebookLists = [];
		this.totalRecords = this.notebookLists.length;
		this.recordsPerPage = 5;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = this.recordsPerPage;
		} else {
			this.endIndex = this.totalRecords;
		}

		this.loadPage('start');
	}

	getNotebooks(){

	}

	deleteNotebooks(id){

	}

	editNotebook(id){

	}

	launchNotebook(id){

	}

	redirectWikiPage(){

	}

  render() {
    return html`
    	<style> 
			@import url('https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
		</style>
		${this.oNotebookLists.length > 0
			? html`
			<div class="row ">
				<div class="col-md-12 py-3">
					<div class="card mb-124 shadow mb-5 bg-white rounded">
						<div class="card-header">
							<div class="row" style="margin:5px 0; margin-top: 0px;">
								<mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
								<h4 class="textColor card-title">Notebooks</h4>
								<div style="position: absolute; right:0" >
									<a href="javascript:void" @click=${(e) => this.redirectWikiPage()} class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
									<a  class="btn btn-sm btn-secondary my-2">-</a> 
									&nbsp;&nbsp;&nbsp;&nbsp;
								</div>
							</div>
						</div>
						<div class="card-body ">
							<div class="row" style="margin:5px 0; position: absolute; right:0">
								<div class="btn-toolbar mb-2 mb-md-0">
									<div class="dropdown">
										<select class="custom-select mr-sm-2" id="template" @change=${(e) => this.sortNotebook(e)}>
											${this.sortCriterias.map(item => html`<option>${item}</option>`)}
										</select>
									</div>
									<div class="btn-group mr-2">
										&nbsp;
										<input type="text" style="height: 30px" class="form-control w-100" @input=${(e) => this.filterNotebooks(e)} placeholder="Search Notebook" aria-label="Search Notebook" aria-describedby="button-addon2">
										
										<div class="input-group-append">
											<a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Notebook Instance">
												<mwc-icon class="white-color">search</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Create Notebook Instance">
													<mwc-icon>add</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btnIcon btn btn-sm btn-secondary  mr-1" data-toggle="tooltip" data-placement="top" title="Associate Existing Notebook">
													<mwc-icon>link</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
										</div>
									</div>
								</div>
							</div>
							<br/>

							<div class="row" style="margin:40px 0; margin-bottom: 10px;">
								<table class="table table-bordered table-sm" id="cssTable">
									<thead class="thead-light">
										<tr class="d-flex">
											<th class="col-1" >#</th>
											<th class="col-3" >Notebook Name</th>
											<th class="col-2" >Version</th>
											<th class="col-2" >Notebook Type</th>
											<th class="col-2" >Creation Date</th>
											<th class="col-2">Actions</th>
										</tr>
									</thead>
									<tbody>
										${this.displayNotebookLists.map((item, index) => 
											html`
											<tr class="d-flex">
												<td class="col-1">${this.tableIndex++}</td>
												<td class="col-3">${item.name}</td>
												<td class="col-2">${item.id}</td>
												<td class="col-2">${item.id}</td>
												<td class="col-2">${item.id}</td>
												<td class="col-2" style="padding: .05rem; padding-left: 20px;">
													<a href="javascript:void" @click=${(e) => this.launchNotebook(item.id)}   class="btnIcon btn btn-sm btn-primary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Notebook Instance">
															<mwc-icon>launch</mwc-icon>
													</a>&nbsp;&nbsp;
													<a href="javascript:void" @click=${(e) => this.editNotebook(item.id)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1 " data-toggle="tooltip" data-placement="top" title="Edit Notebook Instance">
															<mwc-icon>edit</mwc-icon>
													</a>&nbsp;&nbsp;
													<a href="javascript:void" @click=${(e) => this.deleteNotebooks(item.id)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Notebook Instance">
															<mwc-icon>delete</mwc-icon>
													</a>
												</td>
											</tr>
											`
										)}
										
									</tbody>
								</table>
							</div>

							<div class="row" >
								<h7 >&nbsp;&nbsp;&nbsp;&nbsp;Showing ${this.startIndex} to ${this.endIndex} of ${this.totalRecords} Notebook(s)</h7>
								<div style="position: absolute; right:0" >
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
				</div>
			</div>
			`
			
			: html`
			<div class="row">
				<div class="col-md-12 py-3">
					<div class="card mb-124  shadow mb-5 bg-white rounded">
						<div class="card-header">
							<div class="row" style="margin:5px 0; margin-top: 0px;">
								<mwc-icon class="textColor">library_books</mwc-icon>&nbsp;&nbsp;&nbsp;
								<h4 class="textColor card-title">Notebooks</h4>
								<div style="position: absolute; right:0" >
									<a href="javascript:void" @click=${(e) => this.redirectWikiPage()} class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
									<a class="btn btn-sm btn-secondary my-2">-</a> 
									&nbsp;&nbsp;&nbsp;&nbsp;
								</div>
							</div>
						</div>
						<div class="card-body">
							<div class="row" style="margin:10px 0;margin-bottom:20px;">
								<h7 >No Notebooks.</h7>
							</div>
							<div class="row" style="margin:10px 0">
								<button type="button" class="btn btn-primary" @click=${this.submitProject}>Create Notebook</button>&nbsp;&nbsp;&nbsp;
								<button type="button" class="btn btn-secondary-button" @click=${this.submitProject}>Associate Existing Notebook</button>
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
customElements.define('notebook-element', NotebookLitElement);
