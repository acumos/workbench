package org.acumos.workbench.notebookservice.exception;

public class NotebookNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = -2957705932783242451L;

	private static final String MSG = "Requested Notebook Not found";
	public NotebookNotFoundException() {
		super(MSG);
	}
}
