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
import { filter, get } from "lodash-es";
import { OmniModal } from "./@omni/components";
import { RegisterComponent, Forms, DataSource } from "./@omni/core";

import { ValidationMixin, DataMixin } from "./@omni/mixins";
import { style } from "./project-catalog-styles.js";

RegisterComponent(OmniModal);

export class ProjectCatalogLitElement extends DataMixin(ValidationMixin(LitElement)) {
  static get properties() {
    return {
      isOpenModal: { type: Boolean },
      projects: [],
      activeFilter: {},
      activeSort: "",
      currentPage: 0,
      totalPages: 0
    };
  }

  static get styles() {
    return [style];
  }

  constructor() {
    super();

    this.data = {
      newProject: {
        name: "",
        description: "",
        version: ""
      }
    };

    this.$validations.init({
      validations: {
        newProject: {
          name: {
            isNotEmpty: Forms.validators.isNotEmpty,
            minValue: Forms.validators.minValue(5)
          }
        }
      }
    });

    this.sortOptions = [
      { value: "name", label: "Sort By Name" },
      { value: "id", label: "Sort By ID" },
      { value: "created", label: "Sort By Created Date" }
    ];
    this.projectLists = [
      {
        name: "ABProject1",
        id: 1,
        description: "Project1 desc1234",
        tempalte: "proj1 temoplate1",
        status: "active"
      },
      {
        name: "AAProject2",
        id: 11,
        description: "Project2 desc1234",
        tempalte: "proj2 temoplate1",
        status: "active"
      },
      {
        name: "CAProject3",
        id: 2,
        description: "Project3 desc1234",
        tempalte: "proj3 temoplate1",
        status: "active"
      },
      {
        name: "ZZAProject4",
        id: 104,
        description: "4Project1 desc1234",
        tempalte: "proj4 temoplate1",
        status: "archived"
      },
      {
        name: "Project5",
        id: 105,
        description: "5Project1 desc1234",
        tempalte: "proj5 temoplate1",
        status: "active"
      },
      {
        name: "Project6",
        id: 106,
        description: "6Project1 desc1234",
        tempalte: "proj6 temoplate1",
        status: "active"
      },
      {
        name: "Project7",
        id: 107,
        description: "P7roject1 desc1234",
        tempalte: "proj7 temoplate1",
        status: "archived"
      },
      {
        name: "Project8",
        id: 108,
        description: "P8roject1 desc1234",
        tempalte: "proj8 temoplate1",
        status: "active"
      },
      {
        name: "Project9",
        id: 109,
        description: "P9roject1 desc1234",
        tempalte: "proj9 temoplate1",
        status: "active"
      },
      {
        name: "Project10",
        id: 110,
        description: "Pr10oject1 desc1234",
        tempalte: "proj10 temoplate1",
        status: "active"
      },
      {
        name: "Project11",
        id: 110,
        description: "Pr11oject1 desc1234",
        tempalte: "proj11 temoplate1",
        status: "archived"
      },
      {
        name: "Project12",
        id: 110,
        description: "Pr12oject1 desc1234",
        tempalte: "proj12 temoplate1",
        status: "active"
      },
      {
        name: "Project4",
        id: 110,
        description: "4Project1 desc1234",
        tempalte: "proj4 temoplate1",
        status: "active"
      },
      {
        name: "Project5",
        id: 110,
        description: "5Project1 desc",
        tempalte: "proj5 temoplate1",
        status: "archived"
      },
      {
        name: "Project6",
        id: 110,
        description: "6Project1 desc",
        tempalte: "proj6 temoplate1",
        status: "active"
      },
      {
        name: "Project7",
        id: 110,
        description: "P7roject1 desc",
        tempalte: "proj7 temoplate1",
        status: "archived"
      },
      {
        name: "Project8",
        id: 110,
        description: "P8roject1 desc",
        tempalte: "proj8 temoplate1",
        status: "active"
      },
      {
        name: "Project9",
        id: 110,
        description: "P9roject1 desc",
        tempalte: "proj9 temoplate1",
        status: "active"
      },
      {
        name: "Project10",
        id: 110,
        description: "Pr10oject1 desc",
        tempalte: "proj10 temoplate1",
        status: "archived"
      },
      {
        name: "Project11",
        id: 110,
        description: "Pr11oject1 desc",
        tempalte: "proj11 temoplate1",
        status: "active"
      },
      {
        name: "Project12",
        id: 110,
        description: "Pr12oject1 desc",
        tempalte: "proj12 temoplate1",
        status: "active"
      }
    ];

    this.getProjects();
  }

