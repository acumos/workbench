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
import java.util.List;

import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.datasource.service.IDSSharingService;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/")
public class DataSourceSharingController {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("DataSourceValidationServiceImpl")
	private IDataSourceValidationService validationService;
	
	@Autowired
	@Qualifier("DataSourceServiceImpl")
	private IDataSourceService dataSourceService;
	
	@Autowired
	@Qualifier("DSSharingServiceImpl")
	private IDSSharingService dsSharingService;

	@ApiOperation(value = "Share Data Source to a Collaborator")
	@RequestMapping(value = "/users/{authenticatedUserId}/datasource/{dataSourceKey}", method = RequestMethod.POST)
	public ResponseEntity<?> shareDataSource(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "The DataSource Key", required = true) @PathVariable("dataSourceKey") String dataSourceKey,
			@ApiParam(value = "Collaborators Details", required = true) @RequestBody Users collaborators) throws IOException {
		logger.debug("shareDataSource() Begin");
		// 1. Check all the mandatory input request params are there or not
		// 2. Check input request body is having proper json structure or not i.e json structure validation(schema)
		// 3. Check the Authenticated User Id is present or not
		validationService.validateRequest(authenticatedUserId, dataSourceKey, null);
		// 4. Check the login user exists in acumos, or not by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		// 5. Check the login user is owner of the datasource or not, to share the datasource with another user.
		DataSource datasource = dataSourceService.fetchDataSource(authenticatedUserId, dataSourceKey);
		// 6. Check if the given input collaborator is already collaborator for the given DataSource
		dsSharingService.collaboratorExists(dataSourceKey,collaborators);
		// 7. Share the DataSource to another user.
		DataSource result = dsSharingService.shareDataSource(authenticatedUserId,dataSourceKey,collaborators,datasource);
		logger.debug("shareDataSource() End");
		return new ResponseEntity<DataSource>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Remove the User from Collaborator List")
	@RequestMapping(value =  "/users/{authenticatedUserId}/datasource/{dataSourceKey}/collaborators", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeDataSourceCollaborator(@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "The DataSource Key", required = true) @PathVariable("dataSourceKey") String dataSourceKey,
			@ApiParam(value = "Collaborators Details", required = true) @RequestBody Users collaborators) throws IOException{
		logger.debug("removeDataSourceCollaborator() Begin");
		// 1. Check all the mandatory input request params are there or not
		// 2. Check input request body is having proper json structure or not i.e json structure validation(schema)
		// 3. Check the Authenticated User Id is present or not
		validationService.validateRequest(authenticatedUserId, dataSourceKey, null);
		// 4. Check the login user exists in acumos or not, by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		// 5. Check the Login user is the owner of the datasource or not, to delete the datasource
		DataSource datasource = dataSourceService.fetchDataSource(authenticatedUserId, dataSourceKey);
		// 6. Check the collaborator is active or not in Acumos
		dataSourceService.isActiveUser(collaborators);
		// 7. Check the input collaborator is exists or not in Couch DB
		dsSharingService.isCollaboratorRemovable(dataSourceKey,collaborators);
		//DataSource result = dsSharingService.removeCollaborator(authenticatedUserId,dataSourceKey,collaborators);
		DataSource result = dsSharingService.removeCollaborator(authenticatedUserId, dataSourceKey, collaborators,datasource);
		logger.debug("removeDataSourceCollaborator() End");
		return new ResponseEntity<DataSource>(result, HttpStatus.OK);
		
	}

	@ApiOperation(value = "Get the Shared DataSources for a User")
	@RequestMapping(value = "/users/{authenticatedUserId}/datasources/shared", method = RequestMethod.GET)
	public ResponseEntity<?> getSharedDataSources(
			@ApiParam(value = "The Acumos Login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId) {
		logger.debug("getSharedDataSources() Begin");
		// 1. Check all the mandatory input request params are there or not
		// 2. Check input request body is having proper json structure or not i.e json structure validation(schema)
		// 3. Check the Authenticated User Id is present or not
		validationService.validateRequest(authenticatedUserId, null, null);
		// 4. Check the login user exists in acumos or not, by calling cds service
		dataSourceService.getUserDetails(authenticatedUserId);
		// 5. Get the Shared DataSources for login user
		List<DataSource> dataSourceList = dsSharingService.getSharedDataSources(authenticatedUserId);
		logger.debug("getSharedDataSources() End");
		return new ResponseEntity<List<DataSource>>(dataSourceList, HttpStatus.OK);

	}
}
