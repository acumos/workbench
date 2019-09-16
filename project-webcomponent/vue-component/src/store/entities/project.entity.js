import { Model } from "@vuex-orm/core";
import { set } from "lodash-es";

export default class Project extends Model {
  static entity = "project";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr(""),
      status: this.attr(""),
      creationDate: this.attr(""),
      description: this.attr("")
    };
  }

  $toJson() {
    let json = {};

    set(json, "projectId.uuid", this.id);
    set(json, "projectId.name", this.name);
    set(json, "description", this.description);
    set(json, "projectId.versionId.creationTimeStamp", this.creationDate);
    set(json, "projectId.versionId.label", this.version);
    set(json, "artifactStatus.status", this.status);

    return json;
  }
}
