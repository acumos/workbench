import axios from "axios";
import { map } from "lodash-es";

import Pipeline from "../../entities/pipeline.entity";

export default {
  async getAllPipelines({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipelines`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL
      }
    );
  },

  async associatePipeline({ rootState }, pipeline){
    return await axios.put(`${rootState.app.componentUrl}/api/project/associatePipeline`,
    {
      userName: rootState.app.userName,
      url: rootState.app.msConfig.pipelinemSURL,
      pipelinePayload: pipeline.$toJson(),
      projectId: rootState.project.activeProject,
      pipelineId: pipeline.id
    });
  },

  async createPipeline({ rootState }, pipeline) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/createPipeline`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        newPipelineDetails: pipeline,
        projectId: rootState.project.activeProject
      }
    );
  },

  async updatePipeline({ rootState }, pipeline) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/pipeline/update`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        pipelinePayload: pipeline,
        pipelineId: pipeline.pipelineId.uuid
      }
    );
  },

  async archivePipeline({ rootState }, pipelineId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/archivePipeline`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipelineId
      }
    );
  },

  async restorePipeline({ rootState }, pipelineId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/restorePipeline`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipelineId
      }
    );
  },

  async deletePipeline({ rootState }, pipelineId) {
    return await axios.delete(
      `${rootState.app.componentUrl}/api/pipeline/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        pipelineId: pipelineId
      }
    );
  },

  async launchPipeline({ rootState }, pipelineId) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/launch`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipelineId
      }
    );
  },

  async createPipelineStatus({ rootState }, pipelineId) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/project/createPipelineStatus`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipelineId
      }
    );
  },
};
