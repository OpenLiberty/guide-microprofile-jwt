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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.annotation.security.RolesAllowed;

import org.eclipse.microprofile.jwt.Claim;

@RequestScoped
@Path("/properties")
public class SystemResource {

  @Claim("groups")
  private JsonArray roles;

  @GET
  @Path("/os")
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowed[]
  @RolesAllowed({ "admin", "user" })
  // end::rolesAllowed[]
  public String getOS() {
    return System.getProperties().getProperty("os.name");
  }

  @GET
  @Path("/username")
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowed[]
  @RolesAllowed({ "admin", "user" })
  // end::rolesAllowed[]
  public String getUsername() {
    System.out.println("SystemResource/getUsername");
    return System.getProperties().getProperty("user.name");
  }

  @GET
  @Path("/jwtroles")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({ "admin", "user" })
  public String getRoles() {
    return roles.toString();
  }
}
