// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::jwt[]
package io.openliberty.guides.ui;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import io.openliberty.guides.ui.models.SystemModel;
import io.openliberty.guides.ui.util.ServiceUtils;
import io.openliberty.guides.ui.util.SessionUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;

@ManagedBean
@ViewScoped
public class ApplicationBean {

  private String hostname;

  public String getJwt() {
    String jwtTokenString = SessionUtils.getJwtToken();
    String authHeader = "Bearer " + jwtTokenString;
    return authHeader;
  }

  public String getOs() {
    String authHeader = getJwt();
    if (ServiceUtils.responseOk(authHeader)) {
      JsonObject properties = ServiceUtils.getProperties(authHeader);
      return properties.getString("os.name");
    }
    return "You are not authorized to access the system service.";
  }

  public String getInventorySize() {
    String authHeader = getJwt();
    if (ServiceUtils.invOk(authHeader)) {
      JsonObject properties = ServiceUtils.getInventory(authHeader);
      return String.valueOf(properties.getInt("total"));
    }
    return "You are not authorized to access the inventory service.";
  }

  public List<SystemModel> getInventoryList() {
    String authHeader = getJwt();
    if (ServiceUtils.invOk(authHeader)) {
      JsonArray systems = ServiceUtils.getInventory(authHeader).getJsonArray("systems");
      return systems.stream().map(s -> new SystemModel((JsonObject) s)).collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
    String authHeader = getJwt();

    if (ServiceUtils.invOk(authHeader)) {
      ServiceUtils.addSystem(hostname, authHeader);
    }
  }

  public String getUsername() {
    String authHeader = getJwt();
    return ServiceUtils.getJwtUsername(authHeader);
  }

  public String getUserRole() {
    String authHeader = getJwt();
    return ServiceUtils.getJwtRoles(authHeader);
  }
}
// end::jwt[]
