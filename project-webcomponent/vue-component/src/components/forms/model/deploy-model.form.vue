<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <template>
      <div class="p-3">
        <div class="flex mb-2 w-1/2">
          <div class="flex-1 flex flex-col">
            <label class="mt-2"
              >Select Kubernetes Cluster
              <span class="text-red-500">*</span></label
            >
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
                  >{{ cluster.name }}</option
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
        <button class="btn btn-sm btn-secondary" @click="reset()">
          Cancel
        </button>
        <button class="btn btn-sm btn-primary" @click="deploy()">Deploy</button>
      </div>
    </template>
  </ValidationObserver>
</template>

<script>
import Model from "../../../store/entities/model.entity";
import { mapActions } from "vuex";

export default {
  props: {
    initialModel: {
      type: Object
    }
  },
  data() {
    return {
      selectedCluster: "",
      clusters: []
    };
  },

  async created() {
    this.clusters = await this.getClusters();
  },

  methods: {
    ...mapActions("model", [
     "getClusters",
     "deployToK8s"
    ]),

    async deploy() {
      // const isValid = await this.$refs.form.validate();
      // let response = null;
      // if (isValid) {
      //   if (this.isNew) {
      //     const modelAssociation = model.$toJson();
      //     modelAssociation.modelId.metrics.kv.splice(-1, 1);
      //     response = await this.associateModel(modelAssociation);
      //   } else {
      //     response = await this.updateAssociation(model.$toJson());
      //   }

      //   if (response.data.status === "Success") {
      //     await this.getModelDetailsForProject();
      //     this.$emit("onSuccess");
      //     this.reset();
      //     this.showToastMessage({
      //       id: "global",
      //       message: `${response.data.message}`,
      //       type: "success"
      //     });
      //   } else {
      //     this.showToastMessage({
      //       id: "model-form",
      //       message: `${response.data.message}`,
      //       type: "error"
      //     });
      //   }
      // }
    },
    reset() {
      // this.updatedModel = new Model();
      this.$refs.form.reset();
    }
  }
};
</script>
