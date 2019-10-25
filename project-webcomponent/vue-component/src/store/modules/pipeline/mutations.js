export default {
  setPipelineToast(state, { message, type, id }) {
    state.pipelineToast = {
      id,
      enabled: true,
      message,
      type
    };
  },
  setPipelineError(state, pipelineError) {
    state.pipelineError = pipelineError;
  }
};
