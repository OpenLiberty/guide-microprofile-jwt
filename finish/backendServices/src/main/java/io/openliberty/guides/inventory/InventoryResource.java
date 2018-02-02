// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::copyright[]
package io.openliberty.guides.inventory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Context;
import org.eclipse.microprofile.jwt.JsonWebToken;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;


// tag::RequestScoped[]
@RequestScoped
// end::RequestScoped[]
@DeclareRoles({"admin", "user"})
@Path("hosts")
public class InventoryResource {

    // tag::Inject[]
    @Inject InventoryManager manager;
    // end::Inject[]

    @GET
    @RolesAllowed({"admin", "user"})
    @Path("{hostname}")
    @Produces("application/json")
    public Response getPropertiesForHost(@PathParam("hostname") String hostname, @Context HttpHeaders httpHeaders) {

      String authHeader = httpHeaders.getRequestHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      return Response.ok(manager.get(hostname, authHeader), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @RolesAllowed({"admin", "user"})
    @Produces("application/json")
    public Response listContents() {

      return Response.ok(manager.list(), MediaType.APPLICATION_JSON).build();
    }

}
