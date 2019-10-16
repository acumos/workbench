import Vue from "vue";
import Vuex from "vuex";
import VuexORM from "@vuex-orm/core";
import VuexORMAxios from "@vuex-orm/plugin-axios";

/** ORM */
import Notebook from "./entities/notebook.entity";

/** Modules */
import AppModule from "./modules/app";
import NotebookModule from "./modules/notebook";

const database = new VuexORM.Database();

database.register(Notebook);

Vue.use(Vuex);
VuexORM.use(VuexORMAxios, { database });

export default new Vuex.Store({
  modules: {
    app: AppModule,
    notebook: NotebookModule
  },
  plugins: [VuexORM.install(database)]
});
