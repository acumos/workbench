import { Model as _Model } from "@vuex-orm/core";

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
      revisionId: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      modelId : json.modelId.uuid,
      name : json.modelId.name,
      version : json.modelId.versionId.label,
      createdTimestamp : json.modelId.versionId.timeStamp,
      createdBy : json.owner.authenticatedUserId,
			status : json.artifactStatus.status,
			modelType : json.modelId.metrics.kv[0].value, 
			modelCatalog : json.modelId.metrics.kv[2].value,
			publishStatus : json.modelId.metrics.kv[1].value,
			associationId : json.modelId.metrics.kv[3].value,
    };
  }

  $toJson() {
    return {
      modelId: {
        name : this.name,
        uuid : this.modelId,
        versionId : {
          comment : "",
          label : this.version
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
            }
          ]
        }
      }
    }
  }
  $updateToJson() {
    return {
      modelId: {
        name : this.name,
        uuid : this.modelId,
        versionId : {
          comment : "",
          label : this.version
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
            }
          ]
        }
      }
    }
  }
}
