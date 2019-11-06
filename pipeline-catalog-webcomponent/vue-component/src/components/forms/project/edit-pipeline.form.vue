<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI
      id="pipeline-form"
      class="relative p-2"
      innerClass="w-full"
    ></ToastUI>
    <div class="p-3">
      <div class="flex mb-2"></div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col">
          <label class="mt-2">
            Data Pipeline Name:
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="Pipeline name"
            rules="required|startAlpha"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              :class="classes"
              v-model="updatedPipeline.name"
              placeholder="Enter Pipeline Name"
            />
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
      <div class="flex flex-col">
        <label class="mt-2"
          >Pipeline URL <span class="text-red-500">*</span></label
        >
        <ValidationProvider
          class="flex flex-col"
          name="Pipeline URL"
          rules="required"
          v-slot="{ errors, classes }"
        >
          <input
            type="text"
            class="form-input"
            :class="classes"
            v-model="updatedPipeline.url"
            placeholder="Enter Pipeline URL"
          />
          <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
            <FAIcon icon="exclamation-triangle" />
            <span class="ml-1 my-1">{{ errors[0] }}</span>
          </span>
        </ValidationProvider>
      </div>
      <div class="flex flex-col">
        <label class="mt-2">
          Pipeline Description
        </label>
        <ValidationProvider
          class="flex flex-col"
          name="Pipeline description"
          rules=""
          v-slot="{ errors, classes }"
        >
          <textarea
            class="form-textarea"
            rows="4"
            maxlength="1000"
            v-model="updatedPipeline.description"
            placeholder="Enter Pipeline Description"
          ></textarea>
          <span class="leading-none text-right text-gray-600 mt-1"
            >{{ 1000 - updatedPipeline.description.length }} Chars</span
          >
          <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
            <FAIcon icon="exclamation-triangle" />
            <span class="ml-1 my-1">{{ errors[0] }}</span>
          </span>
        </ValidationProvider>
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedPipeline)">
        {{ isNew ? "Create" : "Save" }} Data Pipeline
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";
import ToastUI from "../../../vue-common/components/ui/Toast.ui";
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
      updatedPipeline: undefined
    };
  },
  created() {
    this.updatedPipeline = this.isNew
      ? new Pipeline()
      : new Pipeline(this.data);
  },
  computed: {
    isNew() {
      return isUndefined(this.data);
    }
  },
  methods: {
    ...mapActions("pipeline", ["createPipeline", "allPipelines"]),
    ...mapActions("app", ["showToastMessage"]),
    async save(pipeline) {
      const isValid = await this.$refs.form.validate();
      let response = null;
      if (isValid) {
        if (this.isNew) {
          const pipelineCreation = pipeline.$toJson();
          delete pipelineCreation.pipelineId.uuid;
          response = await this.createPipeline(pipelineCreation);
        }

        if (response.data.status === "Success") {
          await this.allPipelines();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "pipeline-form",
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
