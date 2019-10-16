import Vue from "vue";
import Vuex from "vuex";
import VuexORM from "@vuex-orm/core";
import VuexORMAxios from "@vuex-orm/plugin-axios";

/** ORM */
import Pipeline from "./entities/pipeline.entity";

/** Modules */
import AppModule from "./modules/app";
import PipelineModule from "./modules/pipeline";

const database = new VuexORM.Database();

database.register(Pipeline);

Vue.use(Vuex);
VuexORM.use(VuexORMAxios, { database });

export default new Vuex.Store({
  modules: {
    app: AppModule,
    pipeline: PipelineModule
  },
  plugins: [VuexORM.install(database)]
});
