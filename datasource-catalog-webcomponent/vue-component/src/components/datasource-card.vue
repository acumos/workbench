<template>
  <div
    class="flex flex-col p-4 border border-2 m-3 cursor-pointer hover:shadow-2xl hover:rounded-lg"
    style="width: 21rem;"
  > 
    <a @click="$emit('on-open-dataset', dataset)">
      <span class="text-purple-500 font-semibold text-xl py-2">
        {{ dataset.name | truncate(22) }}
      </span>
      <div class="flex py-1">
        <span class="font-bold mx-1">Connector:</span>
        <span>{{ dataset.category }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">JDBC URL:</span>
        <span>{{ dataset.jdbcUrl | truncate(22) }}</span>
      </div>
      <div class="flex py-1">
        <span class="font-bold mx-1">Permission:</span>
        <span>{{ dataset.readWriteDescriptor }}</span>
      </div>
    </a>
    <div class="flex bg-gray-200 py-3 mt-2 px-2 justify-between">
       <div>
        <button class="mx-1" :title="dataset.owner">
          <FAIcon class="text-2xl text-gray-600" icon="user-circle"></FAIcon>
        </button>
         <!-- <button
          class="mx-1"
          v-if="currentUser !== dataset.owner"
          :title="currentUser"
        >
          <FAIcon class="text-2xl text-gray-600" icon="user-circle"></FAIcon>
        </button>  -->
      </div>
      <!-- <div>
        <button
          class="mx-2"
          title="Unarchive Datasource"
          v-if="isArchived"
          disabled
        >
          <FAIcon class="text-2xl text-gray-600" icon="box-open"></FAIcon>
        </button>
        <a
          :href="dataset.jdbcUrl"
          target="_blank"
          class="mx-2"
          title="Launch Datasource"
          v-if="!isArchived && dataset.jdbcUrl"
          disabled
        >
          <FAIcon class="text-2xl text-gray-600" icon="external-link-alt"></FAIcon>
        </a>
        <button
          class="mx-2"
          title="Archive Datasource"
        >
          <FAIcon class="text-2xl text-gray-600" icon="box"></FAIcon>
        </button>
        <button
          class="mx-2"
          title="Delete Datasource"
          v-if="isArchived"
          @click="datasetDelete(dataset)"
        >
          <FAIcon class="text-2xl text-gray-600" icon="trash-alt"></FAIcon>
        </button>
      </div> -->
    </div>
  </div>
</template>

<script>
import dayjs from "dayjs";
import { mapActions, mapMutations, mapState } from "vuex";

export default {
  props: ["dataset"],
  computed: {
    ...mapState("app", {
      currentUser: state => state.userName
    }),
    isArchived() {
     // return this.project.status === "ARCHIVED";
    },
  },
  methods: {
    ...mapMutations("app", ["confirm"]),
    ...mapActions("datasource", [
      "archive",
      "unarchive",
      "deleteProject",
      "allDatasets"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    // async archiveProject(dataset) {
    //   this.confirm({
    //     title: "Archive " + dataset.name,
    //     body: "Are you sure you want to archive " + dataset.name + "?",
    //     options: {
    //       okLabel: "Archive Project",
    //       dismissLabel: "Cancel"
    //     },
    //     onOk: async () => {
    //       const response = await this.archive(dataset.id);
    //       if (response.data.status === "Success") {
    //         await this.allDatasets();
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "success"
    //         });
    //       } else {
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "error"
    //         });
    //       }
    //     }
    //   });
    // },
    // async unarchiveDataset(dataset) {
    //   this.confirm({
    //     title: "Unarchive " + dataset.name,
    //     body: "Are you sure you want to unarchive " + dataset.name + "?",
    //     options: {
    //       okLabel: "Unarchive Dataset",
    //       dismissLabel: "Cancel"
    //     },
    //     onOk: async () => {
    //       const response = await this.unarchive(dataset.uuid);
    //       if (response.data.status === "Success") {
    //         await this.allDatasets();
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "success"
    //         });
    //       } else {
    //         this.showToastMessage({
    //           id: "global",
    //           message: `${response.data.message}`,
    //           type: "error"
    //         });
    //       }
    //     }
    //   });
    // },
    async datasetDelete(dataset) {
      this.confirm({
        title: "Delete " + dataset.name,
        body: "Are you sure you want to delete " + dataset.name + "?",
        options: {
          okLabel: "Delete Datasource",
          dismissLabel: "Cancel"
        },
        onOk: async () => {
          const response = await this.deleteDataset(dataset.datasetId);
          if (response.data.status === "Success") {
            await this.allDatasets();
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
    }
  }
};
</script>
