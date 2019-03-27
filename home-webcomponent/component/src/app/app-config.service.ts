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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { retry } from 'rxjs/operators';

@Injectable()
export class AppConfigService {
  private appConfig;

  constructor(private http: HttpClient) {}

  loadAppConfig() {
    return this.http
      .get('config')
      .pipe(retry(2))
      .toPromise()
      .then(config => {
        this.appConfig = config;
      }, error => {
        alert('Unable to load the application: ' + error);
        throw new Error(error);
      });
  }

  getConfig() {
    return this.appConfig;
  }
}
