package io.openliberty.guides.ui;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import com.ibm.websphere.security.jwt.JwtToken;

public class SessionUtils {
  private static String port = System.getProperty("default.http.port");

  /**
   * Gets the current session for a logged in user.
   */
  public static HttpSession getSession() {
    return (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
                                     .getSession(false);
  }

  /**
   * Gets Http servlet for user request information.
   */
  public static HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance()
                                            .getExternalContext().getRequest();
  }

  public static String getJwtToken() {
    HttpSession session = getSession();
    return (String) session.getAttribute("jwt");
  }

  public static String getInventory(String newJwt) {
    System.out.println("Get Users");
    ClientBuilder cb = ClientBuilder.newBuilder();
    Client c = cb.build();
    // c.property("com.ibm.ws.jaxrs.client.disableCNCheck", true);
    // c.property("com.ibm.ws.jaxrs.client.ssl.config", "defaultSSLConfig");
    c.property("com.ibm.ws.jaxrs.client.ltpa.handler", "true");
    String res = null;
    res = c.target("http://localhost:" + port + "/users").request().header("jwt", newJwt)
           .get().readEntity(String.class);
    c.close();
    return res;

  }

}
