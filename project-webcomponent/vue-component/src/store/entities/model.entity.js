import { Model as _Model } from "@vuex-orm/core";
import { get } from "lodash-es";

export default class Model extends _Model {
  static entity = "model";

  static fields() {
    return {
      modelId: this.attr(null),
      name: this.attr(""),
      version: this.attr(""),
      createdTimestamp: this.attr(""),
      createdBy: this.attr(""),
      status: this.attr(""),
      modelType: this.attr(""),
      modelCatalog: this.attr(""),
      publishStatus: this.attr(""),
      associationId: this.attr(""),
      revisionId: this.attr(""),
      cluster: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      modelId: get(json, "modelId.uuid"),
      name: get(json, "modelId.name"),
      version: get(json, "modelId.versionId.label"),
      createdTimestamp: get(json, "modelId.versionId.creationTimeStamp"),
      createdBy: get(json, "owner.authenticatedUserId"),
      status: get(json, "artifactStatus.status"),
      modelType: get(json, "modelId.metrics.kv[0].value"),
      modelCatalog: get(json, "modelId.metrics.kv[2].value"),
      publishStatus: get(json, "modelId.metrics.kv[1].value"),
      associationId: get(json, "modelId.metrics.kv[3].value"),
      revisionId: get(json, "modelId.metrics.kv[4].value"),
      cluster: get(json, "modelId.metrics.kv[5].value")
    };
  }

  $toJson() {
    return {
      modelId: {
        name: this.name,
        uuid: this.modelId,
        versionId: {
          comment: "",
          label: this.version
        },
        metrics: {
          kv: [
            {
              key: "MODEL_TYPE_CODE",
              value: this.modelType
            },
            {
              key: "MODEL_PUBLISH_STATUS",
              value: this.publishStatus
            },
            {
              key: "CATALOG_NAMES",
              value: this.modelCatalog
            },
            {
              key: "ASSOCIATION_ID",
              value: this.associationId
            },
            {
              key: "K8S_ID",
              value: this.cluster
            },
          ]
        }
      }
    };
  }
}
