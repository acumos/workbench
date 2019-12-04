<template>
  <div class="flex flex-col m-3">
    <div class="flex w-full justify-end">
      <button class="btn btn-primary" @click="editNotebook()" v-if="hasNotebooks">Create Notebook</button>
      <a :href="notebookWikiURL" target="_blank" class="btn btn-secondary ml-2" title="Learn More">
        <FAIcon icon="question-circle"></FAIcon>
      </a>
    </div>
    <div class="flex w-full my-2" v-if="!hasNotebooks">
      <CollapsableUi title="Notebooks" icon="book-open" :collapse-border="true">
        <div class="p-5">
          <p class="py-4">No Notebooks, get started with ML Workbench by creating your first notebook.</p>
          <button class="btn btn-primary" @click="editNotebook()">Create Notebook</button>
        </div>
      </CollapsableUi>
    </div>
    <div class="flex justify-between my-2" v-if="hasNotebooks">
      <div class="flex">
        <div
          @click="setFilter(filter)"
          v-for="(count, filter) in filters"
          :key="filter"
          class="cursor-pointer border-2 border-gray-400 w-56 px-2 py-2 rounded ml-2"
          :class="{
            'bg-purple-500 text-white': filter === currentFilter,
            'bg-white text-black': filter !== currentFilter
          }"
        >
          {{ filter | capitalize }} Notebooks
          <span
            class="text-sm px-1 rounded"
            :class="{
              'bg-white text-black': filter === currentFilter,
              'bg-gray-600 text-white': filter !== currentFilter
            }"
          >{{ count }}</span>
        </div>
      </div>
      <div class="flex">
        <div class="flex inline-flex items-center">
          <select class="form-select mr-2 py-1" v-model="sortBy">
            <option value>Sort By</option>
            <option value="createdAt">Created</option>
            <option value="name">Name</option>
          </select>
          <input
            type="text"
            class="form-input py-1"
            placeholder="Search Notebooks"
            v-model="searchTerm"
          />
        </div>
      </div>
    </div>
    <div class="flex flex-wrap">
      <NotebookCard
        v-for="(notebook, index) in filteredOrderedAndSorted"
        :key="index"
        :notebook="notebook"
        @on-open-notebook="$emit('on-open-notebook', $event)"
      />
    </div>
    <div>
      <pagination-ui
        ref="pagination"
        v-if="!searchTerm"
        :total="filteredAndOrdered.length"
        :pageChanged="pageChanged"
        :itemsPerPage="itemsPerPage"
      />
    </div>
    <ModalUi
      :title="(activeNotebook ? 'Edit' : 'Create') + ' Notebook'"
      size="md"
      v-if="isEdittingNotebook"
      @onDismiss="isEdittingNotebook = false"
    >
      <EditNotebookForm :data="activeNotebook" @onSuccess="isEdittingNotebook = false" />
    </ModalUi>
  </div>
</template>

<script>
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import EditNotebookForm from "./forms/notebook/edit-notebook.form";
import NotebookCard from "./notebook-card";

import { mapState } from "vuex";
import { filter } from "lodash-es";
import Vue2Filters from "vue2-filters";

export default {
  props: ["notebooks"],
  mixins: [Vue2Filters.mixin],
  components: {
    PaginationUi,
    CollapsableUi,
    ModalUi,
    EditNotebookForm,
    NotebookCard
  },
  computed: {
    hasNotebooks() {
      return this.notebooks.length > 0;
    },
    filteredAndOrdered() {
      const filtered = this.filterBy(this.notebooks, this.searchTerm, "name");
      let filteredByExtraFilter = filtered;

      if (!this.searchTerm) {
        filteredByExtraFilter = this.filterBy(
          filtered,
          this.currentFilter.toUpperCase(),
          "type"
        );
      }

      return this.orderBy(filteredByExtraFilter, this.sortBy, -1);
    },
    filteredOrderedAndSorted() {
      if (this.searchTerm) {
        return this.filteredAndOrdered;
      }

      return this.limitBy(
        this.filteredAndOrdered,
        this.itemsPerPage,
        this.offset
      );
    },
    ...mapState("app", ["notebookWikiURL"]),
    filters() {
      return {
        jupyter: filter(this.notebooks, { type: "JUPYTER" }).length
      };
    },
    sortOptions() {
      if (this.sortBy === "") {
        return {};
      }

      return {
        enabled: true,
        initialSortBy: { field: this.sortBy, type: "desc" }
      };
    }
  },
  data() {
    return {
      currentFilter: "jupyter",
      offset: 0,
      itemsPerPage: 8,
      searchTerm: "",
      sortBy: "createdAt",
      isEdittingNotebook: false,
      activeNotebook: null
    };
  },
  methods: {
    editNotebook(project) {
      this.activeNotebook = project;
      this.isEdittingNotebook = true;
    },
    pageChanged(page) {
      this.offset = this.itemsPerPage * page.currentPage;
    },
    setFilter(filter) {
      this.currentFilter = filter;
      this.offset = 0;
      this.$refs.pagination.goToPage(1);
    }
  }
};
</script>
