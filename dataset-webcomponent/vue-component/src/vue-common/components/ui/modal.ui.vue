<template>
  <div
    class="fixed flex h-full items-center justify-around m-0 p-0 w-full top-0 left-0 text-gray-800"
  >
    <div class="flex flex-col shadow-2xl z-10" :class="_size">
      <div
        class="flex bg-purple-500 text-white py-3 px-3 justify-between rounded-t-lg border border-purple-500 border-b-0"
      >
        <span class="font-bold">{{ title }}</span>
        <span class="mx-1">
          <a class="cursor-pointer" @click="$emit('onDismiss')">
            <FAIcon icon="times"></FAIcon>
          </a>
        </span>
      </div>
      <div class="flex border border-t-0 border-b-0 bg-white">
        <slot></slot>
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
