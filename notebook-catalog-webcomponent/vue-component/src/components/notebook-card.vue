<template>
  <div
    class="flex flex-col p-4 border border-2 m-3 cursor-pointer hover:shadow-2xl hover:rounded-lg"
    style="width: 21rem;"
  >
    <a @click="$emit('on-open-notebook', notebook)">
      <span class="text-purple-500 font-semibold text-xl py-2">
        {{ notebook.name | truncate(22) }}
        <FAIcon class="mx-2 text-gray-800" icon="users" v-if="notebook.collaborators" />
      </span>

      <div class="flex py-1">
        <span class="font-bold mx-1">Type</span>
        <span>{{ notebook.type }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Version:</span>
        <span>{{ notebook.version }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Status:</span>
        <span :class="statusClass">{{ notebook.status }}</span>
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
        <button class="mx-1" :title="notebook.owner">
          <FAIcon class="text-2xl text-gray-600" icon="user-circle"></FAIcon>
        </button>
      </div>
      <div class="flex">
        <button
          class="mx-2"
          title="Unarchive Notebook"
          v-if="isArchived"
          @click="unarchiveNotebook(notebook)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box-open"></FAIcon>
        </button>
        <a
          :href="notebook.url"
          target="_blank"
          class="mx-2"
          title="Launch Notebook"
          v-if="!isArchived && notebook.url"
        >
          <FAIcon class="text-2xl text-gray-600" icon="external-link-alt"></FAIcon>
        </a>
        <button
          class="mx-2"
          title="Archive Notebook"
          v-if="!isArchived"
          @click="archivenotebook(notebook)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box"></FAIcon>
        </button>
        <button
          class="mx-2"
          title="Delete Notebook"
          v-if="isArchived"
          @click="notebookDelete(notebook)"
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
  props: ["notebook"],
  computed: {
    ...mapState("app", {
      currentUser: state => state.userName
    }),
    isArchived() {
      return this.notebook.status === "ARCHIVED";
    },
    statusClass() {
      let _class = "font-semi-bold ";
      switch (this.notebook.status) {
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
      return dayjs(this.notebook.createdAt).format("YYYY-MM-DD");
    },
    modifiedAt() {
      return dayjs(this.notebook.modifiedAt).format("YYYY-MM-DD");
    }
  },
  methods: {
    ...mapMutations("app", ["confirm"]),
    ...mapActions("notebook", [
      "archiveNotebook",
      "restoreNotebook",
      "deleteNotebook",
      "allNotebooks"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    async archivenotebook(notebook) {
      this.confirm({
        title: "Archive " + notebook.name,
        body: "Are you sure you want to archive " + notebook.name + "?",
        options: {
          okLabel: "Archive notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archiveNotebook(notebook.id);
          if (response.data.status === "Success") {
            await this.allNotebooks();
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
    async unarchiveNotebook(notebook) {
      this.confirm({
        title: "Unarchive " + notebook.name,
        body: "Are you sure you want to unarchive " + notebook.name + "?",
        options: {
          okLabel: "Unarchive notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.restoreNotebook(notebook.id);
          if (response.data.status === "Success") {
            await this.allNotebooks();

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
    async notebookDelete(notebook) {
      this.confirm({
        title: "Delete " + notebook.name,
        body: "Are you sure you want to delete " + notebook.name + "?",
        options: {
          okLabel: "Delete notebook",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deleteNotebook(notebook.id);
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
