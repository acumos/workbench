<template>
  <div class="w-full">
    <ToastUI
      id="collaborator-list"
      class="relative p-2"
      innerClass="w-full"
    ></ToastUI>
    <div class="flex flex-col w-full p-3">
      <h3 class="text-lg font-semibold my-2">Add Collaborator</h3>
      <div class="flex items-center">
        <v-select
          class="flex-grow mr-2"
          label="username"
          placeholder="User Login Id"
          :options="users"
          @search="setUsers"
          @input="selectUser"
        >
          <template slot="no-options">
            type to search users..
          </template>
          <template slot="option" slot-scope="option">
            <div class="d-center">
              <img :src="option.picture" />
              {{ option.username }}
            </div>
          </template>
          <template slot="selected-option" slot-scope="option">
            <div class="selected d-center">
              <img :src="option.picture" />
              {{ option.username }}
            </div>
          </template>
        </v-select>

        <select
          class="flex-grow form-select"
          v-model="newCollaborator.permission"
        >
          <option value="">Select a permission</option>
          <option value="READ">READ</option>
          <option value="WRITE" disabled>WRITE</option>
          <option value="ADMIN" disabled>ADMIN</option>
        </select>
        <button
          class="btn btn-primary btn-sm ml-2 my-1"
          @click="addNewCollaborator(newCollaborator)"
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
          <td class="text-left text-sm pr-2">{{ collaborator.name }}</td>
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
    </div>

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
import ToastUI from "../components/ui/Toast.ui";

import { mapActions, mapState } from "vuex";
import { differenceWith, filter } from "lodash-es";

export default {
  components: { ToastUI },
  props: ["initialCollaborators"],
  data() {
    return {
      newCollaborator: new Collaborator(),
      collaboratorList: this.initialCollaborators,
      users: [],
      loading: false
    };
  },

  watch: {
    initialCollaborators: function() {
      this.collaboratorList = this.initialCollaborators;
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

    async setUsers(search, loading) {
      loading(true);
      if (this.users.length > 0) {
        loading(false);
        return;
      }
      let users = await this.getUsersList();
      users = differenceWith(
        users.data.data,
        this.initialCollaborators,
        (user, collaborator) => user.username === collaborator.name
      );
      loading(false);
      this.users = filter(users, user => user.username !== this.userName);
    },

    selectUser(user) {
      this.newCollaborator.id = user.userId;
      this.newCollaborator.name = user.username;
      this.newCollaborator.permission = "";
    },

    async addNewCollaborator(newCollaborator) {
      const response = await this.shareProjectToUsers(
        newCollaborator.$toJson()
      );
      if (response.data.status === "Success") {
        await this.getDetails();
        this.showToastMessage({
          id: "collaborator-list",
          message: `${response.data.message}`,
          type: "success"
        });
      } else{
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
      } else{
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