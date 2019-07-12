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
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;

public class TestCommonUtil {

	





	/**
	 * Create Project instance with values for following fields and rest of the fields are null. 
	 * 		1. Description
	 * 		2. Owner
	 * 		3. ArtifactState
	 * 		4. ProjectId
	 * 		5. ServiceState
	 * 
	 * @return Project
	 * 		returns instance of Project with basic fields values. 
	 */
	public static Project getBasicProject() { 
		Project result = new Project();
		result = new Project(null, null, null, null, null, null, null, null, null, null, null, null, null);
		result.setDescription(TestConstants.DESCRIPTION);
		result.setOwner(getBasicUser(TestConstants.USER_ID, TestConstants.USER_NAME));
		result.setArtifactStatus(getArtifactState(ArtifactStatus.ACTIVE));
		result.setProjectId(getIdentifier(IdentifierType.PROJECT, TestConstants.PROJECT_NAME, TestConstants.PROJECT_REPO_URL, TestConstants.HTTP_LOCALHOST_URL, TestConstants.PROJECT_ID));
		result.setServiceStatus(getServiceState(ServiceStatus.COMPLETED));
		return result;
	}
	
	/**
	 * Creates User input Id and Name & rest of the properties as null. 
	 * @param Id
	 * 		UUID of User
	 * @param name
	 * 		Name of the User
	 * @return User
	 * 		returns User
	 */
	public static User getBasicUser(String Id, String name) { 
		User user = new User();
		Identifier userId = getIdentifier(IdentifierType.USER, name, null, null, Id);
		user = new User(userId,null,null,null,null,null,null, null);
		user.setAuthenticatedUserId(name);
		user.setUserId(userId);
		return user;
	}
	
	/**
	 * Construct Identifier of specified Identifier Type, with version as null.
	 * @param identifierType
	 * 		Type of Identifier to be created
	 * @param name
	 * 		Name of Identifier
	 * @param repositoryUrl
	 * 		Repository URL if required else null
	 * @param serviceUrl
	 * 		Service URL if required else null
	 * @param uuId
	 * 		UUID 
	 * @return Identifier
	 * 		return's Identifier of specified type.
	 */
	public static Identifier getIdentifier(IdentifierType identifierType, String name, String repositoryUrl, 
			String serviceUrl, String uuId){
		Identifier result = new Identifier();
		result = new Identifier(uuId, name, identifierType, null, null, repositoryUrl, serviceUrl);
		result.setVersionId(null);
		return result;
	}
	
	/**
	 * Constructs Version with timeStamp and user as null. 
	 * @param version
	 * 		Label of version 
	 * @param comment
	 * 		Comments on version
	 * @return Version
	 * 		returns Version with specified details & timeStamp and User as null.
	 */
	public static Version getVersion(String version, String comment) { 
		Version result = new Version();
		result = new Version(comment, version, null, null, null);
		//result = new Version(comment, version, null, null);
		return result;
	}
	
	/**
	 * Create ArtifactState of specified artifactStatus.
	 * @param artifactStatus
	 * 		ArtifactStatus value
	 * @return ArtifactState
	 * 		returns instance of ArtifactState
	 */
	public static ArtifactState getArtifactState(ArtifactStatus artifactStatus) { 
		return new ArtifactState(artifactStatus);
	}
	
	
	/**
	 * Create ServiceState with specified ServiceStatus and null message. 
	 * @param serviceStatus
	 * 		ServiceStatus value
	 * @return ServiceState
	 * 		returns instance of ServiceState 
	 */
	public static ServiceState getServiceState(ServiceStatus serviceStatus) { 
		return new ServiceState(serviceStatus, null);
	}
}
