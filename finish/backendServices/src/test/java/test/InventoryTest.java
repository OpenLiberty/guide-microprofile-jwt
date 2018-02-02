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
import org.junit.Before;
import org.junit.Test;
import test.util.TestUtils;

public class InventoryTest {

    String baseUrl =
        "https://"
            + System.getProperty("liberty.test.hostname")
            + ":"
            + System.getProperty("liberty.test.ssl.port");

    String authHeader;

      @Before
      public void setup() throws Exception {
          authHeader = "Bearer "
              + new JWTVerifier()
                  .createUserJWT("TESTUSER");
      }


    @Test
    public void testSuite() {
        this.testEmptyInventoryWithJWT();
        this.testHostRegistrationWithJWT();
    }


    public void testEmptyInventoryWithJWT() {
      // Get system properties by using JWT token
      String invUrl = baseUrl + "/inventory/hosts";
      Response invResponse = TestUtils.processRequest(invUrl, "GET", null, authHeader);

      assertEquals(
          "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
          Status.OK.getStatusCode(),
          invResponse.getStatus());

      JsonObject responseJson = TestUtils.toJsonObj(invResponse.readEntity(String.class));

      assertEquals("The inventory should be empty on application start",
                   0, responseJson.getInt("total"));
      System.out.println(responseJson.getInt("total"));
      System.out.println(invUrl);

    }

    public void testHostRegistrationWithJWT(){
      // Get system properties by using JWT token
      String invUrl = baseUrl + "/inventory/hosts/localhost";
      Response invResponse = TestUtils.processRequest(invUrl, "GET", null, authHeader);

      assertEquals(
          "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
          Status.OK.getStatusCode(),
          invResponse.getStatus());

      JsonObject responseJson = TestUtils.toJsonObj(invResponse.readEntity(String.class));

      assertEquals("The inventory should get the os.name of localhost",
              System.getProperty("os.name"),
              responseJson.getString("os.name"));
      System.out.println(responseJson.getString("os.name"));
      System.out.println(invUrl);

    }



}
