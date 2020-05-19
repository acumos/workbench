import axios from "axios";
import { get } from "lodash-es";

import Dataset from "../../entities/datasource.entity";

export default {
  async getDatasetDetails({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/dataset/details`,
      {
        userName: rootState.app.userName,
        url: rootState.app.datasourcemSURL,
        datasourceKey: rootState.datasource.activeDataset,
        
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
      data: {
        _id: get(data, "data._id"),
        name: get(data, "data.datasourceId.name"),
        version: get(data, "data.datasourceId.versionId.label"),
        category: get(data, "data.category"),
        readWriteDescriptor: get(data, "data.readWriteDescriptor"),
        datasourceDescription: get(data, "data.datasourceDescription"),
        dbServerUsername: get(data, "data.dbDetails.dbServerUsername"),
        dbServerPassword: get(data, "data.dbDetails.dbServerPassword"),
        databaseName: get(data, "data.dbDetails.databaseName"),
        dbQuery: get(data, "data.dbDetails.dbQuery"),
        datasourceId: get(data, "data.datasourceId.uuid"),
        serverName: get(data, "data.commonDetails.serverName"),
        portNumber: get(data, "data.commonDetails.portNumber"),
        jdbcUrl : get(data, "data.commonDetails.serverName") + ":" + get(data, "data.commonDetails.portNumber"),
        // creationDate: get(data, "data.noteBookId.versionId.creationTimeStamp"),
        // modifiedDate: get(data, "data.noteBookId.versionId.modifiedTimeStamp"),
        // description: get(data, "data.description"),
        // url: get(data, "data.noteBookId.serviceUrl")
      }
    });
  },

  async updateDataset({ rootState }, dataset) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/dataset/update`,
      {
        userName: rootState.app.userName,
        url: rootState.app.datasourcemSURL,
        datasetPayload: dataset,
        datasourceKey: rootState.datasource.activeDataset,
      }
    );
  },

  async archiveNotebook({ rootState }) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/notebook/archive`,
      {
        userName: rootState.app.userName,
        url: rootState.app.notebookmSURL,
        noteBookId: rootState.notebook.activeNotebook
      }
    );
  },

  async restoreNotebook({ rootState }) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/notebook/restore`,
      {
        userName: rootState.app.userName,
        url: rootState.app.notebookmSURL,
        noteBookId: rootState.notebook.activeNotebook
      }
    );
  },

  async deleteDataset({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/dataset/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.datasourcemSURL,
        datasourceKey: rootState.datasource.activeDataset,
      }
    );
  },

  async launchNotebook({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/notebook/launch`,
      {
        userName: rootState.app.userName,
        url: rootState.app.notebookmSURL,
        noteBookId: rootState.notebook.activeNotebook
      }
    );
  }
};
