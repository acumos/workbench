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
	
	/*
	 * Here all APIs will be written to integrate with back end mS.
	 */
	var urlAdd = "/users/";
	var auth = "";
	var serviceUrl = "";

    app.post('/api/projects', function(req, res) {
           auth = req.headers.auth;
           serviceUrl = req.body.url+urlAdd;
           getDataSourcesCatalog(auth, serviceUrl).then(function(result) {
                  res.send(result);
           });
    });

    app.post('/api/createProject', function (req, res){
    	auth = req.headers.auth;
    	serviceUrl = req.body.url+urlAdd;
    	var body = req.body.createDetails;
    	postCreateProject(auth, serviceUrl, body).then(function(result){
    		res.send(result);
    	});
    });
    
    app.delete('/api/deleteProject', function (req, res){
    	auth = req.headers.auth;
    	serviceUrl = req.body.url+urlAdd;
    	var body = req.body.projectId;
    	deleteProject(auth, serviceUrl, body).then(function(result){
    		res.send(result);
    	});
    });
    
    app.post('/api/getProjectDetails', function (req, res){
    	auth = req.headers.auth;
    	serviceUrl = req.body.url+urlAdd;
    	var body = req.body.projectId;
    	getSelectedProjectDetails(auth, serviceUrl, body).then(function(result){
    		res.send(result);
    	});
    });
    
    var getDataSourcesCatalog = function(auth, url) {
           return new Promise(function(resolve, reject) {
                  var options = {
                        method : "GET",
                        url : url + "/"+auth+"/projects/",
                        headers : {
                               'Content-Type' : 'application/json',
                               'Authorization' : auth,
                        },
                  };

                  request.get(options, function(error, response, body) {
                        try {
                               if (!error && response.statusCode == 200) {
                                      resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Projects retrieved successfully."));
                               } else if (!error) {
                                      resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Projects."));
                               } else {
                                      resolve(prepRespJsonAndLogit(null, null, null, error));
                               }
                        } catch (e) {
                               resolve(prepRespJsonAndLogit(null, null, null, e));
                        }
                  });
           });
    };

    var postCreateProject = function(auth, srvcUrl, createBody){
    	return new Promise(function(resolve, reject) {
            var options = {
                    method : "POST",
                    url : srvcUrl+auth+"/projects/",
                    headers : {
                           'Content-Type' : 'application/json',
                           'Authorization' : auth,
                    },
                    body: createBody,
                    json: true
              };

              request.post(options, function(error, response, body) {
                    try {
                           if (!error && response.statusCode == 201) {
                                  resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project created successfully."));
                           } else if (!error) {
                                  resolve(prepRespJsonAndLogit(response, response.body, "Unable to create Project."));
                           } else {
                                  resolve(prepRespJsonAndLogit(null, null, null, error));
                           }
                    } catch (e) {
                           resolve(prepRespJsonAndLogit(null, null, null, e));
                    }
              });
       });
    };
    
    var deleteProject = function(auth, srvcUrl, projectId){
    	return new Promise(function(resolve, reject) {
            var options = {
                    method : "DELETE",
                    url : srvcUrl+auth+"/projects/"+projectId,
                    headers : {
                           'Content-Type' : 'application/json',
                           'Authorization' : auth,
                    },
              };

              request.delete(options, function(error, response, body) {
                    try {
                           if (!error && response.statusCode == 200) {
                                  resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project Deleted successfully."));
                           } else if (!error) {
                                  resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Project."));
                           } else {
                                  resolve(prepRespJsonAndLogit(null, null, null, error));
                           }
                    } catch (e) {
                           resolve(prepRespJsonAndLogit(null, null, null, e));
                    }
              });
       });
    };
    
    var getSelectedProjectDetails = function(auth, srvcUrl, projectId){
    	return new Promise(function(resolve, reject) {
            var options = {
                    method : "GET",
                    url : srvcUrl+auth+"/projects/"+projectId,
                    headers : {
                           'Content-Type' : 'application/json',
                           'Authorization' : auth,
                    },
              };

              request.get(options, function(error, response, body) {
                    try {
                           if (!error && response.statusCode == 200) {
                                  resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project details retrieved successfully."));
                           } else if (!error) {
                                  resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Project."));
                           } else {
                                  resolve(prepRespJsonAndLogit(null, null, null, error));
                           }
                    } catch (e) {
                           resolve(prepRespJsonAndLogit(null, null, null, e));
                    }
              });
       });
    };
    
	/* Utility functions */
	var prepRespJsonAndLogit = function(httpResponse, responseData, message, error) {
		var r = {};
		try {
			if (!isNull(httpResponse)) {
				var code = httpResponse.statusCode;
				var body = httpResponse.body;
				
				if (code == 200) {
					console.info(message);
				} else if (code == 401) {
					message = "Unauthorized access! " + message;
					console.error(message);
				} else if (code == 500) {
					
					if (typeof responseData == 'string'){
						responseData = JSON.parse(responseData);
					}
					console.info ('responseData');
					console.info (responseData);
					console.error(message + " Server response code: " + code + " and response body : " + body);
					message = responseData.message;
				} else if (code != 200) {
					console.error(message + " Server response code: " + code + " and response body : " + body);
				}

				r = {
					code : code,
					data : responseData,
					message : message
				};
			} else {
				r = {
					code : null,
					data : null,
					message : "NODE JS Server Error : " + error.message
				};
				console.error(r.message);
			}
		} catch (e) {
			return {
				code : null,
				data : null,
				message : "NODE JS Server Internal Error : " + e.message
			};
		}

		return r;
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
