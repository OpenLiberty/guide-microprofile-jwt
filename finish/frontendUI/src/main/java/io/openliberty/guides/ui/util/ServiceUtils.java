// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
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
package io.openliberty.guides.ui.util;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;

public class ServiceUtils {
    
    // Constants for building URI to the system service.
    private static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("backend.https.port"));
    private static final String HOSTNAME = System.getProperty("backend.hostname");
    private static final String SECURED_PROTOCOL = "https";
    private static final String SYSTEM_PROPERTIES = "/system/properties";
    private static final String INVENTORY_HOSTS = "/inventory/systems";

    // tag::doc[]
    /**
     * <p>Creates a JAX-RS client that retrieves the JVM system properties for the particular host
     * on the given port number.</p>
     */
    // end::doc[]
    public static JsonObject getPropertiesHelper(String authHeader) {
        // Get system properties by using JWT token
        String propUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT, SYSTEM_PROPERTIES);
        Response propResponse = processRequest(propUrl, "GET", null, authHeader);

        JsonObject responseJson = toJsonObj(propResponse.readEntity(String.class));
        // System.out.println(responseJson.getString("os.name"));
        return responseJson;
    }

    public static JsonObject getInventoryHelper(String authHeader) {
        // Get system properties by using JWT token
        String invUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT, INVENTORY_HOSTS);
        Response invResponse = processRequest(invUrl, "GET", null, authHeader);

        JsonObject responseJson = toJsonObj(invResponse.readEntity(String.class));
        // System.out.println(responseJson.getString("os.name"));
        return responseJson;
    }

    // tag::doc[]
    /**
     * <p>Returns whether or not a particular host is running the system service on the
     * given port number.</p>
     */
    // end::doc[]
    public static boolean responseOkHelper(String authHeader) {
        // Get system properties by using JWT token
        String propUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT, SYSTEM_PROPERTIES);
        Response propResponse = processRequest(propUrl, "GET", null, authHeader);

        return (propResponse.getStatus() != Status.OK.getStatusCode()) ? false : true;
    }

    public static boolean invOkHelper(String authHeader) {
        // Get system properties by using JWT token
        String propUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT, INVENTORY_HOSTS);
        Response propResponse = processRequest(propUrl, "GET", null, authHeader);

        return (propResponse.getStatus() != Status.OK.getStatusCode()) ? false : true;
    }

    public static String buildUrl(String protocol, String host, int port, String path) {
        try {
            URI uri = new URI(protocol, null, host, port, path, null, null);
            return uri.toString();
        } catch (Exception e) {
            System.out.println("URISyntaxException");
            return null;
        }
    }

    public static Response processRequest(String url, String method, String payload, String authHeader) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Builder builder = target.request();
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        if (authHeader != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authHeader);
        }
        return (payload != null)
            ? builder.build(method, Entity.json(payload)).invoke()
            : builder.build(method).invoke();
    }

    public static JsonObject toJsonObj(String json) {
        JsonReader jReader = Json.createReader(new StringReader(json));
            return jReader.readObject();
    }
}
// end::jwt[]
