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
      url: this.attr("")
    };
  }
}
