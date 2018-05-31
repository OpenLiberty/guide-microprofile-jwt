// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::jwt[]
package io.openliberty.guides.ui;

import java.util.Set;
import java.util.HashSet;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ibm.websphere.security.jwt.*;
import io.openliberty.guides.ui.util.SessionUtils;

@ManagedBean
@ViewScoped
public class LoginBean {

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String doLogin() throws Exception {
        HttpServletRequest request = SessionUtils.getRequest();

        // do login
        try {
            request.logout();
            request.login(this.username, this.password);
        } catch (ServletException e) {
            System.out.println("Login failed.");
            return "error.jsf";
        }

        // to get remote user using getRemoteUser()
        String remoteUser = request.getRemoteUser();
        Set<String> roles = getRoles(request);


        // update session
        if (remoteUser != null && remoteUser.equals(username)){

            String jwt = buildJwt(username, roles);
            // get the current session
            HttpSession ses = request.getSession();
            if (ses == null) {
                System.out.println("Session is timeout.");
            } else {
                ses.setAttribute("jwt", jwt);
            }
        } else {
            System.out.println("Update Sessional JWT Failed.");
        }
        return "application.jsf";
    }

  private String buildJwt(String userName, Set<String> roles) throws Exception {
        return JwtBuilder.create("jwtFrontEndBuilder")
                         .claim(Claims.SUBJECT, userName)
                         .claim("upn", userName) // MP-JWT defined subject claim
                         .claim("groups", roles.toArray(new String[roles.size()])) // MP-JWT builds an array from this
                         .claim("customClaim", "customValue")
                         .buildJwt()
                         .compact();
    }

    private Set<String> getRoles(HttpServletRequest request) {
        Set<String> roles = new HashSet<String>();
        boolean isAdmin = request.isUserInRole("admin");
        boolean isUser = request.isUserInRole("user");
        if (isAdmin) { roles.add("admin");}
        if (isUser) { roles.add("user");}
        return roles;
    }
}
// end::jwt[]
