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

package org.acumos.workbench.pipelineservice.util;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeLineServiceUtil {
	
	private static final String FAILED = "FA";

	private static final String INPROGRESS = "IP";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	/**
	 * Converts PipeLine View Object input to MLPPipeLine CDS Domain Object.
	 * @param userId
	 * 			the user Id.
	 * @param pipeLine
	 * 			the pipeline object.
	 * @return MLPPipeline
	 * 			returns MLPPipeline object
	 */
	public static MLPPipeline getMLPPipeLine(String userId, Pipeline pipeLine) {
		logger.debug("getMLPPipeLine() Begin");
		MLPPipeline mlpPipeLine = null;
		if (null != pipeLine) {
			mlpPipeLine = new MLPPipeline();
			mlpPipeLine.setActive(true);

			Identifier pipeLineIdentifier = pipeLine.getPipelineId();
			if (null != pipeLineIdentifier) {
				mlpPipeLine.setName(pipeLineIdentifier.getName());
				Version pipeLineVersion = pipeLineIdentifier.getVersionId();
				Instant modifiedTime = Instant.now();
				String versionLabel = PipelineServiceConstants.DEFULAT_PIPELINE_VERSION;
				if (null != pipeLineVersion) {
					if(null != pipeLineVersion.getLabel()) {
						versionLabel = pipeLineVersion.getLabel();
					}
					if (null != pipeLineVersion.getModifiedTimeStamp()) {
						Date parsedDate;
						try {
							parsedDate = dateFormat.parse(pipeLineVersion.getModifiedTimeStamp());
							Timestamp timeStamp = new Timestamp(parsedDate.getTime());
							modifiedTime = timeStamp.toInstant();
						} catch (Exception e) {
							logger.error("Exception Occured in getMLPPipeLine() mathod",e);
						}
					} 
				} 
				mlpPipeLine.setVersion(versionLabel);
				mlpPipeLine.setModified(modifiedTime);
			} else {
				// TODO : Throw new exception indication mandatory field not available.
			}
			if (null != pipeLine.getServiceStatus() && null != pipeLine.getServiceStatus().getStatus()) {
				mlpPipeLine.setServiceStatusCode(pipeLine.getServiceStatus().getStatus().getServiceStatusCode());
			} else {
				mlpPipeLine.setServiceStatusCode(ServiceStatus.INPROGRESS.getServiceStatusCode());
			}
			mlpPipeLine.setDescription(pipeLine.getDescription());
			mlpPipeLine.setUserId(userId);
		}
		logger.debug("getMLPPipeLine() End");
		return mlpPipeLine;

	}

	/**
	 * Converts MLPPipeline CDS Domain input Object to PipeLine View Object
	 * @param responseMLPPileLine
	 * 			CDS MLPPipeline object
	 * @param mlpUser
	 * 			CDS MLPUser object
	 * @return PipeLine
	 * 			returns PipeLine object
	 */
	public static Pipeline getPipeLineVO(MLPPipeline responseMLPPileLine, MLPUser mlpUser) {
		logger.debug("getPipeLineVO() Begin");
		Pipeline result = new Pipeline();	
		Identifier userIdentifier = new Identifier();
		userIdentifier.setIdentifierType(IdentifierType.USER);
		userIdentifier.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
		userIdentifier.setUuid(mlpUser.getUserId());
		User owner = new User();
		owner.setAuthenticatedUserId(mlpUser.getLoginName());
		owner.setUserId(userIdentifier);

		Identifier pipeLineIdentifier = new Identifier();
		Version version = new Version();
		Timestamp timeStamp = null;
		if (null == responseMLPPileLine.getModified()) {
			timeStamp = Timestamp.from(responseMLPPileLine.getCreated());
			version.setModifiedTimeStamp(timeStamp.toString());
		} else {
			timeStamp = Timestamp.from(responseMLPPileLine.getModified());
			version.setModifiedTimeStamp(timeStamp.toString());
		}
		version.setCreationTimeStamp(responseMLPPileLine.getCreated().toString());
		version.setUser(responseMLPPileLine.getUserId());
		version.setLabel(responseMLPPileLine.getVersion());

		pipeLineIdentifier.setIdentifierType(IdentifierType.PIPELINE);
		pipeLineIdentifier.setUuid(responseMLPPileLine.getPipelineId());
		pipeLineIdentifier.setName(responseMLPPileLine.getName());
		pipeLineIdentifier.setRepositoryUrl(responseMLPPileLine.getRepositoryUrl());
		pipeLineIdentifier.setServiceUrl(responseMLPPileLine.getServiceUrl());
		pipeLineIdentifier.setVersionId(version);

		result.setPipelineId(pipeLineIdentifier);
		result.setDescription(responseMLPPileLine.getDescription());
		result.setOwner(owner);

		ArtifactState artifactStatus = new ArtifactState();
		if (responseMLPPileLine.isActive()) {
			artifactStatus.setStatus(ArtifactStatus.ACTIVE);
		} else {
			artifactStatus.setStatus(ArtifactStatus.ARCHIVED);
		}
		ServiceState serviceStatus = new ServiceState();
		serviceStatus.setStatus(ServiceStatus.get(responseMLPPileLine.getServiceStatusCode()));
		result.setServiceStatus(serviceStatus);
		if(responseMLPPileLine.getServiceStatusCode().equals(INPROGRESS)){
		artifactStatus.setStatus(ArtifactStatus.INPROGRESS);
		} else if(responseMLPPileLine.getServiceStatusCode().equals(FAILED)){
			artifactStatus.setStatus(ArtifactStatus.FAILED);
		}
		result.setArtifactStatus(artifactStatus);
		logger.debug("getPipeLineVO() End");
		return result;

	}

	/**
	 * Converts List<MLPPipeline> CDS Domain input Objects to List<PipeLine> View Objects
	 * @param mlpPipelines
	 * 		CDS MLPPipeline object
	 * @param mlpUser
	 * 		CDS MLPUser object
	 * @return
	 * 		returns List<Pipeline>
	 */
	public static List<Pipeline> getPipeLineVOs(List<MLPPipeline> mlpPipelines, MLPUser mlpUser) {
		logger.debug("getPipeLineVOs() Begin");
		List<Pipeline> result = new ArrayList<Pipeline>();
		for(MLPPipeline mlpPipeLine : mlpPipelines){
			result.add(getPipeLineVO(mlpPipeLine, mlpUser));
		}
		logger.debug("getPipeLineVOs() End");
		return result;
	}

	/**
	 * @param oldMLPPipeline
	 * @param pipeLine
	 * @return
	 */
	public static MLPPipeline updateMLPPipeline(MLPPipeline oldMLPPipeline, Pipeline pipeLine) {
		logger.debug("updateMLPPipeline() Begin");
		MLPPipeline result = oldMLPPipeline;
		Identifier pipelineIdentifier =  pipeLine.getPipelineId();
		Version version = pipelineIdentifier.getVersionId();
		result.setName(pipelineIdentifier.getName());
		result.setDescription(pipeLine.getDescription());
		if(null != version && null != version.getLabel()){
			result.setVersion(version.getLabel());
		}
		result.setModified(Instant.now());
		logger.debug("updateMLPPipeline() End");
		return result;
	}
	
}


