import axios from "axios";
import { get } from "lodash-es";

export default {
  async modelCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/model/count`,
      {
        url: rootState.app.portalBEUrl,
        userName: rootState.app.userName,
        reqBody: {
          request_body: {
            published: true,
            active: true,
            pageRequest: {
              page: 1,
              size: 1
            }
          }
        }
      }
    );

    return get(data, "data");
  },
  async privateModelCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/prmodel/count`,
      {
        url: rootState.app.portalBEUrl,
        userName: rootState.app.userName,
        reqBody: {
          request_body: {
            published: false,
            active: true,
            userId: rootState.app.userId,
            pageRequest: {
              page: 1,
              size: 1
            }
          }
        }
      }
    );

    return get(data, "data");
  }
};
