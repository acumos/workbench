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

import { LitElement, html } from "./node_modules/lit-element";

export class DashboardLitElement extends LitElement {
  static get properties() {
    return {
      message: { type: String, notify: true }
    };
  }
  constructor() {
    super();

    this.message = "Custom message placeholder";
  }

  userAction(action) {
    console.log("action");
    console.log(action);
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
        @import url("https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
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

        label {
          color: #4b4b4b;
        }
      </style>

      <div class="content-wrapper ">
        <div class="row">
          <div class="col-lg-12">
            <div class="row">
              <div class="col-md-2">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="text-primary mb-4">
                      <mwc-icon>developer_board</mwc-icon>
                      <h3 class="font-weight-normal mt-2">Projects</h3>
                    </div>
                    <h2 class="font-weight-normal">67</h2>
                    <div class="d-flex justify-content-between">
                      <button
                        class="btn btn-primary btn-sm mt-3 mb-4"
                        @click=${e => this.userAction("catalog-project")}
                      >
                        Catalog
                      </button>
                      <button
                        class="btn btn-primary btn-sm mt-3 mb-4"
                        @click=${e => this.userAction("create-project")}
                      >
                        create
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="text-danger mb-4">
                      <mwc-icon>pie_chart</mwc-icon>
                      <h3 class="font-weight-normal mt-2">Models</h3>
                    </div>
                    <h2 class="font-weight-normal">89</h2>
                    <div class="d-flex justify-content-between">
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Catalog</button>
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Create</button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="text-info mb-4">
                      <mwc-icon>gavel</mwc-icon>
                      <h3 class="font-weight-normal mt-2">Pipelines</h3>
                    </div>
                    <h2 class="font-weight-normal">23</h2>
                    <div class="d-flex justify-content-between">
                      <a
                        class="btn btn-primary btn-sm mt-3 mb-4"
                        href="pipelines/pipelines-catalog.html"
                        >Catalog</a
                      >
                      <a
                        class="btn btn-primary btn-sm mt-3 mb-4"
                        href="pipelines/create-pipeline.html"
                        >Create</a
                      >
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="text-success mb-4">
                      <mwc-icon>supervised_user_circle</mwc-icon>
                      <h3 class="font-weight-normal mt-2">Users</h3>
                    </div>
                    <h2 class="font-weight-normal">14</h2>
                    <div class="d-flex justify-content-between">
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Catalog</button>
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Create</button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="text-primary mb-4">
                      <mwc-icon>collections</mwc-icon>
                      <h3 class="font-weight-normal mt-2">Predictors</h3>
                    </div>
                    <h2 class="font-weight-normal">34</h2>
                    <div class="d-flex justify-content-between">
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Catalog</button>
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Create</button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-2">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="text-danger mb-4">
                      <mwc-icon>perm_data_setting</mwc-icon>
                      <h3 class="font-weight-normal mt-2">DataSets</h3>
                    </div>
                    <h2 class="font-weight-normal">56</h2>
                    <div class="d-flex justify-content-between">
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Catalog</button>
                      <button class="btn btn-primary btn-sm mt-3 mb-4">Create</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="row  mt-4">
              <div class="col-md-3">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="mb-4">
                      <img
                        src="https://via.placeholder.com/92x92"
                        class="img-lg rounded-circle mb-2"
                        alt="profile image"
                      />
                      <h4>CMLP Studio</h4>
                    </div>
                    <p class="mt-4 card-text">
                      Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean
                      commodo ligula eget dolor. Lorem
                    </p>
                    <button class="btn btn-primary btn-sm mt-3 mb-4">Launch</button>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="mb-4">
                      <img
                        src="https://via.placeholder.com/92x92"
                        class="img-lg rounded-circle mb-2"
                        alt="profile image"
                      />
                      <h4>AcuCompose</h4>
                    </div>
                    <p class="mt-4 card-text">
                      Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean
                      commodo ligula eget dolor. Lorem
                    </p>
                    <button class="btn btn-primary btn-sm mt-3 mb-4">Launch</button>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="mb-4">
                      <img
                        src="https://via.placeholder.com/92x92"
                        class="img-lg rounded-circle mb-2"
                        alt="profile image"
                      />
                      <h4>Zeppelin Notebook</h4>
                    </div>
                    <p class="mt-4 card-text">
                      Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean
                      commodo ligula eget dolor. Lorem
                    </p>
                    <button class="btn btn-primary btn-sm mt-3 mb-4">Launch</button>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card shadow-sm">
                  <div class="card-body text-center">
                    <div class="mb-4">
                      <img
                        src="https://via.placeholder.com/92x92"
                        class="img-lg rounded-circle mb-2"
                        alt="profile image"
                      />
                      <h4>Jupyter Notebook</h4>
                    </div>
                    <p class="mt-4 card-text">
                      Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean
                      commodo ligula eget dolor. Lorem
                    </p>
                    <button class="btn btn-primary btn-sm mt-3 mb-4">Launch</button>
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
