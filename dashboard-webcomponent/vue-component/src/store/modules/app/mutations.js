export default {
  setComponentUrl(state, url) {
    state.componentUrl = url;
  },
  setPipelineWikiURL(state, config) {
    state.pipelineWikiURL = config;
  },
  setMsConfig(state, config) {
    state.msConfig = config;
  },
  setPortalBEUrl(state, config) {
    state.portalBEUrl = config;
  },
  setPortalFEUrl(state, config) {
    state.portalFEUrl = config;
  },
  setPipelineFlag(state, config) {
    state.pipelineFlag = config;
  },
  setUserName(state, userName) {
    state.userName = userName;
  },
  setAuthToken(state, authToken) {
    state.authToken = authToken;
  },
  setUserId(state, userId) {
    state.userId = userId;
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
  confirm(state, { title, body, onOk, onDismiss, options }) {
    state.confirm = {
      enabled: true,
      title,
      body,
      onOk,
      onDismiss,
      options: options || {}
    };
  },

  toggleConfirm(state) {
    const newConfirm = { ...state.confirm };

    newConfirm.enabled = !state.confirm.enabled;

    state.confirm = newConfirm;
  },

  setGlobalError(state, globalError){
    state.globalError = globalError;
  }
};
