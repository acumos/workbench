<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <div class="p-3">
      <div class="flex mb-2 w-1/2">
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Model Catalog</label>
          <select class="form-select">
            <option>OPtion 1</option>
            <option>OPtion 1</option>
            <option>OPtion 1</option>
          </select>
        </div>
      </div>
      <div class="flex">
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
          <label class="mt-2"
            >Model Name <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="Model Name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              class="form-input"
              type="text"
              placeholder="Enter Model Name"
              v-model="updatedModel.name"
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
          <label class="mt-2">Model Version</label>
          <select class="form-select">
            <option>OPtion 1</option>
            <option>OPtion 1</option>
            <option>OPtion 1</option>
          </select>
        </div>
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedModel)">
        {{ isNew ? "Associate Model" : "Save Model Association" }}
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";

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
      return isUndefined(this.model);
    }
  },
  methods: {
    async save(model) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        //save data

        // cleanup
        this.reset();
      }
    },
    reset() {
      this.updatedModel = new Model();
      this.$refs.form.reset();
    }
  }
};
</script>
