<template>
  <div
    class="flex items-center justify-between my-2 mr-2"
    v-if="total > itemsPerPage"
  >
    <span class="text-gray-700 ml-2"
      >Showing {{ currentPage }} of
      {{ totalPages | pluralize("page", { includeNumber: true }) }}</span
    >
    <div>
      <button
        @click="goToPage(1)"
        :disabled="currentPage === 1"
        class="btn btn-sm btn-secondary rounded-none text-black mx-1"
      >
        First
      </button>
      <button
        @click="goToPage(currentPage - 1)"
        :disabled="currentPage === 1"
        class="btn btn-sm btn-secondary rounded-none text-black mx-1"
      >
        Previous
      </button>
      <button
        :disabled="currentPage === totalPages"
        @click="goToPage(currentPage + 1)"
        class="btn btn-sm btn-secondary rounded-none text-black mx-1"
      >
        Next
      </button>
      <button
        @click="goToPage(totalPages)"
        :disabled="currentPage === totalPages"
        class="btn btn-sm btn-secondary rounded-none text-black ml-1"
      >
        Last
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    pageChanged: {
      type: Function
    },
    total: {
      type: Number
    },
    itemsPerPage: {
      type: Number
    }
  },
  data() {
    return {
      currentPage: 1
    };
  },
  computed: {
    totalPages() {
      const quotient = Math.floor(this.total / this.itemsPerPage);
      const remainder = this.total % this.currentPerPage;
      return remainder === 0 ? quotient : quotient + 1;
    }
  },
  methods: {
    goToPage(page) {
      this.currentPage = page;
      this.pageChanged({ currentPage: page });
    }
  }
};
</script>
