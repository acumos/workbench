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
import Project from "./store/entities/project.entity";
import Notebook from "./store/entities/notebook.entity";
import Pipeline from "./store/entities/pipeline.entity";
import Model from "./store/entities/model.entity";

import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
// import { mapFields } from "vuex-map-fields";
import Vue2Filters from "vue2-filters";

import ProjectDetails from "./components/project.details";
import NotebookList from "./components/notebook.list";
import PipelineList from "./components/pipeline.list";
import ModelList from "./components/model.list";

export default {
  name: "app",
  components: { ProjectDetails, NotebookList, PipelineList, ModelList },
  mixins: [Vue2Filters.mixin],
  props: ["projectId", "componenturl", "authToken", "userName", "parentMsg"],
  computed: {
    ...mapState("app", ["wikiConfig"]),
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
  async created() {
    if (this.componenturl) {
      this.setComponentUrl(this.componenturl);
    } else {
      const componentUrl = process.env.VUE_APP_API;
      this.setComponentUrl(componentUrl);
    }

    await this.getConfig();
    await this.getDetails("1ea5c88b-aadf-49bf-8ad7-38df3a0f201a");
    await this.getProjectNotebooks("1ea5c88b-aadf-49bf-8ad7-38df3a0f201a");
    await this.getProjectPipelines("1ea5c88b-aadf-49bf-8ad7-38df3a0f201a");
  },
  methods: {
    ...mapMutations("app", ["setComponentUrl"]),
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
