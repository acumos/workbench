<template>
  <div class="w-full">
    <collapsable-ui
      title="Notebooks"
      icon="book-open"
      :collapse-border="!isEmpty"
    >
      <div slot="right-actions" class="inline-flex">
        <a href="#" class="text-sm text-gray-500 underline">Learn More</a>
      </div>
      <div>
        <div class="flex flex-col" v-if="isEmpty">
          <span class="my-5">No Notebooks.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="associateNotebook()"
              :disabled="!loginAsOwner"
            >
              Associate Notebook
            </button>
            <button class="btn btn-primary btn-sm" @click="editNotebook()" :disabled="!loginAsOwner">
              Create Notebook
            </button>
          </div>
        </div>
      </div>
      <div class="flex flex-col">
        <div class="flex justify-end my-3" v-if="!isEmpty">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2 py-1" v-model="sortBy">
              <option value="">Sort By</option>
              <option value="createdAt">Created</option>
              <option value="name">Name</option>
              <option value="id">ID</option>
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
            >
              <FAIcon icon="plus-square" />
            </button>
            <button
              class="btn btn-sm btn-secondary text-black mr-2"
              @click="associateNotebook()"
              :disabled="!loginAsOwner"
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
            <div
              class="flex justify-center"
              v-if="props.column.field === 'pushStatus'"
            >
              <!-- <div class="text-green-700" v-if="props.row.status === 'ACTIVE'"></div>
              <div class="text-red-700" v-if="props.row.status === 'ARCHIVED'"></div> -->
            </div>
            <div v-else-if="props.column.field === 'actions'">
              <div class="flex justify-center">
                <template v-if="props.row.status === 'ACTIVE'">
                  <button
                    class="btn btn-xs btn-primary text-white mx-1"
                    @click="notebookLaunch(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="external-link-alt" />
                  </button>
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    @click="editNotebook(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="pencil-alt" />
                  </button>
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Archive'"
                    @click="notebookArchive(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="box" />
                  </button>
                </template>
                <template v-else-if="props.row.status === 'ARCHIVED'">
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Unarchive'"
                    @click="notebookUnarchive(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="box-open" />
                  </button>
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Delete'"
                    @click="notebookDelete(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="trash-alt" />
                  </button>
                </template>
              </div>
            </div>
            <div v-else class="flex justify-left px-1">
              {{ props.formattedRow[props.column.field] }}
            </div>
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
    </collapsable-ui>
    <modal-ui
      :title="(activeNotebook ? 'Edit' : 'Create') + ' Notebook'"
      size="md"
      v-if="isEdittingNotebook"
      @onDismiss="isEdittingNotebook = false"
    >
      <edit-notebook-form
        :data="activeNotebook"
        @onSuccess="isEdittingNotebook = false"
      />
    </modal-ui>
    <modal-ui
      title="Associate Notebook"
      size="md"
      v-if="isAssociatingNotebook"
      @onDismiss="isAssociatingNotebook = false"
    >
      <associate-notebook-form
        :data="activeNotebook"
        @onSuccess="isAssociatingNotebook = false"
      />
    </modal-ui>
  </div>
</template>

<script>
import dayjs from "dayjs";
import CollapsableUi from "../components/ui/collapsable.ui";
import PaginationUi from "../components/ui/pagination.ui";
import ModalUi from "./ui/modal.ui";
import EditNotebookForm from "./forms/notebook/edit-notebook.form";
import AssociateNotebookForm from "./forms/notebook/associate-notebook.form";

import { mapActions, mapState } from "vuex";
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
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    isEmpty() {
      return this.notebooks.length === 0;
    },
    sortOptions() {
      if (this.sortBy === "") {
        return {};
      }

      return {
        enabled: true,
        initialSortBy: { field: this.sortBy, type: "desc" }
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
    ...mapActions("notebook", ["launchNotebook","archiveNotebook","restoreNotebook","deleteNotebook"]),
    ...mapActions("project", ["getProjectNotebooks"]),
    editNotebook(notebook) {
      this.activeNotebook = notebook;
      this.isEdittingNotebook = true;
    },
    associateNotebook() {
      this.isAssociatingNotebook = true;
    },
    async notebookLaunch(notebook) {
      const statusMessage = await this.launchNotebook(notebook);
      if (statusMessage.data.status === "Success") {
        let launchURL = statusMessage.data.data.noteBookId.serviceUrl;
        window.open(launchURL, "_blank");
      }
    },
    async notebookArchive(notebook){
      const statusMessage = await this.archiveNotebook(notebook);
      if(statusMessage.data.status === "Success")
        await this.getProjectNotebooks();
    },
    async notebookUnarchive(notebook){
      const statusMessage = await this.restoreNotebook(notebook);
      if(statusMessage.data.status === "Success")
        await this.getProjectNotebooks();
    },
    async notebookDelete(notebook){
      const statusMessage = await this.deleteNotebook(notebook);
      if(statusMessage.data.status === "Success")
        await this.getProjectNotebooks();
    }
  }
};
</script>
