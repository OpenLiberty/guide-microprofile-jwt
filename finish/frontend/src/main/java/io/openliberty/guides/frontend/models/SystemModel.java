// tag::copyright[]
/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
// end::copyright[]
package io.openliberty.guides.frontend.models;

import jakarta.json.JsonObject;

public class SystemModel {
    private String hostname;
    private String username;
    private String osName;

    public SystemModel(JsonObject jo) {
        hostname = jo.getString("hostname");

        JsonObject props = jo.getJsonObject("properties");
        username = props.getString("user.name");
        osName = props.getString("os.name");
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getOsName() {
        return osName;
    }
}
