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
		userName : "techmdev",
		projectmSURL : "http://localhost:9088/mlWorkbench/v1/project",
		notebookmSURL : "http://localhost:9089/mlWorkbench/v1/notebook",
		pipelinemSURL : "http://localhost:9083/mlWorkbench/v1/pipeline",
		wikiURL : "https://wiki.acumos.org/display/TRAIN",
	},
	deploy : {
		ENVIRONMENT : process.env.ENVIRONMENT,
		userName : process.env.userName,
		projectmSURL : process.env.projectmSURL,
		notebookmSURL : process.env.notebookmSURL,
		pipelinemSURL : process.env.pipelinemSURL,
		wikiURL : process.env.wikiURL,
	}
};

exports.get = function get(env) {
	return config[env] || config.deploy;
};

