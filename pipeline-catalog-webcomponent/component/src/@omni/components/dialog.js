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

import { LitElement, html, css } from "lit-element";
import { BaseElementMixin } from "../mixins";
import { OmniModal } from "./modal";

export class OmniDialog extends BaseElementMixin(LitElement) {
  static get name() {
    return "omni-dialog";
  }

  get dependencies() {
    return [OmniModal];
  }

  static get properties() {
    return {
      type: { type: String },
      title: { type: String },
      closeString: { type: String, attribute: "close-string" },
      dismissString: { type: String, attribute: "dismiss-string" },
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
      }
    };
  }

  constructor() {
    super();
    this.closeString = "Ok";
    this.dismissString = "Cancel";
  }

  dialogClosed() {
    console.log("closed");
    this.dispatchEvent(new CustomEvent("omni-dialog-closed"));
  }

  dialogDismissed() {
    console.log("dismissed");
    this.dispatchEvent(new CustomEvent("omni-dialog-dimissed"));
  }

  render() {
    return html`
      <omni-modal
        title="${this.title}"
        is-open="${this.isOpen}" canClose="true"
        @omni-modal-dimissed="${this.dialogDismissed}"
        @omni-modal-closed="${this.dialogClosed}"
        close-string="${this.closeString}" dismiss-string="${this.dismissString}"
        variant="${this.type}"
      >
        <slot></slot>
      </omni-modal>
    `;
  }
}
