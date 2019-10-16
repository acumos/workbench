export default {
  componentUrl: null,
  wikiConfig: {
    notebookWikiURL: null,
    projectWikiURL: null,
    pipelineWikiURL: null
  },
  msConfig: null,
  userName: null,
  authToken: null,
  portalBEUrl: null,
  portalFEUrl: null,
  pipelineFlag: null,
  createTimeout: null,
  useExternalNotebook: null,
  useExternalPipeline: null,
  toast: {
    id: "",
    enabled: false,
    type: "",
    message: ""
  },
  confirm: {
    enabled: false,
    title: "",
    body: "",
    onOk: null,
    okDismiss: null,
    options: {}
  }
};
