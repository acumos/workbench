import axios from "axios";
import { get } from "lodash-es";

import Pipeline from "../../entities/pipeline.entity";

export default {
  async getPipelineDetails({ rootState, commit }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/details`,
      {
        userName: rootState.app.userName,
        url: rootState.app.pipelinemSURL,
        pipelineId: rootState.pipeline.activePipeline
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
    Pipeline.create({
      data: {
        id: get(data, "data.pipelineId.uuid"),
        name: get(data, "data.pipelineId.name"),
        version: get(data, "data.pipelineId.versionId.label"),
        status: get(data, "data.artifactStatus.status"),
        creationDate: get(data, "data.pipelineId.versionId.creationTimeStamp"),
        modifiedDate: get(data, "data.pipelineId.versionId.modifiedTimeStamp"),
        description: get(data, "data.description"),
        url: get(data, "data.pipelineId.serviceUrl")
      }
    });
  },

  async updatePipeline({ rootState }, pipeline) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/pipeline/update`,
      {
        userName: rootState.app.userName,
        url: rootState.app.pipelinemSURL,
        pipelinePayload: pipeline,
        pipelineId: pipeline.pipelineId.uuid
      }
    );
  },

  async archivePipeline({ rootState }) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/pipeline/archive`,
      {
        userName: rootState.app.userName,
        url: rootState.app.pipelinemSURL,
        pipelineId: rootState.pipeline.activePipeline
      }
    );
  },

  async restorePipeline({ rootState }) {
    return await axios.put(
      `${rootState.app.componentUrl}/api/pipeline/restore`,
      {
        userName: rootState.app.userName,
        url: rootState.app.pipelinemSURL,
        pipelineId: rootState.pipeline.activePipeline
      }
    );
  },

  async deletePipeline({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/delete`,
      {
        userName: rootState.app.userName,
        url: rootState.app.pipelinemSURL,
        pipelineId: rootState.pipeline.activePipeline
      }
    );
  },

  async launchPipeline({ rootState }) {
    return await axios.post(
      `${rootState.app.componentUrl}/api/pipeline/launch`,
      {
        userName: rootState.app.userName,
        url: rootState.app.pipelinemSURL,
        pipelineId: rootState.pipeline.activePipeline
      }
    );
  }
};
