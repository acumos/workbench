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

package org.acumos.workbench.pipelineservice.service;

import java.util.List;

import org.acumos.workbench.common.vo.Pipeline;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.pipelineservice.exception.DuplicatePipeLineException;
import org.acumos.workbench.pipelineservice.exception.NotOwnerException;
import org.acumos.workbench.pipelineservice.exception.PipelineNotFoundException;
import org.acumos.workbench.pipelineservice.exception.TargetServiceInvocationException;

public interface PipeLineService {
	
	
	/**
	 * This method is to create PipeLine in DB using CDS Client
	 * 
	 * @param authenticatedUserId
	 * 			the authenticated user Id
	 * @param projectId
	 * 			the projectId
	 * @param pipeLine
	 * 			the pipeLine object
	 * @return PipeLine
	 * 			returns PipeLine object with additional details
	 * @throws TargetServiceInvocationException
	 * 			Throws TargetServiceInvocationException in case of any error while accessing CDS.
	 */
	public Pipeline createPipeLine(String authenticatedUserId, String projectId, Pipeline pipeLine) throws TargetServiceInvocationException;

	/**
	 * 
	 * @param authenticatedUserId
	 * 				the authenticated user Id
	 * @param pipeLine
	 * 				the pipeLine object
	 * @throws DuplicatePipeLineException 
	 */
	/**
	 * This method is to check if the pipeline already exists or not in DB using CDS client
	 * @param authenticatedUserId
	 * 		the authenticated user Id
	 * @param projectId
	 * @param pipeLine
	 */
	public void pipeLineExists(String authenticatedUserId, String projectId, Pipeline pipeLine);


	/**
	 * @param authenticatedUserId
	 * 		the authenticated user Id
	 * @param projectId
	 * 		the projectId
	 * @return
	 * 		List of Pipeline
	 */
	public List<Pipeline> getPipelines(String authenticatedUserId, String projectId);

	/**
	 * @param authenticatedUserId
	 * 		the authenticated user Id
	 * @param pipelineId
	 * 		the pipelineId
	 * @return
	 * 		true or false
	 */
	public boolean isOwnerOfPipeline(String authenticatedUserId, String pipelineId) throws NotOwnerException;

	/**
	 * @param authenticatedUserId
	 * 		the authenticated user Id
	 * @param projectId
	 * 		the projectId
	 * @param pipelineId
	 * 		the pipelineId
	 * @param pipeLine
	 * 		the pipeline details
	 * @return
	 * 		Pipeline object
	 */
	public Pipeline updatePipeline(String authenticatedUserId, String projectId, String pipelineId, Pipeline pipeLine)throws DuplicatePipeLineException;

	/**
	 * @param authenticatedUserId
	 * 		the authenticated user Id
	 * @param pipelineId
	 * 		the pipelineId
	 * @return
	 * 		Pipeline object
	 */
	public Pipeline getPipeline(String authenticatedUserId, String pipelineId) throws PipelineNotFoundException;

	/**
	 * @param pipelineId
	 * 		the pipelineId
	 * @return
	 * 		ServiceState object
	 */
	public ServiceState deletePipeline(String pipelineId);

	/**
	 * @param authenticatedUserId
	 * 		the authenticated user Id
	 * @param projectId
	 * 		the projectId
	 * @param pipelineId
	 * 		the pipelineId
	 * @param actionType
	 * 		the actionType which is archived or active 
	 * @return
	 * 		Pipeline object
	 */
	public Pipeline archivePipeline(String authenticatedUserId, String projectId, String pipelineId, String actionType);
	
	/**
	 * @param pipelineId
	 * 		the pipelineId
	 */
	public void isPipelineArchived(String pipelineId);

	/**
	 * @param projectId
	 * 		the projectId
	 * @param pipelineId
	 * 		the pipelineId
	 */
	public void isPipelineAssociatedUnderProject(String projectId, String pipelineId);


	

}
