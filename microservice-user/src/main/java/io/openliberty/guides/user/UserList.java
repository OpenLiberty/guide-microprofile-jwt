package io.openliberty.guides.user;

import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class UserList {

  private static ArrayList<User> users = new ArrayList<>();

  public void addUser(User newUser) {
    users.add(newUser);
    // System.out.println("add user" + user.toString());
  }

  public User getUserByName(String name) {
    for (User user : users) {
      if (user.getUserName().equals(name)) {
        // System.out.println("get user" + user.toString());
        return user;
      }
    }
    return null;
  }

  public User getUserById(String id) {
    for (User user : users) {
      if (user.getId().equals(id)) {
        // System.out.println("get user" + user.toString());
        return user;
      }
    }
    return null;
  }

  public ArrayList<User> getUsersList() {
    return users;
  }

  public JsonArray getUsersListJson() {
    JsonArrayBuilder userArray = Json.createArrayBuilder();
    for (User user : users) {
      userArray.add(user.getPublicJsonObject());
    }
    return userArray.build();
  }
}
