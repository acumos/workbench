/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
			filterText: { type: String, notify: true }
        };
    }
    constructor() {
        super();
		this.message = "Cutom message placeholder";
		this.projectLists = [
			{
				name: 'Project1',
				id: 101,
				description: 'Project1 desc1234',
				tempalte: 'proj1 temoplate1'
			},{
				name: 'Project2',
				id: 102,
				description: 'Project2 desc1234',
				tempalte: 'proj2 temoplate1'
			},{
				name: 'Project3',
				id: 103,
				description: 'Project3 desc1234',
				tempalte: 'proj3 temoplate1'
			},
			{
				name: 'Project4',
				id: 104,
				description: '4Project1 desc1234',
				tempalte: 'proj4 temoplate1'
			},{
				name: 'Project5',
				id: 105,
				description: '5Project1 desc1234',
				tempalte: 'proj5 temoplate1'
			},{
				name: 'Project6',
				id: 106,	
				description: '6Project1 desc1234',
				tempalte: 'proj6 temoplate1'
			},
			{
				name: 'Project7',
				id: 107,
				description: 'P7roject1 desc1234',
				tempalte: 'proj7 temoplate1'
			},{
				name: 'Project8',
				id: 108,
				description: 'P8roject1 desc1234',
				tempalte: 'proj8 temoplate1'
			},{
				name: 'Project9',
				id: 109,
				description: 'P9roject1 desc1234',
				tempalte: 'proj9 temoplate1'
			},
			{
				name: 'Project10',
				id: 110,
				description: 'Pr10oject1 desc1234',
				tempalte: 'proj10 temoplate1'
			},{
				name: 'Project11',
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
		this.oProjectLists = this.projectLists;
		this.totalRecords = this.projectLists.length;
		this.recordsPerPage = 12;
		this.startIndex = 1;
		if (this.totalRecords > this.recordsPerPage) {
			this.endIndex = 12;
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
			this.endIndex = 12;
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
		<style>
			@import url('http://cdn.materialdesignicons.com/3.3.92/css/materialdesignicons.min.css');
		</style>
         <style>
          #divid { 
          		border: thin solid darkblue;
                padding: 25px; 
                border-radius: 5px;
          }
          
	      .btn-primary {
	       		background-color: #591887;
	       		border-color: transparent;
	      }
	       
	      .btn-outline-primary {
	       		color: #591887;
	       		border-color: #591887;
		  }
		  
	      .project-name {
				color: #591887;
		  }

	      label {
	       		color: #4b4b4b;
	      } 
		</style>

		<div class="content-wrapper ">
			<div class="row">
				<div class="col-lg-12">
					<div class="row">
						<div class="col-lg-12">
							<div class="row">
								<div class="col-md-9">
									<ul class="nav nav-pills mb-3" id="pills-tab" role="tablist">
										<li class="nav-item mr-2"> <a class="nav-link active" id="pills-home-tab" data-toggle="pill" href="#pills-home" role="tab" aria-controls="pills-home" aria-selected="true">All&nbsp;&nbsp;<span class="badge badge-light">34</span></a> </li>
										<li class="nav-item mr-2"> 
											<a class="nav-link btn-outline-secondary" id="pills-profile-tab" data-toggle="pill" href="#pills-profile" role="tab" aria-controls="pills-profile" aria-selected="false">Public Projects&nbsp;&nbsp;<span class="badge badge-secondary">12</span></a> </li>
										<li class="nav-item mr-2"> 
											<a class="nav-link btn-outline-secondary" id="pills-contact-tab" data-toggle="pill" href="#pills-contact" role="tab" aria-controls="pills-contact" aria-selected="false">Shared Projects&nbsp;&nbsp;<span class="badge badge-secondary">23</span></a> </li>
										<li class="nav-item mr-2"> 
											<a class="nav-link btn-outline-secondary" id="pills-contact-tab" data-toggle="pill" href="#pills-contact" role="tab" aria-controls="pills-contact" aria-selected="false">My Projects&nbsp;&nbsp;<span class="badge badge-secondary">7</span></a> </li>
									</ul>
								</div>
								<div class="col-md-3">
									<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 ">
										<div class="btn-toolbar mb-2 mb-md-0">
											<div class="dropdown">
												<button class="btn btn-outline-secondary dropdown-toggle mr-2" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> Sort By </button>
												<div class="dropdown-menu" aria-labelledby="dropdownMenuButton"> <a class="dropdown-item" href="#">Date</a> <a class="dropdown-item" href="#">ID</a> <a class="dropdown-item" href="#">Name</a> </div>
											</div>
											<div class="btn-group mr-2">
												<input type="text" class="form-control w-100" @input=${(e) => this.filterProjects(e)} placeholder="Search Project" aria-label="Search Project" aria-describedby="button-addon2">
												<div class="input-group-append">
													<button class="btn btn-primary btn-icon input-group-text" type="button" id="button-addon2"><span data-feather="search"></span></button>
												</div>
											</div>
											
										</div>
									</div>
								</div>
							</div>
							  
							

							<div class="row">
								${this.displayProjectLists.map(item => 
									html`
									<div class="col-md-3">
										<div class="card mb-3 box-shadow">
											<div class="card-body">
												<h2 class="project-name">${item.name}</h2>
												<p class="small"><strong>Project ID</strong>: &nbsp; ${item.id}</p>
												<p class="small"><strong>Project Version</strong>: &nbsp; ${item.id}</p>
												<p class="small"><strong>Creation Date</strong>: &nbsp; ${item.id}</p>
												<p class="small"><strong>Modified Date</strong>: &nbsp; ${item.id}</p>
												
												<p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. </p>
												<div class="d-flex align-items-center pb-2"> 
													<a href="javascript:void" @click=${(e) => this.userAction('view-project', item.id)} class="btn btn-sm btn-primary my-2 mr-2" data-toggle="tooltip" data-placement="top" title="Stop Pipeline Instance">
														<mwc-icon>remove_red_eye</mwc-icon>
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
							<nav aria-label="Page navigation example">
								<ul class="pagination">
									<li class="page-item">
										<a class="page-link" aria-label="Previous">
											<span aria-hidden="true">&laquo;</span>
											<span class="sr-only">Previous</span>
										</a>
									</li>
									<li class="page-item"><a class="page-link" href="javascript:void" @click=${(e) => this.loadPage('previous')}>Previous</a></li>
    								<li class="page-item"><a class="page-link" href="javascript:void" @click=${(e) => this.loadPage('next')}>Next</a></li>
									<li class="page-item">
										<a disabled class="page-link" aria-label="Next">
											<span aria-hidden="true">&raquo;</span>
											<span class="sr-only">Next</span>
										</a>
									</li>
								</ul>
							</nav>
						</div>
					</div>
				</div>
			</div>
		</div>
        `;
    }
}

customElements.define("catalog-project-element", CatalogProjectLitElement);