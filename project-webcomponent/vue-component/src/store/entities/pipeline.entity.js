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
      url: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "pipelineId.uuid"),
      name: get(json, "pipelineId.name"),
      version: get(json, "pipelineId.versionId.label"),
      description: get(json, "description"),
      createdAt: get(json, "pipelineId.versionId.creationTimeStamp")
    };
  }

  $toJson() {
    return {
      noteBookId: {
        name: this.name,
        versionId: {
          comment: this.description,
          label: this.version
        }
      },
      notebookType: this.type,
      description: this.description
    };
  }
}
