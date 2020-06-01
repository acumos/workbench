<template>
  <div>
    <ToastUI v-if="globalError" id="global" />
    <div v-if="!globalError">
      <div class="flex flex-col font-sans" id="project-catalog-webcomponent">
        <ToastUI id="global"></ToastUI>
        <ConfirmUI></ConfirmUI>
        <ProjectList
          :projects="projects"
          :sharedProjects="projectsShared"
          @on-open-project="onOpenProject"
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

import Project from "./store/entities/project.entity";

// UI Elements
import ToastUI from "./vue-common/components/ui/Toast.ui";
import ConfirmUI from "./vue-common/components/ui/Confirm.ui";

import ProjectList from "./components/project.list";

export default {
  name: "app",
  components: {
    ToastUI,
    ConfirmUI,
    ProjectList
  },
  mixins: [Vue2Filters.mixin],
  props: ["projectid", "componenturl", "authtoken", "username", "parentMsg"],
  computed: {
    ...mapState("app", [
      "wikiConfig",
      "componentUrl",
      "authToken",
      "userName",
      "globalError"
    ]),
    ...mapState("project", ["loginAsOwner"]),
    projects() {
      return Project.all();
    }
  },
  data() {
    return {
      projectsShared: []
    };
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
    onOpenProject(project) {
      this.$emit("catalog-project-event", {
        action: "view-project",
        projectId: project.id,
        projectName: project.name
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
        await this.allProjects();
        this.projectsShared = await this.sharedProjects();
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
    ...mapActions("project", ["allProjects", "sharedProjects"])
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
