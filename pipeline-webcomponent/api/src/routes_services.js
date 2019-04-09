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
	
	let uripath = "/users/";
	
	var getJWTToken = function(req){
		return req.cookies.auth_token;
	}

	var configENV = properties.ENVIRONMENT;
	var userName = properties.userName;
	var wikiURL = properties.wikiURL;
	var ms_urls = {
		pipelinemSURL : properties.pipelinemSURL
	};
	
	app.get('/api/config', function(req, res) {
		try {
			let user_name = (req.cookies.userDetail !== undefined && req.cookies.userDetail !== null && req.cookies.userDetail.length > 0) ? 
				req.cookies.userDetail[0]: userName;
			console.info('user name: '+ user_name);
			res.configInfo = {
				configENV : configENV,
				msconfig : ms_urls,
				user_name:  user_name,
				wikiURL: wikiURL
			};
			res.send(res.configInfo);
		} catch (err) {
			reject("error");
		}
	});
	
	app.post('/api/pipeline/details', function (req, res){
		let user_name = req.body.user_name;
    	let serviceUrl = req.body.url + uripath;
    	let pipelineId = req.body.pipelineId;
    	getSelectedNotebookDetails(user_name, serviceUrl, pipelineId, getJWTToken(req)).then(function(result){
    		res.send(result);
    	});
	});

	app.put('/api/pipeline/update', function (req, res){
		let user_name = req.body.user_name;
    	let serviceUrl = req.body.url + uripath;
		let pipelinePayload = req.body.pipelinePayload;
		let pipelineId = req.body.pipelineId;
		updateNotebookDetails(user_name, serviceUrl, pipelineId, pipelinePayload, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/pipeline/archive', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let pipelineId = req.body.pipelineId;
		archiveNotebook(user_name, serviceUrl, pipelineId, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/pipeline/restore', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let pipelineId = req.body.pipelineId;
		restoreNotebook(user_name, serviceUrl, pipelineId, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	app.delete('/api/pipeline/delete', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let user_name = req.body.user_name;
		let pipelineId = req.body.pipelineId;
		deleteNotebook(user_name, serviceUrl, pipelineId, getJWTToken(req)).then(function(result){
			res.send(result);
		});
	});
	
	var getSelectedNotebookDetails = function(user_name, srvcUrl, pipelineId, jwtToken){
    	return new Promise(function(resolve, reject) {
        var options = {
					method : "GET",
					url : srvcUrl + user_name + "/notebooks/" + pipelineId,
					headers : {
						'Content-Type' : 'application/json',
						'Authorization' : jwtToken,
					}
        };
				console.log(options.url);
				
        request.get(options, function(error, response) {
					if (!error && response.statusCode == 200) {
						
						resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook details retrieved successfully."));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Notebook."));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
        });
      });
    };
	
	var updateNotebookDetails = function(user_name, srvcUrl, pipelineId, pipelinePayload, jwtToken){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "PUT",
				url : srvcUrl + user_name + "/notebooks/" + pipelineId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
				},
				body: pipelinePayload,
				json: true
            };

            request.put(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Notebook details updated successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Notebook."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
            });
       });
    };
	
    var archiveNotebook = function(user_name, srvcUrl, pipelineId, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + user_name + "/notebooks/" + pipelineId + "/A",
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
	
	var restoreNotebook = function(user_name, srvcUrl, pipelineId, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + user_name + "/notebooks/" + pipelineId + "/UA",
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
	
	var deleteNotebook = function(user_name, srvcUrl, pipelineId, jwtToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + user_name + "/notebooks/" + pipelineId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : jwtToken,
				},
			};
			request.delete(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook Deleted successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Notebook."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
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
