//tag::comment[]
/*******************************************************************************
* Copyright (c) 2017, 2020 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
// end::comment[]
package it.io.openliberty.guides.system;

import java.util.Set;
import java.util.HashSet;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.Test;
import com.ibm.websphere.security.jwt.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemEndpointIT {

    String authHeaderAdmin = "Bearer eyJraWQiOiJZVU9RVHJQRHVuTlEyQUYxSWdRbiIsInR5cCI6IkpXVCIsImFsZyI6IlJTMjU2In0.eyJ0b2tlbl90eXBlIjoiQmVhcmVyIiwic3ViIjoiYm9iIiwidXBuIjoiYm9iIiwiZ3JvdXBzIjpbImFkbWluIiwidXNlciJdLCJpc3MiOiJodHRwOi8vb3BlbmxpYmVydHkuaW8iLCJleHAiOjM3NTk5MTUzMDk2LCJpYXQiOjE1OTkxNTY2OTZ9.iDSwkCj_hMa3uWCa77oDbge34nTDpW4fvFWnLdA6Ai0dVloAZwP7DXHNBHGfb3_ezNBOYb3-B4Ah-yIyo8eXFZ_y65YHQnGByMrR3RIQrO3RHG_7hyZmU8gOabATIe_9g1xj8c11wJrUwXkqC78wU8-Ou-u50OpP27Ox4RMBi2b9nwCHD1Chk8xh5LWmMoQbp081Uf73zN-3YUbNpzLxZP9ZQ98DygwLneT_numbHXzbzqA0bLcry5grkBb8KSfd57cK4zUWY49523Qd5fsPLqw3rLQTYi17H438waF83dsuF9cjdimgRDlcqdiPqkRmbwsIbewjsmDgUwNYOUB1Hw";
    String authHeaderUser = "Bearer eyJraWQiOiJZVU9RVHJQRHVuTlEyQUYxSWdRbiIsInR5cCI6IkpXVCIsImFsZyI6IlJTMjU2In0.eyJ0b2tlbl90eXBlIjoiQmVhcmVyIiwic3ViIjoiYWxpY2UiLCJ1cG4iOiJhbGljZSIsImdyb3VwcyI6WyJ1c2VyIl0sImlzcyI6Imh0dHA6Ly9vcGVubGliZXJ0eS5pbyIsImV4cCI6Mzc1OTkxNTMxMjIsImlhdCI6MTU5OTE1NjcyMn0.GcRHU-IAhdwo3L1cwLytvrI0WVq8l-WUuYHnGq1lDV5i_-yexcE3xFbNscjVhPz9yvo58L40t6_vY93NmDOQo-uYGhBJ9fcJBNYGfzIW8-1GvAHbm_7246y4L0_KXfeoqKtiDdk_1pJpKka81CIQj3lYRrRXZWKFY6VYEppmZu-DkvQ2jCR7fCxvv475aVHWTwKNnablirJHlhEafbBSZaDQIOgOUaBtSKRUrQP-PFXobnmlhgkEyh1I8qp6X7v9LOONymo8YMp5_124rQy-JOT2bbHmspcZy_ycGnjcwUQPdCpKVze_moJ9a5f6cUyXaESi4JqI9ku4ULSTR_rz5A";

    String urlOS = "http://localhost:8080/system/properties/os";
    String urlUsername = "http://localhost:8080/system/properties/username";
    String urlRoles = "http://localhost:8080/system/properties/jwtroles";

    @Test 
    public void testSecureEndpoint() {
        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
        Response response = client.target(urlOS).request().get();
        assertEquals(401, response.getStatus(), "Unauthorized access granted at " + urlOS);
        response.close();
    }

    @Test
    public void testOSEndpoint() {
        Response response = makeRequest(urlOS, authHeaderAdmin);
        assertEquals(200, response.getStatus(), "Incorrect response code from " + urlOS);
        assertEquals(System.getProperty("os.name"), response.readEntity(String.class), "The system property for the local and remote JVM should match");

        response = makeRequest(urlOS, authHeaderUser);
        assertEquals(403, response.getStatus(), "Incorrect response code from " + urlOS);

        response.close();
    }

    @Test
    public void testUsernameEndpoint() {
        Response response = makeRequest(urlUsername, authHeaderAdmin);
        assertEquals(200, response.getStatus(), "Incorrect response code from " + urlUsername);

        response = makeRequest(urlUsername, authHeaderUser);
        assertEquals(200, response.getStatus(), "Incorrect response code from " + urlUsername);

        response.close();
    }

    @Test
    public void testRolesEndpoint() {
        Response response = makeRequest(urlRoles, authHeaderAdmin);
        assertEquals(200, response.getStatus(), "Incorrect response code from " + urlRoles);
        assertEquals("[\"admin\",\"user\"]", response.readEntity(String.class), "Token groups claim incorrect " + urlRoles);

        response = makeRequest(urlRoles, authHeaderUser);
        assertEquals(200, response.getStatus(), "Incorrect response code from " + urlRoles);
        assertEquals("[\"user\"]", response.readEntity(String.class), "Token groups claim incorrect " + urlRoles);

        response.close();
    }

    private Response makeRequest(String url, String authHeader) {
        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
        Builder builder = client.target(url).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        builder.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response response = builder.get();
        return response;
    }
    
}