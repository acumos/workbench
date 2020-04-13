/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T
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

package org.acumos.workbench.common.vo;

import java.io.Serializable;
import java.util.StringTokenizer;

public class NameValue implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String value;
	
	public NameValue() {
		name = "";
		value = "";
	}
	
	public NameValue(String nameValue) { //NameValue separated by "="
		if(nameValue != null) {
			StringTokenizer st = new StringTokenizer(nameValue, "=");
			if(st.hasMoreTokens())
				this.name = st.nextToken().trim();
			if(st.hasMoreTokens())
				this.value = st.nextToken().trim();
		}
	}
	
	public NameValue(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "NameValue [name=" + name + ", value=" + value + "]";
	}
}
