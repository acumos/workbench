<template>
  <div class="w-screen h-screen flex fixed" v-if="confirm.enabled">
    <div class="bg-black w-screen h-screen fixed opacity-25 z-10"></div>
    <div class="w-1/2 m-auto p-2 rounded-lg shadow-lg border bg-white z-20">
      <div class="flex flex-col">
        <div class="py-2 text-xl">
          {{ confirm.message }}
        </div>
        <div class="py-1 flex justify-between">
          <button class="btn btn-sm btn-secondary" @click="onDismiss">
            Cancel
          </button>
          <button class="btn btn-sm btn-primary" @click="onOk">Ok</button>
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
