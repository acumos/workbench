package org.acumos.workbench.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class ArchivedException extends RuntimeException {

	private static final long serialVersionUID = -6757216353724705L;
	
	public ArchivedException(String msg) { 
		super(msg);
	}

}
