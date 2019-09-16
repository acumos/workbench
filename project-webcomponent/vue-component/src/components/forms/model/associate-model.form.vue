<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI id="model-form" class="relative p-2" innerClass="w-full"></ToastUI>
    <div class="p-3">
      <div class="flex mb-2 w-1/2">
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Model Category</label>
          <select class="form-select" v-model="updatedModel.modelType" :disabled="!isNew">
            <option value>Select Category</option>
            <option
              v-for="(category, index) in categories"
              :key="index"
              :value="category.code"
            >{{category.name}}</option>
            <option value="None">Others</option>
          </select>
        </div>
      </div>
      <div class="flex">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">
            Model Name
            <span class="text-red-500">*</span>
          </label>
          <v-select
            :options="filteredModels"
            label="name"
            placeholder="Enter Model Name"
            v-model="updatedModel.name"
            @input="setModelName"
            :disabled="updatedModel.modelType === '' || !isNew"
          ></v-select>

          <!-- <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>-->
          <!-- </template>
          <template v-if="!isNew">
            <input
              type="text"
              class="form-input"
              v-model="updatedModel.name"
              disabled
              placeholder="Enter Model Name"
            />
          </template>-->
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">
            Model Version
            <span class="text-red-500">*</span>
          </label>
          <select
            class="form-select"
            v-model="updatedModel.version"
            :disabled="updatedModel.name === ''"
          >
            <option value>Select Version</option>
            <option
              v-for="(model, index) in filteredVersions"
              :key="index"
              :value="model.version"
            >{{model.version}}</option>
          </select>
        </div>
      </div>
      <div class="flex mb-2 w-1/2">
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Model Catalog</label>
          <select
            class="form-select"
            v-model="updatedModel.modelCatalog"
            :disabled="updatedModel.version === ''"
          >
            <option value>Select Catalog</option>
            <option
              v-for="(catalog, index) in filteredCatalogs"
              :key="index"
              :value="catalog"
            >{{catalog}}</option>
          </select>
        </div>
      </div>
    </div>
    <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button
        class="btn btn-sm btn-primary"
        @click="save(updatedModel)"
      >{{ isNew ? "Associate Model" : "Save Model Association" }}</button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined, uniqBy, filter, differenceWith } from "lodash-es";
import ToastUI from "../../ui/Toast.ui";
import Model from "../../../store/entities/model.entity";
import { mapState, mapActions } from "vuex";

export default {
  components: { ToastUI },
  props: {
    initialModel: {
      type: Object
    }
  },
  data() {
    return {
      updatedModel: new Model(),
      models: [],
      modelsFilteredCategory: [],
      modelsDropdown: [],
      selectedModelArray: [],
      catalogs: [],
      versionSelected: [],
      removedModels: []
    };
  },
  computed: {
    ...mapState("model", {
      categories: state => state.categories
    }),

    isNew() {
      return isUndefined(this.initialModel);
    },

    filteredModelsByCategory() {
      return filter(
        this.removedModels,
        model => model.modelType === this.updatedModel.modelType
      );
    },

    filteredModels() {
      return uniqBy(
        filter(
          this.filteredModelsByCategory,
          model => model.modelType === this.updatedModel.modelType
        ),
        "name"
      );
    },

    filteredVersions() {
      return this.isNew
        ? filter(
            this.filteredModelsByCategory,
            model => model.name === this.updatedModel.name
          )
        : filter(this.models, model => model.name === this.updatedModel.name);
    },

    filteredCatalogs() {
      let selectedVersion = filter(
        this.filteredVersions,
        model => model.version === this.updatedModel.version
      );
      if (selectedVersion.length > 0) {
        return selectedVersion[0].modelCatalog.split(",");
      }
      return;
    }
  },
  async created() {
    this.updatedModel = this.isNew ? new Model() : new Model(this.initialModel);
    this.models = await this.getAllModels();
    this.models = this.models.data.data.map(model => Model.$fromJson(model));
    const projectModels = Model.all();
    this.removedModels = differenceWith(
      this.models,
      projectModels,
      (model, projectModel) =>
        model.modelId === projectModel.modelId &&
        model.version === projectModel.version
    );
  },

  methods: {
    ...mapActions("model", [
      "getAllModels",
      "associateModel",
      "updateAssociation",
      "getModelDetailsForProject"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    setModelName(model) {
      this.updatedModel.name = model.name;
      this.updatedModel.modelId = model.modelId;
      this.updatedModel.publishStatus = model.publishStatus;
    },

    async save(model) {
      const isValid = await this.$refs.form.validate();
      let response = null;
      if (isValid) {
        if (this.isNew) {
          const modelAssociation = model.$toJson();
          modelAssociation.modelId.metrics.kv.splice(-1, 1);
          response = await this.associateModel(modelAssociation);
        } else {
          response = await this.updateAssociation(model.$toJson());
        }

        if (response.data.status === "Success") {
          await this.getModelDetailsForProject();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "model-form",
            message: `${response.data.message}`,
            type: "error"
          });
        }
      }
    },
    reset() {
      this.updatedModel = new Model();
      this.$refs.form.reset();
    }
  }
};
</script>
