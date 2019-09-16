import axios from "axios";
import { get, map } from "lodash-es";
import Project from "../../entities/project.entity";
import Notebook from "../../entities/notebook.entity";
import Pipeline from "../../entities/pipeline.entity";

export default {
  async getDetails({ rootState }, projectId) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/details`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.projectmSURL,
        projectId
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
  async getProjectNotebooks({ rootState }, projectId) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/notebooksList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL,
        projectId
      }
    );

    Notebook.create({
      data: map(data.data, notebook => Notebook.$fromJson(notebook))
    });
  },
  async getProjectPipelines({ rootState }, projectId) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/project/pipelinesList`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId
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
  }
};
