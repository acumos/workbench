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

package org.acumos.workbench.notebookservice.exception;
/**
 * This exception is thrown for invalid or incorrect application configuration.
 *
 */
public class InvalidConfiguration extends RuntimeException {

	private static final long serialVersionUID = 7799680694143418475L;

	/**
	 * Default Constructor
	 */
	public InvalidConfiguration() {
		super();
	}
	
	/**
	 * Parameterized Constructor
	 * @param msg
	 * 		Message to be listed in log.
	 */
	public InvalidConfiguration(String msg) { 
		super(msg);
	}
	
	/**
	 * Parameterized Constructor 
	 * @param msg
	 * 		Message to be listed in log.
	 * @param cause
	 * 		
	 */
	public InvalidConfiguration(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
