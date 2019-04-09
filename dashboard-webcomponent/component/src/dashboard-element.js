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
      user_name : {type: String},
      projectCount: {type: Number},
      pipelineCount: {type: Number},
      notebookCount: {type: Number}
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
        this.user_name = envVar.user_name;
        if(this.user_name === undefined || this.user_name === null || this.user_name === ''){
          this.errorMessge = 'Unable to retrieve User ID from Session Cookie. Pls login to Acumos portal and come back here..';
          this.view = 'error';
        } else {
          this.getProjectsCount();
          this.getPipelineCount();
          this.getNotebookCount();
        }
    }).catch((error) => {
      console.info('Request failed', error);
      this.errorMessge = 'Unable to retrive configuration information. Error is: '+ error;
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
      },
      body: JSON.stringify({
        "url": this.msconfig.projectmSURL,
        "user_name": this.user_name
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
      },
      body: JSON.stringify({
        "url": this.msconfig.notebookmSURL,
        "user_name": this.user_name
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
      },
      body: JSON.stringify({
        "url": this.msconfig.pipelinemSURL,
        "user_name": this.user_name
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

  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
      </style>
      <div class="content-wrapper ">
        <div class="row">
          <div class="col-lg-12">
            <div class="row">
              <div class="col-md-2">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <a href="javascript:void" @click=${e => this.userAction("project")}>
                    <div class="card-body text-center">
                      <div class="div-color mb-4">
                          <button class="btnIcon btn-primary">
                            <mwc-icon class="mwc-icon">share</mwc-icon>
                          </button>
                        <h4 class="font-weight-normal mt-2">Projects</h4>
                      </div>
                      <h6 class="font-weight-normal">${this.projectCount}</h6>
                    </div>
                  </a>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <a href="javascript:void" @click=${e => this.userAction("notebook")}>
                    <div class="card-body text-center">
                      <div class="div-color mb-4">
                        <button class="btnIcon btn-primary">
                          <mwc-icon class="mwc-icon">library_books</mwc-icon>
                        </button>
                        <h4 class="font-weight-normal mt-2">Notebooks</h4>
                      </div>
                      <h6 class="font-weight-normal">${this.notebookCount}</h6>
                    </div>
                  </a>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <a href="javascript:void" @click=${e => this.userAction("pipeline")}>
                    <div class="card-body text-center">
                      <div class="div-color mb-4">
                        <button class="btnIcon btn-primary">
                          <mwc-icon class="mwc-icon">device_hub</mwc-icon>
                        </button>
                        <h4 class="font-weight-normal mt-2">Data Pipelines</h4>
                      </div>
                      <h6 class="font-weight-normal">${this.pipelineCount}</h6>
                    </div>
                  </a>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body text-center">
                    <div class="div-color mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">dns</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">Datasets</h4>
                    </div>
                    <h6 class="font-weight-normal">Coming soon...</h6>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body text-center">
                    <div class="div-color mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">view_quilt</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">Predictors</h4>
                    </div>
                    <h6 class="font-weight-normal">Coming soon...</h6>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body text-center">
                    <div class="div-color mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">perm_data_setting</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">Models</h4>
                    </div>
                    <h6 class="font-weight-normal">Coming soon...</h6>
                  </div>
                </div>
              </div>
            </div>
            <div class="row  mt-4">
              <div class="col-md-3">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body div-color text-center">
                    <div class="mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">donut_small</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">CMLP Studio</h4>
                    </div>
                    <h6 class="font-weight-normal card-text">
                      CMLP Studio
                    </h6>
                    <button class="btn btn-primary card-button btn-md mt-3 mb-4">Download&nbsp;&nbsp;
                      <mwc-icon>get_app</mwc-icon>
                    </button>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body div-color text-center">
                    <div class="mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">crop</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">AcuCompose</h4>
                    </div>
                    <h6 class="font-weight-normal card-text">
                      AcuCompose
                    </h6>
                    <button class="btn btn-primary card-button btn-md mt-3 mb-4">Launch&nbsp;&nbsp;
                      <mwc-icon>launch</mwc-icon>
                    </button>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body div-color text-center">
                    <div class="mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">library_books</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">Zeppelin Notebook</h4>
                    </div>
                    <h6 class="font-weight-normal card-text">
                      Zeppelin Notebook
                    </h6>
                    <button class="btn btn-primary card-button btn-md mt-3 mb-4">Launch&nbsp;&nbsp;<mwc-icon>launch</mwc-icon>
                    </button>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card-shadow card card-link mb-3 mb-5 bg-white rounded">
                  <div class="card-body div-color text-center">
                    <div class="mb-4">
                      <button class="btnIcon btn-primary">
                        <mwc-icon class="mwc-icon">library_books</mwc-icon>
                      </button>
                      <h4 class="font-weight-normal mt-2">Jupyter Notebook</h4>
                    </div>
                    <h6 class="font-weight-normal card-text">
                      Jupyter Notebook
                    </h6>
                    <button class="btn btn-primary card-button btn-md mt-3 mb-4">Launch&nbsp;&nbsp;
                      <mwc-icon>launch</mwc-icon>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
  }
}

customElements.define("dashboard-element", DashboardLitElement);
