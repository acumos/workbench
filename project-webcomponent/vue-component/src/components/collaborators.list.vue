<template>
  <div class="w-full">
    <div class="flex flex-col w-full p-3">
      <h3 class="text-lg font-semibold my-2">Add Colaborator</h3>
      <div class="flex items-center">
        <select class="flex-grow form-select mr-2">
          <option>Select a user</option>
        </select>
        <select class="flex-grow form-select">
          <option>Select a permission</option>
        </select>
        <button class="btn btn-primary btn-sm ml-2 my-1">
          <FAIcon icon="plus" />
        </button>
      </div>
      <div class="inline-flex justify-end py-2 my-2 border-b"></div>
      <h3 class="text-lg font-semibold my-2">Active Colaborators</h3>
      <table>
        <tr>
          <th class="w-1/6 text-left">User</th>
          <th class="w-full text-left">Permission</th>
          <th class="w-1"></th>
        </tr>
        <tr v-for="(collaborator, index) in collaborators" :key="index">
          <td class="text-left text-sm pr-2">{{ collaborator.name }}</td>
          <td class="text-left pr-2">
            <select class="form-select py-1 text-sm w-full">
              <option>Admin</option>
            </select>
          </td>
          <td class="text-center">
            <button class="btn btn-primary btn-xs bg-red-600">
              <FAIcon icon="minus" />
            </button>
          </td>
        </tr>
      </table>
    </div>

    <div
      class="flex justify-end py-3 px-2 bg-gray-100 border-gray-200 border-t"
    >
      <button class="btn btn-sm btn-secondary" @click="$emit('onDismiss')">
        Close
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: ["initialCollaborators"],
  data() {
    return {
      collaborators: [
        {
          name: "John Doe"
        },
        {
          name: "Jane Doe"
        },
        {
          name: "Bill Doe"
        }
      ]
    };
  }
};
</script>
