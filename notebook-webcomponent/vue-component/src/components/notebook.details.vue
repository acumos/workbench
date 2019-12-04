<template>
  <ValidationObserver ref="form" tag="div" class="flex w-full">
    <collapsable-ui
      :title="notebook.name"
      icon="book-open"
      :collapse-border="true"
    >
      <div class="inline-flex" slot="left-actions">
        <button
          class="btn btn-xs btn-primary w-8 h-8"
          v-if="!isEditing && !isArchived"
          @click="editNotebook()"
          title="Edit Notebook"
        >
          <FAIcon icon="pencil-alt"></FAIcon>
        </button>
        
        <div v-if="isEditing">
          <button
            class="btn btn-xs py-1 px-2 btn-primary rounded-0"
            @click="save(updatedNotebook)"
            title="Save Notebook"
          >
            <FAIcon icon="save"></FAIcon>
          </button>
          <button class="ml-2 text-base" @click="revert(notebook)">
            Cancel
          </button>
        </div>
      </div>

      <table class="project-table">
        <tr>
          <td>
            Notebook Name <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ notebook.name }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Notebook Name"
              rules="required|startAlpha"
              v-slot="{ errors, classes }"
            >
              <input
                class="form-input w-2/6"
                type="text"
                v-model="updatedNotebook.name"
              />
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </td>
        </tr>
        <tr>
          <td>Notebook ID</td>
          <td>{{ notebook.id }}</td>
        </tr>
        <tr>
          <td>
            Notebook Version <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ notebook.version }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Notebook Version"
              rules="required|versionValidation"
              v-slot="{ errors, classes }"
            >
              <input
                class="form-input w-2/6"
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
          </td>
        </tr>
        <tr>
          <td>Notebook Status</td>
          <td>
            <span class="font-semibold" :class="statusClass">
              {{ notebook.status }}
            </span>
          </td>
        </tr>
        <tr>
          <td>Notebook Creation Date</td>
          <td>{{ created }}</td>
        </tr>
        <tr>
          <td>Notebook Modified Date</td>
          <td>{{ modified }}</td>
        </tr>
        <tr>
          <td>
            Notebook Description
            <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ notebook.description }}</td>
          <td v-if="isEditing">
              <textarea
                class="form-textarea w-1/2 h-24 my-2"
                v-model="updatedNotebook.description"
                 maxlength="1000"
                placeholder="Enter Notebook Description"
              ></textarea>
              <span class="leading-none text-right text-gray-600 mt-1"
                >{{ 1000 - updatedNotebook.description.length }} Chars</span
              > 
          </td>
        </tr>
        <tr v-if="useExternalNotebook === 'true'">
          <td>
            Notebook URL
            <span v-if="isEditing" class="text-red-500">*</span>
          </td>
           <td v-if="!isEditing">{{ notebook.url }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Notebook URL"
              rules="required|url"
              v-slot="{ errors, classes }"
            >
              <textarea
                class="form-textarea w-2/6"
                v-model="updatedNotebook.url"
              ></textarea>
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </td>
        </tr>
      </table>
    </collapsable-ui>
  </ValidationObserver>
</template>

<script>
import dayjs from "dayjs";
import { mapActions, mapState } from "vuex";

import Notebook from "../store/entities/notebook.entity.js";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";

export default {
  props: {
    notebook: {
      type: Object
    }
  },
  components: { CollapsableUi },
  data() {
    return {
      updatedNotebook: new Notebook(),
      isEditing: false
    };
  },
  computed: {
    ...mapState("app", {
      useExternalNotebook: state => state.useExternalNotebook
    }),
    statusClass() {
      let _class = "";
      switch (this.notebook.status) {
        case "ACTIVE":
          _class = "text-green-600";
          break;
        case "ARCHIVED":
          _class = "text-orange-600";
          break;
      }

      return _class;
    },
    isArchived() {
      return this.notebook.status === "ARCHIVED";
    },
    created() {
      return dayjs(this.notebook.creationDate).format("YYYY-MM-DD");
    },
    modified() {
      return dayjs(this.notebook.modifiedDate).format("YYYY-MM-DD");
    }
  },

  watch:{
    notebook() {
      this.updatedNotebook = new Notebook(this.notebook);
    }
   },

  created() {
    this.updatedNotebook = new Notebook(this.notebook);
  },
  methods: {
    ...mapActions("notebook", ["updateNotebook", "getNotebookDetails"]),
    ...mapActions("app", ["showToastMessage"]),
    editNotebook() {
      this.isEditing = true;
    },

    async save(updatedNotebook) {
      const isValid = await this.$refs.form.validate();

      if (!isValid) {
        return;
      }

      const response = await this.updateNotebook(updatedNotebook.$toJson());
      if (response.data.status === "Success") {
        await this.getNotebookDetails();
        this.isEditing = false;
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "success"
        });
      } else {
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "error"
        });
      }
    },
    revert(notebook) {
      this.updatedNotebook = new Notebook(notebook);
      this.isEditing = false;
    }
  }
};
</script>

<style lang="postcss">
table.project-table {
  @apply w-full;

  tr {
    td {
      &:first-child {
        @apply text-right font-bold bg-gray-300 text-black;
        @apply border-r border-b border-gray-600;
        @apply w-2/12 p-2;
      }
      &:nth-child(2) {
        @apply text-black;
        @apply border-b border-gray-600;
        @apply pl-2;
      }
    }
  }
}
</style>
