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

import javax.json.JsonObject;

public class SystemClient extends io.openliberty.guides.inventory.client.ClientCore {

    // Constants for building URI to the system service.
    private static final int DEFAULT_PORT = 5051;
    private static final String SYSTEM_PROPERTIES = "/system/properties";
    private static final String PROTOCOL = "http";
    private static final String SECURED_PROTOCOL = "https";

    /**
     * <p>Retrieves the JVM system properties of a particular host.</p>
     *
     * <p>NOTE: the host must expose its JVM's system properties via the
     * system service; ie. the system service must be running on that host.</p>
     *
     * @param hostname - name of host.
     * @return JSON Java object containing the system properties of the host's JVM.
     */
    public static JsonObject getProperties(String hostname) {
      String propUrl = buildUriString(PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
      return getContent(propUrl, null);
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
    public static JsonObject getProperties(String hostname, int port) {
      String propUrl = buildUriString(PROTOCOL, hostname, port, SYSTEM_PROPERTIES);
      return getContent(propUrl, null);
    }


    /**
     * <p>Retrieves the JVM system properties of a particular host with the authorization header.</p>
     *
     * <p>NOTE: the host must expose its JVM's system properties via the
     * system service; ie. the system service must be running on that host.</p>
     *
     * @param hostname - name of host.
     * @param authHeader - authorization header for the system service.
     * @return JSON Java object containing the system properties of the host's JVM.
     */
    public static JsonObject getProperties(String hostname, String authHeader) {
      String propUrl = buildUriString(SECURED_PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
      return getContent(propUrl, authHeader);
    }

    /**
     * <p>Returns whether or not a particular host is exposing its JVM's system properties.
     * In other words, returns whether or not the system service is running on a
     * particular host.</p>
     *
     * @param hostname - name of host.
     * @return true if the host is currently running the system service and false otherwise.
     */
    public static boolean responseOk(String hostname) {
        String propUrl = buildUriString(PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
        return isResponseOk(propUrl, null);
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
    public static boolean responseOk(String hostname, int port) {
        String propUrl = buildUriString(PROTOCOL, hostname, port, SYSTEM_PROPERTIES);
        return isResponseOk(propUrl, null);
    }

    public static boolean responseOk(String hostname, String authHeader) {
        String propUrl = buildUriString(SECURED_PROTOCOL, hostname, DEFAULT_PORT, SYSTEM_PROPERTIES);
        return isResponseOk(propUrl, authHeader);
    }


}
