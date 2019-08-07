/**
 * 
 */
package org.acumos.workbench.modelservice.service;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.List;

import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Models;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.util.ModelServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("InputValidationServiceImpl")
public class InputValidationServiceImpl implements InputValidationService{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired 
	private ModelServiceProperties props;

	@Override
	public void isValuePresent(String fieldName, String value) throws ValueNotFoundException {
		logger.debug("isValuePresent() Begin");
		boolean result = false;
		String msg = MessageFormat.format(props.getMissingFieldValue(), fieldName);
		if(null != value && !value.trim().equals("")){
			result = true;
		}
		if(!result) {
			logger.error("Mandatory field: " +  fieldName + " is missing");
			throw new ValueNotFoundException(msg);
		}
		logger.debug("isValuePresent() End");
	}

	@Override
	public void validateModelInputJson(Model model) throws InvalidInputJSONException {
		logger.debug("validateModelInputJson() Begin");
		boolean result = false;
		if (null != model) {
			if (null != model.getModelId()) {
				if (null != model.getModelId().getVersionId()) {
					Version version = model.getModelId().getVersionId();
					if (null != version.getLabel()) {
						result = true;
					}
				}
			}
		}
		if (!result) {
			logger.error("InvalidInputJSONException occured in validateProjectInputJson()");
			throw new InvalidInputJSONException();
		}
		logger.debug("validateModelInputJson() End");

	}

}
