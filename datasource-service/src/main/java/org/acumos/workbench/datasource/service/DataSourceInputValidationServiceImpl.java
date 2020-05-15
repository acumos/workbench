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
import java.util.ArrayList;

import javax.ws.rs.core.Response.Status;

import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.datasource.enums.CategoryTypeEnum;
import org.acumos.workbench.datasource.enums.ReadWriteTypeEnum;
import org.acumos.workbench.datasource.exception.DataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("DataSourceInputValidationServiceImpl")
public class DataSourceInputValidationServiceImpl implements IDataSourceInputValidationService{

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Override
	public void isValuePresent(String fieldName, String value) throws ValueNotFoundException{
		logger.debug("isValuePresent() Begin");
		boolean result = false;
		String msg = fieldName + " is missing";

		if (null != value && !value.trim().equals("")) {
			result = true;
		}

		if (!result) {
			logger.error("ValueNotFoundException occured in isValuePresent()" + msg);
			throw new ValueNotFoundException(msg);
		}
		logger.debug("isValuePresent() End");

	}
	
	@Override
	public void validateInputJson(DataSource dataSource) throws DataSourceException {
		logger.debug("validateInputJson() Begin");
		ArrayList<String> missedParameters = new ArrayList<String>();
		if (null != dataSource) {
			if (dataSource.getCategory() == null) {
				missedParameters.add("category");
			}

			if (dataSource.getNamespace() == null) {
				missedParameters.add("namespace");
			}

			if (dataSource.getDatasourceId().getName() == null) {
				missedParameters.add("datasourceName");
			}

			if (dataSource.getReadWriteDescriptor() == null) {
				missedParameters.add("readWriteDescriptor");
			}
			if (!missedParameters.isEmpty()) {
				String[] variables = new String[missedParameters.size()];

				for (int i = 0; i < missedParameters.size(); i++) {
					variables[i] = missedParameters.get(i);
				}
				throw new InvalidInputJSONException();
			}
			// Validate the field values
			if (dataSource.getCategory() != null) {
				boolean isValid = false;

				for (CategoryTypeEnum categoryType : CategoryTypeEnum.values()) {
					if (categoryType.getCategoryType().equalsIgnoreCase(dataSource.getCategory())) {
						isValid = true;
						break;
					}
				}

				if (!isValid) {
					checkInvalidEnumeration("category", Status.BAD_REQUEST.getStatusCode());
				}
			}

			if (dataSource.getReadWriteDescriptor() != null) {
				boolean isValid = false;

				for (ReadWriteTypeEnum readWriteType : ReadWriteTypeEnum.values()) {
					if (readWriteType.getReadWriteType()
							.equalsIgnoreCase(dataSource.getReadWriteDescriptor())) {
						isValid = true;
						break;
					}
				}

				if (!isValid) {
					checkInvalidEnumeration("readWriteDescriptor", Status.BAD_REQUEST.getStatusCode());
				}
			}

		}
	}

