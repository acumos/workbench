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

label {
 color: #4b4b4b;
} 

.highlight {
background-color: #f1f1f1;
font-weight: bold;
width: 300px;
}

.table-sm td, .table-sm th {
padding-left: 15px;
}

.table-sm td, .table-sm th {
    padding: 0.5rem;
}

.btnIcon {
padding: 0.1rem 0.1rem;
font-size: .075rem;
line-height: 0.1;
border-radius: 0.3rem;
height: 30px;
}

.btn-secondary {
color: gray;
border-color: gray;
background-color: white;
}

a.disabled {
pointer-events: none;
cursor: default;					
}

.btn-primary.disabled {
color: #1b1e21;
background-color: #adb5bd;
border-color: #adb5bd;
}

.card-header {
background-color: white;
padding-bottom: 0rem;
}

.textColor {
color: #591887;
}

.mwc-icon-gray{
color: gray;
}

.active-status {
color: green
}

.inactive-status {
color: red
}

.toggle-a {
    width: 25px;
    heigth: 25px;
    color: white;
    background-color:lightgray;
    border-color: transparent;
}

.toggle-span {
    color: black;
}

`;