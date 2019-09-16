import { Model } from "@vuex-orm/core";

export default class Notebook extends Model {
  static entity = "notebook";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr("1.0.0"),
      type: this.attr(""),
      url: this.attr(""),
      description: this.attr(""),
      createdAt: this.attr(""),
      createdBy: this.attr(""),
      status: this.attr("")
    };
  }

  $fromJson(json) {
    return {
      id: json.noteBookId.uuid,
      name: json.noteBookId.name,
      version: json.noteBookId.versionId.label,
      createdAt: json.noteBookId.versionId.timeStamp,
      createdBy: json.owner.authenticatedUserId,
      description: json.description,
      status: json.artifactStatus.status,
      type: json.notebookType
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
