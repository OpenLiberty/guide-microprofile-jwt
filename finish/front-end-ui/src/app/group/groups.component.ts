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
import { Component, OnInit } from '@angular/core';
import { GroupService } from './services/group.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { User } from '../user/user';
import { UserService } from '../user/services/user.service';
import { HttpErrorResponse, HttpResponse} from '@angular/common/http';

@Component({
    selector: 'app-groups',
    templateUrl: './groups.component.html',
    providers: [GroupService, UserService]
})
export class GroupsComponent implements OnInit {
    MSG_USER_NOT_RETRIEVED = 'User information could not be retrieved.';
    MSG_USER_ID_INVALID = 'Your session has become invalid. Please login again.';
    MSG_RC_ERR_CLIENT_NETWORK = 'Network connectivity or client error';


    userId = '';
    user: User = new User('', '', '', '', '', '');
    eventMessageError: string = null;
    content: string = null;

    constructor(private route: ActivatedRoute,
        private groupService: GroupService,
        private userService: UserService,
        private router: Router) {}

    ngOnInit() {
        this.route.paramMap.subscribe(params => {
            this.userId = params.get('userId');

            // If we did not receive a user id, the JWT token is, most likely, expired.
            if (this.userId === '' || this.userId === undefined || this.userId === null) {
                this.eventMessageError = this.MSG_USER_ID_INVALID;
                return;
            }

            // Get the current user's data.
            this.userService.getUser(this.userId).subscribe(resp => {
                this.user = resp;
                sessionStorage.userName = this.user.userName;
            }, err => {
                // Report the error and stay on the same page.
                sessionStorage.userName = '';
                this.eventMessageError = this.MSG_USER_NOT_RETRIEVED;
            });

          // this.getToken();
        });
    }

    onCloseEventErrorBox() {
        this.eventMessageError = null;
    }

    onBla(): string {
      // this.groupService.getGroups().subscribe(response => {
      //     // return response;
      //     return "placeholder here";
      // }, err => {
      //      this.eventMessageError = 'An error occurred obtaining the groups from the server.';
      // });
      // return this.eventMessageError; //this.content

      return "placeholderhere";
    }

    getToken() {
      this.groupService.getPropToken().subscribe((res2: HttpResponse<any>) => {
          this.content = res2.headers.get('proptoken');
        }, (err: HttpErrorResponse) => {
            this.eventMessageError = 'An error occurred obtaining the groups from the server.';
        });
    }

}
