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

package org.acumos.workbench.modelservice.service.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.acumos.workbench.common.exception.ArchivedException;
import org.acumos.workbench.common.exception.InvalidInputJSONException;
import org.acumos.workbench.common.exception.NotProjectOwnerException;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.exception.ValueNotFoundException;
import org.acumos.workbench.common.service.ProjectServiceRestClientImpl;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.Project;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.modelservice.controller.test.ModelCommons;
import org.acumos.workbench.modelservice.service.InputValidationServiceImpl;
import org.acumos.workbench.modelservice.service.ModelValidationServiceImpl;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ModelValidationServiceImplTest extends  ModelCommons{
	
	private static final String authenticatedUserId = "123";
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private ModelValidationServiceImpl modelValidationServiceImpl;
	
	@Mock
	private InputValidationServiceImpl inputValidationServiceImpl;
	
	@Mock
    private HttpServletRequest request;
	
	@Mock
	private ProjectServiceRestClientImpl psClient;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void validateInputDataTest(){
		Model model = buildModel();
		doNothing().when(inputValidationServiceImpl).validateModelInputJson(model);
		modelValidationServiceImpl.validateInputData(model);
	}
	
	@Test
	public void checkMandatoryFieldsTest(){
		Model model = buildModel();
		modelValidationServiceImpl.checkMandatoryFields(model);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void checkMandatoryFieldsExceptionTest(){
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		for(KVPair kv : kvPairList){
			if(kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)){
				kv.setValue(null);
				break;
			}
		}
		modelValidationServiceImpl.checkMandatoryFields(model);
	}
	
	@Test(expected = InvalidInputJSONException.class)
	public void checkMandatoryFieldsException1Test(){
		Model model = buildModel();
		model.getModelId().getVersionId().setLabel(null);
		modelValidationServiceImpl.checkMandatoryFields(model);
	}
	
	
	@Test
	public void validateProjectTest(){
		String projectId = "123";
		String authToken = "123";
		Project response = buildProject();
		ResponseEntity<Project> resposneEntity = new ResponseEntity<Project>(response, HttpStatus.OK);
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(resposneEntity);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}
	

	@Test
	public void validateProject1Test(){
		String projectId = "123";
		String authToken = "123";
		Project response = buildProject();
		ServiceState state = new ServiceState();
		state.setStatus(ServiceStatus.ERROR);
		response.setServiceStatus(state);
		ResponseEntity<Project> resposneEntity = new ResponseEntity<Project>(response, HttpStatus.OK);
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(resposneEntity);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}
	
	@Test(expected = ValueNotFoundException.class)
	public void validateProjectException1Test(){
		String projectId = "123";
		String authToken = "123";
		Project response = buildProject();
		ServiceState state = new ServiceState();
		state.setStatus(ServiceStatus.ERROR);
		response.setServiceStatus(state);
		ResponseEntity<Project> resposneEntity = new ResponseEntity<Project>(response, HttpStatus.BAD_REQUEST);
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(resposneEntity);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}
	
	
	@Test(expected = NotProjectOwnerException.class)
	public void validateProjectException2Test(){
		String projectId = "123";
		String authToken = "123";
		Project response = buildProject();
		ServiceState state = new ServiceState();
		state.setStatus(ServiceStatus.ERROR);
		response.setServiceStatus(state);
		ResponseEntity<Project> resposneEntity = new ResponseEntity<Project>(response, HttpStatus.FORBIDDEN);
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(resposneEntity);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}
	
	@Test(expected = ArchivedException.class)
	public void validateProjectException3Test(){
		String projectId = "123";
		String authToken = "123";
		Project response = buildProject();
		ArtifactState state = new ArtifactState();
		state.setStatus(ArtifactStatus.ARCHIVED);
		response.setArtifactStatus(state);
		ResponseEntity<Project> resposneEntity = new ResponseEntity<Project>(response, HttpStatus.OK);
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(resposneEntity);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}
	
	@Test(expected = ProjectNotFoundException.class)
	public void validateProjectException4Test(){
		String projectId = "123";
		String authToken = "123";
		Project response = buildProject();
		response = null;
		ResponseEntity<Project> resposneEntity = new ResponseEntity<Project>(response, HttpStatus.OK);
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(resposneEntity);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}
	
	@Test(expected = ProjectNotFoundException.class)
	public void validateProjectException5Test(){
		String projectId = "123";
		String authToken = "123";
		when(psClient.getProject(authenticatedUserId, projectId, authToken)).thenReturn(null);
		modelValidationServiceImpl.validateProject(authenticatedUserId, projectId, authToken);
		
	}

}
