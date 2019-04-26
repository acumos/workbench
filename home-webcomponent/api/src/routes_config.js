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
	var userId='';
	var ms_wc_urls = {
		dashboardComponent : properties.dashboardComponent,
		projectComponent : properties.projectComponent,
		projectCatalogComponent : properties.projectCatalogComponent,
		notebookCatalogComponent : properties.notebookCatalogComponent,
		notebookComponent: properties.notebookComponent,
		pipelineCatalogComponent : properties.pipelineCatalogComponent,
		pipelineComponent: properties.pipelineComponent,
		portalFEURL: properties.portalFEURL,
	};
	
	var getLatestAuthToken = function (req, authToken){
		console.log("request cookie");
		console.log(req.cookies);
		console.log(req.cookies.authToken);
		let token = (req.cookies !== undefined && req.cookies.authToken !== undefined && req.cookies.authToken !== null ) ? 
				req.cookies.authToken: authToken ;
		console.log("return auth token:" +token);
		return token;
	}

	var getUserName = function (req){
		let userName = '';
		console.log("request cookie under getUsername method");
		console.log(req.cookies);
		console.log(req.cookies.userDetail);
		if(req.cookies !== undefined && req.cookies.userDetail !== undefined && req.cookies.userDetail !== null) {
			let userInfo = JSON.parse(req.cookies.userDetail);
			console.log(userInfo);
			if(userInfo.length === 3){
				userName = userInfo[2];
				userId = userInfo[1];
				console.log("inside if condition:"+userName+" userId: "+userId);
			}
		} 

		return userName;	
	}

	app.get('/config', function(req, res) {
		try {
			res.send(ms_wc_urls);
		} catch (err) {
			console.log(err);
		}
	});


	app.get('/session', function(req, res) {
	
		let userName = getUserName(req);
		let authToken = getLatestAuthToken(req,'');	
		console.log("inside session api:"+userName+" userId: "+userId);
		console.log("auth :"+authToken);
		try {
			res.configInfo = {
        userName:  userName,
				authToken: authToken,
			  	userId: userId
			};
			res.send(res.configInfo);
		} catch (err) {
			console.log(err);
		}
	});

};
