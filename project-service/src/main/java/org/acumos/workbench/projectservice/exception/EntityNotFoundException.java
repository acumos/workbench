package org.acumos.workbench.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException  extends RuntimeException {

	private static final long serialVersionUID = 3173826165751170837L;
	
	public EntityNotFoundException(String msg) { 
		super(msg);
	}

}
