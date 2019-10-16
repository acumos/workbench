<template>
  <div class="w-full">
    <ToastUI
      id="collaborator-list"
      class="relative p-2"
      innerClass="w-full"
    ></ToastUI>
    <ValidationObserver ref="form" tag="div" class="flex flex-col w-full p-3">
      <h3 class="text-lg font-semibold my-2">Add Collaborator</h3>
      <div class="flex items-start">
        <ValidationProvider
          class="w-1/2"
          name="User Id"
          rules="required"
          v-slot="{ errors, classes }"
        >
          <v-select
            class="flex-grow mr-2"
            label="username"
            placeholder="User Login Id"
            :options="users"
            v-model="newCollaborator"
            :filterBy="filterBy"
            :disabled="loadingUsers"
          >
            <template slot="spinner">
              <div class="mx-2 text-xl" v-show="loadingUsers">
                <FAIcon icon="spinner" pulse />
              </div>
            </template>

            <template slot="no-options">
              type to search users..
            </template>
            <template slot="option" slot-scope="option">
              <div class="d-center">
                <img :src="option.picture" />
                {{ option.firstName }} {{ option.lastName }}
              </div>
            </template>
            <template slot="selected-option" slot-scope="option">
              <div class="selected d-center">
                <img :src="option.picture" />
                {{ option.firstName }} {{ option.lastName }}
              </div>
            </template>
          </v-select>
          <span class="text-sm text-red-700 flex items-center" v-if="errors[0]">
            <FAIcon icon="exclamation-triangle" />
            <span class="ml-1 my-1">{{ errors[0] }}</span>
          </span>
        </ValidationProvider>
        <select class="w-1/2 form-select" v-model="newPermission">
          <option value>Select a permission</option>
          <option value="READ">READ</option>
          <option value="WRITE" disabled>WRITE</option>
          <option value="ADMIN" disabled>ADMIN</option>
        </select>
        <button
          class="btn btn-primary btn-sm ml-2 my-1"
          @click="addNewCollaborator(newCollaborator, newPermission)"
        >
          <FAIcon icon="plus" />
        </button>
      </div>
      <div class="inline-flex justify-end py-2 my-2 border-b"></div>
      <h3 class="text-lg font-semibold my-2">Active Collaborators</h3>
      <table>
        <tr>
          <th class="w-1/6 text-left">User</th>
          <th class="w-full text-left">Permission</th>
          <th class="w-1"></th>
        </tr>
        <tr v-for="(collaborator, index) in collaboratorList" :key="index">
          <td class="text-left text-sm pr-2">{{ collaborator.firstName }} {{ collaborator.lastName }}</td>
          <td class="text-left text-sm pr-2">
            {{ collaborator.permission }}
          </td>
          <td class="text-center">
            <button
              class="btn btn-primary btn-xs bg-red-600"
              @click="deleteCollaborator(collaborator)"
            >
              <FAIcon icon="minus" />
            </button>
          </td>
        </tr>
      </table>
    </ValidationObserver>

    <div
      class="flex justify-end py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="closeCollaborator()">
        Close
      </button>
    </div>
  </div>
</template>

<script>
import Collaborator from "../store/entities/collaborator.entity";
import ToastUI from "../vue-common/components/ui/Toast.ui";

import { mapActions, mapState } from "vuex";
import { differenceWith, filter, sortBy } from "lodash-es";

export default {
  components: { ToastUI },
  props: ["initialCollaborators"],
  data() {
    return {
      newCollaborator: null,
      newPermission: "READ",
      collaboratorList: this.initialCollaborators,
      users: [],
      loadingUsers: false,
      filterString: "",
      allUsers: []
    };
  },

  watch: {
    initialCollaborators: function() {
      this.collaboratorList = this.initialCollaborators;
      this.users = differenceWith(
        this.allUsers.data.data,
        this.initialCollaborators,
        (user, collaborator) => user.username === collaborator.name
      );
    }
  },

  computed: {
    ...mapState("project", {
      projectId: state => state.activeProject
    }),
    ...mapState("app", {
      userName: state => state.userName
    })
  },
  async created() {
    this.loadingUsers = true;
    this.allUsers = await this.getUsersList();
    this.users = differenceWith(
      this.allUsers.data.data,
      this.initialCollaborators,
      (user, collaborator) => user.username === collaborator.name
    );
    this.users = filter(this.users, user => user.username !== this.userName);
    this.users = sortBy(this.users, ["firstName"]);
    this.loadingUsers = false;
  },
  methods: {
    ...mapActions("project", [
      "shareProjectToUsers",
      "deleteSharedUserFromProject",
      "getDetails",
      "getUsersList"
    ]),
    ...mapActions("app", ["showToastMessage"]),
    async init() {
      this.users = [];
    },

    filterBy(option, label, search) {
      return (
        option.firstName.toLowerCase().indexOf(search) > -1 ||
        option.lastName.toLowerCase().indexOf(search) > -1
      );
    },

    async addNewCollaborator(newCollaborator, newPermission) {
      const isValid = await this.$refs.form.validate();

      if (!isValid) {
        return;
      }
      const collaborator = new Collaborator();

      collaborator.id = newCollaborator.userId;
      collaborator.name = newCollaborator.username;
      collaborator.permission = newPermission;

      const response = await this.shareProjectToUsers(collaborator.$toJson());
      if (response.data.status === "Success") {
        await this.getDetails();
        this.showToastMessage({
          id: "collaborator-list",
          message: `${response.data.message}`,
          type: "success"
        });
      } else {
        this.showToastMessage({
          id: "collaborator-list",
          message: `${response.data.message}`,
          type: "error"
        });
      }
    },

    async deleteCollaborator(collaborator) {
      collaborator = new Collaborator(collaborator);
      const response = await this.deleteSharedUserFromProject(
        collaborator.$toJson()
      );
      if (response.data.status === "Success") {
        await this.getDetails();
        this.showToastMessage({
          id: "collaborator-list",
          message: `${response.data.message}`,
          type: "success"
        });
      } else {
        this.showToastMessage({
          id: "collaborator-list",
          message: `${response.data.message}`,
          type: "error"
        });
      }
    },

    closeCollaborator() {
      this.$emit("onClose");
    }
  }
};
</script>
<style scoped>
img {
  height: auto;
  max-width: 2.5rem;
  margin-right: 1rem;
}
</style>
