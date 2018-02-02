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
package test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * User Data that a client would send to the server. This object has a different format than the
one
 * the server would store.
 */
public class User {

  public static final String JSON_KEY_USER_ID = "id";
  public static final String JSON_KEY_USER_FIRST_NAME = "firstName";
  public static final String JSON_KEY_USER_LAST_NAME = "lastName";
  public static final String JSON_KEY_USER_NAME = "userName";
  public static final String JSON_KEY_USER_WISH_LIST_LINK = "wishListLink";
  public static final String JSON_KEY_USER_PASSWORD = "password";

  private String id;
  public String firstName;
  public String lastName;
  public String userName;
  public String wishListLink;
  public String password;

  public User(JsonObject jObject) throws Exception {
    this.id = jObject.getString(JSON_KEY_USER_ID);
    firstName = jObject.getString(JSON_KEY_USER_FIRST_NAME);
    lastName = jObject.getString(JSON_KEY_USER_LAST_NAME);
    userName = jObject.getString(JSON_KEY_USER_NAME);
    wishListLink = jObject.getString(JSON_KEY_USER_WISH_LIST_LINK);
    password = jObject.getString(JSON_KEY_USER_PASSWORD, "");
  }

  public User(
      String id,
      String firstName,
      String lastName,
      String userName,
      String wishListLink,
      String password) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userName = userName;
    this.wishListLink = wishListLink;
    this.password = password;
  }

  /** Sets the Id value. */
  public void setId(String id) {
    this.id = id;
  }

  public String getJson() {
    JsonObjectBuilder user = Json.createObjectBuilder();
    if (id != null) {
      user.add(JSON_KEY_USER_ID, id);
    }
    user.add(JSON_KEY_USER_FIRST_NAME, firstName);
    user.add(JSON_KEY_USER_LAST_NAME, lastName);
    user.add(JSON_KEY_USER_NAME, userName);
    user.add(JSON_KEY_USER_WISH_LIST_LINK, wishListLink);
    user.add(JSON_KEY_USER_PASSWORD, password);

    return user.build().toString();
  }


  public boolean isEqual(JsonObject other) {
    // The user returned from the microservice won't have a password in it, so
    // don't compare that field.
    if (other.containsKey(JSON_KEY_USER_PASSWORD)) {
      throw new IllegalStateException("User object should not contain a password");
    }

    return (id.equals(other.getString(JSON_KEY_USER_ID))
        && firstName.equals(other.getString(JSON_KEY_USER_FIRST_NAME))
        && lastName.equals(other.getString(JSON_KEY_USER_LAST_NAME))
        && userName.equals(other.getString(JSON_KEY_USER_NAME))
        && wishListLink.equals(other.getString(JSON_KEY_USER_WISH_LIST_LINK)));
  }
}
