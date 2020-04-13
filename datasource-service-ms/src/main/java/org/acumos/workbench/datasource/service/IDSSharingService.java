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

import java.util.List;

import org.acumos.workbench.common.vo.DataSource;
import org.acumos.workbench.common.vo.Users;

public interface IDSSharingService {
	
	/**
	 * Check the Collaborator already exists for the DataSource
	 * @param dataSourceKey
	 * 			The DataSoruce Key
	 * @param collaborators
	 * 			The DataSource Collaborators
	 */
	public void collaboratorExists(String dataSourceKey, Users collaborators);

	/**
	 * Share the DataSource to a User
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param dataSourceKey
	 * 			The DataSource Id
	 * @param collaborators
	 * 			The DataSource Collaborator Details		
	 * @return
	 * 			returns DataSource object
	 */
	public DataSource shareDataSource(String authenticatedUserId, String dataSourceKey, Users collaborators, DataSource datasource);

	/**
	 * To Check the input Collaborator can be removable or not from datasource
	 * @param dataSourceKey
	 * 			The DataSource Key
	 * @param collaborators
	 * 			The Collaborator Details
	 */
	public void isCollaboratorRemovable(String dataSourceKey, Users collaborators);

	/**
	 * To Remove the Collaborator from the Data Source
	 * @param authenticatedUserId
	 * 			The Acumos User Login Id
	 * @param dataSourceKey
	 * 			The DataSource Id
	 * @param collaborators
	 * 			The DataSource Collaborator Details
	 * @param datasource
	 * 			The DataSource to be updated
	 * @return
	 * 			The DataSource object
	 */
	public DataSource removeCollaborator(String authenticatedUserId, String dataSourceKey, Users collaborators, DataSource datasource);

	/**
	 * Get the Shared DataSources for the login user
	 * @param authenticatedUserId
	 *       	The Acumos User Login Id
	 * @return
	 * 			List<DataSource> DataSources list
	 */
	public List<DataSource> getSharedDataSources(String authenticatedUserId);

}
