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
package io.openliberty.guides.ui.util;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import javax.json.Json;
import javax.json.JsonArray;
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

public class ServiceUtils {

    // Constants for building URI to the system service.
    private static final String DEFAULT_PORT = "5051";
    private static final String PROTOCOL = "https";
    private static final String SYSTEM_PROPERTIES = "/system/properties";
    private static final String HOSTNAME = "localhost";

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
     * <p>Creates a JAX-RS client that retrieves the JVM system properties for the particular host
     * on the given port number.</p>
     */
    public static JsonObject getPropertiesHelper(String authHeader) {

        // Get system properties by using JWT token
        String propUrl = "https://"
            + HOSTNAME
            + ":"
            + DEFAULT_PORT + SYSTEM_PROPERTIES;
        Response propResponse = processRequest(propUrl, "GET", null, authHeader);

        JsonObject responseJson = toJsonObj(propResponse.readEntity(String.class));
        System.out.println(responseJson.getString("os.name"));
        return responseJson;
    }


    /**
     * <p>Returns whether or not a particular host is running the system service on the
     * given port number.</p>
     */
    public static boolean responseOkHelper(String authHeader) {
          // Get system properties by using JWT token
          String propUrl = "https://"
              + HOSTNAME
              + ":"
              + DEFAULT_PORT + SYSTEM_PROPERTIES;
          Response propResponse = processRequest(propUrl, "GET", null, authHeader);

          return (propResponse.getStatus() != Status.OK.getStatusCode()) ? false : true;

    }



}
