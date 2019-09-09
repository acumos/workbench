<template>
  <div class="flex w-full" v-if="project">
    <collapsable-ui
      title="Test Project"
      icon="project-diagram"
      :collapse-border="true"
    >
      <div class="inline-flex" slot="left-actions">
        <button
          class="btn btn-xs py-1 px-2 btn-primary"
          v-if="!isEditing"
          @click="isEditing = true"
        >
          <FAIcon icon="pencil-alt"></FAIcon>
        </button>
        <div v-if="isEditing">
          <button
            class="btn btn-xs py-1 px-2 btn-primary rounded-0"
            @click="save(updatedProject)"
          >
            <FAIcon icon="save"></FAIcon>
          </button>
          <button class="ml-2 text-base" @click="revert(project)">
            Cancel
          </button>
        </div>
      </div>
      <table class="project-table">
        <tr>
          <td>Project Name</td>
          <td v-if="!isEditing">{{ project.name }}</td>
          <td v-if="isEditing">
            <input
              class="form-input w-2/6"
              type="text"
              v-model="updatedProject.name"
            />
          </td>
        </tr>
        <tr>
          <td>Project ID</td>
          <td>{{ project.id }}</td>
        </tr>
        <tr>
          <td>Project Version</td>
          <td v-if="!isEditing">{{ project.version }}</td>
          <td v-if="isEditing">
            <input
              class="form-input w-2/6"
              type="text"
              v-model="updatedProject.version"
            />
          </td>
        </tr>
        <tr>
          <td>Project Status</td>
          <td>
            <span class="font-semibold" :class="statusClass">{{
              project.status
            }}</span>
          </td>
        </tr>
        <tr>
          <td>Project Creation Date</td>
          <td>{{ created }}</td>
        </tr>
        <tr>
          <td>Project Description</td>
          <td v-if="!isEditing">{{ project.description }}</td>
          <td v-if="isEditing">
            <textarea
              class="form-textarea w-2/6"
              v-model="updatedProject.description"
            ></textarea>
          </td>
        </tr>
      </table>
    </collapsable-ui>
  </div>
</template>

<script>
import dayjs from "dayjs";

import Project from "../store/entities/project.entity.js";
import CollapsableUi from "../components/ui/collapsable.ui";

export default {
  props: {
    project: {
      type: Object
    }
  },
  components: { CollapsableUi },
  watch: {
    project(currentProject) {
      this.updatedProject = new Project(currentProject);
    }
  },
  data() {
    return {
      updatedProject: new Project(),
      isEditing: false
    };
  },
  computed: {
    statusClass() {
      let _class = "";
      switch (this.project.status) {
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
      return this.project.status === "ARCHIVED";
    },
    created() {
      return dayjs(this.creationDate).format("YYYY-MM-DD");
    }
  },
  methods: {
    save(updatedProject) {
      updatedProject.$save();
      this.isEditing = false;
    },
    revert(project) {
      this.updatedProject = new Project(project);
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
        @apply text-right font-bold bg-gray-300 text-gray-800;
        @apply border-r border-b border-gray-600;
        @apply w-2/12 p-2;
      }
      &:nth-child(2) {
        @apply text-gray-800;
        @apply border-b border-gray-600;
        @apply pl-2;
      }
    }
  }
}
</style>
