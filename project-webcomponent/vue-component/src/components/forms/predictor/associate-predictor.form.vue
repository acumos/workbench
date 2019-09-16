<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI id="predictor-form" class="relative p-2" innerClass="w-full"></ToastUI>
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">Model Name</label>
          <select class="form-select" v-model="updatedPredictor.modelName" :disabled="!isNew">
            <option value>Select Model Name</option>
            <option
              v-for="(model, index) in projectModels"
              :key="index"
              :value="model.name"
            >{{model.name}}</option>
          </select>
          <!-- <ValidationProvider
            class="flex flex-col"
            name="Model Name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider> -->
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">
            Model Version
            <span class="text-red-500">*</span>
          </label>
          <select class="form-select" v-model="updatedPredictor.modelVersion" :disabled="updatedPredictor.modelName === ''">
            <option value>Select Version</option>
            <option
              v-for="(model, index) in filteredVersions"
              :key="index"
              :value="model.version"
            >{{model.version}}</option>
          </select>
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
            rules="required"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              v-model="updatedPredictor.name"
              placeholder="Enter Predictor Name"
            />
            <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Predictor Engine Key</label>
          <input
            type="text"
            class="form-input"
            v-model="updatedPredictor.key"
            placeholder="Enter Predictor Engine Key"
          />
        </div>
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">Predictor Engine Base URL</label>
          <input
            type="text"
            class="form-input"
            v-model="updatedPredictor.url"
            placeholder="Enter Predictor Engine Base URL"
          />
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Predictor Engine Version</label>
          <input type="text" class="form-input" v-model="updatedPredictor.version" placeholder="Enter Predictor Engine Version" />
        </div>
      </div>
    </div>
    <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button
        class="btn btn-sm btn-primary"
        @click="save(updatedPredictor)"
      >{{ isNew ? "Associate Predictor" : "Save Predictor Association" }}</button>
    </div>
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
      predictors: []
    };
  },
  computed: {
    isNew() {
      return isUndefined(this.initialPredictor);
    },
    filteredVersions() {
      return filter(this.projectModels, model => model.name === this.updatedPredictor.modelName);
    },
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
      "getProjectModels"
    ]),
    async save(predictor) {
      const isValid = await this.$refs.form.validate();
      let response = null;
      if (isValid) {
        if (this.isNew) {
          const predictorAssociation = predictor.$toJson();
          //predictorAssociation.modelId.metrics.kv.splice(-1, 1); need to check
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
      this.$refs.form.reset();
    }
  }
};
</script>
