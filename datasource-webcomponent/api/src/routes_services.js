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
var request = require('request');
module.exports = function(app) {
	
	const uripath = "/users/";
	const configENV = properties.ENVIRONMENT;
	const datasourceWikiURL = properties.datasourceWikiURL;
	
	const ms_urls = {
		datasourcemSURL : properties.datasourcemSURL 
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
				datasourceWikiURL: datasourceWikiURL,
			};
			res.send(res.configInfo);
		} catch (err) {
			console.error('Error while retrieving config details: '+ err);
		}
	});
	
	app.post('/api/dataset/details', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let datasourceKey = req.body.datasourceKey;
		let authToken = req.headers['auth'];
    	getSelectedDatasetDetails(userName, serviceUrl, datasourceKey, getLatestAuthToken(req, authToken)).then(function(result){
    		res.send(result);
    	});
	});

	app.post('/api/notebook/launch', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let noteBookId = req.body.noteBookId;
		let authToken = req.headers['auth'];
		launchNotebook(userName, serviceUrl, noteBookId, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});

	app.put('/api/dataset/update', function (req, res){
		let userName = req.body.userName;
    	let serviceUrl = req.body.url + uripath;
		let datasetPayload = req.body.datasetPayload;
		let datasourceKey = req.body.datasourceKey;
		let authToken = req.headers['auth'];
		updateDatasetDetails(userName, serviceUrl, datasetPayload, datasourceKey, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/notebook/archive', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let noteBookId = req.body.noteBookId;
		let authToken = req.headers['auth'];
		archiveNotebook(userName, serviceUrl, noteBookId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/notebook/restore', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let noteBookId = req.body.noteBookId;
		let authToken = req.headers['auth'];
		restoreNotebook(userName, serviceUrl, noteBookId, getLatestAuthToken(req, authToken)).then(function(result){
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
	
	var getSelectedDatasetDetails = function(userName, srvcUrl, datasourceKey, authToken){
    	return new Promise(function(resolve, reject) {
        var options = {
					method : "GET",
					url : srvcUrl + userName + "/datasource/" + datasourceKey,
					headers : {
						'Content-Type' : 'application/json',
						'Authorization' : authToken,
					}
        };
			
        request.get(options, function(error, response) {
					if (!error && response.statusCode == 200) {
						resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Datasource details retrieved successfully"));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Datasource"));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
        });
      });
    };
	
		var launchNotebook = function(userName, srvcUrl, noteBookId, authToken){
			return new Promise(function(resolve, reject) {
				var options = {
					method : "GET",
					url : srvcUrl + userName + "/notebooks/" + noteBookId + "/launch",
					headers : {
						'Content-Type' : 'application/json',
						'Authorization' : authToken,
					},
				};
				request.get(options, function(error, response) {
					if (!error && response.statusCode == 200) {
						resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook launched successfully"));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to launch Notebook"));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
				});
			});
		};

		var updateDatasetDetails = function(userName, srvcUrl, datasetPayload, datasourceKey, authToken){
			return new Promise(function(resolve, reject) {
        var options = {
					method : "PUT",
					url : srvcUrl + userName + "/datasources/" + datasourceKey,
					headers : {
						'Content-Type' : 'application/json',
						'Authorization' : authToken,
					},
					body: datasetPayload,
					json: true
        };

        request.put(options, function(error, response) {
					if (!error && response.statusCode == 200) {
						resolve(prepRespJsonAndLogit(response, response.body, "Datasource details updated successfully"));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Datasource"));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
        });
      });
    };
	
    var archiveNotebook = function(userName, srvcUrl, noteBookId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/notebooks/" + noteBookId + "/A",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook archived successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to archive Notebook"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var restoreNotebook = function(userName, srvcUrl, noteBookId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/notebooks/" + noteBookId + "/UA",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebook unarchived successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to unarchive Notebook"));
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
				url : srvcUrl + userName + "/datasource/" + datasourceKey,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.delete(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Datasource Deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Datasource"));
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
