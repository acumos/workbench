/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import { LitElement, html } from 'lit-element';


class PipelineLitElement extends LitElement {
  render() {
    return html`
    	<style> 
				@import url('https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
        </style>
		<div class="content-wrapper" style="width: 1800px;">
			<div class="row">
				<div class="col-md-9 py-3">
					<div class="card mb-124  box-shadow">
						<div class="card-body">
							Under construction..           
						</div>
					</div>
				</div>
			</div>
		</div>
    `;
  }
}
customElements.define('pipeline-element', PipelineLitElement);
