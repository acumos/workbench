
import { Model } from "@vuex-orm/core";
import { get } from "lodash-es";

export default class Predictor extends Model {
  static entity = "predictor";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      predictorkey: this.attr(""),
      version: this.attr(""),
      url: this.attr(""),
      deployStatus: this.attr(""),
      createdTimestamp: this.attr(""),
      status: this.attr(""),
      modelId: this.attr(""),
      modelVersion: this.attr(""),
      associationId: this.attr(""),
      owner: this.attr(""),
      revisionId: this.attr(""),
      cluster: this.attr(""),
      k8s_id: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "predictorId.uuid"),
      name: get(json, "predictorId.name"),
      key: get(json, "predictorId.metrics.kv[3].key"),
      version: get(json, "predictorId.versionId.label"),
      url: get(json, "predictorId.serviceUrl"),
      deployStatus: get(json, "artifactStatus.status"),
      createdTimestamp: get(json, "predictorId.versionId.creationTimeStamp"),
      status: get(json, "artifactStatus.status"),
      modelId: get(json, "model.modelId.uuid"),
      modelVersion: get(json, "model.modelId.versionId.label"),
      associationId: get(json, "predictorId.metrics.kv[0].value"),
      owner: get(json, "user.authenticatedUserId"),
      revisionId: get(json, "model.modelId.metrics.kv[0].value"),
      cluster: get(json, "model.modelId.metrics.kv[1].value"),
      k8s_id: get(json, "model.modelId.metrics.kv[2].value")
    };
  }

  static $predictorfromJson(json) {
    return {
      id: get(json, "predictorId.uuid"),
      name: get(json, "predictorId.name"),
      key: get(json, "predictorId.metrics.kv[0].value"),
      version: get(json, "predictorId.versionId.label"),
      url: get(json, "predictorId.serviceUrl"),
      associationId: get(json, "predictorId.metrics.kv[0].value"),
      deployStatus: get(json, "artifactStatus.status"),
      createdTimestamp: get(json, "predictorId.versionId.creationTimeStamp"),
      status: get(json, "artifactStatus.status"),
      modelId: get(json, "model.modelId.uuid"),
      modelVersion: get(json, "model.modelId.versionId.label"),
      owner: get(json, "user.authenticatedUserId"),
      revisionId: get(json, "model.modelId.metrics.kv[0].value")
    };
  }

  $toJson() {
    // return {
    //   predictorId: {
    //     uuid: this.id,
    //     name: this.name,
    //     versionId: {
    //       label: this.version
    //     },
    //     serviceUrl: this.baseUrl,
    //     metrics: {
    //       kv: [
    //         {
    //           key: "ASSOCIATION_ID",
    //           value: this.associationId
    //         }
    //       ]
    //     }
    //   },
    //   model: {
    //     modelId: {
    //       uuid: this.modelId,
    //       versionId: {
    //         label: this.modelVersion
    //       }
    //     }
    //   }
    // };
    return {
      userId: this.owner,
      solutionId: this.modelId,
      revisionId: this.revisionId,
      predictorId: this.id,
      projectId: this.projectId,
      associationId: this.associationId,
      modelStatus: "ACTIVE",
      predictorDeploymentStatus: "ACTIVE",
      predictorName: this.name,
      predictorDescription: this.name,
      predictorVersion: this.version,
      environmentPath: this.url,
      predictorkey: this.key,
      url: this.url
      
    };
  }

  static $deployfromJson(json) {
    return {
      id: get(json, "predictorId.uuid"),
      name: get(json, "predictorId.name"),
      predictorkey: get(json, "predictorId.metrics.kv[3].value"),
      associationId : get(json, "predictorId.metrics.kv[4].value"), 
      version: get(json, "predictorId.versionId.label"),
      modelId: get(json, "model.modelId.uuid"), 
      revisionId: get(json, "predictorId.metrics.kv[0].value"),
      k8s_id: get(json, "predictorId.metrics.kv[2].value"),
      deployStatus: get(json,  "predictorId.metrics.kv[1].value"),
      url: get(json, "predictorId.serviceUrl"),
    };
  }
  $toDeployJson(){
    return{
      model: {
      modelId: {
        metrics: {
          kv: [
            {
              key: "REVISION_ID",
              value: this.revisionId
            },
            {
              key: "K8S_ID",
              value: this.cluster
            },
            {
              key: "PREDICTOR_KEY",
              value: this.key
            },
          ]
        },
        "uuid": this.modelId,
      }
    },
    predictorId:{
        name: this.name, 
        versionId: {
            comment : "",
            label: this.version
          },
     },
    };
  }
}