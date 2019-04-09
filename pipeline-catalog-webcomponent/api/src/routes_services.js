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
	const wikiURL = properties.wikiURL;
	const ms_urls = {
		projectmSURL : properties.projectmSURL,
		pipelinemSURL : properties.pipelinemSURL,
		notebookmSURL : properties.notebookmSURL
	};
	
	var getLatestAuthToken = function (req, authToken){
		let token = (req.cookies !== undefined && req.cookies.authToken !== undefined && req.cookies.authToken !== null ) ? 
				req.cookies.authToken: authToken ;
		return token;
	}

	app.get('/api/config', function(req, res) {
		try {
			let userName = (req.cookies !== undefined && req.cookies.userDetail !== undefined && req.cookies.userDetail !== null 
					&& req.cookies.userDetail.length === 3 ) ? req.cookies.userDetail[2]: '' ;
			let authToken = getLatestAuthToken(req, '');
			
			res.configInfo = {
				configENV : configENV,
				msconfig : ms_urls,
				userName:  userName,
				authToken: authToken,
				wikiUrl: wikiURL
			};
			res.send(res.configInfo);
		} catch (err) {
			console.error('Error while retrieving config details: '+ err);
		}
	});

	app.post('/api/pipelines', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		getNotebooksCatalog(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});

	app.post('/api/pipeline/create', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let newNotebookReq = req.body.newNotebookDetails;
		let authToken = req.headers['auth'];
		createNotebook(userName, serviceUrl, newNotebookReq, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/pipeline/archive', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		archiveNotebook(userName, serviceUrl, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/pipeline/restore', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		restoreNotebook(userName, serviceUrl, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.delete('/api/pipeline/delete', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		deleteNotebook(userName, serviceUrl, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	var getNotebooksCatalog = function(userName, url, authToken) {
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : url + userName + "/notebooks/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebooks retrieved successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Notebooks."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var createNotebook = function(userName, srvcUrl, newNotebookReq, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + userName + "/notebooks/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: newNotebookReq,
				json: true
			};
				
		request.post(options, function(error, response, body) {
				if (!error && response.statusCode == 201) {
					resolve(prepRespJsonAndLogit(response, response.body, "Notebook created successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to create Notebook."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
		});
		});
	};
		
	var deleteNotebook = function(userName, srvcUrl, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/notebooks/" + pipelineId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.delete(options, function(error, response, body) {
				console.log(response.body);
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Notebook Deleted successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Notebook."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var archiveNotebook = function(userName, srvcUrl, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/notebooks/" + pipelineId + "/A",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook archived successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to archive Notebook."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var restoreNotebook = function(userName, srvcUrl, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/notebooks/" + pipelineId + "/UA",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook restored successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to restore Notebook."));
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
