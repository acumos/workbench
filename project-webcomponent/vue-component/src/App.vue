<template>
  <div class="flex flex-col font-sans">
    <ToastUI id="global"></ToastUI>
    <ConfirmUI></ConfirmUI>
    <div class="flex flex-wrap m-2">
      <div class="flex w-full justify-end">
        <div v-if="project">
          <button
            class="btn btn-secondary ml-2"
            @click="archiveProject(project)"
            v-if="project.status === 'ACTIVE'"
            v-tooltip="'Archive'"
            :disabled="!loginAsOwner"
          >
            <FAIcon icon="box"></FAIcon>
          </button>
          <template v-if="project.status === 'ARCHIVED'">
            <button
              class="btn btn-secondary ml-2"
              v-tooltip="'Unarchive'"
              @click="unarchiveProject(project)"
              :disabled="!loginAsOwner"
            >
              <FAIcon icon="box-open"></FAIcon>
            </button>
            <button
              class="btn btn-secondary ml-2 text-red-600"
              v-tooltip="'Delete Project'"
              @click="projectDelete(project)"
              :disabled="!loginAsOwner"
            >
              <FAIcon icon="trash-alt"></FAIcon>
            </button>
          </template>
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
      <template v-if="project.status !== 'ARCHIVED'">
        <NotebookList :notebooks="notebooks" class="my-5" />
        <template v-if="pipelineFlag === 'true'">
          <PipelineList :pipelines="pipelines" class="my-5" />
        </template>
        <ModelList :models="models" class="my-5" />
        <PredictorList :predictors="predictors" class="my-5" />
      </template>
    </div>
  </div>
</template>

<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined, isNull } from "lodash-es";
import Vue2Filters from "vue2-filters";

import Project from "./store/entities/project.entity";
import Notebook from "./store/entities/notebook.entity";
import Pipeline from "./store/entities/pipeline.entity";
import Model from "./store/entities/model.entity";

// UI Elements
import ToastUI from "./components/ui/Toast.ui";
import ConfirmUI from "./components/ui/Confirm.ui";
import ProjectDetails from "./components/project.details";
import NotebookList from "./components/notebook.list";
import PipelineList from "./components/pipeline.list";
import ModelList from "./components/model.list";
import PredictorList from "./components/predictor.list.vue";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    ProjectDetails,
    NotebookList,
    PipelineList,
    ModelList,
    PredictorList
  },
  mixins: [Vue2Filters.mixin],
  props: ["projectid", "componenturl", "authtoken", "username", "parentMsg"],
  computed: {
    ...mapState("app", [
      "wikiConfig",
      "componentUrl",
      "authToken",
      "userName",
      "pipelineFlag"
    ]),
    ...mapState("project", ["activeProject", "loginAsOwner"]),
    project() {
      const project = Project.query().first();

      return isNull(project) ? new Project() : project;
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
      await this.getDetails();
      await this.getProjectNotebooks();
      await this.getProjectPipelines();

      await this.getModelCategories();
      await this.getModelDetailsForProject();
    },
    ...mapMutations("app", [
      "setComponentUrl",
      "setAuthToken",
      "setUserName",
      "confirm"
    ]),
    ...mapMutations("project", ["setActiveProject"]),
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("project", [
      "getDetails",
      "archive",
      "unarchive",
      "deleteProject",
      "getProjectNotebooks",
      "getProjectPipelines"
    ]),
    ...mapActions("model", ["getModelCategories", "getModelDetailsForProject"]),
    async unarchiveProject(project) {
      this.confirm({
        message: "Are you sure you want to unarchive " + project.name + "?",
        onOk: async () => {
          const response = await this.unarchive(project.id);
          if (response.data.status === "Success") {
            await this.getDetails();
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "success"
            });
          } else {
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "error"
            });
          }
        }
      });
    },

    async archiveProject(project) {
      this.confirm({
        message: "Are you sure you want to archive " + project.name + "?",
        onOk: async () => {
          const response = await this.archive(project.id);
          if (response.data.status === "Success") {
            await this.getDetails();
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "success"
            });
          } else {
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "error"
            });
          }
        }
      });
    },
    async projectDelete(project) {
      this.confirm({
        message: "Are you sure you want to delete " + project.name + "?",
        onOk: async () => {
          const response = await this.deleteProject(project.id);
          if (response.data.status === "Success") {
            this.$emit("project-event", {
              detail: {
                data: "catalog-project"
              }
            });
          } else {
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "error"
            });
          }
        }
      });
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
@import "~vue-select/dist/vue-select.css";
@import "./assets/style/style.css";
</style>
