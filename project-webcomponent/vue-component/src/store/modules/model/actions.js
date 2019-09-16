import axios from "axios";
import { get, map, update, differenceWith } from "lodash-es";
import Project from "../../entities/project.entity";
import Model from "../../entities/model.entity";

export default {
  async getModelCatalogs({ rootState }) {
    return await axios.post(`${rootState.app.componentUrl}/api/models/getAllModels`, {
      url: rootState.app.msConfig.portalBEUrl
    })
  },

  async getModelCategories({ rootState, commit }) {
    const { data } = await axios.post(`${rootState.app.componentUrl}/api/models/category`, {
      url: rootState.app.portalBEUrl
    });

    commit("setCategories", data.data);
  },

  async getModelDetailsForProject({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/models/getProjectModels`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.modelmSURL,
        projectId: rootState.project.activeProject
      }
    );

    const models = map(data.data, model => Model.$fromJson(model));
    Model.create({
      data: models
    })
  },

  async associateModel({ rootState }, model) {
    return await axios.post(`${rootState.app.componentUrl}/api/models/associateModel`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.modelmSURL,
      projectId: rootState.project.activeProject,
      modelId: model.modelId.uuid,
      modelPayload: model
    })
  },

  async deleteAssociation({ rootState }, model) {
    return await axios.post(`${rootState.app.componentUrl}/api/models/deleteAssociateModel`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.modelmSURL,
      projectId: rootState.project.activeProject,
      modelId: model.modelId.uuid,
      modelPayload: model
    })
  },

  async getAllModels({ rootState }) {
    const { data } = await axios.post(`${rootState.app.componentUrl}/api/models/getAllModels`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.modelmSURL
    })

    const allModels = data.data.map(model => Model.$fromJson(model));
    const projectModels = Model.all();
    return differenceWith(allModels, projectModels, (model, projectModel) =>
      model.modelId === projectModel.modelId && model.version === projectModel.version
    )

  },

  async updateAssociation({ rootState }, model) {
    return await axios.put(`${rootState.app.componentUrl}/api/models/updateAssociateModel`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.modelmSURL,
      projectId: rootState.project.activeProject,
      modelId: model.modelId.uuid,
      modelPayload: model
    })
  }
};
