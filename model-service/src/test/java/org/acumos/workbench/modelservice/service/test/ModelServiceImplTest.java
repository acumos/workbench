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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.exception.ProjectNotFoundException;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.modelservice.config.HandlerInterceptorConfiguration;
import org.acumos.workbench.modelservice.controller.test.ModelCommons;
import org.acumos.workbench.modelservice.exceptionhandling.AssociationException;
import org.acumos.workbench.modelservice.exceptionhandling.ModelNotFoundException;
import org.acumos.workbench.modelservice.lightcouch.DataSetModel;
import org.acumos.workbench.modelservice.service.CouchDBService;
import org.acumos.workbench.modelservice.service.ModelServiceImpl;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@RunWith(SpringRunner.class)
public class ModelServiceImplTest extends ModelCommons {

	private static final String authenticatedUserId = "techmdev";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private ModelServiceImpl modelServiceImpl;
	
	@Mock
	private ConfigurationProperties configProps;
	
	@Mock
    private CommonDataServiceRestClientImpl cmnDataService;
	
	@Mock
	private CouchDBService couchService;
	
	
	@Mock
	private HandlerInterceptorConfiguration handlerInterceptorConfiguration;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test(expected = NullPointerException.class)
	public void getModelsTest(){
		// TODO : Need to fix the CDS getUserDetails() method mocking, and remove the (expected = NullPointerException.class)
		String projectId = "123";
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		modelServiceImpl.getModels(authenticatedUserId, projectId);
	}
	
	@Test(expected = NullPointerException.class)
	public void getUserDetailsTest(){
		// TODO : Need to fix the CDS getUserDetails() method mocking, and remove the (expected = NullPointerException.class)
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		List<MLPUser> mlpUserList =  new ArrayList<MLPUser>();
		MLPUser mlpUser = getUserDetails();
		mlpUserList.add(mlpUser); 
		
		when(configProps.getResultsetSize()).thenReturn(10);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", mlpUser.getLoginName());
		queryParameters.put("active", true);
		Pageable pageRequest = new PageRequest(0, 10);
		RestPageRequest restPageRequets = new RestPageRequest(0,10);
		RestPageResponse<MLPUser> pageResponse = new RestPageResponse<>(mlpUserList, pageRequest, 1);
		when(cmnDataService.searchUsers(queryParameters, false, restPageRequets)).thenReturn(pageResponse);
		MLPUser response = modelServiceImpl.getUserDetails(authenticatedUserId);
	}
	
	@Test(expected = NullPointerException.class)
	public void isUserAccessibleProjectTest(){
		// TODO : Need to fix the CDS getUserDetails() method mocking, and remove the (expected = NullPointerException.class)
		String projectId = "123";
		InterceptorRegistry registry = new InterceptorRegistry();
		Mockito.doNothing().when(handlerInterceptorConfiguration).addInterceptors(registry);
		List<MLPUser> mlpUserList =  new ArrayList<MLPUser>();
		MLPUser mlpUser = getUserDetails();
		mlpUserList.add(mlpUser);
		when(configProps.getResultsetSize()).thenReturn(10);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", mlpUser.getLoginName());
		queryParameters.put("active", true);
		Pageable pageRequest = new PageRequest(0, 10);
		RestPageRequest restPageRequets = new RestPageRequest(0,10);
		RestPageResponse<MLPUser> pageResponse = new RestPageResponse<>(mlpUserList, pageRequest, 1);
		when(cmnDataService.searchUsers(queryParameters, false, restPageRequets)).thenReturn(pageResponse);
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParameters.put("projectId", projectId);
		queryParameters.put("userId", "123");
		RestPageRequest pageReq = new RestPageRequest(0, 1);
		List<MLPProject> mlpProjectList = new ArrayList<MLPProject>();
		MLPProject mlpProject = getMLPProject();
 		mlpProjectList.add(mlpProject);
		RestPageResponse<MLPProject> pageResp = new RestPageResponse<MLPProject>(mlpProjectList, pageRequest, 1);
		when(cmnDataService.searchProjects(queryParams, false, pageReq)).thenReturn(pageResp);
		modelServiceImpl.isUserAccessibleProject(authenticatedUserId, projectId);
	}
	
	
	@Test(expected = ProjectNotFoundException.class)
	public void projectExistsTest(){
		MLPProject mlpProject = getMLPProject();
		when(cmnDataService.getProject("123")).thenReturn(mlpProject);
		modelServiceImpl.projectExists("123");
		when(cmnDataService.getProject("123")).thenReturn(null);
		modelServiceImpl.projectExists("123");
	}
	
