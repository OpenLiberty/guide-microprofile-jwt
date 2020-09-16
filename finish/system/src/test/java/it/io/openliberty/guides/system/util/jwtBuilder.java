//tag::copyright[]
/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.core.json.JsonArray;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
// import javax.json.JsonArray;
// import javax.json.JsonArrayBuilder;
// import javax.json.JsonObject;
import org.apache.cxf.common.util.Base64Utility;
import java.nio.charset.StandardCharsets;

public class JwtBuilder {

    private static final String JWT_ALGORITHM = "SHA256withRSA";
    private static final String JWT_ISSUER = "http://openliberty.io";
    private static final String keystorePath = "security/key.p12";
    private static final String keyPath = "/security/private_key.pem";

    Vertx vertx = Vertx.vertx();

    public String createUserJwt(String username) throws GeneralSecurityException, IOException {
        Set<String> groups = new HashSet<String>();
        groups.add("user");
        return createJwt(username, groups);
    }

    public String createAdminJwt(String username) throws GeneralSecurityException, IOException {
        Set<String> groups = new HashSet<String>();
        groups.add("admin");
        groups.add("user");
        return createJwt(username, groups);
    }

    public String createJwt(String username, Set<String> groups) throws IOException {
        String privateKey = getPrivateKey();

        JWTAuthOptions config = new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("RS256")
            .setSecretKey(privateKey));

        JWTAuth provider = JWTAuth.create(vertx, config);
        
        io.vertx.core.json.JsonObject claimsObj = new JsonObject()
            .put("exp", (System.currentTimeMillis() / 1000) + 300)  // Expire time
            .put("iat", (System.currentTimeMillis() / 1000))        // Issued time
            .put("jti", Long.toHexString(System.nanoTime()))        // Unique value
            .put("sub", username)                                   // Subject
            .put("upn", username)                                   // Subject again
            .put("iss", "http://openliberty.io")
            .put("groups", getGroupArray(groups)); 

        String token = provider.generateToken(claimsObj, new JWTOptions().setAlgorithm("RS256"));

        return token;
    }

    private String getPrivateKey() throws IOException{
        InputStream keyStream = this.getClass().getResourceAsStream(JwtBuilder.keyPath);
        String privateKey = new String(keyStream.readAllBytes(), StandardCharsets.UTF_8);
        return privateKey;
    }

    private JsonArray getGroupArray(Set<String> groups) {
        JsonArray arr = new JsonArray(); 
        if (groups != null) {
            for (String group : groups) {
                arr.add(group);
            }
        }
        return arr;
    }
    
}