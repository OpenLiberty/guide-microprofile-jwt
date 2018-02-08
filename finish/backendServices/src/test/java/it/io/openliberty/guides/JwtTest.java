package it.io.openliberty.guides;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;
import org.junit.Before;
import it.io.openliberty.guides.util.TestUtils;
import test.JWTVerifier;

public class JwtTest {

  String baseUrl = "https://" + System.getProperty("liberty.test.hostname") + ":"
      + System.getProperty("liberty.test.ssl.port");
  private final String TESTNAME = "TESTUSER";

  String authHeader;

  @Before
  public void setup() throws Exception {
    authHeader = "Bearer " + new JWTVerifier().createUserJWT(TESTNAME);
  }

  @Test
  public void testSuite() {
    this.testJWTGetName();
    this.testJWTGetCustomClaim();
  }

  public void testJWTGetName() {

    String jwtUrl = baseUrl + "/inventory/jwt/username";
    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

    assertEquals(
        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
        Status.OK.getStatusCode(), jwtResponse.getStatus());

    String responseName = jwtResponse.readEntity(String.class);

    assertEquals("The test name and jwt token name should match", TESTNAME,
        responseName);
    System.out.println(responseName);
    System.out.println(jwtUrl);

  }

  public void testJWTGetCustomClaim() {

    String jwtUrl = baseUrl + "/inventory/jwt/customClaim";
    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

    assertEquals("HTTP response code should have been "
        + Status.FORBIDDEN.getStatusCode() + ".", Status.FORBIDDEN.getStatusCode(),
        jwtResponse.getStatus());

    System.out.println(jwtUrl);

  }

}
