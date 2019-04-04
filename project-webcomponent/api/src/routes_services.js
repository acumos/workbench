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
var request = require('request');

module.exports = function(app) {
	let uripath = "/users/";
	let auth = "";
	let serviceUrl = "";

	app.post('/api/project/details', function (req, res){
    	auth = req.headers.auth;
    	serviceUrl = req.body.url + uripath;
    	let projectId = req.body.projectId;
    	getSelectedProjectDetails(auth, serviceUrl, projectId).then(function(result){
    		res.send(result);
    	});
	});

	app.put('/api/project/update', function (req, res){
    	auth = req.headers.auth;
    	serviceUrl = req.body.url + uripath;
		let projectPayload = req.body.projectPayload;
		let projectId = req.body.projectId;
    	updateProjectDetails(auth, serviceUrl, projectId, projectPayload).then(function(result){
    		res.send(result);
    	});
	});
	
	var getSelectedProjectDetails = function(auth, srvcUrl, projectId){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "GET",
				url : srvcUrl + auth + "/projects/" + projectId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : auth,
				}
            };
			
			
            request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project details retrieved successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Project."));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
            });
       });
    };
	
	var updateProjectDetails = function(auth, srvcUrl, projectId, projectPayload){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "PUT",
				url : srvcUrl + auth + "/projects/" + projectId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : auth,
				},
				body: projectPayload,
				json: true
            };
			console.log(srvcUrl);
			console.log(projectId);

            request.put(options, function(error, response) {
				console.log(error);
				console.log(response);

				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project details updated successfully."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Project."));
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
					if (typeof responseData == 'string'){
						responseData = JSON.parse(responseData);
					}
					message = "Unknown server Error: " + message;
				} else {
					message = responseData.serviceStatus.statusMessage;
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
