// ******************************************************************************
//  Copyright (c) 2017 IBM Corporation and others.
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/epl-v10.html
//
//  Contributors:
//  IBM Corporation - initial API and implementation
// ******************************************************************************
package demo;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Populates the pertinent databases. */
public class DemoData {

  private static final String KEY_ID = "id";

  private static final String BOOT_FILE_USERS = "users.json";

  private static String userServiceURL;

  private static String authServiceURL;

  // Map temp ids in json files to the official ids returned when the objects
  // are added to their respective services
  private static HashMap<String, String> userIds = new HashMap<String, String>();

  // JWT returned from user service to use for creating teams
  private static String jwt = null;

  public static void main(String[] args) {

    if (args.length != 4) {
      System.err.println(
          "Usage: This jar expects eight arguments specifying host and ports for four services in the following order: \n\n"
              + "user server hostname\n"
              + "user server https port\n"
              + "auth server hostname\n"
              + "auth server https port");

      System.exit(1);
    }

    String userHost = args[0];
    String userPort = args[1];
    String authHost = args[2];
    String authPort = args[3];

    userServiceURL = "https://" + userHost + ":" + userPort + "/users";

    authServiceURL = "https://" + authHost + ":" + authPort + "/auth";

    try {
      parseUsers();

    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  /** Populates the user microservice database with pre-defined users. */
  private static void parseUsers() throws IOException {
    final URL users = Thread.currentThread().getContextClassLoader().getResource(BOOT_FILE_USERS);
    assert users != null : "Failed to load '" + BOOT_FILE_USERS + "'";

    final JsonReaderFactory factory = Json.createReaderFactory(null);
    JsonReader reader = factory.createReader(users.openStream());

    final JsonArray jsonUsers = reader.readArray();

    // Get JWT to use when creating users
    String authJWT = null;
    try {
      Response authResponse = makeConnection("GET", authServiceURL, null, null);
      authJWT = authResponse.getHeaderString("Authorization");
    } catch (GeneralSecurityException e) {
      System.err.println("Could not connect to the auth service");
    }

    // Send request to user service to add new users
    for (JsonValue userJsonValue : jsonUsers) {
      try {
        String userPayload = userJsonValue.toString();

        Response response = makeConnection("POST", userServiceURL, userPayload, authJWT);
        if (jwt == null) {
          // Get the jwt returned from the user service. We only need
          // this from the first call to create a user.
          jwt = response.getHeaderString("Authorization");
        }
        // Check http response code
        int rc = response.getStatus();
        System.out.println("RC: " + rc);

        if (rc != 200) {
          System.err.println("Add user failed");
          System.exit(1);
        }

        // Add official id to user map
        System.out.println(response.readEntity(String.class));
        JsonObject responseJson = toJsonObj(response.readEntity(String.class));
        String officialId = responseJson.getString(KEY_ID);
        String tempId = toJsonObj(userPayload).getString(KEY_ID);

        userIds.put(tempId, officialId);

      } catch (IOException | GeneralSecurityException e) {
        System.err.println("Could not connect to the user service");
      }
    }
  }

  /** Make an HTTP connection to the specified URL and pass in the specified payload. */
  private static Response makeConnection(
      String method, String urlString, String payload, String jwt)
      throws IOException, GeneralSecurityException {

    // Setup connection
    System.out.println("Creating connection - Method: " + method + ", URL: " + urlString);

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(urlString);

    Invocation.Builder invoBuild = target.request(MediaType.APPLICATION_JSON_TYPE);

    if (jwt != null) {
      invoBuild.header("Authorization", jwt);
    }
    if (payload != null) {
      System.out.println("Request Payload: " + payload);
      Entity<String> data = Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE);
      return invoBuild.build(method, data).invoke();
    } else {
      return invoBuild.build(method).invoke();
    }
  }

  /**
   * Converts a JSON string to a JSONObject instance.
   *
   * @param json The JSON string to convert.
   * @return The JSONObject instance representing the input.
   */
  public static JsonObject toJsonObj(String json) {
    try (JsonReader jReader = Json.createReader(new StringReader(json))) {
      return jReader.readObject();
    }
  }
}
