import { set } from "lodash-es";
import { Model } from "@vuex-orm/core";

export default class Pipeline extends Model {
  static entity = "pipeline";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr("1.0.0"),
      description: this.attr(""),
      creationDate: this.attr(""),
      url: this.attr(""),
      status: this.attr(""),
      createdBy: this.attr(""),
      modifiedDate: this.attr("")
    };
  }

  $toJson() {
    let json = {};

    set(json, "pipelineId.uuid", this.id);
    set(json, "pipelineId.name", this.name);
    set(json, "description", this.description);
    set(json, "pipelineId.versionId.creationTimeStamp", this.creationDate);
    set(json, "pipelineId.versionId.label", this.version);
    set(json, "artifactStatus.status", this.status);
    set(json, "pipelineId.serviceUrl", this.url);
    set(json, "pipelineId.versionId.modifiedTimeStamp", this.modifiedDate);
    
    return json;
  }
}
