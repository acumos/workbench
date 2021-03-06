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
		ENVIRONMENT : "dev",
		projectmSURL : "http://localhost:9088/mlWorkbench/v1/project",
		notebookmSURL : "http://localhost:9089/mlWorkbench/v1/notebook",
		pipelinemSURL : "http://localhost:9090/mlWorkbench/v1/pipeline",
		modelmSURL : "http://localhost:9091/mlWorkbench/v1/modelservice",
    predictormSURL : "http://localhost:8085/mlWorkbench/v1/predictor",
		projectWikiURL : "https://wiki.acumos.org/display/TRAIN",
		notebookWikiURL : "https://wiki.acumos.org/display/TRAIN",
		pipelineWikiURL : "https://wiki.acumos.org/display/TRAIN",
		modelWikiURL : "https://wiki.acumos.org/display/TRAIN",
    predictorWikiURL: "https://wiki.acumos.org/display/TRAIN",
		portalBEURL : "http://localhost:8083",
		portalFEURL: "http://localhost:8085",
		pipelineFlag : "true",
		createTimeout: 60000,
		useExternalNotebook: "true",
		useExternalPipeline: "true"
	},
	deploy : {
		ENVIRONMENT : process.env.ENVIRONMENT,
		projectmSURL : process.env.projectmSURL,
		notebookmSURL : process.env.notebookmSURL,
		pipelinemSURL : process.env.pipelinemSURL,
		modelmSURL : process.env.modelmSURL,
		predictormSURL : process.env.predictormSURL,
		projectWikiURL : process.env.projectWikiURL,
		notebookWikiURL : process.env.notebookWikiURL,
		pipelineWikiURL : process.env.pipelineWikiURL,
		modelWikiURL : process.env.modelWikiURL,
		predictorWikiURL : process.env.predictorWikiURL,
		portalBEURL : process.env.portalBEURL,
		portalFEURL : process.env.portalFEURL,
		pipelineFlag : process.env.pipelineFlag,
		createTimeout: process.env.createTimeout,
		useExternalNotebook: process.env.useExternalNotebook,
		useExternalPipeline: process.env.useExternalPipeline
	}
};

exports.get = function get(env) {
	return config[env] || config.deploy;
};

