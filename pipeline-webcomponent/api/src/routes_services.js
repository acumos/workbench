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
		pipelinemSURL : properties.pipelinemSURL
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
				wikiUrl: wikiURL
			};
			res.send(res.configInfo);
		} catch (err) {
			console.error('Error while retrieving config details: '+ err);
		}
	});
	
	app.post('/api/pipeline/details', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		getSelectedNotebookDetails(userName, serviceUrl, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/pipeline/update', function (req, res){
		let userName = req.body.userName;
    let serviceUrl = req.body.url + uripath;
		let pipelinePayload = req.body.pipelinePayload;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		updateNotebookDetails(userName, serviceUrl, pipelineId, pipelinePayload, getLatestAuthToken(req, authToken)).then(function(result){
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
	
	var getSelectedNotebookDetails = function(userName, srvcUrl, pipelineId, authToken){
    	return new Promise(function(resolve, reject) {
        var options = {
					method : "GET",
					url : srvcUrl + userName + "/notebooks/" + pipelineId,
					headers : {
						'Content-Type' : 'application/json',
						'Authorization' : authToken,
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
	
	var updateNotebookDetails = function(userName, srvcUrl, pipelineId, pipelinePayload, authToken){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "PUT",
				url : srvcUrl + userName + "/notebooks/" + pipelineId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
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
