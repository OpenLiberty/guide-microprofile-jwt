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

import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.JwtBuilder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Context;
import org.eclipse.microprofile.jwt.JsonWebToken;

// tag::RequestScoped[]
@RequestScoped
// end::RequestScoped[]
@Path("hosts")
public class InventoryResource {

    // tag::Inject[]
    @Inject InventoryManager manager;
    // end::Inject[]

    /**
     * The JWT of the current caller. Since this is a request scoped resource, the JWT will be
     * injected for each JAX-RS request. The injection is performed by the mpJwt-1.0 feature.
     */
    @Inject private JsonWebToken jwtPrincipal;

    // @GET
    // @Path("{hostname}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public JsonObject getPropertiesForHost(@PathParam("hostname") String hostname) {
    //     return manager.get(hostname);
    // }
    //
    // @GET
    // @Produces(MediaType.APPLICATION_JSON)
    // public JsonObject listContents() {
    //     return manager.list();
    // }

    @GET
    @Path("{hostname}")
    @Produces("application/json")
    public Response getPropertiesForHost(@PathParam("hostname") String hostname, @Context HttpHeaders httpHeaders) {
      // Validate the JWT. The JWT must be in the 'users' group.
      try {
        validateJWT(new HashSet<String>(Arrays.asList("users")));
      } catch (JWTException jwte) {
        return Response.status(Status.UNAUTHORIZED)
            .type(MediaType.TEXT_PLAIN)
            .entity(jwte.getMessage())
            .build();
      }
      String authHeader = httpHeaders.getRequestHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      return Response.ok(manager.get(hostname, authHeader), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces("application/json")
    public Response listContents() {
      // Validate the JWT. The JWT must be in the 'users' group.
      try {
        validateJWT(new HashSet<String>(Arrays.asList("users")));
      } catch (JWTException jwte) {
        return Response.status(Status.UNAUTHORIZED)
            .type(MediaType.TEXT_PLAIN)
            .entity(jwte.getMessage())
            .build();
      }

      return Response.ok(manager.list(), MediaType.APPLICATION_JSON).build();
    }



  /** Do some basic checks on the JWT, until the MP-JWT annotations are ready. */
  private void validateJWT(Set<String> validGroups) throws JWTException {
    // Make sure the authorization header was present. This check is somewhat
    // silly since the jwtPrincipal will never actually be null since it's a
    // WELD proxy (injected).
    if (jwtPrincipal == null) {
      throw new JWTException("No authorization header or unable to inflate JWT");
    }

    // Make sure we're in one of the groups that is authorized.
    String validatedGroupName = null;
    Set<String> groups = jwtPrincipal.getGroups();
    if (groups != null) {
      for (String group : groups) {
        if (validGroups.contains(group)) {
          validatedGroupName = group;
          break;
        }
      }
    }

    if (validatedGroupName == null) {
      throw new JWTException("User is not in a valid group [" + groups.toString() + "]");
    }
  }

  private static class JWTException extends Exception {
    private static final long serialVersionUID = 423763L;

    public JWTException(String message) {
      super(message);
    }
  }
}
