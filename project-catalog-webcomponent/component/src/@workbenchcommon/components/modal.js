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

import { LitElement, html, css, unsafeCSS } from "lit-element";

const primaryColor = unsafeCSS`#671C9D`;
const secondaryColor = unsafeCSS`#FFFFFF`;

export class OmniModal extends LitElement {
  static get name() {
    return "omni-modal";
  }

  static get styles() {
    return css`
      .btn-primary {
        background-color: ${primaryColor};
      }
      .btn-secondary {
        background-color: ${secondaryColor};
        color: #000000;
      }
      .modal-dialog {
        max-width: 700px;
      }
      .modal-header {
        background-color: ${primaryColor};
        color: #ffffff;
      }
      .close {
        color: #ffffff;
        text-shadow: none;
        font-size: 2rem;
        opacity: 1;
      }

      .close:hover {
        color: #ffffff;
      }
    `;
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
      dismissString: { type: String, attribute: "dismiss-string" },
      variant: { type: String },
      canClose: {
        type: Boolean,
        converter: {
          fromAttribute: value => {
            if (value === "undefined" || value === "null" || value === "false") {
              return false;
            }
            return true;
          }
        }
      }
    };
  }
  constructor() {
    super();
    this.closeString = "Ok";
    this.dismissString = "Cancel";
    this.isOpen = false;
    this.canClose = false;
    console.log('canclpse: ' + this.canClose);
  }

  dismiss() {
    this.dispatchEvent(new CustomEvent("omni-modal-dimissed"));
  }

  close() {
    this.dispatchEvent(new CustomEvent("omni-modal-closed"));
  }

  getVariant(variant) {
    switch (variant) {
      case "warning":
        return "#591887";
      default:
        return "rgb(103, 28, 157);";
    }
  }

  render() {
    return html`
      <link
        rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css"
      />
      <style>
        .display-none {
          display: none !important;
        }
        .modal {
          display: block !important;
          border-radius: 0px !important;
        }
        .isOpen {
          display: block !important;
          opacity: 0.4 !important;
        }
        .variant {
          background-color: ${this.getVariant(this.variant)} !important;
        }

        .modal-header {
          border-radius: 0px !important;
        }
        
        .modal-content {
          border-radius: 0px !important;
        }

        .modal-footer {
          background-color: #f1f1f1 !important;
        }

        .btn {
          border-radius: 0px !important;
        }
        
        .btn-secondary {
          border-color: #E0E0E0 !important;
          height: 36px !important;
        }

        .btn-primary {
          background-color: #671C9D !important;
          border-color: transparent !important;
          height: 36px !important;
          border-radius: 0px !important;
          color:white !important;
        }

        .btn-primary.disabled {
          background-color: #a7a7a7 !important;
          border-color: #E0E0E0 !important;
        }

        .btn-primary.disabled:hover {
          background-color: #a7a7a7 !important;
          border-color: #E0E0E0 !important;
        }

        .btn-primary:hover {
          background-color: #4e147a !important;
        }

      </style>
      <div class="modal-backdrop ${this.isOpen ? 'isOpen' : 'display-none'}"></div>
      <div class="${this.isOpen ? 'modal' : 'display-none'}">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header variant">
              <h5 class="modal-title">${this.title}</h5>
              <button type="button" class="close" @click="${this.dismiss}">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <slot></slot>
            </div>
            <div class="modal-footer d-block">
              <button type="button" class="btn btn-secondary float-left" @mousedown="${this.dismiss}">
                ${this.dismissString}
              </button>
              ${this.canClose
        ? html`
                  <button type="button" class="btn btn-primary float-right" @click="${this.close}" >
                    ${this.closeString}
                  </button>
                `:
        html`
                  <button disbaled class="btn btn-primary float-right disabled">${this.closeString}</button>
              `}
            </div>
          </div>
        </div>
      </div>
    `;
  }
}
