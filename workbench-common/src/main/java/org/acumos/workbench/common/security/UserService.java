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

package org.acumos.workbench.common.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;

public class UserService {

	private static UserService userService;
	
	private ICommonDataServiceRestClient cdsClient;
	
	private UserService(ICommonDataServiceRestClient cdsClient) {
		this.cdsClient = cdsClient;
	}
	
	public static UserService getInstance(ICommonDataServiceRestClient cdsClient) {
		if(userService == null ) {
			userService = new UserService(cdsClient);
		}
		return userService;
	}
	
	public MLPUser findUserByUsername(String userName) {
		MLPUser result = null;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("loginName",userName);
        RestPageRequest pageRequest = new RestPageRequest(0, 1);
        RestPageResponse<MLPUser> responseMLPUsers = cdsClient.searchUsers(queryParams, false, pageRequest);
        List<MLPUser> users = responseMLPUsers.getContent();
        if(null != users && users.size() > 0) {
        	result = users.get(0);
        }
		return result;
	}
}