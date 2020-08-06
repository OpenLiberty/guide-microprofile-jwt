// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::client[]
package io.openliberty.guides.inventory.client;

import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

@RegisterRestClient(configKey = "systemClient", baseUri = "https://localhost:8443/system")


@Path("/properties")
// tag::registerClientHeaders[]
@RegisterClientHeaders 
// end::registerClientHeaders[]
public interface SystemClient extends AutoCloseable {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Properties getProperties() throws ProcessingException;

}


