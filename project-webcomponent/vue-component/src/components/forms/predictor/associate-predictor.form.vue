<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <div class="p-3">
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2"
            >Predictor Name <span class="text-red-500">*</span></label
          >
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
            v-model="updatedPredictor.baseUrl"
            placeholder="Enter Predictor Engine Base URL"
          />
        </div>
        <div class="flex-1 flex flex-col">
          <label class="mt-2">Predictor Engine Version</label>
          <input
            type="text"
            class="form-input"
            placeholder="Enter Predictor Engine Version"
          />
        </div>
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedPredictor)">
        {{ isNew ? "Associate Predictor" : "Save Predictor Association" }}
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";

import Predictor from "../../../store/entities/predictor.entity";

export default {
  props: {
    model: {
      type: Object
    }
  },
  data() {
    return {
      updatedPredictor: new Predictor()
    };
  },
  watch: {
    model(selectedPredictor) {
      this.updatedPredictor = new Predictor(selectedPredictor);
    }
  },
  computed: {
    isNew() {
      return isUndefined(this.model);
    }
  },
  methods: {
    async save(predictor) {
      const isValid = await this.$refs.form.validate();

      if (isValid) {
        //save data

        // cleanup
        this.reset();
      }
    },
    reset() {
      this.updatedPredictor = new Predictor();
      this.$refs.form.reset();
    }
  }
};
</script>
