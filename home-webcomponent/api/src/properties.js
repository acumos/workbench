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

var config = {
	local : {
		dashboardComponent : "http://localhost:9083/src/dashboard-element.js",
		projectComponent: "http://localhost:9084/src/project-element.js",
		projectCatalogComponent: "http://localhost:9085/src/project-catalog-element.js"
	},
	deploy : {
		dashboardComponent : process.env.dashboardComponent,
		projectComponent : process.env.projectComponent,
		projectCatalogComponent: process.env.projectCatalogComponent
	}
};

exports.get = function get(env) {
	return config[env] || config.deploy;
};