	@Test(expected = NullPointerException.class)
	public void isModelAccessibleToUserTest(){
		// TODO : Need to fix the CDS getUserDetails() method mocking, and remove the (expected = NullPointerException.class)
		List<MLPUser> mlpUserList =  new ArrayList<MLPUser>();
		MLPUser mlpUser = getUserDetails();
		mlpUserList.add(mlpUser);
		when(configProps.getResultsetSize()).thenReturn(10);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", mlpUser.getLoginName());
		queryParameters.put("active", true);
		Pageable pageRequest = new PageRequest(0, 10);
		RestPageRequest restPageRequets = new RestPageRequest(0,10);
		RestPageResponse<MLPUser> pageResponse = new RestPageResponse<MLPUser>(mlpUserList, pageRequest, 1);
		when(cmnDataService.searchUsers(queryParameters, false, restPageRequets)).thenReturn(pageResponse);
		MLPSolution mlpSolution = getMLPSolution();
		when(cmnDataService.getSolution("123")).thenReturn(mlpSolution);
		
		List<MLPCatalog> mlpCatalogList = new ArrayList<MLPCatalog>();
		MLPCatalog mlpCatalog = getMLPCatalogs();
		mlpCatalogList.add(mlpCatalog);
		when(cmnDataService.getSolutionCatalogs(mlpSolution.getSolutionId())).thenReturn(mlpCatalogList);
		modelServiceImpl.isModelAccessibleToUser("techmdev", "123");
	}
	
	@Test(expected = ModelNotFoundException.class)
	public void checkModelExistsTest(){
		MLPSolution mlpSolution = getMLPSolution();
		when(cmnDataService.getSolution("123")).thenReturn(mlpSolution);
		modelServiceImpl.checkModelExists("123");
		when(cmnDataService.getSolution("123")).thenReturn(null);
		modelServiceImpl.checkModelExists("123");
	}
	
