import axios from "axios";

export default {
  async getConfig({ commit, rootState }) {
    return await axios
      .get(`${rootState.app.componentUrl}/api/config`)
      .then(({ data }) => {
        commit("setWikiConfig", data.wikiConfig);
        commit("setMsConfig", data.msconfig);
        commit("setPortalBEUrl", data.portalBEUrl);
        commit("setPortalFEUrl", data.portalFEUrl);
        commit("setPipelineFlag", data.pipelineFlag);
        commit("setCreateTimeout", data.createTimeout);
        commit("setUseExternalNotebook", data.useExternalNotebook);
        commit("setUseExternalPipeline", data.useExternalPipeline);
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
