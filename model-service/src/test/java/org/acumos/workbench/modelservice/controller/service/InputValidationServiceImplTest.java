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

package org.acumos.workbench.modelservice.controller.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.modelservice.controller.test.ModelCommons;
import org.acumos.workbench.modelservice.service.InputValidationServiceImpl;
import org.acumos.workbench.modelservice.util.ModelServiceProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InputValidationServiceImplTest extends ModelCommons {
	
	private static final String authenticatedUserId = "123";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private InputValidationServiceImpl inputValidationServiceImpl;
	
	@Mock
	private ModelServiceProperties props;
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = ValueNotFoundException.class)
	public void isValuePresentTest(){
		when(props.getMissingFieldValue()).thenReturn("Mandatory field: {0} is missing");
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		inputValidationServiceImpl.isValuePresent("Acumos User Id","");
	}
	
	@Test(expected = ValueNotFoundException.class)
	public void isValuePresentExceptionTest(){
		when(props.getMissingFieldValue()).thenReturn("Mandatory field: {0} is missing");
		inputValidationServiceImpl.isValuePresent("Acumos User Id",authenticatedUserId);
		inputValidationServiceImpl.isValuePresent("Acumos User Id",null);
	}
	
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonTest(){
		Model model = buildModel();
		inputValidationServiceImpl.validateModelInputJson(model);
		inputValidationServiceImpl.validateModelInputJson(null);
		
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonExceptionTest(){
		Model model = buildModel();
		model.setModelId(null);
		inputValidationServiceImpl.validateModelInputJson(model);
		
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException0Test(){
		Model model = buildModel();
		model.getModelId().setVersionId(null);
		model.getModelId().setMetrics(null);
		inputValidationServiceImpl.validateModelInputJson(model);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException2Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		model.getModelId().getMetrics().setKv(null);
		inputValidationServiceImpl.validateModelInputJson(model);
		
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException3Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		List<KVPair> list = new ArrayList<>();
		model.getModelId().getMetrics().setKv(list);
		inputValidationServiceImpl.validateModelInputJson(model);
		
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException4Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		List<KVPair> newKvPairList = new ArrayList<>();
		for(KVPair pair : kvPairList){
			pair.setKey(null);
			newKvPairList.add(pair);
		}
		model.getModelId().getMetrics().setKv(newKvPairList);
		inputValidationServiceImpl.validateModelInputJson(model);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException5Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		List<KVPair> newKvPairList = new ArrayList<>();
		for(KVPair pair : kvPairList){
			pair.setValue(null);
			newKvPairList.add(pair);
		}
		model.getModelId().getMetrics().setKv(newKvPairList);
		inputValidationServiceImpl.validateModelInputJson(model);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException6Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		List<KVPair> newKvPairList = new ArrayList<>();
		for(KVPair pair : kvPairList){
			pair.setKey("");
			newKvPairList.add(pair);
		}
		model.getModelId().getMetrics().setKv(newKvPairList);
		inputValidationServiceImpl.validateModelInputJson(model);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void validateModelInputJsonException7Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		List<KVPair> newKvPairList = new ArrayList<>();
		for(KVPair pair : kvPairList){
			pair.setValue("");
			newKvPairList.add(pair);
		}
		model.getModelId().getMetrics().setKv(newKvPairList);
		inputValidationServiceImpl.validateModelInputJson(model);
	}
	
}
