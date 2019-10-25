<template>
  <div class="w-full">
    <div class="flex w-full">
      <collapsable-ui title="Notebooks" icon="book-open" :collapse-border="!isEmpty">
        <div slot="right-actions" class="inline-flex">
          <a
            :href="wikiConfig.notebookWikiURL"
            target="_blank"
            class="text-sm text-gray-500 underline"
          >Learn More</a>
        </div>
        <div v-if="notebookError">
          <div class="mt-2" v-if="notebookToast.enabled && notebookToast.id === 'notebook'">
            <div
              class="w-1/2 p-2 rounded-lg shadow-lg border bg-red-300 text-red-800 border-red-400"
            >
              <div class="flex itemx-center">
                <FAIcon cl icon="exclamation-triangle" class="mr-1 text-2xl"></FAIcon>
                {{ notebookToast.message }}
              </div>
            </div>
          </div>
        </div>
        <div v-if="!notebookError">
          <div class="flex flex-col" v-if="isEmpty">
            <span class="my-5">No Notebooks.</span>
            <div class="flex">
              <button
                class="btn btn-secondary btn-sm mr-2"
                @click="associateNotebook()"
                :disabled="!loginAsOwner"
              >Associate Notebook</button>
              <button
                class="btn btn-primary btn-sm"
                @click="editNotebook()"
                :disabled="!loginAsOwner"
              >Create Notebook</button>
            </div>
          </div>
          <div class="flex flex-col">
            <div class="flex justify-end my-3" v-if="!isEmpty">
              <div class="flex inline-flex items-center">
                <select class="form-select mr-2 py-1" v-model="sortBy">
                  <option value disabled selected>Sort By</option>
                  <option value="createdAt">Created</option>
                  <option value="name">Name</option>
                </select>
                <input
                  type="text"
                  class="form-input mr-2 py-1"
                  placeholder="Search Notebooks"
                  v-model="searchTerm"
                />
                <button
                  class="btn btn-sm btn-primary text-white mr-2"
                  @click="editNotebook()"
                  :disabled="!loginAsOwner"
                  title="Create Notebook"
                >
                  <FAIcon icon="plus-square" />
                </button>
                <button
                  class="btn btn-sm btn-secondary text-black mr-2"
                  @click="associateNotebook()"
                  :disabled="!loginAsOwner"
                  title="Associate Notebook"
                >
                  <FAIcon icon="link" />
                </button>
              </div>
            </div>
            <vue-good-table
              v-if="!isEmpty"
              :columns="columns"
              :rows="notebooks"
              :line-numbers="true"
              :pagination-options="{ enabled: true, perPage: 5 }"
              :search-options="{ enabled: true, externalQuery: searchTerm }"
              :sort-options="sortOptions"
            >
              <template slot="table-row" slot-scope="props">
                <div class="flex justify-center" v-if="props.column.field === 'status'">
                  <div
                    :class="{'text-green-500': props.row.status === 'ACTIVE', 'text-red-500': props.row.status === 'ARCHIVED'}"
                  >{{props.row.status}}</div>
                </div>
                <div v-else-if="props.column.field === 'actions'">
                  <div class="flex justify-center">
                    <template v-if="props.row.status === 'ACTIVE'">
                      <button
                        class="btn btn-xs btn-primary text-white mx-1"
                        @click="notebookLaunch(props.row)"
                        title="Launch Notebook"
                      >
                        <FAIcon icon="external-link-alt" />
                      </button>
                      <button
                        class="btn btn-xs btn-secondary text-black mx-1"
                        @click="editNotebook(props.row)"
                        :disabled="!loginAsOwner"
                        title="Edit Notebook"
                      >
                        <FAIcon icon="pencil-alt" />
                      </button>
                      <button
                        class="btn btn-xs btn-secondary text-black mx-1"
                        title="Archive Notebook"
                        @click="notebookArchive(props.row)"
                        :disabled="!loginAsOwner"
                      >
                        <FAIcon icon="box" />
                      </button>
                    </template>
                    <template v-else-if="props.row.status === 'ARCHIVED'">
                      <button
                        class="btn btn-xs btn-secondary text-black mx-1"
                        title="Unarchive Notebook"
                        @click="notebookUnarchive(props.row)"
                        :disabled="!loginAsOwner"
                      >
                        <FAIcon icon="box-open" />
                      </button>
                    </template>
                    <button
                      class="btn btn-xs btn-secondary text-black mx-1"
                      title="Delete Notebook Association"
                      @click="notebookDeleteAssociation(props.row)"
                      :disabled="!loginAsOwner"
                    >
                      <FAIcon icon="unlink" />
                    </button>
                  </div>
                </div>
                <div
                  v-else
                  class="flex justify-center px-1"
                >{{ props.formattedRow[props.column.field] }}</div>
              </template>
              <template slot="pagination-bottom" slot-scope="props">
                <pagination-ui
                  :total="props.total"
                  :pageChanged="props.pageChanged"
                  :itemsPerPage="5"
                />
              </template>
            </vue-good-table>
          </div>
        </div>
      </collapsable-ui>
      <modal-ui
        :title="(activeNotebook ? 'Edit' : 'Create') + ' Notebook'"
        size="md"
        v-if="isEdittingNotebook"
        @onDismiss="isEdittingNotebook = false"
      >
        <edit-notebook-form :data="activeNotebook" @onSuccess="isEdittingNotebook = false" />
      </modal-ui>
      <modal-ui
        title="Associate Notebook"
        size="md"
        v-if="isAssociatingNotebook"
        @onDismiss="isAssociatingNotebook = false"
      >
        <associate-notebook-form :data="activeNotebook" @onSuccess="isAssociatingNotebook = false" />
      </modal-ui>
    </div>
  </div>
