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

package org.acumos.workbench.notebookservice.util;

import java.util.HashMap;
import java.util.Map;

import org.acumos.workbench.common.util.ServiceStatus;

public enum NotebookType {
	JUPYTER("JP"),
	ZEPPELINE("ZP");
	
	private final String notebookTypeCode;
	
	private NotebookType(String notebookTypeCode) {
		this.notebookTypeCode = notebookTypeCode;
	}
	
	/**
	 * To return String value for NotebookType 
	 * @return String
	 * 		return String value for NotebookTypeCode.
	 */
	public String getNotebookTypeCode() { 
		return this.notebookTypeCode;
	}
	
	//Create reverse lookup hash map 
	private static final Map<String, NotebookType> lookup = new HashMap<String, NotebookType>();

	static {
		for (NotebookType nt : NotebookType.values()) {
			lookup.put(nt.getNotebookTypeCode(), nt);
		}
	}
	
	/**
	 * Returns NotebookType Enum for the input code value else null.
	 * @param notebookTypeCode
	 * 		Notebook Type code
	 * @return NotebookType
	 * 		Returns NotebookType Enum for input code.
	 */
	public static NotebookType get(String notebookTypeCode) { 
        //the reverse lookup by simply getting the value from the lookup HsahMap.
		if(lookup.containsKey(notebookTypeCode)) {
			return lookup.get(notebookTypeCode);
		} else { 
			return null;
		}
         
    }
}
