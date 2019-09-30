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

package org.acumos.workbench.projectservice.exception;

public class CollaboratorNotExistsException extends RuntimeException{

	private static final long serialVersionUID = 2115038518454952346L;

	/**
	 * To throw CollaboratorNotExistsException (if user is not the collaborator)
	 * 
	 * @param message 
	 * 			the message
	 */
	public CollaboratorNotExistsException(String message) {
		super(message);
	}

	/**
	 * To throw CollaboratorNotExistsException (if user is not the collaborator)
	 * 
	 * @param message 
	 * 			the message
	 * @param object  
	 * 			the Throwable object
	 */
	public CollaboratorNotExistsException(String message, Throwable object) {
		super(message, object);
	}
}

