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
import { BaseComponent } from '../../base/base.component';

@Component({
  templateUrl: './catalog.component.html',
})
export class CatalogComponent extends BaseComponent implements OnInit {
  public breadCrumbs: any[] = [
    { name: 'Home', href: '' },
    { name: 'Design Studio', href: '' },
    { name: 'ML Workbench', sref: '/pages/dashboard' },
    { name: 'Projects' }];

  constructor(router: Router, script: ScriptService, breadcrumbsService: BreadcrumbsService) {
    super(router, script, breadcrumbsService);
  }

  OnCatalogProjectEvent(e) {
    debugger;
    if (e.detail[0].action === 'view-project') {
      this.router.navigateByUrl('/pages/projects/view/' + e.detail[0].projectId + '/' + e.detail[0].projectName);
    }
  }

  ngOnInit() {
    this.loadHtml = false;
    this.showSpinner = true;
    this.alertOpen = false;
    this.loadComponent('projectCatalogComponent', 'project-catalog-webcomponent', this.breadCrumbs);
  }
}
