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

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PipelineProperties implements Serializable {
	
	private static final long serialVersionUID = -3398327108583051773L;
	
	@Value("${cds.create.pipeline.excp}")
	private String cdsCreatePipelineExcp;
	
	@Value("${cds.search.pipelines.excp}")
	private String cdsSearchPipelinesExcp;
	
	@Value("${cds.get.project.pipelines.excp}")
	private String cdsGetProjectPipelinesExcp;
	
	@Value("${cds.get.pipeline.excp}")
	private String cdsGetPipelineExcp;
	
	@Value("${cds.update.pipeline.excp}")
	private String cdsUpdatePipelineExcp;
	
	//@Value("${cds.get.pipeline.projects.excp}")
	//private String cdsGetPipelineProjectsExcp;
	
	@Value("${cds.drop.project.pipeline.excp}")
	private String cdsDropProjectPipelineExcp;
	
	@Value("${cds.delete.pipeline.excp}")
	private String cdsDeletePipelineExcp;
	
	@Value("${cds.add.project.pipeline.excp}")
	private String cdsAddProjectPipelineExcp;
	
	@Value("${project.service.get.project.excp}")
	private String projectServiceGetProjectExcp;

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the cdsCreatePipelineExcp
	 */
	public String getCdsCreatePipelineExcp() {
		return cdsCreatePipelineExcp;
	}

	/**
	 * @return the cdsSearchPipelinesExcp
	 */
	public String getCdsSearchPipelinesExcp() {
		return cdsSearchPipelinesExcp;
	}

	/**
	 * @return the cdsGetProjectPipelinesExcp
	 */
	public String getCdsGetProjectPipelinesExcp() {
		return cdsGetProjectPipelinesExcp;
	}

	/**
	 * @return the cdsGetPipelineExcp
	 */
	public String getCdsGetPipelineExcp() {
		return cdsGetPipelineExcp;
	}

	/**
	 * @return the cdsUpdatePipelineExcp
	 */
	public String getCdsUpdatePipelineExcp() {
		return cdsUpdatePipelineExcp;
	}

	/**
	 * @return the cdsGetPipelineProjectsExcp
	 */
	/*public String getCdsGetPipelineProjectsExcp() {
		return cdsGetPipelineProjectsExcp;
	}*/

	/**
	 * @return the cdsDropProjectPipelineExcp
	 */
	public String getCdsDropProjectPipelineExcp() {
		return cdsDropProjectPipelineExcp;
	}

	/**
	 * @return the cdsDeletePipelineExcp
	 */
	public String getCdsDeletePipelineExcp() {
		return cdsDeletePipelineExcp;
	}

	/**
	 * @return the cdsAddProjectPipelineExcp
	 */
	public String getCdsAddProjectPipelineExcp() {
		return cdsAddProjectPipelineExcp;
	}

	/**
	 * @return the projectServiceGetProjectExcp
	 */
	public String getProjectServiceGetProjectExcp() {
		return projectServiceGetProjectExcp;
	}
	
	
	
}
