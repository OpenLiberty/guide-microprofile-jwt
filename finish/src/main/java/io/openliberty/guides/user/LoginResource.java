// ******************************************************************************
//  Copyright (c) 2017 IBM Corporation and others.
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/epl-v10.html
//
//  Contributors:
//  IBM Corporation - initial API and implementation
// ******************************************************************************
package io.openliberty.guides.user;

import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.JwtBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * This resource is responsible for logging in a user. Upon a successful login, the ID of the user
 * will be returned. Both regular login with username and password, as well as Twitter login, are
 * supported.
 */
@Path("/logins")
@RequestScoped
public class LoginResource {

  /**
   * The JWT of the current caller. Since this is a request scoped resource, the JWT will be
   * injected for each JAX-RS request. The injection is performed by the mpJwt-1.0 feature.
   */
  @Inject private JsonWebToken jwtPrincipal;

  // -----------------------------------
  // Microprofile config injected values
  // -----------------------------------

  /**
   * The URL serving the twitter sign-in callback. Twitter requires a callback URL to be used after
   * Twitter receives the username and password from the user. The caller is then redirected to the
   * callback, where our application completes the sign-in process.
   *
   * <p>This value is injected by MP Config. It is specified in the project POM, and copied into the
   * Liberty server bootstrap.properties during the build. bootstrap.properties is a pre-defined
   * config source for MP config.
   */
  @Inject
  @ConfigProperty(name = "acme.gifts.frontend.url")
  private String frontEndCallbackURL;

  /** The user ID key for login response body */
  public static final String LOGIN_RESPONSE_ID_KEY = "id";

  /** Error key */
  public static final String LOGIN_RC_ERR_KEY = "error";

  /** Errors values to be returned to caller */
  public static final String LOGIN_RC_ERR_USR_NOT_FOUND = "userNotFound";

  public static final String LOGIN_RC_ERR_INCORRECT_PSWD = "incorrectPassword";
  public static final String LOGIN_RC_ERR_CANNOT_AUTH = "unableToAuthenticate";

  /**
   * This endpoint will attempt to log-in a user using the password specified in the user database.
   * The caller must have a valid JWT obtained from the auth service.
   *
   * @param payload The login request. The username and password must be specified.
   * @return The user ID if the login succeeded.
   */
  @POST
  @Path("/")
  @Consumes("application/json")
  public Response loginUser(JsonObject payload) {
    // Validate the JWT.  The JWT must be in the 'login' group to proceed.
    try {
      validateJWT();
    } catch (JWTException jwte) {
      return Response.status(Status.UNAUTHORIZED)
          .type(MediaType.TEXT_PLAIN)
          .entity(jwte.getMessage())
          .build();
    }

    // Prepare a response.
    JsonBuilderFactory factory = Json.createBuilderFactory(null);
    JsonObjectBuilder builder = factory.createObjectBuilder();
    JsonObject responseBody = null;

    // Read the username and password from the request.
    String userName = payload.getString(User.JSON_KEY_USER_NAME);
    String password = payload.getString("password");



      // Check the password by pre-pending the cleartext password with the salt
      // for this user, and then hashing it.  Compare the hashed password with
      // the hashed password stored in the database.
      String passwordSaltString = ((String) dbUser.get(User.JSON_KEY_USER_PASSWORD_SALT));
      String correctHashedPassword = ((String) dbUser.get(User.JSON_KEY_USER_PASSWORD_HASH));

      try {
        PasswordUtility pwUtil = new PasswordUtility(password, passwordSaltString);
        if (correctHashedPassword.equals(pwUtil.getHashedPassword())) {
          responseBody = builder.add(LOGIN_RESPONSE_ID_KEY, dbId).build();
          buildJwt = true;
        } else {
          responseBody = builder.add(LOGIN_RC_ERR_KEY, LOGIN_RC_ERR_INCORRECT_PSWD).build();
          return Response.status(Status.UNAUTHORIZED).entity(responseBody).build();
        }
      } catch (Throwable t) {
        responseBody = builder.add(LOGIN_RC_ERR_KEY, LOGIN_RC_ERR_CANNOT_AUTH).build();
        return Response.serverError().entity(responseBody).build();
      }


    // If the login succeeded, build the JWT that the caller should use on all future
    // calls.  The group name will be 'users' which gives the authenticated user access
    // to other services.
    ResponseBuilder okResponse = Response.ok(responseBody, MediaType.APPLICATION_JSON);
    if (buildJwt) {
      try {
        okResponse
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + buildJWT(userName))
            .header("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
      } catch (Throwable t) {
        return Response.serverError().entity("Error building authorization token").build();
      }
    }

    return okResponse.build();
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
    return JwtBuilder.create("jwtUserBuilder")
        .claim(Claims.SUBJECT, userName)
        .claim("upn", userName) /* MP-JWT defined subject claim */
        .claim("groups", "users") /* MP-JWT builds an array from this */
        .buildJwt()
        .compact();
  }

  /** Do some basic checks on the JWT, until the MP-JWT annotations are ready. */
  private void validateJWT() throws JWTException {
    // Make sure the authorization header was present.  This check is somewhat
    // silly since the jwtPrincipal will never actually be null since it's a
    // WELD proxy (injected).
    if (jwtPrincipal == null) {
      throw new JWTException("No authorization header or unable to inflate JWT");
    }

    // Make sure we're in the login group.  This JWT only allows logins, after
    // which a new JWT will be created for the specific authenticated user.
    Set<String> groups = jwtPrincipal.getGroups();
    if ((groups == null) || (groups.contains("login") == false)) {
      throw new JWTException("User is not in a valid group [" + groups.toString() + "]");
    }

    // TODO: Additional checks as appropriate.
  }

  private static class JWTException extends Exception {
    private static final long serialVersionUID = 423763L;

    public JWTException(String message) {
      super(message);
    }
  }
}
