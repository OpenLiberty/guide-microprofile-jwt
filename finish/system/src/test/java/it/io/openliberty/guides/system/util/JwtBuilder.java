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

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import org.apache.cxf.common.util.Base64Utility;

public class JwtBuilder {

    private static final String JWT_ALGORITHM = "SHA256withRSA";
    private static final String JWT_ISSUER = "http://openliberty.io";
    private static final String keystorePath = "/security/key.p12";

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

    public String createJwt(String username, Set<String> groups) throws GeneralSecurityException, IOException {
        // Create and Base64 encode the header portion of the JWT
        JsonObject headerObj = Json.createObjectBuilder()
        .add("alg", "RS256")  // Algorithm used
        .add("typ", "JWT")    // Type of token
        .build();

        String headerEnc = Base64Utility.encode(headerObj.toString().getBytes(), true);

        // Create and Base64 encode the claims portion of the JWT
        JsonObject claimsObj = Json.createObjectBuilder()
            .add("exp", (System.currentTimeMillis() / 1000) + 300)  // Expire time
            .add("iat", (System.currentTimeMillis() / 1000))        // Issued time
            .add("jti", Long.toHexString(System.nanoTime()))        // Unique value
            .add("sub", username)                                   // Subject
            .add("upn", username)                                   // Subject again
            .add("iss", JWT_ISSUER)                                 // Issuer
            .add("groups", getGroupArray(groups))                   // Group list
            .build();

        String claimsEnc = Base64Utility.encode(claimsObj.toString().getBytes(), true);
        String headerClaimsEnc = headerEnc + "." + claimsEnc;

        headerClaimsEnc = headerClaimsEnc.replace("=", "");

        // Open the keystore that the server will use to validate the JWT
        KeyStore ks = KeyStore.getInstance("PKCS12");
        InputStream ksStream = this.getClass().getResourceAsStream(JwtBuilder.keystorePath);

        if (ksStream == null){
            System.err.println("Keystore not found!");
        }
        
        char[] password = new String("secret").toCharArray();
        ks.load(ksStream, password);
        
        // Get the private key to use to sign the JWT.  Normally we would not do this but
        // we are pretending to be the backend service here.
        KeyStore.ProtectionParameter keyPassword = new KeyStore.PasswordProtection(password);
        KeyStore.PrivateKeyEntry privateKeyEntry =
        (KeyStore.PrivateKeyEntry) ks.getEntry("default", keyPassword);
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();
        
        // Sign the JWT
        Signature sig = Signature.getInstance(JWT_ALGORITHM);
        sig.initSign(privateKey);
        sig.update(headerClaimsEnc.getBytes());
        String sigEnc = Base64Utility.encode(sig.sign(), true);

        String jwtEnc = headerClaimsEnc + "." + sigEnc;

        // Return the complete JWT (header, claims, signature).
        return jwtEnc;
    }

    private static JsonArray getGroupArray(Set<String> groups) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if (groups != null) {
            for (String group : groups) {
                arrayBuilder.add(group);
            }
        }
        return arrayBuilder.build();
    }
}