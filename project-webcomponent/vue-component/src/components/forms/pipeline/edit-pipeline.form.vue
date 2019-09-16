<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
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
            <input
              type="text"
              class="form-input"
              v-model="updatedPipeline.name"
              placeholder="Enter Data Pipeline Name"
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
          <label class="mt-2">Data Pipeline Description</label>
          <textarea
            class="form-textarea"
            rows="4"
            maxlength="2000"
            v-model="updatedPipeline.description"
            placeholder="Enter Data Pipeline Description"
          ></textarea>
          <span
            class="leading-none text-right text-gray-600 mt-1"
          >{{ 2000 - updatedPipeline.description.length }} Chars</span>
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Data Pipeline URL</label>
          <input
            type="text"
            class="form-input"
            v-model="updatedPipeline.url"
            placeholder="Enter Data Pipeline URL"
          />
        </div>
      </div>
    </div>
    <div class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t">
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button
        class="btn btn-sm btn-primary"
        @click="save(updatedPipeline)"
      >{{ isNew ? "Create" : "Save" }} Data Pipeline</button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";

import Pipeline from "../../../store/entities/pipeline.entity";
import { mapActions, mapState } from "vuex";

export default {
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      updatedPipeline: undefined,
      pipelineCreated: []
    };
  },
  created() {
    this.updatedPipeline = this.isNew
      ? new Pipeline()
      : new Pipeline(this.data);
  },
  computed: {
    ...mapState("app", {
      useExternalPipeline: state => state.useExternalPipeline,
      createTimeout: state => state.createTimeout
    }),
    isNew() {
      return isUndefined(this.data);
    }
  },
  methods: {
    ...mapActions("pipeline", [
      "createPipeline",
      "updatePipeline",
      "createPipelineStatus"
    ]),
    ...mapActions("project", ["getProjectPipelines"]),

    async getPipelineCreationStatus(pipelineId) {
      let pipelineStatus = await this.createPipelineStatus(pipelineId);
      if (pipelineStatus.data.status === "Success") {
        this.pipelineCreated = pipelineStatus.data.data;
      }
    },

    async save(pipeline) {
      const isValid = await this.$refs.form.validate();
      let statusMessage = null;
      if (isValid) {
        if (this.isNew) {
          const pipelineCreation = pipeline.$toJson();
          delete pipelineCreation.pipelineId.uuid;
          statusMessage = await this.createPipeline(pipelineCreation);
          await this.getProjectPipelines();
          if (statusMessage.data.status === "Success") {
            this.$emit("onSuccess");
            this.reset();
            this.pipelineCreated = statusMessage.data.data;
            if (statusMessage.data.code === "202") {
              let creationInterval = setInterval(async () => {
                this.getPipelineCreationStatus(this.pipelineCreated.pipelineId.uuid);
                if (this.pipelineCreated.artifactStatus.status === "ACTIVE") {
                  //this.successMessage = "The pipeline "+this.pipelineCreated.pipelineId.name+" is successfully created";
                  //this.alertOpen = true;
                  await this.getProjectPipelines();
                  clearInterval(creationInterval);
                } else if (
                  this.pipelineCreated.artifactStatus.status === "FAILED"
                ) {
                  //this.errorMessage = "Your pipeline "+this.pipelineCreated.pipelineId.name+" request got failed";
                  //this.alertOpen = true;
                  await this.getProjectPipelines();
                  clearInterval(creationInterval);
                }
              }, this.createTimeout);
            }
          }
        } else {
          statusMessage = await this.updatePipeline(pipeline.$toJson());
          await this.getProjectPipelines();
          this.$emit("onSuccess");
          this.reset();
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
