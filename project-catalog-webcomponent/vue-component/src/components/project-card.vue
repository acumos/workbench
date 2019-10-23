<template>
  <div
    class="flex flex-col p-4 border border-2 m-3 cursor-pointer hover:shadow-2xl hover:rounded-lg"
    style="width: 21rem;"
  >
    <a @click="$emit('on-open-project', project)">
      <span class="text-purple-500 font-semibold text-xl py-2">
        {{ project.name | truncate(22) }}
        <FAIcon class="mx-2 text-gray-800" icon="users" v-if="project.collaborators" />
      </span>

      <div class="flex py-1">
        <span class="font-bold mx-1">ID:</span>
        <span>{{ project.id }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Version:</span>
        <span>{{ project.version }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Status:</span>
        <span :class="statusClass">{{ project.status }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Creation Date:</span>
        <span>{{ createdAt }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Modified Date:</span>
        <span>{{ modifiedAt }}</span>
      </div>
    </a>
    <div class="flex bg-gray-200 py-3 mt-2 px-2 justify-between">
      <div>
        <button class="mx-1" :title="project.owner">
          <FAIcon class="text-2xl text-gray-600" icon="user-circle"></FAIcon>
        </button>
        <button
          class="mx-1"
          v-if="currentUser !== project.owner"
          :title="currentUser"
        >
          <FAIcon class="text-2xl text-gray-600" icon="user-circle"></FAIcon>
        </button>
      </div>
      <div>
        <button
          class="mx-2"
          title="Unarchive Project"
          v-if="isArchived"
          @click="unarchiveProject(project)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box-open"></FAIcon>
        </button>
        <button
          class="mx-2"
          title="Archive Project"
          v-if="!isArchived"
          @click="archiveProject(project)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box"></FAIcon>
        </button>
        <button
          class="mx-2"
          title="Delete Project"
          v-if="isArchived"
          @click="projectDelete(project)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="trash-alt"></FAIcon>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from "dayjs";
import { mapActions, mapMutations, mapState } from "vuex";

export default {
  props: ["project"],
  computed: {
    ...mapState("app", {
      currentUser: state => state.userName
    }),
    isArchived() {
      return this.project.status === "ARCHIVED";
    },
    statusClass() {
      let _class = "font-semi-bold ";
      switch (this.project.status) {
        case "ACTIVE":
          _class += "text-green-500";
          break;
        case "ARCHIVED":
          _class += "text-red-500";
          break;
      }

      return _class;
    },
    createdAt() {
      return dayjs(this.project.createdAt).format("YYYY-MM-DD");
    },
    modifiedAt() {
      return dayjs(this.project.modifiedAt).format("YYYY-MM-DD");
    }
  },
  methods: {
    ...mapMutations("app", ["confirm"]),
    ...mapActions("project", [
      "archive",
      "unarchive",
      "deleteProject",
      "allProjects"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    async archiveProject(project) {
      this.confirm({
        title: "Archive " + project.name,
        body: "Are you sure you want to archive " + project.name + "?",
        options: {
          okLabel: "Archive Project",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archive(project.id);
          if (response.data.status === "Success") {
            await this.allProjects();
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
        }
      });
    },
    async unarchiveProject(project) {
      this.confirm({
        title: "Unarchive " + project.name,
        body: "Are you sure you want to unarchive " + project.name + "?",
        options: {
          okLabel: "Unarchive Project",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.unarchive(project.id);
          if (response.data.status === "Success") {
            await this.allProjects();
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
        }
      });
    },
    async projectDelete(project) {
      this.confirm({
        title: "Delete " + project.name,
        body: "Are you sure you want to delete " + project.name + "?",
        options: {
          okLabel: "Delete Project",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deleteProject(project.id);
          if (response.data.status !== "Success") {
            this.showToastMessage({
              id: "global",
              message: `${response.data.message}`,
              type: "error"
            });
          }
        }
      });
    }
  }
};
</script>
