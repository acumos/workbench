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

import static org.acumos.workbench.common.security.SecurityConstants.AUTHORIZATION_HEADER_KEY;
import static org.acumos.workbench.common.security.SecurityConstants.JWT_TOKEN_HEADER_KEY;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.datasource.service.IDataSourceService;
import org.acumos.workbench.datasource.service.IDataSourceValidationService;
import org.acumos.workbench.datasource.vo.DataSourceAssociationModel;
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
public class DataSourceAssociationController {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	@Qualifier("DataSourceValidationServiceImpl")
	private IDataSourceValidationService validationService;
	
	@Autowired
	@Qualifier("DataSourceServiceImpl")
	private IDataSourceService dataSourceService;
	
	@ApiOperation(value = "Associate the DataSource to the Project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/datasource/{datasourceKey}", method = RequestMethod.POST)
	public ResponseEntity<?> associateDataSourcetoProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "DataSourceKey", required = true) @PathVariable("datasourceKey") String datasourceKey,
			@RequestBody(required = true) DataSource dataSource) throws IOException {
		// DataSourceModel contains datasetName,category,version,isActive,datasourceKey,createdTimestamp
		logger.debug("associateDataSourcetoProject() Begin");
		// The DataSource Service must check if the request JSON structure is valid or not and Check for the User Id Missing or not
		validationService.validateRequest(authenticatedUserId, datasourceKey, null);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not.
		// Check if the projectId and Version already exists or not by calling the Project Service.
		// Check if the Project is Archived or not Get the Authentication Token from the HttpHeaders.
		String authToken = getAuthJWTToken(request);
		dataSourceService.validateProject(authenticatedUserId, projectId, authToken);
		// Check if the SolutionId and the version already exists in CDS
		// Associate the DataSource to a Project
		DataSourceAssociationModel result = dataSourceService.linkDataSourcetoProject(authenticatedUserId,projectId,datasourceKey,dataSource);
		logger.debug("associateDataSourcetoProject() End");
		return new ResponseEntity<DataSourceAssociationModel>(result, HttpStatus.OK);

	}
	
	@ApiOperation(value = "Get the list of DataSources which are associated to a project")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}", method = RequestMethod.GET)
	public ResponseEntity<?> dataSourceListAssociatedtoProject(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId) throws IOException {
		// DataSourceModel contains datasetName,category,version,isActive,datasourceKey,createdTimestamp
		logger.debug("dataSourceListAssociatedtoProject() Begin");
		// The DataSource Service must check if the request JSON structure is valid or not and Check for the User Id Missing or not
		validationService.validateRequest(authenticatedUserId, null, null);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not
		// Check if the projectId and Version already exists or not by calling the Project Service.
		// Check if the Project is Archived or not Get the Authentication Token from the HttpHeaders
		String authToken = getAuthJWTToken(request);
		dataSourceService.validateProject(authenticatedUserId, projectId, authToken);
		// Get the list of DataSources which are associated to Project
		List<DataSourceAssociationModel> result = dataSourceService.getDataSourceListAssociatedToProject(authenticatedUserId,projectId);
		logger.debug("dataSourceListAssociatedtoProject() End");
		return new ResponseEntity<List<DataSourceAssociationModel>>(result, HttpStatus.OK);
	}
	
	// Update the existing Association Details with the user provided new values. viz datasource version,..
	
	@ApiOperation(value = "Update the Association Details of DataSourceProject")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/datasource/{datasourceKey}/association/{associationId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateDataSourceProjectAssociationDetails(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "DataSourceKey", required = true) @PathVariable("datasourceKey") String datasourceKey,
			@ApiParam(value = "Association Id", required = true) @PathVariable("associationId") String associationId,
			@RequestBody(required = true) DataSource dataSource) throws IOException {
		// DataSourceModel contains datasetName,category,version,isActive,datasourceKey,createdTimestamp
		logger.debug("updateDataSourceProjectAssociationDetails() Begin");
		// The DataSource Service must check if the request JSON structure is valid or not and Check for the User Id Missing or not
		validationService.validateRequest(authenticatedUserId, datasourceKey, null);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not
		// Check if the projectId and Version already exists or not by calling the Project Service.
		// Check if the Project is Archived or not Get the Authentication Token from the HttpHeaders
		String authToken = getAuthJWTToken(request);
		dataSourceService.validateProject(authenticatedUserId, projectId, authToken);
		// Update DataSourceProject Association in DB
		DataSourceAssociationModel result = dataSourceService.updateAssociationDetails(authenticatedUserId, projectId,
				associationId, datasourceKey, dataSource);
		logger.debug("updateDataSourceProjectAssociationDetails() End");
		return new ResponseEntity<DataSourceAssociationModel>(result, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Delete the Association Details of DataSourceProject")
	@RequestMapping(value = "/users/{authenticatedUserId}/projects/{projectId}/datasource/{datasourceKey}/association/{associationId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteDataSourceProjectAssocaition(HttpServletRequest request,
			@ApiParam(value = "Acumos User login Id", required = true) @PathVariable("authenticatedUserId") String authenticatedUserId,
			@ApiParam(value = "ProjectId", required = true) @PathVariable("projectId") String projectId,
			@ApiParam(value = "DataSourceKey", required = true) @PathVariable("datasourceKey") String datasourceKey,
			@ApiParam(value = "Association Id", required = true) @PathVariable("associationId") String associationId){
		logger.debug("deleteDataSourceProjectAssocaition() Begin");
		// The DataSource Service must check if the request JSON structure is valid or not and Check for the User Id Missing or not
		validationService.validateRequest(authenticatedUserId, datasourceKey, null);
		// The Model Service must call CDS to check if the Acumos User Id is the owner of the project or not
		// Check if the projectId and Version already exists or not by calling the Project Service.
		// Check if the Project is Archived or not Get the Authentication Token from the HttpHeaders
		String authToken = getAuthJWTToken(request);
		dataSourceService.validateProject(authenticatedUserId, projectId, authToken);
		// Delete the DataSourceProject Association Details from DB
		ServiceState result = dataSourceService.deleteDataSourceAssociationDetails(authenticatedUserId,associationId);
		logger.debug("deleteDataSourceProjectAssocaition() End");
		return new ResponseEntity<ServiceState>(result, HttpStatus.OK);
	}


	private String getAuthJWTToken(HttpServletRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authToken = null;
		authToken = httpRequest.getHeader(AUTHORIZATION_HEADER_KEY);

		if (authToken == null) {
			authToken = httpRequest.getHeader(JWT_TOKEN_HEADER_KEY);
		}
		if (authToken == null) {
			authToken = request.getParameter(JWT_TOKEN_HEADER_KEY);
		}
		return authToken;
	}
}
