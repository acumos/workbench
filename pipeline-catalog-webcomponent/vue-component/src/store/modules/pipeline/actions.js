import axios from "axios";
import { map } from "lodash-es";
import Pipeline from "../../entities/pipeline.entity";

export default {
  async allPipelines({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/pipelines`,
      {
        url: rootState.app.msConfig.pipelinemSURL,
        userName: rootState.app.userName
      }
    );

    Pipeline.create({
      data: map(data.data, pipeline => Pipeline.$fromJson(pipeline))
    });
  },

  async createPipeline({ rootState }, pipeline) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/create`,
      {
        url: rootState.app.msConfig.pipelinemSURL,
        userName: rootState.app.userName,
        newPipelineDetails: pipeline
      }
    );
  },

  async archivePipeline({ rootState }, pipelineId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/pipeline/archive`,
      {
        url: rootState.app.msConfig.pipelinemSURL,
        userName: rootState.app.userName,
        pipelineId
      }
    );
  },

  async restorePipeline({ rootState }, pipelineId) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/pipeline/restore`,
      {
        url: rootState.app.msConfig.pipelinemSURL,
        userName: rootState.app.userName,
        pipelineId
      }
    );
  },

  async deletePipeline({ rootState }, pipelineId) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/delete`,
      {
        url: rootState.app.msConfig.pipelinemSURL,
        userName: rootState.app.userName,
        pipelineId
      }
    );
  }
};
