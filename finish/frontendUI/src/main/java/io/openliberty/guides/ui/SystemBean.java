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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import io.openliberty.guides.ui.util.ServiceUtils;
import io.openliberty.guides.ui.util.SessionUtils;

import javax.json.JsonObject;

@ManagedBean
@ViewScoped
public class SystemBean {

    public String getOs() {
        String authHeader = getJwt();
        if (ServiceUtils.responseOkHelper(authHeader)) {
            JsonObject properties = ServiceUtils.getPropertiesHelper(authHeader);
            return properties.getString("os.name");
        }
        return "You are not authorized to access the system service.";
    }

    public String getInventorySize() {
        String authHeader = getJwt();
        if (ServiceUtils.invOkHelper(authHeader)) {
            JsonObject properties = ServiceUtils.getInventoryHelper(authHeader);
            return String.valueOf(properties.getInt("total"));
        }
        return "You are not authorized to access the inventory service.";
    }

    public String getJwt() {
        String jwtTokenString = SessionUtils.getJwtToken();
        String authHeader = "Bearer " + jwtTokenString;
        return authHeader;
    }

    public String getUsername() {
        return SessionUtils.getUserObj().getName();
    }

    public String getUserRole() {
        return SessionUtils.getUserObj().getRole();
    }
}
// end::jwt[]
