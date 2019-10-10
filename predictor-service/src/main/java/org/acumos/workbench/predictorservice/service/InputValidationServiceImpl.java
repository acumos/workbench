/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.workbench.predictorservice.service;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;

import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.predictorservice.lightcouch.PredictorProjectAssociation;
import org.acumos.workbench.predictorservice.utils.PredictorServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("InputValidationServiceImpl")
public class InputValidationServiceImpl implements InputValidationService {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private PredictorServiceProperties props;
	
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
	public void validateInputData(PredictorProjectAssociation predictorProjAssociation) {
		logger.debug("validateInputData() Begin");
		boolean result = false;
		if (null != predictorProjAssociation) {
			if (null != predictorProjAssociation.getPredictorName() 
					|| null != predictorProjAssociation.getEnvironmentPath()
					|| null != predictorProjAssociation.getPredictorDeploymentStatus()
					|| null != predictorProjAssociation.getSolutionId()
					|| null != predictorProjAssociation.getProjectId()
					|| null != predictorProjAssociation.getRevisionId()) {
				result = true;
			} 
			if (!predictorProjAssociation.getPredictorName().equals("")
					|| !predictorProjAssociation.getEnvironmentPath().equals("")
					|| !predictorProjAssociation.getPredictorDeploymentStatus().equals("")
					|| !predictorProjAssociation.getSolutionId().equals("")
					|| !predictorProjAssociation.getProjectId().equals("")
					|| !predictorProjAssociation.getRevisionId().equals("")) {
				result = true;
			}
		} 
		if (!result) {
			logger.error("InvalidInputJSONException occured in validateInputData()");
			throw new InvalidInputJSONException();
		}
		logger.debug("validateInputData() End");
	}

}
