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

import {css} from 'lit-element/lit-element.js';
export const style = css`
#divid {
  border: thin solid darkblue;
  padding: 25px;
  border-radius: 5px;
}

.btn-primary {
  background-color: #591887;
  border-color: transparent;
  height: 35px;
}

.btn-outline-primary {
  color: #591887;
  border-color: #591887;
}

.btn-secondary {
  color: gray;
  border-color: gray;
  background-color: white;
}

.name {
  color: #591887;
  text-align: center;
}

label {
  color: #4b4b4b;
}

.textColor {
  color: #591887;
}

.btnIcon {
  padding: 0.1rem 0.1rem;
  font-size: 0.075rem;
  line-height: 0.1;
  border-radius: 0.3rem;
  height: 30px;
}

.btnIconTop {
  padding: 0.2rem 0.2rem;
  font-size: 0.075rem;
  line-height: 0.1;
  border-radius: 0.3rem;
  height: 35px;
}

.page-link {
  color: #591887;
}

.pagination li:hover {
  background-color: #591887;
  color: #591887;
}

.custom-select {
  height: 30px;
  font-size: 15px;
}

h7 {
  color: gray;
}

.mwc-icon {
  color: white;
}

.mwc-icon-gray {
  color: gray;
}

hr {
  margin-top: 5px;
}

.card-header {
  background-color: white;
  padding-bottom: 0rem;
}

.gray-light {
  background-color: #d3d3d34d;
}

.card-shadow {
  transition: box-shadow 0.3s;
}

.card-shadow:hover {
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.15);
  transform: scale(1.02);
  transition: all 0.2s ease-in-out;
}

.card.card-link a {
  color: inherit;
  text-decoration: none;
}

.active-status {
  color: green;
}

.inactive-status {
  color: red;
}

.error-status {
  color: red;
}

.success-status {
  color: green;
}
`;