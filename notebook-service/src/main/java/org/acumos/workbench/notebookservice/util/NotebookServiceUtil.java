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

package org.acumos.workbench.notebookservice.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPUser;
import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.IdentifierType;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.ArtifactState;
import org.acumos.workbench.common.vo.Identifier;
import org.acumos.workbench.common.vo.Notebook;
import org.acumos.workbench.common.vo.ServiceState;
import org.acumos.workbench.common.vo.User;
import org.acumos.workbench.common.vo.Version;

public class NotebookServiceUtil {

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	
	public static MLPNotebook getMLPNotebook(String userId, Notebook notebook) {
		//TODO : To implement Null check for each field. 
		MLPNotebook mlpNotebook = null;
		if(null != notebook) { 
			mlpNotebook = new MLPNotebook();
			ArtifactState artifactStatus = notebook.getArtifactStatus();
			if(null != artifactStatus) { 
				if(artifactStatus.getStatus().equals(ArtifactStatus.ARCHIVED)) {
					mlpNotebook.setActive(false);
				}
			} else { 
				mlpNotebook.setActive(true);
			}
			Identifier notebookIdentifier = notebook.getNoteBookId();
			if(null != notebookIdentifier) { 
				mlpNotebook.setName(notebookIdentifier.getName());
				Version notebookVersion = notebookIdentifier.getVersionId();
				if(null != notebookVersion) { 
					mlpNotebook.setVersion(notebookVersion.getLabel());
					if(null != notebookVersion.getTimeStamp()) {
						Date parsedDate;
						try {
							parsedDate = dateFormat.parse(notebookVersion.getTimeStamp());
							Timestamp timestamp = new Timestamp(parsedDate.getTime());
							mlpNotebook.setModified(timestamp.toInstant());
						} catch (ParseException e) {
							//TODO : Log the exception 
							//TODO : Throw new appropriate Exception 
						}
						
					} else { 
						mlpNotebook.setModified(Instant.now());
					}
				}
			} else { 
				//TODO : Throw new exception indication mandatory field not available. 
			}
			mlpNotebook.setDescription(notebook.getDescription());
			mlpNotebook.setNotebookTypeCode(getNotebookTypeCode(notebook.getNotebookType()));
			if(null != notebook.getServiceStatus() && null != notebook.getServiceStatus().getStatus()) {
				mlpNotebook.setServiceStatusCode(notebook.getServiceStatus().getStatus().getServiceStatusCode());
			} else {
				mlpNotebook.setServiceStatusCode(ServiceStatus.ACTIVE.getServiceStatusCode());
			}
			mlpNotebook.setUserId(userId);
			if(notebook.getNotebookType().equalsIgnoreCase("JUPYTER")) {
				mlpNotebook.setKernelTypeCode("PY");
			}
			
		}
		return mlpNotebook;
	}

	
	public static Notebook getNotebookVO(MLPNotebook mlpnotebook, MLPUser mlpUser) {
		Notebook notebook = null;
		if(null != mlpnotebook) { 
			notebook = new Notebook();
			ArtifactState artifactStatus = new ArtifactState();
			if(mlpnotebook.isActive()) { 
				artifactStatus.setStatus(ArtifactStatus.ACTIVE);
				
			} else { 
				artifactStatus.setStatus(ArtifactStatus.ARCHIVED);
			}
			notebook.setArtifactStatus(artifactStatus);
			
			//TODO : notebook.setCollaborators(collaborators);
			//TODO : notebook.setDataSets(dataSets);
			notebook.setDescription(mlpnotebook.getDescription());
			Identifier notebookIdentifier = new Identifier();
			notebookIdentifier.setName(mlpnotebook.getName());
			notebookIdentifier.setIdentifierType(IdentifierType.NOTEBOOK);
			//TODO : notebookIdentifier.setMetrics(metrics);
			if(null != mlpnotebook.getRepositoryUrl()) {
				notebookIdentifier.setRepositoryUrl(mlpnotebook.getRepositoryUrl());
			}
			if(null != mlpnotebook.getServiceUrl()) {
				notebookIdentifier.setServiceUrl(mlpnotebook.getServiceUrl());
			}
			
			if(null != mlpnotebook.getNotebookId()) {
				notebookIdentifier.setUuid(mlpnotebook.getNotebookId());
			}
			
			Version version = new Version();
			//TODO : version.setComment(comment);
			version.setLabel(mlpnotebook.getVersion());
			Timestamp timestamp =  null;
			if(null == mlpnotebook.getModified()) { 
				timestamp = Timestamp.from(mlpnotebook.getCreated());
			} else { 
				timestamp = Timestamp.from(mlpnotebook.getModified());
			}
			version.setTimeStamp(timestamp.toString());
			version.setUser(mlpUser.getLoginName());
			notebookIdentifier.setVersionId(version);
			notebook.setNoteBookId(notebookIdentifier);
			notebook.setNotebookType(getNotebookType(mlpnotebook.getNotebookTypeCode()));
			User notebookOwner = new User();
			notebookOwner.setAuthenticatedUserId(mlpUser.getLoginName());
			//TODO : notebookOwner.setModels(models);
			//TODO : notebookOwner.setNotebooks(notebooks);
			//TODO : notebookOwner.setOrganization(organization);
			//TODO : notebookOwner.setPipelines(pipelines);
			//TODO : notebookOwner.setProjects(projects);
			//TODO : notebookOwner.setRoles(roles);
			Identifier userId = new Identifier();
			userId.setIdentifierType(IdentifierType.USER);
			userId.setName(mlpUser.getFirstName() + " " + mlpUser.getLastName());
			userId.setUuid(mlpUser.getUserId());
			notebookOwner.setUserId(userId);
			notebook.setOwner(notebookOwner);
			//TODO : notebook.setProjects(projects);
			if(null != mlpnotebook.getServiceStatusCode()) {
				ServiceState serviceStatus = new ServiceState();
				serviceStatus.setStatus(ServiceStatus.get(mlpnotebook.getServiceStatusCode()));
				notebook.setServiceStatus(serviceStatus);
			}
		}
		
		return notebook;
		
	}
	
	
	public static MLPNotebook updateMLPNotebook(MLPNotebook mlpNotebook, Notebook notebook) { 
		MLPNotebook result = mlpNotebook;
		Identifier notebookIdentifier = notebook.getNoteBookId();
		Version version = notebookIdentifier.getVersionId();
		result.setName(notebookIdentifier.getName());
		result.setDescription(notebook.getDescription());
		if(null != version && null != version.getLabel()) { 
			result.setVersion(version.getLabel());
		}
		result.setModified(Instant.now());
		return result;
	}
	
	
	public static List<Notebook> getNotebookVOs(List<MLPNotebook> mlpNotebooks, MLPUser mlpUser) { 
		List<Notebook> result = new ArrayList<Notebook>(); 
		
		for(MLPNotebook mlpNotebook : mlpNotebooks) { 
			result.add(getNotebookVO(mlpNotebook, mlpUser));
		}
		return result;
	}
	
	public static String getNotebookType(String notebookTypeCode) { 
		String notebookType = "Jupyter";
		switch(notebookTypeCode) { 
		case "JB" : 
			notebookType = "Jupyter";
			break;
			
		case "ZP" : 
			notebookType = "Zeppelin";
			break;
		}
		
		return notebookType;
	}
	
	public static String getNotebookTypeCode(String notebookType) {
		String notebookTypeCode = "JP";
		switch(notebookType.toUpperCase()) { 
		case "JUPYTER": 
			notebookTypeCode = "JP";
			break;
		case "ZEPPELIN":
			notebookTypeCode = "ZP";
			break;
		}
		return notebookTypeCode;
	}
	
}
