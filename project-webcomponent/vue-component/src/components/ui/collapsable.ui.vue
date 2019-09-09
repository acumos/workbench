<template>
  <div class="flex flex-col w-full border">
    <div class="flex items-center text-xl p-2 border-b">
      <div class="flex-grow inline-flex items-center text-purple-800">
        <FAIcon class="mr-2" v-if="icon" :icon="icon"></FAIcon>
        <span>{{ title }}</span>
        <div class="ml-2">
          <slot name="left-actions" />
        </div>
      </div>
      <div class="flex items-center">
        <div class="flex mr-2">
          <slot name="right-actions" />
        </div>
        <FAIcon
          class="cursor-pointer text-gray-500"
          :icon="collapseIcon"
          @click="isCollapsed = !isCollapsed"
        ></FAIcon>
      </div>
    </div>
    <div :class="{ 'p-2': !collapseBorder }" v-show="!isCollapsed">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    title: String,
    icon: String,
    collapseBorder: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isCollapsed: false
    };
  },
  computed: {
    collapseIcon() {
      return this.isCollapsed ? "plus-square" : "minus-square";
    }
  }
};
</script>
