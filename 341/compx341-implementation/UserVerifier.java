import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class UserVerifier {

    static private HashMap<String, String> credentials = new HashMap<>(){{ //Hashed using MessageDigest SHA-256
        put("encostUserA","5efc2b017da4f7736d192a74dde5891369e0685d4d38f2a455b6fcdab282df9c");
        put("encostUserB","93ff4d79302417d6912b8c2620c1a5fcb8dbe305c1a351a8f3cd7560e3f4d4f2");
        put("encostUserC","c6ba91b90d922e159893f46c387e5dc1b3dc5c101a5a4522f03b987177a24a91");
        put("encostUserD","7c74facdb58cacc48b1003740aaebaaae5624a59946a36562bb9d45584ebfa35");
        put("encostUserE","244091dbe590bba5ed965b2ef31191be8b79ea41a7106ebdf32f841182a7d2bb");
        put("encostUserF","8fd389ed85aad895744ec7452be2b1d6224e954b66f023aed3856835b7880b43");
        put("encostUserG","31457e06e2adb2178358e9fc6705e0b6f37e9b6ec9456e8890d28f292be9adc4");
        put("encorstUserH","4f0794779cce1362ef83c2d789bb8e069039a770b28962f0929971d470c1ca70");
        put("encostUserI","ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f");
        put("encostUserJ","cb28ca00e0fde85aeae3101ebbb701465181eb76241e75f77e3d54e60ed50c35");
    }};


    public static boolean verifyCredentials(String username, String password) {
        if(username == null || password == null) throw new IllegalArgumentException("Username/Password was null!");

        if (!credentials.containsKey(username)) return false;

        // Hash user's password
        //https://www.baeldung.com/sha-256-hashing-java
        MessageDigest hashMethod;
        try {
            hashMethod = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] pwdEncodedHash = hashMethod.digest(password.getBytes(StandardCharsets.UTF_8));

        return credentials.get(username).equals(bytesToHex(pwdEncodedHash));
    }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
