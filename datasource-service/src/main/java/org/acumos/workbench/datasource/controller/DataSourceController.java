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

package org.acumos.workbench.datasource.controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.service.IDataSourceService;
import org.acumos.workbench.datasource.service.IDataSourceValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/")
public class DataSourceController {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("DataSourceServiceImpl")
	private IDataSourceService dataSourceService;
	
	@Autowired
	@Qualifier("DataSourceValidationServiceImpl")
	private IDataSourceValidationService validationService;
	
	
	@ApiOperation(value = "Create a new DataSource")
	@RequestMapping(value = "/users/{authenticatedUserId}", method = RequestMethod.POST)
	public ResponseEntity<?> createDataSource(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@RequestBody(required = true) DataSource dataSource) throws DataSourceNotFoundException, ClassNotFoundException, IOException, SQLException {
		logger.debug("createDataSource() Begin");
		
		// 1. Check all the mandatory input request params are there or not
		// 2. Check input request body is having proper json structure or not i.e json structure validation(schema)
		// 3. Check the Authenticated User Id is present or not
		validationService.validateRequest(authenticatedUserId, null, dataSource);
		// 4. Check the login user exists in acumos, or not by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		DataSource result = dataSourceService.createDataSource(authenticatedUserId,dataSource);
		logger.debug("createDataSource() End");
		return new ResponseEntity<DataSource>(result, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "To Update the DataSource Details of a particular datasource key")
	@RequestMapping(value = "/users/{authenticatedUserId}/datasources/{datasourceKey}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateDataSourceDetail(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Data Source key", required = true) @PathVariable("datasourceKey") String dataSourceKey,
			@RequestBody(required = true) DataSource dataSource) throws IOException, DataSourceNotFoundException, ClassNotFoundException, SQLException {
		
		logger.debug("Controller : updateDataSourceDetail() Begin");
		
		// 1. Check all the mandatory input request params are there or not
		// 2. Check input request body is having proper json structure or not i.e json structure validation(schema)
		// 3. Check the Authenticated User Id is present or not
		validationService.validateRequest(authenticatedUserId, dataSourceKey, dataSource);
		// 4. Check the login user exists in acumos, or not by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		DataSource result = dataSourceService.updateDataSourceDetails(authenticatedUserId,dataSourceKey,dataSource);	
		logger.debug("Controller : updateDataSourceDetail() End");
		return new ResponseEntity<DataSource>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "To get the DataSource Details of a particular datasource key")
	@RequestMapping(value = "/users/{authenticatedUserId}/datasource/{datasourceKey}", method = RequestMethod.GET)
	public ResponseEntity<?> getDataSource(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Data Source key", required = true) @PathVariable("datasourceKey") String dataSourceKey) throws IOException{
		logger.debug("getDataSource() Begin");
		
		// 1. Check all the mandatory input request params are there or not
		// 2. Check input request body is having proper json structure or not i.e json structure validation(schema)
		// 3. Check the Authenticated User Id is present or not
		validationService.validateRequest(authenticatedUserId, dataSourceKey, null);
		// 4. Check the login user exists in acumos, or not by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		
		DataSource dataSourceModel = dataSourceService.fetchDataSource(authenticatedUserId, dataSourceKey);
		logger.debug("getDataSource() End");
		return new ResponseEntity<DataSource>(dataSourceModel, HttpStatus.OK);
		
	}
	
	@ApiOperation(value = "To get the List of DataSource Details ")
	@RequestMapping(value = "/users/{authenticatedUserId}", method = RequestMethod.GET)
	public ResponseEntity<?> getDataSourcesList(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Data Source Category", required = false, defaultValue = " ") @RequestParam("category") String category,
			@ApiParam(value = "Data Sourece Namespace", required = false, defaultValue = " ") @RequestParam("namespace") String namespace,
			@ApiParam(value = "Data Source TextSearch", required = false, defaultValue = " ") @RequestParam("textSearch") String textSearch){
		logger.debug("getDataSourcesList() Begin");
		validationService.validateRequest(authenticatedUserId, null, null);
		// Check the login user exists in acumos, or not by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		
		// validate the category of a datasource
		if(null != category && !category.isEmpty()) {
			validationService.validateInputParameter("category",category);
		}
		List<DataSource> dataSourceList = dataSourceService.fetchDataSourcesList(authenticatedUserId,namespace,category,textSearch);
		logger.debug("getDataSourcesList() End");
		return new ResponseEntity<List<DataSource>>(dataSourceList, HttpStatus.OK);
		
	}
	
	@ApiOperation(value = "To Delete the existing Data Source")
	@RequestMapping(value = "/users/{authenticatedUserId}/datasource/{datasourceKey}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteDataSource(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "Data Source key", required = true) @PathVariable("datasourceKey") String dataSourceKey) throws IOException{
		logger.debug("deleteDataSource() Begin");
		validationService.validateRequest(authenticatedUserId, dataSourceKey, null);
		// Check the login user exists in acumos, or not by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		// Delete the DataSource from Database
		ServiceState state = dataSourceService.deleteDataSource(authenticatedUserId,dataSourceKey);
		logger.debug("deleteDataSource() End");
		return new ResponseEntity<ServiceState>(state, HttpStatus.OK);
		
	}
	

}
