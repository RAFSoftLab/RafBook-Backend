package raf.rs.userservice.util;

public class UserUtils {

    public static String createUsernameFromEmail(String email) {
        int charPos = email.indexOf('@');
        return email.substring(0, charPos);
    }

}
