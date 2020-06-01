<template>
  <div>
    <ToastUI v-if="globalError" id="global" />
    <div v-if="!globalError">
      <div class="flex flex-col font-sans">
        <ToastUI id="global"></ToastUI>
        <ConfirmUI></ConfirmUI>
        <div class="flex flex-wrap m-2">
          <div class="flex w-full justify-end">
            <div v-if="pipeline">
              <button
                class="btn btn-primary ml-2"
                @click="pipelineLaunch(pipeline)"
                v-if="pipeline.status === 'ACTIVE'"
                title="Launch Pipeline"
              >
                <FAIcon icon="external-link-alt"></FAIcon>
              </button>
              <button
                class="btn btn-secondary ml-2"
                @click="pipelineArchive(pipeline)"
                v-if="pipeline.status === 'ACTIVE'"
                title="Archive Pipeline'"
              >
                <FAIcon icon="box"></FAIcon>
              </button>
              <template v-if="pipeline.status === 'ARCHIVED'">
                <button
                  class="btn btn-secondary ml-2"
                  title="Unarchive Pipeline"
                  @click="unarchivePipeline(pipeline)"
                >
                  <FAIcon icon="box-open"></FAIcon>
                </button>
                <button
                  class="btn btn-secondary ml-2 text-red-600"
                  title="Delete Pipeline"
                  @click="pipelineDelete(pipeline)"
                >
                  <FAIcon icon="trash-alt"></FAIcon>
                </button>
              </template>
              <template v-if="pipeline.status === 'FAILED'">
                <button
                  class="btn btn-secondary ml-2 text-red-600"
                  title="Delete Pipeline"
                  @click="pipelineDelete(pipeline)"
                >
                  <FAIcon icon="trash-alt"></FAIcon>
                </button>
              </template>
              <template v-if="pipeline.status === 'INPROGRESS'"></template>
              <a
                :href="pipelineWikiURL"
                target="_blank"
                class="btn btn-secondary text-black ml-2"
                title="Learn More"
              >
                <FAIcon icon="question-circle"></FAIcon>
              </a>
            </div>
          </div>
          <PipelineDetails :pipeline="pipeline" v-if="pipeline" class="my-5" />
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined } from "lodash-es";
import Vue2Filters from "vue2-filters";

import Pipeline from "./store/entities/pipeline.entity";

// UI Elements
import ToastUI from "./vue-common/components/ui/Toast.ui";
import ConfirmUI from "./vue-common/components/ui/Confirm.ui";
import PipelineDetails from "./components/pipeline.details";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    PipelineDetails
  },
  mixins: [Vue2Filters.mixin],
  props: ["pipelineid", "componenturl", "authtoken", "username"],
  computed: {
    ...mapState("app", [
      "pipelineWikiURL",
      "componentUrl",
      "authToken",
      "userName",
      "globalError"
    ]),
    // ...mapState("project", ["activeProject", "loginAsOwner"]),
    pipeline() {
      return Pipeline.query().first();
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
        this.setActivePipeline(process.env.VUE_APP_PIPELINE_ID);
      } else if (!isUndefined(this.username) && !isUndefined(this.authtoken)) {
        this.setComponentUrl(this.componenturl);
        this.setUserName(this.username);
        this.setAuthToken(this.authtoken);
        this.setActivePipeline(this.pipelineid);
      }

      await this.getConfig();
      if (this.userName !== "" && this.authToken !== "") {
        await this.getPipelineDetails();
      } else {
        this.setToastMessage({
          id: "global",
          type: "error",
          message:
            "Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here.."
        });
        this.setGlobalError(true);
      }
      
      this.$emit("on-load-event");
    },
    ...mapMutations("app", [
      "setComponentUrl",
      "setAuthToken",
      "setUserName",
      "confirm",
      "setToastMessage",
      "setGlobalError"
    ]),
    ...mapMutations("pipeline", ["setActivePipeline"]),
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("pipeline", [
      "getPipelineDetails",
      "archivePipeline",
      "restorePipeline",
      "deletePipeline",
      "launchPipeline"
    ]),

    async unarchivePipeline(pipeline) {
      this.confirm({
        title: "Unarchive " + pipeline.name,
        body: "Are you sure you want to unarchive " + pipeline.name + "?",
        options: {
          okLabel: "Unarchive Pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.restorePipeline(pipeline.id);
          if (response.data.status === "Success") {
            await this.getPipelineDetails();
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

    async pipelineArchive(pipeline) {
      this.confirm({
        title: "Archive " + pipeline.name,
        body: "Are you sure you want to archive " + pipeline.name + "?",
        options: {
          okLabel: "Archive Pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archivePipeline(pipeline.id);
          if (response.data.status === "Success") {
            await this.getPipelineDetails();
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
    async pipelineDelete(pipeline) {
      this.confirm({
        title: "Delete " + pipeline.name,
        body: "Are you sure you want to delete " + pipeline.name + "?",
        options: {
          okLabel: "Delete Pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deletePipeline(pipeline.id);
          if (response.data.status === "Success") {
            this.$emit("pipeline-event", {
              data: "catalog-pipeline"
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
    async pipelineLaunch(pipeline) {
      let response = await this.launchPipeline();
      let responseJson = JSON.parse(response.data.data);
      if (response.data.status === "Success") {
        let launchURL = responseJson.pipelineId.serviceUrl;
        window.open(launchURL, "_blank");
      } else {
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "error"
        });
      }
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
