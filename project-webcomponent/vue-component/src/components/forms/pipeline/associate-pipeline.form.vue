<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2"
            >Data Pipeline Name: <span class="text-red-500">*</span></label
          >
          <ValidationProvider
            class="flex flex-col"
            name="Data Pipeline Name"
            rules="required"
            v-slot="{ errors, classes }"
          >
            <select class="form-select" v-model="selectedPipeline">
              <option value="">Select a Pipeline</option>
              <option
                :value="pipeline"
                v-for="(pipeline, index) in pipelines"
                :key="index"
                >{{ pipeline.name }}</option
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
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Data Pipeline Version </label>
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
          <label class="mt-2">Notebok URL</label>
          <input
            type="text"
            class="form-input"
            v-model="selectedPipeline.url"
            placeholder="Enter Notebook URL"
            disabled
          />
        </div>
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedPipeline)">
        {{
          isNew ? "Associate Data Pipeline" : "Save Data Pipeline Association"
        }}
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";

import Pipeline from "../../../store/entities/pipeline.entity";
import { mapActions } from "vuex";

export default {
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      selectedPipeline: ""
    };
  },
  watch: {
    data(selectedPipeline) {
      this.updatedPipeline = new Pipeline(selectedPipeline);
    }
  },
  computed: {
    isNew() {
      return isUndefined(this.notebook);
    },
    pipelines() {
      return Pipeline.all();
    }
  },
  async created() {
    await this.getAllPipelines();
  },
  methods: {
    ...mapActions("pipeline", ["getAllPipelines"]),
    async save(pipeline) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        this.reset();
      }
    },
    reset() {
      this.updatedPipeline = new Pipeline();
      this.$refs.form.reset();
    }
  }
};
</script>
