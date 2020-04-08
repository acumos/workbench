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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.acumos.workbench.common.util.ArtifactStatus;
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
	
	@Autowired
	private PredictorServiceImpl predictorServiceImpl;
	
	String k8sId=null;
	

	/**
	 * To Associate the Predictor to a Project
	 * @param association
	 * 			the PredictorProjectAssociation object
	 * @return
	 * 			returns PredictorProjectAssociation details
	 */
	public DataSetPredictor savePredictorProjectAssociation(DataSetPredictor association) {

		logger.debug("InsertProjectModelAssociation() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		associationExistsInCouch(association.getPredictorId(), association.getProjectId(),
				association.getRevisionId(), association.getSolutionId());

		Response response = new Response();
		try {
			association.setAssociationId(UUID.randomUUID().toString());
			association.setAssociationCreatedTimestamp((Instant.now().toString()));
			association.setAssociationUpdateTimestamp((Instant.now().toString()));
			association.setAssociationStatus(ArtifactStatus.ACTIVE.toString());
			response = dbClient.save(association);
			association.set_rev(response.getRev());
			logger.debug("Response Object from Couch DB  : " + response);
		} catch (CouchDBException e) {
			logger.error("Exception occured while Associating Predictor to Project in Couch DB");
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
	public List<DataSetPredictor> getPredictorsForProject(String projectId) {
		logger.debug("getPredictors() Begin");
		CouchDbClient dbClient = null;
		List<DataSetPredictor> predictorProjAssociation = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORSQUERY,projectId,ArtifactStatus.ACTIVE.toString());
		
		try {
			dbClient = getLightCouchdbClient();
			predictorProjAssociation = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
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
	public void updatePredictorProjectAssociation(DataSetPredictor association) {
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
	public DataSetPredictor getPredictorProjectAssocaition(String associationId) {
		logger.debug("getAssociatedModel() Begin");
		List<DataSetPredictor> association = null;
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORPROJECTASSOCIATION,associationId);
		association = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
		if ((association.size()>1 && null ==association.get(0))) {
			throw new AssociationException("Association not found in Couch DB");
		}
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
		return association.get(0);
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
	public List<PredictorProjectAssociation> getPredictorDetails(String predictorId) {
		logger.debug("predictorExists() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		List<PredictorProjectAssociation> predictorList = null;
		String jsonQuery = String.format(PredictorServiceConstants.PREDICTOREXISTSINCOUCHQUERY, predictorId,ArtifactStatus.ACTIVE.toString());
		try {
			predictorList = dbClient.findDocs(jsonQuery, PredictorProjectAssociation.class);
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
			DataSetPredictor predictorProjAssociation) {
		logger.debug("saveDataSetPredictor() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		Response response = new Response();
		try {
			predictorProjAssociation.setPredictorId((UUID.randomUUID().toString()));
			predictorProjAssociation.setCreatedTimestamp(Instant.now().toString());
			predictorProjAssociation.setUpdateTimestamp(Instant.now().toString());
			response = dbClient.save(predictorProjAssociation);
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
		return predictorProjAssociation;
	}
	
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
	
	public DataSetPredictor createPredictor(String authenticatedUserId,Predictor predictor,String revisionId, String authToken,String K8S_ID,String predictorKey,String projectId) {
		logger.debug("createPredictor() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		DataSetPredictor dataSetPredictor = new DataSetPredictor();
		Response response = new Response();
		
		try {
			
			dataSetPredictor.setPredictorId(UUID.randomUUID().toString());
			dataSetPredictor.setUserId(authenticatedUserId);
			dataSetPredictor.setSolutionId(predictor.getModel().getModelId().getUuid());
			dataSetPredictor.setRevisionId(revisionId);
			dataSetPredictor.setPredictorName(predictor.getPredictorId().getName());
			dataSetPredictor.setPredictorVersion(predictor.getPredictorId().getVersionId().getLabel());
			dataSetPredictor.setPredictorDescription(predictor.getPredictorId().getVersionId().getComment());
			String signature = predictorServiceImpl.getProtobufSignature(authenticatedUserId, predictor.getModel().getModelId().getUuid(), revisionId,authToken);	// Need to check with raman how to get the protobuf signature
			dataSetPredictor.setSignature(signature);
			dataSetPredictor.setK8sId(K8S_ID); //cluster
			dataSetPredictor.setPredictorDeploymentStatus(PredictorServiceConstants.deplyomentStatus.INPROGRESS.toString());
			dataSetPredictor.setCreatedTimestamp(Instant.now().toString());
			dataSetPredictor.setUpdateTimestamp(Instant.now().toString());
			dataSetPredictor.setPredictorkey(predictorKey);
			dataSetPredictor.setProjectId(projectId);
			
			// Save the metaData in Couch DB
			response = dbClient.save(dataSetPredictor);
			dataSetPredictor.set_rev(response.getRev());
			logger.debug("Response Object from Couch DB  : " + response);
		} catch (Exception e) {
			logger.error("Exception occured while saving predictor in DB" + e);
			throw new CouchDBException("Exception occured while saving predictor in DB" + e);
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client" + e);
				throw new CouchDBException("IOException occured while closing the lightcouch client" + e);
			}
		}
		logger.debug("createPredictor() End");
		return dataSetPredictor;
	}
	
	public List<DataSetPredictor> getPreditor(String authenticatedUserId, String predictorName) {
		logger.debug("getPreditor() Begin");
		CouchDbClient dbClient = null;
		List<DataSetPredictor> dataSetPredictorList = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORBYNAMEQUERY,authenticatedUserId,predictorName);

		try {
			dbClient = getLightCouchdbClient();
			dataSetPredictorList = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
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
		logger.debug("getPreditor() End");
		return dataSetPredictorList;
	}

	public List<DataSetPredictor> getPredictorById(String authenticatedUserId, String predictorId) {
		logger.debug("getPredictorById() Begin");
		CouchDbClient dbClient = null;
		List<DataSetPredictor> dataSetPredictorList = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORBYIDQUERY,authenticatedUserId,predictorId);

		try {
			dbClient = getLightCouchdbClient();
			dataSetPredictorList = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
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
		logger.debug("getPredictorById() End");
		return dataSetPredictorList;
	}
	
	public List<DataSetPredictor> getPredictorforUser(String authenticatedUserId,String projectId) {
		logger.debug("getPredictorforUser() Begin");
		CouchDbClient dbClient = null;
		List<DataSetPredictor> dataSetPredictorList = new ArrayList<DataSetPredictor>();
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTOR_FOR_USER,authenticatedUserId,projectId);
		try {
			dbClient = getLightCouchdbClient();
			dataSetPredictorList = dbClient.findDocs(jsonQuery, DataSetPredictor.class);
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
		logger.debug("getPredictorforUser() End");
		return dataSetPredictorList;
	}
	
	public List<PredictorProjectAssociation> getPredictorProject(String authenticatedUserId, String predictorId,String projectId) {
		logger.debug("getPredictorProject() Begin");
		CouchDbClient dbClient = null;
		List<PredictorProjectAssociation> dataSetPredictorList = null;
		String jsonQuery = String.format(PredictorServiceConstants.GETPREDICTORPROJECT,authenticatedUserId,predictorId,predictorId);

		try {
			dbClient = getLightCouchdbClient();
			dataSetPredictorList = dbClient.findDocs(jsonQuery, PredictorProjectAssociation.class);
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
		logger.debug("getPredictorProject() End");
		return dataSetPredictorList;
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
		CouchDbClient couchDbClient = null;
		try {
		couchDbClient = new CouchDbClient(configurationProperties.getCouchDbName(),
				configurationProperties.isCreateIfnotExists(), configurationProperties.getCouchdbProtocol(),
				configurationProperties.getCouchdbHost(), configurationProperties.getCouchdbPort(),
				configurationProperties.getCouchdbUser(), configurationProperties.getCouchdbPwd());
		}
		catch (CouchDBException e) {
		logger.error("Exception Occured while creating couchdb instance", e);
		throw new CouchDBException("Exception Occured while creating couchdb instance", e);
		}
		return couchDbClient;
	}

	public void deletePredictor(DataSetPredictor dataSetPredictor) {
		logger.debug("deletePredictor() Begin");
		CouchDbClient dbClient = getLightCouchdbClient();
		try {
			dbClient.remove(dataSetPredictor);
		} catch (Exception e) {
			logger.error("Exception occured while removiing document from CouchDB", e);
			throw new CouchDBException("Exception occured while removiing document from CouchDB", e);
		} finally {
			try {
				// closing the resources
				dbClient.close();
			} catch (IOException e) {
				logger.error("IOException occured while closing the lightcouch client");
				throw new CouchDBException("IOException occured while closing the lightcouch client");
			}
			logger.debug("deletePredictor() End");
		}

	}}
