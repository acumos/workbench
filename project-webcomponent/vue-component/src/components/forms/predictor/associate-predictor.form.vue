<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <template v-if="loadingPredictors">
      <div class="flex flex-col m-6 items-center">
        <FAIcon icon="spinner" pulse class="text-3xl" />
        <span class="my-2">Loading Predictors</span>
      </div>
    </template>
    <template v-if="!loadingPredictors">
      <ToastUI id="predictor-form" class="relative p-2" innerClass="w-full"></ToastUI>
      <div class="p-3">
        <div class="flex mb-2" v-if="isNew">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Model Name</label>
            <ValidationProvider
              class="flex flex-col"
              name="Model Name"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <select class="form-select" v-model="modelName" @change="resetForm">
                <option value>Select Model Name</option>
                <option
                  v-for="(model, index) in projectModels"
                  :key="index"
                  :value="model.name"
                >{{model.name}}</option>
              </select>
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
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
                v-model="modelVersion"
                :disabled="modelName === ''"
                @change="getPredictors"
              >
                <option value>Select Version</option>
                <option
                  v-for="(model, index) in filteredVersions"
                  :key="index"
                  :value="model.version"
                >{{model.version}}</option>
              </select>
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
        <div class="flex mb-2">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Predictor Name
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Predictor Name"
              rules="required|startAlpha"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="updatedPredictor.name"
                placeholder="Enter Predictor Name"
                :disabled="!isNew"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
          <div class="flex-1 flex flex-col">
            <label class="mt-2">
              Predictor Engine Key
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Predictor Engine Key"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="updatedPredictor.key"
                placeholder="Enter Predictor Engine Key"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
        <div class="flex mb-2">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Predictor Engine Base URL
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Predictor Engine Base URL"
              rules="required|url"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="updatedPredictor.url"
                placeholder="Enter Predictor Engine Base URL"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
          <!-- <div class="flex-1 flex flex-col">
            <label class="mt-2">
              Predictor Engine Version
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Predictor Engine Version"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="updatedPredictor.version"
                placeholder="Enter Predictor Engine Version"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div> -->
        </div>
      </div>
      <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
        <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
        <button
          class="btn btn-sm btn-primary"
          @click="save(updatedPredictor)"
        >{{ isNew ? "Associate Predictor" : "Save Predictor Association" }}</button>
      </div>
    </template>
  </ValidationObserver>
</template>

<script>
import { isUndefined, filter } from "lodash-es";
import ToastUI from "../../ui/Toast.ui";
import Predictor from "../../../store/entities/predictor.entity";
import { mapState, mapActions } from "vuex";

export default {
  components: { ToastUI },
  props: {
    initialPredictor: {
      type: Object
    }
  },
  data() {
    return {
      updatedPredictor: new Predictor(),
      projectModels: [],
      predictors: [],
      modelName: "",
      modelVersion: "",
      model: [],
      loadingPredictors: false
    };
  },
  computed: {
    ...mapState("app", {
      userName: state => state.userName
    }),
    isNew() {
      return isUndefined(this.initialPredictor);
    },
    filteredVersions() {
      return filter(this.projectModels, model => model.name === this.modelName);
    }
  },
  async created() {
    this.updatedPredictor = this.isNew
      ? new Predictor()
      : new Predictor(this.initialPredictor);
    this.projectModels = await this.getProjectModels();
  },
  methods: {
    ...mapActions("predictor", [
      "associatePredictor",
      "updateAssociation",
      "getProjectPredictors",
      "getProjectModels",
      "getPredictorsForModel"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    async getPredictors() {
      this.model = filter(
        this.projectModels,
        model =>
          model.name === this.modelName && model.version === this.modelVersion
      );
      this.loadingPredictors = true;
      let predictor = await this.getPredictorsForModel(this.model[0]);
      this.loadingPredictors = false;
      if (predictor.data.data !== "") {
        predictor = JSON.parse(predictor.data.data);
        this.updatedPredictor = Predictor.$predictorfromJson(predictor);
      }
    },

    resetForm(){
      this.updatedPredictor = new Predictor();
      this.modelVersion = "";
    },
    
    async save(predictor) {
      const isValid = await this.$refs.form.validate();
      predictor = new Predictor(predictor);
      let response = null;
      if (isValid) {
        if (this.isNew) {
          let predictorAssociation = predictor.$toJson();
          predictorAssociation.userId = this.userName;
          predictorAssociation.solutionId = this.model[0].modelId;
          predictorAssociation.revisionId = this.model[0].revisionId;
          response = await this.associatePredictor(predictorAssociation);
        } else {
          response = await this.updateAssociation(predictor.$toJson());
        }

        if (response.data.status === "Success") {
          await this.getProjectPredictors();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "predictor-form",
            message: `${response.data.message}`,
            type: "error"
          });
        }
      }
    },
    reset() {
      this.updatedPredictor = new Predictor();
      this.modelName = "";
      this.modelVersion = "";
      this.model = "";
      this.$refs.form.reset();
    }
  }
};
</script>