</template>

<script>
import dayjs from "dayjs";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import EditNotebookForm from "./forms/notebook/edit-notebook.form";
import AssociateNotebookForm from "./forms/notebook/associate-notebook.form";

import { mapActions, mapState, mapMutations } from "vuex";
export default {
  props: ["notebooks"],
  components: {
    CollapsableUi,
    PaginationUi,
    ModalUi,
    EditNotebookForm,
    AssociateNotebookForm
  },
  computed: {
    ...mapState("app", ["wikiConfig"]),
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    ...mapState("notebook", ["notebookError", "notebookToast"]),
    isEmpty() {
      return this.notebooks.length === 0;
    },
    sortOptions() {
      if (this.sortBy === "") {
        return {};
      }

      return {
        enabled: true,
        initialSortBy: { field: this.sortBy, type: "asc" }
      };
    }
  },
  data() {
    return {
      columns: [
        {
          label: "Notebook Name",
          field: "name"
        },
        {
          label: "Version",
          field: "version",
          width: "90px"
        },
        {
          label: "Notebook Type",
          field: "type",
          width: "175px"
        },
        {
          label: "Notebook URL",
          field: "url"
        },
        {
          label: "Status",
          field: "status"
        },
        {
          label: "Created At",
          field: "createdAt",
          sortFn(dateA, dateB) {
            return dayjs(dateA).isBefore(dayjs(dateB)) ? 1 : -1;
          },
          formatFn(value) {
            return dayjs(value).format("YYYY-MM-DD");
          }
        },
        {
          label: "Actions",
          field: "actions",
          width: "100px"
        }
      ],
      activeNotebook: null,
      isEdittingNotebook: false,
      isAssociatingNotebook: false,
      sortBy: "",
      searchTerm: ""
    };
  },
  methods: {
    ...mapMutations("app", ["confirm"]),
    ...mapActions("notebook", [
      "launchNotebook",
      "archiveNotebook",
      "restoreNotebook",
      "deleteNotebookAssociation"
    ]),
    ...mapActions("project", ["getProjectNotebooks"]),
    ...mapActions("app", ["showToastMessage"]),
    editNotebook(notebook) {
      this.activeNotebook = notebook;
      this.isEdittingNotebook = true;
    },
    associateNotebook() {
      this.isAssociatingNotebook = true;
    },
    async notebookLaunch(notebook) {
      const response = await this.launchNotebook(notebook);
      if (response.data.status === "Success") {
        let launchURL = response.data.data.noteBookId.serviceUrl;
        window.open(launchURL, "_blank");
      } else {
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "error"
        });
      }
    },
    async notebookArchive(notebook) {
      this.confirm({
        title: "Archive " + notebook.name,
        body: "Are you sure you want to archive " + notebook.name + "?",
        options: {
          okLabel: "Archive Notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archiveNotebook(notebook);
          if (response.data.status === "Success") {
            await this.getProjectNotebooks();
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
        }
      });
    },
    async notebookUnarchive(notebook) {
      this.confirm({
        title: "Unarchive " + notebook.name,
        body: "Are you sure you want to unarchive " + notebook.name + "?",
        options: {
          okLabel: "Unarchive Notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.restoreNotebook(notebook);
          if (response.data.status === "Success") {
            await this.getProjectNotebooks();
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
        }
      });
    },
    async notebookDeleteAssociation(notebook) {
      this.confirm({
        title: "Delete " + notebook.name + " Association",
        body:
          "Are you sure you want to delete " + notebook.name + "association?",
        options: {
          okLabel: "Delete Notebook Association",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deleteNotebookAssociation(notebook);
          if (response.data.status === "Success") {
            await this.getProjectNotebooks();
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
        }
      });
    }
  }
};
</script>
