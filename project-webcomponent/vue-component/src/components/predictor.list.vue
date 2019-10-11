<template>
  <div class="flex w-full">
    <collapsable-ui title="Predictors" icon="cubes" :collapseBorder="!isEmpty">
      <div slot="right-actions" class="inline-flex">
        <a :href="wikiConfig.predictorWikiURL" target="_blank" class="text-sm text-gray-500 underline">Learn More</a>
      </div>
      <div v-if="isEmpty">
        <div class="flex flex-col p-2">
          <span class="my-5">No Predictors.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="associatePredictor()"
              :disabled="!loginAsOwner"
            >Associate Predictors</button>
          </div>
        </div>
      </div>
      <div v-if="!isEmpty">
        <div class="flex justify-end my-3">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2" v-model="sortBy">
              <option value>Sort By</option>
              <option value="createdTimestamp">Created</option>
              <option value="name">Name</option>
            </select>
            <input
              type="text"
              class="form-input mr-2"
              placeholder="Search Predictors by Name"
              v-model="searchTerm"
            />
            <button
              class="btn btn-secondary text-black mr-2"
              @click="associatePredictor()"
              :disabled="!loginAsOwner"
            >
              <FAIcon icon="link" />
            </button>
          </div>
        </div>
        <vue-good-table
          v-if="!isEmpty"
          :columns="columns"
          :rows="predictors"
          :line-numbers="true"
          :pagination-options="{ enabled: true, perPage: 5 }"
          :search-options="{ enabled: true, externalQuery: searchTerm }"
          :sort-options="sortOptions"
        >
          <template slot="table-row" slot-scope="props">
            <div class="flex justify-center" v-if="props.column.field === 'deployStatus'">
              <FAIcon
                class="text-gray-500"
                icon="cloud"
                v-if="props.row.deployStatus === 'ARCHIVED'"
              ></FAIcon>
              <FAIcon
                class="text-green-700"
                icon="cloud-upload-alt"
                v-if="props.row.deployStatus === 'ACTIVE'"
              ></FAIcon>
            </div>
            <div v-else-if="props.column.field === 'actions'">
              <div class="flex justify-center">
                <button
                  class="btn btn-xs btn-primary mx-1"
                  @click="editPredictorAssociation(props.row)"
                  :disabled="!loginAsOwner"
                >
                  <FAIcon icon="pencil-alt" />
                </button>
                <button class="btn btn-xs btn-secondary text-black mx-1" disabled>
                  <FAIcon icon="eye" />
                </button>
                <button
                  class="btn btn-xs btn-secondary text-black mx-1"
                  @click="deleteAssociationPredictor(props.row)"
                  :disabled="!loginAsOwner"
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
      title="Associate Predictor"
      size="md"
      v-if="isAssociatingPredictor"
      @onDismiss="isAssociatingPredictor = false"
    >
      <AssociatePredictorForm
        :initialPredictor="activePredictor"
        @onSuccess="isAssociatingPredictor = false"
      />
    </modal-ui>
  </div>
</template>

<script>
import dayjs from "dayjs";
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "../components/ui/modal.ui";
import PaginationUi from "../components/ui/pagination.ui";
import AssociatePredictorForm from "../components/forms/predictor/associate-predictor.form";
import Predictor from "../store/entities/predictor.entity";
import { mapActions, mapState, mapMutations } from "vuex";

export default {
  props: ["predictors"],
  components: {
    CollapsableUi,
    ModalUi,
    PaginationUi,
    AssociatePredictorForm
  },
  computed: {
    ...mapState("app", [
      "wikiConfig"]),
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    isEmpty() {
      return this.predictors.length === 0;
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
      isAssociatingPredictor: false,
      activePredictor: undefined,
      sortBy: "",
      searchTerm: "",
      columns: [
        {
          label: "Predictor Name",
          field: "name"
        },
        {
          label: "Key",
          field: "key"
        },
        {
          label: "Base URL",
          field: "url"
        },
        {
          label: "Deploy Status",
          field: "deployStatus",
          width: "175px"
        },
        // {
        //   label: "Created At",
        //   field: "createdTimestamp",
        //   sortFn(dateA, dateB) {
        //     return dayjs(dateA).isBefore(dayjs(dateB)) ? 1 : -1;
        //   },
        //   formatFn(value) {
        //     return dayjs(value).format("YYYY-MM-DD");
        //   }
        // },
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
    ...mapActions("model", ["getModelDetailsForProject"]),
    ...mapActions("app", ["showToastMessage"]),
    ...mapActions("predictor", [
      "getProjectPredictors",
      "deletePredictorAssociation"
    ]),
    associatePredictor() {
      this.activePredictor = undefined;
      this.isAssociatingPredictor = true;
    },

    editPredictorAssociation(predictor) {
      this.activePredictor = predictor;
      this.isAssociatingPredictor = true;
    },

    async deleteAssociationPredictor(predictor) {
      predictor = new Predictor(predictor);
      this.confirm({
        title: "Delete " + predictor.name + " Association",
        body:
          "Are you sure you want to delete " + predictor.name + " association?",
        options: {
          okLabel: "Delete Predictor Association",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deletePredictorAssociation(predictor);
          if (response.data.status === "Success") {
            await this.getProjectPredictors();
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
