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
  setError(state, error) {
    state.error = error;
  },
  setMessage(state, message) {
    state.message = message;
  },
  setStatus(state, status) {
    state.status = status;
  }
};
