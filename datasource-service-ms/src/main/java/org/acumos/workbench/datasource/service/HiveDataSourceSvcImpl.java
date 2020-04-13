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
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.model.KerberosLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("HiveDataSourceSvcImpl")
public class HiveDataSourceSvcImpl implements IHiveDataSourceSvc{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public String getConnectionStatusWithKerberos(KerberosLogin objKerberosLogin, String hostName, String port,
			String query) throws ClassNotFoundException, SQLException, IOException, DataSourceNotFoundException {
		logger.debug("Creating kerberos keytab for hostname " + hostName + " using principal "
				+ objKerberosLogin.getKerberosLoginUser() + " for hive connectivity testing.");
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.dir")).append(System.getProperty("file.separator"))
				.append(objKerberosLogin.getKerberosLoginUser()).append(".kerberos.keytab");
		
		
		return null;
	}

}
