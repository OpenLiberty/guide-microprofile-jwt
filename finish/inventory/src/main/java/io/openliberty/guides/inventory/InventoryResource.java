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
package io.openliberty.guides.inventory;

import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.openliberty.guides.inventory.model.InventoryList;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Context;
// tag::jsonWebToken[]
import org.eclipse.microprofile.jwt.JsonWebToken;
// end::jsonWebToken[]
@RequestScoped
@Path("/systems")
public class InventoryResource {

  @Inject
  InventoryManager manager;

  @Inject
  private JsonWebToken jwtPrincipal;

  @GET
  @Path("/add/{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowed[]
  @RolesAllowed({ "admin", "user" })
  // end::rolesAllowed[]
  public Response addPropertiesForHost(@PathParam("hostname") String hostname, @Context HttpHeaders httpHeaders) {
    // Get properties
    Properties props = manager.get(hostname);
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("{ \"error\" : \"Unknown hostname or the system service " 
                     + "may not be running on " + hostname + "\" }")
                     .build();
    }
    // Add to inventory
    manager.add(hostname, props);
    return Response.ok(props).build();
  }
  @GET
  @Path("/get/{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowed[]
  @RolesAllowed({ "admin", "user" })
  // end::rolesAllowed[]
  public Properties getPropertiesForHost(@PathParam("hostname") String hostname, @Context HttpHeaders httpHeaders) {
    // Get properties
    Properties props = manager.get(hostname);
    return props;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  // tag::rolesAllowed[]
  @RolesAllowed({ "admin"})
  // end::rolesAllowed[]
  public InventoryList listContents() {
    return manager.list();
  }

  // tag::getJwtUsername[]
  @GET
  @Path("/username")
  public Response getJwtUsername() {
    // tag::getName[]
    return Response.ok(this.jwtPrincipal.getName()).build();
    // end::getName[]
  }
  // end::getJwtUsername[]

  // tag::getJwtGroups[]
  @GET
  @Path("/groups")
  public Response getJwtGroups() {
    // tag::getGroups[]
    return Response.ok(this.jwtPrincipal.getGroups().toString()).build();
    // end::getGroups[]
  }
  // end::getJwtGroups[]

}
