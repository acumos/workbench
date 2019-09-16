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
            >
              Associate Notebook
            </button>
            <button class="btn btn-primary btn-sm" @click="editNotebook()">
              Create Notebook
            </button>
          </div>
        </div>
      </div>
      <div class="flex flex-col">
        <div class="flex justify-end my-3" v-if="!isEmpty">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2 py-1">
              <option>Sort By</option>
            </select>
            <input
              type="text"
              class="form-input mr-2 py-1"
              placeholder="Search Notebooks"
            />
            <button
              class="btn btn-sm btn-primary text-white mr-2"
              @click="editNotebook()"
            >
              <FAIcon icon="plus-square" />
            </button>
            <button
              class="btn btn-sm btn-secondary text-black mr-2"
              @click="associateNotebook()"
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
        >
          <template slot="table-row" slot-scope="props">
            <div
              class="flex justify-center"
              v-if="props.column.field === 'pushStatus'"
            >
              <FAIcon
                class="text-gray-500"
                icon="cloud"
                v-if="props.row.pushStatus"
              ></FAIcon>
              <FAIcon
                class="text-green-700"
                icon="cloud-upload-alt"
                v-if="!props.row.pushStatus"
              ></FAIcon>
            </div>
            <div v-else-if="props.column.field === 'actions'">
              <div class="flex justify-center">
                <button class="btn btn-xs btn-primary text-white mx-1">
                  <FAIcon icon="external-link-alt" />
                </button>
                <button
                  class="btn btn-xs btn-secondary text-black mx-1"
                  @click="editNotebook(props.row)"
                >
                  <FAIcon icon="pencil-alt" />
                </button>
                <button class="btn btn-xs btn-secondary text-black mx-1">
                  <FAIcon icon="trash" />
                </button>
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
      <edit-notebook-form :data="activeNotebook" />
    </modal-ui>
    <modal-ui
      title="Associate Notebook"
      size="md"
      v-if="isAssociatingNotebook"
      @onDismiss="isAssociatingNotebook = false"
    >
      <associate-notebook-form :data="activeNotebook" />
    </modal-ui>
  </div>
</template>

<script>
import CollapsableUi from "../components/ui/collapsable.ui";
import PaginationUi from "../components/ui/pagination.ui";
import ModalUi from "./ui/modal.ui";
import EditNotebookForm from "./forms/notebook/edit-notebook.form";
import AssociateNotebookForm from "./forms/notebook/associate-notebook.form";

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
    isEmpty() {
      return this.notebooks.length === 0;
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
          label: "Actions",
          field: "actions",
          width: "100px"
        }
      ],
      activeNotebook: null,
      isEdittingNotebook: false,
      isAssociatingNotebook: false
    };
  },
  methods: {
    editNotebook(notebook) {
      this.activeNotebook = notebook;
      this.isEdittingNotebook = true;
    },
    associateNotebook() {
      this.isAssociatingNotebook = true;
    }
  }
};
</script>
