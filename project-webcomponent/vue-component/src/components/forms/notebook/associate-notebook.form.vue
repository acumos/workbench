<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI id="associate-notebook" class="relative p-2" innerClass="w-full"></ToastUI>
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
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
            <select class="form-select" v-model="notebookType">
              <option value>Select Notebook Type</option>
              <option value="jupyter">Jupyter</option>
            </select>
            <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">
            Notebook Name
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="notebook name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <!-- 2.  Filter ones already associated to this particular project -->
            <select class="form-select" v-model="selectedNotebook" :disabled="!notebookType">
              <option value>Select a Notebook</option>
              <option
                :value="notebook"
                v-for="(notebook, index) in filteredNotebooks"
                :key="index"
              >{{ notebook.name }}</option>
            </select>
            <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
        </div>
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">Notebook URL</label>
          <input
            type="text"
            class="form-input"
            v-model="selectedNotebook.url"
            disabled
            placeholder="Enter Notebook URL"
          />
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Notebook Version</label>
          <input
            type="text"
            class="form-input"
            v-model="selectedNotebook.version"
            disabled
            placeholder="Enter Notebook URL"
          />
        </div>
      </div>
    </div>
    <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button
        class="btn btn-sm btn-primary"
        @click="save(selectedNotebook)"
      >{{ isNew ? "Associate Notebook" : "Save Association" }}</button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined, sortBy } from "lodash-es";
import ToastUI from "../../../vue-common/components/ui/Toast.ui";
import Notebook from "../../../store/entities/notebook.entity";
import { mapActions } from "vuex";

export default {
  components: { ToastUI },
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      notebookType: "",
      updatedNotebook: new Notebook(),
      selectedNotebook: "",
      notebooks: []
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
    },
    filteredNotebooks() {
      return sortBy(this.notebooks, ["name"]);
    }
  },
  async created() {
    this.notebooks = await this.getAllNotebooks();
  },
  methods: {
    ...mapActions("notebook", ["getAllNotebooks", "associateNotebook"]),
    ...mapActions("project", ["getProjectNotebooks"]),
    ...mapActions("app", ["showToastMessage"]),
    async save(notebook) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        notebook = new Notebook(notebook);
        const response = await this.associateNotebook(notebook);
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
            id: "associate-notebook",
            message: `${response.data.message}`,
            type: "error"
          });
        }
      }
    },
    reset() {
      this.updatedNotebook = new Notebook();
      this.selectedNotebook = "";
      this.$refs.form.reset();
    }
  }
};
</script>
