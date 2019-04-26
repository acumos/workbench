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

package org.acumos.workbench.notebookservice;

import java.lang.invoke.MethodHandles;

import org.acumos.workbench.notebookservice.util.CACertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableAutoConfiguration
public class NotebookServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	
	public static final String CONFIG_ENV_VAR_NAME = "SPRING_APPLICATION_JSON";
	
	
	@Autowired
	CACertUtil caCertUtil;
	
	/**
	 * Starting point of ML Workbench NotebookService Application
	 * @param args
	 *            Command-line arguments
	 * @throws Exception
	 *             On failure
	 */
	public static void main(String[] args) throws Exception {
		final String springApplicationJson = System.getenv(CONFIG_ENV_VAR_NAME);
		if (springApplicationJson != null && springApplicationJson.contains("{")) {
			logger.debug("main: successfully parsed configuration from environment {}", CONFIG_ENV_VAR_NAME);
		} else {
			logger.warn("main: no configuration found in environment {}", CONFIG_ENV_VAR_NAME);
		}
		SpringApplication.run(NotebookServiceApplication.class, args);
	}
	
	/**
	 * This method will do the event handling for ContextRefreshedEvent
	 * @param event
	 * 		This method accepts event as parameter
	 */
	@EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.debug("onApplicationEvent() Begin ");
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) event.getApplicationContext();
		boolean certLoaded = caCertUtil.installCert();
		if(!certLoaded) {
			context.close();
		}
		logger.debug("onApplicationEvent() End ");
    }

}
