import Vue from "vue";
import Vue2Filters from "vue2-filters";
import { ValidationProvider, ValidationObserver } from "vee-validate";
import { config } from "@fortawesome/fontawesome-svg-core";
import VueGoodTablePlugin from "vue-good-table";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import wrap from "@vue/web-component-wrapper";
import VTooltip from 'v-tooltip';
import vSelect from 'vue-select'
import store from "./store";
import App from "./App.vue";
import "./icons";
import "./validators";

Vue.use(Vue2Filters);
Vue.use(VueGoodTablePlugin);
Vue.use(VTooltip);

Vue.component('v-select', vSelect);
Vue.component("FAIcon", FontAwesomeIcon);
Vue.component("ValidationProvider", ValidationProvider);
Vue.component("ValidationObserver", ValidationObserver);

Vue.config.productionTip = false;

// If in production bootstrap webcomponent
if (process.env.NODE_ENV === "production") {
  config.autoAddCss = false;
  App.store = store;

  window.customElements.define("notebook-webcomponent-element", wrap(Vue, App));
} else {
  new Vue({
    store,
    render: h => h(App)
  }).$mount("#app");
}
