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
    background-color: #591887 !important;
    border-color: transparent !important;
  }

  .btn-outline-primary {
    color: #591887 !important;
    border-color: #591887 !important;
  }

  label {
    color: #4b4b4b !important;
  }

  .div-color {
    color: #591887 !important;
  }

  .btnIcon {
    padding: 0.1rem 0.1rem !important;
    font-size: 0.075rem !important;
    line-height: 0.1 !important;
    border-radius: 1.5rem !important;
    height: 40px !important;
    width: 40px !important;
  }
  
  .mwc-icon {
    color: white !important;
  }

  .card-text {
    color: black !important;
  }

  .card-shadow {
    transition: box-shadow 0.3s !important;
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
  
  .card-button {
    display: inline-flex !important;  
    line-height : 1.0 
  }
  
  .alertmessage {
	  background-color: white !important;
	  color: #dc3545 !important;
	  border-color: #dc3545 !important;
	  border: 1px solid !important; 
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