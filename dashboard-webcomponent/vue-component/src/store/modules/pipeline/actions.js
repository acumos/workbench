import axios from "axios";
import { get } from "lodash-es";

export default {
  async pipelineCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/count`,
      {
        url: rootState.app.msConfig.pipelinemSURL,
        userName: rootState.app.userName
      }
    );

    return get(data, "data");
  }
};
