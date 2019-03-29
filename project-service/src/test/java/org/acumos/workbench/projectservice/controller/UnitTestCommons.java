
package org.acumos.workbench.projectservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.acumos.workbench.projectservice.util.ArtifactStatus;
import org.acumos.workbench.projectservice.util.IdentifierType;
import org.acumos.workbench.projectservice.vo.ArtifactState;
import org.acumos.workbench.projectservice.vo.Identifier;
import org.acumos.workbench.projectservice.vo.Models;
import org.acumos.workbench.projectservice.vo.Notebooks;
import org.acumos.workbench.projectservice.vo.Organization;
import org.acumos.workbench.projectservice.vo.Pipelines;
import org.acumos.workbench.projectservice.vo.Project;
import org.acumos.workbench.projectservice.vo.Projects;
import org.acumos.workbench.projectservice.vo.Role;
import org.acumos.workbench.projectservice.vo.User;
import org.acumos.workbench.projectservice.vo.Version;

public abstract class UnitTestCommons {

	public UnitTestCommons() {
		super();
	}

	protected Project buildProject() {
		Project project = new Project();
		ArtifactState artifactState = new ArtifactState();
		artifactState.setStatus(ArtifactStatus.ACTIVE);
		project.setArtifactStatus(artifactState);
		project.setDescription("Project Description");
		Models models = new Models();
		project.setModels(models);
		Notebooks notebooks = new Notebooks();
		project.setNotebooks(notebooks);
		User owner = new User();
		owner.setAuthenticatedUserId("123");
		owner.setModels(models);
		owner.setNotebooks(notebooks);
		Organization organization = new Organization();
		owner.setOrganization(organization);
		Pipelines pipelines = new Pipelines();
		owner.setPipelines(pipelines);
		Role roles = new Role();
		owner.setRoles(roles);
		Identifier userId = new Identifier();
		userId.setUuid("123");
		userId.setName("TechMDev");
		userId.setServiceUrl("https://acumos.org");
		userId.setIdentifierType(IdentifierType.PIPELINE);
		owner.setUserId(userId);
		List<Project> projectList = new ArrayList<Project>();
		projectList.add(project);
		Projects projects = new Projects();
		projects.setProjects(projectList);
		owner.setProjects(projects);
		project.setOwner(owner);
		Identifier identifier = new Identifier();
		identifier.setIdentifierType(IdentifierType.PIPELINE);
		identifier.setName("IdentifierName");
		identifier.setServiceUrl("https://acumos.org");
		identifier.setRepositoryUrl("https://acumos.org");
		identifier.setUuid("123");
		Version version = new Version();
		version.setComment("comment");
		version.setLabel("Label");
		version.setTimeStamp("");
		version.setUser("123");
		identifier.setVersionId(version);
		project.setProjectId(identifier);
		return project;
	}

}