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
	 * @param projectId 
	 * 			The Project Id
	 * @param predictorId 
	 * 			The Predictor Id
	 * @param authenticatedUserId 
	 * 			The Acumos User Login Id
	 * @param association
	 * 			PredictorProjectAssociation VO
	 * @return
	 * 			returns PredictorProjectAssociation VO
	 */
	public PredictorProjectAssociation associatePredictorToProject(String authenticatedUserId, String predictorId, String projectId, PredictorProjectAssociation association) {

		logger.debug("InsertProjectModelAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		associationExistsInCouch(predictorId, projectId,
				association.getRevisionId(), association.getSolutionId());

		Response response = new Response();
		try {
			association.setAssociationID(UUID.randomUUID().toString());
			association.setCreatedTimestamp(Instant.now().toString());
			association.setUpdateTimestamp(Instant.now().toString());
			association.setAssociationStatus("ACTIVE");
			association.setPredcitorId(predictorId);
			association.setProjectId(projectId);
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
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("InsertProjectModelAssociation() End");
		return association;

	}

	/**
	 * To get the Predictors for a specified Project
	 * @param projectId
	 * 			projectid as input param
	 * @return
	 * 			returns List<PredictorProjectAssociation>
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
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		
		logger.debug("getPredictors() End");
		return predictorProjAssociation;
	}

	
	/**
	 * To modify the Predictor Association to a Project 
	 * @param authenticatedUserId
	 * 				the acumos login id
	 * @param predictorProjAssociation
	 * 				the PredictorProjectAssociation VO
	 * @return
	 * 				returns the PredictorProjectAssociation VO
	 */
	public void editAssociation(PredictorProjectAssociation association) {
		logger.debug("editAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		//String jsonQuery = String.format(PredictorServiceConstants.EDITPREDICTORASSOCIATIONTOPROJECTQUERY, association.getPredictorName(), association.getPredcitorId(),
		//		association.getEnvironmentPath(),association.getPredictorVersion());
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
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("editAssociation() End");
	}
	
	
	
	/**
	 * To get the Associated Predictor details
	 * @param associationID
	 * 			the Predictor Project Association Id
	 * @return
	 * 			returns PredictorProjectAssociation VO
	 */
	public PredictorProjectAssociation getAssociatedPredictor(String associationID) {
		logger.debug("getAssociatedModel() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		PredictorProjectAssociation association = null;
		try {
			association = dbClient.find(PredictorProjectAssociation.class, associationID);
		} catch (NoDocumentException e) {
			logger.error("Association not found in Couch DB");
			throw new AssociationException("Association not found in Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("getAssociatedModel() End");
		return association;
	}
	
	/**
	 * To Delete the Predictor Project Association form Couch DB
	 * @param _id
	 * 			the Association ID
	 * @param rev_Id
	 * 			the rev_Id (Auto generated by Couch DB)
	 */
	public void deleteAssociation(String _id, String rev_Id) {
		logger.debug("deleteAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.remove(_id, rev_Id);
		} catch (Exception e) {
			logger.error("Exception occured while deleting the Document from Couch DB");
			throw new CouchDBException("Exception occured while deleting the Document from Couch DB");
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		logger.debug("deleteAssociation() End");
		
	}
	
	/**
	 * To Check if the predictor Exists in Couch DB
	 * @param predictorId
	 * 				The Predictor Id
	 * @return
	 * 				returns List<PredictorProjectAssociation>
	 */
	public List<PredictorProjectAssociation> predictorExists(String predictorId) {
		logger.debug("predictorExists() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<PredictorProjectAssociation> associationList = null;
		String jsonQuery = String.format(PredictorServiceConstants.PREDICTOREXISTSINCOUCHQUERY, predictorId);
		Response response = new Response();
		try {
			associationList = dbClient.findDocs(jsonQuery, PredictorProjectAssociation.class);
			// similar association already exists and is ACTIVE
			if (associationList.isEmpty() || associationList.size() == 0) {
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
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
		}
		
		logger.debug("predictorExists() End");
		return associationList;
	}
	
	/**
	 * To Check the Model Project Association Exists in Couch DB
	 * @param projectId
	 * 			The Project Id
	 * @param solutionId
	 * 			The Solution Id
	 * @param revisionId
	 * 			The Revision Id
	 */
	public void modelProjectAssociationExistsInCouch(String projectId, String solutionId, String revisionId) {
		logger.debug("modelProjectAssociationExistsInCouch() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		String jsonQuery = String.format(PredictorServiceConstants.MODELPROJECTASSOCIATIONEXISTSINCOUCHQUERY, projectId, solutionId,revisionId,"ACTIVE");
		try {
			List<Response> responseList = dbClient.findDocs(jsonQuery, Response.class);
			if (responseList.isEmpty() || responseList.size() == 0) {
				logger.error("Cannot Associate Predictor, as corresponding model is not associated to a Project");
				throw new AssociationException("Cannot Associate Predictor, as corresponding model is not associated to a Project");
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
		logger.debug("modelProjectAssociationExistsInCouch() End");
	}
	

	private void associationExistsInCouch(String predcitorID, String projectId, String revisionId, String solutionId) {
		logger.debug("associationExistsInCouch() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<PredictorProjectAssociation> association = null;
		String jsonQuery = String.format(PredictorServiceConstants.ASSOCIATIONEXISTSINCOUCHQUERY, projectId, solutionId,
				revisionId);
		Response response = new Response();
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
