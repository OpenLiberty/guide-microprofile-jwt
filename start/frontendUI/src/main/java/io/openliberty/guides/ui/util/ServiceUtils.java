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
  private static final int DEFAULT_PORT = Integer.valueOf(
      System.getProperty("backend.https.port"));
  private static final String HOSTNAME = System.getProperty("backend.hostname");
  private static final String SECURED_PROTOCOL = "https";
  private static final String SYSTEM_PROPERTIES = "/system/properties";
  private static final String INVENTORY_HOSTS = "/inventory/systems";
  private static final String INV_JWT = "/inventory/jwt";

  public static JsonObject getProperties(String authHeader) {
    String propUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT,
        SYSTEM_PROPERTIES);
    return getJsonFromUrl(propUrl, authHeader);
  }

  public static JsonObject getInventory(String authHeader) {
    String invUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT,
        INVENTORY_HOSTS);
    return getJsonFromUrl(invUrl, authHeader);
  }

  public static JsonObject addSystem(String hostname, String authHeader) {
    String invUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT, INVENTORY_HOSTS + "/" + hostname);
    return getJsonFromUrl(invUrl, authHeader);
  }

  public static String getJwtRoles(String authHeader) {
    String url = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT,
        INV_JWT + "/groups");
    return getStringFromUrl(url, authHeader);
  }

  public static String getJwtUsername(String authHeader) {
    String url = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT,
        INV_JWT + "/username");
    return getStringFromUrl(url, authHeader);
  }

  public static String getStringFromUrl(String url, String authHeader) {
    Response response = processRequest(url, "GET", null, authHeader);
    return response.readEntity(String.class);
  }

  public static JsonObject getJsonFromUrl(String url, String authHeader) {
    return toJsonObj(getStringFromUrl(url, authHeader));
  }

  public static boolean responseOk(String authHeader) {
    String url = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT,
        SYSTEM_PROPERTIES);
    Response response = processRequest(url, "GET", null, authHeader);
    return (response.getStatus() != Status.OK.getStatusCode()) ? false : true;
  }

  public static boolean invOk(String authHeader) {
    String propUrl = buildUrl(SECURED_PROTOCOL, HOSTNAME, DEFAULT_PORT,
        INVENTORY_HOSTS);
    Response propResponse = processRequest(propUrl, "GET", null, authHeader);
    return (propResponse.getStatus() != Status.OK.getStatusCode()) ? false : true;
  }

  public static String buildUrl(String protocol, String host, int port,
      String path) {
    try {
      URI uri = new URI(protocol, null, host, port, path, null, null);
      return uri.toString();
    } catch (Exception e) {
      System.out.println("URISyntaxException");
      return null;
    }
  }

  public static Response processRequest(String url, String method, String payload,
      String authHeader) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(url);
    Builder builder = target.request();
    builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    if (authHeader != null) {
      builder.header(HttpHeaders.AUTHORIZATION, authHeader);
    }
    return (payload != null) ? builder.build(method, Entity.json(payload)).invoke()
        : builder.build(method).invoke();
  }

  public static JsonObject toJsonObj(String json) {
    JsonReader jReader = Json.createReader(new StringReader(json));
    return jReader.readObject();
  }
}
// end::jwt[]
