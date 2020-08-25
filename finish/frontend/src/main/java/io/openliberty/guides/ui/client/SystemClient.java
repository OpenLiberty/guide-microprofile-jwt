package io.openliberty.guides.ui.client;

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
    public String getOS(@HeaderParam("Authorization") String authorization);
    // end::headerParam1[]

    @GET
    @Path("/username")
    @Produces(MediaType.APPLICATION_JSON)
    // tag::headerParam2[]
    public String getUsername(@HeaderParam("Authorization") String authorization);
    // end::headerParam2[]
    @GET
    @Path("/jwtroles")
    @Produces(MediaType.APPLICATION_JSON)
    // tag::headerParam2[]
    public String getJwtRoles(@HeaderParam("Authorization") String authorization);
    // end::headerParam2[]
}
// end::systemClient[]