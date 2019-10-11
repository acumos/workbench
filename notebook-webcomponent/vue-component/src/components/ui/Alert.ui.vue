<template>
  <ModalUi :title="title" :size="size" @onDismiss="$emit('onDismiss')">
    <slot></slot>
    <template slot="footer">
      <button class="btn-sm btn-secondary" @click="$emit('onDismiss')">
        Cancel
      </button>
      <button
        class="btn-sm btn-primary"
        @click="onClose"
        v-if="!isPendingClose"
      >
        {{ closeString }}
      </button>
      <button disabled v-if="isPendingClose" class="btn btn-primary py-1">
        <FAIcon icon="spinner" pulse size="lg"></FAIcon>
      </button>
    </template>
  </ModalUi>
</template>

<script>
import ModalUi from "./Modal.ui";

export default {
  props: {
    title: String,
    closeString: String,
    size: String,
    close$: Promise
  },
  data() {
    return {
      isPendingClose: false
    };
  },
  components: { ModalUi },
  methods: {
    onClose() {
      if (this.close$) {
        this.isPendingClose = true;

        this.close$.then(() => {
          this.isPendingClose = false;
          this.$emit("onClose");
        });
      } else {
        this.$emit("onClose");
      }
    }
  }
};
</script>
