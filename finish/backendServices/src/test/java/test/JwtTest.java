package test;

import static org.junit.Assert.assertEquals;

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
import org.junit.Test;
import test.util.TestUtils;
import org.junit.Before;

public class JwtTest {

  String baseUrl =
      "https://"
          + System.getProperty("liberty.test.hostname")
          + ":"
          + System.getProperty("liberty.test.ssl.port");
  String testName = "TESTUSER";

  String authHeader;

    @Before
    public void setup() throws Exception {
        authHeader = "Bearer "
            + new JWTVerifier()
                .createUserJWT(testName);
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
          Status.OK.getStatusCode(),
          jwtResponse.getStatus());

      String responseName = jwtResponse.readEntity(String.class);

      assertEquals("The test name and jwt token name should match",
                   testName,
                   responseName);
      System.out.println(responseName);
      System.out.println(jwtUrl);

    }

    public void testJWTGetCustomClaim() {

      String jwtUrl = baseUrl + "/inventory/jwt/customClaim";
      Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

      assertEquals(
          "HTTP response code should have been " + Status.FORBIDDEN.getStatusCode() + ".",
          Status.FORBIDDEN.getStatusCode(),
          jwtResponse.getStatus());

      System.out.println(jwtUrl);

    }


}
