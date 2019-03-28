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

package org.acumos.workbench.util;

public enum ServiceStatus {
	ACTIVE(0), // READY FOR USE
    INACTIVE(1), // Can be Made Active
    FAILED(2), // FAILED TO START SERVICE
    EXCEPTION(3), // GETTING EXCEPTION FROM DEPENDENCIES
    INPROGRESS(4), // IN PROCESS OF STARTING
	COMPLETED(5), //REUESTED OPERATION WAS SUCCESSFULLY COMPLETED
	ERROR(6); //Service API returned an error
	
	private final int serviceStatusCode;
	
	private ServiceStatus(int serviceStatusCode) {
		this.serviceStatusCode = serviceStatusCode;
	}
	
	
	/**
	 * To return int value for ServiceStatusCode. 
	 * @return int
	 * 		return int value for ServiceStatusCode.
	 */
	public int getServiceStatusCode() { 
		return this.serviceStatusCode;
	}
}
