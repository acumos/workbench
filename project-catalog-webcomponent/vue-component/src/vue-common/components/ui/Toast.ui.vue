<template>
  <div class="w-full fixed mt-2" v-if="toast.enabled && toast.id === id">
    <div class="w-1/2 m-auto p-2 rounded-lg shadow-lg border" :class="type">
      <div class="flex itemx-center">
        <FAIcon cl :icon="icon" class="mr-1 text-2xl"></FAIcon>
        {{ toast.message }}
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  props: ["id", "innerClass"],
  computed: {
    ...mapState("app", {
      toast: state => state.toast
    }),
    icon() {
      let icon = "info-circle";

      switch (this.toast.type) {
        case "error":
          icon = "exclamation-triangle";
          break;

        case "success":
          icon = "check";
          break;
      }

      return icon;
    },
    type() {
      let _class = "";

      switch (this.toast.type) {
        case "error":
          _class = "bg-red-300 text-red-800 border-red-400";
          break;
        case "success":
          _class = "bg-green-300 text-green-800 border-green-400";
          break;
        case "info":
          _class = "bg-blue-300 text-blue-800 border-blue-400";
          break;
      }

      return `${_class} ${this.innerClass}`;
    }
  }
};
</script>
