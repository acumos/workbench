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

import { get, set, isUndefined } from "lodash-es";

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
      const _this = this;

      // Cache data so it can be reverted
      let data_cache = {}

      this.$data = {
        /**
         * Takes a snapshot of a data property and caches it.
         * 
         * @param {string} path Path to the property to take a snapshot of
         */
        snapshot(path) {
          data_cache[path] = JSON.stringify(get(_this, `data.${path}`));
        },

        /**
         * Reverts data to the initialized values
         * 
         * @param {string} path Path of the property to restore
         */
        revert(path) {
          set(_this, `data.${path}`, JSON.parse(data_cache[path]));
        },

        /**
         * Sets a field value in the data store
         * 
         * @param {string} path Path to the field property in the data store
         * @param {string | event} e string or event
         * @param {boolean} skipValidationCheck Skip the dirty check for field changes. Useful for initializing data for first time
         */
        set(path, e, skipValidationCheck) {
          let oldValue, newValue;

          oldValue = get(_this, `data.${path}`);
          newValue = get(e, 'target.value', e);

          //  Sets the $dirty flag for this field if it was changed
          if (!skipValidationCheck && (oldValue !== newValue)) {
            if (!isUndefined(_this.$validations)) {
              _this.$validations.setDirty(path, true)
            } else {
              console.warn(`Can't set the $dirty flag for ${path}. Validation mixin is missing on component.`)
            }
          }

          if(skipValidationCheck){
            _this.$validations.setDirty(path, true)
            _this.$validations.setTouched(path, true)
            _this.$validations.setValid(path, true)
          }
          
          set(_this, `data.${path}`, newValue);

          if (!isUndefined(get(e, 'stopPropagation', undefined))) {
            e.stopPropagation();
          }

          _this.requestUpdate();
        }
      };
    }
  };
