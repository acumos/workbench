<template>
  <div class="flex flex-col font-sans">
    <div class="flex flex-wrap m-2">
      <div class="flex w-full justify-end">
        <div>
          <a
            class="btn btn-secondary text-black ml-2"
            @click="archiveProject(project)"
          >
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

export default {
  name: "app",
  components: { ProjectDetails, NotebookList, PipelineList, ModelList },
  mixins: [Vue2Filters.mixin],
  props: ["projectid", "componenturl", "authtoken", "username", "parentMsg"],
  computed: {
    ...mapState("app", ["wikiConfig", "componentUrl", "authToken", "userName"]),
    ...mapState("project", ["activeProjct"]),
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
    }
  },
  watch: {
    componenturl(componenturl) {
      this.setComponentUrl(componenturl);
      this.init();
    },
    username(username) {
      this.setUserName(username);
      this.init();
    },
    authtoken(authtoken) {
      this.setAuthToken(authtoken);
      this.init();
    },
    projectid(projectid) {
      this.setActiveProject(projectid);
      this.init();
    }
  },
  created() {
    this.init();
  },
  methods: {
    async init() {
      if (isUndefined(this.username) || isUndefined(this.authtoken)) {
        return;
      }

      this.setComponentUrl(this.componenturl || process.env.VUE_APP_API);
      this.setUserName(this.username);
      this.setAuthToken(this.authtoken);
      this.setActiveProject(this.projectid || process.env.VUE_APP_PROJECT_ID);

      console.log(this.componenturl);
      console.log(this.username);

      await this.getConfig();
      await this.getDetails(this.projectid);
      await this.getProjectNotebooks(this.projectid);
      await this.getProjectPipelines(this.projectid);
    },
    ...mapMutations("app", [
      "setComponentUrl",
      "setAuthToken",
      "setUserName",
      "resetAppState"
    ]),
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
  },
  destroyed() {
    this.resetAppState();
  }
};
</script>

<style>
@import "./assets/style/style.css";
</style>
