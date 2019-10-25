<template>
  <div class="w-2/3 h-screen flex fixed" v-if="confirm.enabled">
    <div class="bg-black w-screen h-screen fixed opacity-25 z-10"></div>
    <div class="w-1/2 m-auto rounded-lg shadow-lg border bg-white z-20">
      <div class="flex flex-col rounded">
        <div class="bg-purple-500 p-2 text-white rounded-t-lg text-lg">
          {{ confirm.title }}
        </div>
        <div class="text-lg p-2" v-if="confirm.body">
          {{ confirm.body }}
        </div>
        <div class="flex justify-between p-2">
          <button class="btn btn-sm btn-secondary" @click="onDismiss">
            {{ confirm.options.dismissLabel || "Cancel" }}
          </button>
          <button class="btn btn-sm btn-primary" @click="onOk">
            {{ confirm.options.okLabel || "Ok" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import { isFunction } from "lodash-es";

export default {
  computed: {
    ...mapState("app", {
      confirm: state => state.confirm
    })
  },
  methods: {
    ...mapMutations("app", ["toggleConfirm"]),
    onOk() {
      const okFn = this.confirm.onOk;

      if (isFunction(okFn)) {
        this.confirm.onOk();
      }
      this.toggleConfirm();
    },
    onDismiss() {
      const dismissFn = this.confirm.onDismiss;

      if (isFunction(dismissFn)) {
        this.confirm.onDismiss();
      }
      this.toggleConfirm();
    }
  }
};
</script>
