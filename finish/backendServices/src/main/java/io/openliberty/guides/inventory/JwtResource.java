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
package io.openliberty.guides.inventory;

import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
// tag::JsonWebTokenImport[]
import org.eclipse.microprofile.jwt.JsonWebToken;
// end::JsonWebTokenImport[]
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.SecurityContext;
// tag::javaSecurity[]
import java.security.Principal;
// end::javaSecurity[]

@RequestScoped
@Path("jwt")
public class JwtResource {
  // The JWT of the current caller. Since this is a request scoped resource, the
  // JWT will be injected for each JAX-RS request. The injection is performed by
  // the mpJwt-1.0 feature.
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
  public Response getJwtGroups(@Context SecurityContext securityContext) {
    Set<String> groups = null;
    // tag::securityContext[]
    Principal user = securityContext.getUserPrincipal();
    // end::securityContext[]
    if (user instanceof JsonWebToken) {
      JsonWebToken jwt = (JsonWebToken) user;
      // tag::groups[]
      groups = jwt.getGroups();
      // end::groups[]
    }
    return Response.ok(groups.toString()).build();
  }
  // end::getJwtGroups[]

  @GET
  @RolesAllowed({ "admin", "user" })
  @Path("/customClaim")
  // tag::getCustomClaim[]
  public Response getCustomClaim(@Context SecurityContext securityContext) {
    // tag::isUserInRole[]
    if (securityContext.isUserInRole("admin")) {
    // end::isUserInRole[]
      // tag::customClaim[]
      String customClaim = jwtPrincipal.getClaim("customClaim");
      // end::customClaim[]
      return Response.ok(customClaim).build();
    }
    // tag::responseStatus[]
    return Response.status(Response.Status.FORBIDDEN).build();
    // end::responseStatus[]
  }
  // end::getCustomClaim[]
}
// end::jwt[]
