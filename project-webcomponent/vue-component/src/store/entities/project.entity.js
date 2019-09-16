import { Model } from "@vuex-orm/core";

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
}
