<template>
  <div class="w-full">
    <collapsable-ui title="Data Pipelines" icon="code-branch">
      <div slot="right-actions" class="inline-flex">
        <a href="#" class="text-sm text-gray-500 underline">Learn More</a>
      </div>
      <div>
        <div class="flex flex-col" v-if="isEmpty">
          <span class="my-5">No Data Pipelines.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="associatePipeline()"
            >
              Associate Data Pipeline
            </button>
            <button class="btn btn-primary btn-sm" @click="editPipeline()">
              Create Data Pipelines
            </button>
          </div>
        </div>
      </div>
      <div class="flex flex-col">
        <div class="flex justify-end my-3" v-if="!isEmpty">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2 py-1">
              <option>Sort By</option>
              <option value="createdAt">Created</option>
              <option value="name">Name</option>
              <option value="id">ID</option>
            </select>
            <input
              type="text"
              class="form-input mr-2 py-1"
              placeholder="Search Pipelines"
            />
            <button
              class="btn btn-sm btn-primary text-white mr-2"
              @click="editNotebook()"
            >
              <FAIcon icon="plus-square" />
            </button>
            <button
              class="btn btn-sm btn-secondary text-black mr-2"
              @click="associatePipeline()"
            >
              <FAIcon icon="link" />
            </button>
          </div>
        </div>
        <vue-good-table
          v-if="!isEmpty"
          :columns="columns"
          :rows="pipelines"
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
                <button
                  class="btn btn-xs btn-secondary text-black mx-1"
                  v-tooltip="'Archive'"
                >
                  <FAIcon icon="box" />
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
      :title="(activePipeline ? 'Edit' : 'Create') + ' Data Pipeline'"
      size="md"
      v-if="isEdittingPipeline"
      @onDismiss="isEdittingPipeline = false"
    >
      <edit-pipeline-form :data="activePipeline" />
    </modal-ui>
    <modal-ui
      title="Associate Data Pipeline"
      size="md"
      v-if="isAssociatingPipeline"
      @onDismiss="isAssociatingPipeline = false"
    >
      <associate-pipeline-form :data="activePipeline" @onSuccess="isAssociatingPipeline = false"/>
    </modal-ui>
  </div>
</template>

<script>
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "../components/ui/modal.ui";
import EditPipelineForm from "./forms/pipeline/edit-pipeline.form";
import AssociatePipelineForm from "./forms/pipeline/associate-pipeline.form";
import PaginationUi from "../components/ui/pagination.ui";

export default {
  components: {
    CollapsableUi,
    ModalUi,
    EditPipelineForm,
    AssociatePipelineForm,
    PaginationUi
  },
  props: ["pipelines"],
  data() {
    return {
      columns: [
        {
          label: "Pipeline Name",
          field: "name"
        },
        {
          label: "Version",
          field: "version",
          width: "90px"
        },
        {
          label: "Pipeline URL",
          field: "url"
        },
        {
          label: "Actions",
          field: "actions",
          width: "100px"
        }
      ],
      isEdittingPipeline: false,
      isAssociatingPipeline: false,
      activePipeline: null
    };
  },
  computed: {
    isEmpty() {
      return this.pipelines.length === 0;
    }
  },
  methods: {
    editPipeline(pipeline) {
      this.activePipeline = pipeline;
      this.isEdittingPipeline = true;
    },
    associatePipeline() {
      this.isAssociatingPipeline = true;
    }
  }
};
</script>
