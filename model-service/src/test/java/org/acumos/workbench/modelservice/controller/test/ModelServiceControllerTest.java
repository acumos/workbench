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

package org.acumos.workbench.modelservice.controller.test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.Version;
import org.acumos.workbench.modelservice.controller.ModelServiceController;
import org.acumos.workbench.modelservice.service.InputValidationService;
import org.acumos.workbench.modelservice.service.ModelService;
import org.acumos.workbench.modelservice.service.ModelValidationService;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.ResponseEntity;

public class ModelServiceControllerTest extends ModelCommons{
	
	private static final String authenticatedUserId = "techdmev";
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private ModelServiceController modelServiceController;
	
	@Mock
    private HttpServletRequest request;
	
	@Mock
	private InputValidationService inputValidationService;
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private ModelValidationService modelValidationService;
	
	
	@Before
	public void setUp() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void listModelsTest(){
		List<Model> modelList = new ArrayList<Model>();
		Model model = buildModel();
		modelList.add(model);
		doNothing().when(inputValidationService).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		when(modelService.getModels(authenticatedUserId, null)).thenReturn(modelList);
		ResponseEntity<?> result = modelServiceController.listModels(request, authenticatedUserId);
		assertNotNull(result);
	}
	
	@Test
	public void listModelsAssociatedToProjectTest(){
		String projectId = "257c1ec2-c19e-473c-aa13-f0382569b591";
		List<Model> modelList = new ArrayList<Model>();
		Model model = buildModel();
		modelList.add(model);
		when(request.getHeader("Authorization")).thenReturn("123");
		doNothing().when(inputValidationService).isValuePresent("AuthenticatedUserId", authenticatedUserId);
		doNothing().when(inputValidationService).isValuePresent("projectId", projectId);
		doNothing().when(modelValidationService).validateProject("AuthenticatedUserId", projectId, "123");
		when(modelService.getModels(authenticatedUserId, projectId)).thenReturn(modelList);
		ResponseEntity<?> result = modelServiceController.listModelsAssociatedToProject(request, authenticatedUserId,projectId);
		assertNotNull(result);
	}
	
	@Test
	public void associateModeltoProjectTest(){
		String projectId = "257c1ec2-c19e-473c-aa13-f0382569b591";
		String modelId = "5f7be6cb-5cba-43c9-899a-091d99288e46";
		Model model = buildModel();
		//TODO :  Need to modify the project and model association status and _id and rev_id and revisionId and projectId in the model obj
		Model inputModel = new Model();
		Identifier id = new Identifier();
		Version version = new Version();
		version.setLabel("1");
		id.setVersionId(version);
		List<KVPair> kv = new ArrayList<KVPair>();
		KVPair kvPair = null;
		kvPair = new KVPair(ModelServiceConstants.MODELTYPECODE, "CL");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.MODELPUBLISHSTATUS, "AC");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.CATALOGNAMES, "Cat6");
		kv.add(kvPair);

		KVPairs metrics = new KVPairs();
		metrics.setKv(kv);
		id.setMetrics(metrics);
		inputModel.setModelId(id);
		doNothing().when(modelValidationService).validateInputData(inputModel);
		when(request.getHeader("jwtToken")).thenReturn("123");
		doNothing().when(modelValidationService).validateProject("AuthenticatedUserId", projectId, "123");
		when(modelService.insertProjectModelAssociation(authenticatedUserId, projectId, modelId, inputModel)).thenReturn(model);
		ResponseEntity<?> result = modelServiceController.associateModeltoProject(request, authenticatedUserId, projectId, modelId, inputModel);
		assertNotNull(result);
	}
	
	@Test
	public void updateModelAssociationWithProjectTest(){
		String projectId = "257c1ec2-c19e-473c-aa13-f0382569b591";
		String modelId = "5f7be6cb-5cba-43c9-899a-091d99288e46";
		Model model = buildModel();
		Model inputModel = new Model();
		Identifier id = new Identifier();
		Version version = new Version();
		version.setLabel("1");
		id.setVersionId(version);
		List<KVPair> kv = new ArrayList<KVPair>();
		KVPair kvPair = null;
		kvPair = new KVPair(ModelServiceConstants.MODELTYPECODE, "CL");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.MODELPUBLISHSTATUS, "AC");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.CATALOGNAMES, "Cat6");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.ASSOCIATIONID, "07766e52-f349-4740-9cff-ae89d89c14af");
		kv.add(kvPair);
		
		KVPairs metrics = new KVPairs();
		metrics.setKv(kv);
		id.setMetrics(metrics);
		inputModel.setModelId(id);
		
		doNothing().when(modelValidationService).validateInputData(inputModel);
		doNothing().when(modelValidationService).checkMandatoryFields(inputModel);
		when(request.getParameter("jwtToken")).thenReturn("123");
		doNothing().when(modelValidationService).validateProject("AuthenticatedUserId", projectId, "123");
		when(modelService.isModelAccessibleToUser(projectId, modelId)).thenReturn(true);
		when(modelService.updateProjectModelAssociation(authenticatedUserId, projectId, modelId, inputModel)).thenReturn(model);
		ResponseEntity<?> result = modelServiceController.updateModelAssociationWithProject(request, authenticatedUserId, projectId, modelId, inputModel);
		assertNotNull(result);
	}
	
	@Test
	public void deleteModelAssociationWithProjectTest(){
		String projectId = "257c1ec2-c19e-473c-aa13-f0382569b591";
		String modelId = "5f7be6cb-5cba-43c9-899a-091d99288e46";
		Model inputModel = new Model();
		Identifier id = new Identifier();
		Version version = new Version();
		version.setLabel("1");
		id.setVersionId(version);
		List<KVPair> kv = new ArrayList<KVPair>();
		KVPair kvPair = null;
		kvPair = new KVPair(ModelServiceConstants.MODELTYPECODE, "CL");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.MODELPUBLISHSTATUS, "AC");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.CATALOGNAMES, "Cat6");
		kv.add(kvPair);
		kvPair = new KVPair(ModelServiceConstants.ASSOCIATIONID, "07766e52-f349-4740-9cff-ae89d89c14af");
		kv.add(kvPair);
		
		KVPairs metrics = new KVPairs();
		metrics.setKv(kv);
		id.setMetrics(metrics);
		inputModel.setModelId(id);
		
		ServiceState serviceState = new ServiceState();
		serviceState.setStatus(ServiceStatus.COMPLETED);
		serviceState.setStatusMessage("Association Deleted Successfully");
		
		when(request.getHeader("Authorization")).thenReturn("123");
		when(request.getHeader("jwtToken")).thenReturn("123");
		when(request.getParameter("jwtToken")).thenReturn("123");
		
		doNothing().when(modelValidationService).validateInputData(inputModel);
		doNothing().when(modelValidationService).checkMandatoryFields(inputModel);
		doNothing().when(modelValidationService).validateProject("AuthenticatedUserId", projectId, "123");
		when(modelService.isModelAccessibleToUser(projectId, modelId)).thenReturn(true);
		when(modelService.deleteProjectModelAssociation(authenticatedUserId, projectId, modelId, inputModel)).thenReturn(serviceState);
		ResponseEntity<?> result = modelServiceController.deleteModelAssociationWithProject(request,authenticatedUserId, projectId, modelId, inputModel);
		assertNotNull(result);
	}
	
	@Test
	public void getAuthJWTTokenTest(){
		when(request.getHeader("Authorization")).thenReturn("123");
	}
}
