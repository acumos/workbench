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
		datasetCatalogComponent: properties.datasetCatalogComponent,
		datasetComponent: properties.datasetComponent,
		portalFEURL: properties.portalFEURL,
		menuItems : properties.menuItems
	};
	
	var getLatestAuthToken = function (req, authToken){
		let token = (req.cookies !== undefined && req.cookies.authToken !== undefined && req.cookies.authToken !== null ) ? 
				req.cookies.authToken: authToken ;
		return token;
	}

	var getUserName = function (req){
		let userName = '';
		if(req.cookies !== undefined && req.cookies.userDetail !== undefined && req.cookies.userDetail !== null) {
			let userInfo = JSON.parse(req.cookies.userDetail);
			if(userInfo.length === 3){
				userName = userInfo[2];
				userId = userInfo[1];
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
	
		// let userName = process.env.AUTH_USER || getUserName(req);
		// let authToken = process.env.AUTH_TOKEN || getLatestAuthToken(req, '');

		try {
			res.configInfo = {
        userName:  "admin",
				authToken: "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlQ291bnQiOjAsImNhdGFsb2dJZHMiOm51bGwsInJvbGVJZCI6IjEyMzQ1Njc4LWFiY2QtOTBhYi1jZGVmLTEyMzQ1Njc4OTBhYiIsIm5hbWUiOiJNTFAgU3lzdGVtIFVzZXIiLCJhY3RpdmUiOnRydWUsImNyZWF0ZWQiOnsiZXBvY2hTZWNvbmQiOjE1NDU0MDQzNjIsIm5hbm8iOjB9LCJtb2RpZmllZCI6eyJlcG9jaFNlY29uZCI6MTU3NzE5NjUyNywibmFubyI6MH19LHsicGVybWlzc2lvbkxpc3QiOm51bGwsInJvbGVDb3VudCI6MCwiY2F0YWxvZ0lkcyI6bnVsbCwicm9sZUlkIjoiMmExZDU0MWEtNTYzOC00ZjBjLWI1MGQtODFkY2VkNDA1NmQ2IiwibmFtZSI6IkxpY2Vuc2UgQWRtaW4iLCJhY3RpdmUiOnRydWUsImNyZWF0ZWQiOnsiZXBvY2hTZWNvbmQiOjE1ODYzNDkzNTgsIm5hbm8iOjB9LCJtb2RpZmllZCI6eyJlcG9jaFNlY29uZCI6MTU4NjM0OTM1OCwibmFubyI6MH19LHsicGVybWlzc2lvbkxpc3QiOm51bGwsInJvbGVDb3VudCI6MCwiY2F0YWxvZ0lkcyI6bnVsbCwicm9sZUlkIjoiN2RlOGUwNzQtNDBlZS00NjM2LThmOWQtYzcwNjljZjdhYWQ0IiwibmFtZSI6Ik1hc3RlciIsImFjdGl2ZSI6dHJ1ZSwiY3JlYXRlZCI6eyJlcG9jaFNlY29uZCI6MTU2Njk5ODAxOCwibmFubyI6MH0sIm1vZGlmaWVkIjp7ImVwb2NoU2Vjb25kIjoxNTY2OTk4MDE4LCJuYW5vIjowfX0seyJwZXJtaXNzaW9uTGlzdCI6bnVsbCwicm9sZUNvdW50IjowLCJjYXRhbG9nSWRzIjpudWxsLCJyb2xlSWQiOiI4YmU2YmZhZS1lNDMzLTQ0MzgtOWQ3MS1kZDU0Zjk1ODA4NzQiLCJuYW1lIjoiTWFya2V0cGxhY2UgVXNlciIsImFjdGl2ZSI6dHJ1ZSwiY3JlYXRlZCI6eyJlcG9jaFNlY29uZCI6MTU3NDkyNTE4NCwibmFubyI6MH0sIm1vZGlmaWVkIjp7ImVwb2NoU2Vjb25kIjoxNTg4NzU0NDk5LCJuYW5vIjowfX0seyJwZXJtaXNzaW9uTGlzdCI6bnVsbCwicm9sZUNvdW50IjowLCJjYXRhbG9nSWRzIjpudWxsLCJyb2xlSWQiOiI4Yzg1MGYwNy00MzUyLTRhZmQtOThiMS0wMGNiY2VjYTU2OWYiLCJuYW1lIjoiQWRtaW4iLCJhY3RpdmUiOnRydWUsImNyZWF0ZWQiOnsiZXBvY2hTZWNvbmQiOjE1NDU0MDQzNjIsIm5hbm8iOjB9LCJtb2RpZmllZCI6bnVsbH0seyJwZXJtaXNzaW9uTGlzdCI6bnVsbCwicm9sZUNvdW50IjowLCJjYXRhbG9nSWRzIjpudWxsLCJyb2xlSWQiOiI5ZDk2MTAxOC01NDY0LTViMGUtYTljMi0xMWRjZGZkYjY3YTAiLCJuYW1lIjoiUHVibGlzaGVyIiwiYWN0aXZlIjp0cnVlLCJjcmVhdGVkIjp7ImVwb2NoU2Vjb25kIjoxNTQ1NDA0MzYyLCJuYW5vIjowfSwibW9kaWZpZWQiOnsiZXBvY2hTZWNvbmQiOjE1ODI3MDg0NTAsIm5hbm8iOjB9fV0sImNyZWF0ZWQiOjE1ODg5MzczNTI1MDEsImV4cCI6MTU4OTAxNzM1MiwibWxwdXNlciI6eyJjcmVhdGVkIjp7ImVwb2NoU2Vjb25kIjoxNTQ1NDA0MzYyLCJuYW5vIjowfSwibW9kaWZpZWQiOnsiZXBvY2hTZWNvbmQiOjE1ODg5MzczNTIsIm5hbm8iOjcwOTY2MDAwfSwidXNlcklkIjoiMTIzNDU2NzgtYWJjZC05MGFiLWNkZWYtMTIzNDU2Nzg5MGFiIiwiZmlyc3ROYW1lIjoiQWN1bW9zIiwibWlkZGxlTmFtZSI6bnVsbCwibGFzdE5hbWUiOiJBZG1pbiIsIm9yZ05hbWUiOm51bGwsImVtYWlsIjoibm9yZXBseUBhY3Vtb3Mub3JnIiwibG9naW5OYW1lIjoiYWRtaW4iLCJsb2dpbkhhc2giOm51bGwsImxvZ2luUGFzc0V4cGlyZSI6bnVsbCwiYXV0aFRva2VuIjpudWxsLCJhY3RpdmUiOnRydWUsImxhc3RMb2dpbiI6eyJlcG9jaFNlY29uZCI6MTU4ODkzNzM1MiwibmFubyI6Njk3MTUwMDB9LCJsb2dpbkZhaWxDb3VudCI6bnVsbCwibG9naW5GYWlsRGF0ZSI6bnVsbCwicGljdHVyZSI6bnVsbCwiYXBpVG9rZW4iOiJmNTUzNjMzNGE1MDI0NGIyYWRhMGE2NjhkNjEyYjk1MyIsInZlcmlmeVRva2VuSGFzaCI6bnVsbCwidmVyaWZ5RXhwaXJhdGlvbiI6bnVsbCwidGFncyI6W119fQ.qK10XFR78-kkLYSqPPyTuYpf-B4iCxMpHudlYh9F7kmAdDFFHhEKSj-tLlW6BpLQnjLQorgYjZvh6Nstl5LzEQ",
			  	userId: "12345678-abcd-90ab-cdef-1234567890ab"
			};
			res.send(res.configInfo);
		} catch (err) {
			console.log(err);
		}
	});

};
