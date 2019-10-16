import axios from "axios";
import { map } from "lodash-es";
import Project from "../../entities/project.entity";

export default {
  async allProjects({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/projects`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId: rootState.project.activeProject
      }
    );

    Project.create({
      data: map(data.data, project => Project.$fromJson(project))
    });
  },
  async sharedProjects({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/sharedProjects`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId: rootState.project.activeProject
      }
    );

    return await map(
      data.data,
      project => new Project(Project.$fromJson(project))
    );
  },

  async createProject({ rootState }, project) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/create`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        newProjectDetails: project
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

  async deleteProject({ rootState }, projectId) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId
      }
    );
  }
};
