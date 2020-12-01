//tag::copyright[]
/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
// end::copyright[]
package io.openliberty.guides.frontend.client;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.HeaderParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

// tag::systemClient[]
@RegisterRestClient(baseUri = "https://localhost:8443/system")
@Path("/properties")
@RequestScoped
public interface SystemClient extends AutoCloseable{
 
    @GET
    @Path("/os")
    @Produces(MediaType.APPLICATION_JSON)
    // tag::headerParam1[]
    public String getOS(@HeaderParam("Authorization") String authHeader);
    // end::headerParam1[]

    @GET
    @Path("/username")
    @Produces(MediaType.APPLICATION_JSON)
    // tag::headerParam2[]
    public String getUsername(@HeaderParam("Authorization") String authHeader);
    // end::headerParam2[]
    
    @GET
    @Path("/jwtroles")
    @Produces(MediaType.APPLICATION_JSON)
    // tag::headerParam3[]
    public String getJwtRoles(@HeaderParam("Authorization") String authHeader);
    // end::headerParam3[]
}
// end::systemClient[]