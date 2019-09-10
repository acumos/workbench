import { Model } from "@vuex-orm/core";

export default class Pipeline extends Model {
  static entity = "pipelines";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr(""),
      description: this.attr(''),
      creationDate: this.attr("")
    };
  }
}
