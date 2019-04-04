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

@Component({
  templateUrl: './catalog.component.html',
})
export class CatalogComponent implements OnInit {
  router: Router;
  script: ScriptService;
  public projectCatalogComponentURL: string;

  constructor(router: Router, script: ScriptService) {
    this.router = router;
    this.script = script;
  }

  OnCatalogProjectEvent(e) {
    if (e.detail.data.action === 'view-project') {
      this.router.navigateByUrl('/pages/projects/view/' + e.detail.data.projectId + '/' + e.detail.data.projectName);
    }
  }

  ngOnInit() {
    this.projectCatalogComponentURL = this.script.getConfig('projectCatalogComponent');
    this.script.load('projectCatalogComponent', '/src/project-catalog-element.js');
  }
}
