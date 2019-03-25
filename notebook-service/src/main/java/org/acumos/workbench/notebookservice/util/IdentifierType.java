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

package org.acumos.workbench.notebookservice.util;

public enum IdentifierType {
	  WORKBENCH(0),
	  ROLE(1),
	  USER(2),
	  ORGANIZATION(3),
	  PROJECT(4),
	  PIPELINE(5),
	  TOPIC(6),
	  NOTEBOOK(7),
	  MODEL(8),
	  PREDICTOR(9),
	  DATASOURCE(10),
	  DATASET(11),
	  PUBLISHER(12),
	  SUBSCRIBER(13),
	  COMPOSITEMODEL(14),
	  SOLUTION(15),
	  SCHEMAREGISTRY(16),
	  PROJECTTEMPLATE(17);
	  
	  private final int identifierTypeCode;
	  
	  private IdentifierType(int identifierTypeCode){
		  this.identifierTypeCode = identifierTypeCode;
	  }
	  
	  /**
	   * To return int value of Identifier Type Code. 
	   * @return int 
	   * 		return int value of Identifier Type Code. 
	   */
	  public int getIdentifierTypeCode() { 
		  return this.identifierTypeCode;
	  }
}
