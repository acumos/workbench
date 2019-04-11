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

import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.util.UriComponentsBuilder;

public class NotebookServiceUtil {

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	
	/**
	 * Constructs MLPNotebook instance from input Notebook VO 
	 * @param userId
	 * 		uuId of user
	 * @param notebook
	 * 		Notebook VO instance
	 * @return MLPNotebook
	 * 		Returns MLPNotebook instance corresponding to input Notebook VO
	 */
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
			mlpNotebook.setNotebookTypeCode(NotebookType.valueOf(notebook.getNotebookType().toUpperCase()).getNotebookTypeCode());
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

	/**
	 * Constructs Notebook VO instance for input MLPNotebook
	 * @param mlpnotebook
	 * 		MLPNotebook instance
	 * @param mlpUser
	 * 		MLPUser instance
	 * @return Notebook
	 * 		Returns Notebook VO instance corresponding to input MLPNotebook, 
	 * 		with some additional details.
	 *
	 */
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
			notebook.setNotebookType(NotebookType.get(mlpnotebook.getNotebookTypeCode()).toString());
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
	
	/**
	 * Sets below values from NotebookVO into MLPNotebook, to be updated 
	 * 	1. Name
	 *  2. Version if not null
	 *  3. Description
	 *  4. Modified Date
	 *  
	 * @param mlpNotebook
	 * 		MLPNotebook instance
	 * @param notebook
	 * 		Project VO instance
	 * @return MLPNotebook
	 * 		Returns MLPNotebook with updated values.
	 */
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
	
	
	/**
	 * Constructs the Notebook VO list from input MLPNotebook List
	 * @param mlpNotebooks
	 * 		MLPNotebook List
	 * @param mlpUser
	 * 		MLPUser
	 * @return List<Notebook>
	 * 		Returns list of Notebook VO
	 */
	public static List<Notebook> getNotebookVOs(List<MLPNotebook> mlpNotebooks, MLPUser mlpUser) { 
		List<Notebook> result = new ArrayList<Notebook>(); 
		
		for(MLPNotebook mlpNotebook : mlpNotebooks) { 
			result.add(getNotebookVO(mlpNotebook, mlpUser));
		}
		return result;
	}
	
	
	/**
	 * Builds the rest API URI
	 * @param url
	 * 		URL of the rest API to be invoked.
	 * @param uriParams
	 * 		URI Path variable key-value map.  Map<String, String>
	 * @return URI
	 * 		Return UIR constructed based on the input parameters.
	 */
	public static URI buildURI(String url, Map<String, String> uriParams) { 
		URI resultURI = null;
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
		if(null != uriParams) { 
			resultURI = uriBuilder.buildAndExpand(uriParams).encode().toUri();
		} else {
			resultURI = uriBuilder.build().encode().toUri();
		}
		return resultURI;
	}
}
