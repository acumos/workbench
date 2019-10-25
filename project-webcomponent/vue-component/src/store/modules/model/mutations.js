export default {
  setCategories(state, categories) {
    state.categories = categories;
  },
  setModels(state, models) {
    state.models = models;
  },
  setModelToast(state, { message, type, id }) {
    state.modelToast = {
      id,
      enabled: true,
      message,
      type
    };
  },
  setModelError(state, modelError){
    state.modelError = modelError;
  }
};
