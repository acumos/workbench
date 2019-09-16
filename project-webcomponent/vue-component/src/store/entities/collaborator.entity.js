import { Model } from "@vuex-orm/core";

export default class Collaborator extends Model {
    static entity = "collaborator";

    static fields() {
        return {
            id: this.attr(null),
            name: this.attr(""),
            permission: this.attr("")
        };
    }

    static $fromJson(json) {
        return {
            id: json.userId.uuid,
            name: json.userId.name,
            permission: json.roles[0].permissions[0].permission.name
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
