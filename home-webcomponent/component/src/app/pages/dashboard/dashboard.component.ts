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
import { ScriptService } from '../../@core/utils/script.service';
import { BreadcrumbsService } from '../../@core/utils/breadcrumbs.service';
import { BaseComponent } from '../base/base.component';
import { Globals } from '../../globals';

@Component({
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent extends BaseComponent implements OnInit {
  public parentMsg: any;
  public retry: number;
  public breadCrumbs: any[] = [
    { name: 'Home', href: '' },
    { name: 'Design Studio', href: '' },
    { name: 'ML Workbench' }];

  constructor(router: Router, script: ScriptService, breadcrumbsService: BreadcrumbsService, private globals: Globals) {
    super(router, script, breadcrumbsService);
  }

  OnDashboardEvent(e) {
    if (e.detail[0].action === 'notebook') {
      this.router.navigateByUrl('/pages/notebook/catalog');
    } else if (e.detail[0].action === 'project') {
      this.router.navigateByUrl('/pages/projects/catalog');
    } else if (e.detail[0].action === 'pipeline') {
      this.router.navigateByUrl('/pages/pipeline/catalog');
    }
    else if (e.detail[0].action === 'dataset') {
      this.router.navigateByUrl('/pages/dataset/catalog');
    }
  }

  ngOnInit() {
    this.loadHtml = false;
    this.showSpinner = true;
    this.alertOpen = false;
    this.retry = 0;
    this.getGlobalMsg();
  }

  getGlobalMsg() {
    if (this.retry === 5 || this.globals.parentMsg !== undefined) {
      this.parentMsg = this.globals.parentMsg;
      this.loadComponent('dashboardComponent', 'dashboard-webcomponent', this.breadCrumbs);
      return;
    }
    setTimeout( () => {
      ++this.retry;
      this.getGlobalMsg();
    }, 1000);
  }
}
