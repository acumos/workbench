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

export default LitElementBase =>
  class extends LitElementBase {
    static get properties() {
      return {
        data: {
          type: Object
        }
      };
    }

    constructor() {
      super();
      this.data = {};
    }

    /**
     * Initializes the data
     *
     * @param {Object} data The form data
     */
    FormBuilderInitData(data) {
      Object.assign(this.data, data);
      Object.assign(this._FormBuilderData, data);
    }

    /**
     *
     * @param {Object} config Form configuration
     */
    FormBuilderConfig(config) {
      Object.assign(this._FormBuilderFormConfig, config);
    }

    /**
     * Gets a field model by looking it up in the actual data store
     *
     * @param {String} fieldName The name of the field to lookup
     * @param {String} defaultValue A default value if this field doesn't exist
     */
    FormBuilderGetFieldModel(fieldName, defaultValue) {
      return typeof this.data[fieldName] === "undefined"
        ? defaultValue
        : this.data[fieldName];
    }

    /**
     * Sets a value in the actual store
     *
     * @param {String} fieldName The name of the field to lookup
     * @param {String} value The new value of the field
     */
    FormBuilderSetFieldModel(fieldName, value) {
      this.data[fieldName] = value;
      let fieldvalidation = this._FormBuilderFormConfig.validation[fieldName];
    }

    /**
     * Reverts data model from internal data store to component data model
     */
    FormBuilderRevertData() {
      Object.assign(this.data, this._FormBuilderData);

      if (typeof this.onDataCommit === "function") {
        this.onDataCommit(this.data);
      }
    }
  };
