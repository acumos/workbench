import axios from "axios";
import { differenceWith } from "lodash-es";

import Pipeline from "../../entities/pipeline.entity";

export default {
  async getAllPipelines({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/pipelines`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL
      }
    );

    const allPipelines = data.data.map(pipeline =>
      Pipeline.$fromJson(pipeline)
    );
    const projectPipelines = Pipeline.all();
    return differenceWith(
      allPipelines,
      projectPipelines,
      (pipeline, projectPipeline) =>
        pipeline.id === projectPipeline.id &&
        pipeline.version === projectPipeline.version
    );
  },

  async associatePipeline({ rootState }, pipeline) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/associatePipeline`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        pipelinePayload: pipeline.$toJson(),
        projectId: rootState.project.activeProject,
        pipelineId: pipeline.id
      }
    );
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

  async archivePipeline({ rootState }, pipeline) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/archivePipeline`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipeline.id
      }
    );
  },

  async restorePipeline({ rootState }, pipeline) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/project/restorePipeline`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipeline.id
      }
    );
  },

  async deletePipelineAssociation({ rootState }, pipeline) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/deleteAssociation`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipeline.id
      }
    );
  },

  async launchPipeline({ rootState }, pipeline) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/launch`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.pipelinemSURL,
        projectId: rootState.project.activeProject,
        pipelineId: pipeline.id
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
  }
};
