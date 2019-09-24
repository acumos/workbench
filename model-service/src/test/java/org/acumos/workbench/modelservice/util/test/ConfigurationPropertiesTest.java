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
import static org.mockito.Mockito.when;

import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ConfigurationPropertiesTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private ConfigurationProperties configurationProperties;
	
	@Before
    	public void setup() {
        MockitoAnnotations.initMocks(this);
    	}

	
	@Test
	public void getConfigurationPropertiesTest(){
		when(configurationProperties.getCmndatasvcpwd()).thenReturn("pwd");
		when(configurationProperties.getCmndatasvcurl()).thenReturn("CDS url");
		when(configurationProperties.getCmndatasvcuser()).thenReturn("user");
		when(configurationProperties.getCouchdbHost()).thenReturn("host");
		when(configurationProperties.getCouchDbName()).thenReturn("db name");
		when(configurationProperties.getCouchdbPort()).thenReturn(10);
		when(configurationProperties.getCouchdbProtocol()).thenReturn("protocol");
		when(configurationProperties.getCouchdbPwd()).thenReturn("pwd");
		when(configurationProperties.getCouchdbUser()).thenReturn("user");
		when(configurationProperties.isCreateIfnotExists()).thenReturn(true);
		when(configurationProperties.getJwtSecretKey()).thenReturn("secret");
		when(configurationProperties.getProjectServiceURL()).thenReturn("url");
		when(configurationProperties.getResultsetSize()).thenReturn(100);
		
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
