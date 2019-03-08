package org.acumos.workbench.projectservice.exception;

public class ProjectNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = -2957705932783242451L;

	private static final String MSG = "Requested Project Not found";
	public ProjectNotFoundException() {
		super(MSG);
	}

	
	
}
