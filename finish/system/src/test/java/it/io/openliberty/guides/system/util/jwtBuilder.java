package it.io.openliberty.guides.system.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class jwtBuilder {

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
        InputStream ksStream = this.getClass().getResourceAsStream(jwtBuilder.keystorePath);

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