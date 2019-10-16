<template>
  <div class="flex flex-col font-sans" id="dashboard-webcomponent">
    <ToastUI id="global"></ToastUI>
    <ConfirmUI></ConfirmUI>
    <div class="flex">
      <div
        @click="openSection('project')"
        class="flex flex-col border p-4 m-4 shadow-xl justify-center w-1/4 justify-between cursor-pointer"
      >
        <div class="flex flex-col">
          <div class="flex justify-center">
            <div
              class="bg-purple-500 text-white w-10 h-10 inline-flex items-center justify-center rounded-full"
            >
              <FAIcon icon="project-diagram" class="text-xl" />
            </div>
          </div>
          <span class="text-center text-3xl my-2 text-purple-500"
            >Projects</span
          >
        </div>
        <span class="text-2xl text-center" v-if="project_count">
          {{ project_count }}
        </span>
        <button v-if="!project_count" class="btn btn-primary">
          Create Project
        </button>
      </div>
      <div
        @click="openSection('notebook')"
        class="flex flex-col border p-4 m-4 shadow-xl justify-center w-1/4 justify-between cursor-pointer"
      >
        <div class="flex flex-col">
          <div class="flex justify-center">
            <div
              class="bg-purple-500 text-white w-10 h-10 inline-flex items-center justify-center rounded-full"
            >
              <FAIcon icon="book-open" class="text-xl" />
            </div>
          </div>
          <span class="text-center text-3xl my-2 text-purple-500"
            >Notebooks</span
          >
        </div>
        <span class="text-2xl text-center" v-if="notebook_count">
          {{ notebook_count }}
        </span>
        <button class="btn btn-primary" v-if="!notebook_count">
          Create Notebook
        </button>
      </div>
      <div
        v-if="pipelineFlag === 'true'"
        @click="openSection('pipeline')"
        class="flex flex-col border p-4 m-4 shadow-xl justify-center w-1/4 justify-between cursor-pointer"
      >
        <div class="flex flex-col">
          <div class="flex justify-center">
            <div
              class="bg-purple-500 text-white w-10 h-10 inline-flex items-center justify-center rounded-full"
            >
              <FAIcon icon="code-branch" class="text-xl" />
            </div>
          </div>
          <span class="text-center text-3xl my-2 text-purple-500"
            >Pipelines</span
          >
        </div>
        <span class="text-2xl text-center" v-if="pipeline_count">
          {{ pipeline_count }}
        </span>
        <button class="btn btn-primary" v-if="!pipeline_count">
          Create Pipeline
        </button>
      </div>
      <div
        class="flex flex-col border p-4 m-4 shadow-xl justify-center w-1/4 justify-between "
      >
        <div class="flex flex-col">
          <div class="flex justify-center">
            <div
              class="bg-purple-500 text-white w-10 h-10 inline-flex items-center justify-center rounded-full"
            >
              <FAIcon icon="cube" class="text-xl" />
            </div>
          </div>
        </div>
        <button class="btn btn-primary mb-2 mt-5" @click="navigateToMyModels()">
          My Models
          <span class="bg-gray-300 text-black px-2 mx-2 rounded">{{
            private_model_count
          }}</span>
        </button>
        <button class="btn btn-primary" @click="navigateToMarketplace()">
          Marketplace
          <span class="bg-gray-300 text-black px-2 mx-2 rounded">{{
            public_model_count
          }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined } from "lodash-es";
import Vue2Filters from "vue2-filters";

// UI Elements
import ToastUI from "./vue-common/components/ui/Toast.ui";
import ConfirmUI from "./vue-common/components/ui/Confirm.ui";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI
  },
  mixins: [Vue2Filters.mixin],
  props: ["componenturl", "authtoken", "username", "userid", "parentmsg"],
  computed: {
    ...mapState("app", ["wikiConfig", "componentUrl", "authToken", "userName", "portalFEUrl", "pipelineFlag"])
  },
  data() {
    return {
      project_count: 0,
      notebook_count: 0,
      pipeline_count: 0,
      private_model_count: 0,
      public_model_count: 0
    };
  },
  watch: {
    parentmsg() {
      this.init();
    },
    userid() {
      this.init();
    },
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
    openSection(action) {
      this.$emit("dashboard-event", { action });
    },
    navigateToMyModels(){
      if(this.parentMsg === "iframeMsg"){
        window.top.postMessage('navigateToMyModel', '*');
      } else{
        let myModelsUrl = this.portalFEUrl + '/#/manageModule';
        window.open(myModelsUrl, '_blank');
      }
    },
    navigateToMarketplace() {
      if(this.parentMsg === "iframeMsg"){
        window.top.postMessage('navigateToMarketplace', '*');
      } else{
        let myModelsUrl = this.portalFEUrl + '/#/marketPlace';
        window.open(myModelsUrl, '_blank');
      }
    },
    async init() {
      // If running locally use environment config
      if (process.env.VUE_APP_ENV === "local") {
        this.setComponentUrl(process.env.VUE_APP_COMPONENT_API);
        this.setUserName(process.env.VUE_APP_USERNAME);
        this.setAuthToken(process.env.VUE_APP_AUTHTOKEN);
        this.setUserId(process.env.VUE_APP_USER_ID);
      } else if (
        !isUndefined(this.username) &&
        !isUndefined(this.authtoken) &&
        !isUndefined(this.userid)
      ) {
        this.setComponentUrl(this.componenturl);
        this.setUserName(this.username);
        this.setAuthToken(this.authtoken);
        this.setUserId(this.userid);
      }

      await this.getConfig();
      this.project_count = await this.projectCount();
      this.notebook_count = await this.notebookCount();
      this.pipeline_count = await this.pipelineCount();
      this.private_model_count = await this.privateModelCount();
      this.public_model_count = await this.modelCount();

      this.$emit("on-load-event");
    },
    ...mapMutations("app", [
      "setComponentUrl",
      "setAuthToken",
      "setUserName",
      "setUserId"
    ]),
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("project", ["projectCount"]),
    ...mapActions("pipeline", ["pipelineCount"]),
    ...mapActions("notebook", ["notebookCount"]),
    ...mapActions("model", ["modelCount", "privateModelCount"])
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
