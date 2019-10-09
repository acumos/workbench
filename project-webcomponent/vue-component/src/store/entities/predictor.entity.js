import { Model } from "@vuex-orm/core";
import { get } from "lodash-es";

export default class Predictor extends Model {
  static entity = "predictor";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      key: this.attr(""),
      version: this.attr(""),
      url: this.attr(""),
      deployStatus: this.attr(""),
      createdTimestamp: this.attr(""),
      status: this.attr(""),
      modelId: this.attr(""),
      modelVersion: this.attr(""),
      associationId: this.attr(""),
      owner: this.attr(""),
      revisionId: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "predictorId.uuid"),
      name: get(json, "predictorId.name"),
      key: get(json, "predictorId.key"),
      version: get(json, "predictorId.versionId.label"),
      url: get(json, "predictorId.serviceUrl"),
      deployStatus: get(json, "artifactStatus.status"),
      createdTimestamp: get(json, "predictorId.versionId.creationTimeStamp"),
      status: get(json, "artifactStatus.status"),
      modelId: get(json, "model.modelId.uuid"),
      modelVersion: get(json, "model.modelId.versionId.label"),
      associationId: get(json, "predictorId.metrics.kv[0].value"),
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
      predictorDescription: this.description,
      predictorVersion: this.version,
      environmentPath: this.url
    };
  }
}
