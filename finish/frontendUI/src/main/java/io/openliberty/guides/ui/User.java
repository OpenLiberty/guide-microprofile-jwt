package io.openliberty.guides.ui;


public class User {
  private String name;
  private String password;
  private String role;

  public User(String name, String password, String role) {
      this.name = name;
      this.password = password;
      this.role = role;
    }

  public void setRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return this.role;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }
}
