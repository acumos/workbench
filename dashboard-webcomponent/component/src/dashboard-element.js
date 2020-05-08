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
      errorMessage: {type: String},
      userName : {type: String},
      projectCount: {type: Number},
      sharedProjectCount: {type: Number},
      pipelineCount: {type: Number},
      notebookCount: {type: Number},
      datasourceCount: {type: Number},
      modelCount: {type: Number},
      prModelCount: {type: Number},
      portalFEUrl: {type: String},
      userName: {type: String, notify: true},
      authToken: {type: String, notify: true},
      portalBEUrl: {type: String},
      userId: {type: String},
      myModelsUrl: {type: String},
      parentMsg: {type: String},
      pipelineFlag: { type: String }
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

    this.message = "Custom message placeholder";
    this.projectCount = 0;
    this.sharedProjectCount = 0;
    this.pipelineCount = 0;
    this.notebookCount = 0;
    this.datasourceCount = 0;
    this.modelCount = 0;
    this.prModelCount = 0;
    this.view = '';
    
    this.requestUpdate().then(() => {
      this.onLoad();
      this.componenturl = (this.componenturl === undefined || this.componenturl === null)? '' : this.componenturl;
      this.parentMsg = (this.parentMsg === undefined || this.parentMsg === null)? '' : this.parentMsg;
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
        let id = envVar.userId;
        this.portalFEUrl = envVar.portalFEUrl;
        this.portalBEUrl = envVar.portalBEUrl;
        this.pipelineFlag = envVar.pipelineFlag;
        if(this.userName && this.userName !== '' && this.authToken && this.authToken !== '' && this.userId && this.userId !== '') {
          this.view = 'view';   
          this.getProjectsCount();
          this.getPipelineCount();
          this.getNotebookCount();
          this.datasourceCount();
          this.getPRModelCount();
          this.getModelCount();
        } else if(username && username !== '' && token && token !== '' && id && id !== '') {
          this.view = 'view';
          this.authToken = token;
          this.userName = username;
          this.userId = id;
          this.getProjectsCount();
          this.getPipelineCount();
          this.getNotebookCount();
          this.datasourceCount();
          this.getPRModelCount();
          this.getModelCount();
        } else {
          this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
          this.alertOpen = true;
          this.view = 'error';        
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessage = 'Unable to retrive configuration information. Error is: '+ error;
      this.alertOpen = true;
      this.view = 'error';
    });
  }

  resetMessage(){
    this.successMessge = '';
    this.errorMessage = '';
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
          this.errorMessage = n.message;
        } else {
          this.projectCount = n.data;
          this.getSharedProjectsCount();
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Project count fetch request failed with error: '+ error;
    });
  }

  getSharedProjectsCount(){
    const url = this.componenturl + '/api/project/sharedProjects';
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
          this.errorMessage = n.message;
        } else {
          this.sharedProjectCount = n.data+this.projectCount;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Project count fetch request failed with error: '+ error;
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
          this.errorMessage = n.message;
        } else {
          this.notebookCount = n.data;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Notebook count fetch request failed with error: '+ error;
    });
  }

  getdatasourceCount(){
    const url = this.componenturl + '/api/datasource/count';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken,
      },
      body: JSON.stringify({
        "url": this.msconfig.datasourcemSURL,
        "userName": this.userName
      })
    }).then(res => res.json())
      .then((n) => {
        if(n.status === 'Error'){
          this.errorMessage = n.message;
        } else {
          this.datasourceCount = n.data;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Datasource count fetch request failed with error: '+ error;
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
          this.errorMessage = n.message;
        } else {
          this.pipelineCount = n.data;
        }
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Pipeline count fetch request failed with error: '+ error;
    });
  }

  
  getModelCount(){
    const url = this.componenturl + '/api/model/count';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken
      },
      body: JSON.stringify({
        "url": this.portalBEUrl,
        "userName": this.userName,
        "reqBody" : {
          "request_body": {
            "published": true,
            "active": true,
            "pageRequest": {
              "page" : 1,
              "size" : 1
            }
          }
        }

      }),
    }).then(res => res.json())
      .then((n) => {
        // if(n.status === 'Error'){
        //   this.errorMessage = n.message;
        //   this.modelCount = this.prModelCount;
        // } else {
          this.modelCount = n.data;
        //}
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Model count fetch request failed with error: '+ error;
    });
  }

  getPRModelCount(){
    const url = this.componenturl + '/api/prmodel/count';
	  fetch(url, {
		  method: 'POST',
      mode: 'cors',
      cache: 'default',
      headers: {
        "Content-Type": "application/json",
        "auth": this.authToken
      },
      body: JSON.stringify({
        "url": this.portalBEUrl,
        "userName": this.userName,
        "reqBody" : {
          "request_body": {
            "published": false,
            "active": true,
            "userId": this.userId,
            "pageRequest": {
              "page" : 1,
              "size" : 1
            }
          }
        }

      }),
    }).then(res => res.json())
      .then((n) => {
        // if(n.status === 'Error'){
        //   this.errorMessage = n.message;
        //   this.prModelCount = 0;
        // } else {
          this.prModelCount = n.data;
        //}
        
    }).catch((error) => {
      console.error('Request failed', error);
      this.errorMessage = 'Model count fetch request failed with error: '+ error;
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
  
  navigateToMyModel(){
    if(this.parentMsg === "iframeMsg"){
	    window.top.postMessage('navigateToMyModel', '*');
    } else{
      this.myModelsUrl = this.portalFEUrl + '/#/manageModule';
      window.open(this.myModelsUrl, '_blank');
    }
  }

  navigateToMarketplace(){
    if(this.parentMsg === "iframeMsg"){
	    window.top.postMessage('navigateToMarketplace', '*');
    } else{
      this.myModelsUrl = this.portalFEUrl + '/#/marketPlace';
      window.open(this.myModelsUrl, '_blank');
    }
  }

  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
      </style>
      ${this.view === 'error'
        ? html`
          <div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
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
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white h-75">
                      <a href="javascript:void" @click=${e => this.userAction("project")}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                              <button class="btnIcon btn-primary">
                                <mwc-icon class="mwc-icon">share</mwc-icon>
                              </button>
                            <h2 class="font-weight-normal mt-2">Projects</h2>
                          </div>
                          ${this.projectCount === 0
                            ? html`
                              <button class="btn btn-primary card-button btn-md">
                                Create Project
                              </button>
                            `
                            : html`
                              <h4 class="font-weight-normal">${this.sharedProjectCount}</h4>
                            `
                          }
                        </div>
                      </a>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white h-75">
                      <a href="javascript:void" @click=${e => this.userAction("notebook")}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                            <button class="btnIcon btn-primary">
                              <mwc-icon class="mwc-icon">import_contacts</mwc-icon>
                            </button>
                            <h2 class="font-weight-normal mt-2">Notebooks</h2>
                          </div>
                          ${this.notebookCount === 0
                            ? html`
                              <button class="btn btn-primary card-button btn-md">
                                Create Notebook
                              </button>
                            `
                            : html`
                              <h4 class="font-weight-normal">${this.notebookCount}</h4>
                            `
                          }
                        </div>
                      </a>
                    </div>
                  </div>

                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white h-75">
                      <a href="javascript:void" @click=${e => this.userAction("dataset")}>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                            <button class="btnIcon btn-primary">
                              <mwc-icon class="mwc-icon">import_contacts</mwc-icon>
                            </button>
                            <h2 class="font-weight-normal mt-2">Datasources</h2>
                          </div>
                          ${this.datasourceCount === 0
                            ? html`
                              <button class="btn btn-primary card-button btn-md">
                                Create Notebook
                              </button>
                            `
                            : html`
                              <h4 class="font-weight-normal">${this.datasourceCount}</h4>
                            `
                          }
                        </div>
                      </a>
                    </div>
                  </div>

                  ${this.pipelineFlag === "true"
                    ?html`
                    <div class="col-md-3">
                      <div class="card-shadow card card-link mb-3 mb-5 bg-white h-75">
                        <a href="javascript:void" @click=${e => this.userAction("pipeline")}>
                          <div class="card-body text-center">
                            <div class="div-color mb-4">
                              <button class="btnIcon btn-primary">
                                <mwc-icon class="mwc-icon">device_hub</mwc-icon>
                                </button>
                              <h2 class="font-weight-normal mt-2">Data Pipelines</h2>
                              </div>
                              ${this.pipelineCount === 0
                                ? html`
                                  <button class="btn btn-primary card-button btn-md">
                                  Create Pipeline
                                  </button>
                                `
                              : html`
                                  <h4 class="font-weight-normal">${this.pipelineCount}</h4>
                                `
                              }
                            </div>
                        </a>
                      </div>
                    </div>  
                    `
                    :``
                  }
                  <div class="col-md-3">
                    <div class="card-shadow card card-link mb-3 mb-5 bg-white h-75">
                      <a>
                        <div class="card-body text-center">
                          <div class="div-color mb-4">
                            <button class="btnIcon btn-primary">
                              <img src="${this.componenturl}/src/my_model_icon.png" alt="My Models">
                            </button>
                          </div>
                            <button class="btn btn-primary card-button btn-md btn-block" style="width:50%" @click=${e => this.navigateToMyModel()}>
                            My Models&nbsp;&nbsp;
                            <span class="badge badge-light">
                              ${this.prModelCount}</span>
                            </button>
                            <button class="btn btn-primary card-button btn-md btn-block" style="width:50%" @click=${e => this.navigateToMarketplace()}>
                            Marketplace&nbsp;&nbsp;
                            <span class="badge badge-light">
                              ${this.modelCount}</span>
                            </button>
                            
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
