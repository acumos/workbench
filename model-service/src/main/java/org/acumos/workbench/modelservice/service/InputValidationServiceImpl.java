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

package org.acumos.workbench.modelservice.service;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.List;

import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
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
				if (null != model.getModelId().getMetrics()) {
					KVPairs kvPairs = model.getModelId().getMetrics();
					List<KVPair> kvPairList = kvPairs.getKv();
					if (null != kvPairList && !kvPairList.isEmpty()) {
						for (KVPair kvPair : kvPairList) {
							if (null != kvPair.getKey() && null != kvPair.getValue() && !kvPair.getKey().equals("")
									&& !kvPair.getValue().equals("")) {
								if (kvPair.getKey().equals(ModelServiceConstants.CATALOGNAMES)) {
									result = true;
								}
								if (kvPair.getKey().equals(ModelServiceConstants.MODELPUBLISHSTATUS)) {
									result = true;
								}
								if (kvPair.getKey().equals(ModelServiceConstants.MODELTYPECODE)) {
									result = true;
								}
							} else {
								result = false;
							}
						}
					} else {
						result = false;
					}
				} else {
					result = false;
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