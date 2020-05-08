import axios from "axios";
import { get } from "lodash-es";

export default {
  async datasourceCount({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/datasource/count`,
      {
        url: rootState.app.msConfig.datasourcemSURL,
        userName: rootState.app.userName,
        category: 'couch',
        namespace: 'acumosLatest',
        textSearch : ' '
      }
    );

    if (data.status === "Error") {
      return data;
    }
    return get(data, "data");
  }
};
