<template>
  <div class="flex flex-col font-sans" id="project-catalog-webcomponent">
    <ToastUI id="global"></ToastUI>
    <ConfirmUI></ConfirmUI>
    <ProjectList :projects="projects" :sharedProjects="projectsShared" />
  </div>
</template>

<script>
import { dom } from "@fortawesome/fontawesome-svg-core";
import { mapState, mapActions, mapMutations } from "vuex";
import { isUndefined } from "lodash-es";
import Vue2Filters from "vue2-filters";

import Project from "./store/entities/project.entity";

// UI Elements
import ToastUI from "../../../vue-common/components/ui/Toast.ui";
import ConfirmUI from "../../../vue-common/components/ui/Confirm.ui";

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
    ...mapState("app", ["wikiConfig", "componentUrl", "authToken", "userName"]),
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
      await this.allProjects();
      this.projectsShared = await this.sharedProjects();
    },
    ...mapMutations("app", [
      "setComponentUrl",
      "setAuthToken",
      "setUserName",
      "confirm"
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
