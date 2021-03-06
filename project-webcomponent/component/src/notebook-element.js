/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
===================================================================================
This Acumos software file is distributed by AT&T and Tech Mahindra
under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
This file is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
===============LICENSE_END=========================================================
*/

import { LitElement, html } from 'lit-element';
import { style } from './notebook-styles.js';
import { filter, differenceWith } from "lodash-es";
import { OmniModal, OmniDialog } from "./@workbenchcommon/components";
import { Forms, DataSource } from "./@workbenchcommon/core";
import { ValidationMixin, DataMixin, BaseElementMixin } from "./@workbenchcommon/mixins";

class ProjectNotebookLitElement extends DataMixin(ValidationMixin(BaseElementMixin(LitElement))) {
	get dependencies() {
		return [OmniModal, OmniDialog];
	}

	static get properties() {
		return {
			message: { type: String, notify: true },
			notebooksList: { type: Array, notify: true },
			notebooks: [],
			activeFilter: {},
			activeSort: "",
			currentPage: 0,
			totalPages: 0,
			notebookmSURL: { type: String },
			zeppelinNotebookCount: { type: Number },
			jupyterNotebookCount: { type: Number },
			allNotebookCount: { type: Number },
			componenturl: { type: String, notify: true },
			isOpenArchiveDialog: { type: Boolean },
			isOpenDeleteDialog: { type: Boolean },
			isOpenRestoreDialog: { type: Boolean },
			successMessage: { type: String },
			errorMessage: { type: String },
			alertOpen: { type: Boolean },
			projectId: { type: String, notify: true },
			isEdit: { type: Boolean, notify: true },
			view: { type: String, notify: true },
			isOpenModal: { type: Boolean },
			isOpenModalLink: { type: Boolean },
			isOpenModalEdit: { type: Boolean },
			zeppelinNotebooks: [],
			jupyterNotebooks: [],
			unassociatedNotebooks: [],
			notebookWikiURL: { type: String },
			cardShow: { type: Boolean },
			userName: { type: String, notify: true },
			authToken: { type: String, notify: true },
			useExternalNotebook: { type: String }
		};
	}

	static get styles() {
		return [style];
	}

	constructor() {
		super();
		this.view = '';
		this.notebooks = [];
		this.zeppelinNotebooks = [];
		this.jupyterNotebooks = [];
		this.unassociatedNotebooks = [];
		this.initializeCreateNotebookForm();
		this.initializeValidations();
		this.sortOptions = [
			{ value: "created", label: "Sort By Created Date" },
			{ value: "name", label: "Sort By Name" },
			{ value: "id", label: "Sort By ID" }
		];
		this.modalDismissed();
		this.requestUpdate().then(() => {
			this.componenturl = (this.componenturl === undefined || this.componenturl === null) ? '' : this.componenturl;
			this.getConfig();
		})
	}

