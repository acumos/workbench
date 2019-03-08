/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

@Component({
  templateUrl: './view.component.html',
})
export class ViewComponent implements OnInit {

  public id: string;
  public router: Router;
  script: ScriptService;

  constructor(private route: ActivatedRoute, router: Router, script: ScriptService) {
    this.script = script;
    this.router = router;
  }

  OnViewProjectEvent(e) {
    if (e.detail.data === 'catalog-project') {
      this.router.navigateByUrl('/pages/projects/catalog');
    }
  }

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    this.script.load('projectComponent');
  }
}
