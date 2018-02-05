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
package io.openliberty.guides.inventory.util;

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

public class InventoryUtil {

    // Constants for building URI to the system service.
    private static final int DEFAULT_PORT = 5051;
    private static final String SYSTEM_PROPERTIES = "/system/properties";

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

    /**
     * <p>Retrieves the JVM system properties of a particular host.</p>
     *
     * <p>NOTE: the host must expose its JVM's system properties via the
     * system service; ie. the system service must be running on that host.</p>
     *
     * @param hostname - name of host.
     * @return JSON Java object containing the system properties of the host's JVM.
     */
    public static JsonObject getProperties(String hostname, String authHeader) {
        return getPropertiesHelper(hostname, DEFAULT_PORT, authHeader);
    }

    /**
     * <p>Retrieves the JVM system properties of a particular host for the given port number.</p>
     *
     * <p>NOTE: the host must expose its JVM's system properties via the
     * system service; ie. the system service must be running on that host.</p>
     *
     * @param hostname - name of host.
     * @param port     - port number for the system service.
     * @return JSON Java object containing the system properties of the host's JVM.
     */
    public static JsonObject getProperties(String hostname, int port, String authHeader) {
        return getPropertiesHelper(hostname, port, authHeader);
    }

    /**
     * <p>Creates a JAX-RS client that retrieves the JVM system properties for the particular host
     * on the given port number.</p>
     */
    private static JsonObject getPropertiesHelper(String hostname, int port, String authHeader) {
        // Client client = ClientBuilder.newClient();
        // URI propURI = InventoryUtil.buildUri(hostname, port);
        // return client.target(propURI).request().get(JsonObject.class);

        // String authHeader =  "Bearer "  + new JWTVerifier().createJWT("TESTUSER");

        // Get system properties by using JWT token
        String propUrl = "https://" + hostname + ":" + port + SYSTEM_PROPERTIES;
        Response propResponse = processRequest(propUrl, "GET", null, authHeader);

        JsonObject responseJson = toJsonObj(propResponse.readEntity(String.class));
        System.out.println(responseJson.getString("os.name"));
        return responseJson;
    }

    /**
     * <p>Returns whether or not a particular host is exposing its JVM's system properties.
     * In other words, returns whether or not the system service is running on a
     * particular host.</p>
     *
     * @param hostname - name of host.
     * @return true if the host is currently running the system service and false otherwise.
     */
    public static boolean responseOk(String hostname, String authHeader) {
        return responseOkHelper(hostname, DEFAULT_PORT, authHeader);
    }

    /**
     * <p>Returns whether or not a particular host is exposing its JVM's system properties on the
     * given port number. In other words, returns whether or not the system service is running on a
     * particular host on the given port number.</p>
     *
     * @param hostname - name of host.
     * @param port     - port number.
     * @return true if the host is currently running the system service and false otherwise.
     */
    public static boolean responseOk(String hostname, int port, String authHeader) {
        return responseOkHelper(hostname, port, authHeader);
    }

    /**
     * <p>Returns whether or not a particular host is running the system service on the
     * given port number.</p>
     */
    private static boolean responseOkHelper(String hostname, int port, String authHeader) {
        // try {
        //     URL target = new URL(buildUri(hostname, port).toString());
        //     HttpURLConnection http = (HttpURLConnection) target.openConnection();
        //     http.setConnectTimeout(50);
        //     int response = http.getResponseCode();
        //     return (response != 200) ? false : true;
        // } catch (Exception e) {
        //     return false;
        // }

          // Get system properties by using JWT token
          String propUrl = "https://"
              + hostname
              + ":"
              + port + SYSTEM_PROPERTIES;
          Response propResponse = processRequest(propUrl, "GET", null, authHeader);

          return (propResponse.getStatus() != Status.OK.getStatusCode()) ? false : true;

    }
}
