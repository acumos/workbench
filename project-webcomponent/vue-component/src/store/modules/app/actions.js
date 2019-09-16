import axios from "axios";

export default {
  getConfig({ commit, rootState }) {
    return axios
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
  }
};
