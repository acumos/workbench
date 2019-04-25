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

import { each, set, get, isUndefined, isObject, isFunction, isNull, uniq } from "lodash-es";

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
         * @param {boolean} skipTouched Skip touching of fields.
         */
        validate(path, skipTouched) {
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
          if (!skipTouched) {
            set(this.$fields, `${path}.$touched`, true);
          }
          set(this.$fields, `${path}.$valid`, failedValidations.length === 0);
          set(this.$fields, `${path}.$invalid`, failedValidations.length > 0);
          set(this.$fields, `${path}.failedValidations`, failedValidations);
        },

        /**
         * Resets the validation state
         * @param {String} path The path of the form to reset the validation on
         */
        resetValidation(path) {
          this.setDeepProperty('$dirty', false, get(this.$fields, path));
          this.setDeepProperty('$touched', false, get(this.$fields, path));
          this.setDeepProperty('$invalid', false, get(this.$fields, path));
          this.setDeepProperty('failedValidations', [], get(this.$fields, path));
        },

        /**
         * Checks the give path and returns the valiation state
         * @param {String} path The path of the field to check validation
         */

        getValidationErrors(path) {
          return get(this.$fields, `${path}.failedValidations`, []);
        },


        get $touched() {
          return this.countDeepTruthy('$touched', this.$fields) !== 0;
        },

        $valid(path) {
          let touchedAndValidFields;

          // Check to see if the fields defined as requiring validation are touched and valid
          var validationFields = uniq(this.searchTypeDeep(this.validations[path], { unWrappingFn: (node) => node.prev.path }));

          touchedAndValidFields = 0;
          each(validationFields, fieldPath => {
            if (get(this.$fields, `${path}.${fieldPath}.$valid`, false) && get(this.$fields, `${path}.${fieldPath}.$touched`, false)) {
              touchedAndValidFields++;
            }
          })

          return touchedAndValidFields === validationFields.length;
        },

        get $invalid() {
          return this.countDeepTruthy('$invalid', this.$fields) !== 0;
        },

        get $dirty() {
          return this.countDeepTruthy('$dirty', this.$fields) !== 0;
        },

        setDirty(path, state) {
          set(this.$fields, `${path}.$dirty`, state);
        },

        setTouched(path, state) {
          set(this.$fields, `${path}.$touched`, state);
        },

        setValid(path, state) {
          set(this.$fields, `${path}.$valid`, state);
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
            if (!isObject(root)) {
              return 0;
            }
            if (!isUndefined(root[property]) && root[property]) {
              return 1;
            }

            each(root, (value) => {
              acc += searchDeep(value, invalid);
            })

            return acc
          }

          return searchDeep(root, 0);
        },

        setDeepProperty(property, newValue, root) {
          each(root, (value, key) => {
            if (key === property) {
              set(root, key, newValue);
            } else {
              if (isObject(root[key])) {
                this.setDeepProperty(property, newValue, root[key])
              }
            }
          })
        },

        /**
         * Deep searches an object tree for a type
         * 
         * @param {object} node Root node to search   
         * @param {object} config.unWrappingFn Function to use to unwrap node, by default the whole node is returned. Takes paramter of `node`
         * @param {object} config.searchFn The function to test each node. If this returns true, the node is included in the result. Takes parameter of `node`
         * 
         * @returns {array} An array of results
         */
        searchTypeDeep(node, config, _acc) {
          _acc = _acc || [];
          config = config || {};
          node = !isUndefined(node.data) ? node : { data: node, prev: null, path: "" };

          config.unWrappingFn = config.unWrappingFn || (node => node);
          config.searchFn = config.searchFn || (node => isFunction(node.data));

          if (config.searchFn(node)) {
            return [..._acc, config.unWrappingFn(node)];
          }

          each(node.data, (value, key) => {
            _acc = [
              ...this.searchTypeDeep(
                { data: value, prev: node, path: isNull(node.prev) ? key : node.path + "." + key },
                config,
                _acc
              )
            ];
          });

          return _acc;
        }
      };
    }
  };
