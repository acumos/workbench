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

package org.acumos.workbench.projectservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class ProjectServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceApplication.class);
	
	public static final String CONFIG_ENV_VAR_NAME = "SPRING_APPLICATION_JSON";
	
	/**
	 * @param args
	 *            Command-line arguments
	 * @throws Exception
	 *             On failure
	 */
	public static void main(String[] args) throws Exception {
		final String springApplicationJson = System.getenv(CONFIG_ENV_VAR_NAME);
		//TODO : Add springApplicationJson validation code and log the error if any.
		logger.debug("main: successfully parsed configuration from environment {}", CONFIG_ENV_VAR_NAME);
		SpringApplication.run(ProjectServiceApplication.class, args);
	}
}
