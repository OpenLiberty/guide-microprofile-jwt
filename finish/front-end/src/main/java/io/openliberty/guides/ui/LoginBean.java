package io.openliberty.guides.ui;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ibm.websphere.security.jwt.*;
import io.openliberty.guides.ui.util.SessionUtils;


@ManagedBean
@ViewScoped
public class LoginBean {

  private String username;
  private String password;


  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }


  public String doLogIn() throws Exception {

    HttpServletRequest request = SessionUtils.getRequest();

    // do login
    try {
      request.login(this.username, this.password);
    } catch (ServletException e) {
      //context.addMessage(null, new FacesMessage("Login failed."));
      System.out.println("Login failed.");
      return "error.jsf";
    }

    // to get remote user using getRemoteUser()
    String remoteUser = request.getRemoteUser();
    String role = getRole(request);
    System.out.println("AFTER LOGIN, REMOTE USER: " + remoteUser + " " + role);

    // update session
    if (remoteUser != null && remoteUser.equals(username)){
      // User user = new User(username, password, role);
      String jwt = buildJWT(username);
      // get the current session
      HttpSession ses = request.getSession(false);
      if (ses == null) {
        System.out.println("Session is timeout.");
      } else {
        ses.setAttribute("jwt", jwt); // important to set it here!
      }

    } else {
      System.out.println("Update Sessional JWT Failed.");
    }

    return "system.jsf";
  }

    /**
     * Build a JWT that will be used by an authenticated user. The JWT wil be in the 'users' group and
     * should contain the username as defined by MP JWT.
     *
     * @param userName The name of the authenticated user.
     * @return A compact JWT that should be returned to the caller.
     * @throws Exception Something went wrong...?
     */
    private String buildJWT(String userName) throws Exception {
      return JwtBuilder.create("jwtFrontEndBuilder")
          .claim(Claims.SUBJECT, userName)
          .claim("upn", userName) /* MP-JWT defined subject claim */
          .claim("groups", "users") /* MP-JWT builds an array from this */
          .buildJwt()
          .compact();
    }


  private String getRole(HttpServletRequest request) {
    // to check if remote user is granted admin role
    boolean isAdmin = request.isUserInRole("admin");
    if (isAdmin) {
      return "admin";
    }
    return "user";
  }


}
