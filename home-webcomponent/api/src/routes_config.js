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

var env = process.env.ENVIRONMENT || "local";

var properties = require('./properties.js').get(env);
var https = require("https");
var request = require('request');

module.exports = function(app) {

	var ms_wc_urls = {
		dashboardComponent : properties.dashboardComponent,
		projectComponent : properties.projectComponent,
		projectCatalogComponent : properties.projectCatalogComponent,
		notebookCatalogComponent : properties.notebookCatalogComponent,
		notebookComponent: properties.notebookComponent,
		pipelineCatalogComponent : properties.pipelineCatalogComponent,
		pipelineComponent: properties.pipelineComponent
	};
	
	app.get('/config', function(req, res) {
		try {
			res.send(ms_wc_urls);
		} catch (err) {
			console.log(err);
		}
	});

	
};
