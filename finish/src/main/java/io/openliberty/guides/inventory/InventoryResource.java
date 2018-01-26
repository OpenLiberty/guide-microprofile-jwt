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

// CDI
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

// JSON-P
import javax.json.JsonObject;

// JAX-RS
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import io.jsonwebtoken.*;
import com.ibm.websphere.security.jwt.*;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.security.Principal;

@RequestScoped
@Path("hosts")
public class InventoryResource {

    @Inject InventoryManager manager;

    @Context
	  SecurityContext secCon;

  	@Context
  	HttpServletRequest request;

    private JsonWebToken jwtPrincipal;

    @GET
    @Path("{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getPropertiesForHost(@PathParam("hostname") String hostname) {
        return manager.get(hostname);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject listContents() {
        System.out.println(request.getHeader("user-agent"));
        System.out.println(request.getHeader("jwt"));
        // System.out.println(this.jwtPrincipal.getSubject());
       //consumeJWT(request.getHeader("jwt"));
        return manager.list();
    }


       @GET
       @Path("/getInjectedPrincipal")
       public String getInjectedJWT() {
          return  this.jwtPrincipal.getSubject();
       }

    // private void consumeJWT(String token) {
    //
    //    try {
  	// 		JwtConsumer jwtConsumer = JwtConsumer.create("jwtConsumer");
  	// 		JwtToken access_Token =  jwtConsumer.createJwt(token);
  	// 		String name = access_Token.getClaims().getClaim("username", String.class);
    //     System.out.println("JWT  Consumer " + name);
  	// 	} catch (InvalidConsumerException | InvalidTokenException e1) {
  	// 		e1.printStackTrace();
  	// 		//e1.printStackTrace(out);
  	// 	}
    // }

}
