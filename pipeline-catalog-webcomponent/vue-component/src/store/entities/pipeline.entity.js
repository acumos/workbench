import { get } from "lodash-es";
import { Model } from "@vuex-orm/core";

export default class Pipeline extends Model {
  static entity = "pipeline";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr("1.0.0"),
      description: this.attr(""),
      createdAt: this.attr(""),
      url: this.attr(""),
      status: this.attr(""),
      createdBy: this.attr(""),
      owner: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "pipelineId.uuid"),
      name: get(json, "pipelineId.name"),
      version: get(json, "pipelineId.versionId.label"),
      description: get(json, "description"),
      createdAt: get(json, "pipelineId.versionId.creationTimeStamp"),
      url: get(json, "pipelineId.serviceUrl"),
      createdBy: get(json, "owner.authenticatedUserId"),
      status: get(json, "artifactStatus.status"),
      owner: get(json, "owner.userId.name")
    };
  }

  $toJson() {
    return {
      pipelineId: {
        name: this.name,
        uuid: this.id,
        versionId: {
          comment: this.description,
          label: this.version
        },
        serviceUrl: this.url
      },
      description: this.description
    };
  }
}
