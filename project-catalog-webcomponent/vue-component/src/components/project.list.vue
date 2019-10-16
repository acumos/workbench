<template>
  <div class="flex flex-col m-3">
    <div class="flex w-full justify-end">
      <button class="btn btn-primary" @click="editProject()">
        Create Project
      </button>
      <a :href="projectWikiURL" target="_blank" class="btn btn-secondary ml-2"
        ><FAIcon icon="question-circle"></FAIcon>
      </a>
    </div>
    <div class="flex justify-between my-2">
      <div class="flex">
        <div
          @click="setFilter(filter)"
          v-for="(count, filter) in filters"
          :key="filter"
          class="cursor-pointer border-2 border-gray-400 w-48 px-2 py-2 rounded ml-2"
          :class="{
            'bg-purple-500 text-white': filter === currentFilter,
            'bg-white text-black': filter !== currentFilter
          }"
        >
          {{ filter | capitalize }} Projects
          <span
            class="text-sm px-1 rounded"
            :class="{
              'bg-white text-black': filter === currentFilter,
              'bg-gray-600 text-white': filter !== currentFilter
            }"
          >
            {{ count }}
          </span>
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
            placeholder="Search Projects"
            v-model="searchTerm"
          />
        </div>
      </div>
    </div>
    <div class="flex flex-wrap">
      <ProjectCard
        v-for="(project, index) in filteredOrderedAndSorted"
        :key="index"
        :project="project"
        @on-open-project="$emit('on-open-project', $event)"
      ></ProjectCard>
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
      :title="(activeProject ? 'Edit' : 'Create') + ' Project'"
      size="md"
      v-if="isEdittingProject"
      @onDismiss="isEdittingProject = false"
    >
      <EditProjectForm
        :data="activeProject"
        @onSuccess="isEdittingProject = false"
      />
    </ModalUi>
  </div>
</template>

<script>
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import EditProjectForm from "./forms/project/edit-project.form";
import ProjectCard from "./project-card";

import { mapState } from "vuex";
import { filter } from "lodash-es";
import Vue2Filters from "vue2-filters";

export default {
  props: ["projects", "sharedProjects"],
  mixins: [Vue2Filters.mixin],
  components: {
    PaginationUi,
    ModalUi,
    EditProjectForm,
    ProjectCard
  },
  computed: {
    filteredAndOrdered() {
      const filtered = this.filterBy(this.projects, this.searchTerm, "name");
      const filteredByExtraFilter = this.filterBy(
        filtered,
        this.currentFilter.toUpperCase(),
        "status"
      );

      if (this.currentFilter === "all") {
        return [...filtered, ...this.sharedProjects];
      } else if (this.currentFilter === "shared") {
        return this.sharedProjects;
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
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    ...mapState("app", [
      "projectWikiURL"]),
    filters() {
      return {
        active: filter(this.projects, { status: "ACTIVE" }).length,
        archived: filter(this.projects, { status: "ARCHIVED" }).length,
        all: this.projects.length + this.sharedProjects.length,
        shared: this.sharedProjects.length
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
      currentFilter: "active",
      offset: 0,
      itemsPerPage: 8,
      searchTerm: "",
      sortBy: "createdAt",
      isEdittingProject: false,
      activeProject: null
    };
  },
  methods: {
    editProject(project) {
      this.activeProject = project;
      this.isEdittingProject = true;
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
