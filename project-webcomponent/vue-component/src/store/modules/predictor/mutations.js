export default {
  setPredictorToast(state, { message, type, id }) {
    state.predictorToast = {
      id,
      enabled: true,
      message,
      type
    };
  },
  setPredictorError(state, predictorError) {
    state.predictorError = predictorError;
  }
};
