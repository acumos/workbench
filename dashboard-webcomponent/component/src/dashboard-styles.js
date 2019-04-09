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
  }

  .btn-outline-primary {
    color: #591887;
    border-color: #591887;
  }

  label {
    color: #4b4b4b;
  }

  .div-color {
    color: #591887;
  }

  .btnIcon {
    padding: 0.1rem 0.1rem;
    font-size: 0.075rem;
    line-height: 0.1;
    border-radius: 1.5rem;
    height: 40px;
    width: 40px;
  }
  
  .mwc-icon {
    color: white;
  }

  .card-text {
    color: black;
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
  
  .card-button {
    display: inline-flex;  
    line-height : 1.0 
  }
  
  .alertmessage {
	  background-color: white !important;
	  color: #dc3545;
	  border-color: #dc3545;
	  border: 1px solid; 
	}
	
	.alert-success{
	  color: #28a745 ;
	  border-color: #28a745;
  }
  
  .span-message {
    position: absolute;
    margin-top: 5px;
  }
`;