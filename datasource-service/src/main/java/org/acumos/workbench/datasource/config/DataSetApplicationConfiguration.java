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

package org.acumos.workbench.datasource.config;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.workbench.common.logging.LoggingHandlerInterceptor;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.datasource.util.CustomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class DataSetApplicationConfiguration {
	
	@Autowired
	private CustomConfigProperties customconfigProps;
	
	@Bean
	public LoggingHandlerInterceptor loggingHandlerInterceptor() {
		return new LoggingHandlerInterceptor();
	}
	
	@Bean
	@Lazy(value = true)
	public CommonDataServiceRestClientImpl commonDataServiceRestClientImpl() {
		CommonDataServiceRestClientImpl cdsClient = (CommonDataServiceRestClientImpl) CommonDataServiceRestClientImpl
				.getInstance(customconfigProps.getCmndatasvcurl(), customconfigProps.getCmndatasvcuser(), customconfigProps.getCmndatasvcpwd());
		return cdsClient;
	}
	
	@Bean
	@Lazy(value = true) 
	public ProjectServiceRestClientImpl projectServiceRestClientImpl() {
		ProjectServiceRestClientImpl projectRestClient = new ProjectServiceRestClientImpl(customconfigProps.getProjectServiceURL());
		return projectRestClient;
	}

	@SuppressWarnings("deprecation")
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/mlWorkbench/v1/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT",
						"DELETE");
			}
		};
	}

}
