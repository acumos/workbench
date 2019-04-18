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

package org.acumos.workbench.notebookservice.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Models;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.Organization;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.Projects;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;

public abstract class NotebookCommons {
	
	protected Notebook buildNotebook(){
		
		Notebook notebook = new Notebook();
		
		ArtifactState artiState = new ArtifactState();
		artiState.setStatus(ArtifactStatus.ACTIVE);
		
		notebook.setArtifactStatus(artiState);
		
		Identifier identifier = new Identifier();
		identifier.setIdentifierType(IdentifierType.NOTEBOOK);
		identifier.setName("IdentifierName");
		identifier.setServiceUrl("https://acumos.org");
		identifier.setRepositoryUrl("https://acumos.org");
		identifier.setUuid("123");
		Version version = new Version();
		version.setComment("comment");
		version.setLabel("Label");
		version.setTimeStamp(Instant.now().toString());
		version.setUser("123");
		identifier.setVersionId(version);
		notebook.setNoteBookId(identifier);
		notebook.setDescription("NotebookDescription");
		notebook.setKernelType("x86");
		notebook.setNotebookType("JUPYTER");
		
		User owner = new User();
		owner.setAuthenticatedUserId("123");
		Models models = new Models();
		owner.setModels(models);
		Organization organization = new Organization();
		owner.setOrganization(organization);
		notebook.setOwner(owner);
		Projects projects = new Projects();
		Project project = new Project();
		List<Project> projectList = new ArrayList<>();
		projectList.add(project);
		projects.setProjects(projectList);
		notebook.setProjects(projects);
		
		return notebook;
		
	}
	
	

}
