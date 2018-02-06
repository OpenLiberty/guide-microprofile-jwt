package io.openliberty.guides.ui.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import io.openliberty.guides.ui.User;

public class SessionUtils {

  
  // Gets the current session for a logged in user.
  public static HttpSession getSession() {
    return (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
                                     .getSession(false);
  }

  // Gets Http servlet for user request information.
  public static HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance()
                                            .getExternalContext().getRequest();
  }

  // Get the current logged in user object.
  public static User getUserObj() {
    HttpSession session = getSession();
    return (User) session.getAttribute("user");
  }

  // Get the JWT token for the currently logged in user.
  public static String getJwtToken() {
    HttpSession session = getSession();
    return (String) session.getAttribute("jwt");
  }
}
