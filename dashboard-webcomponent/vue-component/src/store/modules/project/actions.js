import axios from "axios";
import { get } from "lodash-es";

export default {
  async projectCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/count`,
      {
        url: rootState.app.msConfig.projectmSURL,
        userName: rootState.app.userName
      }
    );

    if (data.status === "Error") {
      return data;
    }
    return get(data, "data");
  },

  async sharedProjectsCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/sharedProjects`,
      {
        url: rootState.app.msConfig.projectmSURL,
        userName: rootState.app.userName
      }
    );

    if (data.status === "Error") {
      return data;
    }
    return get(data, "data");
  }
};
