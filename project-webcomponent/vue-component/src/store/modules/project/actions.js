import axios from "axios";
import { get, map } from "lodash-es";
import Project from "../../entities/project.entity";
import Notebook from "../../entities/notebook.entity";
import Pipeline from "../../entities/pipeline.entity";

export default {
  async getDetails({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/details`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId: rootState.project.activeProject
      }
    );
    Project.create({
      data: {
        id: get(data, "data.projectId.uuid"),
        name: get(data, "data.projectId.name"),
        version: get(data, "data.projectId.versionId.label"),
        status: get(data, "data.artifactStatus.status"),
        creationDate: get(data, "data.projectId.versionId.creationTimeStamp"),
        description: get(data, "data.description")
      }
    });
  },
  async getProjectNotebooks({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/notebooksList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        projectId: rootState.project.activeProject
      }
    );

    Notebook.create({
      data: map(data.data, notebook => Notebook.$fromJson(notebook))
    });
  },
  async getProjectPipelines({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/pipelinesList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject
      }
    );

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
    await axios.put(`${rootState.app.componentUrl}/api/project/archive`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId
    });
  },

  async unarchive({ rootState }, projectId) {
    await axios.put(`${rootState.app.componentUrl}/api/project/restore`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId
    });
  },
    
  async deleteProject({ rootState }){
    await axios.delete(`${rootState.app.componentUrl}/api/project/delete`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId: rootState.project.activeProject
    })
  },

  async shareProjectToUsers({ rootState }, user){
    return await axios.post(`${rootState.app.componentUrl}/api/project/addUser`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId: rootState.project.activeProject,
      users: user
    });
  },

  async deleteSharedUserFromProject({ rootState }, user){
    return await axios.post(`${rootState.app.componentUrl}/api/project/removeUser`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId: rootState.project.activeProject,
      user: user
    });
  },

  async sharedProjectsForUser({ rootState }){
    await axios.post(`${rootState.app.componentUrl}/api/project/sharedProjects`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL
    });
  },

  async getUsersList({ rootState }){
    return await axios.post(`${rootState.app.componentUrl}/api/users/userList`, {
      userName: rootState.app.userName,
      url: rootState.app.portalFEUrl
    });
    
  }
};
