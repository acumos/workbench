<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI
      id="notebook-form"
      class="relative p-2"
      innerClass="w-full"
    ></ToastUI>
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col">
          <label class="mt-2">
            Notebook Type:
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="notebook type"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <select
              class="form-select"
              v-model="updatedNotebook.type"
              :disabled="!isNew"
            >
              <option value>Select Notebook Type</option>
              <option value="JUPYTER">Jupyter</option>
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
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">
            Notebook Name:
            <span class="text-red-500">*</span>
          </label>
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
          <label class="mt-2">
            Notebook Version
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="notebook version"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              class="form-input"
              type="text"
              v-model="updatedNotebook.version"
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
      <div class="flex mb-2" v-if="useExternalNotebook === 'true'">
        <div class="flex-1 flex flex-col">
          <label class="mt-2">
            Notebook URL
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="notebook url"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              v-model="updatedNotebook.url"
              placeholder="Enter Notebook URL"
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
      <div class="flex flex-col">
        <label class="mt-2">Notebook Description</label>
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
import ToastUI from "../../ui/Toast.ui";
import Notebook from "../../../store/entities/notebook.entity";
import { mapActions, mapState } from "vuex";

export default {
  components: { ToastUI },
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      updatedNotebook: undefined
    };
  },
  created() {
    this.updatedNotebook = this.isNew
      ? new Notebook()
      : new Notebook(this.data);
  },
  computed: {
    ...mapState("app", {
      useExternalNotebook: state => state.useExternalNotebook
    }),
    isNew() {
      return isUndefined(this.data);
    }
  },
  methods: {
    ...mapActions("notebook", ["createNotebook", "updateNotebook"]),
    ...mapActions("project", ["getProjectNotebooks"]),
    ...mapActions("app", ["showToastMessage"]),
    async save(notebook) {
      const isValid = await this.$refs.form.validate();
      let response = null;
      if (isValid) {
        if (this.isNew) {
          const notebookCreation = notebook.$toJson();
          delete notebookCreation.noteBookId.uuid;
          response = await this.createNotebook(notebookCreation);
        } else {
          response = await this.updateNotebook(notebook.$toJson());
        }
        if (response.data.status === "Success") {
          await this.getProjectNotebooks();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "notebook-form",
            message: `${response.data.message}`,
            type: "error"
          });
        }
      }
    },
    reset() {
      this.updatedNotebook = new Notebook();
      this.$refs.form.reset();
    }
  }
};
</script>
