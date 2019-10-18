<template>
  <div
    @click="$emit('on-open-pipeline', pipeline)"
    class="flex flex-col p-4 border border-2 m-3 cursor-pointer hover:shadow-2xl hover:rounded-lg"
    style="width: 21rem;"
  >
    <span class="text-purple-500 font-semibold text-xl py-2">
      {{ pipeline.name | truncate(22) }}
      <FAIcon class="mx-2 text-gray-800" icon="users" v-if="pipeline.collaborators" />
    </span>

    <div class="flex py-1">
      <span class="font-bold mx-1">ID:</span>
      <span>{{ pipeline.id }}</span>
    </div>
    <div class="flex py-1">
      <span class="font-bold mx-1">Version:</span>
      <span>{{ pipeline.version }}</span>
    </div>
    <div class="flex py-1">
      <span class="font-bold mx-1">Status:</span>
      <span :class="statusClass">{{ pipeline.status }}</span>
    </div>
    <div class="flex py-1">
      <span class="font-bold mx-1">Creation Date:</span>
      <span>{{ createdAt }}</span>
    </div>
    <div class="flex py-1">
      <span class="font-bold mx-1">Modified Date:</span>
      <span>{{ modifiedAt }}</span>
    </div>
    <div class="flex bg-gray-200 py-3 mt-2 px-2 justify-between">
      <div>
        <button class="mx-1" :title="pipeline.owner" v-tooltip="pipeline.owner">
          <FAIcon class="text-2xl text-gray-600" icon="user-circle"></FAIcon>
        </button>
      </div>
      <div class="flex" v-if="!inProgress">
        <button
          class="mx-2"
          v-tooltip="'Unarchive Pipeline'"
          v-if="isArchived"
          @click="unarchivePipeline(pipeline)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box-open"></FAIcon>
        </button>
        <a
          :href="pipeline.url"
          target="_blank"
          class="mx-2"
          v-tooltip="'Launch Pipeline'"
          v-if="!isArchived && pipeline.url"
        >
          <FAIcon class="text-2xl text-gray-600" icon="external-link-alt"></FAIcon>
        </a>
        <button
          class="mx-2"
          v-tooltip="'Archive Pipeline'"
          v-if="!isArchived"
          @click="archivepipeline(pipeline)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box"></FAIcon>
        </button>
        <button
          class="mx-2"
          v-tooltip="'Delete Pipeline'"
          v-if="isArchived || isFailed"
          @click="pipelineDelete(pipeline)"
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
  props: ["pipeline"],
  computed: {
    ...mapState("app", {
      currentUser: state => state.userName
    }),
    inProgress() {
      return this.pipeline.status === "INPROGRESS";
    },
    isFailed() {
      return this.pipeline.status === "FAILED";
    },
    isArchived() {
      return this.pipeline.status === "ARCHIVED";
    },
    statusClass() {
      let _class = "font-semi-bold ";
      switch (this.pipeline.status) {
        case "ACTIVE":
          _class += "text-green-500";
          break;
        case "ARCHIVED":
          _class += "text-red-500";
          break;
        case "INPROGRESS":
          _class += "text-blue-500";
          break;
        case "FAILED":
          _class += "text-red-700";
          break;
      }

      return _class;
    },
    createdAt() {
      return dayjs(this.pipeline.createdAt).format("YYYY-MM-DD");
    },
    modifiedAt() {
      return dayjs(this.pipeline.modifiedAt).format("YYYY-MM-DD");
    }
  },
  methods: {
    ...mapMutations("app", ["confirm"]),
    ...mapActions("pipeline", [
      "archivePipeline",
      "restorePipeline",
      "deletePipeline",
      "allPipelines"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    async archivepipeline(pipeline) {
      this.confirm({
        title: "Archive " + pipeline.name,
        body: "Are you sure you want to archive " + pipeline.name + "?",
        options: {
          okLabel: "Archive pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.archivePipeline(pipeline.id);
          if (response.data.status === "Success") {
            await this.allPipelines();
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
    async unarchivePipeline(pipeline) {
      this.confirm({
        title: "Unarchive " + pipeline.name,
        body: "Are you sure you want to unarchive " + pipeline.name + "?",
        options: {
          okLabel: "Unarchive pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.restorePipeline(pipeline.id);
          if (response.data.status === "Success") {
            await this.allPipelines();

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
    async pipelineDelete(pipeline) {
      this.confirm({
        title: "Delete " + pipeline.name,
        body: "Are you sure you want to delete " + pipeline.name + "?",
        options: {
          okLabel: "Delete pipeline",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deletePipeline(pipeline.id);
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
