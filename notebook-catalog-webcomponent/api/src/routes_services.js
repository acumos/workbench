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

	var getJWTToken = function(req){
		return req.cookies.auth_token;
	}

	var configENV = properties.ENVIRONMENT;
	var ms_urls = {
		projectmSURL : properties.projectmSURL,
		pipelinemSURL : properties.pipelinemSURL,
		notebookmSURL : properties.notebookmSURL
	};
	
	app.get('/api/config', function(req, res) {
		try {
			let user_name = (req.cookies.userDetail !== undefined && req.cookies.userDetail !== null && req.cookies.userDetail.length > 0) ? 
				req.cookies.userDetail[0]: "techmdev";
			console.info('user name: '+ user_name);
			res.configInfo = {
				configENV : configENV,
				msconfig : ms_urls,
				user_name:  user_name
			};
			res.send(res.configInfo);
		} catch (err) {
			reject("error");
		}
	});

	app.post('/api/notebooks', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		getNotebooksCatalog(user_name, serviceUrl, getJWTToken(req)).then(function(result) {
					res.send(result);
		});
	});

	app.post('/api/notebook/create', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let newNotebookReq = req.body.newNotebookDetails;
		createNotebook(user_name, serviceUrl, newNotebookReq, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/notebook/archive', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let notebookId = req.body.notebookId;
		archiveNotebook(user_name, serviceUrl, notebookId, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/notebook/restore', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let notebookId = req.body.notebookId;
		restoreNotebook(user_name, serviceUrl, notebookId, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	app.delete('/api/notebook/delete', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let notebookId = req.body.notebookId;
		deleteNotebook(user_name, serviceUrl, notebookId, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	var getNotebooksCatalog = function(user_name, url, jwtToken) {
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : url + user_name + "/notebooks/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
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
	
	var createNotebook = function(user_name, srvcUrl, newNotebookReq, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + user_name + "/notebooks/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
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
		
	var deleteNotebook = function(user_name, srvcUrl, notebookId, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + user_name + "/notebooks/" + notebookId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
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
	
	var archiveNotebook = function(user_name, srvcUrl, notebookId, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + user_name + "/notebooks/" + notebookId + "/A",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
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

	var restoreNotebook = function(user_name, srvcUrl, notebookId, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + user_name + "/notebooks/" + notebookId + "/UA",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
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
					if (typeof responseData == 'string'){
						responseData = JSON.parse(responseData);
					}
					message = "Unknown server Error: " + message;
				} else {
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

	var isEmptyStr = function(str) {
		return str === undefined || str === null || str === "" || str === " ";
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
