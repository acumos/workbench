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
import { AppConfigService } from '../../app-config.service';
import { HttpClient } from '@angular/common/http';
import { retry, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

declare var document: any;

@Injectable()
export class ScriptService {

  private scripts: any = {};
  private config: any = {};
  private session: any = {};

  constructor(config: AppConfigService, private http: HttpClient) {
    this.config = config.getConfig();
  }

  getUserSession() {
    return this.http.get('session').pipe(retry(2), catchError(this.handleError));
  }

  handleError() {
    const errorMessage = 'Error while retrieving Acumos Session information from browser cookies.';
    return throwError(errorMessage);
  }

  getConfig(name: string) {
    return this.config[name];
  }

  load(script: string, uri: string) {
    this.scripts[script] = { src: script };
    return this.loadScript(script, uri);
  }

  loadScript(name: string, uri: string) {
    return new Promise((resolve, reject) => {
      // resolve if already loaded
      if (this.scripts[name].loaded) {
        resolve({ script: name, loaded: true, status: 'Already Loaded' });
      } else {
        const script = document.createElement('script');
        script.type = 'module';
        script.src = this.config[name] + uri;
        if (script.readyState) {
          // IE
          script.onreadystatechange = () => {
            if (script.readyState === 'loaded' || script.readyState === 'complete') {
              script.onreadystatechange = null;
              this.scripts[name].loaded = true;
              resolve({ script: name, loaded: true, status: 'Loaded' });
            }
          };
        } else {
          // Others
          script.onload = () => {
            this.scripts[name].loaded = true;
            resolve({ script: name, loaded: true, status: 'Loaded' });
          };
        }
        script.onerror = (error: any) =>
          resolve({ script: name, loaded: false, status: 'Loaded' });
        document.getElementsByTagName('head')[0].appendChild(script);
      }
    });
  }
}
