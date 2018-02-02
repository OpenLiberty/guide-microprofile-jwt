package io.openliberty.guides.ui;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import io.openliberty.guides.ui.util.ServiceUtils;
import io.openliberty.guides.ui.util.SessionUtils;

import javax.json.JsonObject;

@ManagedBean
@ViewScoped
public class SystemBean {

  private String os;

  public void setOs(String os) {
    this.os = os;
  }

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

}
