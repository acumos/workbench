<template>
  <div class="flex flex-col font-sans">
    <ToastUI id="global"></ToastUI>
    <ConfirmUI></ConfirmUI>
    <div class="flex flex-wrap m-2">
      <div class="flex w-full justify-end">
        <div v-if="notebook">
           <button
            class="btn btn-primary ml-2"
            @click="notebookLaunch(notebook)"
            v-if="notebook.status === 'ACTIVE'"
            v-tooltip="'Launch'"
          >
            <FAIcon icon="external-link-alt"></FAIcon>
          </button>
          <button
            class="btn btn-secondary ml-2"
            @click="notebookArchive(notebook)"
            v-if="notebook.status === 'ACTIVE'"
            v-tooltip="'Archive'"
          >
            <FAIcon icon="box"></FAIcon>
          </button>
          <template v-if="notebook.status === 'ARCHIVED'">
            <button
              class="btn btn-secondary ml-2"
              v-tooltip="'Unarchive'"
              @click="unarchiveNotebook(notebook)"
            >
              <FAIcon icon="box-open"></FAIcon>
            </button>
            <button
              class="btn btn-secondary ml-2 text-red-600"
              v-tooltip="'Delete Notebook'"
              @click="notebookDelete(notebook)"
            >
              <FAIcon icon="trash-alt"></FAIcon>
            </button>
          </template>
          <a
            :href="notebookWikiURL"
            target="_blank"
            class="btn btn-secondary text-black ml-2"
          >
            <FAIcon icon="question-circle"></FAIcon>
          </a>
        </div>
      </div>
      <NotebookDetails :notebook="notebook" v-if="notebook" class="my-5" />
    </div>
  </div>
</template>

<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined } from "lodash-es";
import Vue2Filters from "vue2-filters";

import Notebook from "./store/entities/notebook.entity";

// UI Elements
import ToastUI from "./components/ui/Toast.ui";
import ConfirmUI from "./components/ui/Confirm.ui";
import NotebookDetails from "./components/notebook.details";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    NotebookDetails
  },
  mixins: [Vue2Filters.mixin],
  props: ["notebookid", "componenturl", "authtoken", "username"],
  computed: {
    ...mapState("app", [
      "notebookWikiURL",
      "componentUrl",
      "authToken",
      "userName"
    ]),

    notebook() {
      return Notebook.query().first();
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
        this.setActiveNotebook(process.env.VUE_APP_NOTEBOOK_ID);
      } else if (!isUndefined(this.username) && !isUndefined(this.authtoken)) {
        this.setComponentUrl(this.componenturl);
        this.setUserName(this.username);
        this.setAuthToken(this.authtoken);
        this.setActiveNotebook(this.notebookid);
      }

      await this.getConfig();
      await this.getNotebookDetails();
      this.$emit("on-load-event");
    },
    ...mapMutations("app", [
      "setComponentUrl",
      "setAuthToken",
      "setUserName",
      "confirm"
    ]),
    ...mapMutations("notebook", ["setActiveNotebook"]),
    ...mapActions("app", ["getConfig", "showToastMessage"]),
    ...mapActions("notebook", [
      "getNotebookDetails",
      "archiveNotebook",
      "restoreNotebook",
      "deleteNotebook",
      "launchNotebook"
    ]),

    async unarchiveNotebook(notebook) {
      this.confirm({
        title: "Unarchive " + notebook.name,
        body: "Are you sure you want to unarchive " + notebook.name + "?",
        options: {
          okLabel: "Unarchive Notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.restoreNotebook(notebook.id);
          if (response.data.status === "Success") {
            await this.getNotebookDetails();
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

    async notebookArchive(notebook) {
      this.confirm({
        title: "Archive " + notebook.name,
        body: "Are you sure you want to archive " + notebook.name + "?",
        options: {
          okLabel: "Archive Notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archiveNotebook(notebook.id);
          if (response.data.status === "Success") {
            await this.getNotebookDetails();
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
    async notebookDelete(notebook) {
      this.confirm({
        title: "Delete " + notebook.name,
        body: "Are you sure you want to delete " + notebook.name + "?",
        options: {
          okLabel: "Delete Notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deleteNotebook(notebook.id);
          if (response.data.status === "Success") {
            this.$emit("notebook-event", {
              data: "catalog-notebook"
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
    async notebookLaunch(notebook) {
      let response = await this.launchNotebook();
      if (response.data.status === "Success") {
        let launchURL = response.data.data.noteBookId.serviceUrl;
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
@import "~vue-select/dist/vue-select.css";
@import "./assets/style/style.css";
</style>
