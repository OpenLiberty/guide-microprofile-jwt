// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::testClass[]
package it.io.openliberty.guides.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.WebTarget;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RestClientIT {

  private static String port;

  private Client client;

  private final String INVENTORY_SYSTEMS = "inventory/systems";

  @BeforeAll
  public static void oneTimeSetup() {
    port = System.getProperty("http.port");
  }

  @BeforeEach
  public void setup() {
    client = ClientBuilder.newClient();
    client.register(JsrJsonpProvider.class);
  }

  @AfterEach
  public void teardown() {
    client.close();
  }

  @Test
  public void testSuite() {
    this.testDefaultLocalhost();
    this.testRestClientBuilder();
  }

  // tag::testDefaultLocalhost[]
  public void testDefaultLocalhost() {
    String hostname = "localhost";

    String url = "http://localhost:" + port + "/" + INVENTORY_SYSTEMS + "/" + hostname;

    JsonObject obj = fetchProperties(url);

    assertEquals(System.getProperty("os.name"), obj.getString("os.name"),
                 "The system property for the local and remote JVM should match");
  }
  // end::testDefaultLocalhost[]

  // tag::testRestClientBuilder[]
  public void testRestClientBuilder() {
    String hostname = null;
    try{
      hostname = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      System.err.println("Unknown Host.");
    }

    String url = "http://localhost:" + port + "/" + INVENTORY_SYSTEMS + "/" + hostname;

    JsonObject obj = fetchProperties(url);

    assertEquals(System.getProperty("os.name"), obj.getString("os.name"),
                 "The system property for the local and remote JVM should match");
  }
  // end::testRestClientBuilder[]

  private JsonObject fetchProperties(String url) {
    WebTarget target = client.target(url);
    Response response = target.request().get();

    assertEquals(200, response.getStatus(), "Incorrect response code from " + url);

    JsonObject obj = response.readEntity(JsonObject.class);
    response.close();
    return obj;
  }

}
// end::testClass[]