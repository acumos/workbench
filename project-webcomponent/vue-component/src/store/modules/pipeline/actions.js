import axios from "axios";
import { map } from "lodash-es";

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
    Pipeline.create({
      data: map(data.data, pipeline => Pipeline.$fromJson(pipeline))
    });
  }
};
