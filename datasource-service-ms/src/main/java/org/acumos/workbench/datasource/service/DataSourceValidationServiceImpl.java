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

import java.lang.invoke.MethodHandles;

import javax.ws.rs.core.Response.Status;

import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.datasource.enums.CategoryTypeEnum;
import org.acumos.workbench.datasource.exception.DataSourceException;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("DataSourceValidationServiceImpl")
public class DataSourceValidationServiceImpl implements IDataSourceValidationService{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("DataSourceInputValidationServiceImpl")
	private IDataSourceInputValidationService inputValidationService;

	@Override
	public void validateRequest(String authenticatedUserId, String dataSourceKey, DataSource dataSource)
			throws DataSourceNotFoundException {
		logger.debug("validateRequest() Begin");
		// Check the Authenticated User Id is present or not
		inputValidationService.isValuePresent("Acumos User Id", authenticatedUserId);

		if (null != dataSourceKey) {
			inputValidationService.isValuePresent("Data Source Key", dataSourceKey);
		}

		// Check input request body is having proper json structure or not i.e json structure validation(schema)
		if (null != dataSource) {
			if (null != dataSource.getDatasourceId()) {
				if(null !=  dataSource.getDatasourceId().getVersionId().getLabel()) {
					inputValidationService.isValuePresent("version", dataSource.getDatasourceId().getVersionId().getLabel());
				}
			}
			inputValidationService.validateInputJson(dataSource);
			// Check for the input connection parameters are there are not
			inputValidationService.validateConnectionParameters(dataSource);
		}

		logger.debug("validateRequest() End");
	}

	@Override
	public void validateInputParameter(String category, String paramValue) {
		logger.debug("validateInputParameter() Begin");
		boolean isValid = false;
		for (CategoryTypeEnum categoryType : CategoryTypeEnum.values()) {
			if (categoryType.getCategoryType().equalsIgnoreCase(paramValue)) {
				isValid = true;
				break;
			}
		}
		if (!isValid) {
			throw new DataSourceException("DataSource has invalid category value. Please send the correct value.",
					Status.NOT_FOUND.getStatusCode());
		}
		logger.debug("validateInputParameter() End");

	}
	
}
