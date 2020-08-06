// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
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
package io.openliberty.guides.inventory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
// tag::jsonWebToken[]
import org.eclipse.microprofile.jwt.JsonWebToken;
// end::jsonWebToken[]
import javax.annotation.security.RolesAllowed;


@RequestScoped
@Path("jwt")
public class JwtResource {
  @Inject
  private JsonWebToken jwtPrincipal;

  @GET
  @RolesAllowed({ "admin", "user" })
  @Path("/username")
  public Response getJwtUsername() {
    // tag::getName[]
    return Response.ok(this.jwtPrincipal.getName()).build();
    // end::getName[]
  }

  @GET
  @RolesAllowed({ "admin", "user" })
  @Path("/groups")
  public Response getJwtGroups() {
    // tag::getGroups[]
    return Response.ok(this.jwtPrincipal.getGroups().toString()).build();
    // end::getGroups[]
  }
}
