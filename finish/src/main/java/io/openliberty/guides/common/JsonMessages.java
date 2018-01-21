// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::copyright[]
package io.openliberty.guides.common;

// JSON-P
import javax.json.Json;
import javax.json.JsonObject;

public enum JsonMessages {

    SERVICE_UNREACHABLE();

    private JsonObject json;

    public JsonObject getJson() {
        switch(this) {
            case SERVICE_UNREACHABLE:
                this.serviceUnreachable();
                break;
            default:
                break;
        }
        return json;
    }

    private void serviceUnreachable() {
        json = Json.createObjectBuilder()
                .add("ERROR", "Unknown hostname or the resource may not be running on the host machine")
                .build();
    }

}
