import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Arrays;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;


public class PasswordHashing {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ITERATIONS = 64000;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    public static String generateSalt() {
        // Default: 512-bits salt
        byte[] saltBytes = new byte[64];
        RANDOM.nextBytes(saltBytes);
        return Base64.getEncoder().withoutPadding().encodeToString(saltBytes);
    }

    public static String generateKey(String password, String salt) {
        // Default: 512-bits keys -> 86 characters
        return generateKey(password, salt, 86);
    }

    public static String generateKey(String password, String salt, int keyLengthChars){
        if (keyLengthChars <1 ||keyLengthChars > 86) {
            throw new IllegalArgumentException("The algorithm can generate keys up to 86 characters or 512 bits.");
        }
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        // Base64:
        // 3n bytes -> 4n characters
        // n characters -> int((3/4)n) * 8 bits
        int keyLengthBits = (int) (keyLengthChars * 3.0 / 4.0) * 8;
        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, keyLengthBits);
        // Clear password array
        Arrays.fill(passwordChars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] keyBytes = fac.generateSecret(spec).getEncoded();
            return Base64.getEncoder().withoutPadding().encodeToString(keyBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        } finally {
            spec.clearPassword();
        }
        return null;
    }

}
