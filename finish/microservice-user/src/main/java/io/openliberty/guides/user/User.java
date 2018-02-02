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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class User {

  public static final String JSON_KEY_USER_ID = "id";
  public static final String JSON_KEY_USER_FIRST_NAME = "firstName";
  public static final String JSON_KEY_USER_LAST_NAME = "lastName";
  public static final String JSON_KEY_USER_NAME = "userName";
  public static final String JSON_KEY_USER_WISH_LIST_LINK = "wishListLink";
  public static final String JSON_KEY_USER_PASSWORD_HASH = "password";
  public static final String JSON_KEY_USER_PASSWORD_SALT = "salt";

  /** The unique ID for the user. */
  private String id;

  /** The user's first name. */
  private String firstName;

  /** The user's last name. */
  private String lastName;

  /** The name that the user will use to log into the application. */
  private String userName;

  /** A URL pointing to the user's wish list. */
  private String wishListLink;

  /** The hashed password that is stored in the database for this user. */
  private String passwordHash;

  /** The generated salt that is contained in the hashed password. */
  private String passwordSalt;

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
    this.passwordHash = password;
  }

  /** Constructor for reading the user from the JSON that was a part of a JAX-RS request. */
  public User(JsonObject user) {
    if (user.containsKey(JSON_KEY_USER_ID)) {
      id = user.getString(JSON_KEY_USER_ID);
    }
    this.firstName = user.getString(JSON_KEY_USER_FIRST_NAME, "");
    this.lastName = user.getString(JSON_KEY_USER_LAST_NAME, "");
    this.wishListLink = user.getString(JSON_KEY_USER_WISH_LIST_LINK, "");

    if (user.containsKey(JSON_KEY_USER_NAME)) {
      this.userName = user.getString(JSON_KEY_USER_NAME);
    }
    if (user.containsKey(JSON_KEY_USER_PASSWORD_HASH)) {
      this.passwordHash = user.getString(JSON_KEY_USER_PASSWORD_HASH);
    }
    if (user.containsKey(JSON_KEY_USER_PASSWORD_SALT)) {
      this.passwordSalt = user.getString(JSON_KEY_USER_PASSWORD_SALT);
    }
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUserName() {
    return userName;
  }

  public String getWishListLink() {
    return wishListLink;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getPasswordSalt() {
    return passwordSalt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * Return a JSON object suitable to be returned to the caller with no confidential information
   * (like password or salt).
   */
  public JsonObject getPublicJsonObject() {
    JsonObjectBuilder user = Json.createObjectBuilder();
    user.add(JSON_KEY_USER_ID, id);
    user.add(JSON_KEY_USER_FIRST_NAME, firstName);
    user.add(JSON_KEY_USER_LAST_NAME, lastName);
    user.add(JSON_KEY_USER_NAME, userName);
    user.add(JSON_KEY_USER_WISH_LIST_LINK, wishListLink);

    return user.build();
  }
}
