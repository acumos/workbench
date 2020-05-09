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

import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardModule } from './dashboard/dashboard.module';
import { ProjectsModule } from './projects/projects.module';
import { NotebookModule } from './notebook/notebook.module';
import { PipelineModule } from './pipeline/pipeline.module';
import { DatasourceModule } from './datasource/datasource.module';
import { AcuComposeModule } from './acuCompose/acuCompose.module';
import { BaseModule } from './base/base.module';

import { PagesRoutingModule } from './pages-routing.module';
import { ThemeModule } from '../@theme/theme.module';
import { MiscellaneousModule } from './miscellaneous/miscellaneous.module';

const PAGES_COMPONENTS = [PagesComponent];

@NgModule({
  imports: [
    PagesRoutingModule,
    ThemeModule,
    DashboardModule,
    ProjectsModule,
    NotebookModule,
    PipelineModule,
    DatasourceModule,
    MiscellaneousModule,
    AcuComposeModule,
    BaseModule,
  ],
  declarations: [...PAGES_COMPONENTS],
})
export class PagesModule {}
