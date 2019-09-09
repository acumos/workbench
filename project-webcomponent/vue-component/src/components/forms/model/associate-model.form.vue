<template>
  <div class="flex flex-col w-full pb-5">
    <div class="flex mb-2">
      <div class="flex-1 flex flex-col mr-2">
        <label class="mt-2">Model Format</label>
        <select class="form-select">
          <option>OPtion 1</option>
          <option>OPtion 1</option>
          <option>OPtion 1</option>
        </select>
      </div>
      <div class="flex-1 flex flex-col">
        <label class="mt-2">Model Category</label>
        <select class="form-select">
          <option>OPtion 1</option>
          <option>OPtion 1</option>
          <option>OPtion 1</option>
        </select>
      </div>
    </div>
    <div class="flex">
      <div class="flex-1 flex flex-col mr-2">
        <label class="mt-2">Model Name</label>
        <select class="form-select">
          <option>OPtion 1</option>
          <option>OPtion 1</option>
          <option>OPtion 1</option>
        </select>
      </div>
      <div class="flex-1 flex flex-col">
        <label class="mt-2">Model Version</label>
        <select class="form-select">
          <option>OPtion 1</option>
          <option>OPtion 1</option>
          <option>OPtion 1</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script>
import Model from "../../../store/entities/model.entity";

export default {
  props: {
    model: {
      type: Object
    }
  },
  data() {
    return {
      updatedModel: new Model()
    };
  },
  watch: {
    model(selectedModel) {
      this.updatedModel = new Model(selectedModel);
    }
  },
  computed: {
    isNew() {
      return this.model === null;
    }
  }
};
</script>
