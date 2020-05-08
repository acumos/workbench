export default {
  setActiveDataset(state, dataset) {
      state.activeDataset = dataset;
    },
    setLoginAsOwner(state, ownerLogin){
      state.loginAsOwner = ownerLogin;
    }
};
