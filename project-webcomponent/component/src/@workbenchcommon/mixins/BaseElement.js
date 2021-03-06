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

import { RegisterComponent } from "../core";
import { each, camelCase } from "lodash-es";

export default LitElementBase =>
  class extends LitElementBase {
    static get properties() {
      return {
        $services: {
          type: Object
        }
      };
    }

    constructor() {
      super();

      let dependencies;

      dependencies = this.dependencies || [];

      each(dependencies, dependency => {
        RegisterComponent(dependency.name, dependency);
      });
    }
  };
