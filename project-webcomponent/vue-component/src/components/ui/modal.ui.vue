<template>
  <div
    class="fixed flex h-full items-center justify-around m-0 p-0 w-full top-0 left-0"
  >
    <div class="flex flex-col shadow-2xl z-10" :class="_size">
      <div
        class="flex bg-purple-700 text-white py-3 px-3 justify-between rounded-t-lg border border-purple-800 border-b-0"
      >
        <span class="font-bold">{{ title }}</span>
        <span>
          <a class="cursor-pointer" @click="$emit('onDismiss')">
            <FAIcon icon="times"></FAIcon>
          </a>
        </span>
      </div>
      <div class="flex p-3 border border-t-0 border-b-0 bg-white">
        <slot></slot>
      </div>
      <div class="px-3 py-2 border bg-gray-100">
        <slot name="footer"></slot>
      </div>
    </div>
    <div class="bg-black fixed flex h-full opacity-50 w-full z-0"></div>
  </div>
</template>

<script>
export default {
  props: {
    title: String,
    closable: Boolean,
    size: String
  },
  computed: {
    _size() {
      let _class = "";

      switch (this.size) {
        case "sm":
          _class = "w-2/5";
          break;
        case "md":
          _class = "w-2/4";
          break;
        case "lg":
          _class = "w-3/4";
          break;
      }

      return _class;
    }
  }
};
</script>
