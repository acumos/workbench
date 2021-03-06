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
	const createTimeout = properties.createTimeout;
	const useExternalNotebook = properties.useExternalNotebook;
	const useExternalPipeline = properties.useExternalPipeline;
	const wiki_urls = {
		projectWikiURL: properties.projectWikiURL,
		notebookWikiURL: properties.notebookWikiURL,
		pipelineWikiURL: properties.pipelineWikiURL,
		modelWikiURL: properties.modelWikiURL,
		predictorWikiURL: properties.predictorWikiURL
	};
	const ms_urls = {
		projectmSURL : properties.projectmSURL,
		notebookmSURL : properties.notebookmSURL,
		pipelinemSURL : properties.pipelinemSURL,
		modelmSURL : properties.modelmSURL,
		predictormSURL : properties.predictormSURL
	};
	const pipelineFlag = properties.pipelineFlag;
	const portalBEUrl = properties.portalBEURL;
	const portalFEUrl = properties.portalFEURL;

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
			let userName = process.env.AUTH_USER || getUserName(req);
			let authToken = process.env.AUTH_TOKEN || getLatestAuthToken(req, '');
			
			res.configInfo = {
				configENV : configENV,
				msconfig : ms_urls,
				userName:  userName,
				authToken: authToken,
				wikiConfig: wiki_urls,
				portalBEUrl: portalBEUrl,
				portalFEUrl: portalFEUrl,
				pipelineFlag: pipelineFlag,
				createTimeout: createTimeout,
				useExternalNotebook: useExternalNotebook,
				useExternalPipeline: useExternalPipeline
			};
			res.send(res.configInfo);
		} catch (err) {
			console.error('Error while retrieving config details: '+ err);
		}
	});
	
	app.post('/api/project/details', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		getSelectedProjectDetails(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/project/update', function (req, res){
		let userName = req.body.userName;
    let serviceUrl = req.body.url + uripath;
		let projectPayload = req.body.projectPayload;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		updateProjectDetails(userName, serviceUrl, projectId, projectPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/project/archive', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
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
	
	app.post('/api/project/delete', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		deleteProject(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.post('/api/project/notebooksList', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		getSelectedProjectNotebookDetails(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/project/associateNotebook', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let notebookPayload = req.body.notebookPayload;
		let projectId = req.body.projectId;
		let notebookId = req.body.notebookId;
		let authToken = req.headers['auth'];
		associateNotebookToProject(userName, serviceUrl, projectId, notebookId, notebookPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/project/archiveNotebook', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let notebookId = req.body.notebookId;
		let authToken = req.headers['auth'];
		archiveNotebook(userName, serviceUrl, projectId, notebookId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/project/restoreNotebook', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let notebookId = req.body.notebookId;
		let authToken = req.headers['auth'];
		restoreNotebook(userName, serviceUrl, projectId, notebookId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/deleteNotebookAssociation', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let notebookId = req.body.notebookId;
		let authToken = req.headers['auth'];
		deleteNotebookAssociation(userName, serviceUrl, projectId, notebookId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.post('/api/project/createNotebook', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let newNotebookReq = req.body.newNotebookDetails;
		let authToken = req.headers['auth'];
		createNotebook(userName, serviceUrl, projectId, newNotebookReq, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/notebooks', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		getNotebooksList(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});
	
	app.post('/api/notebook/launch', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let notebookId = req.body.notebookId;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		launchNotebook(userName, serviceUrl, projectId, notebookId, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});

	app.put('/api/notebook/update', function (req, res){
		let userName = req.body.userName;
    let serviceUrl = req.body.url + uripath;
		let notebookPayload = req.body.notebookPayload;
		let notebookId = req.body.notebookId;
		let authToken = req.headers['auth'];
		updateNotebookDetails(userName, serviceUrl, notebookId, notebookPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.post('/api/project/pipelinesList', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		getSelectedProjectPipelineDetails(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/project/associatePipeline', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let pipelinePayload = req.body.pipelinePayload;
		let projectId = req.body.projectId;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		associatePipelineToProject(userName, serviceUrl, projectId, pipelineId, pipelinePayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/project/archivePipeline', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		archivePipeline(userName, serviceUrl, projectId, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.put('/api/project/restorePipeline', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		restorePipeline(userName, serviceUrl, projectId, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.post('/api/pipeline/deleteAssociation', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		deletePipelineAssociation(userName, serviceUrl, projectId, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/createPipeline', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let newPipelineReq = req.body.newPipelineDetails;
		let authToken = req.headers['auth'];
		createPipeline(userName, serviceUrl, projectId, newPipelineReq, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/createPipelineStatus', function (req, res){
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		createPipelineStatus(userName, serviceUrl, projectId, pipelineId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/pipelines', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let authToken = req.headers['auth'];
		getPipelinesList(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});
	
	app.post('/api/pipeline/launch', function(req, res) {
		let serviceUrl = req.body.url + uripath;
		let userName = req.body.userName;
		let pipelineId = req.body.pipelineId;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		launchPipeline(userName, serviceUrl, projectId, pipelineId, getLatestAuthToken(req, authToken)).then(function(result) {
					res.send(result);
		});
	});

	app.put('/api/pipeline/update', function (req, res){
		let userName = req.body.userName;
    let serviceUrl = req.body.url + uripath;
		let pipelinePayload = req.body.pipelinePayload;
		let pipelineId = req.body.pipelineId;
		let authToken = req.headers['auth'];
		updatePipelineDetails(userName, serviceUrl, pipelineId, pipelinePayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});
	
	app.post('/api/models/getProjectModels', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		getSelectedProjectModels(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/models/associateModel', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let modelId = req.body.modelId;
		let authToken = req.headers['auth'];
		let modelPayload = req.body.modelPayload;
		associateModelProject(userName, serviceUrl, projectId, modelId, modelPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/models/updateAssociateModel', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let modelId = req.body.modelId;
		let authToken = req.headers['auth'];
		let modelPayload = req.body.modelPayload;
		updateModelAssociation(userName, serviceUrl, projectId, modelId, modelPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/models/deleteAssociateModel', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let modelId = req.body.modelId;
		let authToken = req.headers['auth'];
		let modelPayload = req.body.modelPayload;
		deleteModelAssociation(userName, serviceUrl, projectId, modelId, modelPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/models/getAllModels', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let authToken = req.headers['auth'];
		fetchAllModels(userName, serviceUrl,getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/models/category', function (req, res){
		let serviceUrl = req.body.url;
		let authToken = req.headers['auth'];
		getModelCategories(serviceUrl, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/models/catalog', function (req, res){
		let serviceUrl = req.body.url;
		let authToken = req.headers['auth'];
		let reqBody = req.body.request_body;
		getModelCatalogs(serviceUrl, reqBody, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/addUser', function (req, res){
		let serviceUrl = req.body.url+uripath;
		let authToken = req.headers['auth'];
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let users = req.body.users;
		shareProjectToUsers(serviceUrl, userName, projectId, users, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/removeUser', function (req, res){
		let serviceUrl = req.body.url+uripath;
		let authToken = req.headers['auth'];
		let userName = req.body.userName;
		let projectId = req.body.projectId;
		let user = req.body.user;
		deleteSharedUserFromProject(serviceUrl, userName, projectId, user, getLatestAuthToken(req, authToken)).then(function(result){
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

	app.post('/api/users/userList', function (req, res){
		let serviceUrl = req.body.url;
		let authToken = req.headers['auth'];
		getUsersList(serviceUrl, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	 app.post('/api/project/getProjectPredictors', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		getSelectedProjectPredictors(userName, serviceUrl, projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	 });

	 app.post('/api/project/users/predictors', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url;
		let authToken = req.headers['auth'];
		let deployPayload = req.body.deployPayload;
		let projectId = req.body.projectId;
		deployModelKub(userName, serviceUrl,deployPayload,projectId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	 app.post('/api/project/associatePredictor', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let projectId = req.body.projectId;
		let authToken = req.headers['auth'];
		let predictorPayload = req.body.predictorPayload;
		associatePredictorProject(userName, serviceUrl, projectId, predictorPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.put('/api/project/updateAssociatePredictor', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let associationId = req.body.associationId;
		let predictorId = req.body.predictorId;
		let authToken = req.headers['auth'];
		let predictorPayload = req.body.predictorPayload;
		updatePredictorAssociation(userName, serviceUrl, predictorId, associationId, predictorPayload, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/deleteAssociatePredictor', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let associationId = req.body.associationId;
		let authToken = req.headers['auth'];
		deletePredictorAssociation(userName, serviceUrl, associationId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/predictor/modelSelected', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url + uripath;
		let authToken = req.headers['auth'];
		let modelId = req.body.modelId;
		let modelVersion = req.body.modelVersion;
		getPredictorForModel(userName, serviceUrl, modelId, modelVersion, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/users', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url;
		let authToken = req.headers['auth'];
		getSelectedClusters(userName, serviceUrl, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});

	app.post('/api/project/users/predictorsId', function (req, res){
		let userName = req.body.userName;
		let serviceUrl = req.body.url;
		let authToken = req.headers['auth'];
		let predictorId = req.body.predictorId;
		getDeploymentStatus(userName, serviceUrl,  predictorId, getLatestAuthToken(req, authToken)).then(function(result){
			res.send(result);
		});
	});


	var getSelectedProjectDetails = function(userName, srvcUrl, projectId, authToken){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/" + projectId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
            };
            
        request.get(options, function(error, response) {
					if (!error && response.statusCode == 200) {
						
						resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project details retrieved successfully"));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Project"));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
        });
      });
    };
	
	var updateProjectDetails = function(userName, srvcUrl, projectId, projectPayload, authToken){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: projectPayload,
				json: true
            };

            request.put(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Project details updated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Project"));
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
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project unarchived successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to unarchive Project"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var deleteProject = function(userName, srvcUrl, projectId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/projects/" + projectId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.delete(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Project Deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Project"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var getSelectedProjectNotebookDetails = function(userName, srvcUrl, projectId, authToken){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
            };
			
        request.get(options, function(error, response) {
					if (!error && response.statusCode == 200) {						
						resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebooks for the project retrieved successfully"));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Notebooks"));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
        });
      });
    };
	
    var createNotebook = function(userName, srvcUrl, projectId, newNotebookReq, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: newNotebookReq,
				json: true
			};
				
		request.post(options, function(error, response, body) {
				if (!error && response.statusCode == 201) {
					resolve(prepRespJsonAndLogit(response, response.body, "Notebook created successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to create Notebook"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
		});
		});
	};
	
	var associateNotebookToProject = function(userName, srvcUrl, projectId, notebookId, notebookPayload, authToken){
    	return new Promise(function(resolve, reject) {
            var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks/" + notebookId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: notebookPayload,
				json: true
            };

            request.put(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Notebook associated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to associate Notebook"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
            });
       });
    };
	
    var archiveNotebook = function(userName, srvcUrl, projectId, notebookId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks/" + notebookId + "/A",
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
	
	var restoreNotebook = function(userName, srvcUrl, projectId, notebookId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks/" + notebookId + "/UA",
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

	var deleteNotebookAssociation = function(userName, srvcUrl, projectId, noteBookId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks/" + noteBookId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.delete(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Notebook Association Deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Notebook Association"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var getNotebooksList = function(userName, url, authToken) {
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
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Notebooks retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Notebooks"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var launchNotebook = function(userName, srvcUrl, projectId, noteBookId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/" + projectId + "/notebooks/" + noteBookId + "/launch",
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

	var updateNotebookDetails = function(userName, srvcUrl, noteBookId, notebookPayload, authToken){
		return new Promise(function(resolve, reject) {
	    var options = {
					method : "PUT",
					url : srvcUrl + userName + "/notebooks/" + noteBookId,
					headers : {
						'Content-Type' : 'application/json',
						'Authorization' : authToken,
					},
					body: notebookPayload,
					json: true
	    };
	
	    request.put(options, function(error, response) {
					if (!error && response.statusCode == 200) {
						resolve(prepRespJsonAndLogit(response, response.body, "Notebook details updated successfully"));
					} else if (!error) {
						resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Notebook"));
					} else {
						resolve(prepRespJsonAndLogit(null, null, null, error));
					}
	    });
	  });
	};
	
	var getSelectedProjectPipelineDetails = function(userName, srvcUrl, projectId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
			method : "GET",
			url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/",
			headers : {
				'Content-Type' : 'application/json',
				'Authorization' : authToken,
			}
					};
		
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {						
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Data Pipelines for the project retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Data Pipelines"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var createPipeline = function(userName, srvcUrl, projectId, newPipelineReq, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + userName + "/projects/" + projectId + "/pipelines",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: newPipelineReq,
				json: true
			};
				
		request.post(options, function(error, response, body) {
				if (!error && response.statusCode == 201 || response.statusCode == 202) {
					resolve(prepRespJsonAndLogit(response, response.body, "Data Pipeline created successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to create Data Pipeline"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
		});
		});
	};

	var createPipelineStatus = function(userName, srvcUrl, projectId, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/" + pipelineId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Data Pipeline creation status fetched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to get Data Pipeline creation status"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var associatePipelineToProject = function(userName, srvcUrl, projectId, pipelineId, pipelinePayload, authToken){
		return new Promise(function(resolve, reject) {
					var options = {
			method : "PUT",
			url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/" + pipelineId,
			headers : {
				'Content-Type' : 'application/json',
				'Authorization' : authToken,
			},
			body: pipelinePayload,
			json: true
					};

					request.put(options, function(error, response) {
			if (!error && response.statusCode == 200) {
				resolve(prepRespJsonAndLogit(response, response.body, "Data Pipeline associated successfully"));
			} else if (!error) {
				resolve(prepRespJsonAndLogit(response, response.body, "Unable to associate Data Pipeline"));
			} else {
				resolve(prepRespJsonAndLogit(null, null, null, error));
			}
					});
		 });
	};

	var archivePipeline = function(userName, srvcUrl, projectId, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/" + pipelineId + "/A",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Data Pipeline archived successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to archive Data Pipeline"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var restorePipeline = function(userName, srvcUrl, projectId, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/" + pipelineId + "/UA",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.put(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Data Pipeline unarchived successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to unarchive Data Pipeline"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var deletePipelineAssociation = function(userName, srvcUrl, projectId, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/" + pipelineId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			request.delete(options, function(error, response, body) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Data Pipeline Association Deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Data Pipeline association"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var getPipelinesList = function(userName, url, authToken) {
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
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Data Pipelines retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Data Pipelines"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var launchPipeline = function(userName, srvcUrl, projectId, pipelineId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/" + projectId + "/pipelines/" + pipelineId + "/launch",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
			};
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Data Pipeline launched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to launch Data Pipeline"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var updatePipelineDetails = function(userName, srvcUrl, pipelineId, pipelinePayload, authToken){
		return new Promise(function(resolve, reject) {
					var options = {
			method : "PUT",
			url : srvcUrl + userName + "/pipelines/" + pipelineId,
			headers : {
				'Content-Type' : 'application/json',
				'Authorization' : authToken,
			},
			body: pipelinePayload,
			json: true
					};

			request.put(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Data Pipeline details updated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Data Pipeline"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var getSelectedProjectModels = function(userName, srvcUrl, projectId, authToken){
		return new Promise(function(resolve, reject){
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/projects/" + projectId + "/models/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {						
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Models for the project retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Models"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});

		});
	};

	var getSelectedClusters = function(userName, srvcUrl, authToken){	
		return new Promise(function(resolve, reject){
			var options = {
				method : "GET",
				url : srvcUrl + '/users/' + userName + '/siteConfig',
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {						
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Model deployed on cluster successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to deploy model"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				
				}
			});

		});
	};

	var associateModelProject = function(userName, srvcUrl, projectId, modelId, modelPayload, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + userName + "/projects/" + projectId + "/models/" + modelId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: modelPayload,
				json: true
			};

			request.post(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Model associated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to associate Model"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var updateModelAssociation = function(userName, srvcUrl, projectId, modelId, modelPayload, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/projects/" + projectId + "/models/" + modelId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: modelPayload,
				json: true
			};

			request.put(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Model association updated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Model association"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var deleteModelAssociation = function(userName, srvcUrl, projectId, modelId, modelPayload, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/projects/" + projectId + "/models/" + modelId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: modelPayload,
				json: true
			};

			request.delete(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Model association deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Model association"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var fetchAllModels = function(userName, srvcUrl, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/models/",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				json: true
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Models fetched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to fetch Models"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};
	
	var getModelCategories = function(srvcUrl, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + "/filter/modeltype",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode === 200 && response.body !== undefined) {
					let modelCategoryResponse = JSON.parse(response.body);
					if(modelCategoryResponse.response_body !== null)
						modelCategory = modelCategoryResponse.response_body;

					resolve(prepRespJsonAndLogit(response, modelCategory, "Model Categories fetched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to fetch Model Categories"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getModelCatalogs = function(srvcUrl, reqBody, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + "/catalogs",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body : JSON.stringify(reqBody)
			};

			request.post(options, function(error, response) {
				if (!error && response.statusCode === 200 && response.body !== undefined) {
					let modelCatalogResponse = JSON.parse(response.body);
					if(modelCatalogResponse.response_body !== null)
						modelCatalog = modelCatalogResponse.response_body.content;

					resolve(prepRespJsonAndLogit(response, modelCatalog, "Model Categories fetched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to fetch Model Categories"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var shareProjectToUsers = function(srvcUrl, userName, projectId, users, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + userName + "/projects/" + projectId + "/collaborators",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: users,
				json: true
			};

			request.post(options, function(error, response) {
				if (!error && (response.statusCode == 200 || response.statusCode == 201)) {
					resolve(prepRespJsonAndLogit(response, response.body, "Project shared to users successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to share project to users"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var deleteSharedUserFromProject = function(srvcUrl, userName, projectId, user, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/projects/" + projectId + "/collaborators",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: user,
				json: true
			};
			
			request.delete(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Deleted shared user from project successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete shared user from project"));
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
			
			request.post(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "List of shared projects for user retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve shared projects for user"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getUsersList = function(srvcUrl, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + "/users/userDetails",
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode === 200 && response.body !== undefined) {
					let userDetailsResponse = JSON.parse(response.body);
					if(userDetailsResponse.response_body !== null)
						userDetails = userDetailsResponse.response_body;

					resolve(prepRespJsonAndLogit(response, userDetails, "Users list fetched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to fetch users list"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};


	 var getSelectedProjectPredictors = function(userName, srvcUrl, projectId, authToken){
		return new Promise(function(resolve, reject){
			var options = {
				method : "GET",
				url : srvcUrl  + userName + '/projects/predictorList?projectId=' + projectId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {						
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Predictors for the project retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Predictors"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});

		});
	};


	var getDeploymentStatus = function(userName, srvcUrl, predictorId , authToken){
		return new Promise(function(resolve, reject){
			var options = {
				method : "GET",
				url : srvcUrl + '/users/' + userName + '/predictors/' + predictorId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};
			
			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {						
					resolve(prepRespJsonAndLogit(response, JSON.parse(response.body), "Deployment status retrieved successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to retrieve Deployment status"));
				} else {
					resolve("error");
				}
			});

		});
	};

	var deployModelKub = function(userName, srvcUrl, deployPayload, projectId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + '/users/' +  userName + "/" + projectId + '/predictors' ,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: deployPayload,
				json: true
			};

			request.post(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Deployment is in progress, the predictor section will be update in a few minutes."));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to deploy model"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var associatePredictorProject = function(userName, srvcUrl, projectId, predictorPayload, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "POST",
				url : srvcUrl + userName + "/projects/" + projectId + "/predictors" ,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: predictorPayload,
				json: true
			};

			request.post(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Predictor associated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to associate Predictor"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var updatePredictorAssociation = function(userName, srvcUrl, predictorId, associationId, predictorPayload, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "PUT",
				url : srvcUrl + userName + "/predictors/" + predictorId + "/associations/" + associationId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				},
				body: predictorPayload,
				json: true
			};

			request.put(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Predictor association updated successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to update Predictor association"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var deletePredictorAssociation = function(userName, srvcUrl, associationId, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "DELETE",
				url : srvcUrl + userName + "/predictors/associations/" + associationId,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};

			request.delete(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Predictor association deleted successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to delete Predicto association"));
				} else {
					resolve(prepRespJsonAndLogit(null, null, null, error));
				}
			});
		});
	};

	var getPredictorForModel = function(userName, srvcUrl, modelId, version, authToken){
		return new Promise(function(resolve, reject) {
			var options = {
				method : "GET",
				url : srvcUrl + userName + "/models/" + modelId + "/version/" + version,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : authToken,
				}
			};

			request.get(options, function(error, response) {
				if (!error && response.statusCode == 200) {
					resolve(prepRespJsonAndLogit(response, response.body, "Predictor for model fetched successfully"));
				} else if (!error) {
					resolve(prepRespJsonAndLogit(response, response.body, "Unable to fetch predictor for model"));
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
