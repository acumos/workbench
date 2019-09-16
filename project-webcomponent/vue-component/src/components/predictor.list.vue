<template>
  <div class="flex w-full">
    <collapsable-ui title="Predictors" icon="cubes" :collapseBorder="true">
      <div slot="right-actions" class="inline-flex">
        <a href="#" class="text-sm text-gray-500 underline">Learn More</a>
      </div>
      <div v-if="rows.length === 0">
        <div class="flex flex-col p-2">
          <span class="my-5">No Predictors.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="isAssociatingPredictor = true"
            >
              Associate Predictors
            </button>
          </div>
        </div>
      </div>
      <div v-if="rows.length > 0">
        <div class="flex justify-end my-3">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2">
              <option>Sort By</option>
            </select>
            <input
              type="text"
              class="form-input mr-2"
              placeholder="Search Models"
            />
            <button class="btn btn-secondary text-black mr-2">
              <FAIcon icon="link" />
            </button>
          </div>
        </div>
        <vue-good-table
          :columns="columns"
          :rows="rows"
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
                <button class="btn btn-xs btn-primary mx-1">
                  <FAIcon icon="pencil-alt" />
                </button>
                <button class="btn btn-xs btn-secondary text-black mx-1">
                  <FAIcon icon="eye" />
                </button>
                <button class="btn btn-xs btn-secondary text-black mx-1">
                  <FAIcon icon="unlink" />
                </button>
              </div>
            </div>
            <div v-else class="flex justify-center">
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
      title="Associate Predictor"
      size="md"
      v-if="isAssociatingPredictor"
      @onDismiss="isAssociatingPredictor = false"
    >
      <AssociatePredictorForm :data="activePredictor" />
    </modal-ui>
  </div>
</template>

<script>
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "../components/ui/modal.ui";
import PaginationUi from "../components/ui/pagination.ui";
import AssociatePredictorForm from "../components/forms/predictor/associate-predictor.form";

export default {
  components: {
    CollapsableUi,
    ModalUi,
    PaginationUi,
    AssociatePredictorForm
  },
  data() {
    return {
      isAssociatingPredictor: false,
      activePredictor: null,
      columns: [
        {
          label: "#",
          field: "id"
        },
        {
          label: "Model Name",
          field: "name"
        },
        {
          label: "Model Format",
          field: "format"
        },
        {
          label: "Category",
          field: "category"
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
          field: "pushStatus",
          width: "175px"
        },
        {
          label: "Actions",
          field: "actions",
          width: "100px"
        }
      ],
      rows: []
    };
  }
};
</script>
