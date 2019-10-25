import axios from "axios";
import { map } from "lodash-es";
import Notebook from "../../entities/notebook.entity";

export default {
  async allNotebooks({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/notebooks`,
      {
        url: rootState.app.msConfig.notebookmSURL,
        userName: rootState.app.userName
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
      data: map(data.data, notebook => Notebook.$fromJson(notebook))
    });
  },

  async createNotebook({ rootState }, notebook) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/notebook/create`,
      {
        url: rootState.app.msConfig.notebookmSURL,
        userName: rootState.app.userName,
        newNotebookDetails: notebook
      }
    );
  },

  async archiveNotebook({ rootState }, noteBookId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/notebook/archive`,
      {
        url: rootState.app.msConfig.notebookmSURL,
        userName: rootState.app.userName,
        noteBookId
      }
    );
  },

  async restoreNotebook({ rootState }, noteBookId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/notebook/restore`,
      {
        url: rootState.app.msConfig.notebookmSURL,
        userName: rootState.app.userName,
        noteBookId
      }
    );
  },

  async deleteNotebook({ rootState }, noteBookId) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/notebook/delete`,
      {
        url: rootState.app.msConfig.notebookmSURL,
        userName: rootState.app.userName,
        noteBookId
      }
    );
  }
};
