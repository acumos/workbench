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

package org.acumos.workbench.modelservice.util;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelServiceProperties implements Serializable{

	private static final long serialVersionUID = -6558664852959449736L;
	
	@Value("${msg.missingFieldValue}")
	private String missingFieldValue;

	@Value("${cds.get.user.excp}")
	private String cdsGetUserExcp;
	
	@Value("${cds.find.usersolutions.excp}")
	private String cdsFindUserSolutionsExcp;
	
	@Value("${cds.get.project.modles.excp}")
	private String cdsGetProjectModelsExcp;
	
	@Value("${cds.get.solution.excp}")
	private String cdsGetSolutionException;
	
	@Value("${cds.get.solution.revision.excp}")
	private String cdsGetSolutionRevisionException;
	
	
	
	/**
	 * @return the missingFieldValue
	 */
	public String getMissingFieldValue() {
		return missingFieldValue;
	}

	/**
	 * @return the cdsGetUserExcp
	 */
	public String getCdsGetUserExcp() {
		return cdsGetUserExcp;
	}


	/**
	 * @return the cdsFindUserSolutionsExcp
	 */
	public String getCdsFindUserSolutionsExcp() {
		return cdsFindUserSolutionsExcp;
	}

	/**
	 * @return the cdsGetProjectModelsExcp
	 */
	public String getCdsGetProjectModelsExcp() {
		return cdsGetProjectModelsExcp;
	}

	/**
	 * @return the cdsGetSolutionException
	 */
	public String getCdsGetSolutionException() {
		return cdsGetSolutionException;
	}

	/**
	 * @return the cdsGetSolutionRevisionException
	 */
	public String getCdsGetSolutionRevisionException() {
		return cdsGetSolutionRevisionException;
	}

	
	

	
	

	
	
	


}