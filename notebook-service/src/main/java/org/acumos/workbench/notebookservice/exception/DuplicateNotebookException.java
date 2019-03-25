package org.acumos.workbench.notebookservice.exception;

public class DuplicateNotebookException extends BadRequestException {

	private static final long serialVersionUID = -1591533159073146327L;
	
	private static final String MSG = "Notebook name and type [and version] already exists";
	
	/**
	 * To handle Duplicate Project Exception. 
	 */
	public DuplicateNotebookException() {
		super(MSG);
	}
	
}
