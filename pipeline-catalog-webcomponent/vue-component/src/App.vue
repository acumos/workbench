<template>
  <div>
    <ToastUI v-if="globalError" id="global" />
    <div v-if="!globalError">
      <div class="flex flex-col font-sans" id="pipeline-catalog-webcomponent">
        <ToastUI id="global"></ToastUI>
        <ConfirmUI></ConfirmUI>
        <PipelineList :pipelines="pipelines" @on-open-pipeline="onOpenPipeline" />
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

import PipelineList from "./components/pipeline.list";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    PipelineList
  },
  mixins: [Vue2Filters.mixin],
  props: ["componenturl", "authtoken", "username"],
  computed: {
    ...mapState("app", [
      "wikiConfig",
      "componentUrl",
      "authToken",
      "userName",
      "globalError"
    ]),
    pipelines() {
      return Pipeline.all();
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
    onOpenPipeline(pipeline) {
      this.$emit("catalog-pipeline-event", {
        action: "view-pipeline",
        pipelineId: pipeline.id,
        pipelineName: pipeline.name
      });
    },
    async init() {
      // If running locally use environment config
      if (process.env.VUE_APP_ENV === "local") {
        this.setComponentUrl(process.env.VUE_APP_COMPONENT_API);
        this.setUserName(process.env.VUE_APP_USERNAME);
        this.setAuthToken(process.env.VUE_APP_AUTHTOKEN);
      } else if (!isUndefined(this.username) && !isUndefined(this.authtoken)) {
        this.setComponentUrl(this.componenturl);
        this.setUserName(this.username);
        this.setAuthToken(this.authtoken);
      }

      await this.getConfig();
      if (this.userName !== "" && this.authToken !== "") {
        await this.allPipelines();
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
      "setToastMessage",
      "setGlobalError"
    ]),
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("pipeline", ["allPipelines"])
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
