<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2"
            >Notebook Type: <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="notebook type"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <select class="form-select" v-model="updatedNotebook.type">
              <option value="">Select Notebook Type</option>
            </select>
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
          <label class="mt-2"
            >Notebok Name <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="notebook name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              v-model="updatedNotebook.name"
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
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">Notebok URL</label>
          <input
            type="text"
            class="form-input"
            v-model="updatedNotebook.url"
            placeholder="Enter Notebook URL"
          />
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Notebok Version</label>
          <select class="form-select" v-model="updatedNotebook.version">
            <option value="">Select Notebook Version</option>
          </select>
        </div>
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedNotebook)">
        {{ isNew ? "Associate Notebook" : "Save Association" }}
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";

import Notebook from "../../../store/entities/notebook.entity";

export default {
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      updatedNotebook: new Notebook()
    };
  },
  watch: {
    data(selectedNotebook) {
      this.updatedNotebook = new Notebook(selectedNotebook);
    }
  },
  computed: {
    isNew() {
      return isUndefined(this.notebook);
    }
  },
  methods: {
    async save(notebook) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        this.reset();
      }
    },
    reset() {
      this.updatedNotebook = new Notebook();
      this.$refs.form.reset();
    }
  }
};
</script>
