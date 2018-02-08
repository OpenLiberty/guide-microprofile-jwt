// // tag::copyright[]
// /*******************************************************************************
//  * Copyright (c) 2017 IBM Corporation and others.
//  * All rights reserved. This program and the accompanying materials
//  * are made available under the terms of the Eclipse Public License v1.0
//  * which accompanies this distribution, and is available at
//  * http://www.eclipse.org/legal/epl-v10.html
//  *
//  * Contributors:
//  *     IBM Corporation - Initial implementation
//  *******************************************************************************/
//  // end::copyright[]
// package io.openliberty.guides.inventory.client;
//
// import javax.json.JsonObject;
//
// public class SystemClient extends io.openliberty.guides.inventory.client.ClientCore {
//
//     // Constants for building URI to the system service.
//     private static final int DEFAULT_PORT = 5051;
//     private static final String SYSTEM_PROPERTIES = "/system/properties";
//     private static final String PROTOCOL = "http";
//     private static final String SECURED_PROTOCOL = "https";
//
//     // tag::doc[]
//     /**
//      * <p>Retrieves the JVM system properties of a particular host.</p>
//      *
//      * <p>NOTE: the host must expose its JVM's system properties via the
//      * system service; ie. the system service must be running on that host.</p>
//      *
//      * @param hostname - name of host.
//      * @return JSON Java object containing the system properties of the host's JVM.
//      */
//     // end::doc[]
//     public static JsonObject getProperties(String hostname) {
//       String propUrl = buildUriString(PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
//       return getContent(propUrl, null);
//     }
//     // tag::doc[]
//     /**
//      * <p>Retrieves the JVM system properties of a particular host for the given port number.</p>
//      *
//      * <p>NOTE: the host must expose its JVM's system properties via the
//      * system service; ie. the system service must be running on that host.</p>
//      *
//      * @param hostname - name of host.
//      * @param port     - port number for the system service.
//      * @return JSON Java object containing the system properties of the host's JVM.
//      */
//     // end::doc[]
//     public static JsonObject getProperties(String hostname, int port) {
//       String propUrl = buildUriString(PROTOCOL, hostname, port, SYSTEM_PROPERTIES);
//       return getContent(propUrl, null);
//     }
//
//     // tag::doc[]
//     /**
//      * <p>Retrieves the JVM system properties of a particular host with the authorization header.</p>
//      *
//      * <p>NOTE: the host must expose its JVM's system properties via the
//      * system service; ie. the system service must be running on that host.</p>
//      *
//      * @param hostname - name of host.
//      * @param authHeader - authorization header for the system service.
//      * @return JSON Java object containing the system properties of the host's JVM.
//      */
//     // end::doc[]
//     public static JsonObject getProperties(String hostname, String authHeader) {
//       String propUrl = buildUriString(SECURED_PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
//       return getContent(propUrl, authHeader);
//     }
//
//     // tag::doc[]
//     /**
//      * <p>Returns whether or not a particular host is exposing its JVM's system properties.
//      * In other words, returns whether or not the system service is running on a
//      * particular host.</p>
//      *
//      * @param hostname - name of host.
//      * @return true if the host is currently running the system service and false otherwise.
//      */
//     // end::doc[]
//     public static boolean responseOk(String hostname) {
//         String propUrl = buildUriString(PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
//         return isResponseOk(propUrl, null);
//     }
//
//     // tag::doc[]
//     /**
//      * <p>Returns whether or not a particular host is exposing its JVM's system properties on the
//      * given port number. In other words, returns whether or not the system service is running on a
//      * particular host on the given port number.</p>
//      *
//      * @param hostname - name of host.
//      * @param port     - port number.
//      * @return true if the host is currently running the system service and false otherwise.
//      */
//     // end::doc[]
//     public static boolean responseOk(String hostname, int port) {
//         String propUrl = buildUriString(PROTOCOL, hostname, port, SYSTEM_PROPERTIES);
//         return isResponseOk(propUrl, null);
//     }
//
//     public static boolean responseOk(String hostname, String authHeader) {
//         String propUrl = buildUriString(SECURED_PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
//         return isResponseOk(propUrl, authHeader);
//     }
// }

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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Properties;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SystemClient {

  // Constants for building URI to the system service.
  private static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("https.port"));
  private static final String SYSTEM_PROPERTIES = "/system/properties";
  private static final String PROTOCOL = "http";
  private static final String SECURED_PROTOCOL = "https";

  private String url;
  private boolean status;
  private Properties content;

  /**
   * Used by the following guide(s): CDI, MP-METRICS
   */
  public SystemClient(String hostname) {
    init(PROTOCOL, hostname, DEFAULT_PORT, null);
  }

  /**
   * Used by the following guide(s): MP-CONFIG, MP-HEALTH, FAULT-TOLERANCE
   */
  public SystemClient(String hostname, int port) {
    init(PROTOCOL, hostname, port, null);
  }

  /**
   * Used by the following guide(s): MP-JWT
   */
  public SystemClient(String hostname, String authHeader) {
    init(SECURED_PROTOCOL, hostname, DEFAULT_PORT, authHeader);
  }

  /**
   * Helper function to set the attributes.
   */
  private void init(String protocol, String hostname, int port, String authHeader) {
    this.setUrl(protocol, hostname, port, SYSTEM_PROPERTIES);
    this.setStatus(authHeader);
    if (this.status) {
      this.setContent(authHeader);
    }
  }

  /**
   * URL getter
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Status getter
   */
  public boolean isResponseOk() {
    return this.status;
  }

  /**
   * Content getter
   */
  public Properties getContent() {
    return this.content;
  }

  /**
   * <p>
   * Builds the URI string to the system service for a particular host. This is
   * just a helper method.
   * </p>
   *
   * @param protocol
   *          - http or https.
   * @param host
   *          - name of host.
   * @param port
   *          - port number.
   * @param path
   *          - Note that the path needs to start with a slash!!!
   * @return String representation of the URI to the system properties service.
   */
  private void setUrl(String protocol, String host, int port, String path) {
    try {
      URI uri = new URI(protocol, null, host, port, path, null, null);
      this.url = uri.toString();
    } catch (Exception e) {
      System.out.println("URISyntaxException");
      this.url = null;
    }
  }

  /**
   * Status setter
   */
  private void setStatus(String authHeader) {
    try {
      URL target = new URL(this.url);
      HttpURLConnection http = (HttpURLConnection) target.openConnection();
      if (authHeader != null) {
        http.setRequestProperty ("Authorization", authHeader);
      }
      http.setConnectTimeout(50);
      int response = http.getResponseCode();
      this.status = (response != 200) ? false : true;
    } catch (Exception e) {
      this.status = false;
    }
  }

  /**
   * Content setter
   */
  private void setContent(String authHeader) {
    Client client = ClientBuilder.newClient();
    Builder builder = client.target(this.url).request();
    builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    if (authHeader != null) {
      builder.header(HttpHeaders.AUTHORIZATION, authHeader);
    }
    Response response = builder.build("GET").invoke();
    this.content = response.readEntity(Properties.class);
  }

}
