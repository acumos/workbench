<template>
  <div class="flex flex-col w-full">
    <div class="flex mb-2">
      <div class="flex-1 flex flex-col mr-2">
        <label class="mt-2"
          >Notebook Name: <span class="text-red-500">*</span></label
        >
        <input
          type="text"
          class="form-input"
          v-model="updatedNotebook.name"
          placeholder="Enter Notebook Name"
        />
      </div>
      <div class="flex-1 flex flex-col">
        <label class="mt-2">Notebok Version</label>
        <select class="form-select" v-model="updatedNotebook.version">
          <option>OPtion 1</option>
        </select>
      </div>
    </div>
    <div class="flex mb-2">
      <div class="flex-1 flex flex-col mr-2">
        <label class="mt-2"
          >Notebook Type <span class="text-red-500">*</span></label
        >
        <select class="form-select" v-model="updatedNotebook.type">
          <option value="">Select Notebook type</option>
        </select>
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
      <span class="leading-none text-right text-gray-600 mt-1">{{
        2000 - updatedNotebook.description.length
      }}</span>
    </div>
  </div>
</template>

<script>
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
      return this.notebook === null;
    }
  }
};
</script>
