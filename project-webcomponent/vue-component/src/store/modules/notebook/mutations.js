export default {
  setActiveProject(state, project) {
    state.activeProject = project;
  },
  setNotebookToast(state, { message, type, id }) {
    state.notebookToast = {
      id,
      enabled: true,
      message,
      type
    };
  },
  setNotebookError(state, notebookError){
    state.notebookError = notebookError;
  }
};
