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
                  .createJWT("TESTUSER");
      }


    @Test
    public void testSuite() {
        this.testEmptyInventoryWithJWT();
        this.testHostRegistrationWithJWT();
        // this.testSystemPropertiesMatch();
        // this.testUnknownHost();
    }


    public void testEmptyInventoryWithJWT() {
      // Get system properties by using JWT token
      String invUrl = baseUrl + "/inventory/hosts";
      Response invResponse = processRequest(invUrl, "GET", null, authHeader);

      assertEquals(
          "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
          Status.OK.getStatusCode(),
          invResponse.getStatus());

      JsonObject responseJson = toJsonObj(invResponse.readEntity(String.class));

      assertEquals("The inventory should be empty on application start",
                   0, responseJson.getInt("total"));
      System.out.println(responseJson.getInt("total"));
      System.out.println(invUrl);

    }

    public void testHostRegistrationWithJWT(){
      // Get system properties by using JWT token
      String invUrl = baseUrl + "/inventory/hosts/localhost";
      Response invResponse = processRequest(invUrl, "GET", null, authHeader);

      assertEquals(
          "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
          Status.OK.getStatusCode(),
          invResponse.getStatus());

      JsonObject responseJson = toJsonObj(invResponse.readEntity(String.class));

      assertEquals("The inventory should get the os.name of localhost",
              System.getProperty("os.name"),
              responseJson.getString("os.name"));
      System.out.println(responseJson.getString("os.name"));
      System.out.println(invUrl);

    }


    public Response processRequest(String url, String method, String payload, String authHeader) {
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

    public JsonObject toJsonObj(String json) {
        JsonReader jReader = Json.createReader(new StringReader(json));
          return jReader.readObject();
    }

}
