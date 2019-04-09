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
import { Globals } from '../../globals';

@Component({
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  router: Router;
  script: ScriptService;
  public dashboardComponentURL: string;
  public userName: any;
  public authToken: any;
  public userId: any;
  public sessionError: any;
  public alertOpen: any;
  public loadHtml: any;
  public parentMsg: any;
  public retry: number;
  public breadCrumbs: any[] = [
    { name: 'Home', href: '' },
    { name: 'Design Studio', href: '' },
    { name: 'ML Workbench' }];

  constructor(router: Router, script: ScriptService, private breadcrumbsService: BreadcrumbsService, private globals: Globals) {
    this.router = router;
    this.script = script;
  }

  OnDashboardEvent(e) {
    if (e.detail.data === 'notebook') {
      this.router.navigateByUrl('/pages/notebook/catalog');
    } else if (e.detail.data === 'project') {
      this.router.navigateByUrl('/pages/projects/catalog');
    } else if (e.detail.data === 'pipeline') {
      this.router.navigateByUrl('/pages/pipeline/catalog');
    }
  }

  ngOnInit() {
    this.loadHtml = false;
    this.retry = 0;
    this.getGlobalMsg();
  }

  getGlobalMsg() {
    if(this.retry == 5 || this.globals.parentMsg !== undefined){
      this.loadComponent();
      return;
    }
    setTimeout( () => {
      ++this.retry;
      this.getGlobalMsg();
    }, 1000);
  }

  private loadComponent() {
    this.dashboardComponentURL = this.script.getConfig('dashboardComponent');
    this.script.getUserSession().subscribe((res: any) => {
      if ( res.userName !== '' && res.authToken !== '' && res.userId !== '') {
        let portalFEURL: any;
        portalFEURL = this.script.getConfig('portalFEURL');
        this.breadCrumbs[0].href = portalFEURL;
        this.breadCrumbs[1].href = portalFEURL + '/#/designStudio';

        this.breadcrumbsService.setBreadcrumbs(this.breadCrumbs);
        this.userName = res.userName;
        this.authToken = res.authToken;
        this.userId = res.userId;
        if(this.globals.parentMsg !== null && this.globals.parentMsg !== undefined)
          this.parentMsg = this.globals.parentMsg;
        this.alertOpen = false;
        this.script.load('dashboardComponent', '/src/dashboard-element.js');
        this.loadHtml = true;
      } else {
        this.sessionError = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
        this.alertOpen = true;
      }

    }, (error) => {
      console.error('Unable to get the user session :' + error);
    });
  }
}
