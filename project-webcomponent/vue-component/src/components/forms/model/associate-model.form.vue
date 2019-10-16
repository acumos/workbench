<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <template v-if="loadingModels">
      <div class="flex flex-col m-6 items-center">
        <FAIcon icon="spinner" pulse class="text-3xl" />
        <span class="my-2">Loading models</span>
      </div>
    </template>
    <template v-if="!loadingModels">
      <ToastUI
        id="model-form"
        class="relative p-2"
        innerClass="w-full"
      ></ToastUI>
      <div class="p-3">
        <div class="flex mb-2 w-1/2">
          <div class="flex-1 flex flex-col">
            <label class="mt-2"
              >Model Category <span class="text-red-500">*</span></label
            >
            <ValidationProvider
              class="flex flex-col"
              name="Model category"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <select
                class="form-select"
                v-model="updatedModel.modelType"
                :disabled="!isNew"
              >
                <option value>Select Category</option>
                <option
                  v-for="(category, index) in categories"
                  :key="index"
                  :value="category.code"
                  >{{ category.name }}</option
                >
                <option value="None">Others</option>
              </select>
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
        <div class="flex">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Model Name
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Model name"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <v-select
                :options="filteredModels"
                label="name"
                placeholder="Enter Model Name"
                v-model="updatedModel.name"
                @input="setModelName"
                :disabled="updatedModel.modelType === '' || !isNew"
              ></v-select>
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
          <div class="flex-1 flex flex-col">
            <label class="mt-2">
              Model Version
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Model version"
              rules="required"
              v-slot="{ errors, classes }"
            >
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
                  >{{ model.version }}</option
                >
              </select>
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
        <div class="flex mb-2 w-1/2">
          <div class="flex-1 flex flex-col">
            <label class="mt-2"
              >Model Catalog <span class="text-red-500">*</span></label
            >
            <ValidationProvider
              class="flex flex-col"
              name="Model catalog"
              rules="required"
              v-slot="{ errors, classes }"
            >
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
                  >{{ catalog }}</option
                >
              </select>
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
      </div>
      <div
        class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
      >
        <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
        <button class="btn btn-sm btn-primary" @click="save(updatedModel)">
          {{ isNew ? "Associate Model" : "Save Model Association" }}
        </button>
      </div>
    </template>
  </ValidationObserver>
</template>

<script>
import { isUndefined, uniqBy, filter, differenceWith, sortBy } from "lodash-es";
import ToastUI from "../../../vue-common/components/ui/Toast.ui";
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
      loadingModels: false,
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
      const filtered = filter(
        this.filteredModelsByCategory,
        model => model.modelType === this.updatedModel.modelType
      );
      const unique = uniqBy(filtered, "name");

      return sortBy(unique, ["name"]);
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
      let filteredCatalogs = [];

      let selectedVersion = filter(
        this.filteredVersions,
        model => model.version === this.updatedModel.version
      );
      if (selectedVersion.length > 0) {
        filteredCatalogs = selectedVersion[0].modelCatalog.split(",");
      }

      return filteredCatalogs;
    }
  },
  async created() {
    this.loadingModels = true;
    this.updatedModel = this.isNew ? new Model() : new Model(this.initialModel);
    const models = await this.getAllModels();
    this.loadingModels = false;

    this.models = models.data.data.map(model => Model.$fromJson(model));
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
