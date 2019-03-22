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
