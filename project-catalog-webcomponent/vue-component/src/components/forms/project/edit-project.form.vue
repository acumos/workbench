<template>
  <ValidationObserver ref="form" tag="div" class="flex flex-col w-full">
    <ToastUI
      id="project-form"
      class="relative p-2"
      innerClass="w-full"
    ></ToastUI>
    <div class="p-3">
      <div class="flex mb-2"></div>
      <div class="flex mb-2">
        <div class="flex-1 flex flex-col mr-2">
          <label class="mt-2">
            Project Name:
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="Project name"
            rules="required|startAlpha"
            v-slot="{ errors, classes }"
          >
            <input
              type="text"
              class="form-input"
              :class="classes"
              v-model="updatedProject.name"
              placeholder="Enter Project Name"
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
          <label class="mt-2">
            Project Version
            <span class="text-red-500">*</span>
          </label>
          <ValidationProvider
            class="flex flex-col"
            name="Project version"
            rules="required|versionValidation"
            v-slot="{ errors, classes }"
          >
            <input
              class="form-input"
              type="text"
              placeholder="Project version"
              v-model="updatedProject.version"
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
        <label class="mt-2">Project Description</label>
        <textarea
          class="form-textarea"
          rows="4"
          maxlength="2000"
          v-model="updatedProject.description"
          placeholder="Enter Project Description"
        ></textarea>
        <span class="leading-none text-right text-gray-600 mt-1"
          >{{ 2000 - updatedProject.description.length }} Chars</span
        >
      </div>
    </div>
    <div
      class="flex justify-between py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="reset()">Reset</button>
      <button class="btn btn-sm btn-primary" @click="save(updatedProject)">
        {{ isNew ? "Create" : "Save" }} Project
      </button>
    </div>
  </ValidationObserver>
</template>

<script>
import { isUndefined } from "lodash-es";
import ToastUI from "../../../../../../vue-common/components/ui/Toast.ui";
import Project from "../../../store/entities/project.entity";
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
      updatedProject: undefined
    };
  },
  created() {
    this.updatedProject = this.isNew ? new Project() : new Project(this.data);
  },
  computed: {
    isNew() {
      return isUndefined(this.data);
    }
  },
  methods: {
    ...mapActions("project", ["createProject", "allProjects"]),
    ...mapActions("app", ["showToastMessage"]),
    async save(project) {
      const isValid = await this.$refs.form.validate();
      let response = null;
      if (isValid) {
        if (this.isNew) {
          const projectCreation = project.$toJson();
          delete projectCreation.projectId.uuid;
          delete projectCreation.artifactStatus;
          delete projectCreation.collaborators;
          response = await this.createProject(projectCreation);
        }

        if (response.data.status === "Success") {
          await this.allProjects();
          this.$emit("onSuccess");
          this.reset();
          this.showToastMessage({
            id: "global",
            message: `${response.data.message}`,
            type: "success"
          });
        } else {
          this.showToastMessage({
            id: "project-form",
            message: `${response.data.message}`,
            type: "error"
          });
        }
      }
    },
    reset() {
      this.updatedProject = new Project();
      this.$refs.form.reset();
    }
  }
};
</script>
