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

package org.acumos.workbench.common.exception;

/**
 * This Exception is thrown in case of any error accessing dependent service.
 *
 */
public class TargetServiceInvocationException extends RuntimeException {

	private static final long serialVersionUID = -8269469583909822292L;
	
	private static final String baseMsg = TargetServiceInvocationException.class.getSimpleName() + " : ";
	
	/**
	 * Default Constructor
	 */
	public TargetServiceInvocationException() {
		super();
	}
	
	/**
	 * Parameterized Constructor
	 * @param msg
	 * 		Message to be listed in log.
	 */
	public TargetServiceInvocationException(String msg) { 
		super(baseMsg + msg);
	}
	
	/**
	 * Parameterized Constructor 
	 * @param msg
	 * 		Message to be listed in log.
	 * @param cause
	 * 		Root Exception
	 */
	public TargetServiceInvocationException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
	
}
