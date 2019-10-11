import axios from "axios";

export default {
  async getConfig({ commit, rootState }) {
    return await axios
      .get(`${rootState.app.componentUrl}/api/config`)
      .then(({ data }) => {
        commit("setPipelineWikiURL", data.pipelineWikiURL);
        commit("setPipelinemSURL", data.msconfig.pipelinemSURL);
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