  filterProjects(criteria) {
    this.activeFilter = criteria;
    this.dataSource.filter(criteria);
    this.projects = this.dataSource.data();
  }

  sortProjects(key) {
    if (key === "created") {
      this.dataSource.sort((n1, n2) => {
        if (n1.createdTimestamp < n2.createdTimestamp) {
          return 1;
        } else if (n1.createdTimestamp > n2.createdTimestamp) {
          return -1;
        } else {
          return 0;
        }
      });
    } else {
      this.dataSource.sort(key);
    }

    this.projects = this.dataSource.data();
  }

  navigatePage(direction) {
    this.dataSource.navigatePage(direction);

    this.currentPage = this.dataSource.page() + 1;
    this.projects = this.dataSource.data();
  }

  getFilteredCount(criteria) {
    return filter(this.dataSource._rawData, criteria).length;
  }

  searchProjects(query) {
    this.dataSource.filter(query, true);

    this.projects = this.dataSource.data();
  }

  tempMethod() {
    //https://randomuser.me/api/?results=10

    fetch("/config", {
      mode: "cors",
      redirect: "follow",
      headers: new Headers({
        "Content-Type": "text/plain",
        Authorization: "Basic azsedcd"
      })
    })
      .then(res => res.json())
      .then(function(text) {
        console.log("Request successful here for config call");
        console.log(text.results);
      })
      .catch(function(error) {
        console.log("Request failed", error);
      });

    fetch("/api1/config", {
      mode: "cors",
      redirect: "follow",
      headers: new Headers({
        "Content-Type": "text/plain",
        Authorization: "Basic authorization"
      })
    })
      .then(res => res.json())
      .then(function(text) {
        console.log("Request successful here.");
        console.log(text.results);
      })
      .catch(function(error) {
        console.log("Request failed", error);
      });

    console.log("after first Request successful here");

    const Http = new XMLHttpRequest();
    const url = "https://jsonplaceholder.typicode.com/posts";
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange = e => {
      console.log("second Request successful here.");
      console.log(Http.responseText);
    };
    console.log("after second Request successful here");
  }

  userAction(action, projectId) {
    console.log("action");
    console.log(action);
    this.dispatchEvent(
      new CustomEvent("catalog-project-event", {
        detail: {
          data: {
            action: action,
            projectId: projectId
          }
        }
      })
    );
  }

  modalDismissed() {
    this.isOpenModal = false;
  }

  modalClosed() {
    this.$validations.validate("newProject");
    this.isOpenModal = false;
  }

  onDataCommit(data) {
    console.log(data);
  }
  openModal() {
    this.isOpenModal = true;
  }

  getProjects() {
    this.activeFilter = { status: "active" };
    this.activeSort = "name";

    this.dataSource = new DataSource({
      data: this.projectLists,
      filter: this.activeFilter,
      sort: this.activeSort
    });

    this.currentPage = this.dataSource.page() + 1;
    this.totalPages = this.dataSource.totalPages();
    this.projects = this.dataSource.data();
  }

  createProject() {}

  deleteProject(projectId) {}

  archiveProject(projectId) {}

  restoreProject(projectId) {}

  viewProject(projectId) {
    console.log("inside the viewProject methodf");
  }

  redirectWikiPage() {}

