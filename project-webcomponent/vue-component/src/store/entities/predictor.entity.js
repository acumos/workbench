import { Model } from "@vuex-orm/core";

export default class Predictor extends Model {
  static entity = "predictor";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      catalog: this.attr(""),
      key: this.attr(""),
      baseUrl: this.attr(""),
      status: this.attr("")
    };
  }
}
