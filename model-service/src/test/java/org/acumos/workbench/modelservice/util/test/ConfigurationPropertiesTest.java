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

package org.acumos.workbench.modelservice.util.test;

import static org.junit.Assert.assertNotNull;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ConfigurationPropertiesTest {
	
	@InjectMocks
	private ConfigurationProperties configurationProperties;
	
	@Test
	public void getConfigurationPropertiesTest(){
		assertNotNull(configurationProperties.getCmndatasvcpwd());
		assertNotNull(configurationProperties.getCmndatasvcurl());
		assertNotNull(configurationProperties.getCmndatasvcuser());
		assertNotNull(configurationProperties.getCouchdbHost());
		assertNotNull(configurationProperties.getCouchDbName());
		assertNotNull(configurationProperties.getCouchdbPort());
		assertNotNull(configurationProperties.getCouchdbProtocol());
		assertNotNull(configurationProperties.getCouchdbPwd());
		assertNotNull(configurationProperties.getCouchdbUser());
		assertNotNull(configurationProperties.isCreateIfnotExists());
		assertNotNull(configurationProperties.getJwtSecretKey());
		assertNotNull(configurationProperties.getProjectServiceURL());
		assertNotNull(configurationProperties.getResultsetSize());

		
	}

}
