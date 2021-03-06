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
import { Globals } from '../../globals';

@Component({
  selector: 'AcuComposeComponent',
  template: ''
})

export class AcuComposeComponent implements OnInit {
  router: Router;
  script: ScriptService;

  public alertOpen: any;
  public portalFEURL: any;
  public acuComposeURL: any;

  constructor(router: Router, script: ScriptService,private globals: Globals) {
    this.router = router;
    this.script = script;
  }

  ngOnInit() {
    this.loadComponent();
  }

  private loadComponent() {
    if(this.globals.parentMsg === "iframeMsg"){
        window.top.postMessage('navigateToAcuCompose', '*');
    } else{
        this.portalFEURL = this.script.getConfig('portalFEURL');
        this.acuComposeURL = this.portalFEURL+'/#/acuCompose';
        window.open(this.acuComposeURL, '_blank');
    }
  }
}
