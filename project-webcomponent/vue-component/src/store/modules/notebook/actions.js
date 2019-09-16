import axios from "axios";
import { map } from "lodash-es";

import Notebook from "../../entities/notebook.entity";

export default {
  async getAllNotebooks({ rootState }) {
    const { data } = await axios.post(
      `${rootState.app.componentUrl}/api/notebooks`,
      {
        userName: rootState.app.userName,
        url: rootState.app.msConfig.notebookmSURL
      }
    );
    Notebook.create({
      data: map(data.data, notebook => Notebook.$fromJson(notebook))
    });
  }
};
