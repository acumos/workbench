import Vue from "vue";
import Vue2Filters from "vue2-filters";
import { ValidationProvider } from "vee-validate";
import VueGoodTablePlugin from "vue-good-table";
import { library, config } from "@fortawesome/fontawesome-svg-core";
import {
  faQuestionCircle,
  faUserCircle,
  faExternalLinkAlt,
  faArchive,
  faSearch,
  faExclamationTriangle,
  faSpinner,
  faTimes,
  faMinusSquare,
  faProjectDiagram,
  faPlusSquare,
  faCodeBranch,
  faCube,
  faBookOpen,
  faPencilAlt,
  faCloudUploadAlt,
  faCloud,
  faEye,
  faUnlink,
  faLink,
  faSave,
  faTrash
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

import wrap from "@vue/web-component-wrapper";
import store from "./store";
import App from "./App.vue";

Vue.use(Vue2Filters);
Vue.use(VueGoodTablePlugin);

library.add(
  faQuestionCircle,
  faUserCircle,
  faExternalLinkAlt,
  faArchive,
  faSearch,
  faExclamationTriangle,
  faSpinner,
  faTimes,
  faMinusSquare,
  faPlusSquare,
  faProjectDiagram,
  faCodeBranch,
  faCube,
  faBookOpen,
  faPencilAlt,
  faCloudUploadAlt,
  faCloud,
  faEye,
  faUnlink,
  faLink,
  faArchive,
  faSave,
  faTrash,
);

Vue.component("FAIcon", FontAwesomeIcon);
Vue.component("ValidationProvider", ValidationProvider);

Vue.config.productionTip = false;

// If in production bootstrap webcomponent
if (process.env.NODE_ENV === "production") {
  config.autoAddCss = false;
  App.store = store;
  // Vue.use(vueCustomElement);

  window.customElements.define("project-webcomponent-element", wrap(Vue, App));
} else {
  new Vue({
    store,
    render: h => h(App)
  }).$mount("#app");
}
