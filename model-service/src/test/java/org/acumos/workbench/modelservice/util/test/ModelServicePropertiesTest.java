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


package org.acumos.workbench.modelservice.util.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.acumos.workbench.modelservice.util.AssociationStatus;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.acumos.workbench.modelservice.util.ModelServiceProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ModelServicePropertiesTest {
	
	@InjectMocks
	private ModelServiceProperties modelServiceProperties;
	
	
	@Test
	public void getModelServicePropertiesTest(){
		assertNotNull(modelServiceProperties.getCdsFindUserSolutionsExcp());
		assertNotNull(modelServiceProperties.getCdsGetProjectModelsExcp());
		assertNotNull(modelServiceProperties.getCdsGetSolutionException());
		assertNotNull(modelServiceProperties.getCdsGetSolutionRevisionException());
		assertNotNull(modelServiceProperties.getCdsGetUserExcp());
		assertNotNull(modelServiceProperties.getMissingFieldValue());

		
	}
	
	@Test
	public void associationStatusEnumTest(){
		AssociationStatus status = AssociationStatus.ACTIVE;
		assertNotNull(status.getAssociationStatusCode());
	}
	
	@Test
	public void modelServiceConstantsTest(){
		assertEquals(ModelServiceConstants.MODELAUTHENTICATEDUSERID,ModelServiceConstants.MODELAUTHENTICATEDUSERID);
		assertEquals(ModelServiceConstants.DELETED,ModelServiceConstants.DELETED);
		assertEquals(ModelServiceConstants.MODELISACTIVE,ModelServiceConstants.MODELISACTIVE);
		assertEquals(ModelServiceConstants.CATALOGNAMES,ModelServiceConstants.CATALOGNAMES);
		assertEquals(ModelServiceConstants.UNARCHIVE,ModelServiceConstants.UNARCHIVE);
		assertEquals(ModelServiceConstants.ARCHIVE,ModelServiceConstants.ARCHIVE);  
		
		
		assertEquals(ModelServiceConstants.PROJECTID,ModelServiceConstants.PROJECTID);
		assertEquals(ModelServiceConstants.ASSOCIATIONID,ModelServiceConstants.ASSOCIATIONID);
		assertEquals(ModelServiceConstants.MODELTYPECODE,ModelServiceConstants.MODELTYPECODE);
		assertEquals(ModelServiceConstants.MODELPUBLISHSTATUS,ModelServiceConstants.MODELPUBLISHSTATUS);
		assertEquals(ModelServiceConstants.REVISIONID,ModelServiceConstants.REVISIONID);
		assertEquals(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY,ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY);
		assertEquals(ModelServiceConstants.GETMODELSASSOCIATEDTOPROJECTQUERY,ModelServiceConstants.GETMODELSASSOCIATEDTOPROJECTQUERY);
		assertEquals(ModelServiceConstants.GETDOCUMENTSQUERY,ModelServiceConstants.GETDOCUMENTSQUERY);
	}

}