	initializeValidations() {
		this.$validations.init({
			validations: {
				newNotebook: {
					noteBookId: {
						name: {
							isNotEmpty: Forms.validators.isNotEmpty,
							pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_]{5,29}$')
						},
						versionId: {
							label: {
								isNotEmpty: Forms.validators.isNotEmpty,
								pattern: Forms.validators.pattern('^[a-zA-Z0-9_.]{1,14}$')
							}
						},
						serviceUrl: this.useExternalNotebook === 'true' ? {
							isNotEmpty: Forms.validators.isNotEmpty,
							pattern: Forms.validators.pattern('https://.*')
						} : {}
					}
				},
				linkNotebook: {
					noteBookId: {
						name: {
							isNotEmpty: Forms.validators.isNotEmpty,
						}
					}
				},
				editNotebook: {
					noteBookId: {
						name: {
							isNotEmpty: Forms.validators.isNotEmpty,
							pattern: Forms.validators.pattern('^[a-zA-Z][a-zA-Z0-9_]{5,29}$')
						},
						versionId: {
							label: {
								isNotEmpty: Forms.validators.isNotEmpty,
								pattern: Forms.validators.pattern('^[a-zA-Z0-9_.]{1,14}$')
							}
						},
						serviceUrl: this.useExternalNotebook === 'true' ? {
							isNotEmpty: Forms.validators.isNotEmpty,
							pattern: Forms.validators.pattern('https://.*')
						} : {}
					}
				}
			}
		});
	}

	initializeCreateNotebookForm() {
		this.data = {
			createErrorMessage: "",
			associateErrorMessage: "",
			editErrorMessage: "",
			newNotebook: {
				noteBookId: {
					name: "",
					versionId: {
						comment: "",
						label: ""
					},
					serviceUrl: ""
				},
				notebookType: "JUPYTER",
				description: ""
			},
			linkNotebook: {
				noteBookId: {
					name: "",
					versionId: {
						comment: "",
						label: ""
					}
				},
				notebookType: "",
			},
			editNotebook: {
				noteBookId: {
					name: "",
					uuid: "",
					versionId: {
						comment: "",
						label: ""
					},
					serviceUrl: ""
				},
				description: ""
			}
		};

		this.$data.snapshot('newNotebook');
		this.$data.snapshot('linkNotebook');
		this.$data.snapshot('editNotebook');

		this.$data.set('createErrorMessage', '');
		this.$data.set('associateErrorMessage', '');
		this.$data.set('editErrorMessage', '');
		this.$data.set('newNotebook.noteBookId.name', '');
		this.$data.set('newNotebook.noteBookId.versionId.label', '');
		this.$data.set('newNotebook.noteBookId.serviceUrl', '');
		this.$data.set('newNotebook.description', '');
		this.$data.set('newNotebook.notebookType', 'JUPYTER');
		this.$data.set('linkNotebook.noteBookId.name', '');
		this.$data.set('linkNotebook.noteBookId.versionId.label', '');
		this.$data.set('linkNotebook.description', '');
		this.$data.set('linkNotebook.notebookType', '');
		this.$data.set('editNotebook.noteBookId.name', '');
		this.$data.set('editNotebook.noteBookId.uuid', '');
		this.$data.set('editNotebook.noteBookId.versionId.label', '');
		this.$data.set('editNotebook.noteBookId.serviceUrl', '');
		this.$data.set('editNotebook.description', '');

	}

	connectedCallback() {
		super.connectedCallback();
		window.addEventListener('hashchange', this._boundListener);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		window.removeEventListener('hashchange', this._boundListener);
	}

	getConfig() {
		const url = this.componenturl + '/api/config';
		this.resetMessage();
		fetch(url, {
			method: 'GET',
			mode: 'cors',
			cache: 'default'
		}).then(res => res.json())
			.then((envVar) => {
				this.notebookmSURL = envVar.msconfig.notebookmSURL;
				this.notebookWikiURL = envVar.wikiConfig.notebookWikiURL;
				this.useExternalNotebook = envVar.useExternalNotebook;
				let username = envVar.userName;
				let token = envVar.authToken;
				this.initializeValidations();
				if (this.userName && this.userName !== '' && this.authToken && this.authToken !== '') {
					this.getNotebookDetailsForProject(true);
				} else if (username && username !== '' && token && token !== '') {
					this.authToken = token;
					this.userName = username;
					this.getNotebookDetailsForProject(true);
				} else {
					this.errorMessage = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
					this.alertOpen = true;
					this.view = 'error';
				}
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Unable to retrive notebook configuration information. Error is: ' + error;
				this.view = 'error';
				this.alertOpen = true;
			});
	}

	resetMessage() {
		this.successMessage = '';
		this.errorMessage = '';
	}

	getNotebookDetailsForProject(reset) {
		const url = this.componenturl + '/api/project/notebooksList';
		if (reset) {
			this.resetMessage();
		}

		fetch(url, {
			method: 'POST',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"projectId": this.projectId,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((response) => {
				if (response.status === 'Error') {
					this.errorMessage = response.message;
					this.alertOpen = true;
					this.view = 'error';
				} else {
					this.notebooksList = [];
					this.notebooks = [];
					this.cardShow = true;
					this.convertNotebookObject(response.data);
				}
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Notebooks fetch request for project failed with error: ' + error;
				this.view = 'error';
				this.alertOpen = true;
			});
	}

	convertNotebookObject(notebooksInfo) {
		let tempNotebook;
		notebooksInfo.forEach(item => {
			tempNotebook = {};
			tempNotebook.noteBookId = item.noteBookId.uuid;
			tempNotebook.name = item.noteBookId.name;
			tempNotebook.version = item.noteBookId.versionId.label;
			tempNotebook.createdTimestamp = item.noteBookId.versionId.timeStamp;
			tempNotebook.createdBy = item.owner.authenticatedUserId;
			tempNotebook.description = item.description;
			tempNotebook.status = item.artifactStatus.status;
			tempNotebook.notebookType = item.notebookType;
			tempNotebook.serviceUrl = item.noteBookId.serviceUrl;
			this.notebooksList.push(tempNotebook);
		});
		this.displayNotebooks();
	}

	createNotebook() {
		const url = this.componenturl + '/api/project/createNotebook';
		this.resetMessage();
		fetch(url, {
			method: 'POST',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"projectId": this.projectId,
				"newNotebookDetails": this.data.newNotebook,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					this.initializeCreateNotebookForm();
					this.successMessage = n.message;
					this.alertOpen = true;
					this.getNotebookDetailsForProject();

					this.$data.revert('newNotebook');
					this.$validations.resetValidation('newNotebook');
					this.isOpenModal = false;
				} else {
					this.$data.set('createErrorMessage', n.message);
				}
			}).catch((error) => {
				console.error('Request failed', error);
				this.$data.set('createErrorMessage', 'Notebook create request failed with error: ' + error);
			});
	}

	archiveNotebook() {
		const url = this.componenturl + '/api/project/archiveNotebook';
		this.resetMessage();
		fetch(url, {
			method: 'PUT',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"projectId": this.projectId,
				"notebookId": this.selectedNotebookId,

				"userName": this.userName
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					this.successMessage = n.message;
					this.getNotebookDetailsForProject();
				} else {
					this.errorMessage = n.message;
				}
				this.alertOpen = true;
				this.isOpenArchiveDialog = false;
			}).catch((error) => {
				console.error('Request failed', error);
				this.errorMessage = 'Notebook archive request failed with error: ' + error;
				this.alertOpen = true;
			});
	}

	associateNotebook() {
		const url = this.componenturl + '/api/project/associateNotebook';
		this.resetMessage();
		fetch(url, {
			method: 'PUT',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"userName": this.userName,
				"url": this.notebookmSURL,
				"projectId": this.projectId,
				"notebookId": this.selectedNotebookId,
				"notebookPayload": this.data.linkNotebook
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					this.successMessage = n.message;
					this.alertOpen = true;
					this.initializeCreateNotebookForm();
					this.getNotebookDetailsForProject();

					this.$data.revert('linkNotebook');
					this.$validations.resetValidation('linkNotebook');
					this.isOpenModalLink = false;
				} else {
					this.$data.set('associateErrorMessage', n.message);
				}
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessge = 'Update Notebook request failed with error: ' + error;
				this.alertOpen = true;
			});
	}

	restoreNotebook() {
		const url = this.componenturl + '/api/project/restoreNotebook';
		this.resetMessage();
		fetch(url, {
			method: 'PUT',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"projectId": this.projectId,
				"notebookId": this.selectedNotebookId,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					this.successMessage = n.message;
					this.getNotebookDetailsForProject();
				} else {
					this.errorMessage = n.message;
				}
				this.alertOpen = true;
				this.isOpenRestoreDialog = false;
			}).catch((error) => {
				console.error('Request failed', error);
				this.errorMessage = 'Notebook unarchive request failed with error: ' + error;
				this.alertOpen = true;
			});
	}

	deleteNotebook() {
		const url = this.componenturl + '/api/project/deleteNotebook';
		this.resetMessage();
		fetch(url, {
			method: 'DELETE',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"noteBookId": this.selectedNotebookId,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					this.successMessage = n.message;
					this.getNotebookDetailsForProject();
				} else {
					this.errorMessage = n.message;
				}
				this.alertOpen = true;
				this.isOpenDeleteDialog = false;
			}).catch((error) => {
				console.error('Request failed', error);
				this.errorMessage = 'Notebook delete request failed with error: ' + error;
				this.alertOpen = true;
			});
	}

	getNotebookList() {
		const url = this.componenturl + '/api/notebooks';
		fetch(url, {
			method: 'POST',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"userName": this.userName
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Error') {
					this.errorMessage = n.message;
					this.view = 'error';
					this.alertOpen = true;
				} else {
					this.allNotebooks = [];
					this.convertAllNotebookObject(n.data);
				}
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Notebook fetch request failed with error: ' + error;
				this.view = 'error';
				this.alertOpen = true;
			});
	}

	updateNotebook() {
		const url = this.componenturl + '/api/notebook/update';
		this.resetMessage();
		fetch(url, {
			method: 'PUT',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"userName": this.userName,
				"url": this.notebookmSURL,
				"noteBookId": this.selectedNotebookId,
				"notebookPayload": this.data.editNotebook
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					this.successMessage = n.message;
					this.initializeCreateNotebookForm();
					this.getNotebookDetailsForProject();

					this.$data.revert('editNotebook');
					this.$validations.resetValidation('editNotebook');
					this.isOpenModalEdit = false;
				} else {
					this.$data.set('editErrorMessage', n.message);
				}
				this.alertOpen = true;
			}).catch((error) => {
				console.info('Request failed', error);
				this.errorMessage = 'Update notebook request failed with error: ' + error;
				this.alertOpen = true;
			});
	}

	launchNotebook(noteBookId) {
		const url = this.componenturl + '/api/notebook/launch';
		this.resetMessage();
		fetch(url, {
			method: 'POST',
			mode: 'cors',
			cache: 'default',
			headers: {
				"Content-Type": "application/json",
				"auth": this.authToken,
			},
			body: JSON.stringify({
				"url": this.notebookmSURL,
				"notebookId": noteBookId,
				"userName": this.userName,
				"projectId": this.projectId
			})
		}).then(res => res.json())
			.then((n) => {
				if (n.status === 'Success') {
					let launchURL = n.data.noteBookId.serviceUrl;
					window.open(launchURL, '_blank');
				} else {
					this.errorMessage = n.message;
					this.alertOpen = true;
				}
			}).catch((error) => {
				console.error('Request failed', error);
				this.errorMessage = 'Notebook launch request failed with error: ' + error;
				this.alertOpen = true;
			});
	}

	convertAllNotebookObject(allNotebooksInfo) {
		let tempNotebook;
		let allNotebooks = [];
		allNotebooksInfo.forEach(item => {
			tempNotebook = {};
			tempNotebook.noteBookId = item.noteBookId.uuid;
			tempNotebook.name = item.noteBookId.name;
			tempNotebook.version = item.noteBookId.versionId.label;
			tempNotebook.createdTimestamp = item.noteBookId.versionId.timeStamp;
			tempNotebook.createdBy = item.owner.authenticatedUserId;
			tempNotebook.description = item.description;
			tempNotebook.status = item.artifactStatus.status;
			tempNotebook.notebookType = item.notebookType;
			tempNotebook.serviceUrl = item.noteBookId.serviceUrl;
			allNotebooks.push(tempNotebook);
		});
		this.getAllNotebooks(allNotebooks);
	}

	getAllNotebooks(nbList) {
		this.unassociatedNotebooks = differenceWith(nbList, this.notebooks, function (notebook, projectNotebook) {
			return notebook.noteBookId === projectNotebook.noteBookId;
		});

		this.jupyterNotebooks = this.unassociatedNotebooks.filter(notebook => {
			return notebook.notebookType === 'JUPYTER';
		});
		this.zeppelinNotebooks = this.unassociatedNotebooks.filter(notebook => {
			return notebook.notebookType === 'ZEPPELIN';
		});
	}

	userAction(action, noteBookId, notebookName) {
		this.dispatchEvent(
			new CustomEvent("catalog-notebook-event", {
				detail: {
					data: {
						action: action,
						noteBookId: noteBookId,
						notebookName: notebookName
					}
				}
			})
		);
	}

	displayNotebooks() {
		this.activeFilter = {};
		this.activeSort = "created";

		this.dataSource = new DataSource({
			data: this.notebooksList,
			filter: this.activeFilter,
			sort: this.activeSort,
			pageSize: 5
		});
		this.sortNotebooks(this.activeSort);
		this.notebooks = this.dataSource.data;
		this.currentPage = this.dataSource.page + 1;
		this.totalPages = this.dataSource.totalPages;
		this.totalNotebooks = this.notebooksList.length;
		this.allNotebookCount = this.getFilteredCount();
		this.zeppelinNotebookCount = this.getFilteredCount({ notebookType: "ZEPPELIN" });
		this.jupyterNotebookCount = this.getFilteredCount({ notebookType: "JUPYTER" });

		if (this.totalNotebooks > 0) {
			this.view = 'view';
		} else {
			this.view = 'add';
		}
	}

	filterNotebooks(criteria) {
		this.activeFilter = criteria;
		this.dataSource.page = 0;
		this.currentPage = this.dataSource.page + 1;
		this.dataSource.filter(criteria);
		this.notebooks = this.dataSource.data;
		this.totalPages = this.dataSource.totalPages;
	}

	sortNotebooks(key) {
		if (key === "created") {
			this.dataSource.sort((n1, n2) => {
				if (n1.createdTimestamp < n2.createdTimestamp) {
					return 1;
				} else if (n1.createdTimestamp > n2.createdTimestamp) {
					return -1;
				} else {
					return 0;
				}
			});
		} else {
			this.dataSource.sort(key);
		}

		this.activeSort = key;
		this.dataSource.page = 0;
		this.currentPage = this.dataSource.page + 1;
		this.notebooks = this.dataSource.data;
	}

	searchNotebooks(searchCriteria) {
		this.dataSource.search(searchCriteria);
		this.notebooks = this.dataSource.data;
		this.dataSource.page = 0;
		this.currentPage = this.dataSource.page + 1;
		this.totalPages = this.dataSource.totalPages;
	}

	navigatePage(direction) {
		this.dataSource.navigatePage(direction);

		this.currentPage = this.dataSource.page + 1;
		this.notebooks = this.dataSource.data;
	}

	getFilteredCount(criteria) {
		return filter(this.dataSource._rawData, criteria).length;
	}

	linkNotebook(notebook) {
		if (this.data.linkNotebook.notebookType === "ZEPPELIN") {
			notebook = this.zeppelinNotebooks.filter(zpNotebook => {
				return zpNotebook.name === notebook;
			});
		} else if (this.data.linkNotebook.notebookType === "JUPYTER") {
			notebook = this.jupyterNotebooks.filter(jpNotebook => {
				return jpNotebook.name === notebook;
			});
		}
		this.selectedNotebookId = notebook[0].noteBookId;
		this.$data.set('linkNotebook.noteBookId.name', notebook[0].name);
		this.$data.set('linkNotebook.noteBookId.versionId.label', notebook[0].version);
		this.$data.set('linkNotebook.description', notebook[0].description);
		this.$data.set('linkNotebook.notebookType', notebook[0].notebookType);
	}

	modalDismissed() {
		this.$data.revert('newNotebook');
		this.$validations.resetValidation('newNotebook');

		this.$data.revert('editNotebook');
		this.$validations.resetValidation('editNotebook');

		this.$data.revert('linkNotebook');
		this.$validations.resetValidation('linkNotebook');

		this.$data.set('createErrorMessage', '');
		this.$data.set('associateErrorMessage', '');
		this.$data.set('editErrorMessage', '');

		this.isOpenModal = false;
		this.isOpenModalLink = false;
		this.isOpenModalEdit = false;
	}

	modalClosed() {
		this.requestUpdate();
		this.createNotebook();
	}

	modalClosedLink() {
		this.requestUpdate();
		this.associateNotebook();
	}

	modalClosedEdit() {
		this.requestUpdate();
		this.updateNotebook();
	}

	openModal() {
		this.isOpenModal = true;
		this.isOpenModalLink = false;
		this.isOpenModalEdit = false;
	}

	openModalLink() {
		this.getNotebookList();
		this.$data.set('linkNotebook.noteBookId.name', '');
		this.isOpenModalLink = true;
		this.isOpenModal = false;
		this.isOpenModalEdit = false;
	}

	openModalEdit(item) {
		this.selectedNotebookId = item.noteBookId;
		this.$data.set('editNotebook.noteBookId.name', item.name, true);
		this.$data.set('editNotebook.noteBookId.versionId.label', item.version, true);
		this.$data.set('editNotebook.noteBookId.uuid', item.noteBookId, true);
		this.$data.set('editNotebook.description', item.description, true);
		this.$data.set('editNotebook.notebookType', item.notebookType, true);
		this.$data.set('editNotebook.noteBookId.serviceUrl', item.serviceUrl, true);
		this.isOpenModalEdit = true;
		this.isOpenModalLink = false;
		this.isOpenModal = false;
	}

	archiveDialogDismissed() {
		this.isOpenArchiveDialog = false;
	}

	restoreDialogDismissed() {
		this.isOpenRestoreDialog = false;
	}

	deleteDialogDismissed() {
		this.isOpenDeleteDialog = false;
	}

	openArchiveDialog(noteBookId, notebookName) {
		this.selectedNotebookId = noteBookId;
		this.selectedNotebookName = notebookName;
		this.isOpenArchiveDialog = true;
	}

	openRestoreDialog(noteBookId, notebookName) {
		this.selectedNotebookId = noteBookId;
		this.selectedNotebookName = notebookName;
		this.isOpenRestoreDialog = true;
	}

	openDeleteDialog(noteBookId, notebookName) {
		this.selectedNotebookId = noteBookId;
		this.selectedNotebookName = notebookName;
		this.isOpenDeleteDialog = true;
	}

	render() {
		return html`
    	<style> 
				@import url('https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css');
				.hide {
					display: none;
				}
				.show {
					display: block;
				}
			</style>
		 <omni-dialog  title="Archive ${this.selectedNotebookName}" close-string="Archive Notebook" dismiss-string="Cancel"
		 		is-open="${this.isOpenArchiveDialog}" @omni-dialog-dimissed="${this.archiveDialogDismissed}"
        @omni-dialog-closed="${this.archiveNotebook}" type="warning">
        <form><P>Are you sure want to archive ${this.selectedNotebookName}?</p></form>
      </omni-dialog>

			<omni-dialog title="Unarchive ${this.selectedNotebookName}" close-string="Unarchive Notebook" dismiss-string="Cancel"
				is-open="${this.isOpenRestoreDialog}" @omni-dialog-dimissed="${this.restoreDialogDismissed}"
        @omni-dialog-closed="${this.restoreNotebook}" type="warning">
        <form><P>Are you sure want to unarchive ${this.selectedNotebookName}?</p></form>
      </omni-dialog>

			<omni-dialog title="Delete ${this.selectedNotebookName}" close-string="Delete Notebook" dismiss-string="Cancel"
				is-open="${this.isOpenDeleteDialog}" @omni-dialog-dimissed="${this.deleteDialogDismissed}"
        @omni-dialog-closed="${this.deleteNotebook}" type="warning">
        <form><P>Are you sure want to delete ${this.selectedNotebookName}?</p></form>
      </omni-dialog>

      <omni-modal title="Create Notebook" close-string="Create Notebook" dismiss-string="Cancel"
        is-open="${this.isOpenModal}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosed}"
        canClose="${this.$validations.$valid('newNotebook') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.createErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Notebook Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Notebook Name" .value="${this.data.newNotebook.noteBookId.name}"
                  @keyup="${ e => {
				this.$data.set('newNotebook.noteBookId.name', e);
				this.$validations.validate('newNotebook.noteBookId.name');
			}
			}"
                />
                ${
			this.$validations.getValidationErrors('newNotebook.noteBookId.name').map(error => {
				switch (error) {
					case 'isNotEmpty':
						return html`<div class="invalid-feedback d-block">Notebook Name is required</div>`
					case 'pattern':
						return html`<div class="invalid-feedback d-block">Notebook Name should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number</div>`
				}
			})
			}
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Notebook Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Notebook Version" .value="${this.data.newNotebook.noteBookId.versionId.label}"
                  @keyup="${ e => {
				this.$data.set('newNotebook.noteBookId.versionId.label', e);
				this.$validations.validate('newNotebook.noteBookId.versionId.label');
			}
			}"
                />
                ${
			this.$validations.getValidationErrors('newNotebook.noteBookId.versionId.label').map(error => {
				switch (error) {
					case 'isNotEmpty':
						return html`<div class="invalid-feedback d-block">Notebook Version is required</div>`
					case 'pattern':
						return html`<div class="invalid-feedback d-block">Notebook Version should contain only 1-14 numeric characters, may include "_" and "."</div>`
				}
			})
			}
              </div>
            </div>
					</div>
					<br/>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Notebook Type <small class="text-danger">*</small></label>
                <select class="form-control" id="mySelect"
                  @change="${e => this.$data.set('newNotebook.notebookType', e.target.value)}">
                  <option value="JUPYTER">Jupyter Notebook</option>
                </select>
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Notebook Description</label>
                <textarea class="form-control" placeholder="Enter Notebook Description" .value="${this.data.newNotebook.description}"
									@keyup="${e => {
				this.$data.set('newNotebook.description', e)
				this.$validations.validate('newNotebook.description');
			}}">
								</textarea>
              </div>
            </div>
					</div>
					${this.useExternalNotebook === "true"
          ? html`
            <br/>
            <div class="row">
              <div class="col">
                <div class="form-group">
                  <label>Notebook URL <small class="text-danger">*</small></label>
                  <input type="url" class="form-control" placeholder="Enter Notebook URL" 
                    .value="${this.data.newNotebook.noteBookId.serviceUrl}"
                    @keyup="${ e => {
                    this.$data.set('newNotebook.noteBookId.serviceUrl', e);
                    this.$validations.validate('newNotebook.noteBookId.serviceUrl');
                  }
                  }"
                            />
                            ${
                  this.$validations.getValidationErrors('newNotebook.noteBookId.serviceUrl').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Notebook URL is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Notebook URL should begin with https://</div>`
                    }
                  })
                  }
                </div>
              </div>
            </div>
            `
          : ``
          }
        </form>
      </omni-modal>
      
      <omni-modal title="Associate Notebook" close-string="Associate Notebook" dismiss-string="Cancel"
				is-open="${this.isOpenModalLink}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedLink}"
				canClose="${this.$validations.$valid('linkNotebook') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.associateErrorMessage} </p>
          <div class="row">
            <div class="col">
            	<div class="form-group">
									<label>Notebook Type <small class="text-danger">*</small></label>
	                <select class="form-control" id="mySelect" 
										@change="${e => {
				this.$data.set('linkNotebook.notebookType', e.target.value);
				this.$validations.validate('linkNotebook.notebookType');
			}}">
										<option value="" ?selected="${this.data.linkNotebook.notebookType === ''}">  Select Notebook Type</option>
	                  <option value="JUPYTER">Jupyter Notebook</option>
									</select>
									${
			this.$validations.getValidationErrors('linkNotebook.notebookType').map(error => {
				switch (error) {
					case 'isNotEmpty':
						return html`<div class="invalid-feedback d-block">Notebook Type is required</div>`
				}
			})
			}
	            </div>
	        	</div>
					</div>
					<br/>
					<div class="row">
						<div class="col">
							<div class="form-group">
								<label>Notebook Name <small class="text-danger">*</small></label>
								${(this.data.linkNotebook.notebookType !== undefined && this.data.linkNotebook.notebookType !== "null" && this.data.linkNotebook.notebookType !== '')
				? html`
										${(this.data.linkNotebook.notebookType === "ZEPPELIN")
						? html`								
												<select class="form-control" id="selectNotebook" 
													@change="${e => {
								this.linkNotebook(e.target.value);
								this.$data.set('linkNotebook.noteBookId.name', e);
								this.$validations.validate('linkNotebook.noteBookId.name');
							}}">
													<option value="">Select a Notebook</option>
													${this.zeppelinNotebooks.map((item, index) =>
								html`															
															<option value="${item.name}">${item.name}</option>
														`
							)}
												</select>
												
											`
						: ``
					}	
										${this.data.linkNotebook.notebookType === "JUPYTER"
						? html`
												<select class="form-control" id="selectNotebook" 
													@change="${e => {
								this.linkNotebook(e.target.value);
								this.$data.set('linkNotebook.noteBookId.name', e);
								this.$validations.validate('linkNotebook.noteBookId.name');
							}}">
													<option value="null">Select a Notebook</option>
													${this.jupyterNotebooks.map((item, index) =>
								html`
															<option value="${item.name}">${item.name}</option>
														`
							)}
												</select>
											`
						: ``
					}
										
										${
					this.$validations.getValidationErrors('linkNotebook.noteBookId.name').map(error => {
						switch (error) {
							case 'isNotEmpty':
								return html`<div class="invalid-feedback d-block">Please select Notebook from dropdown</div>`

						}
					})
					}
									`
				: html`
										<select class="form-control" id="selectNotebook" disabled/>
									`
			}
							</div>
						</div>
						<div class="col">
							<div class="form-group">
								<label>Notebook Version <small class="text-danger">*</small></label>
								${(this.data.linkNotebook.noteBookId.versionId.label !== undefined || this.data.linkNotebook.noteBookId.versionId.label !== '')
				? html`
										<input type="text" class="form-control" placeholder="${this.data.linkNotebook.noteBookId.versionId.label}" 
										value="${this.data.linkNotebook.noteBookId.versionId.label}" disabled/>
									`
				: html`
									<input type="text" class="form-control" placeholder="Notebook Version" value="${this.data.linkNotebook.noteBookId.versionId.label}"  disabled/>
									`
			}
							</div>
						</div>
					</div>	
				</form>
			</omni-modal>
	
	    <omni-modal title="Edit Notebook" close-string="Update Notebook" dismiss-string="Cancel"
        is-open="${this.isOpenModalEdit}" @omni-modal-dimissed="${this.modalDismissed}" @omni-modal-closed="${this.modalClosedEdit}"
        canClose="${this.$validations.$valid('editNotebook') && this.$validations.$dirty}">
        <form novalidate>
          <p class="text-danger">${this.data.editErrorMessage} </p>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Notebook Name <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Notebook Name" .value="${this.data.editNotebook.noteBookId.name}"
                  @keyup="${ e => {
				this.$data.set('editNotebook.noteBookId.name', e);
				this.$validations.validate('editNotebook.noteBookId.name');
			}
			}"
                />
                ${
			this.$validations.getValidationErrors('editNotebook.noteBookId.name').map(error => {
				switch (error) {
					case 'isNotEmpty':
						return html`<div class="invalid-feedback d-block">Notebook Name is required</div>`
					case 'pattern':
						return html`<div class="invalid-feedback d-block">Notebook Name should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number</div>`
				}
			})
			}
              </div>
            </div>
            <div class="col">
              <div class="form-group">
                <label>Notebook Version <small class="text-danger">*</small></label>
                <input type="text" class="form-control" placeholder="Enter Notebook Version" .value="${this.data.editNotebook.noteBookId.versionId.label}"
                  @keyup="${ e => {
				this.$data.set('editNotebook.noteBookId.versionId.label', e);
				this.$validations.validate('editNotebook.noteBookId.versionId.label');
			}
			}"
                />
                ${
			this.$validations.getValidationErrors('editNotebook.noteBookId.versionId.label').map(error => {
				switch (error) {
					case 'isNotEmpty':
						return html`<div class="invalid-feedback d-block">Notebook Version is required</div>`
					case 'pattern':
						return html`<div class="invalid-feedback d-block">Notebook Version should contain only 1-14 numeric characters, may include "_" and "."</div>`
				}
			})
			}
              </div>
            </div>
					</div>
					<br/>
          <div class="row">
            <div class="col">
              <div class="form-group">
                <label>Notebook Description</label>
                <textarea class="form-control" placeholder="Enter Notebook Description" .value="${this.data.editNotebook.description}"
									@keyup="${e => {
				this.$data.set('editNotebook.description', e)
				this.$validations.validate('editNotebook.description');
			}}">
								</textarea>
              </div>
            </div>
					</div>
					${this.useExternalNotebook === "true"
          ? html`
            <br/>
            <div class="row">
              <div class="col">
                <div class="form-group">
                  <label>Notebook URL <small class="text-danger">*</small></label>
                  <input type="url" class="form-control" placeholder="Enter Notebook URL" 
                    .value="${this.data.editNotebook.noteBookId.serviceUrl}"
                    @keyup="${ e => {
                    this.$data.set('editNotebook.noteBookId.serviceUrl', e);
                    this.$validations.validate('editNotebook.noteBookId.serviceUrl');
                  }
                  }"
                            />
                            ${
                  this.$validations.getValidationErrors('editNotebook.noteBookId.serviceUrl').map(error => {
                    switch (error) {
                      case 'isNotEmpty':
                        return html`<div class="invalid-feedback d-block">Notebook URL is required</div>`
                      case 'pattern':
                        return html`<div class="invalid-feedback d-block">Notebook URL should begin with https://</div>`
                    }
                  })
                  }
                </div>
              </div>
            </div>
            `
          : ``
          }
        </form>
      </omni-modal>
      
      ${this.view === 'view'
				? html`
        	<div class="row ">
					<div class="col-md-12 py-3">
						<div class="card mb-124 shadow mb-5 bg-white">
							<div class="card-header">
								<div class="row" style="margin:5px 0; margin-top: 0px;">
									<mwc-icon class="textColor">import_contacts</mwc-icon>&nbsp;&nbsp;&nbsp;
									<h4 class="textColor card-title">Notebooks</h4>
									<div style="position: absolute; right:0" >
										<a href=${this.notebookWikiURL} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
										${
					this.cardShow === false
						? html`
												<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = true}>
													<span class="toggle-plus-span toggle-span">+</span>
												</a>
											`
						: html`
												<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = false}>
													<span class="toggle-span">-</span>
												</a>
											`
					}
										&nbsp;&nbsp;&nbsp;&nbsp;
									</div>
								</div>
							</div>			
						<div class="card-body ${this.cardShow ? 'show' : 'hide'}">
							<div class="row" style="margin:5px 0; margin-top: 0px;">
							 	<div class="col-lg-12">
									${this.successMessage !== ''
						? html`
											<div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
												<a class="close" @click=${e => this.alertOpen = false}>
													<span aria-hidden="true">&nbsp;&times;</span>
												</a> <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
											</div>
										`: ``
					}
									${this.errorMessage !== ''
						? html`
											<div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
												<a class="close" @click=${e => this.alertOpen = false}>
														<span aria-hidden="true">&nbsp;&times;</span>
												</a> <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
											</div>
										`: ``
					}
				        </div>
				      </div>
							<div  class="row" style="margin:5px 0; margin-top: 0px; position: absolute; right:0">								
								<div class="btn-toolbar mb-2 mb-md-0">
									<div class="dropdown">
										<select class="custom-select mr-sm-2" id="template" @change=${e => this.sortNotebooks(e.target.value)}>
											${this.sortOptions.map(item => item.value === this.activeSort
						? html`
													<option value="${item.value}" selected>${item.label}</option>
												`
						: html`
													<option value="${item.value}">${item.label}</option>
												`
					)}
										</select>
									</div>
									<div class="btn-group mr-2">
										&nbsp;
										<input type="text" class="form-control w-100" placeholder="Search Notebook"
												@input=${e => this.searchNotebooks(e.target.value)}/>
										<div class="input-group-append">
											<a class="btnIcon btn btn-sm btn-primary  mr-1" data-toggle="tooltip" data-placement="top" title="Search Notebook Instance" >
												<mwc-icon class="mwc-icon-primary white-color">search</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.openModal()} class="btnIcon btn btn-sm btn-primary mr-1" data-toggle="tooltip" data-placement="top" title="Create Notebook">
													<mwc-icon class="mwc-icon-primary">add</mwc-icon>
											</a>&nbsp;
											<a href="javascript:void" @click=${(e) => this.openModalLink()} class="btnIcon btn btn-sm btn-secondary  mr-1" data-toggle="tooltip" data-placement="top" title="Associate Existing Notebook">
													<mwc-icon class="mwc-icon-secondary">link</mwc-icon>
											</a>&nbsp;&nbsp;&nbsp;
										</div>
									</div>
								</div>
							</div>
							<br/>

							<div class="row" style="margin:40px 0; margin-bottom: 10px;">
								<table class="table table-bordered table-sm" id="cssTable">
									<thead class="thead-light">
										<tr class="d-flex">
											<th class="col-1" >#</th>
											<th class="col-2" >Notebook Name</th>
											<th class="col-1" >Version</th>
											<th class="col-2" >Notebook Type</th>
											<th class="col-2" >Notebook Status</th>
											<th class="col-2" >Creation Date</th>
											<th class="col-2">Actions</th>
										</tr>
									</thead>
									<tbody>
										${this.notebooks.length !== 'undefined'
						? html`
											${this.notebooks.map((item, index) =>
							html`
												<tr class="d-flex">
													<td class="col-1">${(this.currentPage - 1) * 5 + ++index}</td>
													<td class="col-2">${item.name}</td>
													<td class="col-1">${item.version}</td>
													<td class="col-2">${item.notebookType}</td>
													<td class="col-2">
														${item.status === 'ACTIVE'
									? html`
																<span class="active-status">${item.status}</span>
															`
									: html`
																<span class="inactive-status">${item.status}</span>
															`
								}
													</td>
													<td class="col-2">${item.createdTimestamp}</td>
													<td class="col-2" style="padding: .05rem; padding-left: 20px;">
														${item.status == 'ACTIVE'
									? html`
														<a href="javascript:void" @click=${(e) => this.launchNotebook(item.noteBookId)}   class="btnIcon btn btn-sm btn-primary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Launch Notebook">
																<mwc-icon class="mwc-icon-primary">launch</mwc-icon>
														</a>&nbsp;
														<a href="javascript:void" @click=${e => this.openModalEdit(item)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1 " data-toggle="tooltip" data-placement="top" title="Edit Notebook">
																<mwc-icon class="mwc-icon-secondary">edit</mwc-icon>
														</a>&nbsp;
														<a href="javascript:void" @click=${(e) => this.openArchiveDialog(item.noteBookId, item.name)} class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Archive Notebook">
																<mwc-icon class="mwc-icon-secondary">archive</mwc-icon>
														</a>
														`
									: html`
															<a href="javascript:void" @click="${e => this.openRestoreDialog(item.noteBookId, item.name)}"
																class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Unarchive Notebook">
																<mwc-icon class="mwc-icon-secondary">restore_from_trash</mwc-icon>
															</a>&nbsp;
															<a href="javascript:void" @click="${e => this.openDeleteDialog(item.noteBookId, item.name)}"
																class="btnIcon btn btn-sm btn-secondary my-1 mr-1" data-toggle="tooltip" data-placement="top" title="Delete Notebook">
																<mwc-icon class="mwc-icon-secondary">delete</mwc-icon>
															</a>
														`}
													</td>
												</tr>
												`
						)}`
						: ``
					}
										
									</tbody>
								</table>
							</div>

							<div class="row">
                <h7>&nbsp;&nbsp;&nbsp;&nbsp;Showing ${this.currentPage} of ${this.totalPages === 0 ? 1 : this.totalPages} pages</h7>
                <div style="position: absolute; right:0;">
                  <nav aria-label="Page navigation example">
                    <ul class="pagination justify-content-end">
											<li class="page-item">
												<a href="javascript:void" @click=${e => this.navigatePage("first")}
													class="page-link ${this.currentPage !== 1 ? "active" : "inactive"}">First</a>                          
											</li>
											<li class="page-item">
												<a class="page-link ${this.currentPage !== 1 ? "active" : "inactive"}" href="javascript:void" 
													@click=${e => this.navigatePage("previous")} >Previous</a>
											</li>
											<li class="page-item">
												<a class="page-link ${this.currentPage < this.totalPages ? "active" : "inactive"}" href="javascript:void" 
													@click=${e => this.navigatePage("next")} >Next</a>
											</li>
											<li class="page-item">
												<a class="page-link ${this.currentPage < this.totalPages ? "active" : "inactive"}" href="javascript:void" 
													@click=${e => this.navigatePage("last")} >Last</a>
											</li>
                      &nbsp;&nbsp;&nbsp;&nbsp;
                    </ul>
                  </nav>
                </div>
              </div>
							<br/>
						</div>
					</div>
				</div>
			</div>
			`
				: html`
	      `}
	      
	      ${this.view === 'add'
				? html`
						<div class="row">
							<div class="col-md-12 py-3">
								<div class="card mb-124  shadow mb-5 bg-white">
									<div class="card-header">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
											<mwc-icon class="textColor">import_contacts</mwc-icon>&nbsp;&nbsp;&nbsp;
											<h4 class="textColor card-title">Notebooks</h4>
											<div style="position: absolute; right:0" >
												<a href=${this.notebookWikiURL} target="_blank" class="my-2">Learn more</a>&nbsp;&nbsp;&nbsp;
												${
					this.cardShow === false
						? html`
														<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = true}>
															<span class="toggle-plus-span toggle-span">+</span>
														</a>
													`
						: html`
														<a class="toggle-a btn btn-sm btn-secondary my-2" @click=${e => this.cardShow = false}>
															<span class="toggle-span">-</span>
														</a>
													`
					}
												&nbsp;&nbsp;&nbsp;&nbsp;
											</div>
										</div>
									</div>
									<div class="card-body ${this.cardShow ? 'show' : 'hide'}">
										<div class="row" style="margin:5px 0; margin-top: 0px;">
											<div class="col-lg-12">
												${this.successMessage !== ''
						? html`
														<div class="alert alert-success ${this.alertOpen ? 'show' : 'hide'}">
															<a class="close" @click=${e => this.alertOpen = false}>
																<span aria-hidden="true">&nbsp;&times;</span>
															</a> <mwc-icon>done_outline</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.successMessage}</span>
														</div>
													`: ``
					}
												${this.errorMessage !== ''
						? html`
														<div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
															<a class="close" @click=${e => this.alertOpen = false}>
																	<span aria-hidden="true">&nbsp;&times;</span>
															</a>  <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
														</div>
													`: ``
					}
											</div>
										</div>
										<div class="row" style="margin:5px 0; margin-top: 0px;margin-bottom:20px;">
											<h7 >No Notebooks, get started by creating your first Notebook</h7>
										</div>
										<div class="row" style="margin:10px 0">
											<button type="button" class="btn btn-primary" @click=${(e) => this.openModal()}>Create Notebook</button>&nbsp;&nbsp;&nbsp;
											<button type="button" class="btn-secondary-button btn" @click=${(e) => this.openModalLink()}>Associate Existing Notebook</button>											
										</div>
									</div>
							</div>
						</div>
					</div>
					`
				: html`
     `}
          
      ${this.view === 'error'
				? html`
          <div class="alert alert-danger ${this.alertOpen ? 'show' : 'hide'}">
            <a class="close" @click=${e => this.alertOpen = false}>
                <span aria-hidden="true">&nbsp;&times;</span>
            </a>
            <mwc-icon>error</mwc-icon>&nbsp;&nbsp;<span class="span-message">${this.errorMessage}</span>
          </div>
        `
				: html`
      `}

      ${this.view === ''
				? html`
          <p class="success-status"> Loading Notebooks..</p>
        `
				: html`
      `}  	
    `;
	}
}
customElements.define('project-notebook-element', ProjectNotebookLitElement);
