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

package org.acumos.workbench.datasource.service;

import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;

public interface IDataSourceInputValidationService {
	
	/**
	 * This method will validates the input json structure
	 * @param dataSource
	 * 		The DataSource object with input values
	 * @throws DataSourceNotFoundException 
	 */
	public void validateInputJson(DataSource dataSource) throws DataSourceNotFoundException;

	/**
	 * To Check is this value is exists or not
	 * @param fieldName
	 * 			input field name
	 * @param value
	 * 			input value
	 */
	public void isValuePresent(String fieldName, String value);
	
	/**
	 * To validate the connection parameters
	 * @param dataSource
	 * 			input DataSource model object
	 */
	public void validateConnectionParameters(DataSource dataSource);

}
