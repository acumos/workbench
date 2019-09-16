import axios from "axios";
import { map } from "lodash-es";

import Notebook from "../../entities/notebook.entity";

export default {
  async getAllNotebooks({ rootState }) {
    return await axios.post(`${rootState.app.componentUrl}/api/notebooks`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.notebookmSURL
    });
  },

  async associateNotebook({ rootState }, selectedNotebook) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/associateNotebook`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        notebookPayload: selectedNotebook.$toJson(),
        projectId: rootState.project.activeProject,
        notebookId: selectedNotebook.id
      }
    );
  },

  async createNotebook({ rootState }, notebook) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/createNotebook`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        newNotebookDetails: notebook,
        projectId: rootState.project.activeProject
      }
    );
  },

  async updateNotebook({ rootState }, notebook) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/notebook/update`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        notebookPayload: notebook,
        notebookId: notebook.notebookId.uuid
      }
    );
  },

  async archiveNotebook({ rootState }, notebook) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/archiveNotebook`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        projectId: rootState.project.activeProject,
        notebookId: notebook.id
      }
    );
  },

  async restoreNotebook({ rootState }, notebook) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/restoreNotebook`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        projectId: rootState.project.activeProject,
        notebookId: notebook.id
      }
    );
  },

  async deleteNotebook({ rootState }, notebook) {
    return await axios.delete(
      `${rootState.app.componentUrl}/api/project/deleteNotebook`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        notebookId: notebook.id
      }
    );
  },

  async launchNotebook({ rootState }, notebook) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/notebook/launch`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        projectId: rootState.project.activeProject,
        notebookId: notebook.id
      }
    );
  }
};
