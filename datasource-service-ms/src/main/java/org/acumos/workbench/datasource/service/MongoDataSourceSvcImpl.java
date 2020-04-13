/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.datasource.service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.acumos.workbench.common.vo.DataSourceMetadata;
import org.acumos.workbench.common.vo.NameValue;
import org.acumos.workbench.datasource.exception.DataSourceNotFoundException;
import org.acumos.workbench.datasource.model.MongoDBConnectionModel;
import org.bson.BasicBSONEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

@Service("MongoDataSourceSvcImpl")
public class MongoDataSourceSvcImpl implements IMongoDataSourceSvc{
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@SuppressWarnings("deprecation")
	@Override
	public String getMongoConnectionStatus(MongoDBConnectionModel mongoDbConnectionModel, String query)
			throws IOException, DataSourceNotFoundException {
		logger.debug("getMongoConnectionStatus Begin");
		logger.debug("getConnectionStatus, trying to establish connection to mongo db on : "
				+ mongoDbConnectionModel.getHostname() + " using port : " + mongoDbConnectionModel.getPort());

		MongoClient mongo = null;
		DB db = null;
		String connectionMessage = "failed";

		if (mongoDbConnectionModel.getHostname() != null && mongoDbConnectionModel.getPort() != 0
				&& mongoDbConnectionModel.getDbName() != null) {

			logger.debug(
					"getMongoConnectionStatus, trying to establish mongo connection with credentials using username: "
							+ mongoDbConnectionModel.getUsername());
			logger.debug(
					"getMongoConnectionStatus, trying to establish mongo connection with credentials for database: "
							+ mongoDbConnectionModel.getDbName());

			mongo = getConnection(mongoDbConnectionModel);

			db = mongo.getDB(mongoDbConnectionModel.getDbName());
			if (query != null) {
				BasicDBObject dbQuery = (BasicDBObject) JSON.parse(query);

				DBCursor cursor = db.getCollection(mongoDbConnectionModel.getCollectionName()).find(dbQuery);
				int numberOfDocs = 0;
				double size = 0;
				DBCollection coll = db.getCollection(mongoDbConnectionModel.getCollectionName());
				while (cursor.hasNext()) {
					numberOfDocs++;
					connectionMessage = "success";
					DBObject dbo = new BasicDBObject("_id", ((BasicDBObject) cursor.next()).getString("_id"));
					DBObject obj = coll.findOne(dbo);
					if (obj != null) {
						int bsonSize = (new BasicBSONEncoder()).encode(obj).length;
						size += bsonSize;
					}
				}

				try {
					if (connectionMessage.equals("success")) {
						saveMetadata(mongoDbConnectionModel, db, size, numberOfDocs, dbQuery);
					}
				} catch (Exception e) {
					logger.debug("Exception to save metadata : " + e.getMessage());
					throw e;
				}
			} else if (db != null) {
				connectionMessage = "success";
			}

		}

		logger.debug("Connection has been established to mongo db on " + mongoDbConnectionModel + " using port : "
				+ mongoDbConnectionModel);
		logger.debug("getMongoConnectionStatus End");
		return connectionMessage;
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public MongoClient getConnection(MongoDBConnectionModel mongoDbConnectionModel)
			throws IOException, DataSourceNotFoundException {
		logger.debug("getConnection Begin");
		MongoClient mongo = null;

		if(mongoDbConnectionModel.getPassword() != null && !mongoDbConnectionModel.getPassword().isEmpty()) {
			logger.debug("getConnection, trying to establish mongo connection with credentials using username: "
					+ mongoDbConnectionModel.getUsername());
			logger.debug("getConnection, trying to establish mongo connection with credentials for database: "
					+ mongoDbConnectionModel.getDbName());
			
			MongoCredential credential = MongoCredential.createCredential(mongoDbConnectionModel.getUsername(),
					mongoDbConnectionModel.getDbName(), mongoDbConnectionModel.getPassword().toCharArray());
			mongo = new MongoClient(
					new ServerAddress(mongoDbConnectionModel.getHostname(), mongoDbConnectionModel.getPort()),
					Arrays.asList(credential));
		} else {
			logger.debug("getConnection, trying to establish mongo connection with no credentials");
			logger.debug("getConnection, trying to establish mongo connection with no credentials for database: "
					+ mongoDbConnectionModel.getDbName());
			
			mongo = new MongoClient(
					new ServerAddress(mongoDbConnectionModel.getHostname(), mongoDbConnectionModel.getPort()));
		}
		logger.debug("getConnection End");
		return mongo;
	}
	
	
	private void saveMetadata(MongoDBConnectionModel mongoDbConnectionModel, DB db, double size, int numberOfDocs,
			BasicDBObject dbQuery) {
		logger.debug("saveMetadata Begin");
		DataSourceMetadata m = new DataSourceMetadata();
        ArrayList<NameValue> lstSimpleMetadata = new ArrayList<NameValue>();
        lstSimpleMetadata.add(new NameValue("noOfDocs", String.valueOf(numberOfDocs)));
        lstSimpleMetadata.add(new NameValue("sizeOfDocs", String.valueOf(size)+"bytes"));
        m.setMetaDataInfo(lstSimpleMetadata);


        List<DBObject>  indexList = db.getCollection(mongoDbConnectionModel.
				getCollectionName()).getIndexInfo();

		for(DBObject dbobj: indexList)
		{
			((BasicDBObject)dbobj).remove("weights");
			((BasicDBObject)dbobj).remove("v");

		}
        m.setMetaDataInfo(lstSimpleMetadata);
       //m.setListIndex(indexList);
       // mongoDbConnectionModel.setMetadata(m);
        logger.debug("saveMetadata End");
	}
	
	

}
