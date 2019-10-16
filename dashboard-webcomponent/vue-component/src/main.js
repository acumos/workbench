import Vue from "vue";
import Vue2Filters from "vue2-filters";
import { config } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import wrap from "@vue/web-component-wrapper";
import store from "./store";
import App from "./App.vue";
import "./icons";
import TooltipDirective from "./vue-common/directives/tooltip.directive";

// Directives
Vue.directive(TooltipDirective.name, TooltipDirective.directive);

Vue.use(Vue2Filters);

Vue.component("FAIcon", FontAwesomeIcon);

Vue.config.productionTip = false;

// If in production bootstrap webcomponent
if (process.env.NODE_ENV === "production") {
  config.autoAddCss = false;
  App.store = store;

  window.customElements.define("dashboard-webcomponent", wrap(Vue, App));
} else {
  new Vue({
    store,
    render: h => h(App)
  }).$mount("#app");
}
