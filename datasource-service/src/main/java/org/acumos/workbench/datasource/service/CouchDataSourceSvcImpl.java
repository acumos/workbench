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

import org.acumos.workbench.datasource.model.CouchConnectionModel;
import org.lightcouch.CouchDbClient;
import org.springframework.stereotype.Service;

@Service("CouchDataSourceSvcImpl")
public class CouchDataSourceSvcImpl implements ICouchDataSourceSvc {
	
	@Override
	public String getCouchConnectionStatus(CouchConnectionModel couchConnectionModel, String dbQuery) {
		CouchDbClient client = getLightCouchdbClient(couchConnectionModel);
		String conenctionStatus = "false";
		if(null != client) {
			conenctionStatus = "success";
		}
		return conenctionStatus;
	}
	
	private CouchDbClient getLightCouchdbClient(CouchConnectionModel couchConnectionModel) {
		return new CouchDbClient(couchConnectionModel.getDbName(),
				couchConnectionModel.isCreateIfnotExists(), couchConnectionModel.getProtocol(),
				couchConnectionModel.getHostname(), couchConnectionModel.getPort(),
				couchConnectionModel.getUsername(), couchConnectionModel.getPassword());
	}
	
}
