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

package org.acumos.workbench.modelservice.util.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.modelservice.util.ModelServiceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ModelServiceUtilTest{
	
	@Test
	public void getModelVOTest(){
		MLPSolution mlpSolution = getMLPSolution();
		
		MLPSolutionRevision mlpSolRevision = getMLPSolutionRevision();
		MLPCatalog mlpCatalog = getMLPCatalogs();
		List<MLPCatalog> mlpCatalogs = new ArrayList<MLPCatalog>();
		mlpCatalogs.add(mlpCatalog);
		MLPUser mlpUser= getUserDetails(); 
		Model result = ModelServiceUtil.getModelVO(mlpSolution, mlpSolRevision, mlpCatalogs, mlpUser);
		assertNotNull(result);
	}
	
	@Test
	public void getModelVONullCheckTest(){
		Model result = ModelServiceUtil.getModelVO(null, null, null, null);
		assertNull(result);
	}
	
	@Test
	public void getModelVOCatalogsNullCheckTest(){
		MLPSolution mlpSolution = getMLPSolution();
		mlpSolution.setActive(false);
		mlpSolution.setSolutionId(null);
		mlpSolution.setModified(null);
		mlpSolution.setModelTypeCode(null);
		
		MLPSolutionRevision mlpSolRevision = getMLPSolutionRevision();
		MLPCatalog mlpCatalog = getMLPCatalogs();
		List<MLPCatalog> mlpCatalogs = new ArrayList<MLPCatalog>();
		mlpCatalogs.add(mlpCatalog);
		MLPUser mlpUser= getUserDetails(); 
		Model result = ModelServiceUtil.getModelVO(mlpSolution, mlpSolRevision, null, mlpUser);
		assertNotNull(result);
	}
	
	@Test
	public void getModelVOCatalogsEmptyCheckTest(){
		MLPSolution mlpSolution = getMLPSolution();
		mlpSolution.setActive(false);
		mlpSolution.setSolutionId(null);
		mlpSolution.setModified(null);
		mlpSolution.setModelTypeCode(null);
		
		MLPSolutionRevision mlpSolRevision = getMLPSolutionRevision();
		List<MLPCatalog> mlpCatalogs = new ArrayList<MLPCatalog>();
		MLPUser mlpUser= getUserDetails(); 
		Model result = ModelServiceUtil.getModelVO(mlpSolution, mlpSolRevision, mlpCatalogs, mlpUser);
		assertNotNull(result);
	}
	
	@Test
	public void getModelVOsTest(){
		MLPSolution mlpSolution = getMLPSolution();
		
		MLPSolutionRevision mlpSolRevision = getMLPSolutionRevision();
		List<MLPSolutionRevision> mlpSolRevisions = new ArrayList<MLPSolutionRevision>();
		mlpSolRevisions.add(mlpSolRevision);
		MLPCatalog mlpCatalog = getMLPCatalogs();
		List<MLPCatalog> mlpCatalogs = new ArrayList<MLPCatalog>();
		mlpCatalogs.add(mlpCatalog);
		MLPUser mlpUser= getUserDetails(); 
		ModelServiceUtil.getModelVOs(mlpSolution, mlpSolRevisions, mlpCatalogs, mlpUser);
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

}
