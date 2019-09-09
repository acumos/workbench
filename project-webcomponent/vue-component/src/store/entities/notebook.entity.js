import { Model } from "@vuex-orm/core";

export default class Notebook extends Model {
  static entity = "notebooks";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      version: this.attr(""),
      type: this.attr(""),
      url: this.attr(''),
      description: this.attr(''),
      creationDate: this.attr("")
    };
  }
}
