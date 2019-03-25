package org.acumos.workbench.notebookservice.service;

import org.acumos.workbench.common.vo.Project;
import org.springframework.http.ResponseEntity;

public interface ProjectServiceRestClient {

	/**
	 * Get the Project details by accessing project-service API
	 * 
	 * @param authenticatedUserId
	 * 		Acumos User login Id
	 * @param projectId
	 * 		Project Id 
	 * @return ResponseEntity<Project>
	 * 		returns ResponseEntity<Project> 
	 */
	ResponseEntity<Project> getProject(String authenticatedUserId,String projectId);
}
