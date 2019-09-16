<template>
  <div class="flex w-full">
    <collapsable-ui title="Models" icon="cube" :collapseBorder="!isEmpty">
      <div slot="right-actions" class="inline-flex">
        <a href="#" class="text-sm text-gray-500 underline">Learn More</a>
      </div>
      <div v-if="isEmpty">
        <div class="flex flex-col p-2">
          <span class="my-5">No Models.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="associateModel()" :disabled="!loginAsOwner"
            >Associate Models</button>
          </div>
        </div>
      </div>
      <div v-if="!isEmpty">
        <div class="flex justify-end my-3">
          <div class="flex inline-flex items-center">
            <select class="form-select mr-2">
              <option>Sort By</option>
              <option value="createdAt">Created</option>
              <option value="name">Name</option>
              <option value="id">ID</option>
            </select>
            <input type="text" class="form-input mr-2" placeholder="Search Models" />
            <button class="btn btn-secondary text-black mr-2" @click="associateModel()" :disabled="!loginAsOwner">
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
        >
          <template slot="table-row" slot-scope="props">
            <div class="flex justify-center" v-if="props.column.field === 'publishStatus'">
              <FAIcon class="text-gray-500" icon="cloud" v-if="props.row.publishStatus === 'false'"></FAIcon>
              <FAIcon class="text-green-700" icon="cloud-upload-alt" v-if="props.row.publishStatus === 'true'"></FAIcon>
            </div>

            <div class="flex justify-center" v-else-if="props.column.field === 'modelType'">
                {{ props.row.modelType === "None"? "Others": lookUpCategory(props.row.modelType)}}
            </div>

            <div class="flex justify-center" v-else-if="props.column.field === 'modelCatalog'">
                {{ props.row.modelCatalog === "None"? "Private Catalog": props.row.modelCatalog}}
            </div>

            <div v-else-if="props.column.field === 'actions'">
              <div class="flex justify-center">
                <button class="btn btn-xs btn-primary mx-1" @click="editModelAssociation(props.row)" :disabled="!loginAsOwner">
                  <FAIcon icon="pencil-alt" />
                </button>
                <button class="btn btn-xs btn-secondary text-black mx-1" @click="viewModel(props.row)" :disabled="!loginAsOwner && !(props.row.publishStatus === 'true') ">
                  <FAIcon icon="eye" />
                </button>
                <button class="btn btn-xs btn-secondary text-black mx-1" @click="deleteModelAssociation(props.row)" :disabled="!loginAsOwner">
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
      title="Associate Model"
      size="md"
      v-if="isAssociatingModel"
      @onDismiss="isAssociatingModel = false"
    >
      <associate-model-form :initialModel="activeModel" @onSuccess="isAssociatingModel = false"/>
    </modal-ui>
  </div>
</template>

<script>
import Model from "../store/entities/model.entity";
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "../components/ui/modal.ui";
import PaginationUi from "../components/ui/pagination.ui";
import AssociateModelForm from "../components/forms/model/associate-model.form";
import { mapActions, mapState } from "vuex";
import { find } from "lodash-es";

export default {
  props: ["models"],
  components: {
    CollapsableUi,
    ModalUi,
    PaginationUi,
    AssociateModelForm
  },
 
  computed: {
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
  },
  data() {
    return {
      isAssociatingModel: false,
      activeModel: undefined,
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
          label: "Actions",
          field: "actions",
          width: "100px"
        }
      ],
      rows: []
    };
  },

  methods: {
    ...mapActions("model", ["deleteAssociation", "getModelDetailsForProject"]),
    associateModel() {
      this.activeModel = undefined;
      this.isAssociatingModel = true;
    },

    editModelAssociation(model) {
      this.activeModel = model;
      this.isAssociatingModel = true;
    },

    async deleteModelAssociation(model){
      model = new Model(model);
      const statusMessage = await this.deleteAssociation(model.$toJson());
      await this.getModelDetailsForProject();
    },

    viewModel(model){
      let url = this.portalFEUrl + '/#/marketSolutions?solutionId='+model.modelId+'&revisionId='+model.revisionId+'&parentUrl=mymodel';
      window.open(url, "_blank");
   },
   
    lookUpCategory(modelType){
      return find(this.modelCategories, { code: modelType}).name;
    }
  }
};
</script>
