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

package org.acumos.workbench.common;

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.Environment;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.PersistenceType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.junit.Assert;
import org.junit.Test;

public class EnumTest {
	
	@Test
	public void testArtifactStatus() { 
		Assert.assertEquals(0, ArtifactStatus.ACTIVE.ordinal());
		Assert.assertEquals(1, ArtifactStatus.ARCHIVED.ordinal());
	}
	
	
	@Test
	public void testEnvironment() { 
		Assert.assertEquals(0, Environment.DEVELOPMENT.ordinal());
		Assert.assertEquals(1, Environment.TEST.ordinal());
		Assert.assertEquals(2, Environment.VALIDATION.ordinal());
		Assert.assertEquals(3, Environment.STAGING.ordinal());
		Assert.assertEquals(4, Environment.PRODUCTION.ordinal());
	}
	
	@Test
	public void testIdentifierType() { 
		Assert.assertEquals(0, IdentifierType.WORKBENCH.ordinal());
		Assert.assertEquals(1, IdentifierType.ROLE.ordinal());
		Assert.assertEquals(2, IdentifierType.USER.ordinal());
		Assert.assertEquals(3, IdentifierType.ORGANIZATION.ordinal());
		Assert.assertEquals(4, IdentifierType.PROJECT.ordinal());
		Assert.assertEquals(5, IdentifierType.PIPELINE.ordinal());
		Assert.assertEquals(6, IdentifierType.TOPIC.ordinal());
		Assert.assertEquals(7, IdentifierType.NOTEBOOK.ordinal());
		Assert.assertEquals(8, IdentifierType.MODEL.ordinal());
		Assert.assertEquals(9, IdentifierType.PREDICTOR.ordinal());
		Assert.assertEquals(10, IdentifierType.DATASOURCE.ordinal());
		Assert.assertEquals(11, IdentifierType.DATASET.ordinal());
		Assert.assertEquals(12, IdentifierType.PUBLISHER.ordinal());
		Assert.assertEquals(13, IdentifierType.SUBSCRIBER.ordinal());
		Assert.assertEquals(14, IdentifierType.COMPOSITEMODEL.ordinal());
		Assert.assertEquals(15, IdentifierType.SOLUTION.ordinal());
		Assert.assertEquals(16, IdentifierType.SCHEMAREGISTRY.ordinal());
		Assert.assertEquals(17, IdentifierType.PROJECTTEMPLATE.ordinal());
	}
	
	
	@Test
	public void testPersistenceType() { 
		Assert.assertEquals(0, PersistenceType.READONLY.ordinal());
		Assert.assertEquals(1, PersistenceType.READWRITE.ordinal());
	}
	

	@Test
	public void testServiceStatus() { 
		Assert.assertEquals(TestConstants.ACTIVE_STATUS, ServiceStatus.ACTIVE.getServiceStatusCode());
		Assert.assertEquals(TestConstants.INACTIVE_STATUS, ServiceStatus.INACTIVE.getServiceStatusCode());
		Assert.assertEquals(TestConstants.FAILED_STATUS, ServiceStatus.FAILED.getServiceStatusCode());
		Assert.assertEquals(TestConstants.EXCEPTION_STATUS, ServiceStatus.EXCEPTION.getServiceStatusCode());
		Assert.assertEquals(TestConstants.INPROGRESS_STATUS, ServiceStatus.INPROGRESS.getServiceStatusCode());
		Assert.assertEquals(TestConstants.COMPLETE_STATUS, ServiceStatus.COMPLETED.getServiceStatusCode());
		Assert.assertEquals(TestConstants.ERROR_STATUS, ServiceStatus.ERROR.getServiceStatusCode());
	}
	
	
}
