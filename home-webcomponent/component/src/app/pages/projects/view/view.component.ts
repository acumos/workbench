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
import { ActivatedRoute, Router } from '@angular/router';
import { ScriptService } from '../../../@core/utils/script.service';
import { BreadcrumbsService } from '../../../@core/utils/breadcrumbs.service';

@Component({
  templateUrl: './view.component.html',
})
export class ViewComponent implements OnInit {

  public id: string;
  public name: string;
  public router: Router;
  script: ScriptService;
  public projectComponentURL: string;
  public userName: any;
  public authToken: any;
  public sessionError: any;
  public alertOpen: any;
  public loadHtml: any;
  public breadCrumbs: any[] = [
    { name: 'Home', href: '' },
    { name: 'Design Studio', href: '' },
    { name: 'ML Workbench', sref: '/pages/dashboard' },
    { name: 'Projects', sref: '/pages/projects/catalog' }];

  constructor(private route: ActivatedRoute, router: Router, script: ScriptService, private breadcrumbsService: BreadcrumbsService) {
    this.script = script;
    this.router = router;
  }

  OnViewProjectEvent(e) {
    if (e.detail.data === 'catalog-project') {
      this.router.navigateByUrl('/pages/projects/catalog');
    }
  }

  ngOnInit() {
    this.loadHtml = false;
    this.loadComponent();
  }

  private loadComponent() {
    this.id = this.route.snapshot.paramMap.get('id');
    this.name = this.route.snapshot.paramMap.get('name');
    this.projectComponentURL = this.script.getConfig('projectComponent');
    this.script.getUserSession().subscribe((res: any) => {
      if (res.userName !== '' && res.authToken !== '') {
        let portalFEURL: any;
        portalFEURL = this.script.getConfig('portalFEURL');
        this.breadCrumbs[0].href = portalFEURL;
        this.breadCrumbs[1].href = portalFEURL + '/#/designStudio';
        this.breadCrumbs.push({ name: this.name });

        this.breadcrumbsService.setBreadcrumbs(this.breadCrumbs);
        this.userName = res.userName;
        this.authToken = res.authToken;
        this.alertOpen = false;
        this.script.load('projectComponent', '/src/project-element.js');
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
