package API.BusinessLayer;

import API.Model.HashedPassword;
import API.Model.Role;
import API.Model.UserDto;
import API.Util.Repositories.RoleRepository;
import API.Util.Repositories.UserRepository;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Base64;

@Component
public class AuthenticationHandler {

    UserRepository userRepository;
    RoleRepository roleRepository;

    public AuthenticationHandler(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private static final String TokenCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890";

    public UserDto hashAndSaltUserPassword(UserDto user) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {

        byte[] salt;
        if (user.getSalt() == null || user.getSalt().isEmpty()) {
            salt = generateSalt();
        } else {
            salt = user.getEncodedSalt();
        }

        byte[] hash = hashSecureStrings(user.getPassword(), salt);
        Base64.Encoder encoder = Base64.getEncoder();
        user.setPassword(encoder.encodeToString(hash));
        user.setSalt(encoder.encodeToString(salt));
        return user;
    }

    public UserDto validateUserPassword(UserDto user) {
        byte[] salt = user.getEncodedSalt();
        String userPassword = user.getPassword();
        if (salt == null || salt.length == 0) {
            throw new IllegalArgumentException("The user must have a salted password");
        } else if (userPassword == null || userPassword.isEmpty()) {
            throw new IllegalArgumentException("The password cannot be empty");
        }
        try {
            byte[] hash = hashSecureStrings(userPassword, salt);
            Base64.Encoder encoder = Base64.getEncoder();
            user.setPassword(encoder.encodeToString(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return user;

    }

    public HashedPassword hashPassword(String originalPassword) {
        byte[] salt = generateSalt();
        byte[] hash = new byte[0];
        String passwordHash = "";
        String saltString = "";
        HashedPassword hashedPassword = null;
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            hash = hashSecureStrings(originalPassword, salt);
            passwordHash = encoder.encodeToString(hash);
            saltString = encoder.encodeToString(salt);
            hashedPassword = new HashedPassword(passwordHash, saltString);
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    public byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public String generatePasswordResetToken() {
        return generateSecuredToken();
    }

    private String generateSecuredToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] random = new byte[42];
        return populateToken(secureRandom, random);
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] random = new byte[24];
        return populateToken(secureRandom, random);
    }

    private String populateToken(SecureRandom secureRandom, byte[] random) {
        secureRandom.nextBytes(random);
        StringBuilder token = new StringBuilder("");
        for (byte element : random) {
            token.append(TokenCharSet.charAt(Math.abs(element) % TokenCharSet.length()));
        }
        return token.toString();
    }

    public boolean authenticateUser(String username, String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
        UserDto user = this.userRepository.getByUserName(username);
        if (user != null) {
            UserDto testUser = new UserDto();
            testUser.setSalt(user.getSalt());
            testUser.username = username;
            testUser.setPassword(password);
            testUser = validateUserPassword(testUser);
            if (user != null) {
                return testUser.getPassword().equals(user.getPassword());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean validateActivation(UserDto user, String activationId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        boolean isReadyForActivation = false;
        if (generateActivationId(user).equals(activationId)) {
            isReadyForActivation = true;
        }
        return isReadyForActivation;
    }

    public byte[] hashUnSecureStrings(String originalString, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(originalString.toCharArray(), salt, 1651, 5658);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return keyFactory.generateSecret(spec).getEncoded();
    }

    public String hasPubSubPublishBody(String originalString, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException {
        Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
        SecretKeySpec secret_key = new SecretKeySpec(salt, "HmacSHA1");
        sha1_HMAC.init(secret_key);

        return Hex.encodeHexString(sha1_HMAC.doFinal(originalString.getBytes("UTF-8")));
    }

    public byte[] hashSecureStrings(String originalString, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(originalString.toCharArray(), salt, 1651, 5658);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return keyFactory.generateSecret(spec).getEncoded();
    }

    public byte[] hashPubSubResponse(String originalString, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec spec = new SecretKeySpec( salt,"HmacSHA1");
        Mac hmac = Mac.getInstance("HmacSHA1");
        byte[] hashedData;
        hmac.init(spec);
        return hmac.doFinal(originalString.getBytes("UTF-8"));
    }

    public String generateSession(UserDto user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] salt = generateSalt();
        byte[] hashedSessionId = hashUnSecureStrings(user.username + user.email, salt);
        return Base64.getEncoder().encodeToString(hashedSessionId);
    }

    public String generateEmailToken(UserDto user) {
        return generateToken();
    }

    public String generateActivationId(UserDto user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return generateEmailToken(user);
    }

    public boolean userIsAdmin(String sessionId) throws SQLException {
        UserDto correspondingUser = this.userRepository.getBySessionId(sessionId);
        Role role;
        boolean isAdmin = false;
        if (correspondingUser != null) {
            role = this.roleRepository.getRoleByUserId(correspondingUser.userId);
            isAdmin = role.ranking == 0;
        }
        return isAdmin;
    }
}
