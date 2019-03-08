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

package org.acumos.workbench.home.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	
	@Autowired
	 private Environment env;
	
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String homepage() {
		return "index.html";
	}
	
	@RequestMapping(value="/config", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map< String, String> getConfig() {
		Map< String, String> urls = new HashMap<>();
		urls.put("dashboardComponent",env.getProperty("dashboardComponent"));
		urls.put("projectComponent",env.getProperty("projectComponent"));
		urls.put("projectCatalogComponent",env.getProperty("projectCatalogComponent"));
 		return urls;
	}
}
