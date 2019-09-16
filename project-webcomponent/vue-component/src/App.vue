<template>
  <div class="flex flex-col font-sans">
    <div class="flex flex-wrap m-2">
      <div class="flex w-full justify-end">
        <div>
          <a class="btn btn-primary">Create Project</a>
          <a class="btn btn-secondary ml-2" @click="archiveProject(project)">
            <FAIcon icon="archive"></FAIcon>
          </a>
          <a
            :href="wikiConfig.projectWikiURL"
            target="_blank"
            class="btn btn-secondary text-black ml-2"
          >
            <FAIcon icon="question-circle"></FAIcon>
          </a>
        </div>
      </div>
      <ProjectDetails :project="project" class="my-5" />
      <NotebookList :notebooks="notebooks" class="my-5" />
      <PipelineList :pipelines="pipelines" class="my-5" />
      <ModelList :models="models" class="my-5" />
      <PredictorList :predictors="predictors" class="my-5" />
    </div>
  </div>
</template>

<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined } from "lodash-es";
import Vue2Filters from "vue2-filters";

import Project from "./store/entities/project.entity";
import Notebook from "./store/entities/notebook.entity";
import Pipeline from "./store/entities/pipeline.entity";
import Model from "./store/entities/model.entity";

import ProjectDetails from "./components/project.details";
import NotebookList from "./components/notebook.list";
import PipelineList from "./components/pipeline.list";
import ModelList from "./components/model.list";
import PredictorList from "./components/predictor.list.vue";

export default {
  name: "app",
  components: {
    ProjectDetails,
    NotebookList,
    PipelineList,
    ModelList,
    PredictorList
  },
  mixins: [Vue2Filters.mixin],
  props: ["projectid", "componenturl", "authtoken", "username", "parentMsg"],
  computed: {
    ...mapState("app", ["wikiConfig", "componentUrl", "authToken", "userName"]),
    ...mapState("project", ["activeProject"]),
    project() {
      return Project.query().first();
    },
    notebooks() {
      return Notebook.all();
    },
    pipelines() {
      return Pipeline.all();
    },
    models() {
      return Model.all();
    },
    predictors() {
      return [];
    }
  },
  watch: {
    username() {
      this.init();
    },
    authtoken() {
      this.init();
    }
  },
  created() {
    this.init();
  },
  methods: {
    async init() {
      // If running locally use environment config
      if (process.env.VUE_APP_ENV === "local") {
        this.setComponentUrl(process.env.VUE_APP_COMPONENT_API);
        this.setUserName(process.env.VUE_APP_USERNAME);
        this.setAuthToken(process.env.VUE_APP_AUTHTOKEN);
        this.setActiveProject(process.env.VUE_APP_PROJECT_ID);
      } else if (!isUndefined(this.username) && !isUndefined(this.authtoken)) {
        this.setComponentUrl(this.componenturl);
        this.setUserName(this.username);
        this.setAuthToken(this.authtoken);
        this.setActiveProject(this.projectid);
      }

      await this.getConfig();
      await this.getDetails(this.activeProject);
      await this.getProjectNotebooks(this.activeProject);
      await this.getProjectPipelines(this.activeProject);
    },
    ...mapMutations("app", ["setComponentUrl", "setAuthToken", "setUserName"]),
    ...mapMutations("project", ["setActiveProject"]),
    ...mapActions("app", ["getConfig"]),
    ...mapActions("project", [
      "getDetails",
      "archive",
      "getProjectNotebooks",
      "getProjectPipelines"
    ]),
    async archiveProject(project) {
      await this.archive(project.id);
      await this.getDetails(project.id);
    }
  },

  mounted() {
    if (process.env.NODE_ENV === "production") {
      // This will only work on your root Vue component since it's using $parent
      const { shadowRoot } = this.$parent.$options;
      const id = "fa-styles";
      if (shadowRoot) {
        if (!shadowRoot.getElementById(`${id}`)) {
          const faStyles = document.createElement("style");
          faStyles.setAttribute("id", id);
          faStyles.textContent = dom.css();
          shadowRoot.appendChild(faStyles);
        }
      }
    }
  }
};
</script>

<style>
@import "./assets/style/style.css";
</style>
