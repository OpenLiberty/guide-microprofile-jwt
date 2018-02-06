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
package io.openliberty.guides.system;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.annotation.security.RolesAllowed;

@RequestScoped
@Path("properties")
public class SystemResource {

    @GET
    @RolesAllowed({"admin", "user"})
    @Produces("application/json")
    public Response getProperties() {

      JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
      System.getProperties()
            .entrySet()
            .stream()
            .forEach(entry -> responseBuilder.add((String)entry.getKey(),
                                          (String)entry.getValue()));

      return Response.ok(responseBuilder.build(), MediaType.APPLICATION_JSON).build();
    }
}
