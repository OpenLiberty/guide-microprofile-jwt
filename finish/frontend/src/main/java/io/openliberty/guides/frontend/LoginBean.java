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
package io.openliberty.guides.frontend;

import java.util.Set;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.Claims;

import io.openliberty.guides.frontend.util.SessionUtils;

// tag::loginBean[]
@ApplicationScoped
@Named
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

    // tag::doLogin[]
    public String doLogin() throws Exception {
        HttpServletRequest request = SessionUtils.getRequest();
        
        try {
            request.logout();
            request.login(this.username, this.password);
        } catch (ServletException e) {
            System.out.println("Login failed.");
            return "error.jsf";
        }

        String remoteUser = request.getRemoteUser();
        Set<String> roles = getRoles(request);
        if (remoteUser != null && remoteUser.equals(username)){
            String jwt = buildJwt(username, roles);
            HttpSession ses = request.getSession();
            if (ses == null) {
                System.out.println("Session is timeout.");
            } else {
                // tag::setAttribute[]
                ses.setAttribute("jwt", jwt);
                // end::setAttribute[]
            }
        } else {
            System.out.println("Update Sessional JWT Failed.");
        }
        return "application.jsf?faces-redirect=true";
    }
    // end::doLogin[]
    // tag::buildJwt[]
    
  private String buildJwt(String userName, Set<String> roles) throws Exception {
        // tag::jwtBuilder[]
        return JwtBuilder.create("jwtFrontEndBuilder")
        // end::jwtBuilder[]
                         .claim(Claims.SUBJECT, userName)
                         .claim("upn", userName)
                         // tag::groups[]
                         .claim("groups", roles.toArray(new String[roles.size()])) 
                         // end::groups[]
                         .buildJwt()
                         .compact();
        
    }
    // end::buildJwt[]

    private Set<String> getRoles(HttpServletRequest request) {
        Set<String> roles = new HashSet<String>();
        boolean isAdmin = request.isUserInRole("admin");
        boolean isUser = request.isUserInRole("user");
        if (isAdmin) { roles.add("admin");}
        if (isUser) { roles.add("user");}
        return roles;
    }
}
// end::loginBean[]
