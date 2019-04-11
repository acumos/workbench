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

import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.pipelineservice.exception.IncorrectValueException;
import org.acumos.workbench.pipelineservice.exception.InvalidInputJsonStructureException;
import org.acumos.workbench.pipelineservice.exception.ValueNotFoundException;

public interface InputValidationService {
	
	/**
	 * This method validates the Input Json Structure for PipeLine
	 * @param pipeLine
	 * 			pipeLine acts as input param
	 * @throws InvalidInputJsonStructureException
	 * 			throws InvalidJsonStructureException in case of incorrect input Json.
	 */
	public void validatePipeLineInputJsonStructure(Pipeline pipeLine) throws InvalidInputJsonStructureException;
	
	/**
	 * This method verifies if the value is exists i.e, value is not null or empty.
	 * @param fieldName
	 * 			The name of the filed to be shown in the error message.
	 * @param value
	 * 			The value to be validated.
	 * @throws ValueNotFoundException
	 * 			throws ValueNotFoundException in case value is null or empty.
	 */
	public void isValueExists(String fieldName, String value) throws ValueNotFoundException;
	
	/**
	 * This method validates the pipeline name
	 * @param value
	 * 			the value to be validated.
	 * @throws IncorrectValueException
	 * 			throws IncorrectValueException in case pipeline name is not valid.
	 */
	public void validatePipeLineName(String value) throws IncorrectValueException;
	
	/**
	 * This method validates the pipeline version 
	 * @param value
	 * 			the value to be validated.
	 * @throws IncorrectValueException
	 * 			throws IncorrectValueException in case pipeline version is not valid.
	 */
	public void validateVersion(String value) throws IncorrectValueException;
	
	
}
