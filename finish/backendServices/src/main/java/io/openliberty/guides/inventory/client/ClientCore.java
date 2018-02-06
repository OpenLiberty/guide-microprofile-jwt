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
package io.openliberty.guides.inventory.client;

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

public class ClientCore {

  /**
   * <p>Creates a JAX-RS client that retrieves the JVM system properties</p>
   */
    public static JsonObject getContent(String url, String authHeader) {
        Response response = processRequest(url, "GET", null, authHeader);
        return toJsonObj(response.readEntity(String.class));
    }

    /**
     * <p>Returns whether or not a particular host is running the system service </p>
     */
    public static boolean isResponseOk(String url, String authHeader) {
          Response response = processRequest(url, "GET", null, authHeader);
          return (response.getStatus() != Status.OK.getStatusCode()) ? false : true;
    }

    /**
     * <p>Builds the URI string to the system service for a particular host. This is just a helper method.</p>
     * @param protocol - http or https.
     * @param host - name of host.
     * @param port - port number.
     * @param path - Note that the path needs to start with a slash!!!
     * @return String representation of the URI to the system properties service.
     */
    public static String buildUriString(String protocol, String host, int port, String path){
        String auth = null;
        String query = null;
        String fragment = null;
        try {
          URI uri =  new URI(protocol, auth, host, port, path, query, fragment);
          return uri.toString();
        } catch (Exception e){
          System.out.println("URISyntaxException");
        }
        return "";
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
