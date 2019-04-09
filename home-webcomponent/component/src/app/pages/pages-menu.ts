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

import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'Dashboard',
    icon: 'fa fa-tachometer-alt',
    link: '/pages/dashboard',
    home: true,
  },
  {
    title: 'Projects',
    icon: 'fa fa-project-diagram',
    link: '/pages/projects/catalog',
  },
  {
    title: 'Notebooks',
    icon: 'fa fa-book',
    link: '/pages/notebook/catalog',
  },
  /*{
    title: 'Data Pipelines',
    icon: 'fa fa-code-branch',
    link: '/pages/pipeline/catalog',
  },
  {
    title: 'CMLP Studio',
    icon: 'fa fa-brain',
  },*/
  {
    title: 'AcuCompose',
    icon: 'fas fa-crop-alt',
    link: '/pages/acuCompose',
  },
];
