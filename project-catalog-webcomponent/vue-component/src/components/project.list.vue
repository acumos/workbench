<template>
  <div class="flex flex-col m-3">
    <div class="flex w-full justify-end">
      <button class="btn btn-primary" @click="editProject()" v-if="hasProjects">Create Project</button>
      <a :href="projectWikiURL" target="_blank" class="btn btn-secondary ml-2" v-tooltip="'Learn More'">
        <FAIcon icon="question-circle"></FAIcon>
      </a>
    </div>
    <div class="flex w-full my-2" v-if="!hasProjects">
      <CollapsableUi title="Projects" icon="project-diagram" :collapse-border="true">
        <div class="p-5">
          <p class="py-4">No Projects, get started with ML Workbench by creating your first project.</p>
          <button class="btn btn-primary" @click="editProject()">Create Project</button>
        </div>
      </CollapsableUi>
    </div>
    <div class="flex justify-between my-2" v-if="hasProjects">
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
      <EditProjectForm :data="activeProject" @onSuccess="isEdittingProject = false" />
    </ModalUi>
  </div>
</template>

<script>
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
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
    CollapsableUi,
    ModalUi,
    EditProjectForm,
    ProjectCard
  },
  computed: {
    hasProjects() {
      return this.projects.length > 0;
    },
    filteredAndOrdered() {
      let filtered = this.filterBy(this.projects, this.searchTerm, "name");
      let sharedProjectsFiltered = this.filterBy(
        this.sharedProjects,
        this.searchTerm,
        "name"
      );

      let filteredByExtraFilter = [];
      if (this.currentFilter === "all") {
        filteredByExtraFilter = [...filtered, ...sharedProjectsFiltered];
      } else if (this.currentFilter === "shared") {
        filteredByExtraFilter = sharedProjectsFiltered;
      } else {
        filteredByExtraFilter = this.filterBy(
          filtered,
          this.currentFilter.toUpperCase(),
          "status"
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
    ...mapState("project", {
      loginAsOwner: state => state.loginAsOwner
    }),
    ...mapState("app", ["projectWikiURL"]),
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
