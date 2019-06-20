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

package org.acumos.workbench.pipelineservice.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.acumos.workbench.common.vo.Pipeline;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
@Component
public class MLWBRequestCache implements Serializable {
	
	private static final long serialVersionUID = -4688732173089705360L;
	private Map<String, Pipeline> createRequests; // Key is Request Id and value is pipeline input
	private Map<String, Pipeline> updateRequests; // Key is Request Id and value is pipeline input
	private Map<String, String> deleteRequests;   // Key is Request Id and value is pipeline Id
	private Map<String, String> archiveRequests; // Key is Request Id and value is pipeline Id
	
	public MLWBRequestCache() {
		createRequests = new HashMap<String, Pipeline>();
		updateRequests = new HashMap<String, Pipeline>();
		deleteRequests = new HashMap<String, String>();
		archiveRequests = new HashMap<String, String>();
	}
	
	public void addCreateRequest(String key, Pipeline value) { 
		createRequests.put(key, value);
	}
	
	public void removeCreateRequest(String key) { 
		createRequests.remove(key);
	}
	
	public Pipeline getCreateRequestByKey(String key) { 
		if(createRequests.containsKey(key)) {
			return createRequests.get(key);
		}
		return null;
	}
	
	
	/**
	 * Check if request with given requestId or Pipeline already exists in the cache.
	 * @param key
	 * 		request Id
	 * @param value
	 * 		Pipeline
	 * @return boolean
	 * 		returns ture if request with same details already exists or else false. 
	 * 	
	 */
	public boolean checkIfCreateRequestExists(String key, Pipeline value) { 
		boolean result  = false;
		Pipeline prePipelineReqValue = null;
		for (Map.Entry<String, Pipeline> entry : createRequests.entrySet()) {
			if(key.equals(entry.getKey())){
				result = true; 
				break;
			}
			prePipelineReqValue = entry.getValue();
			if(value.getPipelineId().getName().equals(prePipelineReqValue.getPipelineId().getName())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public void addUpdateRequest(String key, Pipeline value) { 
		updateRequests.put(key, value);
	}
	
	public void removeUpdateRequest(String key) { 
		updateRequests.remove(key);
	}
	
	public Pipeline getUpdateRequestByKey(String key) { 
		if(updateRequests.containsKey(key)) {
			return updateRequests.get(key);
		}
		return null;
	}
	
	public boolean checkIfUpdateRequestExists(String key, Pipeline value) { 
		boolean result  = false;
		Pipeline prePipelineReqValue = null;
		for (Map.Entry<String, Pipeline> entry : updateRequests.entrySet()) {
			if(key.equals(entry.getKey())){
				result = true; 
				break;
			}
			prePipelineReqValue = entry.getValue();
			if(value.getPipelineId().getUuid().equals(prePipelineReqValue.getPipelineId().getUuid())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	
	public void addDeleteRequest(String key, String value) { 
		deleteRequests.put(key, value);
	}
	
	public void removeDeleteRequest(String key) { 
		deleteRequests.remove(key);
	}
	
	public String getdeleteRequestByKey(String key) { 
		if(deleteRequests.containsKey(key)) {
			return deleteRequests.get(key);
		}
		return null;
	}
	
	public boolean checkIfDeleteRequestExists(String key, String value) { 
		boolean result  = false;
		for (Map.Entry<String, String> entry : deleteRequests.entrySet()) {
			if(key.equals(entry.getKey())){
				result = true; 
				break;
			}
			if(value.equals(entry.getValue())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public void addArchiveRequest(String key, String value) { 
		archiveRequests.put(key, value);
	}
	
	public void removeArchiveRequest(String key) { 
		archiveRequests.remove(key);
	}
	
	public String getArchiveRequestByKey(String key) { 
		if(archiveRequests.containsKey(key)) {
			return archiveRequests.get(key);
		}
		return null;
	}
	
	public boolean checkIfArchiveRequestExists(String key, String value) { 
		boolean result  = false;
		for (Map.Entry<String, String> entry : archiveRequests.entrySet()) {
			if(key.equals(entry.getKey())){
				result = true; 
				break;
			}
			if(value.equals(entry.getValue())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * @return the createRequests
	 */
	public Map<String, Pipeline> getCreateRequests() {
		return createRequests;
	}
	/**
	 * @param createRequests the createRequests to set
	 */
	public void setCreateRequests(Map<String, Pipeline> createRequests) {
		this.createRequests = createRequests;
	}
	/**
	 * @return the updateRequests
	 */
	public Map<String, Pipeline> getUpdateRequests() {
		return updateRequests;
	}
	/**
	 * @param updateRequests the updateRequests to set
	 */
	public void setUpdateRequests(Map<String, Pipeline> updateRequests) {
		this.updateRequests = updateRequests;
	}
	/**
	 * @return the deleteRequests
	 */
	public Map<String, String> getDeleteRequests() {
		return deleteRequests;
	}
	/**
	 * @param deleteRequests the deleteRequests to set
	 */
	public void setDeleteRequests(Map<String, String> deleteRequests) {
		this.deleteRequests = deleteRequests;
	}

	/**
	 * @return the archiveRequests
	 */
	public Map<String, String> getArchiveRequests() {
		return archiveRequests;
	}

	/**
	 * @param archiveRequests the archiveRequests to set
	 */
	public void setArchiveRequests(Map<String, String> archiveRequests) {
		this.archiveRequests = archiveRequests;
	}
	

	

}
