import axios from "axios";
import { map } from "lodash-es";
import Dataset from "../../entities/datasource.entity";

export default {
  async allDatasets({ rootState, commit }) {

    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/datasets`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.datasourcemSURL,
        category: 'couch',
        namespace: 'acumosLatest',
        textSearch : ' '
      }
    );

    if (data.status === "Error") {
      commit(
        "app/setToastMessage",
        {
          id: "global",
          type: "error",
          message: data.message
        },
        { root: true }
      );

      commit("app/setGlobalError", true, { root: true });
      return [];
    }
 
    Dataset.create({
      data: map(data.data, dataset => Dataset.$fromJson(dataset))
    });
  },
  async sharedProjects({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/sharedProjects`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId: rootState.project.activeProject
      }
    );

    if (data.status === "Error") {
      commit(
        "app/setToastMessage",
        {
          id: "global",
          type: "error",
          message: data.message
        },
        { root: true }
      );

      commit("app/setGlobalError", true, { root: true });
      return [];
    }
    return map(data.data, project => new Project(Project.$fromJson(project)));
  },

  async createDataset({ rootState }, dataset) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/dataset/create`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.datasourcemSURL,
        newDatasetDetails: dataset
      }
    );
  },

  async archive({ rootState }, projectId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/archive`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId
      }
    );
  },

  async unarchive({ rootState }, projectId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/restore`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId
      }
    );
  },

  async deleteDataset({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/dataset/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.datasourcemSURL,
        datasourceKey: rootState.notebook.activeDataset,
      }
    );
  }
};
