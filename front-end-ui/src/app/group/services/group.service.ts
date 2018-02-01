/*******************************************************************************
* Copyright (c) 2017 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/
import 'rxjs/add/operator/map';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';

/**
 * Perform operations with the group backend microservice.
 */
@Injectable()
export class GroupService {

    // Maven fills in these variables from the pom.xml
    private groupsUrl = 'https://${group.hostname}:${group.https.port}/groups/';

  constructor(private http: HttpClient) {
      if (sessionStorage.jwt == null) {
          console.log('JSON Web Token is not available. Login before you continue.');
      }
  }

  getGroups(): Observable<string> {
      let headers = new HttpHeaders();
      headers = headers.set('Authorization', sessionStorage.jwt);

      return this.http.get(this.groupsUrl + 'prop', { headers: headers })
      .map(data => data);
  }

  getPropToken(): Observable<HttpResponse<any>> {
      return this.http.get<HttpResponse<any>>(this.groupsUrl + 'prop', { observe: 'response'}).map(data => data);
  }
}
