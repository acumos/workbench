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

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ScriptService } from '../../../@core/utils/script.service';
import { BreadcrumbsService } from '../../../@core/utils/breadcrumbs.service';

@Component({
  templateUrl: './catalog.component.html',
})
export class NotebookCatalogComponent implements OnInit {
  router: Router;
  script: ScriptService;
  public notebookCatalogComponentURL: string;
  public userName: any;
  public authToken: any;
  public sessionError: any;
  public alertOpen: any;
  public loadHtml: any;
  public breadCrumbs: any[] = [
    { name: 'Home', href: '' },
    { name: 'Design Studio', href: '' },
    { name: 'ML Workbench', sref: '/pages/dashboard' },
    { name: 'Notebooks' }];

  constructor(router: Router, script: ScriptService, private breadcrumbsService: BreadcrumbsService) {
    this.router = router;
    this.script = script;
  }

  OnCatalogNotebookEvent(e) {
    if (e.detail.data.action === 'view-notebook') {
      this.router.navigateByUrl('/pages/notebook/view/' + e.detail.data.noteBookId + '/' + e.detail.data.notebookName);
    }
  }

  ngOnInit() {
    this.loadHtml = false;
    this.loadComponent();
  }

  private loadComponent() {
    this.notebookCatalogComponentURL = this.script.getConfig('notebookCatalogComponent');
    this.script.getUserSession().subscribe((res: any) => {
      if ( res.userName !== '' && res.authToken !== '') {
        let portalFEURL: any;
        portalFEURL = this.script.getConfig('portalFEURL');
        this.breadCrumbs[0].href = portalFEURL;
        this.breadCrumbs[1].href = portalFEURL + '/#/designStudio';

        this.breadcrumbsService.setBreadcrumbs(this.breadCrumbs);
        this.userName = res.userName;
        this.authToken = res.authToken;
        this.alertOpen = false;
        this.loadHtml = true;
        this.script.load('notebookCatalogComponent', '/src/notebook-catalog-element.js');
      } else {
        this.sessionError = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
        this.alertOpen = true;
      }
    }, (error) => {
      console.error('Unable to get the user session :' + error);
    });
  }
}
