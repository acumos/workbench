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

package org.acumos.workbench.predictorservice.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.acumos.workbench.common.util.ArtifactStatus;
import org.acumos.workbench.common.util.ServiceStatus;
import org.acumos.workbench.common.vo.Predictor;
import org.acumos.workbench.predictorservice.exception.AssociationException;
import org.acumos.workbench.predictorservice.exception.CouchDBException;
import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.lightcouch.DataSetPredictor;
import org.acumos.workbench.predictorservice.lightcouch.PredictorProjectAssociation;
import org.acumos.workbench.predictorservice.utils.ConfigurationProperties;
import org.acumos.workbench.predictorservice.utils.PredictorServiceConstants;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CouchDBService")
public class CouchDBService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ConfigurationProperties configurationProperties;

	/**
	 * To Associate the Predictor to a Project
	 * @param association
	 * 			the PredictorProjectAssociation object
	 * @return
	 * 			returns PredictorProjectAssociation details
	 */
	public PredictorProjectAssociation savePredictorProjectAssociation(PredictorProjectAssociation association) {

		logger.debug("InsertProjectModelAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		associationExistsInCouch(association.getPredictorId(), association.getProjectId(),
				association.getRevisionId(), association.getSolutionId());

		Response response = new Response();
		try {
			association.setAssociationID(UUID.randomUUID().toString());
			association.setCreatedTimestamp(Instant.now().toString());
			association.setUpdateTimestamp(Instant.now().toString());
			association.setAssociationStatus(ArtifactStatus.ACTIVE.toString());
			response = dbClient.save(association);
			association.set_rev(response.getRev());
			logger.debug("Response Object from Couch DB  : " + response);
		} catch (CouchDBException e) {
			logger.error("Exception occured while Associating Predicotr to Project in Couch DB");
			throw new CouchDBException("Exception occured while Associating Predicotr to Project in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		logger.debug("InsertProjectModelAssociation() End");
		return association;

	}

	/**
	 * To get the Predictors for a specified Project
	 * @param projectId
	 * 			projectId as input param
	 * @return
	 * 			returns list of PredictorProjectAssociation's, If no predictors are associated then returns empty object([])
	 */
	public List<PredictorProjectAssociation> getPredictorsForProject(String projectId) {
		logger.debug("getPredictors() Begin");
		CouchDbClient dbClient = null;
		List<PredictorProjectAssociation> predictorProjAssociation = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORSQUERY,projectId,ArtifactStatus.ACTIVE.toString());
		
		try {
			dbClient = getLightCouchdbClient();
			predictorProjAssociation = dbClient.findDocs(jsonQuery, PredictorProjectAssociation.class);
		} catch (CouchDBException e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		
		logger.debug("getPredictors() End");
		return predictorProjAssociation;
	}

	
	/**
	 * To Modifies Predictor Project Association
	 * @param association
	 * 			the PredictorProjectAssociation object
	 */
	public void updatePredictorProjectAssociation(PredictorProjectAssociation association) {
		logger.debug("editAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.update(association);
		} catch (Exception e) {
			logger.error("Exception occured while updating the document in Couch DB");
			throw new CouchDBException("Exception occured while updating the document in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client", e);
			}
		}
		logger.debug("editAssociation() End");
	}
	
	
	
	/**
	 * To get the Predictor Project Association details
	 * @param associationId
	 * 			the Predictor Project Association Id
	 * @return
	 * 			returns Predictor Project Association for specified associationId
	 */
	public PredictorProjectAssociation getPredictorProjectAssocaition(String associationId) {
		logger.debug("getAssociatedModel() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		PredictorProjectAssociation association = null;
		try {
			association = dbClient.find(PredictorProjectAssociation.class, associationId);
		} catch (NoDocumentException e) {
			logger.error("Association not found in Couch DB");
			throw new AssociationException("Association not found in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		logger.debug("getAssociatedModel() End");
		return association;
	}
	
	
	/**
	 * Deletes the Predictor Project Association
	 * @param associationId
	 * 			the Association ID
	 * @param revId
	 * 			the revId
	 */
	public void deleteAssociation(String associationId, String revId) {
		logger.debug("deleteAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.remove(associationId, revId);
		} catch (Exception e) {
			logger.error("Exception occured while deleting the Document from Couch DB");
			throw new CouchDBException("Exception occured while deleting the Document from Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		logger.debug("deleteAssociation() End");
		
	}
	
	/**
	 * Checks if the Predictor exists
	 * @param predictorId
	 * 				The Predictor Id
	 * @return
	 * 				returns List of PredictorManager details
	 */
	public List<DataSetPredictor> getPredictorDetails(String predictorId) {
		logger.debug("predictorExists() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSetPredictor> predictorList = null;
		String jsonQuery = String.format(PredictorServiceConstants.PREDICTOREXISTSINCOUCHQUERY, predictorId,ArtifactStatus.ACTIVE.toString());
		try {
			predictorList = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
			// similar predictor already exists and is ACTIVE
			if (predictorList.isEmpty() || predictorList.size() == 0) {
				logger.error("Predictor is not in Active State so cannot associate to a Project");
				throw new PredictorException("Predictor is not in Active State so cannot associate to a Project");
			}
		} catch (CouchDBException e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		
		logger.debug("predictorExists() End");
		return predictorList;
	}
	
	/**
	 * Fetch the Predictor Details for the specified Model
	 * @param authenticatedUserId
	 * 			The Acumos Login Id
	 * @param modelId
	 * 			The ModelId/SolutionId
	 * @param modelRevisionId
	 * 			The SolutionRevision Id
	 * @return
	 * 			The List of DataSetPredictor
	 */
	public List<DataSetPredictor> fetchPredictorDetails(String authenticatedUserId, String modelId, String modelRevisionId) {
		logger.debug("fetchPredictorDetails() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<DataSetPredictor> dataSetPredictor = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETDATASETPREDICTORQUERY, authenticatedUserId, modelId,
				modelRevisionId);
		try {
			dataSetPredictor = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
			// similar predictor already exists and is ACTIVE
		} catch (CouchDBException e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		logger.debug("fetchPredictorDetails() End");
		
		return dataSetPredictor;
	}

	/**
	 * Save the DataSetPredictor Details in Couch DB  
	 * @param authenticatedUserId
	 * 			The Acumos Login Id
	 * @param projectId
	 * 			The ProjectId
	 * @param predictorProjAssociation
	 * 			The PredictorProjectAssociation details
	 */
	public DataSetPredictor saveDataSetPredictor(String authenticatedUserId, String projectId,
			PredictorProjectAssociation predictorProjAssociation) {
		logger.debug("saveDataSetPredictor() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		Response response = new Response();
		DataSetPredictor predictor = new DataSetPredictor();
		try {
			predictor.setPredcitorId(UUID.randomUUID().toString());
			predictor.setCreatedTimestamp(Instant.now().toString());
			predictor.setUpdateTimestamp(Instant.now().toString());
			predictor.setEnvironmentPath(predictorProjAssociation.getEnvironmentPath());
			predictor.setMetadata1(predictorProjAssociation.getMetadata1());
			predictor.setMetadata2(predictorProjAssociation.getMetadata2());
			predictor.setPredictorDescription(predictorProjAssociation.getPredictorDescription());
			predictor.setPredictorName(predictorProjAssociation.getPredictorName());
			predictor.setPredictorVersion(predictorProjAssociation.getPredictorVersion());
			predictor.setRevisionId(predictorProjAssociation.getRevisionId());
			predictor.setSolutionId(predictorProjAssociation.getSolutionId());
			predictor.setUserId(predictorProjAssociation.getUserId());
			predictor.setPredictorDeploymentStatus(ArtifactStatus.ACTIVE.toString());
			predictor.setPredictorkey(predictorProjAssociation.getPredictorkey());
			response = dbClient.save(predictor);
			logger.debug("Response Object from Couch DB  : " + response);
		} catch (CouchDBException e) {
			logger.error("Exception occured while Associating Predicotr to Project in Couch DB");
			throw new CouchDBException("Exception occured while Associating Predicotr to Project in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
		
		logger.debug("saveDataSetPredictor() End");
		return predictor;
	}
	
	/**
	 * Get Predictor details for input id
	 * 
	 * @param predictorId 
	 * 			the predictor id
	 * @return 
	 * 			returns the DataSetPredictor details else null
	 */
	
	/**
	 * Get Predictor details for input id
	 * 
	 * @param predictorId
	 * 		Predictor Id 
	 * @return DataSetPredictor
	 * 		Returns DataSetPredictor if found else throws CouchDBException
	 * @throws CouchDBException
	 * 		throws CouchDBException
	 */
	public DataSetPredictor getPredictor(String predictorId) throws CouchDBException {
		logger.debug("getPredictor() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		Response response = new Response();
		DataSetPredictor predictor = null;
		try {
			predictor = dbClient.find(DataSetPredictor.class, predictorId);
		} catch (NoDocumentException e) {
			logger.error("Exception : Predictor not found in Couch DB");
			throw new CouchDBException("Predictor not found");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client",e);
			}
		}
				
		logger.debug("getPredictor() End");
		return predictor;
	}
	
	/**
	 * Update the Predictor in CouchDB 
	 * 
	 * @param predictor
	 * 		Predictor details to be update 
	 * 
	 * @throws CouchDBException
	 * 		In case of any error throws CouchDBException
	 * 
	 */
	public void updatePredictor(DataSetPredictor predictor) throws CouchDBException {
		logger.debug("updatePredictor() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.update(predictor);
		} catch (Exception e) {
			logger.error("Exception occured while updating Predictor document in Couch DB");
			throw new CouchDBException("Exception occured while updating Predictor");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client", e);
			}
		}
		logger.debug("updatePredictor() End");
		
	}
	
	
	private void associationExistsInCouch(String predcitorID, String projectId, String revisionId, String solutionId) {
		logger.debug("associationExistsInCouch() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<PredictorProjectAssociation> association = null;
		String jsonQuery = String.format(PredictorServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY, projectId, solutionId,
				revisionId);
		try {
			association = dbClient.findDocs(jsonQuery, PredictorProjectAssociation.class);
			// similar association already exists and is ACTIVE
			if (null != association && association.size() > 0) {
				logger.error("Predictor already associated");
				throw new AssociationException("Predictor already associated");
			}
		} catch (CouchDBException e) {
			logger.error("Exception occured while finding the documents in couchDB");
			throw new CouchDBException("Exception occured while finding the documents in couchDB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("associationExistsInCouch() End");
	}
	
	private CouchDbClient getLightCouchdbClient() {
		return new CouchDbClient(configurationProperties.getCouchDbName(),
				configurationProperties.isCreateIfnotExists(), configurationProperties.getCouchdbProtocol(),
				configurationProperties.getCouchdbHost(), configurationProperties.getCouchdbPort(),
				configurationProperties.getCouchdbUser(), configurationProperties.getCouchdbPwd());
	}

	


	

	
}
