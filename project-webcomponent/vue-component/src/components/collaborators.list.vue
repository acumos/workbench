<template>
  <div class="w-full">
    <div class="flex flex-col w-full p-3">
      <div v-if="statusMessage" class="p-2 rounded-lg text-white" :class="{'bg-green-500': statusMessage.data.status === 'Success','bg-red-500': statusMessage.data.status === 'Error' }">{{statusMessage.data.message}}</div>
      <h3 class="text-lg font-semibold my-2">Add Collaborator</h3>
      <div class="flex items-center">
        <v-select class="flex-grow mr-2" label="username" placeholder="User Login Id" :options="users" @search="setUsers" @input="selectUser">
          <template slot="no-options">
            type to search users..
          </template>
          <template slot="option" slot-scope="option">
            <div class="d-center">
              <img :src='option.picture'/> 
              {{ option.username }}
              </div>
          </template>
          <template slot="selected-option" slot-scope="option">
            <div class="selected d-center">
              <img :src='option.picture'/> 
              {{ option.username }}
            </div>
          </template>
        </v-select>

        <select class="flex-grow form-select" v-model="newCollaborator.permission">
          <option value="">Select a permission</option>
          <option value="ADMIN">ADMIN</option>
          <option value="READ">READ</option>
          <option value="WRITE">WRITE</option>
        </select>
        <button class="btn btn-primary btn-sm ml-2 my-1" @click="addNewCollaborator(newCollaborator)">
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
            <button class="btn btn-primary btn-xs bg-red-600" @click="deleteCollaborator(collaborator)">
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
import { mapActions, mapState } from "vuex";
import { differenceWith, filter } from "lodash-es";

export default {
  props: ["initialCollaborators"],
  data() {
    return {
      newCollaborator: new Collaborator(),
      collaboratorList: this.initialCollaborators,
      users: [],
      loading: false,
      statusMessage: null
    };
  },

  watch: {
    initialCollaborators: function(){
      this.collaboratorList = this.initialCollaborators;
    }
  },

  computed:{
    ...mapState("project", {
      projectId: state => state.activeProject,
    }),
    ...mapState("app", {
      userName: state => state.userName
    }),
  },
  methods: {
    ...mapActions("project", [
      "shareProjectToUsers",
      "deleteSharedUserFromProject",
      "getDetails",
      "getUsersList",
      ]),

      async init() {
        this.users = [];
      },

      async setUsers(search, loading){
        loading(true);
        if(this.users.length > 0){
          loading(false);
          return;
        }
        let users = await this.getUsersList();
        users = differenceWith(users.data.data, this.initialCollaborators, (user, collaborator) =>
          user.username === collaborator.name
        );
        loading(false);
        this.users = filter(users, (user) => user.username !== this.userName);
        
      },

      selectUser(user){
        this.newCollaborator.id = user.userId;
        this.newCollaborator.name = user.username;
      },

      async addNewCollaborator(newCollaborator){
        this.statusMessage = await this.shareProjectToUsers(newCollaborator.$toJson());
        if(this.statusMessage.data.status === "Success"){
          await this.getDetails(this.projectId);
          // const updatedCollaborator = await this.getDetails();
          // this.collaboratorList = updatedCollaborator.data.data.collaborators.users.map(user => Collaborator.$fromJson(user));

        }
        
      },

      async deleteCollaborator(collaborator){  
        collaborator = new Collaborator(collaborator);      
        this.statusMessage = await this.deleteSharedUserFromProject(collaborator.$toJson());
        if(this.statusMessage.data.status === "Success"){
          //this.collaboratorList = this.statusMessage.data.data.collaborators.users.map(user => Collaborator.$fromJson(user));
        await this.getDetails(this.projectId);
        }
      },

      closeCollaborator(){
        this.$emit('onClose');
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
