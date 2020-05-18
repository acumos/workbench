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
	
	const uripath = "/users/";
	const configENV = properties.ENVIRONMENT;
	const datasourceWikiURL = properties.datasourceWikiURL;
	const ms_urls = {
		datasourcemSURL : properties.datasourcemSURL,
		projectmSURL : properties.projectmSURL,
		pipelinemSURL : properties.pipelinemSURL,
		notebookmSURL : properties.notebookmSURL
	};
	
	var getUserName = function (req){
		let userName = '';
		if(req.cookies !== undefined && req.cookies.userDetail !== undefined && req.cookies.userDetail !== null) {
			let userInfo = JSON.parse(req.cookies.userDetail);
			if(userInfo.length === 3){
				userName = userInfo[2];
			}
		} 
		return userName;	
	}
	
	var getLatestAuthToken = function (req, authToken){
		let token = (req.cookies !== undefined && req.cookies.authToken !== undefined && req.cookies.authToken !== null ) ? 
				req.cookies.authToken: authToken ;
		return token;
	}

	app.get('/api/config', function(req, res) {
		try {
			let userName = getUserName(req);
			let authToken = getLatestAuthToken(req, '');

			res.configInfo = {
				configENV : configENV,
				msconfig : ms_urls,
				userName:  userName,
				authToken: authToken,
				datasourceWikiURL: datasourceWikiURL
			};
			res.send(res.configInfo);
		} catch (err) {
			console.error('Error while retrieving config details: '+ err);
		}
	});

	app.post('/api/datasets', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let category = req.body.category;
		let namespace = req.body.namespace;
		let textSearch = req.body.textSearch;
		let authToken = req.headers['auth'];
		getDatasetCatalog(userName, category, namespace, textSearch, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});

	app.post('/api/dataset/create', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let newDsReq = req.body.newDatasetDetails;
		let authToken = req.headers['auth'];
		createDataset(userName, serviceUrl, newDsReq, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/project/archive', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		archiveProject(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/project/restore', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		restoreProject(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.post('/api/dataset/delete', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let datasourceKey = req.body.datasourceKey;
		let authToken = req.headers['auth'];
		deleteDataset(userName, serviceUrl, datasourceKey, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/sharedProjects', function (req, res){
		let serviceUrl = req.body.url+uripath;
		let authToken = req.headers['auth'];
		let userName = req.body.userName;
		sharedProjectsForUser(serviceUrl, userName, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	var getDatasetCatalog = function(userName, category, namespace, textSearch, url, authToken) {
		return new Promise(function(resolve, reject) {
		
			var options = {
				method : "GET",
				url : url  + userName + "?" + "category="+ category + "&namespace="+ namespace + "&textSearch=" + textSearch,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};

			request.get(options, function(error, response) {

				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Datasources retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Datasources"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
		
	var createDataset = function(userName, srvcUrl, newDatasetReq, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl+ userName ,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: newDatasetReq,
				json: true
			};
				
			request.post(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Datasource created successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to create Datasource"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
		
	var deleteDataset = function(userName, srvcUrl, datasourceKey, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + "/users/" +  userName + "/datasource/" + datasourceKey,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.delete(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Datsource Deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Datasource"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var archiveProject = function(userName, srvcUrl, projectId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/actionType/A",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project archived successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to archive Project"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var restoreProject = function(userName, srvcUrl, projectId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/actionType/UA",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project restored successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to restore Project"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var sharedProjectsForUser = function(srvcUrl, userName, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/shared",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "List of shared projects for user retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve shared projects for user"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	/* Utility functions */
	var prepRespJsonAndLogit = function(httpResponse, responseData, message, error) {
		let r = {};
		let errorFlag = true;
		let code = 0;
		let status = '';
		try {
			if (!isNull(httpResponse)) {
				code = httpResponse.statusCode;
				if (code === 200 || code === 201) {
					errorFlag = false;
				} else if (code === 500){
					message = "Unknown server Error: " + message;
				} else {
					if (typeof responseData == 'string'){
						responseData = JSON.parse(responseData);
					}
					if(responseData.serviceStatus !== undefined && responseData.serviceStatus.statusMessage !== undefined){
						message = responseData.serviceStatus.statusMessage;
					}else{ 
						message = responseData;
					}
				}
			} else {
				message = "Server is not available." + error;
			}

			if(errorFlag) {
				status = 'Error';
			} else {
				status = 'Success';
			}

			r = {
				status: status,
				code : code,
				data : responseData,
				message : message
			};
			return r;
		} catch (e) {
			return {
				status: 'Error',
				code : null,
				message : 'NODE JS Server Internal Error : ' + e.message
			};
		}		
	};

	function serviceStartedLog(serviceName) {
		console.info(new Date() + " - " + serviceName + " service is started");
	}

	function serviceProcessingLog(serviceName) {
		console.info(new Date() + " - " + serviceName + " service is processing");
	}

	function serviceFinishedLog(serviceName) {
		console.info(new Date() + " - " + serviceName + " service is finished");
	}

	function timelog(str) {
		console.info(new Date() + " - " + str);
	}

	var base64Decryption = function(encryptedStr) {
		var credentials = [];
		if (encryptedStr !== undefined && encryptedStr !== "" && encryptedStr !== null) {
			encryptedStr = encryptedStr.replace("Basic ", "");
			credentials = Buffer.from(encryptedStr, 'base64').toString('ascii').split(':');
		}

		return credentials;
	};

	var isNull = function(obj) {
		return obj === undefined || obj === null;
	};

	function parseJSON(str) {
		try {
			var j = JSON.parse(str);
			return j;
		} catch (e) {
			return str;
		}
	}

};
