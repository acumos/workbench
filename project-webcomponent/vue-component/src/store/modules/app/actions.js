import axios from "axios";

export default {
  getConfig({ commit, rootState }) {
    return axios
      .get(`${rootState.app.componentUrl}/api/config`)
      .then(({ data }) => {
        commit("setWikiConfig", data.wikiConfig);
        commit("setMsConfig", data.msconfig);
        commit("setUserName", data.userName);

        // Set auth token
        axios.defaults.headers.common["auth"] = data.authToken;
      });
  }
};
