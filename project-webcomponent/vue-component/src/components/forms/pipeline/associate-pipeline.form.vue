<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI id="associate-pipeline" class="relative p-2" innerClass="w-full"></ToastUI>
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">
            Data Pipeline Name:
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="Data Pipeline Name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <select class="form-select" v-model="selectedPipeline">
              <option value>Select a Pipeline</option>
              <option
                :value="pipeline"
                v-for="(pipeline, index) in filteredPipelines"
                :key="index"
              >{{ pipeline.name }}</option>
            </select>
            <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
              <FAIcon icon="exclamation-triangle" />
              <span class="ml-1 my-1">{{ errors[0] }}</span>
            </span>
          </ValidationProvider>
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Data Pipeline Version</label>
          <input
            disabled
            type="text"
            class="form-input"
            v-model="selectedPipeline.version"
            placeholder="Data Pipeline Version"
          />
        </div>
      </div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">Data Pipeline URL</label>
          <input
            type="text"
            class="form-input"
            v-model="selectedPipeline.url"
            placeholder="Enter Data Pipeline URL"
            disabled
          />
        </div>
      </div>
    </div>
    <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(selectedPipeline)">
        {{
        isNew ? "Associate Data Pipeline" : "Save Data Pipeline Association"
        }}
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined, sortBy } from "lodash-es";
import ToastUI from "../../ui/Toast.ui";
import Pipeline from "../../../store/entities/pipeline.entity";
import { mapActions } from "vuex";

export default {
  components: { ToastUI },
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      selectedPipeline: "",
      pipelines: []
    };
  },
  watch: {
    data(selectedPipeline) {
      this.updatedPipeline = new Pipeline(selectedPipeline);
    }
  },
  computed: {
    isNew() {
      return isUndefined(this.pipeline);
    },
    filteredPipelines() {
      return sortBy(this.pipelines, ["name"]);
    }
  },
  async created() {
    this.pipelines = await this.getAllPipelines();
  },
  methods: {
    ...mapActions("pipeline", ["getAllPipelines", "associatePipeline"]),
    ...mapActions("project", ["getProjectPipelines"]),
    ...mapActions("app", ["showToastMessage"]),
    async save(pipeline) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        pipeline = new Pipeline(pipeline);
        const response = await this.associatePipeline(pipeline);
        if (response.data.status === "Success") {
          await this.getProjectPipelines();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "associate-pipeline",
            message: `${response.data.message}`,
            type: "error"
          });
        }
      }
    },
    reset() {
      this.updatedPipeline = new Pipeline();
      this.$refs.form.reset();
    }
  }
};
</script>
