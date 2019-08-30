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

package org.acumos.workbench.modelservice.controller.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Projects;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Users;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;

public abstract class ModelCommons {

	protected Model buildModel() {

		Model model = new Model();
		// Set the Artifact Status
		ArtifactState artifactState = new ArtifactState();
		artifactState.setStatus(ArtifactStatus.ACTIVE);
		model.setArtifactStatus(artifactState);

		// Set the ModelId Details
		Identifier identifier = new Identifier();
		// Set the IdentifierType
		identifier.setIdentifierType(IdentifierType.MODEL);
		// Set the Metrics
		List<KVPair> kv = new ArrayList<KVPair>();
		KVPair kvPair = null;
		kvPair = new KVPair(ModelServiceConstants.ASSOCIATIONID, "01ad49de-b021-4301-b0cd-62db3e5afe70");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.CATALOGNAMES, "Cat6");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.MODELPUBLISHSTATUS, "AC");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.MODELTYPECODE, "CL");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.REVISIONID, "82d98dcf-4b41-4e32-ab14-8764b984be2f");
		kv.add(kvPair);

		KVPairs metrics = new KVPairs();
		metrics.setKv(kv);
		identifier.setMetrics(metrics);
		// Set the Name
		identifier.setName("Crosssell_528");
		// Set the repository URL
		identifier.setRepositoryUrl("");
		// Set the Service URL
		identifier.setServiceUrl("");
		// Set the UUID
		identifier.setUuid(UUID.randomUUID().toString());
		// Set the Version Details
		Version version = new Version();
		version.setComment("New Comment");
		version.setCreationTimeStamp(Instant.now().toString());
		version.setModifiedTimeStamp(Instant.now().toString());
		version.setLabel("1.0.0");
		version.setUser("techmdev");
		identifier.setVersionId(version);
		model.setModelId(identifier);

		// Set the Owner Details
		List<User> userList = new ArrayList<User>();
		User owner = new User();
		Identifier ownerIdentifier = new Identifier();
		ownerIdentifier.setUuid("ec96276f-0484-44a2-9e21-375e75a29888");
		ownerIdentifier.setName("vasu K");
		ownerIdentifier.setIdentifierType(IdentifierType.USER);
		owner.setUserId(ownerIdentifier);
		userList.add(owner);
		model.setOwner(owner);

		// Set the Project Details
		List<Project> projectList = new ArrayList<Project>();
		Project project = new Project();
		Identifier projectIdentifier = new Identifier();
		projectIdentifier.setIdentifierType(IdentifierType.PROJECT);
		projectIdentifier.setUuid("257c1ec2-c19e-473c-aa13-f0382569b591");
		project.setProjectId(projectIdentifier);
		projectList.add(project);
		Projects projects = new Projects();
		projects.setProjects(projectList);
		model.setProjects(projects);

		// Set the Service Status
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.ACTIVE);
		serviceState.setStatusMessage("Service is Active");
		model.setServiceStatus(serviceState);

		// Set the User Details

		User user = new User();
		Identifier userIdentifier = new Identifier();
		userIdentifier.setUuid("19a554b1-4b00-4135-a122-2b6061480185");
		userIdentifier.setName("TechM Dev");
		userIdentifier.setIdentifierType(IdentifierType.USER);
		user.setUserId(userIdentifier);
		user.setAuthenticatedUserId("techdmev");
		userList.add(user);
		Users users = new Users();
		users.setUsers(userList);
		model.setUsers(users);

		return model;

	}

}
