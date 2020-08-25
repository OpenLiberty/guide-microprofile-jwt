// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.system;

import javax.json.JsonArray;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.annotation.security.RolesAllowed;

import org.eclipse.microprofile.jwt.Claim;

@RequestScoped
@Path("/properties")
public class SystemResource {

  @Inject
  // tag::claim
  @Claim("groups")
  // end::claim
  // tag::rolesArray
  private JsonArray roles;
  // end::rolesArray
  @GET
  // tag::usernameEndpoint
  @Path("/username")
  // end::usernameEndpoint
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowedAdminUser[]
  @RolesAllowed({ "admin", "user" })
  // end::rolesAllowedAdminUser[]
  public String getUsername() {
    return System.getProperties().getProperty("user.name");
  }

  @GET
  // tag::osEndpoint
  @Path("/os")
  // end::osEndpoint
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowedAdmin[]
  @RolesAllowed({ "admin" })
  // end::rolesAllowedAdmin[]
  public String getOS() {
    return System.getProperties().getProperty("os.name");
  }

  @GET
  // tag::rolesEndpoint
  @Path("/jwtroles")
  // end::rolesEndpoint
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({ "admin", "user" })
  public String getRoles() {
    return roles.toString();
  }
}
