package Domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Vai on 6/11/17.
 */
public class Crypt {

    private static final String SALT = "691337";

    final protected static char[] hexArray = "0123456789ABCDEF"
            .toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String hashPassword(String in){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(SALT.getBytes());
            md.update(in.getBytes());
            byte[] out = md.digest();
            return bytesToHex(out);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return"";
    }
}
