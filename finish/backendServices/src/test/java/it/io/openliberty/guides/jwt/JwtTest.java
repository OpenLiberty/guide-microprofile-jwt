// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;
import org.junit.Before;
import it.io.openliberty.guides.jwt.util.TestUtils;
import it.io.openliberty.guides.jwt.util.JwtVerifier;

public class JwtTest {

  private final String TESTNAME = "TESTUSER";
  private final String INV_JWT = "/inventory/jwt";

  String baseUrl = "https://" + System.getProperty("liberty.test.hostname") + ":"
      + System.getProperty("liberty.test.ssl.port");

  String authHeader;

  @Before
  // tag::setup[]
  public void setup() throws Exception {
    authHeader = "Bearer " + new JwtVerifier().createUserJwt(TESTNAME);
  }
  // end::setup[]

  @Test
  public void testSuite() {
    this.testJwtGetName();
    this.testJwtGetCustomClaim();
  }

  // tag::testJwtGetName[]
  public void testJwtGetName() {
    String jwtUrl = baseUrl + INV_JWT + "/username";
    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

    assertEquals(
        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
        Status.OK.getStatusCode(), jwtResponse.getStatus());

    String responseName = jwtResponse.readEntity(String.class);

    assertEquals("The test name and jwt token name should match", TESTNAME,
        responseName);
  }
  // end::testJwtGetName[]

  // tag::testJwtGetCustomClaim[]
  public void testJwtGetCustomClaim() {
    String jwtUrl = baseUrl + INV_JWT + "/customClaim";
    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

    assertEquals("HTTP response code should have been "
        + Status.FORBIDDEN.getStatusCode() + ".", Status.FORBIDDEN.getStatusCode(),
        jwtResponse.getStatus());
  }
  // end::testJwtGetCustomClaim[]

}
// end::test[]
