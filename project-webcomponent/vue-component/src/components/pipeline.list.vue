<template>
  <div class="w-full">
    <div class="flex w-full">
      <collapsable-ui title="Data Pipelines" icon="code-branch">
        <div slot="right-actions" class="inline-flex">
          <a
            :href="wikiConfig.pipelineWikiURL"
            target="_blank"
            class="text-sm text-gray-500 underline"
          >Learn More</a>
        </div>
        <div v-if="pipelineError">
          <div class="mt-2" v-if="pipelineToast.enabled && pipelineToast.id === 'pipeline'">
            <div
              class="w-1/2 p-2 rounded-lg shadow-lg border bg-red-300 text-red-800 border-red-400"
            >
              <div class="flex itemx-center">
                <FAIcon cl icon="exclamation-triangle" class="mr-1 text-2xl"></FAIcon>
                {{ pipelineToast.message }}
              </div>
            </div>
          </div>
        </div>
        <div v-if="!pipelineError">
          <div class="flex flex-col" v-if="isEmpty">
            <span class="my-5">No Data Pipelines.</span>
            <div class="flex">
              <button
                class="btn btn-secondary btn-sm mr-2"
                @click="associatePipeline()"
                :disabled="!loginAsOwner"
              >Associate Data Pipeline</button>
              <button
                class="btn btn-primary btn-sm"
                @click="editPipeline()"
                :disabled="!loginAsOwner"
              >Create Data Pipelines</button>
            </div>
          </div>
          <div class="flex flex-col">
            <div class="flex justify-end my-3" v-if="!isEmpty">
              <div class="flex inline-flex items-center">
                <select class="form-select mr-2 py-1" v-model="sortBy">
                  <option value disabled selected>Sort By</option>
                  <option value="createdAt">Created Date</option>
                  <option value="name">Name</option>
                </select>
                <input
                  type="text"
                  class="form-input mr-2 py-1"
                  placeholder="Search Pipelines"
                  v-model="searchTerm"
                />
                <button
                  class="btn btn-sm btn-primary text-white mr-2"
                  @click="editPipeline()"
                  :disabled="!loginAsOwner"
                  title="Create Pipeline"
                >
                  <FAIcon icon="plus-square" />
                </button>
                <button
                  class="btn btn-sm btn-secondary text-black mr-2"
                  @click="associatePipeline()"
                  :disabled="!loginAsOwner"
                  title="Associate Pipeline"
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
                <div class="flex justify-center" v-if="props.column.field === 'status'">
                  <div
                    :class="{'text-green-500': props.row.status === 'ACTIVE', 'text-red-500': props.row.status === 'ARCHIVED', 'text-blue-500': props.row.status === 'INPROGRESS', 'text-red-700': props.row.status === 'FAILED'}"
                  >{{props.row.status}}</div>
                </div>
                <div v-else-if="props.column.field === 'actions'">
                  <div class="flex justify-center">
                    <template v-if="props.row.status === 'ACTIVE'">
                      <button
                        class="btn btn-xs btn-primary text-white mx-1"
                        @click="pipelineLaunch(props.row)"
                        title="Launch Pipeline"
                      >
                        <FAIcon icon="external-link-alt" />
                      </button>
                      <button
                        class="btn btn-xs btn-secondary text-black mx-1"
                        @click="editPipeline(props.row)"
                        :disabled="!loginAsOwner"
                        title="Edit Pipeline"
                      >
                        <FAIcon icon="pencil-alt" />
                      </button>
                      <button
                        class="btn btn-xs btn-secondary text-black mx-1"
                        title="Archive Pipeline"
                        @click="pipelineArchive(props.row)"
                        :disabled="!loginAsOwner"
                      >
                        <FAIcon icon="box" />
                      </button>
                    </template>
                    <template v-else-if="props.row.status === 'ARCHIVED'">
                      <button
                        class="btn btn-xs btn-secondary text-black mx-1"
                        title="Unarchive Pipeline"
                        @click="pipelineUnarchive(props.row)"
                        :disabled="!loginAsOwner"
                      >
                        <FAIcon icon="box-open" />
                      </button>
                    </template>

                    <button
                      class="btn btn-xs btn-secondary text-black mx-1"
                      title="Delete Pipeline Association"
                      @click="pipelineDeleteAssociation(props.row)"
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
        :title="(activePipeline ? 'Edit' : 'Create') + ' Data Pipeline'"
        size="md"
        v-if="isEdittingPipeline"
        @onDismiss="isEdittingPipeline = false"
      >
        <edit-pipeline-form :data="activePipeline" @onSuccess="isEdittingPipeline = false" />
      </modal-ui>
      <modal-ui
        title="Associate Data Pipeline"
        size="md"
        v-if="isAssociatingPipeline"
        @onDismiss="isAssociatingPipeline = false"
      >
        <associate-pipeline-form :data="activePipeline" @onSuccess="isAssociatingPipeline = false" />
      </modal-ui>
    </div>
  </div>
</template>

<script>
import dayjs from "dayjs";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import EditPipelineForm from "./forms/pipeline/edit-pipeline.form";
import AssociatePipelineForm from "./forms/pipeline/associate-pipeline.form";
import PaginationUi from "../vue-common/components/ui/pagination.ui";

import { mapActions, mapState, mapMutations } from "vuex";
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
    ...mapState("app", ["wikiConfig"]),
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    ...mapState("pipeline", ["pipelineToast", "pipelineError"]),
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
    ...mapMutations("app", ["confirm"]),
    ...mapActions("pipeline", [
      "launchPipeline",
      "archivePipeline",
      "restorePipeline",
      "deletePipelineAssociation"
    ]),
    ...mapActions("project", ["getProjectPipelines"]),
    ...mapActions("app", ["showToastMessage"]),
    editPipeline(pipeline) {
      this.activePipeline = pipeline;
      this.isEdittingPipeline = true;
    },
    associatePipeline() {
      this.isAssociatingPipeline = true;
    },
    async pipelineLaunch(pipeline) {
      const response = await this.launchPipeline(pipeline);
      if (response.data.status === "Success") {
        let launchURL = response.data.data.pipelineId.serviceUrl;
        window.open(launchURL, "_blank");
      } else {
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "error"
        });
      }
    },
    async pipelineArchive(pipeline) {
      this.confirm({
        title: "Archive " + pipeline.name,
        body: "Are you sure you want to archive " + pipeline.name + "?",
        options: {
          okLabel: "Archive Pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archivePipeline(pipeline);
          if (response.data.status === "Success") {
            await this.getProjectPipelines();
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
    async pipelineUnarchive(pipeline) {
      this.confirm({
        title: "Unarchive " + pipeline.name,
        body: "Are you sure you want to unarchive " + pipeline.name + "?",
        options: {
          okLabel: "Unarchive Pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.restorePipeline(pipeline);
          if (response.data.status === "Success") {
            await this.getProjectPipelines();
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
    async pipelineDeleteAssociation(pipeline) {
      this.confirm({
        title: "Delete " + pipeline.name + " Association",
        body:
          "Are you sure you want to delete " + pipeline.name + "association?",
        options: {
          okLabel: "Delete Pipeline Association",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deletePipelineAssociation(pipeline);
          if (response.data.status === "Success") {
            await this.getProjectPipelines();
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
