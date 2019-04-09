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
.nav-pills .nav-link {
  border-radius: 1px;
}

.nav-item a {
  color: black;
}
.nav-pills .nav-link.active {
  background-color: rgb(103, 28, 157);
}

.nav-pills .nav-link {
  background-color: white;
  border: 2px solid #dee2e6;
}

#divid {
  border: thin solid darkblue;
  padding: 25px;
  border-radius: 5px;
}

.btn-primary {
  background-color: #671C9D;
  border-color: transparent;
  height: 36px;
  border-radius: 0px;
}

.btn-primary:hover {
  background-color: #4e147a;
}

.btn-outline-primary {
  color: #671C9D;
  border-color: #671C9D;
  border-radius: 0px;
}

.btn-outline-primary:hover {
  background-color: #4e147a;
}

.btn-secondary {
  color: gray;
  border-color: gray;
  background-color: white;
  border-radius: 0px;
  height: 36px;
}

.pipeline-name {
  color: #671C9D;
  text-align: left;
}

label {
  color: #4b4b4b;
}

.textColor {
  color: #671C9D;
}

.btnIcon {
  padding: 0.1rem 0.1rem;
  font-size: 0.075rem;
  line-height: 0.1;
  height: 36px;
  width: 36px;
  border-radius: 0px;
}

.btnIconTop {
  padding: 0.2rem 0.2rem;
  font-size: 0.075rem;
  line-height: 0.1;
  height: 36px;
  width: 36px;
  border-radius: 0px;
}

.page-link.active {
  color: #671C9D;
  background-color: white;
}

.page-link.inactive {
  color: gray;
  background-color: #f1f1f1;
  cursor: not-allowed;
  opacity: 0.5;
  text-decoration: none;
  display: inline-block;
  pointer-events: none;
}

.page-link.active:hover {
  background-color: #671C9D;
  color: white;
}

.custom-select {
  height: 30px;
  font-size: 15px;
  border-radius: 0px;
}

h7 {
  color: gray;
}

.mwc-icon {
  color: white;
  padding-top: 4px;
}

.mwc-icon-gray {
  color: gray;
  padding-top: 2px;
  font-size: 28px;
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

.card {
  border-radius: 0px;
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

.card-body {
  padding-right: 1.00rem;
  line-height: 23px;
}

.toggle-a {
  width: 25px;
  heigth: 25px;
  color: white;
  background-color:lightgray;
  border-color: transparent;
  margin: 0px;
  padding: 0px;
}

.toggle-span {
  font-size: 42px;
  line-height: 0.1px;
  padding-top: 7px;
  height: 20px;
  display: block;
  padding-left: 5px;
  width: 14px;
  color: white;
}

.toggle-plus-span {
  font-weight: bold;
  font-size: 25px;
  padding-top: 8px;
}

.alertmessage {
  background-color: white !important;
  color: #dc3545;
  border-color: #dc3545;
  border: 1px solid; 
  padding: .35rem 1.00rem;
}

.alert-success{
  color: #28a745 ;
  border-color: #28a745;
}

.span-message {
  position: absolute;
  margin-top: 5px;
}

.w-100{
  width: 200px !important;
  border-radius: 0px;
  margin-left: 4px;
  height: 36px;
}

.form-control {
  border-radius: 0px;
}

.custom-select {
  height: 36px;
}

.help-icon{
  padding-top: 2px;
  color: black;
}
`;