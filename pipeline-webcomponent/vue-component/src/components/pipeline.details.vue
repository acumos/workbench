<template>
  <ValidationObserver ref="form" tag="div" class="flex w-full">
    <collapsable-ui
      :title="pipeline.name"
      icon="code-branch"
      :collapse-border="true"
    >
      <div class="inline-flex" slot="left-actions">
        <button
          class="btn btn-xs btn-primary w-8 h-8"
          v-if="!isEditing && !isArchived"
          @click="editPipeline()"
          title="Edit Pipeline"
        >
          <FAIcon icon="pencil-alt"></FAIcon>
        </button>
        
        <div v-if="isEditing">
          <button
            class="btn btn-xs py-1 px-2 btn-primary rounded-0"
            @click="save(updatedPipeline)"
            title="Save Pipeline"
          >
            <FAIcon icon="save"></FAIcon>
          </button>
          <button class="ml-2 text-base" @click="revert(pipeline)">
            Cancel
          </button>
        </div>
      </div>

      <table class="project-table">
        <tr>
          <td>
            Pipeline Name <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ pipeline.name }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Pipeline Name"
              rules="required|startAlpha"
              v-slot="{ errors, classes }"
            >
              <input
                class="form-input w-2/6"
                type="text"
                v-model="updatedPipeline.name"
              />
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </td>
        </tr>
        <tr>
          <td>Pipeline ID</td>
          <td>{{ pipeline.id }}</td>
        </tr>
        <tr>
          <td>
            Pipeline Version <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ pipeline.version }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Pipeline Version"
              rules="required|versionValidation"
              v-slot="{ errors, classes }"
            >
              <input
                class="form-input w-2/6"
                type="text"
                v-model="updatedPipeline.version"
              />
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </td>
        </tr>
        <tr>
          <td>Pipeline Status</td>
          <td>
            <span class="font-semibold" :class="statusClass">
              {{ pipeline.status }}
            </span>
          </td>
        </tr>
        <tr>
          <td>Pipeline Creation Date</td>
          <td>{{ created }}</td>
        </tr>
        <tr>
          <td>Pipeline Modified Date</td>
          <td>{{ modified }}</td>
        </tr>
        <tr>
          <td>
            Pipeline Description
            <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ pipeline.description }}</td>
          <td v-if="isEditing">
           
              <textarea
                class="form-textarea w-1/2 h-24 my-2"
                v-model="updatedPipeline.description"
                maxlength="2000"
                placeholder="Enter Data Pipeline Description"
              ></textarea>
              <span class="leading-none text-right text-gray-600 mt-1"
                >{{ 2000 - updatedPipeline.description.length }} Chars</span
              >
          </td>
        </tr>
        <tr>
          <td>
            Pipeline URL
            <span v-if="isEditing" class="text-red-500">*</span>
          </td>
           <td v-if="!isEditing">{{ pipeline.url }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Pipeline URL"
              rules="required|url"
              v-slot="{ errors, classes }"
            >
              <textarea
                class="form-textarea w-1/2 h-24 my-2"
                v-model="updatedPipeline.url"
              ></textarea>
              <span
                class="text-sm text-red-700 flex items-center"
                v-if="errors[0]"
              >
                <FAIcon icon="exclamation-triangle" />
                <span class="ml-1 my-1">{{ errors[0] }}</span>
              </span>
            </ValidationProvider>
          </td>
        </tr>
      </table>
    </collapsable-ui>
  </ValidationObserver>
</template>

<script>
import dayjs from "dayjs";
import { mapActions } from "vuex";

import Pipeline from "../store/entities/pipeline.entity.js";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";

export default {
  props: {
    pipeline: {
      type: Object
    }
  },
  components: { CollapsableUi },
  data() {
    return {
      updatedPipeline: new Pipeline(),
      isEditing: false
    };
  },
  computed: {
    statusClass() {
      let _class = "";
      switch (this.pipeline.status) {
        case "ACTIVE":
          _class = "text-green-600";
          break;
        case "ARCHIVED":
          _class = "text-orange-600";
          break;
      }

      return _class;
    },
    isArchived() {
      return this.pipeline.status === "ARCHIVED";
    },
    created() {
      return dayjs(this.pipeline.creationDate).format("YYYY-MM-DD");
    },
    modified() {
      return dayjs(this.pipeline.modifiedDate).format("YYYY-MM-DD");
    }
  },
  watch:{
    pipeline() {
      this.updatedPipeline = new Pipeline(this.pipeline);
    }
   },

  created() {
    this.updatedPipeline = new Pipeline(this.pipeline);
  },
  methods: {
    ...mapActions("pipeline", ["updatePipeline", "getPipelineDetails"]),
    ...mapActions("app", ["showToastMessage"]),
    editPipeline() {
      this.isEditing = true;
    },

    async save(updatedPipeline) {
      const isValid = await this.$refs.form.validate();

      if (!isValid) {
        return;
      }

      const response = await this.updatePipeline(updatedPipeline.$toJson());
      if (response.data.status === "Success") {
        await this.getPipelineDetails();
        this.isEditing = false;
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "success"
        });
      } else {
        this.showToastMessage({
          id: "global",
          message: `${response.data.message}`,
          type: "error"
        });
      }
    },
    revert(pipeline) {
      this.updatedPipeline = new Pipeline(pipeline);
      this.isEditing = false;
    }
  }
};
</script>

<style lang="postcss">
table.project-table {
  @apply w-full;

  tr {
    td {
      &:first-child {
        @apply text-right font-bold bg-gray-300 text-black;
        @apply border-r border-b border-gray-600;
        @apply w-2/12 p-2;
      }
      &:nth-child(2) {
        @apply text-black;
        @apply border-b border-gray-600;
        @apply pl-2;
      }
    }
  }
}
</style>
