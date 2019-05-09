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

.btn-primary:disabled {
    background-color: #a7a7a7 !important;
    border-color: #E0E0E0 !important;
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

label {
 color: #4b4b4b !important;
} 

.highlight {
background-color: #f1f1f1 !important;
font-weight: bold !important;
width: 300px !important;
}

.table-sm td, .table-sm th {
padding-left: 15px !important;
}

.table-sm td, .table-sm th {
    padding: 0.5rem !important;
}

.btnIcon {
    padding: 0.1rem 0.1rem !important;
    font-size: .075rem !important;
    line-height: 0.1 !important;
    height: 30px !important;
    width: 30px !important;
    border-radius: 0px !important;
}

.btnIconTop {
    padding: 0.1rem 0.1rem !important;
    font-size: .075rem !important;
    line-height: 0.1 !important;
    height: 36px !important;
    width: 36px !important;
    border-radius: 0px !important;
}

a.disabled {
pointer-events: none !important;
cursor: default !important;					
}

.card {
    border-radius: 0px !important;
}

.card-header {
background-color: white !important;
padding-bottom: 0rem !important;
}

.textColor {
color: #671C9D !important;
}

.mwc-icon-gray{
color: black !important;
padding-top: 3px !important;
}

.active-status {
color: green !important;
}

.inactive-status {
color: red !important;
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
`;