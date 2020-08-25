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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.openliberty.guides.ui.util.SessionUtils;
import io.openliberty.guides.ui.client.SystemClient;

import org.eclipse.microprofile.rest.client.inject.RestClient;


@ApplicationScoped
@Named
public class ApplicationBean{

  @Inject
  @RestClient
  private SystemClient defaultRestClient;

  public String getJwt() {
    String jwtTokenString = SessionUtils.getJwtToken();
    String authHeader = "Bearer " + jwtTokenString;
    return authHeader;
  }

  public String getOs() {
    String authHeader = getJwt();
    String os;
    try {
      os = defaultRestClient.getOS(authHeader);
    } catch(Exception e) {
      System.out.println(e);
      return "User does not have access to OS property";
    }
    return os;
  }

    public String getUsername() {
    String authHeader = getJwt();
    System.out.println(defaultRestClient);
    return defaultRestClient.getUsername(authHeader);
  }

  public String getJwtRoles() {
    String authHeader = getJwt();
    String jwtRoles = defaultRestClient.getJwtRoles(authHeader);
    return jwtRoles;
  }
}
// end::jwt[]
