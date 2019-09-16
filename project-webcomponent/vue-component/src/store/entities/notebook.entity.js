import { Model } from "@vuex-orm/core";
import { get } from "lodash-es";

export default class Notebook extends Model {
  static entity = "notebook";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr("1.0.0"),
      type: this.attr("JUPYTER"),
      url: this.attr(""),
      description: this.attr(""),
      createdAt: this.attr(""),
      createdBy: this.attr(""),
      status: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "noteBookId.uuid"),
      name: get(json, "noteBookId.name"),
      version: get(json, "noteBookId.versionId.label"),
      createdAt: get(json, "noteBookId.versionId.creationTimeStamp"),
      createdBy: get(json, "owner.authenticatedUserId"),
      description: get(json, "description"),
      status: get(json, "artifactStatus.status"),
      type: get(json, "notebookType"),
      url: get(json, "noteBookId.serviceUrl")
    };
  }

  $toJson() {
    return {
      noteBookId: {
        name: this.name,
        uuid: this.id,
        versionId: {
          comment: this.description,
          label: this.version
        },
        serviceUrl: this.url
      },
      notebookType: this.type,
      description: this.description
    };
  }

}
