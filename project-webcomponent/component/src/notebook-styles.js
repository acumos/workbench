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
.btn-primary {
  background-color: #671C9D;
  border-color: transparent;
  height: 36px;
  border-radius: 0px;
}

.btn-primary:hover {
  background-color: #4e147a;
}

.btn-primary:disabled {
  background-color: #a7a7a7;
  border-color: #E0E0E0;
}

.btn-outline-primary {
  color: #671C9D;
  border-color: #671C9D;
  border-radius: 0px;
  height: 36px;
}

.btn-outline-primary:hover {
  background-color: #4e147a;
}

.btn-secondary {
  color: gray;
  border-color: gray;
  background-color: white;
  border-radius: 0px;
}
  
#cssTable td {
  vertical-align: middle;
  padding-left: 15px;
  padding-top: 10px;
}

#cssTable th {
  vertical-align: middle;
  padding-left: 15px;
  padding-top: 10px;			
}

.btn-secondary-button {
  color: #671C9D;
  border-color: #671C9D;
  background-color: white;
  border-radius: 0px;
}

.btnIcon {
  padding: .10rem .10rem;
  font-size: .075rem;
  line-height: 0.1;
  height: 36px;
  width: 36px;
  border-radius: 0px;
}

.page-link {
  color: #671C9D;
}

.custom-select {
  height: 36px;
  font-size: 15px;
  border-radius: 0px;
}

h7 {
  color: gray;
}

hr {
  margin-top: 5px;
}

.textColor {
  color: #671C9D;
}

.card-header {
  background-color: white;
  padding-bottom: 0rem;
 }
 
 .card {
  border-radius: 0px;
}

.gray-color{
  color: gray;
}

.white-color{
  color: white;
}

.table-bordered{
  border: 0px solid #dee2e6;
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

.active-status {
color: green
}

.inactive-status {
color: red
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

.mwc-icon-secondary{
  color: black;
  padding-top: 3px;
}

.mwc-icon-primary{
  padding-top: 3px;
}

.form-control {
  border-radius: 0px;
}

`;