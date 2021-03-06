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


import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ThemeModule } from '../../@theme/theme.module';
import { PipelineCatalogComponent } from './catalog/catalog.component';
import { PipelineViewComponent } from './view/view.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [ThemeModule, RouterModule],
  declarations: [PipelineCatalogComponent, PipelineViewComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PipelineModule {}
