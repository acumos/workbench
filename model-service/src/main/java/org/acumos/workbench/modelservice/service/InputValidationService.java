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

package org.acumos.workbench.modelservice.service;

import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Models;

public interface InputValidationService {
	
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
	 * This method will validates the input Json value of Model
	 * @param model
	 * 			the model object with input values
	 * @throws InvalidInputJSONException
	 * 			throws InvalidInputJSONException in case of error in the input JSON
	 */
	public void validateModelInputJson(Model model) throws InvalidInputJSONException;
	
}
