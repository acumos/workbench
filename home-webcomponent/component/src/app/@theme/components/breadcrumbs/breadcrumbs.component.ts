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

import { Component, Input, OnInit } from '@angular/core';
import { NbMenuService, NbSidebarService } from '@nebular/theme';
import { BreadcrumbsService } from '../../../@core/utils/breadcrumbs.service';
import { Observable } from 'rxjs';
import { Globals } from '../../../globals'

@Component({
  selector: 'ngx-ml-breadcrumbs',
  styleUrls: ['./breadcrumbs.component.scss'],
  templateUrl: './breadcrumbs.component.html',
})
export class BreadcrumbsComponent implements OnInit {
  public breadcrumbs$: Observable<string[]>;
  public defaultBreadcrumbs: string[];
  public parentMsg: string;

  constructor(private breadcrumbsService: BreadcrumbsService, private globals: Globals) {

  }

  ngOnInit() {
    this.breadcrumbs$ = this.breadcrumbsService.getBreadcrumbs();
    this.defaultBreadcrumbs = ['Home', 'Dashboard'];
  }
  
  navigateToPortal(path) {
    if(this.globals.parentMsg === "iframeMsg"){
      if(path.name === "Home") {
        window.top.postMessage('navigateToHome', '*');
      } else if(path.name === "Design Studio") {
        window.top.postMessage('navigateToDesignStudio', '*');
      } 
    } else{
      console.info("This is not an iframe");
    }
  }
}
