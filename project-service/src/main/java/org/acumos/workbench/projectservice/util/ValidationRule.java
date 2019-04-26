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

import java.util.regex.Pattern;

public enum ValidationRule {

	NAME("^[a-zA-Z][a-zA-Z0-9_ ]{5,29}$"),
	VERSION("[a-zA-Z0-9_.]{1,14}$");
	
	private final Pattern pattern; 
	
	private ValidationRule(String patternstr){
		this.pattern = Pattern.compile(patternstr);
	}
	
	/**
	 * To return regex pattern for ValidationRule. 
	 * 
	 * @return Pattern 
	 * 		returns compiled Pattern for the corresponding ValidationRule regex pattern. 
	 */
	public Pattern getPattern() { 
		return this.pattern;
	}
}
