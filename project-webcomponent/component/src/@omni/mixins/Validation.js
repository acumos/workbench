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

import { each, isUndefined, concat } from "lodash-es";


export default LitElementBase =>
  class extends LitElementBase {
    static get properties() {
      return {
        validations: { type: Object }
      };
    }

    constructor() {
      super();
      const _this = this;

      this.$validations = {
        $form: {
          $valid: false
        },
        $invalid: {},
        validations: {},
        config: {},
        
        /**
         * 
         * @param {Object} options.validations Validations
         * @param {Object} options.config Validator configuration
         */
        init({ validations, config }) {
          this.validations = validations;
          this.config = config;
        },

        /**
         * Validates a form against any validations configured for the form
         * 
         * @param {String} formName The name of the form to validate
         */
        validate(formName) {
          this.$invalid = {}

          each(this.validations[formName], (validations, field) => {
            let model_value = _this.data[formName][field];

            if (!isUndefined(model_value)) {
              let failedValidations = [];

              each(validations, (validation_fn, validation) => {
                if (!validation_fn(model_value)) {
                  failedValidations.push(validation);
                }
              });

              if(failedValidations.length > 0){
                this.$invalid[field] = failedValidations
              }
            }
          });
        }
      };
    }
  };