  render() {
    return html`
      <style>
        @import url("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css");
      </style>
      <omni-modal
        title="Create Project"
        close-string="Save"
        is-open="${this.isOpenModal}"
        @omni-modal-dimissed="${this.modalDismissed}"
        @omni-modal-closed="${this.modalClosed}"
      >
        <form>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Project Name</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Enter Project Name"
                  value="${this.data.newProject.name}"
                  @blur="${ e => this.$data.$set('newProject.name', e.target.value)}"
                />
              </div>
              <div class="form-group">
                <label>Project Description</label>
                <textarea class="form-control"
                  @blur="${ e => this.$data.$set('newProject.description', e.target.value)}"
                >
                  ${this.data.newProject.description}</textarea
                >
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Project Version</label>
                <input
                  type="text"
                  class="form-control"
                  placeholder="Enter Project Version"
                  value="${this.data.newProject.version}"
                  @blur="${ e => this.$data.$set('newProject.version', e.target.value)}"
                />
              </div>
            </div>
          </div>
        </form>
      </omni-modal>

      ${this.projects.length > 0
        ? html`
            <div class="row" style="margin:5px 0;">
              <div class="col-lg-12">
                <div class="row" style="margin:15px 0;">
                  <div
                    class="btn-toolbar mb-2 mb-md-0"
                    style="position: absolute; right:0;"
                  >
                    <div class="btn-group mr-2">
                      <button
                        type="button"
                        class="btn btn-primary"
                        style="border-top-right-radius:3px; border-bottom-right-radius: 3px"
                        @click="${this.openModal}"
                      >
                        Create Project</button
                      >&nbsp;&nbsp;

                      <div class="input-group-append">
                        <a
                          href="javascript:void"
                          @click=${e => this.redirectWikiPage()}
                          class="btnIconTop btn btn-sm btn-secondary mr-1"
                          data-toggle="tooltip"
                          data-placement="top"
                          title="Click here for wiki help"
                        >
                          <mwc-icon>help</mwc-icon>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
                <br />
                <div class="row" style="margin:5px 0; margin-top:20px;">
                  <div class="btn-toolbar mb-2 mb-md-0">
                    <ul class="nav nav-pills mb-3">
                      <li class="nav-item mr-2">
                        <a
                          href="#"
                          @click=${e => this.filterProjects({ status: "active" })}
                          class="nav-link ${get(this.activeFilter, "status", "") ===
                          "active"
                            ? "active"
                            : ""}"
                          >Active Projects&nbsp;&nbsp;<span class="badge badge-light"
                            >${this.getFilteredCount({ status: "active" })}</span
                          ></a
                        >
                      </li>
                      <li class="nav-item mr-2">
                        <a
                          href="#"
                          @click=${e => this.filterProjects({ status: "archived" })}
                          class="nav-link btn-outline-secondary ${get(
                            this.activeFilter,
                            "status",
                            ""
                          ) === "archived"
                            ? "active"
                            : ""}"
                          >Archived Projects&nbsp;&nbsp;<span
                            class="badge badge-secondary"
                            >${this.getFilteredCount({ status: "archived" })}</span
                          ></a
                        >
                      </li>
                      <li class="nav-item mr-2">
                        <a
                          href="#"
                          @click=${e => this.filterProjects()}
                          class="nav-link btn-outline-secondary  ${get(
                            this.activeFilter,
                            "status",
                            ""
                          ) === ""
                            ? "active"
                            : ""}"
                          >All Projects&nbsp;&nbsp;<span class="badge badge-secondary"
                            >${this.getFilteredCount()}</span
                          ></a
                        >
                      </li>
                    </ul>

                    <div
                      class="btn-toolbar mb-2 mb-md-0"
                      style="position: absolute; right:0;"
                    >
                      <div class="dropdown">
                        <select
                          class="custom-select mr-sm-2"
                          id="template"
                          @change=${e => this.sortProjects(e.target.value)}
                        >
                          ${this.sortOptions.map(item =>
                            item.value === this.activeSort
                              ? html`
                                  <option value="${item.value}" selected
                                    >${item.label}</option
                                  >
                                `
                              : html`
                                  <option value="${item.value}">${item.label}</option>
                                `
                          )}
                        </select>
                      </div>
                      <div class="btn-group mr-2">
                        &nbsp;
                        <input
                          type="text"
                          style="height: 30px"
                          class="form-control w-100"
                          @input=${e => this.searchProjects({ name: e.target.value })}
                          placeholder="Search Project"
                        />

                        <div class="input-group-append">
                          <a
                            class="btnIcon btn btn-sm btn-primary  mr-1"
                            data-toggle="tooltip"
                            data-placement="top"
                            title="Search Project"
                          >
                            <mwc-icon class="mwc-icon">search</mwc-icon>
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row" style="margin-top:10px;">
                  ${this.projects.map(
                    item =>
                      html`
                        <div class="col-md-3">
                          <div
                            class="card-shadow card card-link mb-3 mb-5 bg-white rounded"
                          >
                            <a
                              href="javascript:void"
                              @click=${e => this.userAction("view-project", item.id)}
                            >
                              <div class="card-body">
                                <h4 class="project-name">${item.name}</h4>
                                <span><strong>Project ID</strong>: &nbsp; ${item.id}</span
                                ><br />
                                <span
                                  ><strong>Project Version</strong>: &nbsp;
                                  ${item.id}</span
                                ><br />
                                <strong>Project Status</strong>: &nbsp;
                                ${item.status === "active"
                                  ? html`
                                      <span class="active-status">${item.status}</span>
                                    `
                                  : html`
                                      <span class="inactive-status">${item.status}</span>
                                    `}
                                <br />
                                <span
                                  ><strong>Creation Date</strong>: &nbsp; ${item.id}</span
                                ><br />
                                <span
                                  ><strong>Modified Date</strong>: &nbsp; ${item.id}</span
                                ><br />
                                <br />

                                <div class="gray-light pt-2 pb-2 pl-2 pr-2">
                                  <div
                                    class="d-flex justify-content-between  align-middle"
                                  >
                                    <div style="margin-top:8px;">
                                      <span title="${item.id}"
                                        ><mwc-icon class="mwc-icon-gray"
                                          >account_circle</mwc-icon
                                        >
                                      </span>
                                    </div>
                                    <div>
                                      ${item.status === "active"
                                        ? html`
                                            <a
                                              href="javascript:void"
                                              @click=${e => this.archiveProject(item.id)}
                                              class="btnIcon btn btn-sm my-1 mr-1"
                                              data-toggle="tooltip"
                                              data-placement="top"
                                              title="Archive Project"
                                            >
                                              <mwc-icon class="mwc-icon-gray"
                                                >archive</mwc-icon
                                              >
                                            </a>
                                          `
                                        : html`
                                            <a
                                              href="javascript:void"
                                              @click=${e => this.restoreProject(item.id)}
                                              class="btnIcon btn btn-sm my-1 mr-1"
                                              data-toggle="tooltip"
                                              data-placement="top"
                                              title="Restore Project"
                                            >
                                              <mwc-icon class="mwc-icon-gray"
                                                >restore</mwc-icon
                                              >
                                            </a>
                                            <a
                                              href="javascript:void"
                                              @click=${e => this.deleteProject(item.id)}
                                              class="btnIcon btn btn-sm my-1 mr-1"
                                              data-toggle="tooltip"
                                              data-placement="top"
                                              title="Delete Project"
                                            >
                                              <mwc-icon class="mwc-icon-gray"
                                                >delete</mwc-icon
                                              >
                                            </a>
                                          `}
                                    </div>
                                  </div>
                                </div>
                              </div></a
                            >
                          </div>
                        </div>
                      `
                  )}
                </div>

                <div class="row">
                  <h7>Showing ${this.currentPage} of ${this.totalPages} pages</h7>
                  <div style="position: absolute; right:0;">
                    <nav aria-label="Page navigation example">
                      <ul class="pagination justify-content-end">
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("first")}
                            >First</a
                          >
                        </li>
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("previous")}
                            >Previous</a
                          >
                        </li>
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("next")}
                            >Next</a
                          >
                        </li>
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="javascript:void"
                            @click=${e => this.navigatePage("last")}
                            >Last</a
                          >
                        </li>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                      </ul>
                    </nav>
                  </div>
                </div>
                <br />
              </div>
            </div>
          `
        : html`
            <div class="row" style="margin:15px 0;">
              <div class="btn-toolbar mb-2 mb-md-0" style="position: absolute; right:0;">
                <div class="btn-group mr-2">
                  <div class="input-group-append">
                    <a
                      href="javascript:void"
                      @click=${e => this.redirectWikiPage()}
                      class="btnIconTop btn btn-sm btn-secondary  mr-1"
                      data-toggle="tooltip"
                      data-placement="top"
                      title="Click here for wiki help"
                    >
                      <mwc-icon>help</mwc-icon>
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <br />
            <div class="row">
              <div class="col-md-12 py-3">
                <div class="card mb-124  shadow mb-5 bg-white rounded">
                  <div class="card-header">
                    <div class="row" style="margin:5px 0; margin-top: 0px;">
                      <mwc-icon class="textColor">share</mwc-icon>&nbsp;&nbsp;&nbsp;
                      <h4 class="textColor card-title">Projects</h4>
                      <div style="position: absolute; right:0">
                        <a class="btn btn-sm btn-secondary my-2">-</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                      </div>
                    </div>
                  </div>
                  <div class="card-body">
                    <div class="row" style="margin:10px 0;margin-bottom:20px;">
                      <h7
                        >No Projects, get started with ML Workbench by creating your first
                        project.</h7
                      >
                    </div>
                    <div class="row" style="margin:10px 0">
                      <button
                        type="button"
                        class="btn btn-primary"
                        @click="${this.openModal}"
                      >
                        Create Project
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          `}
    `;
  }
}

customElements.define("project-catalog-element", ProjectCatalogLitElement);
