import axios from "axios";
import { get } from "lodash-es";

export default {
  async notebookCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/notebook/count`,
      {
        url: rootState.app.msConfig.notebookmSURL,
        userName: rootState.app.userName
      }
    );

    if (data.status === "Error") {
      return data;
    }
    return get(data, "data");
  }
};
