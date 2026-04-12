package com.brewandbite.service;

import com.brewandbite.model.UserRole;
import java.util.Map;

/**
 * Validates staff credentials against a hardcoded map and returns the
 * corresponding {@link UserRole}, or {@code null} if authentication fails.
 */
public class AuthService {

    // username (lower-case) -> { password, role-name }
    private static final Map<String, String[]> CREDENTIALS = Map.of(
        "barista1", new String[]{"barista123", "BARISTA"},
        "barista2", new String[]{"brew456",    "BARISTA"},
        "manager1", new String[]{"manager123", "MANAGER"},
        "admin",    new String[]{"admin2024",  "MANAGER"}
    );

    /**
     * @return the authenticated {@link UserRole}, or {@code null} if credentials are invalid.
     */
    public UserRole authenticate(String username, String password) {
        if (username == null || password == null) return null;
        String[] entry = CREDENTIALS.get(username.toLowerCase().trim());
        if (entry != null && entry[0].equals(password)) {
            return UserRole.valueOf(entry[1]);
        }
        return null;
    }
}
