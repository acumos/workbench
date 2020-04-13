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

package org.acumos.workbench.datasource.enums;

public enum CategoryTypeEnum {
	
	_FILE ("file"),
	_CASSANDRA ("cassandra"),
	_MONGO ("mongo"),
	_JDBC ("jdbc"),
	_HIVE ("hive"),
	_HIVEBATCH ("hive batch"),
	_HDFS ("hdfs"),
	_HDFSBATCH ("hdfs batch"),
	_MYSQL ("mysql"),
	_COUCH ("couch"),
	_SPARK ("Spark Standalone"),
	_SPARKYARN ("Spark on Yarn");
	
    private String categoryType;
   
    CategoryTypeEnum(String type) {
        this.categoryType = type;
    }

	public String getCategoryType() {
		return categoryType;
	}

}
