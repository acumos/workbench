/*-
 * ===============LICENSE_START=======================================================
 * Acumos
  * ===================================================================================
 * Copyright (C) 2017 - 2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.projectservice.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Queues implements Serializable {

	private static final long serialVersionUID = -2706498941636497320L;
	private Queue queues;

	/**
	 * @return the queues
	 */
	public Queue getQueues() {
		return queues;
	}

	/**
	 * @param queues the queues to set
	 */
	public void setQueues(Queue queues) {
		this.queues = queues;
	}
	
}