	@Test(expected = NullPointerException.class)
	public void insertProjectModelAssociationTest(){
		Model model = buildModel();
		// TODO : Need to fix the CDS getUserDetails() method,need to mock it, and remove the (expected = NullPointerException.class)
		List<MLPUser> mlpUserList =  new ArrayList<MLPUser>();
		MLPUser mlpUser = getUserDetails();
		mlpUserList.add(mlpUser);
		when(configProps.getResultsetSize()).thenReturn(10);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("loginName", mlpUser.getLoginName());
		queryParameters.put("active", true);
		Pageable pageRequest = new PageRequest(0, 10);
		RestPageRequest restPageRequets = new RestPageRequest(0,10);
		RestPageResponse<MLPUser> pageResponse = new RestPageResponse<MLPUser>(mlpUserList, pageRequest, 1);
		when(cmnDataService.searchUsers(queryParameters, false, restPageRequets)).thenReturn(pageResponse);
		// TODO : Need to search for catalogs by calling the CDS searchCatalogs() metho, need to mock it
		MLPSolution mlpSolution = getMLPSolution();
		when(cmnDataService.getSolution("123")).thenReturn(mlpSolution);
		List<MLPSolutionRevision> mlpSolRevList = new ArrayList<MLPSolutionRevision>();
		MLPSolutionRevision mlpSolutionRevision = getMLPSolutionRevision();
		mlpSolRevList.add(mlpSolutionRevision);
		when(cmnDataService.getSolutionRevisions("123")).thenReturn(mlpSolRevList);
		// TODO : Need to Mock the CouchService.insertProjectModelAssociation() method
		modelServiceImpl.insertProjectModelAssociation("123", "123", "123", model);
	}
	
	
	@Test
	public void updateProjectModelAssociationTest() {
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				associationId = kv.getValue();
			}
		}
		List<MLPSolutionRevision> mlpSolRevList = new ArrayList<MLPSolutionRevision>();
		MLPSolutionRevision mlpSolutionRevision = getMLPSolutionRevision();
		mlpSolutionRevision.setVersion("1.0.0");
		MLPSolutionRevision rev = new MLPSolutionRevision();
		rev.setCreated(Instant.now());
		rev.setMetadata("SolRevMetaData1");
		rev.setModified(Instant.now());
		rev.setOnboarded(Instant.now());
		rev.setPublisher("techdmev");
		rev.setRevisionId("1234");
		rev.setSolutionId("123");
		rev.setSourceId("123");
		rev.setUserId("123");
		rev.setVersion("2.0.0");
		mlpSolRevList.add(rev);
		mlpSolRevList.add(mlpSolutionRevision);

		DataSetModel dataSetModel = getDataSetModel(associationId);

		when(cmnDataService.getSolutionRevisions("123")).thenReturn(mlpSolRevList);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).updateAssocaitedModel(dataSetModel);
		Model result  = modelServiceImpl.updateProjectModelAssociation("123", "123", "123", model);
		assertNotNull(result);
	}
	
	@Test
	public void updateProjectModelAssociation1Test() {
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				associationId = kv.getValue();
			}
		}
		DataSetModel dataSetModel = getDataSetModel(associationId);

		when(cmnDataService.getSolutionRevisions("123")).thenReturn(null);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).updateAssocaitedModel(dataSetModel);
		Model result  = modelServiceImpl.updateProjectModelAssociation("123", "123", "123", model);
		assertNotNull(result);
	}
	
	@Test
	public void updateProjectModelAssociation2Test() {
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				associationId = kv.getValue();
			}
		}
		List<MLPSolutionRevision> mlpSolRevList = new ArrayList<MLPSolutionRevision>();

		DataSetModel dataSetModel = getDataSetModel(associationId);

		when(cmnDataService.getSolutionRevisions("123")).thenReturn(mlpSolRevList);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).updateAssocaitedModel(dataSetModel);
		Model result  = modelServiceImpl.updateProjectModelAssociation("123", "123", "123", model);
		assertNotNull(result);
	}
	
	@Test
	public void updateProjectModelAssociation3Test() {
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				associationId = kv.getValue();
			}
		}
		List<MLPSolutionRevision> mlpSolRevList = new ArrayList<MLPSolutionRevision>();
		MLPSolutionRevision mlpSolutionRevision = getMLPSolutionRevision();
		mlpSolRevList.add(mlpSolutionRevision);
		
		DataSetModel dataSetModel = getDataSetModel(associationId);
		dataSetModel.setRevisionId("123");
		when(cmnDataService.getSolutionRevisions("123")).thenReturn(mlpSolRevList);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).updateAssocaitedModel(dataSetModel);
		Model result  = modelServiceImpl.updateProjectModelAssociation("123", "123", "123", model);
		assertNotNull(result);
	}
	
	
	@Test
	public void deleteProjectModelAssociationTest(){
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				associationId = kv.getValue();
			}
		}
		DataSetModel dataSetModel = getDataSetModel(associationId);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).deleteAssocaitedModel(dataSetModel.getAssociationID(), dataSetModel.get_rev());
		ServiceState state = modelServiceImpl.deleteProjectModelAssociation("123", "123", "123", model);
		assertNotNull(state);
	}
	
	@Test(expected = AssociationException.class)
	public void deleteProjectModelAssociationExceptionTest(){
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				kv.setValue(null);
				associationId = kv.getValue();
			}
		}
		DataSetModel dataSetModel = getDataSetModel(associationId);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).deleteAssocaitedModel(dataSetModel.getAssociationID(), dataSetModel.get_rev());
		ServiceState state = modelServiceImpl.deleteProjectModelAssociation("123", "123", "123", model);
		assertNotNull(state);
	}
	
	
	@Test(expected = AssociationException.class)
	public void deleteProjectModelAssociationException1Test(){
		Model model = buildModel();
		List<KVPair> kvPairList = model.getModelId().getMetrics().getKv();
		String associationId = null;
		for (KVPair kv : kvPairList) {
			if (kv.getKey().equals(ModelServiceConstants.ASSOCIATIONID)) {
				kv.setValue("");
				associationId = kv.getValue();
			}
		}
		DataSetModel dataSetModel = getDataSetModel(associationId);
		when(couchService.getAssociatedModel(associationId)).thenReturn(dataSetModel);
		doNothing().when(couchService).deleteAssocaitedModel(dataSetModel.getAssociationID(), dataSetModel.get_rev());
		ServiceState state = modelServiceImpl.deleteProjectModelAssociation("123", "123", "123", model);
		assertNotNull(state);
	}
	
	private MLPUser getUserDetails() {
		MLPUser mlpUser = new MLPUser();
		mlpUser.setActive(true);
		mlpUser.setCreated(Instant.now());
		mlpUser.setEmail("email@att.com");
		mlpUser.setFirstName("Mukesh");
		mlpUser.setLastName("Manthan");
		mlpUser.setLastLogin(Instant.now());
		mlpUser.setLoginName("techmdev");
		mlpUser.setLoginPassExpire(Instant.now());
		mlpUser.setMiddleName("Ratan");
		mlpUser.setModified(Instant.now());
		mlpUser.setOrgName("TechMahindra");
		mlpUser.setUserId("123");
		mlpUser.setVerifyExpiration(Instant.now());
		return mlpUser;
	}
	
	private MLPProject getMLPProject(){
		MLPProject mlpProject = new MLPProject();
		mlpProject.setActive(true);
		mlpProject.setCreated(Instant.now());
		mlpProject.setDescription("MLPProjectDescription");
		mlpProject.setModified(Instant.now());
		mlpProject.setName("MLPProject");
		mlpProject.setProjectId("123");
		mlpProject.setRepositoryUrl("https://github.com/project/service");
		mlpProject.setServiceStatusCode("ACTIVE");
		mlpProject.setUserId("123");
		mlpProject.setVersion("1.0.0");
		return mlpProject;
	}
	
	private MLPSolution getMLPSolution(){
		MLPSolution mlpSolution = new MLPSolution();
		mlpSolution.setActive(true);
		mlpSolution.setCreated(Instant.now());
		mlpSolution.setFeatured(true);
		mlpSolution.setMetadata("MetaData");
		mlpSolution.setModelTypeCode("PR");
		mlpSolution.setModified(Instant.now());
		mlpSolution.setName("MLPSolution");
		mlpSolution.setSolutionId("123");
		mlpSolution.setSourceId("source");
		mlpSolution.setToolkitTypeCode("PR");
		mlpSolution.setUserId("123");
		return mlpSolution;
		
	}
	
	private MLPCatalog getMLPCatalogs(){
		MLPCatalog mlpCatalogs = new MLPCatalog();
		mlpCatalogs.setAccessTypeCode("PR");
		mlpCatalogs.setCatalogId("123");
		mlpCatalogs.setCreated(Instant.now());
		mlpCatalogs.setDescription("Catalogs");
		mlpCatalogs.setModified(Instant.now());
		mlpCatalogs.setName("ABC");
		mlpCatalogs.setPublisher("techdmev");
		mlpCatalogs.setSelfPublish(false);
		mlpCatalogs.setUrl("https://acumos/catalogs");
		return mlpCatalogs;
		
	}
	
	private MLPSolutionRevision getMLPSolutionRevision(){
		MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();
		mlpSolRev.setCreated(Instant.now());
		mlpSolRev.setMetadata("SolRevMetaData");
		mlpSolRev.setModified(Instant.now());
		mlpSolRev.setOnboarded(Instant.now());
		mlpSolRev.setPublisher("techdmev");
		mlpSolRev.setRevisionId("123");
		mlpSolRev.setSolutionId("123");
		mlpSolRev.setSourceId("123");
		mlpSolRev.setUserId("123");
		mlpSolRev.setVersion("1.0.0");
		return mlpSolRev;
		
	}
	
	private DataSetModel getDataSetModel(String associationId) {
		DataSetModel dataSetModel = new DataSetModel();
		dataSetModel.set_rev(UUID.randomUUID().toString());
		dataSetModel.setAssociationID(associationId);
		dataSetModel.setCatalogId("123");
		dataSetModel.setCatalogName("ABC");
		dataSetModel.setCreatedTimestamp(Instant.now().toString());
		dataSetModel.setModelType("PR");
		dataSetModel.setProjectId("123");
		dataSetModel.setRevisionId("456");
		dataSetModel.setSolutionId("123");
		dataSetModel.setStatus("ACTIVE");
		dataSetModel.setUpdateTimestamp(Instant.now().toString());
		dataSetModel.setUserId("123");
		return dataSetModel;
	}
	
	

}
