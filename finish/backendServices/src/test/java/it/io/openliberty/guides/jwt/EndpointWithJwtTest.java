// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::test[]
package it.io.openliberty.guides.jwt;

import static org.junit.Assert.assertEquals;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Test;
import it.io.openliberty.guides.jwt.util.TestUtils;
import test.JWTVerifier;

public class EndpointWithJwtTest {

  private final String INVENTORY_HOSTS = "/inventory/systems";
  private final String SYSTEM_PROPERTIES = "/system/properties";
  private final String TESTNAME = "TESTUSER";

  String baseUrl = "https://" + System.getProperty("liberty.test.hostname") + ":"
      + System.getProperty("liberty.test.ssl.port");

  String authHeader;

  @Before
  public void setup() throws Exception {
    authHeader = "Bearer " + new JWTVerifier().createAdminJWT(TESTNAME);
  }

  @Test
  public void testSuite() {
    this.testGetPropertiesWithJWT();
    this.testEmptyInventoryWithJWT();
    this.testHostRegistrationWithJWT();
  }

  public void testGetPropertiesWithJWT() {
    // Get system properties by using JWT token
    String propUrl = baseUrl + SYSTEM_PROPERTIES;
    Response propResponse = TestUtils.processRequest(propUrl, "GET", null,
        authHeader);

    assertEquals(
        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
        Status.OK.getStatusCode(), propResponse.getStatus());

    JsonObject responseJson = TestUtils.toJsonObj(
        propResponse.readEntity(String.class));

    assertEquals("The system property for the local and remote JVM should match",
        System.getProperty("os.name"), responseJson.getString("os.name"));
    // System.out.println(responseJson.getString("os.name"));
    // System.out.println(propUrl);

  }

  public void testEmptyInventoryWithJWT() {
    String invUrl = baseUrl + INVENTORY_HOSTS;
    Response invResponse = TestUtils.processRequest(invUrl, "GET", null, authHeader);

    assertEquals(
        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
        Status.OK.getStatusCode(), invResponse.getStatus());

    JsonObject responseJson = TestUtils.toJsonObj(
        invResponse.readEntity(String.class));

    assertEquals("The inventory should be empty on application start", 0,
        responseJson.getInt("total"));
    // System.out.println(responseJson.getInt("total"));
    // System.out.println(invUrl);

  }

  public void testHostRegistrationWithJWT() {
    String invUrl = baseUrl + INVENTORY_HOSTS + "/localhost";
    Response invResponse = TestUtils.processRequest(invUrl, "GET", null, authHeader);

    assertEquals(
        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
        Status.OK.getStatusCode(), invResponse.getStatus());

    JsonObject responseJson = TestUtils.toJsonObj(
        invResponse.readEntity(String.class));

    assertEquals("The inventory should get the os.name of localhost",
        System.getProperty("os.name"), responseJson.getString("os.name"));
    // System.out.println(responseJson.getString("os.name"));
    // System.out.println(invUrl);

  }

}
// end::test[]
