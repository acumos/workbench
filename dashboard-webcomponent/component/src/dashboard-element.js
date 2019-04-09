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
import {style} from './dashboard-styles.js';

export class DashboardLitElement extends LitElement {
  static get properties() {
    return {
      message: { type: String, notify: true },
      componenturl: {type: String, notify: true},
      successMessge: {type: String},
      errorMessge: {type: String},
      userName : {type: String},
      projectCount: {type: Number},
      pipelineCount: {type: Number},
      notebookCount: {type: Number},
      modelCount: {type: Number},
      acuComposeUrl: {type: String},
      userName: {type: String, notify: true},
      authToken: {type: String, notify: true}
    };
  }

  static get styles() {
     return [style];
  }

  constructor() {
    super();

    this.message = "Custom message placeholder";
    this.projectCount = 0;
    this.pipelineCount = 0;
    this.notebookCount = 0;
    this.modelCount = 0;
    this.view = '';
    this.requestUpdate().then(() => {
      console.log('update componenturl : ' + this.componenturl);
      this.componenturl = (this.componenturl === undefined || this.componenturl === null)? '' : this.componenturl;
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
        this.msconfig = envVar.msconfig;
        let username = envVar.userName;
        let token = envVar.authToken;
        this.acuComposeUrl = envVar.acuComposeUrl;
        if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
          this.view = 'view';   
          this.getProjectsCount();
          this.getPipelineCount();
          this.getNotebookCount();
        } else if(username && username !== '' && token && token !== '') {
          this.view = 'view';
          this.authToken = token;
          this.userName = username;
          this.getProjectsCount();
          this.getPipelineCount();
          this.getNotebookCount();
        } else {
          this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
          this.alertOpen = true;
          this.view = 'error';        
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessge = 'Unable to retrive configuration information. Error is: '+ error;
      this.alertOpen = true;
      this.view = 'error';
    });
  }

  resetMessage(){
    this.successMessge = '';
    this.errorMessge = '';
  }

  getProjectsCount(){
    const url = this.componenturl + '/api/project/count';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.msconfig.projectmSURL,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessge = n.message;
        } else {
          this.projectCount = n.data;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessge = 'Project count fetch request failed with error: '+ error;
    });
  }

  getNotebookCount(){
    const url = this.componenturl + '/api/notebook/count';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.msconfig.notebookmSURL,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessge = n.message;
        } else {
          this.notebookCount = n.data;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessge = 'Notebook count fetch request failed with error: '+ error;
    });
  }

  getPipelineCount(){
    const url = this.componenturl + '/api/pipeline/count';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.msconfig.pipelinemSURL,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessge = n.message;
        } else {
          this.pipelineCount = n.data;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessge = 'Pipeline count fetch request failed with error: '+ error;
    });
  }

  userAction(action) {
    this.dispatchEvent(
      new CustomEvent("dashboard-event", {
        detail: {
          data: action
        }
      })
    );
  }

  launchAcuCompose(){
    window.open(this.acuComposeUrl, '_blank');
  }

  navigateToMyModel(){
    window.top.postMessage('redirectToMyModels', '*');
  }

  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
        .alertmessage {
          display: ${this.alertOpen ? "block" : "none"};
        }
      </style>
      ${this.view === 'error'
        ? html`
          <div class="alertmessage alert alert-danger">
            <a class="close" @click=${e => this.alertOpen = false}>
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

      ${this.view === 'view'
        ? html`
          <div class="content-wrapper ">
            <div class="row">
              <div class="col-lg-12">
                <div class="row">
                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                      <a href="javascript:void" @click=${e => this.userAction("project")}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                              <button class="btnIcon btn-primary">
                                <mwc-icon class="mwc-icon">share</mwc-icon>
                              </button>
                            <h4 class="font-weight-normal mt-2">Projects</h4>
                          </div>
                          ${this.projectCount === 0
                            ? html`
                              <button class="btn btn-primary card-button btn-md">
                                Create Project
                              </button>
                            `
                            : html`
                              <h6 class="font-weight-normal">${this.projectCount}</h6>
                            `
                          }
                        </div>
                      </a>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                      <a href="javascript:void" @click=${e => this.userAction("notebook")}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                            <button class="btnIcon btn-primary">
                              <mwc-icon class="mwc-icon">library_books</mwc-icon>
                            </button>
                            <h4 class="font-weight-normal mt-2">Notebooks</h4>
                          </div>
                          ${this.notebookCount === 0
                            ? html`
                              <button class="btn btn-primary card-button btn-md">
                                Create Notebook
                              </button>
                            `
                            : html`
                              <h6 class="font-weight-normal">${this.notebookCount}</h6>
                            `
                          }
                        </div>
                      </a>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                      <a href="javascript:void" @click=${e => this.userAction("pipeline")}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                            <button class="btnIcon btn-primary">
                              <mwc-icon class="mwc-icon">device_hub</mwc-icon>
                            </button>
                            <h4 class="font-weight-normal mt-2">Data Pipelines</h4>
                          </div>
                          ${this.pipelineCount === 0
                            ? html`
                              <button class="btn btn-primary card-button btn-md">
                                Create Pipeline
                              </button>
                            `
                            : html`
                              <h6 class="font-weight-normal">${this.pipelineCount}</h6>
                            `
                          }
                        </div>
                      </a>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                      <a href="javascript:void" @click=${e => this.navigateToMyModel()}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                            <button class="btnIcon btn-primary">
                              <img src="${this.componenturl}/src/my_model_icon.png" alt="My Models">
                            </button>
                            <h4 class="font-weight-normal mt-2">My Models</h4>
                          </div>
                          <h6 class="font-weight-normal">${this.modelCount}</h6>
                        </div>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        `
      : html`
      `}
    `;
  }
}

customElements.define("dashboard-element", DashboardLitElement);
