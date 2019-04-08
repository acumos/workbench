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

import { each, set, get, isUndefined, isEmpty } from "lodash-es";
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
        $fields: {},
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
         * @param {String} path The path of the field to validate
         */
        validate(path) {
          let fieldValidations, fieldModel, failedValidations;

          fieldValidations = get(this.validations, path, {});
          fieldModel = get(_this.data, path, '');
          failedValidations = [];

          each(fieldValidations, (validationFn, validationName) => {
            if (!validationFn(fieldModel)) {
               failedValidations.push(validationName);
            }
          })

          // Set field validation state
          set(this.$fields, `${path}.$touch`, true);
          set(this.$fields, `${path}.$dirty`, true);
          set(this.$fields, `${path}.$invalid`, failedValidations.length > 0);
          set(this.$fields, `${path}.failedValidations`, failedValidations);
        },

        /**
         * Checks the give path and returns the valiation state
         * @param {String} path The path of the field to check validation
         */

        getValidationErrors(path) {
          return get(this.$fields, `${path}.failedValidations`, []);
        },

        get $valid() {          
          return this.countDeepTruthy('$invalid', this.$fields) === 0;
        },

        get $dirty() {
          return this.countDeepTruthy('$dirty', this.$fields) !== 0;
        },

        /**
         * Does a DFS and counts how many times a property is truthy
         * 
         * @param {String} property The property to search for
         * @param {Object} root The starting object
         * @returns {Number} Number of times {property} is truthy
         */
        countDeepTruthy(property, root) {
          function searchDeep(root, acc) {
            acc = isUndefined(acc) ? 0 : acc;
            let invalid = 0;
            if (!isUndefined(root[property]) && root[property]) {
              return 1;
            }

            each(root, (value) => {
              acc += searchDeep(value, invalid);
            })

            return acc
          }

          return searchDeep(root, 0);
        }
      };
    }
  };
