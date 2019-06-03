// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
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

@RequestScoped
@Path("systems")
public class InventoryResource {

  @Inject
  InventoryManager manager;

  @GET
  // tag::RolesAllowed[]
  @RolesAllowed({ "admin", "user" })
  // end::RolesAllowed[]
  @Path("{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  // tag::getPropertiesForHost[]
  public Response getPropertiesForHost(@PathParam("hostname") String hostname,
      @Context HttpHeaders httpHeaders) {
    String authHeader = httpHeaders.getRequestHeaders()
                                   .getFirst(HttpHeaders.AUTHORIZATION);
    // Get properties
    // tag::managerGet[]
    Properties props = manager.get(hostname, authHeader);
    // end::managerGet[]
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity(
                         "ERROR: Unknown hostname or the resource"
                         + "may not be running on the host machine")
                         
                     .build();
    }

    // Add to inventory
    manager.add(hostname, props);
    return Response.ok(props).build();
  }
  // end::getPropertiesForHost[]

  @GET
  // tag::RolesAllowed2[]
  @RolesAllowed({ "admin" })
  // end::RolesAllowed2[]
  @Produces(MediaType.APPLICATION_JSON)
  public InventoryList listContents() {
    return manager.list();
  }
}
// end::jwt[]
