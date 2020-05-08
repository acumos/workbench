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
		dashboardComponent : "http://localhost:9083",
		projectComponent: "http://localhost:9084",
		projectCatalogComponent: "http://localhost:9085",
		notebookComponent: "http://localhost:9086",
		notebookCatalogComponent: "http://localhost:9087",
		pipelineComponent: "http://localhost:9091",
		pipelineCatalogComponent: "http://localhost:9092",
		datasourceCatalogComponent: "http://localhost:9093",
		datasourceComponent: "http://localhost:9094",
		portalFEURL: "http://localhost:8085",
		menuItems: '[{"title": "Dashboard", "icon": "fa fa-tachometer-alt", "link": "/pages/dashboard", "home": true}, {"title": "Projects", "icon": "fa fa-project-diagram", "link": "/pages/projects/catalog"}, {"title": "Notebooks", "icon": "fas fa-book-open", "link": "/pages/notebook/catalog"}, {"title": "Data Pipelines", "icon": "fa fa-code-branch", "link": "/pages/pipeline/catalog"}, {"title": "Datasource", "icon": "fa fa-database", "link": "/pages/datasource/catalog"}]'
	},
	deploy : {
		dashboardComponent : process.env.dashboardComponent,
		projectComponent : process.env.projectComponent,
		projectCatalogComponent: process.env.projectCatalogComponent,
		notebookComponent : process.env.notebookComponent,
		notebookCatalogComponent: process.env.notebookCatalogComponent,
		pipelineComponent : process.env.pipelineComponent,
		pipelineCatalogComponent: process.env.pipelineCatalogComponent,
		datasourceCatalogComponent: process.env.datasourceCatalogComponent,
		datasourceComponent: process.env.datasourceComponent,
		portalFEURL: process.env.portalFEURL,
		menuItems: process.env.menuItems
	}
};

exports.get = function get(env) {
	return config[env] || config.deploy;
};

