<template>
  <div class="flex flex-col m-3">
    <div class="flex w-full justify-end">
      <button class="btn btn-primary" @click="editPipeline()" v-if="hasPipelines">Create Pipeline</button>
      <a :href="pipelineWikiURL" target="_blank" class="btn btn-secondary ml-2" title="Learn More">
        <FAIcon icon="question-circle"></FAIcon>
      </a>
    </div>
    <div class="flex w-full my-2" v-if="!hasPipelines">
      <CollapsableUi title="Pipelines" icon="code-branch" :collapse-border="true">
        <div class="p-5">
          <p class="py-4">No Pipelines, get started with ML Workbench by creating your first pipeline.</p>
          <button class="btn btn-primary" @click="editPipeline()">Create Pipeline</button>
        </div>
      </CollapsableUi>
    </div>
    <div class="flex justify-between my-2" v-if="hasPipelines">
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
          {{ filter | capitalize }} Data Pipelines
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
            placeholder="Search Pipelines"
            v-model="searchTerm"
          />
        </div>
      </div>
    </div>
    <div class="flex flex-wrap">
      <PipelineCard
        v-for="(pipeline, index) in filteredOrderedAndSorted"
        :key="index"
        :pipeline="pipeline"
        @on-open-pipeline="$emit('on-open-pipeline', $event)"
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
      :title="(activePipeline ? 'Edit' : 'Create') + ' Data Pipeline'"
      size="md"
      v-if="isEdittingPipeline"
      @onDismiss="isEdittingPipeline = false"
    >
      <EditPipelineForm :data="activePipeline" @onSuccess="isEdittingPipeline = false" />
    </ModalUi>
  </div>
</template>

<script>
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import EditPipelineForm from "./forms/project/edit-pipeline.form";
import PipelineCard from "./pipeline-card";

import { mapState } from "vuex";
import { filter } from "lodash-es";
import Vue2Filters from "vue2-filters";

export default {
  props: ["pipelines"],
  mixins: [Vue2Filters.mixin],
  components: {
    PaginationUi,
    CollapsableUi,
    ModalUi,
    EditPipelineForm,
    PipelineCard
  },
  computed: {
    hasPipelines() {
      return this.pipelines.length > 0;
    },
    filteredAndOrdered() {
      const filtered = this.filterBy(this.pipelines, this.searchTerm, "name");
      let filteredByExtraFilter = filtered;

      if (this.currentFilter !== "all") {
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
    ...mapState("app", ["pipelineWikiURL"]),
    filters() {
      return {
        active: filter(this.pipelines, { status: "ACTIVE" }).length,
        archived: filter(this.pipelines, { status: "ARCHIVED" }).length,
        all: this.pipelines.length
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
      isEdittingPipeline: false,
      activePipeline: null
    };
  },
  methods: {
    editPipeline(project) {
      this.activePipeline = project;
      this.isEdittingPipeline = true;
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
