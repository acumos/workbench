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

@Component({
  template: '',
})

export class BaseComponent implements OnInit {
  public url: string;
  public userName: string;
  public authToken: string;
  public sessionError: string;
  public alertOpen: boolean;
  public userId: string;
  public loadHtml: boolean;

  constructor(public router: Router, public script: ScriptService, 
    public breadcrumbsService: BreadcrumbsService) {
  }

  ngOnInit() {
  }

  public loadComponent(componentName: string, componentFileName: string, breadCrumbs: any[]) {
    this.url = this.script.getConfig(componentName);
    this.script.getUserSession().subscribe((res: any) => {
      if ( res.userName !== '' && res.authToken !== '') {
        let portalFEURL: any;
        portalFEURL = this.script.getConfig('portalFEURL');
        breadCrumbs[0].href = portalFEURL;
        breadCrumbs[1].href = portalFEURL + '/#/designStudio';

        this.breadcrumbsService.setBreadcrumbs(breadCrumbs);
        this.userName = res.userName;
        this.authToken = res.authToken;
        this.userId = res.userId;

        this.alertOpen = false;
        this.script.load(componentName, '/src/' + componentFileName + '.js');
        this.loadHtml = true;
      } else {
        this.sessionError = 'Acumos session details are unavailable in browser cookies. Pls login to Acumos portal and come back here..';
        this.alertOpen = true;
      }

    }, (error) => {
      console.error('Unable to get the user session :' + error);
      this.sessionError = error;
      this.alertOpen = true;
    });
  }

}
