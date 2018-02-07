package io.openliberty.guides.ui;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import io.openliberty.guides.ui.util.ServiceUtils;
import io.openliberty.guides.ui.util.SessionUtils;

import javax.json.JsonObject;

@ManagedBean
@ViewScoped
public class SystemBean {

  public String getOs() {
    String jwtTokenString = SessionUtils.getJwtToken();
    String authHeader = "Bearer " + jwtTokenString;
    if (ServiceUtils.responseOkHelper(authHeader)) {
      JsonObject properties = ServiceUtils.getPropertiesHelper(authHeader);
      return properties.getString("os.name");
    } else {
      return "wrong os";
    }
  }

  public int getInvSize() {
    String jwtTokenString = SessionUtils.getJwtToken();
    String authHeader = "Bearer " + jwtTokenString;
    if (ServiceUtils.invOkHelper(authHeader)) {
      JsonObject properties = ServiceUtils.getInventoryHelper(authHeader);
      return properties.getInt("total");
    } else {
      return -1;
    }
  }

  public String getJwt() {
    String jwtTokenString = SessionUtils.getJwtToken();
    String authHeader = "Bearer " + jwtTokenString;
    return authHeader;
  }

  public String getUserName() {
    return SessionUtils.getUserObj().getName();
  }

  public String getUserRole() {
    return SessionUtils.getUserObj().getRole();
  }

}
