import { Model } from "@vuex-orm/core";
import { set, get } from "lodash-es";

export default class Project extends Model {
  static entity = "project";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr(""),
      status: this.attr(""),
      createdAt: this.attr(""),
      modifiedAt: this.attr(""),
      description: this.attr(""),
      collaborators: this.attr(null),
      owner: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "projectId.uuid"),
      name: get(json, "projectId.name"),
      version: get(json, "projectId.versionId.label"),
      status: get(json, "artifactStatus.status"),
      createdAt: get(json, "projectId.versionId.creationTimeStamp"),
      modifiedAt: get(json, "projectId.versionId.modifiedTimeStamp"),
      description: get(json, "description"),
      collaborators: get(json, "collaborators"),
      owner: get(json, "owner.authenticatedUserId")
    };
  }

  $toJson() {
    let json = {};

    set(json, "projectId.uuid", this.id);
    set(json, "projectId.name", this.name);
    set(json, "description", this.description);
    set(json, "projectId.versionId.creationTimeStamp", this.createdAt);
    set(json, "projectId.versionId.modifiedTimeStamp", this.modifiedAt);
    set(json, "projectId.versionId.label", this.version);
    set(json, "artifactStatus.status", this.status);
    set(json, "collaborators", this.collaborators);

    return json;
  }
}
