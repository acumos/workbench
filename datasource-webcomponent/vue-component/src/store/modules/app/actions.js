import axios from "axios";

export default {
  async getConfig({ commit, rootState }) {
    return await axios
      .get(`${rootState.app.componentUrl}/api/config`)
      .then(({ data }) => {
        commit("setDatasetWikiURL", data.datasourceWikiURL);
        commit("setDatasetmSURL", data.msconfig.datasourcemSURL);
        // Set auth token
        axios.defaults.headers.common["auth"] = rootState.app.authToken;
      });
  },
  showToastMessage({ commit }, toast) {
    commit("setToastMessage", toast);

    setTimeout(() => {
      commit("toggleToast");
    }, 5000);
  }
};
