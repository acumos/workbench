import axios from "axios";
import { map } from "lodash-es";
import Model from "../../entities/model.entity";
import Predictor from "../../entities/predictor.entity";

export default {
  async getProjectModels({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/models/getProjectModels`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.modelmSURL,
        projectId: rootState.project.activeProject
      }
    );

    if (data.status === "Error") {
      commit("setPredictorToast", {
        id: "predictor",
        type: "error",
        message: data.message
      });

      commit("setPredictorError", true);
      return [];
    }
  },

  async getClusters({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/users`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL
      }
    );

    if (data.status === "Error") {
      commit("setPredictorToast", {
        id: "predictor",
        type: "error",
        message: data.message
      });

      commit("setPredictorError", true);
     return [];
    }
    else{
      return data.data;
    }
  },

  async getProjectPredictors({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/getProjectPredictors`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        projectId: rootState.project.activeProject
      }
    );

    if (data.status === "Error") {
      commit("setPredictorToast", {
        id: "predictor",
        type: "error",
        message: data.message
      });

      commit("setPredictorError", true);
      return [];
    }
    const predictors = map(data.data, predictor =>
      Predictor.$deployfromJson(predictor)
    );
    Predictor.create({
      data: predictors
    });
  },

 
  async getPredictorDeploymentStatus({ rootState, commit }, predictorId) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/users/predictorsId`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        predictorId: predictorId
      }
    );


      return data;

  },
  
  async deployModelKub({ rootState }, deployModel) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/users/predictors`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        deployPayload: deployModel,
        projectId: rootState.project.activeProject
      }
    );
  },

  async associatePredictor({ rootState }, predictor) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/associatePredictor`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        projectId: rootState.project.activeProject,
        predictorPayload: predictor
      }
    );
  },

  async deletePredictorAssociation({ rootState }, predictor) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/deleteAssociatePredictor`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        associationId: predictor.associationId
      }
    );
  },

  async updateAssociation({ rootState }, predictor) {
    predictor.projectId = rootState.project.activeProject;
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/updateAssociatePredictor`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        predictorId: predictor.predictorId,
        associationId: predictor.associationId,
        predictorPayload: predictor
      }
    );
  },

  async getPredictorsForModel({ rootState }, model) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/predictor/modelSelected`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.predictormSURL,
        modelId: model.modelId,
        modelVersion: model.version
      }
    );
  }
};
