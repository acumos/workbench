import { Model } from "@vuex-orm/core";
import { set, get } from "lodash-es";

export default class Dataset extends Model {
  static entity = "dataset";

  static fields() {
    return {
      id: this.attr(null),
      rev: this.attr(""),
      datasourceId: this.attr(""),
      name:this.attr(""),
      versionId: this.attr(""),
      creationTimeStamp: this.attr(""),
      modifiedTimeStamp: this.attr(""),
      category: this.attr(""),
      jdbcUrl: this.attr(""),
      namespace: this.attr("acumosLatest"),
      datasourceDescription: this.attr(""),
      readWriteDescriptor: this.attr(""),
      isDataReference: this.attr("true"),
      serverName: this.attr(""),
      portNumber: this.attr(""),
      environment: this.attr("DEV"),
      databaseName: this.attr(""),
      dbServerUsername: this.attr(""),
      dbServerPassword: this.attr(""),
      dbQuery: this.attr(""),
      dbCollectionName: this.attr(""),
      uuid: this.attr(""),
      active: this.attr("true"),
      owner: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "_id"),
      rev: get(json, "_rev"),
      datasourceId: get(json, "datasourceId.uuid"),
      name: get(json, "datasourceId.name"),
      versionId: get(json, "datasourceId.versionId.label"),
      creationTimeStamp: get(json, "datasourceId.versionId.label.creationTimeStamp"),
      modifiedTimeStamp: get(json, "datasourceId.versionId.label.modifiedTimeStamp"),
      category: get(json, "category"),
      namespace: get(json, "namespace"),
      datasourceDescription: get(json, "datasourceDescription"),
      readWriteDescriptor: get(json, "readWriteDescriptor"),
      isDataReference: get(json, "isDataReference"),
      jdbcUrl : get(json, "commonDetails.serverName") + ":" + get(json, "commonDetails.portNumber"),
      environment: get(json, "commonDetails.environment"),
      databaseName: get(json, "dbDetails.databaseName"),
      dbServerUsername: get(json, "dbDetails.dbServerUsername"),
      dbServerPassword: get(json, "dbDetails.dbServerPassword"),
      dbQuery: get(json, "dbDetails.dbQuery"),
      dbCollectionName: get(json, "dbDetails.dbCollectionName"),
      uuid: get(json, "owner.userId.uuid"),
      owner: get(json, "owner.userId.name"),
      active: get(json, "active"),
    };
  }

  // $toJson() {
  //   let json = {};
  //   set(json, "active", this.active);
  //   set(json, "category", this.category);

  //   set(json, "_id", this.id);
  //   set(json, "datasourceId.uuid", this.datasourceId);
  //   set(json, "description", this.description);
  //   set(json, "projectId.versionId.creationTimeStamp", this.createdAt);
  //   set(json, "projectId.versionId.modifiedTimeStamp", this.modifiedAt);
  //   set(json, "projectId.versionId.label", this.version);
  //   set(json, "artifactStatus.status", this.status);
  //   set(json, "collaborators", this.collaborators);

  //   return json;
  // }

  $toJson(){
    return{
      active: this.active,
      category: this.category,
      commonDetails:{
      environment: this.environment,
      portNumber: this.portNumber,
      serverName: this.serverName,
      },
      datasourceDescription: this.datasourceDescription,
      datasourceId: {
        name: this.name,
        versionId:{
          label: "1.0.0",
        }
      },
      dbDetails:{
        databaseName: this.databaseName,
        dbServerUsername: this.dbServerUsername,
        dbServerPassword: this.dbServerPassword,
        dbQuery: this.dbQuery,
      },
      isDataReference: this.isDataReference,
      namespace: this.namespace,
      readWriteDescriptor: this.readWriteDescriptor
    };
  }
}
