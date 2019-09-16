<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2"
            >Data Pipeline Name: <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="Data Pipeline Name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              v-model="updatedPipeline.name"
              placeholder="Enter Notebook Name"
            />
            <span
              class="text-sm text-red-700 flex items-center"
              v-if="errors[0]"
            >
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
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
          <span class="leading-none text-right text-gray-600 mt-1"
            >{{ 2000 - updatedPipeline.description.length }} Chars</span
          >
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
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedPipeline)">
        {{ isNew ? "Create" : "Edit" }} Data Pipeline
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";

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
      return isUndefined(this.notebook);
    }
  },
  methods: {
    async save(pipeline) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        //save data

        // cleanup
        this.reset();
      }
    },
    reset() {
      this.updatedPipeline = new Pipeline();
      this.$refs.form.reset();
    }
  }
};
</script>
