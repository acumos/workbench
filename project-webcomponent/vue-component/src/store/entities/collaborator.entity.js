import { Model } from "@vuex-orm/core";
import { get } from "lodash-es";

export default class Collaborator extends Model {
  static entity = "collaborator";

  static fields() {
    return {
      id: this.attr(null),
      name: this.attr(""),
      permission: this.attr("READ"),
      firstName: this.attr(""),
      lastName: this.attr("")
    };
  }

  static $fromJson(json) {
    return {
      id: get(json, "userId.uuid"),
      name: get(json, "userId.name"),
      permission: get(json, "roles[0].permissions[0].permission.name"),
      firstName: get(json, "userId.metrics.kv[0].value"),
      lastName: get(json, "userId.metrics.kv[1].value")
    };
  }

    $toJson() {
        return {
            users: [
                {
                    userId: {
                        uuid: this.id,
                        name: this.name
                    },
                    roles: [
                        {
                            permissions: [
                                {
                                    permission: {
                                        name: this.permission
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    }
}
