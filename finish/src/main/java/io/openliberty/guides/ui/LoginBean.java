package io.openliberty.guides.ui;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.ibm.websphere.security.jwt.*;
import io.openliberty.guides.ui.WebUtils;


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


  public String doLogIn() {

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
        System.out.println(SessionUtils.getInventory(buildJWT(role)));
    } else {
      System.out.println("Update Sessional User Failed.");
    }

    System.out.println("From Frontend: " + WebUtils.getUserAgent(request));

    // // get the current session
    // HttpSession ses = request.getSession(false);
    // if (ses == null) {
    //   System.out.println("Session is timeout.");
    // }
    // ses.setAttribute("jwt", newJwt); // important to set it here!

    return "index.jsf";
  }



  private String buildJWT(String role) {
    String newJwt = null;
     try {
      JwtBuilder jwtBuilder = JwtBuilder.create();
      jwtBuilder.subject(username).claim(Claims.AUDIENCE, "http://localhost:9080/security/jwt").claim("iss","http://localhost:9080/jwtBuilder" ).claim("alg","RS256" ).claim("username", username).claim("password", password).claim("role", role);
      // jwtBuilder.signWith(RS256, "secret".getBytes("UTF-8"));
      JwtToken goToken = jwtBuilder.buildJwt();
      newJwt = goToken.compact();
      System.out.println("Writer Interceptor added token :: "+newJwt);

    } catch (InvalidClaimException e) {
      System.out.println("InvalidClaimException");
      e.printStackTrace();
    } catch (JwtException e) {
      System.out.println("JwtException");
      e.printStackTrace();
    } catch (InvalidBuilderException e) {
      System.out.println("InvalidBuilderException");
      e.printStackTrace();
    }

    return newJwt;

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
