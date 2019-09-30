export default {
  setActiveProject(state, project) {
    state.activeProject = project;
  },
  setLoginAsOwner(state, ownerLogin){
    state.loginAsOwner = ownerLogin;
  }
};
