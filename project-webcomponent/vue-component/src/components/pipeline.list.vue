<template>
  <div class="w-full">
    <collapsable-ui title="Data Pipelines" icon="code-branch">
      <div slot="right-actions" class="inline-flex">
        <a href="#" class="text-sm text-gray-500 underline">Learn More</a>
      </div>
      <div>
        <div class="flex flex-col">
          <span class="my-5">No Data Pipelines.</span>
          <div class="flex">
            <button
              class="btn btn-secondary btn-sm mr-2"
              @click="associatePipeline()"
            >
              Associate Data Pipeline
            </button>
            <button class="btn btn-primary btn-sm" @click="editPipeline()">
              Create Data Pipelines
            </button>
          </div>
        </div>
      </div>
    </collapsable-ui>
    <modal-ui
      :title="(activePipeline ? 'Edit' : 'Create') + ' Data Pipeline'"
      size="md"
      v-if="isEdittingPipeline"
      @onDismiss="isEdittingPipeline = false"
    >
      <edit-pipeline-form :data="activePipeline" />
    </modal-ui>
    <modal-ui
      title="Associate Data Pipeline"
      size="md"
      v-if="isAssociatingPipeline"
      @onDismiss="isAssociatingPipeline = false"
    >
      <associate-pipeline-form :data="activePipeline" />
    </modal-ui>
  </div>
</template>

<script>
import CollapsableUi from "../components/ui/collapsable.ui";
import ModalUi from "../components/ui/modal.ui";
import EditPipelineForm from "./forms/pipeline/edit-pipeline.form";
import AssociatePipelineForm from "./forms/pipeline/associate-pipeline.form";

export default {
  components: {
    CollapsableUi,
    ModalUi,
    EditPipelineForm,
    AssociatePipelineForm
  },
  data() {
    return {
      isEdittingPipeline: false,
      isAssociatingPipeline: false,
      activePipeline: null
    };
  },
  methods: {
    editPipeline(pipeline) {
      this.activePipeline = pipeline;
      this.isEdittingPipeline = true;
    },
    associatePipeline() {
      this.isAssociatingPipeline = true;
    }
  }
};
</script>
