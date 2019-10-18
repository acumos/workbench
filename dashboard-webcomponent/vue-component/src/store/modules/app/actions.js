import axios from "axios";

export default {
  async getConfig({ commit, rootState }) {
    return await axios
      .get(`${rootState.app.componentUrl}/api/config`)
      .then(({ data }) => {
        commit("setPipelineWikiURL", data.pipelineWikiURL);
        commit("setMsConfig", data.msconfig);
        commit("setPortalBEUrl", data.portalBEUrl);
        commit("setPortalFEUrl", data.portalFEUrl);
        commit("setPipelineFlag", data.pipelineFlag);

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
