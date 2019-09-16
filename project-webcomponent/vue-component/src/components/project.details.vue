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
          v-if="!isEditing && !isArchived"
          @click="editProject()"
          :disabled="!loginAsOwner"
        >
          <FAIcon icon="pencil-alt"></FAIcon>
        </button>
        <button
          class="btn btn-xs btn-primary py-1 ml-2"
          v-if="!isEditing && !isArchived"
          @click="isManagingCollaborators = true"
          :disabled="!loginAsOwner"
        >
          <FAIcon icon="users"></FAIcon>
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
      <div slot="right-actions" class="flex mr-3">
        <template v-if="collaborators.length > 3">
          <div class="inline-flex items-center text-gray-500 text-base mr-2">
            <div class="mx-2 text-xs">{{ collaborators.length - 3 }} more</div>
            <FAIcon icon="ellipsis-h" class="text-xs"></FAIcon>
          </div>
        </template>
        <div class="flex">
          <div
            v-tooltip="{
              content: item.name
            }"
            v-for="(item, index) in firstThreeCollaborators"
            :key="index"
            class="w-8 h-8 border rounded-full inline-flex items-center justify-around shadow-md bg-gray-100 text-gray-400 -mr-2"
          >
            <FAIcon icon="user"></FAIcon>
          </div>
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
    <modal-ui
      title="Manage Collaborators"
      size="md"
      v-if="isManagingCollaborators"
      @onDismiss="isManagingCollaborators = false"
    >
      <CollaboratorsList
        :initialCollaborators="collaborators"
        @onClose="isManagingCollaborators = false"
      />
    </modal-ui>
  </div>
</template>

<script>
import dayjs from "dayjs";
import { mapActions, mapState } from "vuex";

import Project from "../store/entities/project.entity.js";
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "./ui/modal.ui";
import CollaboratorsList from "./collaborators.list";
import Collaborator from "../store/entities/collaborator.entity.js";

export default {
  props: {
    project: {
      type: Object
    }
  },
  components: { CollapsableUi, ModalUi, CollaboratorsList },
  watch: {
    project(currentProject) {
      this.updatedProject = new Project(currentProject);
      if (
        this.updatedProject.collaborators !== [] &&
        this.updatedProject.collaborators !== null
      )
        this.collaborators = this.updatedProject.collaborators.users.map(user =>
          Collaborator.$fromJson(user)
        );
      // this.collaborators = [{"userId": "19a554b1-4b00-4135-a122-2b6061480185","name": "testuser","roles": "READ"}];
    }
  },
  data() {
    return {
      updatedProject: new Project(),
      isEditing: false,
      isManagingCollaborators: false,
      isHoveringOverCollaborators: false,
      collaborators: []
    };
  },
  computed: {
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    firstThreeCollaborators() {
      return this.collaborators.slice(0, 3);
    },
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
      return dayjs(this.project.creationDate).format("YYYY-MM-DD");
    }
  },
  created() {
    this.updatedProject = new Project(this.project);
    if (
      this.updatedProject.collaborators !== [] &&
      this.updatedProject.collaborators !== null
    )
      this.collaborators = this.updatedProject.collaborators.users.map(user =>
        Collaborator.$fromJson(user)
      );
  },
  methods: {
    ...mapActions("project", ["updateProject", "getDetails"]),
    editProject() {
      this.updatedProject = this.project;
      this.isEditing = true;
    },

    async save(updatedProject) {
      const result = await this.updateProject(updatedProject.$toJson());
      await this.getDetails();
      // debugger;

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
