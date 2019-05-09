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
  border-radius: 1px !important;
}

.nav-item a {
  color: black !important;
}
.nav-pills .nav-link.active {
  background-color: rgb(103, 28, 157) !important;
}

.nav-pills .nav-link {
  background-color: white !important;
  border: 2px solid #dee2e6 !important;
}

#divid {
  border: thin solid darkblue !important;
  padding: 25px !important;
  border-radius: 5px !important;
}

.btn-primary {
  background-color: #671C9D !important;
  border-color: transparent !important;
  height: 36px !important;
  border-radius: 0px !important;
}

.btn-primary:hover {
  background-color: #4e147a !important;
}

.btn-outline-primary {
  color: #671C9D !important;
  border-color: #671C9D !important;
  border-radius: 0px !important;
  height: 36px !important;
}

.btn-outline-primary:hover {
  background-color: #4e147a !important;
}

.btn-secondary {
  color: gray !important;
  border-color: gray !important;
  background-color: white !important;
  border-radius: 0px !important;
}

.project-name {
  color: #671C9D !important;
  text-align: left !important;
}

label {
  color: #4b4b4b !important;
}

.textColor {
  color: #671C9D !important;
}

.btnIcon {
  padding: 0.1rem 0.1rem !important;
  font-size: 0.075rem !important;
  line-height: 0.1 !important;
  height: 36px !important;
  width: 36px !important;
  border-radius: 0px !important;
}

.btnIconTop {
  padding: 0.2rem 0.2rem !important;
  font-size: 0.075rem !important;
  line-height: 0.1 !important;
  height: 36px !important;
  width: 36px !important;
  border-radius: 0px !important;
}

.page-link.active {
  color: #671C9D !important;
  background-color: white !important;
}

.page-link.inactive {
  color: gray !important;
  background-color: #f1f1f1 !important;
  cursor: not-allowed !important;
  opacity: 0.5 !important;
  text-decoration: none !important;
  display: inline-block !important;
  pointer-events: none !important;
}

.page-link.active:hover {
  background-color: #671C9D !important;
  color: white !important;
}

.custom-select {
  height: 30px !important;
  font-size: 15px !important;
  border-radius: 0px !important;
}

h7 {
  color: gray !important;
}

.mwc-icon {
  color: white !important;
  padding-top: 4px !important;
}

.mwc-icon-gray {
  color: gray !important;
  padding-top: 2px !important;
  font-size: 28px !important;
}

hr {
  margin-top: 5px !important;
}

.card-header {
  background-color: white !important;
  padding-bottom: 0rem !important;
}

.gray-light {
  background-color: #d3d3d34d !important;
}

.card-shadow {
  transition: box-shadow 0.3s !important;
}

.card {
  border-radius: 0px !important;
}

.card-shadow:hover {
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.15) !important;
  transform: scale(1.02) !important;
  transition: all 0.2s ease-in-out !important;
}

.card.card-link a {
  color: inherit !important;
  text-decoration: none !important;
}

.active-status {
  color: green !important;
}

.inactive-status {
  color: red !important;
}

.error-status {
  color: red !important;
}

.success-status {
  color: green !important;
}

.card-body {
  padding-right: 1.00rem !important;
  line-height: 23px !important;
}

.toggle-a {
  width: 25px !important;
  heigth: 25px !important;
  color: white !important;
  background-color:lightgray !important;
  border-color: transparent !important;
  margin: 0px !important;
  padding: 0px !important;
}

.toggle-span {
  font-size: 42px !important;
  line-height: 0.1px !important;
  padding-top: 7px !important;
  height: 20px !important;
  display: block !important;
  padding-left: 5px !important;
  width: 14px !important;
  color: white !important;
}

.toggle-plus-span {
  font-weight: bold !important;
  font-size: 25px !important;
  padding-top: 8px !important;
}

.alertmessage {
  background-color: white !important !important;
  color: #dc3545 !important;
  border-color: #dc3545 !important;
  border: 1px solid !important; 
  padding: .35rem 1.00rem !important;
}

.alert-success{
  color: #28a745  !important;
  border-color: #28a745 !important;
}

.span-message {
  position: absolute !important;
  margin-top: 5px !important;
}

.w-100{
  width: 200px !important !important;
  border-radius: 0px !important;
  margin-left: 4px !important;
  height: 36px !important;
}

.form-control {
  border-radius: 0px !important;
}

.custom-select {
  height: 36px !important;
}

.help-icon{
  padding-top: 2px !important;
  color: black !important;
}
`;