/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.datasource.exception;

public class DataSourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1154726640423870697L;

	/**
	 * To Handle DataSourceNotFoundException and returns appropriate response to UI.
	 * @param msg
	 * 			Accepts the Customized Exception Message
	 */
	public DataSourceNotFoundException(String msg) {
		super(msg);
	}
	/**
	 * To handle DataSourceNotFoundException and returns appropriate response to UI.
	 * @param string
	 * 			Accepts the Customized Exception Message
	 * @param statusCode
	 * 			Exception Status Code 
	 */
	public DataSourceNotFoundException(String message, int statusCode) {
		super(message);
	}
	
	/**
	 * To handle DataSourceNotFoundException and returns appropriate response to UI.
	 * @param message
	 * 			Accepts the Customized Exception Message
	 * @param object
	 * 			Throwable class object
	 */
	public DataSourceNotFoundException(String message, Throwable object) {
		super(message, object);
	}
	

}
