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

import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CatalogComponent } from './projects/catalog/catalog.component';
import { ViewComponent } from './projects/view/view.component';
import { NotebookCatalogComponent } from './notebook/catalog/catalog.component';
import { NotebookViewComponent } from './notebook/view/view.component';
import { PipelineCatalogComponent } from './pipeline/catalog/catalog.component';
import { PipelineViewComponent } from './pipeline/view/view.component';
import { AcuComposeComponent } from './acuCompose/acuCompose.component';
import { DatasourceCatalogComponent } from './datasource/catalog/catalog.component';
import { DatasourceViewComponent } from './datasource/view/view.component';

const routes: Routes = [
  {
    path: '',
    component: PagesComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
      },
      {
        path: 'projects/catalog',
        component: CatalogComponent,
      },
      {
        path: 'projects/view/:id/:name',
        component: ViewComponent,
      },
      {
        path: 'notebook/catalog',
        component: NotebookCatalogComponent,
      },
      {
        path: 'notebook/view/:id/:name',
        component: NotebookViewComponent,
      },
      {
        path: 'pipeline/catalog',
        component: PipelineCatalogComponent,
      },
      {
        path: 'pipeline/view/:id/:name',
        component: PipelineViewComponent,
      },
      {
        path: 'datasource/catalog',
        component: DatasourceCatalogComponent,
      },
      {
        path: 'datasource/view/:id/:name',
        component: DatasourceViewComponent,
      },
      {
        path: 'acuCompose',
        component: AcuComposeComponent,
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {}
