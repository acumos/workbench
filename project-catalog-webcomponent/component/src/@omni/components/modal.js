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

export  class OmniModal extends LitElement {
  static get name() {
    return 'omni-modal'
  }
  
  static get properties() {
    return {
      title: { type: String },
      isOpen: {
        attribute: "is-open",
        type: Boolean,
        converter: {
          fromAttribute: value => {
            if (value === "undefined" || value === "null" || value === "false") {
              return false;
            }
            return true;
          }
        }
      },
      closeString: { type: String, attribute: "close-string" },
      dismissString: { type: String }
    };
  }
  constructor() {
    super();
    this.acceptString = "Ok";
    this.dismissString = "Cancel";
    this.isOpen = false;
  }

  dismiss() {
    this.isOpen = false;

    this.dispatchEvent(new CustomEvent("omni-modal-dimissed"));
  }

  close() {
    this.isOpen = false;

    this.dispatchEvent(new CustomEvent("omni-modal-closed"));
  }

  render() {
    return html`
      <link
        rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css"
      />
      <style>
        .modal {
          display: ${this.isOpen ? "block" : "none"};
        }

        .isOpen {
          display: ${this.isOpen ? "block" : "none"};
          opacity: 0.4;
        }
      </style>
      <div class="modal-backdrop isOpen"></div>
      <div class="modal">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${this.title}</h5>
              <button type="button" class="close" @click="${this.dismiss}">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <slot></slot>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="${this.dismiss}">
                ${this.dismissString}
              </button>
              <button type="button" class="btn btn-primary" @click="${this.close}">
                ${this.acceptString}
              </button>
            </div>
          </div>
        </div>
      </div>
    `;
  }
}