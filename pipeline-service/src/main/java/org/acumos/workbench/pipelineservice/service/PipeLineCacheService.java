/*-
* ===============LICENSE_START=======================================================
* Acumos
* ===================================================================================
* Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
* ===================================================================================
* This Acumos software file is distributed by AT&T and Tech Mahindra
* under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*  
 *      http://www.apache.org/licenses/LICENSE-2.0
*  
 * This file is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ===============LICENSE_END=========================================================
*/

package org.acumos.workbench.pipelineservice.service;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.pipelineservice.exception.DuplicateRequestException;
import org.acumos.workbench.pipelineservice.util.MLWBRequestCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PipeLineCacheService {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private MLWBRequestCache requestCache;
	
	public void putCreateRequest(String requestId, Pipeline pipeline) {
		logger.debug("putCreateRequest() Begin ");
		Boolean exist = requestCache.checkIfCreateRequestExists(requestId, pipeline);
		if(exist) { 
			logger.error("Duplicate Request Exception ocured. ");
			throw new DuplicateRequestException();
		} else {
			requestCache.addCreateRequest(requestId, pipeline);
		}
		logger.debug("putCreateRequest() End");
	}
	
	public void removeCreateRequest(String requestId, Pipeline pipeline) {
		requestCache.removeCreateRequest(requestId);
	}
	
	
	public void putUpdateRequest(String requestId, Pipeline pipeline) {
		logger.debug("putUpdateRequest() Begin ");
		Boolean exist = requestCache.checkIfUpdateRequestExists(requestId, pipeline);
		if(exist) { 
			logger.error("Duplicate Request Exception ocured. ");
			throw new DuplicateRequestException();
		} else {
			requestCache.addUpdateRequest(requestId, pipeline);
		}
		logger.debug("putUpdateRequest() End ");
	}
	
	public void removeUpdateRequest(String requestId, Pipeline pipeline) {
		requestCache.removeUpdateRequest(requestId);
	}
	
	public void putDeleteRequest(String requestId, String pipelineId) {
		logger.debug("putDeleteRequest() Begin ");
		Boolean exist = requestCache.checkIfDeleteRequestExists(requestId, pipelineId);
		if(exist) { 
			logger.error("Duplicate Request Exception ocured. ");
			throw new DuplicateRequestException();
		} else {
			requestCache.addDeleteRequest(requestId, pipelineId);
		}
		logger.debug("putDeleteRequest() End ");
	}
	
	public void removeDeleteRequest(String requestId, String pipelineId) {
		requestCache.removeDeleteRequest(requestId);
	}
	
	public void putArchiveRequest(String requestId, String pipelineId) {
		logger.debug("putArchiveRequest() Begin ");
		Boolean exist = requestCache.checkIfArchiveRequestExists(requestId, pipelineId);
		if(exist) {
			logger.error("Duplicate Request Exception ocured. ");
			throw new DuplicateRequestException();
		} else {
			requestCache.addArchiveRequest(requestId, pipelineId);
		}
		logger.debug("putArchiveRequest() End ");
	}
	
	public void removeArchiveRequest(String requestId, String pipelineId) {
		requestCache.removeArchiveRequest(requestId);
	}
	
	public void removeRequest(String key) { 
		requestCache.removeArchiveRequest(key);
		requestCache.removeCreateRequest(key);
		requestCache.removeDeleteRequest(key);
		requestCache.removeUpdateRequest(key);
	}
}
