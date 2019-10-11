import { set } from "lodash-es";
import { Model } from "@vuex-orm/core";

export default class Notebook extends Model {
  static entity = "notebook";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr("1.0.0"),
      type: this.attr("JUPYTER"),
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

    set(json, "noteBookId.uuid", this.id);
    set(json, "noteBookId.name", this.name);
    set(json, "description", this.description);
    set(json, "noteBookId.versionId.creationTimeStamp", this.creationDate);
    set(json, "noteBookId.versionId.label", this.version);
    set(json, "artifactStatus.status", this.status);
    set(json, "noteBookId.serviceUrl", this.url);
    set(json, "noteBookId.versionId.modifiedTimeStamp", this.modifiedDate);
    set(json, "notebookType", this.type);
    
    return json;
  }
}
