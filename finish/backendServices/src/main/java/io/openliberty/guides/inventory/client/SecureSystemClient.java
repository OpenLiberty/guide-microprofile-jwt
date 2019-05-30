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
package io.openliberty.guides.inventory.client;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import java.util.Properties;
import io.openliberty.guides.inventory.client.SystemClient;

@RequestScoped
// tag::SecureSystemClient[]
// tag::SystemClient[]
public class SecureSystemClient extends SystemClient {
// end::SystemClient[]  

  // Constants for building URI to the system service.
  private final int DEFAULT_SEC_PORT = Integer.valueOf(
      System.getProperty("https.port"));
  private final String SYSTEM_PROPERTIES = "/system/properties";
  private final String SECURED_PROTOCOL = "https";

  public String buildUrl(String protocol, String host, int port, String path) {
    return super.buildUrl(protocol, host, port, path);
  }

  // tag::buildClientBuilder[]
  public Builder buildClientBuilder(String url, String authHeader) {
    Builder builder = super.buildClientBuilder(url);
    // tag::return[]
    return builder.header(HttpHeaders.AUTHORIZATION, authHeader);
    // end::return[]
  }
  // end::buildClientBuilder[]

  public Properties getProperties(String hostname, String authHeader) {
    String url = buildUrl(SECURED_PROTOCOL, hostname, 
                          DEFAULT_SEC_PORT, SYSTEM_PROPERTIES);
    Builder clientBuilder = buildClientBuilder(url, authHeader);
    return getPropertiesHelper(clientBuilder);
  }
}
// end::SecureSystemClient[]
// end::jwt[]
