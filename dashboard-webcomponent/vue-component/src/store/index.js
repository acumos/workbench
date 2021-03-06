import Vue from "vue";
import Vuex from "vuex";

/** Modules */
import AppModule from "./modules/app";
import PipelineModule from "./modules/pipeline";
import ModelModule from "./modules/model";
import NotebookModule from "./modules/notebook";
import ProjectModule from "./modules/project";
import DatasourceModule from "./modules/datasource";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    app: AppModule,
    pipeline: PipelineModule,
    project: ProjectModule,
    notebook: NotebookModule,
    datasource: DatasourceModule,
    model: ModelModule
  }
});
