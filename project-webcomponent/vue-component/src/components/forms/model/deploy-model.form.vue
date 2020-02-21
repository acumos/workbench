<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <template v-if="loadingPredictors">
      <div class="flex flex-col m-6 items-center">
        <FAIcon icon="spinner" pulse class="text-3xl" />
        <span class="my-2">Loading Predictors</span>
      </div>
    </template>
    <template v-if="!loadingPredictors">
      <ToastUI id="deploy-form" class="relative p-2" innerClass="w-full"></ToastUI>
      <div class="p-3">
       <div class="flex mb-2">
          <label class="mt-2"> <FAIcon icon="info-circle" /> Deploying this model outside the Acumos system may expose its information to third parties.</label>
        </div>
        <div class="flex mb-2">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">Model Name<span class="text-red-500">*</span>
             </label>
            <ValidationProvider
              class="flex flex-col"
              name="Model Name"
              rules="required"
              v-slot="{ errors, classes }"
            >
             <input
                type="text"
                class="form-input"
                v-model="updatedPredictor.name"
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
              Model Version
              <span class="text-red-500">*</span>
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Model version"
              rules="required"
              v-slot="{ errors, classes }"
            >
               <input
                type="text"
                class="form-input"
                v-model="updatedPredictor.version"
               :disabled="!isNew"
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
            <label class="mt-2">Select Kubernetes Cluster
			<span class="text-red-500">*</span></label>
            <ValidationProvider
              class="flex flex-col"
              name="Kubernetes Cluster"
              rules="required"
              v-slot="{ errors, classes }"
            >
              <select class="form-select" v-model="selectedCluster">
                <option value>Select Kubernetes Cluster</option>
                <option
                  v-for="(cluster, index) in clusters"
                  :key="index"
                  :value="cluster.name"
                >{{cluster.name}}</option>
                 <option value="cluster1"> Cluster 1</option>
              </select>
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div> 
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Enter Predictor Name
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
              v-model="predictorName"
                placeholder="Enter Predictor Name"
                 @focus="showPredictor = true"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
		
		
		    <div class="flex mb-2" v-if="showPredictor">
          <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Predictor Engine Key
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Predictor Engine Key"
              v-slot="{ errors, classes }"
            >
              <input
                type="text"
                class="form-input"
                v-model="predictorkey"
                placeholder="Enter Predictor Engine Key"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
		        <div class="flex-1 flex flex-col mr-2">
            <label class="mt-2">
              Predictor Engine Base URL
            </label>
            <ValidationProvider
              class="flex flex-col"
              name="Predictor Engine Base URL"
              rules="url"
              v-slot="{ errors, classes }"
              disabled
            >
              <input
                type="text"
                class="form-input"
                v-model="predictorUrl"
                placeholder="Enter Predictor Engine Base URL"
              />
              <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </div>
        </div>
		</div>
		 <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
        <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
        <button
          class="btn btn-sm btn-primary"
          @click="deploy(predictor)"
        >Deploy </button>
      </div>
    </template>
  </ValidationObserver>
</template>

<script>
import { isUndefined, filter } from "lodash-es";
import ToastUI from "../../../vue-common/components/ui/Toast.ui";
import Predictor from "../../../store/entities/predictor.entity";
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
      updatedPredictor: new Model(),
      projectModels: [],
      predictor: new Predictor(),
      predictorName: "",
      predictorkey: "",
      predictorUrl: "",
      clusters:[],
      modelName: "",
      modelVersion: "",
      selectedCluster: "",
      showPredictor: false,
      model: [],
      loadingPredictors: false
    };
  },
  computed: {
    ...mapState("app", {
      userName: state => state.userName
    }),
    isNew() {
      return isUndefined(this.initialModel);
    },
    filteredVersions() {
      return filter(this.projectModels, model => model.name === this.modelName);
    }
  },
  async created() {
    this.updatedPredictor = new Model(this.initialModel);	
    let model = {
      'modelId' : this.updatedPredictor.modelId,
      'version' : this.updatedPredictor.version
    }
    
	this.loadingPredictors = true;
    let pre = await this.getPredictorsForModel(model);
   
  if(pre.data.data !== ""){
   this.predictorModel = JSON.parse(pre.data.data);
    this.predictorName = this.predictorModel.predictorId.name;
    this.predictorkey = this.predictorModel.predictorId.metrics.kv[0].value;
   this.predictorUrl = this.predictorModel.predictorId.serviceUrl;
  } 
  this.loadingPredictors = false;

  },
   async clusters() {
   this.clusters = await this.getClusters();
 },
  methods: {
    ...mapActions("predictor", [
      "associatePredictor",
      "getProjectPredictors",
      "getClusters",
      "deployModelKub",
      "getPredictorsForModel"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    resetForm(){
      this.updatedPredictor = new Predictor();
      this.modelVersion = "";
       this.selectedCluster = "";
    },
    
    async deploy(predictor) {
     const isValid = await this.$refs.form.validate();
      if (isValid) {
     this.updatedPredictor.cluster = this.selectedCluster;
      predictor = new Predictor(this.updatedPredictor);
      let response = null;
      let deployModel = predictor.$toDeployJson();
           response = await this.deployModelKub(deployModel);

        if (response.data.status === "Success") {
          await this.getProjectPredictors();
          await this.getPredictorDeploymentStatus();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "deploy-form",
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
      this.selectedCluster = "";
      this.$refs.form.reset();
    }
  }
};
</script>
