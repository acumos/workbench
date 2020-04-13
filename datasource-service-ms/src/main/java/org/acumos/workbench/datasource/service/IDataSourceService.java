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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.exception.UserNotFoundException;
import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.vo.DataSourceAssociationModel;

public interface IDataSourceService {

	/**
	 * To create the data source 
	 * @param authenticatedUserId
	 * 			The Acumos Login Id
	 * @param dataSource
	 *  		The DataSource model object
	 * @return
	 * 			returns DataSourceModel object
	 * @throws DataSourceNotFoundException
	 * 			throws DataSourceException in case of any failure
	 * @throws ClassNotFoundException
	 * 			throws ClassNotFoundException in case of any failure
	 * @throws IOException
	 * 			throws IOException in case of any failure
	 * @throws SQLException
	 * 			throws SQLException in case of any failure
	 */
	public DataSource createDataSource(String authenticatedUserId, DataSource dataSource) throws DataSourceNotFoundException, ClassNotFoundException, IOException, SQLException;

	/**
	 * To check the datasource details
	 * @param user
	 * 		The Acumos Authenticated user id
	 * @param datasource
	 * 		The DataSource model object
	 * @param dataSourceKey
	 * 		The data source key
	 * @param mode
	 * 		Mode of operation like create/ update
	 * @return
	 * 		returns success/failure response as a string 
	 * @throws DataSourceNotFoundException
	 * 		throws DataSourceException in case of any failure
	 * @throws IOException
	 * 		throws IOException in case of any failure
	 * @throws SQLException
	 * 		throws SQLException in case of any failure
	 * @throws ClassNotFoundException
	 * 		throws ClassNotFoundException in case of any failure
	 */
	public String checkDataSourceConnectionStatus(String user, DataSource datasource, String dataSourceKey, String mode)
			throws DataSourceNotFoundException, IOException, SQLException, ClassNotFoundException;
	
	
	/**
	 * To get the user details based on authenticationUserId which is the Acumos User LoginId. 
	 * @param authenticatedUserId
	 * 		The Acumos user login id.
	 * @return MLPUser
	 * 		returns the MLPUser Instance. 
	 * 
	 * @throws UserNotFoundException
	 * 		in case if user is not found throws UserNotFoundException.
	 */
	public MLPUser getUserDetails(String authenticatedUserId) throws UserNotFoundException;

	/**
	 * To Update the DataSource Details of a specific datasourcekey
	 * @param authenticatedUserId
	 * 			The Acumos user login id.
	 * @param dataSourceKey
	 * 			Updated DataSourceKey 
	 * @param dataSource
	 * 			DataSource object
	 * @return
	 * 			returns DataSource object
	 * @throws IOException
	 * 			throws IOException in case of any failure
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws DataSourceNotFoundException 
	 */
	public DataSource updateDataSourceDetails(String authenticatedUserId, String dataSourceKey,
			DataSource dataSource) throws IOException, DataSourceNotFoundException, ClassNotFoundException, SQLException;

	/**
	 * To fetch the Data Source for a particular DataSource Key
	 * @param authenticatedUserId
	 * 			The Acumos user login id.
	 * @param dataSourceKey
	 * 			The Data Source Key
	 * @return
	 * 			returns DataSource object
	 * @throws IOException
	 * 			throws IOException in case of any failure
	 */
	public DataSource fetchDataSource(String authenticatedUserId, String dataSourceKey) throws IOException;

	/**
	 * To fetch the List of DataSources, by using textSearch, namespace, category
	 * @param authenticatedUserId
	 * 			The Acumos user login id.
	 * @param namespace
	 * 			The Namespace
	 * @param category
	 * 			The Category
	 * @param textSearch
	 * 			The Text Search
	 * @return
	 * 			returns the List of DataSource Object
	 */
	public List<DataSource> fetchDataSourcesList(String authenticatedUserId, String namespace, String category,
			String textSearch);

	/**
	 * To Delete the Data Source
	 * @param authenticatedUserId
	 * 		The Acumos user login id.
	 * @param dataSourceKey
	 * 		The Data Source Key
	 * @return
	 * 		returns the ServiceState Object
	 * @throws IOException 
	 * 		throws IOException in case of any failure
	 */
	public ServiceState deleteDataSource(String authenticatedUserId, String dataSourceKey) throws IOException;

	/**
	 * To Validate the Project is exists or not
	 * @param authenticatedUserId
	 * 		The Acumos user login id.
	 * @param projectId
	 * 		The Project Id
	 * @param authToken
	 * 		The Auth Token
	 */
	public void validateProject(String authenticatedUserId, String projectId, String authToken);

	/**
	 * Associate the DataSource to a Project
	 * @param authenticatedUserId
	 * 		The Acumos User Login Id
	 * @param projectId
	 * 		The Project Id
	 * @param datasourceKey
	 * 		The DataSource Key
	 * @param dataSourceAssociationModel
	 * 		The DataSourceAssociation Model object
	 * @return
	 * 		returns DataSource Model object
	 * @throws IOException 
	 */
	public DataSourceAssociationModel linkDataSourcetoProject(String authenticatedUserId, String projectId, String datasourceKey,
			DataSource dataSourceModel) throws IOException;

	/**
	 * Get the List of DataSources which are Associated to a Project
	 * @param authenticatedUserId
	 * 		The Acumos User Login Id
	 * @param projectId
	 * 		The ProjectId
	 * @return
	 * 		Returns the List<DataSourceAssociationModel> model object
	 */
	public List<DataSourceAssociationModel> getDataSourceListAssociatedToProject(String authenticatedUserId,
			String projectId);

	/**
	 * Update the DataSourceProject Association Details
	 * @param authenticatedUserId
	 * 		The Acumos User Login Id
	 * @param projectId
	 * 		The Project Id
	 * @param associationId
	 * 		The DataSourceProject AssociationId
	 * @param datasourceKey
	 * 		The Data Source Key
	 * @param dataSource
	 * 		The Data Source object
	 * @return
	 * 		returns the DataSourceAssociationModel object
	 */
	public DataSourceAssociationModel updateAssociationDetails(String authenticatedUserId, String projectId,String associationId,
			String datasourceKey, DataSource dataSource);

	/**
	 * Delete the DataSourceProject Association Details
	 * @param authenticatedUserId
	 * 		The Acumos User Login Id
	 * @param associationId
	 * 		The DataSourceProject AssociationId
	 * @return
	 * 		returns the Service State object
	 */
	public ServiceState deleteDataSourceAssociationDetails(String authenticatedUserId, String associationId);

	/**
	 * To check the collaborator is Active user or not
	 * @param collaborators
	 * 			The Collaborators
	 */
	public void isActiveUser(Users collaborators);



}
