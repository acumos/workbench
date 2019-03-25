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

package org.acumos.workbench.notebookservice.service;

import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.notebookservice.exception.IncorrectValueException;
import org.acumos.workbench.notebookservice.exception.InvalidInputJSONException;
import org.acumos.workbench.notebookservice.exception.ValueNotFoundException;

public interface InputValidationService {

	/**
	 * This method validates the input JSON for Notebook.
	 * @param notebook
	 * 		the Notebook object with input values. 
	 * @throws InvalidInputJSONException
	 * 		throws InvalidInputJSONException in case of error in the input JSON
	 */
	public void validateNotebookInputJson(Notebook notebook) throws  InvalidInputJSONException;
	
	/**
	 * This method check if value is present i.e, value is not null or Empty. 
	 * 
	 * @param fieldName
	 * 		The name of the filed to be shown in the error message. 
	 * @param value
	 * 		The value to be validated
	 * @throws ValueNotFoundException
	 * 		throws ValueNotFoundException in case value is null or empty.
	 */
	public void isValuePresent(String fieldName, String value) throws ValueNotFoundException;
	
	/**
	 * This method validate Notebook name.
	 * @param value
	 * 		the value to be validated. 
	 * @throws IncorrectValueException
	 * 		throws IncorrectValueException in case Notebook name is not valid. 
	 */
	public void validateNotebookName(String value) throws IncorrectValueException;
	
	/**
	 * This method validate Notebook version
	 * @param value
	 * 		the value to be validated. 
	 * @throws IncorrectValueException
	 * 		throws IncorrectValueException in case Notebook version is not valid. 
	 */
	public void validateVersion(String value) throws IncorrectValueException;
	
	
	/**
	 * To validate Notebook Type
	 * @param notebookType
	 * 		the Notebook type value to be validated. 
	 * 
	 * @throws IncorrectValueException
	 * 	throws IncorrectValueException in case Notebook type is not valid. 
	 */
	public void validateNotebookType(String notebookType) throws IncorrectValueException;
	
}