	@Override
	public void validateConnectionParameters(DataSource dataSource) throws DataSourceException {
		if (dataSource.getCategory().equals("mysql")) {
			if (dataSource.getCommonDetails() == null || dataSource.getDbDetails() == null
					|| dataSource.getCommonDetails().getServerName() == null
					|| dataSource.getCommonDetails().getPortNumber() == 0
					|| dataSource.getDbDetails().getDbServerUsername() == null
					|| dataSource.getDbDetails().getDbServerPassword() == null
					|| dataSource.getDbDetails().getDatabaseName() == null
					|| dataSource.getDbDetails().getDbQuery() == null) {

				logger.error(
						"Datasource Model has missing mandatory information for category type: mysql. throwing excpetion...");

				ArrayList<String> missedParameters = new ArrayList<String>();
				if (dataSource.getCommonDetails() == null)
					missedParameters.add("commonDetails");
				else if (dataSource.getDbDetails() == null)
					missedParameters.add("dbDetails");
				else {
					if (dataSource.getDbDetails().getDbServerUsername() == null)
						missedParameters.add("dbServerUsername");
					if (dataSource.getDbDetails().getDbServerPassword() == null)
						missedParameters.add("dbServerPassword");
					if (dataSource.getDbDetails().getDatabaseName() == null)
						missedParameters.add("databaseName");
					if (dataSource.getDbDetails().getDbQuery() == null)
						missedParameters.add("dbQuery");
					if (dataSource.getCommonDetails().getServerName() == null)
						missedParameters.add("serverName");
					if (dataSource.getCommonDetails().getPortNumber() == 0)
						missedParameters.add("portNumber");
				}
				String[] variables = new String[missedParameters.size()];
				for (int i = 0; i < missedParameters.size(); i++) {
					variables[i] = missedParameters.get(i);
				}

				throw new DataSourceException(
						"Datasource has missing mandatory information. Please send all the required information.",
						Status.BAD_REQUEST.getStatusCode());
			}

		} else if (dataSource.getCategory().equals("mongo")) {
			if (dataSource.getCommonDetails() == null || dataSource.getDbDetails() == null
					|| dataSource.getCommonDetails().getServerName() == null
					|| dataSource.getCommonDetails().getPortNumber() == 0
					|| dataSource.getDbDetails().getDatabaseName() == null
					|| dataSource.getDbDetails().getDbQuery() == null
					|| dataSource.getDbDetails().getDbCollectionName() == null) {

				logger.error(
						"Datasource Model has missing mandatory information for category type: mongo. throwing excpetion...");
				ArrayList<String> missedParameters = new ArrayList<String>();
				if (dataSource.getCommonDetails() == null)
					missedParameters.add("commonDetails");
				else if (dataSource.getDbDetails() == null)
					missedParameters.add("dbDetails");
				else {
					if (dataSource.getCommonDetails().getServerName() == null)
						missedParameters.add("serverName");
					if (dataSource.getCommonDetails().getPortNumber() == 0)
						missedParameters.add("portNumber");
					if (dataSource.getDbDetails().getDatabaseName() == null)
						missedParameters.add("databaseName");
					if (dataSource.getDbDetails().getDbQuery() == null)
						missedParameters.add("dbQuery");
					if (dataSource.getDbDetails().getDbCollectionName() == null)
						missedParameters.add("dbCollectionName");
				}

				String[] variables = new String[missedParameters.size()];

				for (int i = 0; i < missedParameters.size(); i++) {
					variables[i] = missedParameters.get(i);
				}
				throw new DataSourceException("Datasource has missing mandatory information. Please send all the required information.", Status.BAD_REQUEST.getStatusCode());
			}
		} else if (dataSource.getCategory().equals("couch")) {
			if (dataSource.getCommonDetails() == null || dataSource.getDbDetails() == null
					|| dataSource.getCommonDetails().getServerName() == null
					|| dataSource.getCommonDetails().getPortNumber() == 0
					|| dataSource.getDbDetails().getDatabaseName() == null) {
				ArrayList<String> missedParameters = new ArrayList<String>();
				if (dataSource.getCommonDetails() == null)
					missedParameters.add("commonDetails");
				else if (dataSource.getDbDetails() == null)
					missedParameters.add("dbDetails");
				else {
					if (dataSource.getDbDetails().getDbServerUsername() == null)
						missedParameters.add("dbServerUsername");
					if (dataSource.getDbDetails().getDbServerPassword() == null)
						missedParameters.add("dbServerPassword");
					if (dataSource.getDbDetails().getDatabaseName() == null)
						missedParameters.add("databaseName");
					if (dataSource.getDbDetails().getDbQuery() == null)
						missedParameters.add("dbQuery");
					if (dataSource.getCommonDetails().getServerName() == null)
						missedParameters.add("serverName");
					if (dataSource.getCommonDetails().getPortNumber() == 0)
						missedParameters.add("portNumber");
				}

				String[] variables = new String[missedParameters.size()];

				for (int i = 0; i < missedParameters.size(); i++) {
					variables[i] = missedParameters.get(i);
				}
				throw new DataSourceException("Datasource has missing mandatory information. Please send all the required information.", Status.BAD_REQUEST.getStatusCode());
			}
		}
	}
	
	/**
	 * To check for the Invalid Enumeration such as Category and readWriteDescriptor
	 * @param parameter
	 * 		parameter such as category or read write descriptor
	 * @param exceptionType
	 * 		type of the exception such as NotFound
	 * @throws DataSourceException
	 */
	public void checkInvalidEnumeration(String parameter, int exceptionType) throws DataSourceException {
		ArrayList<String> validValues = new ArrayList<String>();

		if ("category".equals(parameter)) {

			validValues.add(parameter);

			CategoryTypeEnum[] categories = CategoryTypeEnum.values();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < categories.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(categories[i].getCategoryType());
			}

			validValues.add(sb.toString());

		} else if ("readWriteDescriptor".equals(parameter)) {

			validValues.add(parameter);

			ReadWriteTypeEnum[] readWriteTypes = ReadWriteTypeEnum.values();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < readWriteTypes.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(readWriteTypes[i].getReadWriteType());
			}

			validValues.add(sb.toString());
		}

		if (!validValues.isEmpty()) {

			String[] variables = new String[validValues.size()];

			for (int i = 0; i < validValues.size(); i++) {
				variables[i] = validValues.get(i);
			}

			if (exceptionType == 400) {
				throw new DataSourceException("DataSource has invalid category value. Please send the correct value.", Status.BAD_REQUEST.getStatusCode());
			} else {
				throw new DataSourceException("DataSource has invalid category value. Please send the correct value.", Status.NOT_FOUND.getStatusCode());
			}
		}

	}

}
