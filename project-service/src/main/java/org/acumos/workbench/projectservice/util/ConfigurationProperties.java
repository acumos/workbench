/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.projectservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationProperties {

	@Value("${cmndatasvc.cmndatasvcendpointurl}")
	private String cmndatasvcendpointurl;

	@Value("${cmndatasvc.cmndatasvcuser}")
	private String cmndatasvcuser;

	@Value("${cmndatasvc.cmndatasvcpwd}")
	private String cmndatasvcpwd;

	@Value("${resultsetSize}")
	private int resultsetSize;
	
	@Value("${jwt.secretkey}")
	private String jwtSecretKey;
	
	@Value("${couchdb.user}")
	private String couchdbUser;
	
	@Value("${couchdb.pwd}")
	private String couchdbPwd;

	@Value("${couchdb.name}")
	private String couchDbName;

	@Value("${couchdb.createdb.if-not-exist}")
	private boolean createIfnotExists;

	@Value("${couchdb.protocol}")
	private String couchdbProtocol;

	@Value("${couchdb.host}")
	private String couchdbHost;

	@Value("${couchdb.port}")
	private int couchdbPort;
	
	/**
	 * @return the resultsetSize
	 */
	public int getResultsetSize() {
		return resultsetSize;
	}

	/**
	 * @return the cmndatasvcendpointurl
	 */
	public String getCmndatasvcendpointurl() {
		return cmndatasvcendpointurl;
	}

	/**
	 * @return the cmndatasvcuser
	 */
	public String getCmndatasvcuser() {
		return cmndatasvcuser;
	}

	/**
	 * @return the cmndatasvcpwd
	 */
	public String getCmndatasvcpwd() {
		return cmndatasvcpwd;
	}

	/**
	 * @return the jwtSecretKey
	 */
	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

	/**
	 * @return the couchdbUser
	 */
	public String getCouchdbUser() {
		return couchdbUser;
	}

	/**
	 * @return the couchdbPwd
	 */
	public String getCouchdbPwd() {
		return couchdbPwd;
	}

	/**
	 * @return the couchDbName
	 */
	public String getCouchDbName() {
		return couchDbName;
	}

	/**
	 * @return the createIfnotExists
	 */
	public boolean isCreateIfnotExists() {
		return createIfnotExists;
	}

	/**
	 * @return the couchdbProtocol
	 */
	public String getCouchdbProtocol() {
		return couchdbProtocol;
	}

	/**
	 * @return the couchdbHost
	 */
	public String getCouchdbHost() {
		return couchdbHost;
	}

	/**
	 * @return the couchdbPort
	 */
	public int getCouchdbPort() {
		return couchdbPort;
	}

}
