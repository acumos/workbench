import axios from "axios";

export default {
  getConfig({ commit, rootState }) {
    return axios
      .get(`${rootState.app.componentUrl}/api/config`)
      .then(({ data }) => {
        commit("setWikiConfig", data.wikiConfig);
        commit("setMsConfig", data.msconfig);
        if (!rootState.app.userName) {
          commit("setUserName", data.userName);
        }

        let authToken = rootState.app.authToken;

        if (!authToken) {
          commit("setAuthToken", data.authToken);
          authToken = data.authToken;
        }
        // Set auth token
        axios.defaults.headers.common["auth"] = authToken;
      });
  }
};
