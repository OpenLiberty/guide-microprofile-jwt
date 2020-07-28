// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::manager[]
package io.openliberty.guides.inventory;

import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.core.HttpHeaders;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.inventory.client.SecureSystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;



// tag::ApplicationScoped[]
@ApplicationScoped
// end::ApplicationScoped[]
public class InventoryManager {

  private List<SystemData> systems = Collections.synchronizedList(
                                       new ArrayList<SystemData>());

  @Inject
  @ConfigProperty(name = "system.http.port", defaultValue="8443")
  String DEFAULT_PORT;

  // tag::Inject[]
  @Inject
  // end::Inject[]
  // tag::RestClient[]
  @RestClient
  // end::RestClient[]
  // tag::SystemClient[]
  private SecureSystemClient defaultRestClient;
  // end::SystemClient[]

  public Properties get(String hostname) {
	Properties properties = null;
    if (hostname.equals("localhost")) {
		properties = getPropertiesWithDefaultHostName();
    } else {
		properties = getPropertiesWithGivenHostName(hostname);
	}
	return properties;
  }

  public void add(String hostname, Properties systemProps) {
    Properties props = new Properties();
    props.setProperty("os.name", systemProps.getProperty("os.name"));
    props.setProperty("user.name", systemProps.getProperty("user.name"));

    SystemData host = new SystemData(hostname, props);
    if (!systems.contains(host))
      systems.add(host);
  }

  public InventoryList list() {
    return new InventoryList(systems);
  }

  private Properties getPropertiesWithDefaultHostName() {
	return defaultRestClient.getProperties();
  }
  // end::getPropertiesWithDefaultHostName[]

  // tag::getPropertiesWithGivenHostName[]
  private Properties getPropertiesWithGivenHostName(String hostname) {
	String customURIString = "https://" + hostname + ":" + DEFAULT_PORT + "/system";
    URI customURI = null;
    try {
      customURI = URI.create(customURIString);
      SecureSystemClient customRestClient = RestClientBuilder.newBuilder()
                                        .baseUri(customURI)
                                        .build(SecureSystemClient.class);
	  return customRestClient.getProperties();
	  
    } catch (ProcessingException ex) {
      handleProcessingException(ex);
    }
    return null;
  }

  // tag::getPropertiesWithDefaultHostName[]

  private void handleProcessingException(ProcessingException ex) {
    Throwable rootEx = ExceptionUtils.getRootCause(ex);
    if (rootEx != null && (rootEx instanceof UnknownHostException
        || rootEx instanceof ConnectException)) {
      System.err.println("The specified host is unknown.");
    } else {
      throw ex;
    }
  }

}
// end::manager[]
