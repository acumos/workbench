import axios from "axios";
import { get, map } from "lodash-es";
import Project from "../../entities/project.entity";
import Notebook from "../../entities/notebook.entity";
import Pipeline from "../../entities/pipeline.entity";

export default {
  async getDetails({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/details`,
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

    commit(
      "setLoginAsOwner",
      rootState.app.userName === data.data.owner.authenticatedUserId
        ? true
        : false
    );
    Project.create({
      data: {
        id: get(data, "data.projectId.uuid"),
        name: get(data, "data.projectId.name"),
        version: get(data, "data.projectId.versionId.label"),
        status: get(data, "data.artifactStatus.status"),
        creationDate: get(data, "data.projectId.versionId.creationTimeStamp"),
        modifiedDate: get(data, "data.projectId.versionId.modifiedTimeStamp"),
        description: get(data, "data.description"),
        collaborators: get(data, "data.collaborators"),
        owner: get(data, "data.owner.authenticatedUserId")
      }
    });
  },
  async getProjectNotebooks({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/notebooksList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        projectId: rootState.project.activeProject
      }
    );

    if (data.status === "Error") {
      commit(
        "notebook/setNotebookToast",
        {
          id: "notebook",
          type: "error",
          message: data.message
        },
        { root: true }
      );

      commit("notebook/setNotebookError", true, { root: true });
      return [];
    }
    Notebook.create({
      data: map(data.data, notebook => Notebook.$fromJson(notebook))
    });
  },
  async getProjectPipelines({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/pipelinesList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject
      }
    );

    if (data.status === "Error") {
      commit(
        "pipeline/setPipelineToast",
        {
          id: "pipeline",
          type: "error",
          message: data.message
        },
        { root: true }
      );

      commit("pipeline/setPipelineError", true, { root: true });
      return [];
    }
    Pipeline.create({
      data: map(data.data, pipeline => Pipeline.$fromJson(pipeline))
    });
  },
  async updateProject({ rootState }, project) {
    return await axios.put(`${rootState.app.componentUrl}/api/project/update`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId: rootState.project.activeProject,
      projectPayload: project
    });
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

  async deleteProject({ rootState }, projectId) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId
      }
    );
  },

  async shareProjectToUsers({ rootState }, user) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/addUser`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId: rootState.project.activeProject,
        users: user
      }
    );
  },

  async deleteSharedUserFromProject({ rootState }, user) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/removeUser`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId: rootState.project.activeProject,
        user: user
      }
    );
  },

  async sharedProjectsForUser({ rootState }) {
    await axios.post(
      `${rootState.app.componentUrl}/api/project/sharedProjects`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL
      }
    );
  },

  async getUsersList({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/users/userList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.portalBEUrl
      }
    );
  }
};
