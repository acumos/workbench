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

package org.acumos.workbench.modelservice.util;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.KVPair;
import org.acumos.workbench.common.vo.KVPairs;
import org.acumos.workbench.common.vo.Model;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelServiceUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String CATALOG_NAMES = "CATALOG_NAMES";
	private static final String MODEL_PUBLISH_STATUS = "MODEL_PUBLISH_STATUS";
	private static final String TOOLKIT_TYPE_CODE = "TOOLKIT_TYPE_CODE";
	private static final String MODEL_TYPE_CODE = "MODEL_TYPE_CODE";

	/**
	 * Constructs the Model VO list from input MLPSolution List
	 * @param mlpSolutions
	 * 		MLPSolution List
	 * @param mlpUser
	 * 		MLPUser
	 * @return List<Model>
	 * 		Returns list of Model VO
	 */
	public static List<Model> getModelVOs(MLPSolution mlpSolution, List<MLPSolutionRevision> mlpSolRevisions,List<MLPCatalog> mlpCatalogs,
			MLPUser mlpUser) {
		logger.debug("getModelVOs() Begin");
		List<Model> result = new ArrayList<Model>();
		for (MLPSolutionRevision rev : mlpSolRevisions) {
			result.add(getModelVO(mlpSolution, rev,mlpCatalogs, mlpUser));
		}
		logger.debug("getModelVOs() End");
		return result;
	}
	
	/**
	 * Constructs Model VO instance for input MLPModel
	 * @param mlpModel
	 * 			MLPModel instance
	 * @param mlpUser
	 * 			MLPUser instance
	 * @return Model
	 * 			Returns Model VO instance corresponding to input MLPModel, with some additional details
	 */
	public static Model getModelVO(MLPSolution mlpSolution,MLPSolutionRevision mlpSolRevision,List<MLPCatalog> mlpCatalogs, MLPUser mlpUser) {
		Model model = null;
		if(null != mlpSolution) { 
			model = new Model();
			ArtifactState artifactStatus = new ArtifactState();
			if(mlpSolution.isActive()) { 
				artifactStatus.setStatus(ArtifactStatus.ACTIVE);
			} else { 
				artifactStatus.setStatus(ArtifactStatus.ARCHIVED);
			}
			model.setArtifactStatus(artifactStatus);
			Identifier modelIdentifier = new Identifier();
			modelIdentifier.setName(mlpSolution.getName());
			modelIdentifier.setIdentifierType(IdentifierType.MODEL);
			
			if(null != mlpSolution.getSolutionId()) {
				modelIdentifier.setUuid(mlpSolution.getSolutionId());
			}
			Version version = new Version();
			version.setLabel(mlpSolRevision.getVersion());
			Timestamp timestamp =  null;
			if(null == mlpSolution.getModified()) { 
				timestamp = Timestamp.from(mlpSolution.getCreated());
				version.setModifiedTimeStamp(timestamp.toString());
			} else { 
				timestamp = Timestamp.from(mlpSolution.getModified());
				version.setModifiedTimeStamp(timestamp.toString());
			}
			version.setCreationTimeStamp(mlpSolution.getCreated().toString());
			version.setUser(mlpUser.getLoginName());
			modelIdentifier.setVersionId(version);
			KVPairs kvPairs = getKVPairDetails(mlpSolution, mlpCatalogs);
			modelIdentifier.setMetrics(kvPairs);
			
			model.setModelId(modelIdentifier);
			User modelOwner = new User();
			modelOwner.setAuthenticatedUserId(mlpUser.getLoginName());
			Identifier userId = new Identifier();
			userId.setIdentifierType(IdentifierType.USER);
			userId.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
			userId.setUuid(mlpUser.getUserId());
			modelOwner.setUserId(userId);
			model.setOwner(modelOwner);
			ServiceState serviceStatus = new ServiceState();
			serviceStatus.setStatus(ServiceStatus.ACTIVE);
			model.setServiceStatus(serviceStatus);
		}
		return model;
		
	}

	private static KVPairs getKVPairDetails(MLPSolution mlpSolution, List<MLPCatalog> mlpCatalogs) {
		// MODEL_TYPE_CODE
		KVPair modelCategory = new KVPair();
		modelCategory.setKey(MODEL_TYPE_CODE);
		if(null == mlpSolution.getModelTypeCode()){
			modelCategory.setValue("None");
		}else {
			modelCategory.setValue(mlpSolution.getModelTypeCode());
		}
		
		// TOOLKIT_TYPE_CODE
		// Need to set the solution is Model or Composite Solution 
		KVPair toolKitTypeCode = new KVPair();
		toolKitTypeCode.setKey(TOOLKIT_TYPE_CODE);
		toolKitTypeCode.setValue(mlpSolution.getToolkitTypeCode());
		// MODEL_PUBLISH_STATUS
		KVPair modelPublishStatus = new KVPair();
		modelPublishStatus.setKey(MODEL_PUBLISH_STATUS);
		// CATALOG_NAMES
		KVPair modelCatalogNames = new KVPair();
		modelCatalogNames.setKey(CATALOG_NAMES);
		
		List<String> strList = new ArrayList<>();
		// Need to get the model is published/restricted or not and club the catalog names as well.
		if(null != mlpCatalogs && !mlpCatalogs.isEmpty()){
			for(MLPCatalog catalogs : mlpCatalogs){
				String catalogName = catalogs.getName();
				strList.add(catalogName);
			}
			modelCatalogNames.setValue(StringUtils.join(strList));
			modelPublishStatus.setValue("true");
		}else {
			modelCatalogNames.setValue("None");
			modelPublishStatus.setValue("false");
		}
		List<KVPair> kvPairList = new ArrayList<KVPair>();
		kvPairList.add(modelCategory);
		kvPairList.add(modelPublishStatus);
		kvPairList.add(modelCatalogNames);
		kvPairList.add(toolKitTypeCode);
		KVPairs kvPairs = new KVPairs();
		kvPairs.setKv(kvPairList);
		return kvPairs;
	}
	
}