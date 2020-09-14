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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.openliberty.guides.frontend.client.SystemClient;
import io.openliberty.guides.frontend.util.SessionUtils;


@ApplicationScoped
@Named
public class ApplicationBean { 

    // tag::restClient[]
    @Inject
    @RestClient
    private SystemClient defaultRestClient;
    // end::restClient[]

    // tag::getJwt[]
    public String getJwt() {
        String jwtTokenString = SessionUtils.getJwtToken();
        String authHeader = "Bearer " + jwtTokenString;
        return authHeader;
    }
    // end::getJwt[]
    
    // tag::getOs[]
    public String getOs() {
        // tag::authHeader1[]
        String authHeader = getJwt();
        // end::authHeader1[]
        String os;
        try {
            os = defaultRestClient.getOS(authHeader);
        } catch(Exception e) {
            return "You are not authorized to access this system property";
        }
        return os;
    }
    // end::getOs[]

    // tag::getUsername[]
    public String getUsername() {
        // tag::authHeader2[]
        String authHeader = getJwt();
        // end::authHeader2[]
        return defaultRestClient.getUsername(authHeader);
    }
    // end::getUsername[]

    // tag::getJwtRoles[]
    public String getJwtRoles() {
        // tag::authHeader3[]
        String authHeader = getJwt();
        // end::authHeader3[]
        String jwtRoles = defaultRestClient.getJwtRoles(authHeader);
        return jwtRoles;
    }
    // end::getJwtRoles[]

}