export default {
  setComponentUrl(state, url) {
    state.componentUrl = url;
  },
  setDatasetWikiURL(state, config) {
    state.datasourceWikiURL = config;
  },
  setDatasetmSURL(state, config) {
    state.datasourcemSURL = config;
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
