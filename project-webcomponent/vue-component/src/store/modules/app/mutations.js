export default {
  setComponentUrl(state, url) {
    state.componentUrl = url;
  },
  setWikiConfig(state, config) {
    state.wikiConfig = config;
  },
  setMsConfig(state, config) {
    state.msConfig = config;
  },
  setUserName(state, userName) {
    state.userName = userName;
  },
  setAuthToken(state, authToken) {
    state.authToken = authToken;
  },
  setError(state, error) {
    state.error = error;
  },
  setMessage(state, message) {
    state.message = message;
  },
  setStatus(state, status) {
    state.status = status;
  },
  setPortalBEUrl(state, portalBEUrl){
    state.portalBEUrl = portalBEUrl;
  },
  setPortalFEUrl(state, portalFEUrl){
    state.portalFEUrl = portalFEUrl;
  },
  setPipelineFlag(state, pipelineFlag){
    state.pipelineFlag = pipelineFlag;
  },
  setCreateTimeout(state, createTimeout){
    state.createTimeout = createTimeout;
  },
  setUseExternalNotebook(state, useExternalNotebook){
    state.useExternalNotebook = useExternalNotebook;
  },
  setUseExternalPipeline(state, useExternalPipeline){
    state.useExternalPipeline = useExternalPipeline;
  }
};
