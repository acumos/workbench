import axios from "axios";
import { get, map } from "lodash-es";
import Project from "../../entities/project.entity";
import Notebook from "../../entities/notebook.entity";
import Pipeline from '../../entities/pipeline.entity';

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
        creationDate: get(data, "data.projectId.versionId.timeStamp"),
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
    const notebooks = map(data.data, notebook => ({
      id: notebook.noteBookId.uuid,
      name: notebook.noteBookId.name,
      version: notebook.noteBookId.versionId.label,
      type: notebook.notebookType,
      creationDate: notebook.noteBookId.versionId.timeStamp
    }));
    Notebook.create({
      data: notebooks
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
    const pipelines = map(data.data, notebook => ({
      id: notebook.noteBookId.uuid,
      name: notebook.noteBookId.name,
      version: notebook.noteBookId.versionId.label,
      type: notebook.notebookType,
      creationDate: notebook.noteBookId.versionId.timeStamp
    }));

    Pipeline.create({
      data: pipelines
    });
  },
  async update({ rootState }, project) {
    await axios.put(`${rootState.app.componentUrl}/api/project/update`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId: project.id,
      projectPayload: project
    });
  },
  async archive({ rootState }, projectId) {
    await axios.put(`${rootState.app.componentUrl}/api/project/archive`, {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.projectmSURL,
      projectId
    });
  }
};
