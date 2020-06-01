<template>
  <div>
    <ToastUI v-if="globalError" id="global" />
    <div v-if="!globalError">
      <div class="flex flex-col font-sans">
        <ToastUI id="global"></ToastUI>
        <ConfirmUI></ConfirmUI>
        <div class="flex flex-wrap m-2">
          <div class="flex w-full justify-end">
            <div v-if="dataset">
              <!-- <button
                class="btn btn-primary ml-2"
                @click="datasetLaunch(dataset)"
                 title="Launch Datasource"
                 disabled
              >
                <FAIcon icon="external-link-alt"></FAIcon>
              </button> -->
              <!-- <button
                class="btn btn-secondary ml-2"
                @click="datasetArchive(dataset)"
                title="Archive Datasource"
                disabled
              >
                <FAIcon icon="box"></FAIcon>
              </button> -->
              <template>
                <!-- <button
                  class="btn btn-secondary ml-2"
                  title="Unarchive Datasource"
                  @click="unarchiveDataset(dataset)"
                  disabled
                >
                  <FAIcon icon="box-open"></FAIcon>
                </button> -->
                <button
                  class="btn btn-secondary ml-2 text-red-600"
                  title="Delete Datasource"
                  @click="datasetDelete(dataset)"
                >
                  <FAIcon icon="trash-alt"></FAIcon>
                </button>
              </template>
              <a
                :href="datasourceWikiURL"
                target="_blank"
                class="btn btn-secondary text-black ml-2"
                title="Learn More"
              >
                <FAIcon icon="question-circle"></FAIcon>
              </a>
            </div>
          </div>
          <DatasourceDetails :dataset="dataset" v-if="dataset" class="my-5" />
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

import Dataset from "./store/entities/datasource.entity";

// UI Elements
import ToastUI from "./vue-common/components/ui/Toast.ui";
import ConfirmUI from "./vue-common/components/ui/Confirm.ui";
import DatasourceDetails from "./components/datasource.details";


export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    DatasourceDetails
  },
  mixins: [Vue2Filters.mixin],
  props: ["datasourceid","componenturl", "authtoken", "username"],
  computed: {
    ...mapState("app", [
      "datasourceWikiURL",
      "componentUrl",
      "authToken",
      "userName",
      "globalError"
    ]),

...mapState("datasource", ["activeDataset", "loginAsOwner"]),

  dataset() {
      return Dataset.query().first();
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
        this.setActiveDataset(process.env.VUE_APP_DATASOURCE_ID);
      } else if (!isUndefined(this.username) && !isUndefined(this.authtoken)) {
        this.setComponentUrl(this.componenturl);
        this.setUserName(this.username);
        this.setAuthToken(this.authtoken);
        this.setActiveDataset(this.datasourceid);
      }
      
      await this.getConfig();
      if (this.userName !== "" && this.authToken !== "") {
        await this.getDatasetDetails();
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
    ...mapMutations("datasource", ["setActiveDataset"]),
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("datasource", [
      "getDatasetDetails",
      "archiveDataset",
      "restoreDataset",
      "deleteDataset",
      "launchDataset"
    ]),

    // async unarchiveDataset(dataset) {
    //   this.confirm({
    //     title: "Unarchive " + dataset.name,
    //     body: "Are you sure you want to unarchive " + dataset.name + "?",
    //     options: {
    //       okLabel: "Unarchive Dataset",
    //       dismissLabel: "Cancel"
    //     },
    //     onOk: async () => {
    //       const response = await this.restoreDataset();
    //       if (response.data.status === "Success") {
    //         await this.getDatasetDetails();
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "success"
    //         });
    //       } else {
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "error"
    //         });
    //       }
    //     }
    //   });
    // },

    // async datasetArchive(dataset) {
    //   this.confirm({
    //     title: "Archive " + dataset.name,
    //     body: "Are you sure you want to archive " + dataset.name + "?",
    //     options: {
    //       okLabel: "Archive Dataset",
    //       dismissLabel: "Cancel"
    //     },
    //     onOk: async () => {
    //       const response = await this.archiveDataset();
    //       if (response.data.status === "Success") {
    //         await this.getDatasetDetails();
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "success"
    //         });
    //       } else {
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "error"
    //         });
    //       }
    //     }
    //   });
    // },
    async datasetDelete(dataset) {
      this.confirm({
        title: "Delete " + dataset.name,
        body: "Are you sure you want to delete " + dataset.name + "?",
        options: {
          okLabel: "Delete Datasource",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deleteDataset();
          if (response.data.status === "Success") {
            this.$emit("datasource-event", {
              data: "catalog-datasource"
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
    // async datasetLaunch(dataset) {
    //   let response = await this.launchDataset();
    //   if (response.data.status === "Success") {
    //     let launchURL = response.data.data.dataset.jdbcURL;
    //     window.open(launchURL, "_blank");
    //   } else {
    //     this.showToastMessage({
    //       id: "global",
    //       message: `${response.data.message}`,
    //       type: "error"
    //     });
    //   }
    // }
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
