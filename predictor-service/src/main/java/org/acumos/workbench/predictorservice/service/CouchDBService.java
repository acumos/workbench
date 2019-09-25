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

import org.acumos.workbench.predictorservice.exception.AssociationException;
import org.acumos.workbench.predictorservice.exception.CouchDBException;
import org.acumos.workbench.predictorservice.exception.PredictorException;
import org.acumos.workbench.predictorservice.lightcouch.PredictorManager;
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
		associationExistsInCouch(association.getPredcitorId(), association.getProjectId(),
				association.getRevisionId(), association.getSolutionId());

		Response response = new Response();
		try {
			association.setAssociationID(UUID.randomUUID().toString());
			association.setCreatedTimestamp(Instant.now().toString());
			association.setUpdateTimestamp(Instant.now().toString());
			association.setAssociationStatus("ACTIVE");
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
	public List<PredictorProjectAssociation> getPredictors(String projectId) {
		logger.debug("getPredictors() Begin");
		CouchDbClient dbClient = null;
		List<PredictorProjectAssociation> predictorProjAssociation = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORSQUERY,projectId,"ACTIVE");
		
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
	public List<PredictorManager> getPredictorDetails(String predictorId) {
		logger.debug("predictorExists() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<PredictorManager> predictorList = null;
		String jsonQuery = String.format(PredictorServiceConstants.PREDICTOREXISTSINCOUCHQUERY, predictorId,"ACTIVE");
		try {
			predictorList = dbClient.findDocs(jsonQuery, PredictorManager.class);
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
				logger.error("Association already exists in Couch DB");
				throw new AssociationException("Association already exists in Couch DB");
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
