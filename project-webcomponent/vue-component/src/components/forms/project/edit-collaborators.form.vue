<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2"
            >Notebook Name: <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="Notebook type"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              :class="classes"
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
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Notebok Version</label>
          <input
            class="form-input"
            type="text"
            v-model="updatedNotebook.version"
            disabled
          />
        </div>
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2"
            >Notebook Type <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="Notebook Type"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <select
              class="form-select"
              :class="classes"
              v-model="updatedNotebook.type"
            >
              <option value="">Select Notebook type</option>
              <option value="asdf">new</option>
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
          <label class="mt-2">Notebok URL</label>
          <input
            type="text"
            class="form-input"
            v-model="updatedNotebook.url"
            placeholder="Enter Notebook URL"
          />
        </div>
      </div>
      <div class="flex flex-col">
        <label class="mt-2">Notebok Description</label>
        <textarea
          class="form-textarea"
          rows="4"
          maxlength="2000"
          v-model="updatedNotebook.description"
          placeholder="Enter Notebook Description"
        ></textarea>
        <span class="leading-none text-right text-gray-600 mt-1"
          >{{ 2000 - updatedNotebook.description.length }} Chars</span
        >
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedNotebook)">
        {{ isNew ? "Create" : "Save" }} Notebook
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
      return isUndefined(this.data);
    }
  },
  methods: {
    async save(notebook) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        //save data
        const data = notebook.$toJson();
      }
    },
    reset() {
      this.updatedNotebook = new Notebook();
      this.$refs.form.reset();
    }
  }
};
</script>
