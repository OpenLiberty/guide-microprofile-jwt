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

public class SystemTest {


    @Test
    public void testGetPropertiesWithJWT() throws Exception {
        // tag::systemProperties[]
        String baseUrl =
            "https://"
                + System.getProperty("liberty.test.hostname")
                + ":"
                + System.getProperty("liberty.test.ssl.port");
        // end::systemProperties[]

      String authHeader =
          "Bearer "
              + new JWTVerifier()
                  .createUserJWT("TESTUSER");

      // Get system properties by using JWT token
      String propUrl = baseUrl + "/system/properties";
      Response propResponse = TestUtils.processRequest(propUrl, "GET", null, authHeader);

      assertEquals(
          "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
          Status.OK.getStatusCode(),
          propResponse.getStatus());

      JsonObject responseJson = TestUtils.toJsonObj(propResponse.readEntity(String.class));

      assertEquals("The system property for the local and remote JVM should match",
                   System.getProperty("os.name"),
                   responseJson.getString("os.name"));
      System.out.println(responseJson.getString("os.name"));
      System.out.println(propUrl);


    }


}
