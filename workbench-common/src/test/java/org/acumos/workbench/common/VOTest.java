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
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Version;
import org.junit.Assert;
import org.junit.Test;


/**
 * TO test getter and setter of all VO classes.
 *
 */
public class VOTest {
	
	@Test
	public void testArtifactState() { 
		ArtifactState artifactState = TestCommonUtil.getArtifactState(ArtifactStatus.ACTIVE);
		Assert.assertEquals(0, artifactState.getStatus().ordinal());
		artifactState = TestCommonUtil.getArtifactState(ArtifactStatus.ARCHIVED);
		Assert.assertEquals(1, artifactState.getStatus().ordinal());
	}
	
	@Test
	public void testIdentifier() { 
		Identifier identifier = TestCommonUtil.getIdentifier(IdentifierType.PROJECT, TestConstants.PROJECT_NAME, TestConstants.PROJECT_REPO_URL,TestConstants.HTTP_LOCALHOST_URL,TestConstants.PROJECT_ID);
		Assert.assertEquals(IdentifierType.PROJECT,identifier.getIdentifierType());
		Assert.assertEquals(TestConstants.PROJECT_NAME,identifier.getName());
		Assert.assertEquals(TestConstants.PROJECT_REPO_URL,identifier.getRepositoryUrl());
		Assert.assertEquals(TestConstants.HTTP_LOCALHOST_URL,identifier.getServiceUrl());
		Assert.assertEquals(TestConstants.PROJECT_ID,identifier.getUuid());
		Version version = TestCommonUtil.getVersion(TestConstants.VERSION, TestConstants.COMMENT);
		identifier.setVersionId(version);
		Assert.assertEquals(TestConstants.VERSION,identifier.getVersionId().getLabel());
	}
	
	@Test
	public void testVersion() { 
		Version version = TestCommonUtil.getVersion(TestConstants.VERSION, TestConstants.COMMENT);
		//version.setTimeStamp(TestConstants.TIMESTAMP);
		version.setCreationTimeStamp(TestConstants.TIMESTAMP);
		version.setModifiedTimeStamp(TestConstants.TIMESTAMP);
		version.setUser(TestConstants.USER_NAME);
		Assert.assertEquals(TestConstants.VERSION,version.getLabel());
		Assert.assertEquals(TestConstants.COMMENT, version.getComment());
		Assert.assertEquals(TestConstants.TIMESTAMP, version.getCreationTimeStamp());
		Assert.assertEquals(TestConstants.TIMESTAMP, version.getModifiedTimeStamp());
		Assert.assertEquals(TestConstants.USER_NAME, TestConstants.USER_NAME);
		
	}
		
	
	@Test
	public void testProject() {
		Project project = TestCommonUtil.getBasicProject();
		Assert.assertEquals(TestConstants.DESCRIPTION, project.getDescription());
		Assert.assertEquals(TestConstants.USER_NAME, project.getOwner().getAuthenticatedUserId());
		Assert.assertEquals(TestConstants.USER_ID, project.getOwner().getUserId().getUuid());
		Assert.assertEquals(ArtifactStatus.ACTIVE, project.getArtifactStatus().getStatus());
		Assert.assertEquals(TestConstants.PROJECT_ID, project.getProjectId().getUuid());
	}
	
	
}
