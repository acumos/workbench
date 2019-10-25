import axios from "axios";
import { get } from "lodash-es";

import Notebook from "../../entities/notebook.entity";

export default {
  async getNotebookDetails({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/notebook/details`,
      {
        userName: rootState.app.userName,
        url: rootState.app.notebookmSURL,
        noteBookId: rootState.notebook.activeNotebook
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

    Notebook.create({
      data: {
        id: get(data, "data.noteBookId.uuid"),
        name: get(data, "data.noteBookId.name"),
        version: get(data, "data.noteBookId.versionId.label"),
        status: get(data, "data.artifactStatus.status"),
        creationDate: get(data, "data.noteBookId.versionId.creationTimeStamp"),
        modifiedDate: get(data, "data.noteBookId.versionId.modifiedTimeStamp"),
        description: get(data, "data.description"),
        url: get(data, "data.noteBookId.serviceUrl")
      }
    });
  },

  async updateNotebook({ rootState }, notebook) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/notebook/update`,
      {
        userName: rootState.app.userName,
        url: rootState.app.notebookmSURL,
        notebookPayload: notebook,
        noteBookId: notebook.noteBookId.uuid
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

  async deleteNotebook({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/notebook/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.notebookmSURL,
        noteBookId: rootState.notebook.activeNotebook
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
