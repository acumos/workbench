<template>
  <ValidationObserver ref="form" tag="div" class="flex w-full">
    <collapsable-ui
      :title="project.name"
      icon="project-diagram"
      :collapse-border="true"
    >
      <div class="inline-flex" slot="left-actions">
        <button
          class="btn btn-xs btn-primary w-8 h-8"
          v-if="!isEditing && !isArchived"
          @click="editProject()"
          :disabled="!loginAsOwner"
          v-tooltip="'edit'"
        >
          <FAIcon icon="pencil-alt"></FAIcon>
        </button>
        <button
          class="btn btn-xs btn-primary ml-2 w-8 h-8"
          v-if="!isEditing && !isArchived"
          @click="isManagingCollaborators = true"
          :disabled="!loginAsOwner"
          v-tooltip="'Manage Collaborators'"
        >
          <FAIcon icon="users"></FAIcon>
        </button>
        <div class="flex ml-4" v-if="!isEditing && !isArchived">
          <div class="flex">
            <div
              v-tooltip="item.firstName+' '+item.lastName"
              v-for="(item, index) in firstThreeCollaborators"
              :key="index"
              class="w-8 h-8 border rounded-full inline-flex items-center justify-around shadow-md bg-gray-100 text-gray-400 -ml-2"
            >
              <FAIcon icon="user"></FAIcon>
            </div>
          </div>
          <template v-if="collaborators.length > 3">
            <div class="inline-flex items-center text-gray-500 text-base ml-2">
              <FAIcon icon="ellipsis-h" class="text-xs"></FAIcon>
              <div class="mx-2 text-xs">
                {{ collaborators.length - 3 }} more
              </div>
            </div>
          </template>
        </div>
        <div v-if="isEditing">
          <button
            class="btn btn-xs py-1 px-2 btn-primary rounded-0"
            @click="save(updatedProject)"
            v-tooltip="'Save Project'"
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
          <td>
            Project Name <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ project.name }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Project Name"
              rules="required|startAlpha"
              v-slot="{ errors, classes }"
            >
              <input
                class="form-input w-2/6 my-2"
                type="text"
                v-model="updatedProject.name"
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
          <td>Project ID</td>
          <td>{{ project.id }}</td>
        </tr>
        <tr>
          <td>
            Project Version <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ project.version }}</td>
          <td v-if="isEditing">
            <ValidationProvider
              class="flex flex-col"
              name="Project Version"
              rules="required|versionValidation"
              v-slot="{ errors, classes }"
            >
              <input
                class="form-input w-2/6 my-2"
                type="text"
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
          </td>
        </tr>
        <tr>
          <td>Project Status</td>
          <td>
            <span class="font-semibold" :class="statusClass">
              {{ project.status }}
            </span>
          </td>
        </tr>
        <tr>
          <td>Project Creation Date</td>
          <td>{{ created }}</td>
        </tr>
        <tr>
          <td>Project Modified Date</td>
          <td>{{ modified }}</td>
        </tr>
        <tr>
          <td>
            Project Description
            <span v-if="isEditing" class="text-red-500">*</span>
          </td>
          <td v-if="!isEditing">{{ project.description }}</td>
          <td v-if="isEditing">
              <textarea
                class="form-textarea w-1/2 h-24 my-2"
                v-model="updatedProject.description"
                maxlength="2000"
                placeholder="Enter Project Description"
              ></textarea>
              <span class="leading-none text-right text-gray-600 mt-1"
                >{{ 2000 - updatedProject.description.length }} Chars</span
              >          
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
  </ValidationObserver>
</template>

<script>
import dayjs from "dayjs";
import { mapActions, mapState } from "vuex";
import { get } from "lodash-es";

import Project from "../store/entities/project.entity.js";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import CollaboratorsList from "./collaborators.list";
import Collaborator from "../store/entities/collaborator.entity.js";

export default {
  props: {
    project: {
      type: Object
    }
  },
  components: { CollapsableUi, ModalUi, CollaboratorsList },
  data() {
    return {
      updatedProject: new Project(),
      isEditing: false,
      isManagingCollaborators: false,
      isHoveringOverCollaborators: false
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
    },
    modified() {
      return dayjs(this.project.modifiedDate).format("YYYY-MM-DD");
    },
    collaborators() {
      return get(this.project, "collaborators.users", []).map(user =>
        Collaborator.$fromJson(user)
      );
    }
  },
  watch:{
    project() {
      this.updatedProject = new Project(this.project);
    }
   },

  created() {
    this.updatedProject = new Project(this.project);
  },
  
  methods: {
    ...mapActions("project", ["updateProject", "getDetails"]),
    ...mapActions("app", ["showToastMessage"]),
    editProject() {
      this.isEditing = true;
    },

    async save(updatedProject) {
      const isValid = await this.$refs.form.validate();

      if (!isValid) {
        return;
      }

      const response = await this.updateProject(updatedProject.$toJson());
      if (response.data.status === "Success") {
        await this.getDetails();
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
