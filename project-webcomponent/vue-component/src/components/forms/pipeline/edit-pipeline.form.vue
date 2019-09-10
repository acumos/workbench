<template>
  <div class="flex flex-col w-full">
    <div class="flex mb-2">
      <div class="flex-1 flex flex-col mr-2">
        <label class="mt-2"
          >Data Pipeline Name: <span class="text-red-500">*</span></label
        >
        <input
          type="text"
          class="form-input"
          v-model="updatedPipeline.name"
          placeholder="Enter Notebook Name"
        />
      </div>
      <div class="flex-1 flex flex-col">
        <label class="mt-2">Notebok Version</label>
        <select class="form-select" v-model="updatedPipeline.version">
          <option>OPtion 1</option>
        </select>
      </div>
    </div>
    <div class="flex mb-2">
      <div class="flex-1 flex flex-col mr-2">
        <label class="mt-2">Data Pipeline Description</label>
        <textarea
          class="form-textarea"
          rows="4"
          maxlength="2000"
          v-model="updatedPipeline.description"
          placeholder="Enter Notebook Description"
        ></textarea>
        <span class="leading-none text-right text-gray-600 mt-1">{{
          2000 - updatedPipeline.description.length
        }}</span>
      </div>
      <div class="flex-1 flex flex-col">
        <label class="mt-2">Notebok URL</label>
        <input
          type="text"
          class="form-input"
          v-model="updatedPipeline.url"
          placeholder="Enter Notebook URL"
        />
      </div>
    </div>
  </div>
</template>

<script>
import Pipeline from "../../../store/entities/pipeline.entity";

export default {
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      updatedPipeline: new Pipeline()
    };
  },
  watch: {
    data(selectedPipeline) {
      this.updatedPipeline = new Pipeline(selectedPipeline);
    }
  },
  computed: {
    isNew() {
      return this.notebook === null;
    }
  }
};
</script>
