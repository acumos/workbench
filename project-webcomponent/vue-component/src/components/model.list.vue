<template>
  <div class="flex w-full">
    <collapsable-ui title="Models" icon="cube" :collapseBorder="!isEmpty">
      <div slot="right-actions" class="inline-flex">
        <a
          :href="wikiConfig.modelWikiURL"
          target="_blank"
          class="text-sm text-gray-500 underline"
        >Learn More</a>
      </div>
      <div v-if="isEmpty">
        <div class="flex flex-col p-2">
          <span class="my-5">No Models.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="associateModel()"
              :disabled="!loginAsOwner"
            >Associate Models</button>
          </div>
        </div>
      </div>
      <div v-if="!isEmpty">
        <div class="flex justify-end my-3">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2" v-model="sortBy">
              <option value="" disabled selected>Sort By</option>
              <option value="createdTimestamp">Created</option>
              <option value="name">Name</option>
            </select>
            <input
              type="text"
              class="form-input mr-2"
              placeholder="Search Models by Name"
              v-model="searchTerm"
            />
            <button
              class="btn btn-secondary text-black mr-2"
              @click="associateModel()"
              :disabled="!loginAsOwner"
              v-tooltip="'Associate Model'"
            >
              <FAIcon icon="link" />
            </button>
          </div>
        </div>
        <vue-good-table
          v-if="!isEmpty"
          :columns="columns"
          :rows="models"
          :line-numbers="true"
          :pagination-options="{ enabled: true, perPage: 5 }"
          :search-options="{ enabled: true, externalQuery: searchTerm }"
          :sort-options="sortOptions"
        >
          <template slot="table-row" slot-scope="props">
            <div class="flex justify-center" v-if="props.column.field === 'publishStatus'">
              <FAIcon class="text-gray-500" icon="cloud" v-if="props.row.publishStatus === 'false'"></FAIcon>
              <FAIcon
                class="text-green-700"
                icon="cloud-upload-alt"
                v-if="props.row.publishStatus === 'true'"
              ></FAIcon>
            </div>

            <div class="flex justify-center" v-else-if="props.column.field === 'modelType'">
              {{
              props.row.modelType === "None"
              ? "Others"
              : lookUpCategory(props.row.modelType)
              }}
            </div>

            <div class="flex justify-center" v-else-if="props.column.field === 'modelCatalog'">
              {{
              props.row.modelCatalog === "None"
              ? "Private Catalog"
              : props.row.modelCatalog
              }}
            </div>

            <div v-else-if="props.column.field === 'actions'">
              <div class="flex justify-center">
                <button
                  class="btn btn-xs btn-primary mx-1"
                  @click="editModelAssociation(props.row)"
                  :disabled="!loginAsOwner"
                  v-tooltip="'Edit Model Association'"
                >
                  <FAIcon icon="pencil-alt" />
                </button>
                <button
                  class="btn btn-xs btn-secondary text-black mx-1"
                  @click="viewModel(props.row)"
                  :disabled="
                    !loginAsOwner && !(props.row.publishStatus === 'true')
                  "
                  v-tooltip="'View Model'"
                >
                  <FAIcon icon="eye" />
                </button>
                <button
                  class="btn btn-xs btn-secondary text-black mx-1"
                  @click="deleteModelAssociation(props.row)"
                  :disabled="!loginAsOwner"
                  v-tooltip="'Delete Model Association'"
                >
                  <FAIcon icon="unlink" />
                </button>
              </div>
            </div>
            <div v-else class="flex justify-center">{{ props.formattedRow[props.column.field] }}</div>
          </template>
          <template slot="pagination-bottom" slot-scope="props">
            <pagination-ui :total="props.total" :pageChanged="props.pageChanged" :itemsPerPage="5" />
          </template>
        </vue-good-table>
      </div>
    </collapsable-ui>
    <modal-ui
      :title="activeModel ? 'Edit Model Association' : 'Associate Model'"
      size="md"
      v-if="isAssociatingModel"
      @onDismiss="isAssociatingModel = false"
    >
      <associate-model-form :initialModel="activeModel" @onSuccess="isAssociatingModel = false" />
    </modal-ui>
  </div>
