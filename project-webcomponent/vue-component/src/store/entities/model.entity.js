import { Model as _Model } from "@vuex-orm/core";

export default class Model extends _Model {
  static entity = "models";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      format: this.attr(""),
      category: this.attr(""),
      status: this.attr(""),
      version: this.attr(""),
      publishStatus: this.attr("")
    };
  }
}
