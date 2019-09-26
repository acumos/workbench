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

package org.acumos.workbench.modelservice.test;

import static org.mockito.Mockito.when;

import org.acumos.workbench.modelservice.config.ApplicationConfiguration;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ApplicationConfigurationTest {
	
	@InjectMocks
	private ApplicationConfiguration applicationConfiguration;
	
	@Mock
	private ConfigurationProperties configProps;
	
	@Test
	public void loggingHandlerInterceptorTest(){
		applicationConfiguration.loggingHandlerInterceptor();
	}
	
	@Test
	public void commonDataServiceRestClientImplTest(){
		when(configProps.getCmndatasvcurl()).thenReturn("http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:8000/ccds");
		when(configProps.getCmndatasvcuser()).thenReturn("ccds_client");
		when(configProps.getCmndatasvcpwd()).thenReturn("ccds_client");
		applicationConfiguration.commonDataServiceRestClientImpl();
	}
	
	@Test
	public void projectServiceRestClientImplTest(){
		when(configProps.getProjectServiceURL()).thenReturn("http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com:9088/mlWorkbench/v1/project");
		applicationConfiguration.projectServiceRestClientImpl();
	}
	
	@Test
	public void corsConfigurerTest(){
		applicationConfiguration.corsConfigurer();
	}

}
