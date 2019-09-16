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
  setStatus(state, status) {
    state.status = status;
  },
  setPortalBEUrl(state, portalBEUrl) {
    state.portalBEUrl = portalBEUrl;
  },
  setPortalFEUrl(state, portalFEUrl) {
    state.portalFEUrl = portalFEUrl;
  },
  setPipelineFlag(state, pipelineFlag) {
    state.pipelineFlag = pipelineFlag;
  },
  setCreateTimeout(state, createTimeout) {
    state.createTimeout = createTimeout;
  },
  setUseExternalNotebook(state, useExternalNotebook) {
    state.useExternalNotebook = useExternalNotebook;
  },
  setUseExternalPipeline(state, useExternalPipeline) {
    state.useExternalPipeline = useExternalPipeline;
  },
  setToastMessage(state, { message, type, id }) {
    state.toast = {
      id,
      enabled: true,
      message,
      type
    };
  },
  toggleToast(state) {
    let newToast = { ...state.toast };

    newToast.enabled = !newToast.enabled;

    state.toast = newToast;
  },
  confirm(state, { message, onOk, onDismiss }) {
    state.confirm = {
      enabled: true,
      message,
      onOk,
      onDismiss
    };
  },

  toggleConfirm(state) {
    const newConfirm = { ...state.confirm };

    newConfirm.enabled = !state.confirm.enabled;

    state.confirm = newConfirm;
  }
};
