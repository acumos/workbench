package org.acumos.workbench.predictorservice.utils;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PredicotrServiceProperties implements Serializable {

	private static final long serialVersionUID = -5573486748325618231L;

	@Value("${msg.missingFieldValue}")
	private String missingFieldValue;
	

	public String getMissingFieldValue() {
		return missingFieldValue;
	}

	
	
}
