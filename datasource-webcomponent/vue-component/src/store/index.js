import Vue from "vue";
import Vuex from "vuex";
import VuexORM from "@vuex-orm/core";
import VuexORMAxios from "@vuex-orm/plugin-axios";

/** ORM */
import Dataset from "./entities/datasource.entity";

/** Modules */
import AppModule from "./modules/app";
import DatasourceModule from "./modules/datasource";

const database = new VuexORM.Database();

database.register(Dataset);

Vue.use(Vuex);
VuexORM.use(VuexORMAxios, { database });

export default new Vuex.Store({
  modules: {
    app: AppModule,
    datasource: DatasourceModule
  },
  plugins: [VuexORM.install(database)]
});
