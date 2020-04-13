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

import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.model.KerberosLogin;

public interface IHiveDataSourceSvc {
	
	/**
	 * Get the Connection Status with K8s Kerberos
	 * @param objKerberosLogin
	 * 		kerberos login Id
	 * @param hostName
	 * 		kerberos hostname
	 * @param port
	 * 		kerberos port
	 * @param query
	 * 		kerberos query
	 * @return
	 * 		return kerberos connection status
	 * @throws ClassNotFoundException
	 * 		throws ClassNotFoundException in case of class not available
	 * @throws SQLException
	 * 		throws SQLException in case of Invalid query
	 * @throws IOException
	 * 		throws IOException in case of doing IO stream operations
	 * @throws DataSrcException
	 * 		throws DataSrcException in case of any failure
	 */
	public String getConnectionStatusWithKerberos(KerberosLogin kerberosLogin, String hostName, String port, String query) throws ClassNotFoundException, SQLException, IOException, DataSourceNotFoundException;

}