</template>

<script>
import dayjs from "dayjs";
import Model from "../store/entities/model.entity";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import AssociateModelForm from "../components/forms/model/associate-model.form";
import { mapActions, mapState, mapMutations } from "vuex";
import { find, filter } from "lodash-es";

export default {
  props: ["models"],
  components: {
    CollapsableUi,
    ModalUi,
    PaginationUi,
    AssociateModelForm
  },

  computed: {
    ...mapState("app", ["wikiConfig"]),
    ...mapState("model", {
      modelCategories: state => state.categories
    }),
    ...mapState("app", {
      portalFEUrl: state => state.portalFEUrl
    }),
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    isEmpty() {
      return this.models.length === 0;
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
      isAssociatingModel: false,
      activeModel: undefined,
      sortBy: "",
      searchTerm: "",
      columns: [
        {
          label: "Model Name",
          field: "name"
        },
        {
          label: "Model Catalog",
          field: "modelCatalog"
        },
        {
          label: "Model Category",
          field: "modelType"
        },
        {
          label: "Status",
          field: "status",
          width: "80px"
        },
        {
          label: "Version",
          field: "version",
          width: "80px"
        },
        {
          label: "Model Publish Status",
          field: "publishStatus",
          width: "175px"
        },
        {
          label: "Created At",
          field: "createdTimestamp",
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
      rows: []
    };
  },

  methods: {
    ...mapMutations("app", ["confirm"]),
    ...mapActions("model", [
      "deleteAssociation",
      "getModelDetailsForProject",
      "getPredictorDetailsForProject"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    ...mapActions("predictor", ["deletePredictorAssociation", "getProjectPredictors"]),
    associateModel() {
      this.activeModel = undefined;
      this.isAssociatingModel = true;
    },

    editModelAssociation(model) {
      this.activeModel = model;
      this.isAssociatingModel = true;
    },

    async deleteModelAssociation(model) {
      model = new Model(model);
      let predictors = await this.getPredictorDetailsForProject();
      let predictorAssociated = [];
      if(predictors.length > 0){
        predictorAssociated = filter(
          predictors,
          predictor =>
            predictor.modelId === model.modelId &&
            predictor.modelVersion === model.version
        );
      }

      if (predictorAssociated.length > 0) {
        this.confirm({
        title: "Delete " + model.name + " Association",
        body:
          "Removing the Model association will also remove Predictor association.",
        options: {
          okLabel: "Confirm",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
            const response = await this.deletePredictorAssociation(
              predictorAssociated[0]
            );
            if (response.data.status === "Success") {
              await this.getProjectPredictors();
              const response1 = await this.deleteAssociation(model.$toJson());
              if (response1.data.status === "Success") {
                await this.getModelDetailsForProject();
                this.showToastMessage({
                  id: "global",
                  message: `${response1.data.message}`,
                  type: "success"
                });
              } else {
                this.showToastMessage({
                  id: "global",
                  message: `${response1.data.message}`,
                  type: "error"
                });
              }
            } else {
              this.showToastMessage({
                id: "global",
                message: `${response.data.message}`,
                type: "error"
              });
            }
          }
        });
      } else{
        this.confirm({
          title: "Delete " + model.name + " Association",
          body:
            "Are you sure you want to delete " + model.name + " association?",
          options: {
            okLabel: "Delete Model Association",
            dismissLabel: "Cancel"
          },
          onOk: async () => {
            const response = await this.deleteAssociation(model.$toJson());
            if (response.data.status === "Success") {
              await this.getModelDetailsForProject();
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
    },

    viewModel(model) {
      let url =
        this.portalFEUrl +
        "/#/marketSolutions?solutionId=" +
        model.modelId +
        "&revisionId=" +
        model.revisionId +
        "&parentUrl=mymodel";
      window.open(url, "_blank");
    },

    lookUpCategory(modelType) {
      return find(this.modelCategories, { code: modelType }).name;
    }
  }
};
</script>
