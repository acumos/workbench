<template>
  <div>
    <ToastUI v-if="globalError" id="global" />
    <div v-if="!globalError">
      <div class="flex flex-col font-sans" id="datasource-catalog-webcomponent">
        <ToastUI id="global"></ToastUI>
        <ConfirmUI></ConfirmUI>
        <DatasourceList
          :datasets="datasets"
          @on-open-dataset="onOpenDataset"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined } from "lodash-es";
import Vue2Filters from "vue2-filters";

import Dataset from "./store/entities/datasource.entity";

// UI Elements
import ToastUI from "./vue-common/components/ui/Toast.ui";
import ConfirmUI from "./vue-common/components/ui/Confirm.ui";

import DatasourceList from "./components/datasource.list";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    DatasourceList
  },
  mixins: [Vue2Filters.mixin],
  props: ["datasourceid","componenturl", "authtoken", "username", "parentMsg"],
  computed: {
    ...mapState("app", [
      "wikiConfig",
      "componentUrl",
      "authToken",
      "userName",
      "globalError"
    ]),
    ...mapState("datasource", ["loginAsOwner"]),
    datasets() {
      return Dataset.all();
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
    onOpenDataset(dataset) {
      this.$emit("catalog-datasource-event", {
        action: "view-datasource",
        datasourceId: dataset.datasourceId,
        datasetName: dataset.name
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
        await this.allDatasets();
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
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("datasource", ["allDatasets"])
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
