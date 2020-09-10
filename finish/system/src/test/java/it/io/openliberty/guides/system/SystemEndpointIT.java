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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.io.openliberty.guides.system.utils.JwtBuilder;

public class SystemEndpointIT {

    static String authHeaderAdmin;
    static String authHeaderUser;

    String urlOS = "http://localhost:8080/system/properties/os";
    String urlUsername = "http://localhost:8080/system/properties/username";
    String urlRoles = "http://localhost:8080/system/properties/jwtroles";

    @BeforeAll
    private static void testJWT() throws Exception{
        authHeaderAdmin = "Bearer " + new JwtBuilder().createAdminJwt("testUser");
        authHeaderUser = "Bearer " + new JwtBuilder().createUserJwt("testUser");
    }

    @Test 
    public void testSecureEndpoint() {
        Client client = ClientBuilder.newClient();
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
        Builder builder = client.target(url).request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        builder.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response response = builder.get();
        return response;
    }
    
}