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

import org.acumos.workbench.common.exception.BadRequestException;

public class DuplicateNotebookException extends BadRequestException {

	private static final long serialVersionUID = -1591533159073146327L;
	
	private static final String MSG = "Notebook name and type [and version] already exists";
	
	/**
	 * To handle Duplicate Project Exception. 
	 */
	public DuplicateNotebookException() {
		super(MSG);
	}
	
}
