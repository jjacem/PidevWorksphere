package esprit.tn.utils;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;

import java.sql.SQLException;

public class SessionManager {
    private static String jwtToken;
    private static String username;
    private static String role;

    public static void setSession(String token, String user, String userRole) {
        jwtToken = token;
        username = user;
        role = userRole;
    }
public static User extractuserfromsession() throws SQLException {
    ServiceUser serviceUser = new ServiceUser();
    JwtUtil jwtUtil = new JwtUtil();
    int id = jwtUtil.getIdFromToken(jwtToken);
    User user = serviceUser.findbyid(id);
        return user ;
}
public int extractidfromsession() {
    JwtUtil jwtUtil = new JwtUtil();
    return jwtUtil.getIdFromToken(jwtToken);
}
    public static void setToken(String token) {
        jwtToken = token;
}
    public static String getToken() {
        return jwtToken;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static void clearSession() {
        jwtToken = null;
        username = null;
        role = null;
    }

    public static boolean isLoggedIn() {
        return jwtToken != null;
    }
}
