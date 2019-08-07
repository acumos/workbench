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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelServiceUtil {
	/**
	 * 
	 */
	private static final String MODEL_TYPE_CODE = "Model Type Code";
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Constructs the Model VO list from input MLPSolution List
	 * @param mlpSolutions
	 * 		MLPSolution List
	 * @param mlpUser
	 * 		MLPUser
	 * @return List<Model>
	 * 		Returns list of Model VO
	 */
	public static List<Model> getModelVOs(MLPSolution mlpSolution, List<MLPSolutionRevision> mlpSolRevisions,
			MLPUser mlpUser) {
		logger.debug("getModelVOs() Begin");
		List<Model> result = new ArrayList<Model>();
		for (MLPSolutionRevision rev : mlpSolRevisions) {
			result.add(getModelVO(mlpSolution, rev, mlpUser));
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
	public static Model getModelVO(MLPSolution mlpSolution,MLPSolutionRevision mlpSolRevision, MLPUser mlpUser) {
		logger.debug("getModelVO() Begin");
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
			String solId = mlpSolution.getSolutionId();
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
			
			KVPair kvPair = new KVPair();
			kvPair.setKey(MODEL_TYPE_CODE);
			kvPair.setValue(mlpSolution.getModelTypeCode());
			List<KVPair> kvPairList = new ArrayList<KVPair>();
			kvPairList.add(kvPair);
			KVPairs kvPairs = new KVPairs();
			kvPairs.setKv(kvPairList);
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
		logger.debug("getModelVO() End");
		return model;
		
	}

}
