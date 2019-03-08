package org.acumos.workbench.projectservice.exception;

public class UserNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = -2805376696178365682L;

	private static final String MSG = " User not found";
	public UserNotFoundException(String authenticatedUserId) { 
		super(authenticatedUserId + MSG);
	}
}
