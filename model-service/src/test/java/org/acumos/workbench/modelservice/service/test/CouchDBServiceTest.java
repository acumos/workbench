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
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.modelservice.controller.test.ModelCommons;
import org.acumos.workbench.modelservice.exceptionhandling.CouchDBException;
import org.acumos.workbench.modelservice.lightcouch.DataSetModel;
import org.acumos.workbench.modelservice.service.CouchDBService;
import org.acumos.workbench.modelservice.service.ModelServiceImpl;
import org.acumos.workbench.modelservice.util.AssociationStatus;
import org.acumos.workbench.modelservice.util.ConfigurationProperties;
import org.acumos.workbench.modelservice.util.ModelServiceConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.Response;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CouchDBServiceTest extends ModelCommons {
	
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private ModelServiceImpl modelServiceImpl;
	
	@Mock
	private ConfigurationProperties configProps;
	
	@Mock
    private CommonDataServiceRestClientImpl cmnDataService;
	
	@InjectMocks
	private CouchDBService couchService;
	
	@Mock
	private CouchDbClient couchDbClient;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	

	@Test(expected = CouchDbException.class)
	public void insertProjectModelAssociationExceptionTest() {
		Model model = buildModel();
		MLPUser mlpUser = buildMLPUSer();
		DataSetModel modelData = getDataSetModelHardCodedData();
		List<DataSetModel> dataSetModel = new ArrayList<DataSetModel>();

		when(configProps.getCouchDbName()).thenReturn("couchtestdb");
		when(configProps.getCouchdbHost()).thenReturn("acumos-deployment-techm.eastus.cloudapp.azure.com");
		when(configProps.getCouchdbPort()).thenReturn(31576);
		when(configProps.getCouchdbProtocol()).thenReturn("http");
		Response response = new Response();
		String jsonQuery = String.format(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY,
				"5bca2567-8f91-48a6-ae6d-e0589df12b3aa", "3c6d28d2-a8ea-4c0b-9ad1-482a076f2a2d",
				"4b1db3fe-228b-4389-b360-7b22c35db9be", AssociationStatus.ACTIVE.getAssociationStatusCode());
		when(couchDbClient.findDocs(jsonQuery, DataSetModel.class)).thenReturn(dataSetModel);
		when(couchDbClient.save(modelData)).thenReturn(new Response());

		DataSetModel dataModel = couchService.insertProjectModelAssociation(modelData, model, mlpUser);
		assertNotNull(dataModel);
		
	}
	
	@Test(expected = CouchDbException.class)
	public void insertProjectModelAssociationTest() {
		Model model = buildModel();
		MLPUser mlpUser = buildMLPUSer();
		DataSetModel modelData = getDataSetModel();
		List<DataSetModel> dataSetModel = new ArrayList<DataSetModel>();

		when(configProps.getCouchDbName()).thenReturn("couchtestdb");
		when(configProps.getCouchdbHost()).thenReturn("acumos-deployment-techm.eastus.cloudapp.azure.com");
		when(configProps.getCouchdbPort()).thenReturn(31576);
		when(configProps.getCouchdbProtocol()).thenReturn("http");
		Response response = new Response();
		String jsonQuery = String.format(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY,
				"1", "1",
				"1", AssociationStatus.ACTIVE.getAssociationStatusCode());
		when(couchDbClient.findDocs(jsonQuery, DataSetModel.class)).thenReturn(dataSetModel);
		when(couchDbClient.save(modelData)).thenReturn(new Response());

		DataSetModel dataModel = couchService.insertProjectModelAssociation(modelData, model, mlpUser);
		assertNotNull(dataModel);
		
	}
	
	
	@Test(expected = CouchDbException.class)
	public void insertProjectModelAssociationTest2(){
		Model model = buildModel();
		MLPUser mlpUser = buildMLPUSer();
		DataSetModel modelData = getDataSetModel();
		List<DataSetModel> dataSetModel = new ArrayList<DataSetModel>();

		when(configProps.getCouchDbName()).thenReturn("couchtestdb");
		when(configProps.getCouchdbHost()).thenReturn("acumos-deployment-techm.eastus.cloudapp.azure.com");
		when(configProps.getCouchdbPort()).thenReturn(31576);
		when(configProps.getCouchdbProtocol()).thenReturn("http");
		Response response = new Response();
		String jsonQuery = String.format(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY,
				"1", "1",
				"1", AssociationStatus.ACTIVE.getAssociationStatusCode());
		when(couchDbClient.findDocs(jsonQuery, DataSetModel.class)).thenReturn(dataSetModel);
		when(couchDbClient.save(modelData)).thenReturn(new Response());
		Model result = couchService.insertProjectModelAssociation(modelData, "Predictor", model, mlpUser);
		assertNotNull(result);
	}
	
	@Test(expected = CouchDbException.class)
	public void getModelsAssociatedToProjectTest(){
		List<DataSetModel> dataSetModel = new ArrayList<DataSetModel>();
		when(configProps.getCouchDbName()).thenReturn("couchtestdb");
		when(configProps.getCouchdbHost()).thenReturn("acumos-deployment-techm.eastus.cloudapp.azure.com");
		when(configProps.getCouchdbPort()).thenReturn(31576);
		when(configProps.getCouchdbProtocol()).thenReturn("http");
		Response response = new Response();
		String jsonQuery = String.format(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY,
				"1", "1",
				"1", AssociationStatus.ACTIVE.getAssociationStatusCode());
		
		when(couchDbClient.findDocs(jsonQuery, DataSetModel.class)).thenReturn(dataSetModel);
		dataSetModel = couchService.getModelsAssociatedToProject("techmdev", "1");
		assertNotNull(dataSetModel);
	}
	
	@Test(expected = CouchDbException.class)
	public void getModelsAssociatedToProjectExceptionTest(){
		List<DataSetModel> dataSetModel = new ArrayList<DataSetModel>();
		when(configProps.getCouchDbName()).thenReturn("couchtestdb");
		when(configProps.getCouchdbHost()).thenReturn("acumos-deployment-techm.eastus.cloudapp.azure.com");
		when(configProps.getCouchdbPort()).thenReturn(31576);
		when(configProps.getCouchdbProtocol()).thenReturn("http");
		Response response = new Response();
		String jsonQuery = String.format(ModelServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY,
				"1", "1",
				"1", AssociationStatus.ACTIVE.getAssociationStatusCode());
		
		when(couchDbClient.findDocs(jsonQuery, DataSetModel.class)).thenReturn(dataSetModel);
		dataSetModel = couchService.getModelsAssociatedToProject("techmdev", "1");
		assertNotNull(dataSetModel);
	}
	
	
	


	private DataSetModel getDataSetModelHardCodedData() {
		DataSetModel modelData = new DataSetModel();
		modelData.setAssociationID("3e153506-52e7-40b2-b991-f9ff16c1bebe");
		modelData.setCatalogId("ad9b93bb-c097-4ee5-a63c-84e3413134aa");
		modelData.setCatalogName("cat4");
		modelData.setCreatedTimestamp(Instant.now().toString());
		modelData.setModelType("CL");
		modelData.setProjectId("5bca2567-8f91-48a6-ae6d-e0589df12b3aaaa");
		modelData.setRevisionId("4b1db3fe-228b-4389-b360-7b22c35db9be");
		modelData.setSolutionId("3c6d28d2-a8ea-4c0b-9ad1-482a076f2a2d");
		modelData.setStatus("AC");
		modelData.setUpdateTimestamp(Instant.now().toString());
		modelData.setUserId("techmdev");
		return modelData;
	}
	
	private DataSetModel getDataSetModel() {
		DataSetModel modelData = new DataSetModel();
		modelData.setAssociationID(UUID.randomUUID().toString());
		modelData.setCatalogId("ad9b93bb-c097-4ee5-a63c-84e3413134aa");
		modelData.setCatalogName("cat4");
		modelData.setCreatedTimestamp(Instant.now().toString());
		modelData.setModelType("CL");
		modelData.setProjectId(UUID.randomUUID().toString());
		modelData.setRevisionId(UUID.randomUUID().toString());
		modelData.setSolutionId(UUID.randomUUID().toString());
		modelData.setStatus("AC");
		modelData.setUpdateTimestamp(Instant.now().toString());
		modelData.setUserId("techmdev");
		return modelData;
	}

}
