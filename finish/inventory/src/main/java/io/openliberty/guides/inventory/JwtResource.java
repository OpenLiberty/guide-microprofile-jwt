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
// tag::JsonWebTokenImport[]
import org.eclipse.microprofile.jwt.JsonWebToken;
// end::JsonWebTokenImport[]
import javax.annotation.security.RolesAllowed;


@RequestScoped
@Path("jwt")
public class JwtResource {
  // The JWT of the current caller. Since this is a request scoped resource, the
  // JWT will be injected for each JAX-RS request. The injection is performed by
  // the mpJwt-1.1 feature.
  @Inject
  // tag::JsonWebToken[]
  private JsonWebToken jwtPrincipal;
  // end::JsonWebToken[]

  @GET
  @RolesAllowed({ "admin", "user" })
  @Path("/username")
  // tag::getJwtUsername[]
  public Response getJwtUsername() {
    // tag::getName[]
    return Response.ok(this.jwtPrincipal.getName()).build();
    // end::getName[]
  }
  // end::getJwtUsername[]

  @GET
  @RolesAllowed({ "admin", "user" })
  @Path("/groups")
  // tag::getJwtGroups[]
  public Response getJwtGroups() {
    return Response.ok(this.jwtPrincipal.getGroups().toString()).build();
  }
  // end::getJwtGroups[]
}
