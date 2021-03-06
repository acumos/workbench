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
	const portalFEUrl = properties.portalFEURL;
	const portalBEUrl = properties.portalBEURL;
	let userId = '';

	const ms_urls = {
		projectmSURL : properties.projectmSURL,
		pipelinemSURL : properties.pipelinemSURL,
		notebookmSURL : properties.notebookmSURL,
		datasourcemSURL : properties.datasourcemSURL
	};
	
	const pipelineFlag = properties.pipelineFlag;
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
				portalFEUrl: portalFEUrl,
				portalBEUrl: portalBEUrl,
				userId : userId,
				pipelineFlag: pipelineFlag
			};
			res.send(res.configInfo);
		} catch (err) {
			reject("error");
		}
	});
	
	app.post('/api/project/count', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		getProjectsCount(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
			res.send(result);
		});
	});

	app.post('/api/pipeline/count', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		getPipelineCount(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
			res.send(result);
		});
	});

	app.post('/api/notebook/count', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		getNotebookCount(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
			res.send(result);
		});
	});

	app.post('/api/datasource/count', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		let category = req.body.category;
		let namespace = req.body.namespace;
		let textSearch = req.body.textSearch;
		getDatasourceCount(userName, category, namespace, textSearch, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
			res.send(result);
		});
	});

	app.post('/api/model/count', function(req, res) {
		let serviceUrl = req.body.url;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		let reqBody = req.body.reqBody;
		getModelCount(serviceUrl, getLatestAuthToken(req, authToken), reqBody).then(function(result) {
			res.send(result);
		});
	});

	app.post('/api/prmodel/count', function(req, res) {
		let serviceUrl = req.body.url;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		let reqBody = req.body.reqBody;
		getPRModelCount(serviceUrl, getLatestAuthToken(req, authToken), reqBody).then(function(result) {
			res.send(result);
		});
	});

	app.post('/api/project/sharedProjects', function (req, res){
		let serviceUrl = req.body.url+uripath;
		let authToken = req.headers['auth'];
		let userName = req.body.userName;
		sharedProjectsCount(serviceUrl, userName, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	var getProjectsCount = function(userName, url, authToken) {
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : url + userName + "/projects/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let projectCount = JSON.parse(response.body).length;
					resolve(prepRespJsonAndLogit(response, projectCount, "Projects count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Projects"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var sharedProjectsCount = function(srvcUrl, userName, authToken){
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
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let projectCount = JSON.parse(response.body).length;
					resolve(prepRespJsonAndLogit(response, projectCount, "Shared Projects count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve shared projects for user"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getNotebookCount = function(userName, url, authToken) {
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
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let notebookCount = JSON.parse(response.body).length;
					resolve(prepRespJsonAndLogit(response, notebookCount, "Notebook count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Notebook count"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getDatasourceCount = function(userName, category, namespace, textSearch, url, authToken) {
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
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let datasourceCount = JSON.parse(response.body).length;
					resolve(prepRespJsonAndLogit(response, datasourceCount, "Datasource count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Datasource count"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getPipelineCount = function(userName, url, authToken) {
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : url + userName + "/pipelines/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let pipelineCount = JSON.parse(response.body).length;
					resolve(prepRespJsonAndLogit(response, pipelineCount, "Pipeline count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Pipeline count"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getModelCount = function(url, authToken, reqBody) {
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : url + "/portal/solutions",
				body : JSON.stringify(reqBody),
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			
			request.post(options, function(error, response) {
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let responseData = JSON.parse(response.body);
					let modelCount = 0;
					if(responseData.response_body !== null)
						modelCount = responseData.response_body.totalElements;
					resolve(prepRespJsonAndLogit(response, modelCount, "Models count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Models count"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getPRModelCount = function(url, authToken, reqBody) {
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : url + "/user/solutions",
				body : JSON.stringify(reqBody),
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			
			request.post(options, function(error, response) {
				if (!error && response.statusCode == 200 && response.body !== undefined) {
					let prResponseData = JSON.parse(response.body);
					let prModelCount = 0;
					if(prResponseData.response_body !== null)
						prModelCount = prResponseData.response_body.totalElements;
					resolve(prepRespJsonAndLogit(response, prModelCount, "Private Models count retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Private Models count"));
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
