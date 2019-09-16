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
              :disabled="!loginAsOwner"
            >
              Associate Data Pipeline
            </button>
            <button class="btn btn-primary btn-sm" @click="editPipeline()" :disabled="!loginAsOwner">
              Create Data Pipelines
            </button>
          </div>
        </div>
      </div>
      <div class="flex flex-col">
        <div class="flex justify-end my-3" v-if="!isEmpty">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2 py-1" v-model="sortBy">
              <option>Sort By</option>
              <option value="createdAt">Created</option>
              <option value="name">Name</option>
              <option value="id">ID</option>
            </select>
            <input
              type="text"
              class="form-input mr-2 py-1"
              placeholder="Search Pipelines"
              v-model="searchTerm"
            />
            <button
              class="btn btn-sm btn-primary text-white mr-2"
              @click="editPipeline()" :disabled="!loginAsOwner"
            >
              <FAIcon icon="plus-square" />
            </button>
            <button
              class="btn btn-sm btn-secondary text-black mr-2"
              @click="associatePipeline()" :disabled="!loginAsOwner"
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
          :search-options="{ enabled: true, externalQuery: searchTerm }"
          :sort-options="sortOptions"
        >
          <template slot="table-row" slot-scope="props">
            <div
              class="flex justify-center"
              v-if="props.column.field === 'pushStatus'"
            >
            <!-- need to change -->
            </div>
            <div v-else-if="props.column.field === 'actions'">
              <div class="flex justify-center">
                <template v-if="props.row.status === 'ACTIVE'">
                  <button class="btn btn-xs btn-primary text-white mx-1"
                  @click="pipelineLaunch(props.row)" :disabled="!loginAsOwner">
                    <FAIcon icon="external-link-alt" />
                  </button>
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    @click="editPipeline(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="pencil-alt" />
                  </button>
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Archive'"
                    @click="pipelineArchive(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="box" />
                  </button>
                </template>
                <template v-else-if="props.row.status === 'ARCHIVED'">
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Unarchive'"
                    @click="pipelineUnarchive(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="box-open" />
                  </button>
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Delete'"
                    @click="pipelineDelete(props.row)"
                    :disabled="!loginAsOwner"
                  >
                    <FAIcon icon="trash-alt" />
                  </button>
                </template>
                <template v-else-if="props.row.status === 'IN PROGRESS'">
                </template>
                <template v-else-if="props.row.status === 'FAILED'">
                  <button
                    class="btn btn-xs btn-secondary text-black mx-1"
                    v-tooltip="'Delete'"
                    @click="pipelineDelete(props.row)"
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
      :title="(activePipeline ? 'Edit' : 'Create') + ' Data Pipeline'"
      size="md"
      v-if="isEdittingPipeline"
      @onDismiss="isEdittingPipeline = false"
    >
      <edit-pipeline-form :data="activePipeline" @onSuccess="isEdittingPipeline = false"/>
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
import dayjs from "dayjs";
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "../components/ui/modal.ui";
import EditPipelineForm from "./forms/pipeline/edit-pipeline.form";
import AssociatePipelineForm from "./forms/pipeline/associate-pipeline.form";
import PaginationUi from "../components/ui/pagination.ui";

import { mapActions, mapState } from "vuex";
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
      isEdittingPipeline: false,
      isAssociatingPipeline: false,
      activePipeline: null,
      sortBy: "",
      searchTerm: ""
    };
  },
  computed: {
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    isEmpty() {
      return this.pipelines.length === 0;
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
  methods: {
    ...mapActions("pipeline", ["launchPipeline","archivePipeline","restorePipeline","deletePipeline"]),
    ...mapActions("project", ["getProjectPipelines"]),
    editPipeline(pipeline) {
      this.activePipeline = pipeline;
      this.isEdittingPipeline = true;
    },
    associatePipeline() {
      this.isAssociatingPipeline = true;
    },
    async pipelineLaunch(pipeline) {
      const statusMessage = await this.launchPipeline(pipeline);
      if (statusMessage.data.status === "Success") {
        let launchURL = statusMessage.data.data.pipelineId.serviceUrl;
        window.open(launchURL, "_blank");
      }
    },
    async pipelineArchive(pipeline){
      const statusMessage = await this.archivePipeline(pipeline);
      if(statusMessage.data.status === "Success")
        await this.getProjectPipelines();
    },
    async pipelineUnarchive(pipeline){
      const statusMessage = await this.restorePipeline(pipeline);
      if(statusMessage.data.status === "Success")
        await this.getProjectPipelines();
    },
    async pipelineDelete(pipeline){
      const statusMessage = await this.deletePipeline(pipeline);
      if(statusMessage.data.status === "Success")
        await this.getProjectPipelines();
    }
  }
};
</script>
